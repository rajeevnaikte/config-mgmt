package com.rajeevn.common.interfaces;

import java.util.function.Consumer;

/**
 * It is {@link Consumer} with ability to throw specified exception.
 * @author Rajeev Naik
 * @since 2018/03/04
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface ThrowableConsumer<T, E extends Exception>
{
    void accept(T t) throws E;

    /**
     * Handler to perform when the operation represented by this interface throws exception.
     * @param onThrow
     * @return
     */
    default Consumer<T> onThrow(Consumer<E> onThrow)
    {
        return ((T t) ->
        {
            try
            {
                accept(t);
            }
            catch (Exception e)
            {
                onThrow.accept((E) e);
            }
        });
    }
}
