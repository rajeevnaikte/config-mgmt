package com.rajeevn.common.interfaces;

import java.util.function.Consumer;

/**
 * It is {@link Callback} with ability to throw specified exception.
 * @author Rajeev Naik
 * @since 2018/03/04
 * @param <E>
 */
@FunctionalInterface
public interface ThrowableCallback<E extends Exception>
{
    void call() throws E;

    /**
     * Handler to perform when the operation represented by this interface throws exception.
     * @param onThrow
     * @return
     */
    default <E> Callback onThrow(Consumer<E> onThrow)
    {
        return (() ->
        {
            try
            {
                call();
            }
            catch (Exception e)
            {
                onThrow.accept((E) e);
            }
        });
    }
}
