package alexiil.mc.lib.attributes.item.filter;

import net.minecraft.item.ItemStack;

public enum ConstantItemFilter implements ReadableItemFilter {
    ANYTHING(true),
    NOTHING(false);

    private final boolean result;

    private ConstantItemFilter(boolean result) {
        this.result = result;
    }

    public static ConstantItemFilter of(boolean result) {
        return result ? ANYTHING : NOTHING;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("You should never test an IItemFilter with an empty stack!");
        }
        return result;
    }

    @Override
    public ItemFilter negate() {
        return of(!result);
    }

    @Override
    public ItemFilter and(ItemFilter other) {
        if (result) {
            return other;
        } else {
            return NOTHING;
        }
    }

    @Override
    public ItemFilter or(ItemFilter other) {
        if (result) {
            return ANYTHING;
        } else {
            return other;
        }
    }

    // Don't override asPredicate so that we still get the better version that calls our own negate(), and(), or()
    // methods.
}
