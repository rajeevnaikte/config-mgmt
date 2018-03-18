package com.rajeevn.common.interfaces;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * It is {@link Function} with ability to throw specified exception.
 * @author Rajeev Naik
 * @since 2018/03/04
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Exception>
{
    R apply(T t) throws E;

    /**
     * Handler to perform when the operation represented by this interface throws exception.
     * @param onThrow
     * @return
     */
    default Function<T, R> onThrow(Consumer<E> onThrow)
    {
        return ((T t) ->
        {
            try
            {
                return apply(t);
            }
            catch (Exception e)
            {
                onThrow.accept((E) e);
            }
            return null;
        });
    }
}
