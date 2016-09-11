package jp.keita.kagurazaka.filteredobservablecollection.util;

import android.databinding.BaseObservable;

public class ObservableItem extends BaseObservable {
    private String value;

    public ObservableItem(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        if (this.value == null && value == null) {
            return;
        }
        if (value.equals(this.value)) {
            return;
        }
        this.value = value;
        notifyPropertyChanged(1);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ObservableItem) {
            return ((ObservableItem) obj).value.equals(this.value);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ObservableItem[" + this.value + "]";
    }
}
