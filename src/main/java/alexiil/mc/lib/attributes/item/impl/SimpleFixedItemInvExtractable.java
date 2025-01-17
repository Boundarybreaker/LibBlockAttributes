package alexiil.mc.lib.attributes.item.impl;

import net.minecraft.item.ItemStack;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemStackUtil;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;

/** @deprecated Use {@link GroupedItemInvFixedWrapper} instead of this! */
@Deprecated
public final class SimpleFixedItemInvExtractable implements ItemExtractable {

    private final FixedItemInv inv;

    public SimpleFixedItemInvExtractable(FixedItemInv inv) {
        this.inv = inv;
    }

    @Override
    public ItemStack attemptExtraction(ItemFilter filter, int maxCount, Simulation simulation) {

        if (maxCount < 0) {
            throw new IllegalArgumentException("maxAmount cannot be negative! (was " + maxCount + ")");
        }
        ItemStack stack = ItemStack.EMPTY;
        if (maxCount == 0) {
            return stack;
        }

        for (int s = 0; s < inv.getSlotCount(); s++) {
            ItemStack invStack = inv.getInvStack(s);
            if (invStack.isEmpty() || !filter.matches(invStack)) {
                continue;
            }
            if (!stack.isEmpty()) {
                if (!ItemStackUtil.areEqualIgnoreAmounts(stack, invStack)) {
                    continue;
                }
            }
            invStack = invStack.copy();

            ItemStack addable = invStack.split(maxCount);
            if (inv.setInvStack(s, invStack, simulation)) {

                if (stack.isEmpty()) {
                    stack = addable;
                } else {
                    stack.addAmount(addable.getAmount());
                }
                maxCount -= addable.getAmount();
                assert maxCount >= 0;
                if (maxCount <= 0) {
                    return stack;
                }
            }
        }

        return stack;
    }
}
