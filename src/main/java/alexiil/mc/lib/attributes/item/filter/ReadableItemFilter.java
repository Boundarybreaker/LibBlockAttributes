package alexiil.mc.lib.attributes.item.filter;

/** Marker interface for {@link ItemFilter} that indicates that object obtaining instances of this might be able to
 * read the real contents.
 * <p>
 * NOTE: This can only be implemented by classes <strong>included in LibBlockAttributes!</strong>. (As many
 * implementations must implement direct support for subclasses of this). */
public interface ReadableItemFilter extends ItemFilter {

    public static void checkValidity(ReadableItemFilter filter) {
        String clsName = ReadableItemFilter.class.getName();
        String expectedPackage = clsName.substring(0, clsName.lastIndexOf('.'));
        if (!filter.getClass().getName().startsWith(expectedPackage)) {
            throw new IllegalStateException(
                "The owner of " + filter.getClass() + " has incorrectly implemented IReadableItemFilter!\n"
                    + "Note that only LibBlockAttributes should define readable item filters, "
                    + "as otherwise there's no way to guarentee compatibility!");
        }
    }
}
