package com.rajeevn.common.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

/**
 * Utility methods for operations on collections.
 *
 * @author Rajeev Naik
 * @since 2018/03/04
 */
public abstract class CollectionsUtil
{
    /**
     * Convert list to string by joining them with given delimiter.
     *
     * @param list
     * @param delim
     * @return
     */
    public static String listToString(List<String> list, String delim)
    {
        return ofNullable(list)
                .map(l -> l.stream().collect(joining(delim)))
                .orElse("");
    }

    /**
     * Convert string to list by splitting it on given delimiter.
     *
     * @param s
     * @param delim
     * @return
     */
    public static List<String> stringToList(String s, String delim)
    {
        return asList(ofNullable(s).filter(s1 -> !s1.isEmpty()).map(s1 -> s1.split(delim)).orElse(new String[]{}));
    }

    /**
     * return empty list if given list is null.
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> List<T> toEmptyIfNull(List<T> list)
    {
        return ofNullable(list).orElse(EMPTY_LIST);
    }

    /**
     * Return unmodifiable list if the given list is not null, or else return empty list
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> List<T> toUnmodifiableOrEmptyIfNull(List<T> list)
    {
        return ofNullable(list).map(Collections::unmodifiableList).orElse(EMPTY_LIST);
    }

    /**
     * Check it given object is present in any of given lists.
     *
     * @param obj
     * @param lists
     * @param <T>
     * @return
     */
    public static <T> boolean presentInAny(T obj, List<T>... lists)
    {
        for (List<T> list : lists)
        {
            if (list.contains(obj))
                return true;
        }
        return false;
    }

    /**
     * Flatten given collection recursively (i.e. if it is nested collections) and return flattened stream.
     *
     * @param coll
     * @param <E>
     * @return
     */
    public static <E> Stream<E> flatten(Collection coll)
    {
        return coll.stream().flatMap(o ->
        {
            if (o instanceof Collection)
                return flatten((Collection) o);
            return Stream.of(o);
        });
    }

    /**
     * Find if there is any string in the array that starts with given string
     *
     * @param startsWithStr
     * @param arr
     * @return
     */
    public static boolean isAnyStartsWith(String startsWithStr, String[] arr)
    {
        for (String item : arr)
        {
            if (item.startsWith(startsWithStr))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get first item which starts with given string
     * @param startsWithStr
     * @param arr
     * @return
     */
    public static Optional<String> getFirstStartsWith(String startsWithStr, String[] arr)
    {
        for (String item : arr)
        {
            if (item.startsWith(startsWithStr))
            {
                return of(item);
            }
        }
        return empty();
    }

    /**
     * Find if any of the string in array is starting part of given text
     * @param arr
     * @param text
     * @return
     */
    public static boolean isStartsWithAny(String[] arr, String text)
    {
        for (String item : arr)
        {
            if (!item.isEmpty() && text.startsWith(item))
            {
                return true;
            }
        }
        return false;
    }
}
