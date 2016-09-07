package jp.keita.kagurazaka.filteredobservablecollection;

import android.databinding.ListChangeRegistry;
import android.databinding.Observable;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Real-time filtered {@link ObservableList}.
 *
 * @param <T> the type of elements
 */
public class FilteredReadOnlyObservableList<T> extends ArrayList<T>
        implements ObservableList<T>, Closeable {
    private final ObservableList<T> source;
    private final List<IndexWithRemover> indexList = new ArrayList<>();
    private final Observable.OnPropertyChangedCallback itemChangedCallback;
    private final OnListChangedCallback<ObservableList<T>> listChangedCallback;
    private Filter<T> filter;
    private transient ListChangeRegistry registry;

    /**
     * Creates a new {@code FilteredReadOnlyObservableList} instance with a filter by which all
     * elements of the source {@link ObservableList} will be pass through.
     *
     * @param source an {@link ObservableList} to be filtered
     */
    public FilteredReadOnlyObservableList(final ObservableList<T> source) {
        this(source, new Filter<T>() {
            @Override
            public boolean execute(T element) {
                return true;
            }
        });
    }

    /**
     * Creates a new {@code FilteredReadOnlyObservableList} instance with the specified filter.
     *
     * @param source an {@link ObservableList} to be filtered
     */
    public FilteredReadOnlyObservableList(final ObservableList<T> source, final Filter<T> filter) {
        this.source = source;
        this.filter = filter;

        // observe item property change events when the item is Observable
        itemChangedCallback = new Observable.OnPropertyChangedCallback() {
            @SuppressWarnings("unchecked")
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                T item = (T) observable;
                int sourceIndex = source.indexOf(item);
                Integer filteredIndex = indexList.get(sourceIndex).index;
                boolean isTarget = getFilter().execute(item);

                if (filteredIndex == null && isTarget) {
                    onItemAppeared(sourceIndex);
                } else if (filteredIndex != null && !isTarget) {
                    onItemDisappeared(sourceIndex);
                } else if (filteredIndex != null) {
                    onItemUpdated(sourceIndex);
                }
            }
        };

        // setup
        int itemCount = 0;
        for (final T item : source) {
            IndexWithRemover iwr = new IndexWithRemover(null);
            if (getFilter().execute(item)) {
                iwr.index = itemCount;
                itemCount++;
                super.add(item);
            }

            if (item instanceof Observable) {
                final Observable observable = (Observable) item;
                observable.addOnPropertyChangedCallback(itemChangedCallback);
                iwr.remover = new Remover() {
                    @Override
                    public void remove() {
                        observable.removeOnPropertyChangedCallback(itemChangedCallback);
                    }
                };
            }

            indexList.add(iwr);
        }

        // observe the source list change events
        listChangedCallback = new OnListChangedCallback<ObservableList<T>>() {
            @Override
            public void onChanged(ObservableList<T> sender) {
                setFilter(getFilter());
            }

            @Override
            public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
                FilteredReadOnlyObservableList.this.onItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
                FilteredReadOnlyObservableList.this.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
                FilteredReadOnlyObservableList.this.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
                FilteredReadOnlyObservableList.this.onItemRangeRemoved(positionStart, itemCount);
            }
        };
        source.addOnListChangedCallback(listChangedCallback);
    }

    @Override
    public void close() {
        for (final IndexWithRemover iwr : indexList) {
            if (iwr.remover != null) {
                iwr.remover.remove();
                iwr.remover = null;
            }
        }
        source.removeOnListChangedCallback(listChangedCallback);
    }

    /**
     * Adds a callback to be notified when changes to the list occur.
     *
     * @param listener a callback to be added
     */
    @Override
    public void addOnListChangedCallback(OnListChangedCallback listener) {
        if (registry == null) {
            registry = new ListChangeRegistry();
        }
        registry.add(listener);
    }

    /**
     * Removes a callback previously added.
     *
     * @param listener a callback to be removed
     */
    @Override
    public void removeOnListChangedCallback(OnListChangedCallback listener) {
        if (registry != null) {
            registry.remove(listener);
        }
    }

    @Override
    public boolean add(T object) {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    @Override
    public void add(int index, T object) {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> collection) {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    @Override
    public T set(int index, T object) {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("FilteredReadOnlyObservableList is immutable.");
    }

    /**
     * Sets the specified filter to this {@code FilteredReadOnlyObservableList}.
     *
     * @param filter a filter to be set
     */
    public void setFilter(final Filter<T> filter) {
        this.filter = filter;
        super.clear();

        int itemCount = 0;
        for (int i = 0; i < indexList.size(); ++i) {
            if (getFilter().execute(source.get(i))) {
                indexList.get(i).index = itemCount;
                itemCount++;
                super.add(source.get(i));
            } else {
                indexList.get(i).index = null;
            }
        }

        registry.notifyChanged(this);
    }

    private Filter<T> getFilter() {
        return filter;
    }

    private int findNonNullNearIndex(int position) {
        for (int i = position - 1; i >= 0; --i) {
            if (indexList.get(i).index != null) {
                return indexList.get(i).index;
            }
        }
        return -1;
    }

    private int appearItem(int sourceIndex) {
        int nearIndex = findNonNullNearIndex(sourceIndex);
        int index = nearIndex + 1;
        indexList.get(sourceIndex).index = index;
        super.add(index, source.get(sourceIndex));

        for (int i = sourceIndex + 1; i < indexList.size(); ++i) {
            if (indexList.get(i).index != null) {
                indexList.get(i).index++;
            }
        }

        return index;
    }

    private int disappearItem(int sourceIndex) {
        if (indexList.get(sourceIndex).index == null) {
            return -1; // already disappeared
        }

        int index = indexList.get(sourceIndex).index;
        super.remove(index);
        indexList.get(sourceIndex).index = null;

        for (int i = sourceIndex; i < indexList.size(); ++i) {
            if (indexList.get(i).index != null) {
                indexList.get(i).index--;
            }
        }

        return index;
    }

    private void onItemAppeared(int sourceIndex) {
        int index = appearItem(sourceIndex);
        if (index >= 0) {
            registry.notifyInserted(this, index, 1);
        }
    }

    private void onItemDisappeared(int sourceIndex) {
        int index = disappearItem(sourceIndex);
        if (index >= 0) {
            registry.notifyRemoved(this, index, 1);
        }
    }

    private void onItemUpdated(int sourceIndex) {
        int index = indexList.get(sourceIndex).index;
        super.set(index, source.get(sourceIndex));
        registry.notifyChanged(this, index, 1);
    }

    private void onItemRangeAppeared(final List<Change> changes) {
        List<Integer> notifyIndices = new ArrayList<>();
        for (final Change change : changes) {
            if (change.type == Change.Type.SKIP) {
                continue;
            }
            int index = appearItem(change.sourceIndex);
            if (index >= 0) {
                notifyIndices.add(index);
            }
        }

        int notifyCount = notifyIndices.size();
        if (notifyCount > 0) {
            registry.notifyInserted(this, notifyIndices.get(0), notifyCount);
        }
    }

    private void onItemRangeDisappeared(final List<Change> changes) {
        List<Integer> notifyIndices = new ArrayList<>();
        for (final Change change : changes) {
            if (change.type == Change.Type.SKIP) {
                continue;
            }
            int index = disappearItem(change.sourceIndex);
            if (index >= 0) {
                notifyIndices.add(index);
            }
        }

        int notifyCount = notifyIndices.size();
        if (notifyCount > 0) {
            registry.notifyRemoved(this, notifyIndices.get(notifyCount - 1), notifyCount);
        }
    }

    private void onItemRangeUpdated(final List<Change> changes) {
        List<Integer> notifyIndices = new ArrayList<>();
        for (final Change change : changes) {
            if (change.type == Change.Type.SKIP) {
                continue;
            }
            int index = indexList.get(change.sourceIndex).index; // always not null
            super.set(index, source.get(change.sourceIndex));
            notifyIndices.add(index);
        }

        int notifyCount = notifyIndices.size();
        if (notifyCount > 0) {
            registry.notifyChanged(this, notifyIndices.get(0), notifyCount);
        }
    }

    private void onItemRangeChanged(int positionStart, int itemCount) {
        List<List<Change>> changesList = new ArrayList<>();
        Change.Type beforeChangeType = Change.Type.NONE;

        for (int i = positionStart; i < positionStart + itemCount; ++i) {
            Integer index = indexList.get(i).index;
            boolean isTarget = getFilter().execute(source.get(i));

            Change.Type currentChangeType;
            if (index == null && isTarget) {
                currentChangeType = Change.Type.INSERT;
            } else if (index != null && !isTarget) {
                currentChangeType = Change.Type.REMOVE;
            } else if (index != null) {
                currentChangeType = Change.Type.UPDATE;
            } else {
                currentChangeType = Change.Type.SKIP;
            }

            if (currentChangeType != Change.Type.SKIP && currentChangeType != beforeChangeType) {
                changesList.add(new ArrayList<Change>());
                beforeChangeType = currentChangeType;
            }

            if (changesList.size() > 0) {
                changesList.get(changesList.size() - 1).add(new Change(i, currentChangeType));
            }
        }

        for (final List<Change> changes : changesList) {
            if (changes == null || changes.size() == 0) {
                continue;
            }

            // check the type of the changes
            Change.Type type = Change.Type.SKIP;
            for (final Change change : changes) {
                if (change.type != Change.Type.SKIP) {
                    type = change.type;
                    break;
                }
            }
            if (type == Change.Type.SKIP) {
                continue;
            }

            switch (type) {
                case INSERT:
                    onItemRangeAppeared(changes);
                    break;
                case REMOVE:
                    onItemRangeDisappeared(changes);
                    break;
                case UPDATE:
                    onItemRangeUpdated(changes);
                    break;
                default:
                    throw new IllegalStateException("never reached");
            }
        }
    }

    private void onItemRangeInserted(int positionStart, int itemCount) {
        List<Integer> notifyIndices = new ArrayList<>();
        for (int i = positionStart; i < positionStart + itemCount; ++i) {
            IndexWithRemover iwr = new IndexWithRemover(null);

            // register callback for receiving onPropertyChanged events of inserted elements
            T item = source.get(i);
            if (item instanceof Observable) {
                final Observable observable = (Observable) item;
                observable.addOnPropertyChangedCallback(itemChangedCallback);
                iwr.remover = new Remover() {
                    @Override
                    public void remove() {
                        observable.removeOnPropertyChangedCallback(itemChangedCallback);
                    }
                };
            }

            indexList.add(i, iwr);

            if (getFilter().execute(item)) {
                int index = appearItem(i);
                if (index >= 0) {
                    notifyIndices.add(index);
                }
            }
        }

        int notifyCount = notifyIndices.size();
        if (notifyCount > 0) {
            registry.notifyInserted(this, notifyIndices.get(0), notifyCount);
        }
    }

    private void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        // not move
        if (fromPosition <= toPosition && toPosition <= fromPosition + itemCount) {
            return;
        }

        // remove -> insert
        onItemRangeRemoved(fromPosition, itemCount);

        int actualToPosition;
        if (toPosition < fromPosition) {
            actualToPosition = toPosition;
        } else if (fromPosition + itemCount < toPosition) {
            actualToPosition = toPosition - itemCount;
        } else {
            throw new IllegalStateException("never reached");
        }

        onItemRangeInserted(actualToPosition, itemCount);
    }

    private void onItemRangeRemoved(int positionStart, int itemCount) {
        List<Integer> notifyIndices = new ArrayList<>();
        for (int i = positionStart + itemCount - 1; i >= positionStart; --i) {
            IndexWithRemover iwr = indexList.get(i);
            if (iwr.remover != null) {
                iwr.remover.remove();
                iwr.remover = null;
            }

            int index = disappearItem(i);
            if (index >= 0) {
                notifyIndices.add(index);
            }
            indexList.remove(i);
        }

        int notifyCount = notifyIndices.size();
        if (notifyCount > 0) {
            registry.notifyRemoved(this, notifyIndices.get(notifyCount - 1), notifyCount);
        }
    }

    private interface Remover {
        void remove();
    }

    private static class IndexWithRemover {
        Integer index;
        Remover remover;

        IndexWithRemover(@Nullable final Integer index) {
            this(index, null);
        }

        IndexWithRemover(@Nullable final Integer index, @Nullable final Remover remover) {
            this.index = index;
            this.remover = remover;
        }
    }

    private static class Change {
        int sourceIndex;
        Type type;

        Change(int sourceIndex, Type type) {
            this.sourceIndex = sourceIndex;
            this.type = type;
        }

        enum Type {
            NONE,
            SKIP,
            INSERT,
            REMOVE,
            UPDATE
        }
    }
}
