package alexiil.mc.lib.attributes.misc;

import net.minecraft.item.ItemStack;

public interface ItemStackListener {
	//TODO: implement ListenerToken and ListenerRemovalToken use
	public void listen(ItemStack previous, ItemStack current);
}
