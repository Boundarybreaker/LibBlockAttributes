package alexiil.mc.lib.attributes.fluid.impl;

import java.util.Collections;
import java.util.Set;

import alexiil.mc.lib.attributes.ListenerRemovalToken;
import alexiil.mc.lib.attributes.ListenerToken;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInvAmountChangeListener;
import alexiil.mc.lib.attributes.fluid.GroupedFluidInv;
import alexiil.mc.lib.attributes.fluid.GroupedFluidInvView;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

/** {@link GroupedFluidInvView} for an empty inventory. */
public enum EmptyGroupedFluidInv implements GroupedFluidInv {
    INSTANCE;

    @Override
    public FluidInvStatistic getStatistics(FluidFilter filter) {
        return new FluidInvStatistic(filter, 0, 0, 0);
    }

    @Override
    public Set<FluidKey> getStoredFluids() {
        return Collections.emptySet();
    }

    @Override
    public int getTotalCapacity() {
        return 0;
    }

    @Override
    public ListenerToken addListener(FluidInvAmountChangeListener listener, ListenerRemovalToken removalToken) {
        // We don't need to keep track of the listener because this empty inventory never changes.
        return () -> {
            // (And we don't need to do anything when the listener is removed)
        };
        // Never call the removal token as it's unnecessary (and saves the caller from re-adding it every tick)
    }

    @Override
    public FluidVolume attemptInsertion(FluidVolume fluid, Simulation simulation) {
        return fluid;
    }

    @Override
    public FluidVolume attemptExtraction(FluidFilter filter, int maxAmount, Simulation simulation) {
        return FluidKeys.EMPTY.withAmount(0);
    }
}
