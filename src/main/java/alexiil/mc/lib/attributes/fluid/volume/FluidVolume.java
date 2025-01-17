package alexiil.mc.lib.attributes.fluid.volume;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.render.DefaultFluidVolumeRenderer;
import alexiil.mc.lib.attributes.fluid.render.FluidRenderFace;
import alexiil.mc.lib.attributes.fluid.render.FluidVolumeRenderer;

public abstract class FluidVolume {

    /** The base unit for all fluids. This is arbitrarily chosen to be 1 / 1620 of a bucket. NOTE: You should
     * <i>never</i> tell the player what unit this is! Instead use */
    // and to establish easy compatibility with silk, which is where the numbers came from
    public static final int BASE_UNIT = 1;

    public static final int BUCKET = 20 * 9 * 9 * BASE_UNIT;
    public static final int BOTTLE = BUCKET / 3;

    private static final String KEY_AMOUNT = "Amount";

    public final FluidKey fluidKey;

    /** The number of {@link #BASE_UNIT units} in this volume. */
    // Private because then we disallow fluids with negative amounts
    private int amount;

    public FluidVolume(FluidKey key, int amount) {
        this.fluidKey = key;
        this.amount = amount;

        if (key.registryEntry.isEmpty()) {
            if (amount != 0) {
                throw new IllegalArgumentException("Empty Fluid Volume's must have an amount of 0!");
            }
        } else if (amount <= 0) {
            throw new IllegalArgumentException("Fluid Volume's must have an amount greater than 0!");
        }
    }

    public FluidVolume(FluidKey key, CompoundTag tag) {
        this.fluidKey = key;

        if (key.registryEntry.isEmpty()) {
            this.amount = 0;
        } else {
            int readAmount = tag.getInt(KEY_AMOUNT);
            this.amount = Math.max(1, readAmount);
        }
    }

    public static FluidVolume fromTag(CompoundTag tag) {
        if (tag.isEmpty()) {
            return FluidKeys.EMPTY.withAmount(0);
        }
        return FluidKey.fromTag(tag).readVolume(tag);
    }

    public final CompoundTag toTag() {
        return toTag(new CompoundTag());
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (isEmpty()) {
            return tag;
        }
        fluidKey.toTag(tag);
        tag.putInt(KEY_AMOUNT, amount);
        return tag;
    }

    /** Creates a new {@link FluidVolume} from the given fluid, with the given amount stored. This just delegates
     * internally to {@link FluidKey#withAmount(int)}. */
    public static FluidVolume create(FluidKey fluid, int amount) {
        return fluid.withAmount(amount);
    }

    /** Creates a new {@link FluidVolume} from the given fluid, with the given amount stored. */
    public static FluidVolume create(Fluid fluid, int amount) {
        return FluidKeys.get(fluid).withAmount(amount);
    }

    /** Creates a new {@link FluidVolume} from the given potion, with the given amount stored. */
    public static FluidVolume create(Potion potion, int amount) {
        return FluidKeys.get(potion).withAmount(amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        FluidVolume other = (FluidVolume) obj;
        if (isEmpty()) {
            return other.isEmpty();
        }
        if (other.isEmpty()) {
            return false;
        }
        return amount == other.amount//
            && Objects.equals(fluidKey, other.fluidKey);
    }

    @Override
    public int hashCode() {
        if (isEmpty()) {
            return 0;
        } else {
            return fluidKey.hashCode() + 31 * amount;
        }
    }

    @Override
    public String toString() {
        return fluidKey + " " + fluidKey.unit.localizeAmount(getAmount());
    }

    /** @deprecated Use {@link Objects#equals(Object)} instead of this. */
    @Deprecated
    public static boolean areFullyEqual(FluidVolume a, FluidVolume b) {
        return Objects.equals(a, b);
    }

    public static boolean areEqualExceptAmounts(FluidVolume a, FluidVolume b) {
        if (a.isEmpty()) {
            return b.isEmpty();
        } else if (b.isEmpty()) {
            return false;
        }
        return a.getFluidKey().equals(b.getFluidKey());
    }

    public final boolean isEmpty() {
        return fluidKey == FluidKeys.EMPTY || amount == 0;
    }

    public FluidKey getFluidKey() {
        return fluidKey;
    }

    /** @return The minecraft {@link Fluid} instance that this contains, or null if this is based on something else
     *         (like {@link Potion}'s). */
    @Nullable
    public Fluid getRawFluid() {
        return getFluidKey().getRawFluid();
    }

    public final FluidVolume copy() {
        return isEmpty() ? FluidKeys.EMPTY.withAmount(0) : copy0();
    }

    protected FluidVolume copy0() {
        return getFluidKey().withAmount(amount);
    }

    public final int getAmount() {
        return isEmpty() ? 0 : amount;
    }

    /** @return The raw amount value, which might not be 0 if this is {@link #isEmpty() empty}. */
    protected int getRawAmount() {
        return amount;
    }

    /** Protected to allow the implementation of {@link #split(int)} and {@link #merge0(FluidVolume)} to set the
     * amount. */
    protected final void setAmount(int newAmount) {
        // Note that you can always set the amount to 0 to make this volume empty
        if (newAmount < 0) {
            throw new IllegalArgumentException("newAmount was less than 0! (was " + newAmount + ")");
        }
        this.amount = newAmount;
    }

    /** @param a The merge target. Might be modified and/or returned.
     * @param b The other fluid. Might be modified, and might be returned.
     * @return the merged fluid. Might be either a or b depending on */
    @Nullable
    public static FluidVolume merge(FluidVolume a, FluidVolume b) {
        if (a.isEmpty()) {
            if (b.isEmpty()) {
                return FluidKeys.EMPTY.withAmount(0);
            }
            return b;
        }
        if (b.isEmpty()) {
            return a;
        }
        if (a.merge(b, Simulation.ACTION)) {
            return a;
        }
        return null;
    }

    public final boolean canMerge(FluidVolume with) {
        return merge(with, Simulation.SIMULATE);
    }

    public final boolean merge(FluidVolume other, Simulation simulation) {
        if (isEmpty() || other.isEmpty()) {
            throw new IllegalArgumentException("Don't try to merge two empty fluids!");
        }
        if (getClass() != other.getClass() || !Objects.equals(fluidKey, other.fluidKey)) {
            return false;
        }
        if (simulation == Simulation.ACTION) {
            merge0(other);
        }
        return true;
    }

    /** Actually merges two {@link FluidVolume}'s together.
     * 
     * @param other The other fluid volume. This will always be the same class as this. This should change the amount of
     *            the other fluid to 0. */
    protected void merge0(FluidVolume other) {
        setAmount(getAmount() + other.getAmount());
        other.setAmount(0);
    }

    public final FluidVolume split(int toRemove) {
        if (toRemove < 0) {
            throw new IllegalArgumentException("Cannot split off a negative amount!");
        }
        if (toRemove == 0 || isEmpty()) {
            return FluidKeys.EMPTY.withAmount(0);
        } else if (amount <= toRemove) {
            FluidVolume newFluid = copy();
            setAmount(0);
            return newFluid;
        } else {
            return split0(toRemove);
        }
    }

    /** @param toTake A valid subtractable amount.
     * @return */
    protected FluidVolume split0(int toTake) {
        setAmount(getAmount() - toTake);
        return getFluidKey().withAmount(toTake);
    }

    /** @return An {@link Identifier} for the sprite that this fluid volume should render with in gui's and in-world. */
    public Identifier getSprite() {
        return getFluidKey().spriteId;
    }

    /** @return The colour tint to use when rendering this fluid volume in gui's or in-world. Note that this MUST be in
     *         0xRR_GG_BB format: <code>(r << 16) | (g << 8) | (b)</code> */
    public int getRenderColor() {
        return getFluidKey().renderColor;
    }

    public Component getName() {
        return getFluidKey().name;
    }

    @Environment(EnvType.CLIENT)
    public List<Component> getTooltipText(TooltipContext ctx) {
        List<Component> list = new ArrayList<>();
        list.add(getName());
        if (ctx.isAdvanced()) {
            list.add(new TextComponent(
                FluidRegistryEntry.getName(getFluidKey().registryEntry.backingRegistry).toString())
                    .applyFormat(ChatFormat.DARK_GRAY));
            list.add(new TextComponent(getFluidKey().registryEntry.getId().toString())
                .applyFormat(ChatFormat.DARK_GRAY));
        }
        return list;
    }

    /** Returns the {@link FluidVolumeRenderer} to use for rendering this fluid. */
    @Environment(EnvType.CLIENT)
    public FluidVolumeRenderer getRenderer() {
        return DefaultFluidVolumeRenderer.INSTANCE;
    }

    /** Delegate method to
     * {@link #getRenderer()}.{@link FluidVolumeRenderer#render(FluidVolume, List, double, double, double) render(faces,
     * x, y, z)} */
    @Environment(EnvType.CLIENT)
    public final void render(List<FluidRenderFace> faces, double x, double y, double z) {
        getRenderer().render(this, faces, x, y, z);
    }

    /** Delegate method to
     * {@link #getRenderer()}.{@link FluidVolumeRenderer#renderGuiRectangle(FluidVolume, double, double, double, double)} */
    @Environment(EnvType.CLIENT)
    public final void renderGuiRect(double x0, double y0, double x1, double y1) {
        getRenderer().renderGuiRectangle(this, x0, y0, x1, y1);
    }
}
