# filtered-observable-collection

Real-time filtered ObservableList for Android Data binding.

This library is inspired by `IFilteredReadOnlyObservableCollection` interface of [ReactiveProperty](https://github.com/runceel/ReactiveProperty).


## Download

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.k-kagurazaka.filtered-observable-collection:library:1.0.0'
}
```


## Usage

`FilteredReadOnlyObservableList` provides a real-time filtering view of `ObservableList`.

```java
// create a source ObservableList
ObservableList<String> allList = new ObservableArrayList<>();

// create an ObservableList that only contains elements of which length less than 5
FilteredReadOnlyObservableList<String> filteredList
    = new FilteredReadOnlyObservableList<>(allList, it -> it.length() < 5);
    
// => filteredList == []

// when the source ObservableList changes, FilteredReadOnlyObservableList reflects the change
// with the specified filter
allList.addAll(Arrays.asList("e1", "e2", "long-name-e3", "long-name-e4", "e5"));

// => filteredList == ["e1", "e2", "e5"]

// change the filter
filteredList.setFilter(it -> it.contains("long"));

// => filteredList == ["long-name-e3", "long-name-e4"]
```

## Observe changes of `FilteredReadOnlyObservableList`

`FilteredReadOnlyObservableList` implements `ObservableList`, then you can observe the changes as follows:

```java
filteredList.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<String>>() {
    @Override
    public void onChanged(ObservableList<String> sender) {
        ...
    }

    @Override
    public void onItemRangeChanged(ObservableList<String> sender, int positionStart, int itemCount) {
        ...
    }

    @Override
    public void onItemRangeInserted(ObservableList<String> sender, int positionStart, int itemCount) {
        ...
    }

    @Override
    public void onItemRangeMoved(ObservableList<String> sender, int fromPosition, int toPosition, int itemCount) {
        ...
    }

    @Override
    public void onItemRangeRemoved(ObservableList<String> sender, int positionStart, int itemCount) {
        ...
    }
});
```

Unlike `ObservableArrayList`, `FilteredReadOnlyObservableList` raises change events when the elements implement `Observable` and it's property changes.

```java
public class Animal extends BaseObservable {
    ...
}

ObservableList<Animal> sourceList = new ObservableArrayList<>();
sourceList.addAll(Arrays.asList(new Animal("Tom"), new Animal("Jerry")));

FilteredReadOnlyObservableList<Animal> filteredList
    = new FilteredReadOnlyObservableList<>(sourceList, it -> it.getName().length() > 3);

// when a property of elements of sourceList changes, filteredList raises onItemRangeChanged
sourceList.get(1).setName("Jerry Mouse");

// => filteredList == ["Jerry Mouse"]

// when a new element appears by a change of sourceList element property, filteredList raises onItemRangeInserted
sourceList.get(0).setName("Tom Cat");

// => filteredList == ["Tom Cat", "Jerry Mouse"]
```


## License

    The MIT License (MIT)

    Copyright (c) 2016 Keita Kagurazaka

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
