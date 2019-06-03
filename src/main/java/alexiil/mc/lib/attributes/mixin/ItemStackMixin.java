package alexiil.mc.lib.attributes.mixin;

import alexiil.mc.lib.attributes.misc.ItemStackListenable;
import alexiil.mc.lib.attributes.misc.ItemStackListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackListenable {
	private List<ItemStackListener> listeners = new ArrayList<>();
	private ItemStack lastStack = ItemStack.EMPTY;
	@Override
	public void addListener(ItemStackListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(ItemStackListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void forceAlert() {
		alertListeners();
	}

	public void alertListeners() {
		for (ItemStackListener listener : listeners) {
			listener.listen(lastStack, (ItemStack)(Object)this);
		}
		lastStack = ((ItemStack)(Object)this).copy();
	}

	@Inject(method = "updateEmptyFlag", at = @At("TAIL"))
	private void alertEmptyUpdate(CallbackInfo ci) {
		alertListeners();
	}

	@Inject(method = "getTag", at = @At("HEAD"))
	private void alertGetTag(CallbackInfoReturnable cir) {
		alertListeners();
	}

	@Inject(method = "getSubCompoundTag", at = @At("HEAD"))
	private void alertGetSubCompoundTag(String sub, CallbackInfoReturnable cir) {
		alertListeners();
	}

	@Inject(method = "removeSubTag", at = @At("HEAD"))
	private void updateRemoveSubTag(String sub, CallbackInfo ci) {
		lastStack = ((ItemStack)(Object)this).copy();
	}

	@Inject(method = "removeSubTag", at = @At("TAIL"))
	private void alertRemoveSubTag(String sub, CallbackInfo ci) {
		alertListeners();
	}

	@Inject(method = "setTag", at = @At("HEAD"))
	private void updateSetTag(CompoundTag tag, CallbackInfo ci) {
		lastStack = ((ItemStack)(Object)this).copy();
	}

	@Inject(method = "setTag", at = @At("TAIL"))
	private void alertSetTag(CompoundTag tag, CallbackInfo ci) {
		alertListeners();
	}

	@Inject(method = "setChildTag", at = @At("HEAD"))
	private void updateSetChildTag(String name, Tag tag, CallbackInfo ci) {
		lastStack = ((ItemStack)(Object)this).copy();
	}

	@Inject(method = "setChildTag", at = @At("TAIL"))
	private void alertSetChildTag(String name, Tag tag, CallbackInfo ci) {
		alertListeners();
	}
}
