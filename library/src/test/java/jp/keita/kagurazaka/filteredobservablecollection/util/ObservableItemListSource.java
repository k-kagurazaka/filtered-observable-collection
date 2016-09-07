package jp.keita.kagurazaka.filteredobservablecollection.util;

import android.databinding.ObservableList;

import org.junit.rules.ExternalResource;
import org.mockito.Mockito;

import jp.keita.kagurazaka.filteredobservablecollection.FilteredReadOnlyObservableList;

public class ObservableItemListSource extends ExternalResource {
    private boolean createEmptySource;
    private ObservableArrayListMod<ObservableItem> source;
    private FilteredReadOnlyObservableList<ObservableItem> list;
    private ObservableList.OnListChangedCallback<ObservableList<ObservableItem>> listChangedCallback;

    public ObservableItemListSource() {
        this(false);
    }

    public ObservableItemListSource(boolean createEmptySource) {
        this.createEmptySource = createEmptySource;
    }

    public ObservableArrayListMod<ObservableItem> getSource() {
        return source;
    }

    public ObservableList.OnListChangedCallback<ObservableList<ObservableItem>> getCallback() {
        return listChangedCallback;
    }

    @SuppressWarnings("unchecked")
    public void setUpList(final FilteredReadOnlyObservableList<ObservableItem> list) {
        this.list = list;
        this.listChangedCallback = Mockito.mock(ObservableList.OnListChangedCallback.class);
        this.list.addOnListChangedCallback(listChangedCallback);
    }

    @Override
    protected void before() throws Throwable {
        source = new ObservableArrayListMod<>();
        if (!createEmptySource) {
            source.add(new ObservableItem("element1"));
            source.add(new ObservableItem("element2"));
            source.add(new ObservableItem("element3"));
            source.add(new ObservableItem("element4"));
            source.add(new ObservableItem("element5"));
        }
    }

    @Override
    protected void after() {
        if (list != null) {
            list.removeOnListChangedCallback(listChangedCallback);
            list.close();
        }
        source = null;
        list = null;
        listChangedCallback = null;
    }
}
