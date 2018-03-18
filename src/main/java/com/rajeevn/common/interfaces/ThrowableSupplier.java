package com.rajeevn.common.interfaces;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * It is {@link Supplier} with ability to throw specified exception.
 * @author Rajeev Naik
 * @since 2018/03/04
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowableSupplier<T, E extends Exception>
{
    T get() throws E;

    /**
     * Handler to perform when the operation represented by this interface throws exception.
     * @param onThrow
     * @return
     */
    default Supplier<T> onThrow(Consumer<E> onThrow)
    {
        return (() ->
        {
            try
            {
                return get();
            }
            catch (Exception e)
            {
                onThrow.accept((E) e);
            }
            return null;
        });
    }
}
