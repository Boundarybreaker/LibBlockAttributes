package alexiil.mc.lib.attributes.item.entity;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemStackUtil;
import alexiil.mc.lib.attributes.item.ItemTransferable;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;

public class ItemTransferableItemEntity implements ItemTransferable {
    private final ItemEntity entity;

    public ItemTransferableItemEntity(ItemEntity entity) {
        this.entity = entity;
    }

    @Override
    public ItemStack attemptInsertion(ItemStack stack, Simulation simulation) {
        if (!entity.isAlive()) {
            return stack;
        }
        ItemStack current = entity.getStack();
        int max = current.getMaxAmount() - current.getAmount();
        if (max <= 0 || current.isEmpty()) {
            return stack;
        }
        if (!ItemStackUtil.areEqualIgnoreAmounts(stack, current)) {
            return stack;
        }
        stack = stack.copy();
        ItemStack insertable = stack.split(max);
        if (simulation == Simulation.ACTION) {
            current = current.copy();
            current.addAmount(insertable.getAmount());
            entity.setStack(current);
        }
        return stack;
    }

    @Override
    public ItemStack attemptExtraction(ItemFilter filter, int maxAmount, Simulation simulation) {
        if (maxAmount < 1) {
            return ItemStack.EMPTY;
        }
        if (!entity.isAlive()) {
            return ItemStack.EMPTY;
        }
        ItemStack current = entity.getStack();
        if (!filter.matches(current)) {
            return ItemStack.EMPTY;
        }
        current = current.copy();
        ItemStack extracted = current.split(maxAmount);
        if (simulation == Simulation.ACTION) {
            entity.setStack(current);
            if (current.isEmpty()) {
                entity.remove();
            }
        }
        return extracted;
    }
}
