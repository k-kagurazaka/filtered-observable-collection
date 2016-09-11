package jp.keita.kagurazaka.filteredobservablecollection.util;

import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObservableArrayListMod<T> extends ObservableArrayList<T> {
    private final List<OnListChangedCallback> registry = new ArrayList<>();

    @Override
    public void addOnListChangedCallback(OnListChangedCallback listener) {
        registry.add(listener);
        super.addOnListChangedCallback(listener);
    }

    @Override
    public void removeOnListChangedCallback(OnListChangedCallback listener) {
        registry.remove(listener);
        super.removeOnListChangedCallback(listener);
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }

    @SuppressWarnings("unchecked")
    public void setAll(final int index, @NonNull final Collection<? extends T> collection) {
        withOutNotification(new Action() {
            @Override
            public void call() {
                int i = index;
                for (final T value : collection) {
                    set(i, value);
                    i++;
                }
            }
        });

        for (final OnListChangedCallback callback : registry) {
            if (callback != null) {
                callback.onItemRangeChanged(this, index, collection.size());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void move(final int fromPosition, final int toPosition, final int itemCount) {
        withOutNotification(new Action() {
            @Override
            public void call() {
                List<T> willBeMoved = new ArrayList<>();
                for (int i = fromPosition; i < fromPosition + itemCount; ++i) {
                    willBeMoved.add(get(i));
                }

                int actualToPosition;
                if (toPosition < fromPosition) {
                    actualToPosition = toPosition;
                } else if (fromPosition + itemCount < toPosition) {
                    actualToPosition = toPosition - itemCount;
                } else {
                    actualToPosition = fromPosition;
                }

                removeAll(willBeMoved);
                addAll(actualToPosition, willBeMoved);
            }
        });

        for (final OnListChangedCallback callback : registry) {
            if (callback != null) {
                callback.onItemRangeMoved(this, fromPosition, toPosition, itemCount);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void notifyFakeChange() {
        for (final OnListChangedCallback callback : registry) {
            if (callback != null) {
                callback.onChanged(this);
            }
        }
    }

    private void withOutNotification(final Action action) {
        for (final OnListChangedCallback callback : registry) {
            super.removeOnListChangedCallback(callback);
        }

        action.call();

        for (final OnListChangedCallback callback : registry) {
            super.addOnListChangedCallback(callback);
        }
    }

    private interface Action {
        void call();
    }
}
