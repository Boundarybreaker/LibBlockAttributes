package alexiil.mc.lib.attributes.misc;

public interface ItemStackListenable {
	//TODO: implement ListenerToken and ListenerRemovalToken use
	void addListener(ItemStackListener listener);
	void removeListener(ItemStackListener listener);
	void forceAlert();
}
