package alexiil.mc.lib.attributes.fluid.impl;

import java.util.List;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.filter.ExactFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public final class CombinedFluidExtractable implements FluidExtractable {

    private final List<? extends FluidExtractable> list;

    public CombinedFluidExtractable(List<? extends FluidExtractable> list) {
        this.list = list;
    }

    @Override
    public FluidVolume attemptExtraction(FluidFilter filter, int maxAmount, Simulation simulation) {
        if (maxAmount < 0) {
            throw new IllegalArgumentException("maxCount cannot be negative! (was " + maxAmount + ")");
        }
        FluidVolume extracted = FluidKeys.EMPTY.withAmount(0);
        for (FluidExtractable extractable : list) {
            if (extracted.isEmpty()) {
                extracted = extractable.attemptExtraction(filter, maxAmount, simulation);
                if (extracted.isEmpty()) {
                    continue;
                }
                if (extracted.getAmount() >= maxAmount) {
                    return extracted;
                }
                filter = new ExactFluidFilter(extracted.fluidKey);
            } else {
                int newMaxCount = maxAmount - extracted.getAmount();
                FluidVolume additional = extractable.attemptExtraction(filter, newMaxCount, simulation);
                if (additional.isEmpty()) {
                    continue;
                }
                extracted = FluidVolume.merge(extracted, additional);
                if (extracted == null) {
                    throw new IllegalStateException("bad FluidExtractable " + extractable.getClass().getName());
                }
                if (extracted.getAmount() >= maxAmount) {
                    return extracted;
                }
            }
        }
        return extracted;
    }
}
