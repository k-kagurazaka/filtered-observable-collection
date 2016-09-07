package jp.keita.kagurazaka.filteredobservablecollection.util;

import android.databinding.ObservableList;

import org.junit.rules.ExternalResource;
import org.mockito.Mockito;

import jp.keita.kagurazaka.filteredobservablecollection.FilteredReadOnlyObservableList;

public class NonObservableItemListSource extends ExternalResource {
    private boolean createEmptySource;
    private ObservableArrayListMod<String> source;
    private FilteredReadOnlyObservableList<String> list;
    private ObservableList.OnListChangedCallback<ObservableList<String>> listChangedCallback;

    public NonObservableItemListSource() {
        this(false);
    }

    public NonObservableItemListSource(boolean createEmptySource) {
        this.createEmptySource = createEmptySource;
    }

    public ObservableArrayListMod<String> getSource() {
        return source;
    }

    public ObservableList.OnListChangedCallback<ObservableList<String>> getCallback() {
        return listChangedCallback;
    }

    @SuppressWarnings("unchecked")
    public void setUpList(final FilteredReadOnlyObservableList<String> list) {
        this.list = list;
        this.listChangedCallback = Mockito.mock(ObservableList.OnListChangedCallback.class);
        this.list.addOnListChangedCallback(listChangedCallback);
    }

    @Override
    protected void before() throws Throwable {
        source = new ObservableArrayListMod<>();
        if (!createEmptySource) {
            source.add("element1");
            source.add("element2");
            source.add("element3");
            source.add("element4");
            source.add("element5");
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
