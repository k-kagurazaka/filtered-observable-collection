package jp.keita.kagurazaka.filteredobservablecollection;

import android.databinding.ObservableList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Arrays;

import jp.keita.kagurazaka.filteredobservablecollection.util.NonObservableItemListSource;
import jp.keita.kagurazaka.filteredobservablecollection.util.ObservableItem;
import jp.keita.kagurazaka.filteredobservablecollection.util.ObservableItemListSource;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(Enclosed.class)
public class FilteredReadOnlyObservableListTest {

    public static class NonObservableItemList {
        @Rule
        public NonObservableItemListSource rule = new NonObservableItemListSource();

        private final Filter<String> filter = new Filter<String>() {
            @Override
            public boolean execute(String element) {
                return !element.contains("2");
            }
        };

        @Test
        public void constructWithoutFilter() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            assertThat(list.size(), is(5));
            assertThat(list.get(1), is(rule.getSource().get(1)));
        }

        @Test
        public void constructWithFilter() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            assertThat(list.size(), is(4));
            assertThat(list.get(1), is(rule.getSource().get(2)));
        }

        @Test
        public void pushBack() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().add("add1");

            verify(rule.getCallback()).onItemRangeInserted(list, 5, 1);
            assertThat(list.get(5), is("add1"));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void pushBackButFiltered() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().add("add2");

            verify(rule.getCallback(), never())
                    .onItemRangeInserted((ObservableList) any(), anyInt(), anyInt());
            assertThat(list.size(), is(4));
        }

        @Test
        public void multiplePushBack() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().addAll(Arrays.asList("add1", "add2"));

            verify(rule.getCallback()).onItemRangeInserted(list, 5, 2);
            assertThat(list.get(5), is("add1"));
            assertThat(list.get(6), is("add2"));
        }

        @Test
        public void multiplePushBackButFilteredPartially() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().addAll(Arrays.asList("add1", "add2-1", "add3", "add2-2"));

            verify(rule.getCallback()).onItemRangeInserted(list, 4, 2);
            assertThat(list.get(4), is("add1"));
            assertThat(list.get(5), is("add3"));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void multiplePushBackButFilteredAll() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().addAll(Arrays.asList("add2-1", "add2-2"));

            verify(rule.getCallback(), never())
                    .onItemRangeInserted((ObservableList) any(), anyInt(), anyInt());
            assertThat(list.size(), is(4));
        }

        @Test
        public void insert() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().add(0, "add1");
            rule.getSource().add(6, "add2");

            verify(rule.getCallback()).onItemRangeInserted(list, 0, 1);
            verify(rule.getCallback()).onItemRangeInserted(list, 6, 1);
            assertThat(list.get(0), is("add1"));
            assertThat(list.get(6), is("add2"));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void insertButFiltered() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().add(0, "add2");

            verify(rule.getCallback(), never())
                    .onItemRangeInserted((ObservableList) any(), anyInt(), anyInt());
            assertThat(list.size(), is(4));
        }

        @Test
        public void multipleInsert() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().addAll(0, Arrays.asList("add1", "add2"));
            rule.getSource().addAll(7, Arrays.asList("add3", "add4"));

            verify(rule.getCallback()).onItemRangeInserted(list, 0, 2);
            verify(rule.getCallback()).onItemRangeInserted(list, 7, 2);
            assertThat(list.get(0), is("add1"));
            assertThat(list.get(1), is("add2"));
            assertThat(list.get(7), is("add3"));
            assertThat(list.get(8), is("add4"));
        }

        @Test
        public void multipleInsertButFilteredPartially() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().addAll(1, Arrays.asList("add1", "add2-1", "add3", "add2-2"));

            verify(rule.getCallback()).onItemRangeInserted(list, 1, 2);
            assertThat(list.get(1), is("add1"));
            assertThat(list.get(2), is("add3"));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void multipleInsertButFilteredAll() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().addAll(2, Arrays.asList("add2-1", "add2-2"));

            verify(rule.getCallback(), never())
                    .onItemRangeInserted((ObservableList) any(), anyInt(), anyInt());
            assertThat(list.size(), is(4));
        }

        @Test
        public void clear() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().clear();

            verify(rule.getCallback()).onItemRangeRemoved(list, 0, 5);
            assertThat(list.size(), is(0));
        }

        @Test
        public void clearWithFilter() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().clear();

            verify(rule.getCallback()).onItemRangeRemoved(list, 0, 4);
            assertThat(list.size(), is(0));
        }

        @Test
        public void remove() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().remove("element1");

            verify(rule.getCallback()).onItemRangeRemoved(list, 0, 1);
            assertThat(list.get(0), is("element2"));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void removeButFiltered() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().remove("element2");

            verify(rule.getCallback(), never())
                    .onItemRangeRemoved((ObservableList) any(), anyInt(), anyInt());
            assertThat(list.size(), is(4));
        }

        @Test
        public void multipleRemove() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().removeRange(2, 4);

            verify(rule.getCallback()).onItemRangeRemoved(list, 2, 2);
            assertThat(list.get(0), is("element1"));
            assertThat(list.get(1), is("element2"));
            assertThat(list.get(2), is("element5"));
        }

        @Test
        public void multipleRemoveButFilteredPartially() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return !element.equals("element2") && !element.equals("element4");
                }
            });
            rule.setUpList(list);

            rule.getSource().removeRange(1, 4);

            verify(rule.getCallback()).onItemRangeRemoved(list, 1, 1);
            assertThat(list.get(0), is("element1"));
            assertThat(list.get(1), is("element5"));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void multipleRemoveButFilteredAll() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return !element.equals("element2") && !element.equals("element3");
                }
            });
            rule.setUpList(list);

            rule.getSource().removeRange(1, 3);

            verify(rule.getCallback(), never())
                    .onItemRangeRemoved((ObservableList) any(), anyInt(), anyInt());
            assertThat(list.size(), is(3));
        }

        @Test
        public void replace() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().set(0, "element1-2");

            verify(rule.getCallback()).onItemRangeChanged(list, 0, 1);
            assertThat(list.get(0), is("element1-2"));
        }

        @Test
        public void replaceThenAppear() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().set(1, "changed-element");

            verify(rule.getCallback()).onItemRangeInserted(list, 1, 1);
            assertThat(list.size(), is(5));
            assertThat(list.get(1), is("changed-element"));
        }

        @Test
        public void replaceThenDisappear() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().set(2, "element3-2");

            verify(rule.getCallback()).onItemRangeRemoved(list, 1, 1);
            assertThat(list.size(), is(3));
            assertThat(list.get(1), is("element4"));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void replaceButFiltered() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().set(1, "element2-2");

            verify(rule.getCallback(), never())
                    .onItemRangeChanged((ObservableList) any(), anyInt(), anyInt());
            assertThat(list.size(), is(4));
        }

        @Test
        public void multipleReplace() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().setAll(0, Arrays.asList("element1-2", "element2-2"));

            verify(rule.getCallback()).onItemRangeChanged(list, 0, 2);
            assertThat(list.get(0), is("element1-2"));
            assertThat(list.get(1), is("element2-2"));
        }

        @Test
        public void multipleReplaceThenAppearAll() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return !element.contains("element");
                }
            });
            rule.setUpList(list);

            // element1 -> 1           |          -> 1         (appear)
            // element2 -> 2           |          -> 2         (appear)
            // element3 -> 3           |          -> 3         (appear)
            // element4 -> 4           |          -> 4         (appear)
            // element5 -> 5           |          -> 5         (appear)
            rule.getSource().setAll(0, Arrays.asList("1", "2", "3", "4", "5"));

            verify(rule.getCallback()).onItemRangeInserted(list, 0, 5);
            assertThat(list.size(), is(5));
            assertThat(list.get(0), is("1"));
            assertThat(list.get(1), is("2"));
            assertThat(list.get(2), is("3"));
            assertThat(list.get(3), is("4"));
            assertThat(list.get(4), is("5"));
        }

        @Test
        public void multipleReplaceThenDisappearAll() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return element.contains("element");
                }
            });
            rule.setUpList(list);

            // element1 -> 1           | element1 ->           (disappear)
            // element2 -> 2           | element2 ->           (disappear)
            // element3 -> 3           | element3 ->           (disappear)
            // element4 -> 4           | element4 ->           (disappear)
            // element5 -> 5           | element5 ->           (disappear)
            rule.getSource().setAll(0, Arrays.asList("1", "2", "3", "4", "5"));

            verify(rule.getCallback()).onItemRangeRemoved(list, 0, 5);
            assertThat(list.size(), is(0));
        }

        @Test
        public void multipleReplaceThenChangeAll() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return element.contains("element");
                }
            });
            rule.setUpList(list);

            // element1 -> element1m   | element1 -> element1m (change)
            // element2 -> element2m   | element2 -> element2m (change)
            // element3 -> element3m   | element3 -> element3m (change)
            // element4 -> element4m   | element4 -> element4m (change)
            // element5 -> element5m   | element5 -> element5m (change)
            rule.getSource().setAll(0,
                    Arrays.asList(
                            "element1m",
                            "element2m",
                            "element3m",
                            "element4m",
                            "element5m"));

            verify(rule.getCallback()).onItemRangeChanged(list, 0, 5);
            assertThat(list.size(), is(5));
            assertThat(list.get(0), is("element1m"));
            assertThat(list.get(1), is("element2m"));
            assertThat(list.get(2), is("element3m"));
            assertThat(list.get(3), is("element4m"));
            assertThat(list.get(4), is("element5m"));
        }

        @Test
        public void multipleReplaceButFilteredPartially1() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return !element.contains("2") && !element.contains("3");
                }
            });
            rule.setUpList(list);

            // element1 -> element1-2  | element1 ->           (disappear)
            // element2 -> appear      |          -> appear    (appear)
            // element3 -> appear      | element3 -> appear    (appear)
            // element4 -> element4m   | element4 -> element4m (changed)
            // element5 -> element5    | element5 -> element5  (not change)
            rule.getSource()
                    .setAll(0, Arrays.asList("element1-2", "appear", "appear", "element4m"));

            verify(rule.getCallback()).onItemRangeRemoved(list, 0, 1);
            verify(rule.getCallback()).onItemRangeInserted(list, 0, 2);
            verify(rule.getCallback()).onItemRangeChanged(list, 2, 1);
            assertThat(list.size(), is(4));
            assertThat(list.get(0), is("appear"));
            assertThat(list.get(1), is("appear"));
            assertThat(list.get(2), is("element4m"));
            assertThat(list.get(3), is("element5"));
        }

        @Test
        public void multipleReplaceButFilteredPartially2() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            // element1 -> element1m  | element1 -> element1m (change)
            // element2 -> element2-2 |          ->           (nothing)
            // element3 -> element3m  | element3 -> element3m (change)
            // element4 -> element4-2 | element4 ->           (disappear)
            // element5 -> element5-2 | element5 ->           (disappear)
            rule.getSource().setAll(0,
                    Arrays.asList(
                            "element1m",
                            "element2-2",
                            "element3m",
                            "element4-2",
                            "element5-2"));

            verify(rule.getCallback()).onItemRangeChanged(list, 0, 2);
            verify(rule.getCallback()).onItemRangeRemoved(list, 2, 2);
            assertThat(list.size(), is(2));
            assertThat(list.get(0), is("element1m"));
            assertThat(list.get(1), is("element3m"));
        }

        @Test
        public void multipleReplaceButFilteredPartially3() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return !element.contains("2") && !element.contains("5");
                }
            });
            rule.setUpList(list);

            // element1 -> element1m  | element1 -> element1m  (change)
            // element2 -> elementTwo |          -> elementTwo (appear)
            // element3 -> element3-2 | element3 ->            (disappear)
            // element4 -> element4m  | element4 -> element4m  (change)
            // element5 -> elementFiv |          -> elementFiv (appear)
            rule.getSource().setAll(0,
                    Arrays.asList(
                            "element1m",
                            "elementTwo",
                            "element3-2",
                            "element4m",
                            "elementFiv"));

            verify(rule.getCallback()).onItemRangeChanged(list, 0, 1);
            verify(rule.getCallback()).onItemRangeInserted(list, 1, 1);
            verify(rule.getCallback()).onItemRangeRemoved(list, 2, 1);
            verify(rule.getCallback()).onItemRangeChanged(list, 2, 1);
            verify(rule.getCallback()).onItemRangeInserted(list, 3, 1);
            assertThat(list.size(), is(4));
            assertThat(list.get(0), is("element1m"));
            assertThat(list.get(1), is("elementTwo"));
            assertThat(list.get(2), is("element4m"));
            assertThat(list.get(3), is("elementFiv"));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void multipleReplaceButFilteredAll() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return !element.contains("2") && !element.contains("3");
                }
            });
            rule.setUpList(list);

            rule.getSource().setAll(1, Arrays.asList("element2-2", "element3-2"));

            verify(rule.getCallback(), never())
                    .onItemRangeChanged((ObservableList) any(), anyInt(), anyInt());
            assertThat(list.size(), is(3));
        }

        @Test
        public void moveTopToBottom() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().move(0, 5, 2);

            verify(rule.getCallback()).onItemRangeRemoved(list, 0, 2);
            verify(rule.getCallback()).onItemRangeInserted(list, 3, 2);
            assertThat(list.get(0), is("element3"));
            assertThat(list.get(3), is("element1"));
        }

        @Test
        public void moveBottomToMiddle() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().move(3, 1, 2);

            verify(rule.getCallback()).onItemRangeRemoved(list, 3, 2);
            verify(rule.getCallback()).onItemRangeInserted(list, 1, 2);
            assertThat(list.get(1), is("element4"));
            assertThat(list.get(3), is("element2"));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void notMove() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().move(1, 2, 3);

            verify(rule.getCallback(), never())
                    .onItemRangeRemoved((ObservableList) any(), anyInt(), anyInt());
            verify(rule.getCallback(), never())
                    .onItemRangeInserted((ObservableList) any(), anyInt(), anyInt());
            assertThat(list.get(1), is("element2"));
        }

        @Test
        public void moveButFilteredPartially() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return !element.contains("2") && !element.contains("4");
                }
            });
            rule.setUpList(list);

            rule.getSource().move(0, 5, 4);

            verify(rule.getCallback()).onItemRangeRemoved(list, 0, 2);
            verify(rule.getCallback()).onItemRangeInserted(list, 1, 2);
            assertThat(list.get(0), is("element5"));
            assertThat(list.get(1), is("element1"));
            assertThat(list.get(2), is("element3"));
        }

        @Test
        @SuppressWarnings("unchecked")
        public void moveButFilteredAll() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return !element.contains("2") && !element.contains("3");
                }
            });
            rule.setUpList(list);

            rule.getSource().move(1, 0, 2);

            verify(rule.getCallback(), never())
                    .onItemRangeRemoved((ObservableList) any(), anyInt(), anyInt());
            verify(rule.getCallback(), never())
                    .onItemRangeInserted((ObservableList) any(), anyInt(), anyInt());
            assertThat(list.get(0), is("element1"));
            assertThat(list.get(1), is("element4"));
        }

        @Test
        public void changeAll() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().notifyFakeChange();

            verify(rule.getCallback()).onChanged(list);
        }

        @Test
        public void resetFilter() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            list.setFilter(new Filter<String>() {
                @Override
                public boolean execute(String element) {
                    return !element.contains("1") && !element.contains("3");
                }
            });

            verify(rule.getCallback()).onChanged(list);
            assertThat(list.get(0), is("element2"));
            assertThat(list.get(1), is("element4"));
            assertThat(list.get(2), is("element5"));
        }
    }

    public static class ObservableItemList {
        @Rule
        public ObservableItemListSource rule = new ObservableItemListSource(false);

        private final Filter<ObservableItem> filter = new Filter<ObservableItem>() {
            @Override
            public boolean execute(ObservableItem element) {
                return !element.getValue().contains("2");
            }
        };

        @Test
        public void insert() {
            FilteredReadOnlyObservableList<ObservableItem> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().add(1, new ObservableItem("add1"));

            verify(rule.getCallback()).onItemRangeInserted(list, 1, 1);
            assertThat(list.get(1).getValue(), is("add1"));

            rule.getSource().get(1).setValue("add1-changed");

            verify(rule.getCallback()).onItemRangeChanged(list, 1, 1);
        }

        @Test
        @SuppressWarnings("unchecked")
        public void remove() {
            FilteredReadOnlyObservableList<ObservableItem> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            ObservableItem willBeRemoved = rule.getSource().get(0);
            rule.getSource().remove(new ObservableItem("element1"));

            verify(rule.getCallback()).onItemRangeRemoved(list, 0, 1);
            assertThat(list.get(0).getValue(), is("element2"));

            willBeRemoved.setValue("element1-changed");

            verify(rule.getCallback(), never())
                    .onItemRangeChanged((ObservableList) any(), anyInt(), anyInt());
        }

        @Test
        public void updateItem() {
            FilteredReadOnlyObservableList<ObservableItem> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().get(1).setValue("element2-2");

            verify(rule.getCallback()).onItemRangeChanged(list, 1, 1);
        }

        @Test
        public void appearItem() {
            FilteredReadOnlyObservableList<ObservableItem> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().get(1).setValue("changed-element");

            verify(rule.getCallback()).onItemRangeInserted(list, 1, 1);
        }

        @Test
        public void disappearItem() {
            FilteredReadOnlyObservableList<ObservableItem> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().get(0).setValue("element1-2");

            verify(rule.getCallback()).onItemRangeRemoved(list, 0, 1);
        }

        @Test
        @SuppressWarnings("unchecked")
        public void updateItemButFiltered() {
            FilteredReadOnlyObservableList<ObservableItem> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource(), filter);
            rule.setUpList(list);

            rule.getSource().get(1).setValue("element2-2");

            verify(rule.getCallback(), never())
                    .onItemRangeInserted((ObservableList) any(), anyInt(), anyInt());
        }
    }

    public static class EmptySource {
        @Rule
        public NonObservableItemListSource rule = new NonObservableItemListSource(true);

        @Test
        public void pushBack() {
            FilteredReadOnlyObservableList<String> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());
            rule.setUpList(list);

            rule.getSource().add("add1");

            verify(rule.getCallback()).onItemRangeInserted(list, 0, 1);
        }
    }

    public static class WithoutObservers {
        @Rule
        public ObservableItemListSource rule = new ObservableItemListSource(false);

        @Test
        public void manipulateWithoutObservers() {
            FilteredReadOnlyObservableList<ObservableItem> list
                    = new FilteredReadOnlyObservableList<>(rule.getSource());

            rule.getSource().add(new ObservableItem("add1"));
            rule.getSource().get(0).setValue("changed");
        }
    }

    public static class UnsupportedOperation {
        @Rule
        public NonObservableItemListSource rule = new NonObservableItemListSource();

        private FilteredReadOnlyObservableListMod<String> list;

        @Before
        public void setUp() {
            list = new FilteredReadOnlyObservableListMod<>(rule.getSource());
        }

        @Test(expected = UnsupportedOperationException.class)
        public void add() {
            list.add("add1");
        }

        @Test(expected = UnsupportedOperationException.class)
        public void addAll() {
            list.addAll(Arrays.asList("add1", "add2"));
        }

        @Test(expected = UnsupportedOperationException.class)
        public void insert() {
            list.add(1, "add1");
        }

        @Test(expected = UnsupportedOperationException.class)
        public void insertAll() {
            list.addAll(1, Arrays.asList("add1", "add2"));
        }

        @Test(expected = UnsupportedOperationException.class)
        public void clear() {
            list.clear();
        }

        @Test(expected = UnsupportedOperationException.class)
        public void remove() {
            list.remove(rule.getSource().get(0));
        }

        @Test(expected = UnsupportedOperationException.class)
        public void removeAt() {
            list.remove(0);
        }

        @Test(expected = UnsupportedOperationException.class)
        public void removeAll() {
            list.removeAll(Arrays.asList(rule.getSource().get(0), rule.getSource().get(1)));
        }

        @Test(expected = UnsupportedOperationException.class)
        public void retainAll() {
            list.retainAll(Arrays.asList(rule.getSource().get(0), rule.getSource().get(1)));
        }

        @Test(expected = UnsupportedOperationException.class)
        public void set() {
            list.set(0, "changed");
        }

        @Test(expected = UnsupportedOperationException.class)
        public void removeRange() {
            list.removeRange(0, 1);
        }

        private static class FilteredReadOnlyObservableListMod<T>
                extends FilteredReadOnlyObservableList<T> {

            FilteredReadOnlyObservableListMod(final ObservableList<T> source) {
                super(source);
            }

            @Override
            public void removeRange(int fromIndex, int toIndex) {
                super.removeRange(fromIndex, toIndex);
            }
        }
    }

}
