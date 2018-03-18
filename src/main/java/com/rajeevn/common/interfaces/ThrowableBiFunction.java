package com.rajeevn.common.interfaces;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * It is {@link BiFunction} with ability to throw specified exception.
 * @author Rajeev Naik
 * @since 2018/03/04
 * @param <T>
 * @param <U>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowableBiFunction<T, U, R, E extends Exception>
{
    R apply(T t, U u) throws E;

    /**
     * Handler to perform when the operation represented by this interface throws exception.
     * @param onThrow
     * @return
     */
    default BiFunction<T, U, R> onThrow(Consumer<E> onThrow)
    {
        return ((T t, U u) ->
        {
            try
            {
                return apply(t, u);
            }
            catch (Exception e)
            {
                onThrow.accept((E) e);
            }
            return null;
        });
    }
}
