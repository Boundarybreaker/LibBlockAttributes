package alexiil.mc.lib.attributes;

import javax.annotation.Nonnull;

public class Attributes {
    public static <T> Attribute<T> create(Class<T> clazz) {
        return new Attribute<>(clazz);
    }

    public static <T> Attribute<T> create(Class<T> clazz, CustomAttributeAdder<T> customAdder) {
        return new Attribute<>(clazz, customAdder);
    }

    public static <T> DefaultedAttribute<T> createDefaulted(Class<T> clazz, @Nonnull T defaultValue) {
        return new DefaultedAttribute<>(clazz, defaultValue);
    }

    public static <T> DefaultedAttribute<T> createDefaulted(Class<T> clazz, @Nonnull T defaultValue,
        CustomAttributeAdder<T> customAdder) {
        return new DefaultedAttribute<>(clazz, defaultValue, customAdder);
    }

    public static <T> CombinableAttribute<T> createCombinable(Class<T> clazz, @Nonnull T defaultValue,
        AttributeCombiner<T> combiner) {
        return new CombinableAttribute<>(clazz, defaultValue, combiner);
    }

    public static <T> CombinableAttribute<T> createCombinable(Class<T> clazz, @Nonnull T defaultValue,
        AttributeCombiner<T> combiner, CustomAttributeAdder<T> customAdder) {
        return new CombinableAttribute<>(clazz, defaultValue, combiner, customAdder);
    }
}
