package jp.keita.kagurazaka.filteredobservablecollection.sample;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.view.View;
import android.widget.AdapterView;

import jp.keita.kagurazaka.filteredobservablecollection.Filter;
import jp.keita.kagurazaka.filteredobservablecollection.FilteredReadOnlyObservableList;
import me.tatarka.bindingcollectionadapter.ItemView;

public class ViewModel {
    private static final Filter<TodoItem> FILTER_ALL = new Filter<TodoItem>() {
        @Override
        public boolean execute(TodoItem element) {
            return true;
        }
    };

    private static final Filter<TodoItem> FILTER_ACTIVE = new Filter<TodoItem>() {
        @Override
        public boolean execute(TodoItem element) {
            return !element.getIsCompleted();
        }
    };

    private static final Filter<TodoItem> FILTER_COMPLETED = new Filter<TodoItem>() {
        @Override
        public boolean execute(TodoItem element) {
            return element.getIsCompleted();
        }
    };

    public final ObservableList<TodoItem> allTodoItemList = new ObservableArrayList<>();

    public final ItemView itemView = ItemView.of(jp.keita.kagurazaka.filteredobservablecollection.sample.BR.todoItem, R.layout.item_todo);

    public final FilteredReadOnlyObservableList<TodoItem> currentList
            = new FilteredReadOnlyObservableList<>(allTodoItemList, FILTER_ALL);

    public final AdapterView.OnItemSelectedListener onViewModeChanged
            = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    currentList.setFilter(FILTER_ALL);
                    break;
                case 1:
                    currentList.setFilter(FILTER_ACTIVE);
                    break;
                case 2:
                    currentList.setFilter(FILTER_COMPLETED);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            currentList.setFilter(FILTER_ALL);
        }
    };

    public ObservableField<TodoItem> inputTodoItem = new ObservableField<>(new TodoItem("", false));

    public final View.OnClickListener onAddButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            allTodoItemList.add(inputTodoItem.get());
            inputTodoItem.set(new TodoItem("", false));
        }
    };
}
