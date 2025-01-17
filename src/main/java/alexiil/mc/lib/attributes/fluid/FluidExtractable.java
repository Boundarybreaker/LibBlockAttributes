package alexiil.mc.lib.attributes.fluid;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

/** Defines an object that can have items extracted from it. */
public interface FluidExtractable {

    /** Attempt to extract *any* {@link FluidVolume} from this that {@link FluidFilter#matches(FluidKey) matches} the
     * given {@link FluidFilter}.
     * 
     * @param filter
     * @param maxAmount The maximum amount of fluid that can be extracted. Negative numbers throw an exception.
     * @param simulation If {@link Simulation#SIMULATE} then this should return the same result that a call with
     *            {@link Simulation#ACTION} would do, but without modifying anything else.
     * @return A new, independent {@link FluidVolume} that was extracted. */
    FluidVolume attemptExtraction(FluidFilter filter, int maxAmount, Simulation simulation);

    /** Calls {@link #attemptExtraction(FluidFilter, int, Simulation) attemptExtraction()} with an {@link FluidFilter}
     * of {@link ConstantFluidFilter#ANYTHING}. */
    default FluidVolume attemptAnyExtraction(int maxAmount, Simulation simulation) {
        return attemptExtraction(ConstantFluidFilter.ANYTHING, maxAmount, simulation);
    }
}
