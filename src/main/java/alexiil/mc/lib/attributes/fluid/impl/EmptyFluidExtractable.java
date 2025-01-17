package alexiil.mc.lib.attributes.fluid.impl;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

/** A {@link FluidExtractable} that never returns any items from
 * {@link #attemptExtraction(FluidFilter, int, Simulation)}. */
public enum EmptyFluidExtractable implements FluidExtractable {
    /** A {@link FluidExtractable} that should be treated as equal to null in all circumstances - that is any checks
     * that depend on an object being extractable should be considered FALSE for this instance. */
    NULL,

    /** A {@link FluidExtractable} that informs callers that it will push items into a nearby {@link FluidInsertable},
     * but doesn't expose any other item based attributes.
     * <p>
     * The buildcraft quarry is a good example of this - it doesn't have any inventory tanks itself and it pushes items
     * out of it as it mines them from the world, but item pipes should still connect to it so that it can insert into
     * them. */
    SUPPLIER;

    @Override
    public FluidVolume attemptExtraction(FluidFilter filter, int maxCount, Simulation simulation) {
        return FluidKeys.EMPTY.withAmount(0);
    }
}
