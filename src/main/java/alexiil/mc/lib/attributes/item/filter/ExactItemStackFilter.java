package alexiil.mc.lib.attributes.item.filter;

import net.minecraft.item.ItemStack;

import alexiil.mc.lib.attributes.item.ItemStackUtil;

/** An {@link ItemFilter} that only matches on a single {@link ItemStack}. */
public final class ExactItemStackFilter implements ReadableItemFilter {

    public final ItemStack stack;

    public ExactItemStackFilter(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean matches(ItemStack other) {
        return ItemStackUtil.areEqualIgnoreAmounts(this.stack, other);
    }
}
