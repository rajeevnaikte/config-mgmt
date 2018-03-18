package com.rajeevn.common.interfaces;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * It is {@link BiConsumer} with ability to throw specified exception.
 * @author Rajeev Naik
 * @since 2018/03/04
 * @param <T>
 * @param <U>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowableBiConsumer<T, U, E extends Exception>
{
    void accept(T t, U u) throws E;

    /**
     * Handler to perform when the operation represented by this interface throws exception.
     * @param onThrow
     * @return
     */
    default BiConsumer<T, U> onThrow(Consumer<E> onThrow)
    {
        return ((T t, U u) ->
        {
            try
            {
                accept(t, u);
            }
            catch (Exception e)
            {
                onThrow.accept((E) e);
            }
        });
    }
}
