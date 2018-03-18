package com.rajeevn.common.util;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Utility methods for operations on arrays.
 * @author Rajeev Naik
 * @since 2018/03/04
 */
public abstract class ArraysUtil
{
    /**
     * Get item at index. This method will handle for {@link IndexOutOfBoundsException}
     * @param arr
     * @param index
     * @param <T>
     * @return
     */
    public static <T> Optional<T> itemAtIndex(T[] arr, int index)
    {
        if (arr != null && arr.length > index)
        {
            return ofNullable(arr[index]);
        }
        return empty();
    }
}
