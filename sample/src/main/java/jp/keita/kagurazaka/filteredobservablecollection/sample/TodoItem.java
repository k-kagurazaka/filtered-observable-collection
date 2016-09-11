package jp.keita.kagurazaka.filteredobservablecollection.sample;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

public class TodoItem extends BaseObservable {
    private String title;
    private boolean isCompleted;

    public TodoItem(final String title, boolean isCompleted) {
        this.title = title;
        this.isCompleted = isCompleted;
    }

    private static <T> boolean compare(T value1, T value2) {
        return (value1 == null && value2 == null) || (value1 != null && value1.equals(value2));
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        if (!compare(this.title, title)) {
            this.title = title;
            notifyPropertyChanged(jp.keita.kagurazaka.filteredobservablecollection.sample.BR.title);
        }
    }

    @Bindable
    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        if (this.isCompleted != isCompleted) {
            this.isCompleted = isCompleted;
            notifyPropertyChanged(jp.keita.kagurazaka.filteredobservablecollection.sample.BR.isCompleted);
        }
    }
}
