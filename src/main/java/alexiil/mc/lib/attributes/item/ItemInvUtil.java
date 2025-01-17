package alexiil.mc.lib.attributes.item;

import java.util.function.Consumer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.AggregateItemFilter;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;

/** Various hooks and methods for dealing with pairs of {@link FixedItemInv}, {@link FixedItemInvView},
 * {@link ItemInsertable}, {@link ItemExtractable}, and {@link GroupedItemInvView} instances. */
public enum ItemInvUtil {
    ;

    // #######################
    // Direct utility methods
    // #######################

    public static Consumer<ItemStack> createPlayerInsertable(PlayerEntity player) {
        return stack -> insertItemIntoPlayerInventory(player, stack);
    }

    public static void insertItemIntoPlayerInventory(PlayerEntity player, ItemStack stack) {
        if (player.inventory.insertStack(stack) && stack.isEmpty()) {
            return;
        }
        player.dropItem(stack, /* PreventPlayerQuickPickup = */ false);
    }

    /** Attempts to move up to the given maximum number of items from the {@link ItemExtractable} to the
     * {@link ItemInsertable}.
     * 
     * @return The number of items moved.
     * @see #move(ItemExtractable, ItemInsertable, ItemFilter, int) */
    public static int move(ItemExtractable from, ItemInsertable to, int maximum) {
        return move(from, to, null, maximum);
    }

    /** Attempts to move up to the given maximum number of items from the {@link ItemExtractable} to the
     * {@link ItemInsertable}, provided they match the given {@link ItemFilter}.
     * 
     * @return The number of items moved. */
    public static int move(ItemExtractable from, ItemInsertable to, ItemFilter filter, int maximum) {
        ItemFilter insertionFilter = to.getInsertionFilter();
        if (filter != null && filter != ConstantItemFilter.ANYTHING) {
            insertionFilter = AggregateItemFilter.and(insertionFilter, filter);
        }

        ItemStack extracted = from.attemptExtraction(insertionFilter, maximum, Simulation.SIMULATE);
        if (extracted.isEmpty()) {
            return 0;
        }
        ItemStack leftover = to.attemptInsertion(extracted, Simulation.ACTION);
        int insertedAmount = extracted.getAmount() - (leftover.isEmpty() ? 0 : leftover.getAmount());
        ItemStack reallyExtracted = from.attemptExtraction(insertionFilter, insertedAmount, Simulation.ACTION);

        if (reallyExtracted.isEmpty()) {
            throw throwBadImplException("Tried to extract the filter (C) from A but it returned an empty item stack "
                + "after we have already inserted the expected stack into B!\nThe inventory is now in an invalid (duped) state!",
                new String[] { "from A", "to B", "filter C" }, new Object[] { from, to, filter });
        }
        if (reallyExtracted.getAmount() != insertedAmount) {
            throw throwBadImplException(
                "Tried to extract " + insertedAmount + " but we actually extracted " + reallyExtracted.getAmount()
                    + "!\nThe inventory is now in an invalid (duped) state!",
                new String[] { "from A", "to B", "filter C", "originally extracted", "really extracted" },
                new Object[] { from, to, insertionFilter, extracted, reallyExtracted });
        }
        return insertedAmount;
    }

    private static IllegalStateException throwBadImplException(String reason, String[] names, Object[] objs) {
        String detail = "\n";
        int max = Math.max(names.length, objs.length);
        for (int i = 0; i < max; i++) {
            String name = names.length <= i ? "?" : names[i];
            Object obj = objs.length <= i ? "" : objs[i];
            // TODO: Full object detail!
            detail += "\n" + name + " = " + obj;
        }
        throw new IllegalStateException(reason + detail);
    }
}
