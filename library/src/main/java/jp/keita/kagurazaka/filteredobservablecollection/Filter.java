package jp.keita.kagurazaka.filteredobservablecollection;

/**
 * Interface representing a filter of collection.
 *
 * @param <T> the type of collection elements to be filtered
 */
public interface Filter<T> {
    /**
     * Returns true if the specified element passes this filter.
     *
     * @param element an element to be filtered
     * @return true if the specified element passes this filter; otherwise else
     */
    boolean execute(T element);
}
