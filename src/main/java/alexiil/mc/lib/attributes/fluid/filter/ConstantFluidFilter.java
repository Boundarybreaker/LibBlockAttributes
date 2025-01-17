package alexiil.mc.lib.attributes.fluid.filter;

import alexiil.mc.lib.attributes.fluid.volume.FluidKey;

public enum ConstantFluidFilter implements ReadableFluidFilter {
    ANYTHING(true),
    NOTHING(false);

    private final boolean result;

    private ConstantFluidFilter(boolean result) {
        this.result = result;
    }

    public static ConstantFluidFilter of(boolean result) {
        return result ? ANYTHING : NOTHING;
    }

    @Override
    public boolean matches(FluidKey fluid) {
        if (fluid.isEmpty()) {
            throw new IllegalArgumentException("You should never test an IFluidFilter with an empty fluid!");
        }
        return result;
    }

    @Override
    public FluidFilter negate() {
        return of(!result);
    }

    @Override
    public FluidFilter and(FluidFilter other) {
        if (result) {
            return other;
        } else {
            return NOTHING;
        }
    }

    @Override
    public FluidFilter or(FluidFilter other) {
        if (result) {
            return ANYTHING;
        } else {
            return other;
        }
    }

    // Don't override asPredicate so that we still get the better version that calls our own negate(), and(), or()
    // methods.
}
