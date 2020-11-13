
## Android-PickerView

原介绍 [Android-PickerView](https://github.com/Bigkoo/Android-PickerView)

## 介绍

最近有一个需求是选择多级联动数据，数据级别不固定，可能是五级，可能是两级，具体看用户等级。

所以就需要一个多级联动选择控件 ，在网上一番搜索或找到了这个控件，[Android-PickerView](https://github.com/Bigkoo/Android-PickerView)

这个控件在三级以内的的联动都没有问题，但是最多只能到三级。

我在原有的基础上做了一些扩展，主要是添加了两个 picker
- MultiWheelPickerView 可以根据数据动态生成多个滚轮，不再局限于两个三个选项
- DynamicWheelPickerView 也是动态生成，但可以一级一级的加载数据并追加滚轮。

在使用时，根据自身情况让你的 JavaBean 实现 IWheelItem 或者 IDynamicWheelItem 就好。

这里记录并分享一下我的思路和实现，也希望能和大家一起讨论更好的实现方案。

![动态加载滚轮](https://github.com/skymxc/Android-PickerView/blob/master/preview/dynamic.gif)

起初，只是想根据获取到的数据动态的生成滚轮，有多少级就生成多少个，自动排列出来就好。

在看了源码后发现原来的 OptionsPickerView 里写死了三个 WheelView ，所以最多只能是三个。

如果想动态生成 WheelView 就不能写死，只能根据数据生成，所以我选择使用代码创建 WheelView，不使用 layout 布局固定数量了。

除了 WheelView 部分外，其他部分还都是使用原来的布局。

因为要动态显示数据，就不能使用原来的 `IPickerViewData`了，使用了一个新的 `IWheelItem`
```java
public interface IWheelItem {

    /**
     *
     * @return 显示在滚轮的文本
     */
    String getShowText();

    /**
     *
     * @return 下一级的数据
     */
    <T extends IWheelItem> List<T> getNextItems();

}

```
只有两个方法，返回显示数据用来显示在滚轮上；在选择了一级后自动获取下一级内容显示。

这种多级联动的数据，明显有着上下级关系，我就默认为这种结构了，一级套着一级。

并在 WheelView 里做了调整
```java
  /**
     * 获取所显示的数据源
     *
     * @param item data resource
     * @return 对应显示的字符串
     */
    private String getContentText(Object item) {
        if (item == null) {
            return "";
        } else if (item instanceof IPickerViewData) {
            return ((IPickerViewData) item).getPickerViewText();
        } else if (item instanceof Integer) {
            //如果为整形则最少保留两位数.
            return getFixNum((int) item);
        }else if (item instanceof IWheelItem){
            return ((IWheelItem)item).getShowText();
        }
        return item.toString();
    }

```



First of all, 确定数据的层级，根据层级决定生成 WheelView 的数量。
```java
   /**
     * 获取当前 list 的层级，最深有多少层
     * 需要根据层级确定多少个滚轮
     * @param list 数据
     * @return 最深层级
     */
    private int getLevel(List<T> list) {
        int level = 0;
        if (list != null && list.size() > 0) {
            level = 1;
            int childLevel = 0;
            for (T code : list) {
                List<T> children =code.getNextItems();
                int temp = getLevel(children);
                if (temp > childLevel) {
                    childLevel = temp;
                }
            }
            level += childLevel;
        }
        return level;
    }

```
我使用的是一个 LinearLayout 横向排列，用来承载动态生成的 WheelView 。

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <include
        layout="@layout/include_pickerview_topbar"
        android:layout_width="match_parent"
        android:layout_height="@dimen/pickerview_topbar_height" />

    <LinearLayout
        android:id="@+id/ll_multi_picker"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/white"
        android:gravity="center"
        android:minHeight="180dp"
        android:orientation="horizontal">
    </LinearLayout>
</LinearLayout>
```
***注意**:这里有一个问题就是，如果生成的滚轮很多，会显得比较拥挤。*

知道了要生成多少个滚轮后，代码创建直接添加到 LinearLayout 里了。
```java
int level =getLevel(wheelItems);
if (level > 0) {
    //生成 滚轮
    for (int i = 0; i < level; i++) {
        WheelView wheelView = generateWheel();
        mLlContainer.addView(wheelView);
    }
    //为滚轮赋值 ，都取第一个赋值
    initWheel(wheelItems, 0);
}
```
生成 WheelView 之后，就是给控件赋值了，我这里默认取第一个当做选中的值。

只要前边一级选中了，那就获取它的下一级数据给下一个控件赋值，如此递归到最后一个。

```java
    protected void initWheel(List<T> list, int wheelIndex) {
        WheelView wheelView = (WheelView) mLlContainer.getChildAt(wheelIndex);
        if (null == wheelView) {
            Log.d(MultiWheelPickerView.class.getSimpleName(), "initWheel: 超出了范围 " + wheelIndex + " > " + mLlContainer.getChildCount());
            return;
        }
        if (null != list && list.size() > 0) {
            wheelView.setAdapter(new MultiWheelAdapter(list));
            wheelView.setCurrentItem(0);
            wheelView.setOnItemSelectedListener(new MultiWheelItemSelector(list, wheelIndex));
            //默认选中第一项，添加到结果里。
            T wheelItem = list.get(0);
            addToResult(wheelItem, wheelIndex);
            List<T> children = list.get(0).getNextItems();
            //有子集，继续添加
            wheelIndex++;
            initWheel(children, wheelIndex);
        }else{
            for (int i=wheelIndex;i<mLlContainer.getChildCount();i++){
                wheelView = (WheelView) mLlContainer.getChildAt(i);
                wheelView.setAdapter(new MultiWheelAdapter(null));
            }
        }
    }
```
关于选中的数据和事件，和原来一样，只是换了一种形式，使用 List 容器。

按照顺序，把选中的数据都列在里面了，逻辑如下
```java
   protected void addToResult(T value, int index) {
        //  检测是否发生了变化，需要对外释放信号
        int size = resultList.size();
        Log.d(MultiWheelPickerView.class.getSimpleName(), "addToResult: " + index + "-->" + value + "; size->" + size);
        //上级换了人，下级全部移除掉
        while (index < size) {
            resultList.remove(index);
            size = resultList.size();
        }
        //已经把之后的删除了,直接添加就行了
        boolean isAddToResult =true;
        if (null!=listener){
        // 这里可以从外部判断是否可以选择，有的 是不需要选择的，例如 all, 或者 “”
            isAddToResult = listener.isAddToResult(value);
        }
        if (isAddToResult) {
            resultList.add(value);
        }
        if (null!=listener){
            listener.onChange(resultList);
        }
    }
```
就这样稍微改一改，一个动态多级关联控件就有了，在使用时，让你的 JavaBean 实现 IWheelItem 就好。

简单使用方式如下
```java
   MultiWheelPickerView<CodeTable> fixedPickerView;

    private void fixedPicker() {
        if (null == fixedPickerView) {
            MultiWheelPickerBuilder<CodeTable> builder = new MultiWheelPickerBuilder<>(this,
                    new MultiWheelSelectListener<CodeTable>() {
                        @Override
                        public void onChange(List<CodeTable> result) {
                            //在滚轮选择发生变化时会被调用
                            showChange(result);
                        }

                        @Override
                        public void onSelect(List<CodeTable> result) {
                            //在按下确定按钮时会被调用
                            StringBuffer buffer = new StringBuffer();
                            int size = result.size();
                            for (int i = 0; i < size; i++) {
                                if (i != 0) {
                                    buffer.append("->");
                                }
                                buffer.append(result.get(i).getShowText());
                            }
                            mTvResult.setText(buffer.toString());
                        }

                        @Override
                        public boolean isAddToResult(CodeTable selectValue) {
                            //此方法返回值会确定这个值是否可以被选中
                            return !selectValue.getCode().equalsIgnoreCase("all");
                        }
                    });
            fixedPickerView = builder.build();
            fixedPickerView.setTitleText("行政区划");
            fixedPickerView.setWheelItems(getPickerData());
        }
        fixedPickerView.show();
    }

```

----

虽然实现了多级联动，但是在实际使用时又发现了不可忽视的问题：
如果数据过多，就会加载很长时间，从省级到村级，会有数万条记录，一次获取过来体验太差了，而且有崩溃的风险。

更好的办法是一级一级的去获取数据，选中省级再去获取下属的市级并追加滚轮显示，选中市级再去获取县级，如此类推。

So, 接续改，因为数据也是多次获取了，就无法确定层级了，故就需要每有新的层级时添加新的 WheelView 追加到显示容器里（突然增加一个View会出现横跳的情况，最好是加入一个动画平滑一点）。

在选中一个数据时，也要判断是否需要去加载下一级，在我的需求里，有的是需要到村级，有的则需要到县级。

所以具体是否要加载下一级的配置要放出来，我这里放在了数据接口上，由数据自身判断。

在 IWheelItem 的基础上扩展了一个 IDynamicWheelItem
```java
public interface IDynamicWheelItem extends IWheelItem {
    /**
     * @return 是否需要加载下一级
     */
    boolean isLoadNext() ;
}
```

然后是在生成 WheelView 这里做了一些修改,根据传入的数据生成。

也是默认选择了第一项，如果能被选中，则继续生成或者去加载子级数据。
```java
    protected void generateWheel(List<T> data) {
        if (data != null && data.size() > 0) {
            //需要生成 wheel
            WheelView wheelView = generateWheel();
            wheelView.setAdapter(new ArrayWheelAdapter(data));
            mLlContainer.addView(wheelView);
            int level = mLlContainer.getChildCount() - 1;
            wheelView.setOnItemSelectedListener(new DynamicWheelItemSelector(data, level));
            T iWheelItem = data.get(0);
            addToResult(iWheelItem, level);
            if (canSelect(iWheelItem)) {
                List<T> nextItems = iWheelItem.getNextItems();
                if (null != nextItems && nextItems.size() > 0) {
                    generateWheel(nextItems);
                } else {
                    if (iWheelItem.isLoadNext()) {
                        loadNext(iWheelItem, ++level);
                    }
                }
            }

        }
    }
```
在选中一个数据后的滚轮赋值也做了修改，如果是判断是否需要去加载下一级数据或者是否现有数据

在后续没有数据的情况下，也没有移除掉 WheelView 。一旦没有数据就移除，会出现左右横跳的情况(这里也可以做一个动画，会显得没有那么突兀)。
```java
    /**
     * 设置下级Wheel 的数据
     *
     * @param current 数据
     * @param nextLevel   下一层
     */
    private void setupChildWheel(T current, int nextLevel) {
        if (mLlContainer.getChildCount() == nextLevel) {
            if (current.isLoadNext()) { //最后一级了，但是下一级仍然需要显示
                loadNext(current, nextLevel);
            }
            return;
        }
        List<T> nextItems = current.getNextItems();
        //对于下级wheel的设置上对应的数据，即使没有那么多级的，也不能移除view，只能将数据设置为null
        WheelView wheelView = (WheelView) mLlContainer.getChildAt(nextLevel);
        if (null != nextItems && nextItems.size() > 0) {
            //有子集
            //在 level ==count 时可能为空
            if (wheelView == null) {
                wheelView = generateWheel();
            }
            wheelView.setAdapter(new ArrayWheelAdapter(nextItems));
            wheelView.setCurrentItem(0);
            wheelView.setOnItemSelectedListener(new DynamicWheelItemSelector(nextItems, nextLevel));
            T wheelItem = nextItems.get(0);
            addToResult(wheelItem, nextLevel);
            nextLevel++;
            if (canSelect(wheelItem)) {
                setupChildWheel(wheelItem, nextLevel);
            }else{ //当前已经不能选择了，之后的滚轮数据也必须置空
                for (int i = nextLevel; i < mLlContainer.getChildCount(); i++) {
                    wheelView = (WheelView) mLlContainer.getChildAt(i);
                    wheelView.setOnItemSelectedListener(null);
                    wheelView.setAdapter(new MultiWheelAdapter(null));
                }
            }
        } else {
            //还需要判断是否需要再次去获取子集。
            //没有子集 全部置空
            for (int i = nextLevel; i < mLlContainer.getChildCount(); i++) {
                wheelView = (WheelView) mLlContainer.getChildAt(i);
                wheelView.setOnItemSelectedListener(null);
                wheelView.setAdapter(new MultiWheelAdapter(null));
            }
            //没有数据，需要去加载
            if (canSelect(current)&&current.isLoadNext()) {
                loadNext(current, nextLevel);
            }
        }
    }

```

在加载数据成功后，要将数据追加到对应的滚轮上
```java
    public void appendWheel(List<T> list, int level) {
        WheelView wheelView = null;
        if (level < mLlContainer.getChildCount()) {
            wheelView = (WheelView) mLlContainer.getChildAt(level);
        } else {
            wheelView = generateWheel();
            if (null != list && list.size() > 0)
                mLlContainer.addView(wheelView);
        }
        if (null != list && list.size() > 0) {
            wheelView.setAdapter(new MultiWheelAdapter(list));
            wheelView.setCurrentItem(0);
            T codeTable = list.get(0);
            addToResult(codeTable,level);
            wheelView.setOnItemSelectedListener(new DynamicWheelItemSelector(list, level));
            if (canSelect(codeTable)) { //合法数据，能被选择。
                //需要加载下一级
                level++;
                setupChildWheel(codeTable,level);
            }

        }
    }

```

至此，改完了，比之前那个多放出来两个方法。

在侦听器里扩展了一个加载下级的方法。
```java
public interface DynamicWheelSelectListener<T extends IDynamicWheelItem>extends MultiWheelSelectListener<T> {
    /**
     * 加载下一级的数据
     * @param item 当前数据
     * @param nextLevel 下一级的层级
     */
    void loadNextItems(T item, int nextLevel);
}
```

使用办法和上面的 MultiWheelPickerView 大同小异

```java
   DynamicWheelPickerView<CodeTable> dynamicPickerView;
    private void dynamicPicker() {
        if (null == dynamicPickerView) {
            dynamicPickerView =new DynamicWheelPickerBuilder<CodeTable>(this,new DynamicWheelSelectListener<CodeTable>() {
                @Override
                public void loadNextItems(CodeTable item, int nextLevel) {
                    //这里模拟的数据，在加载后将 isLoadNext 设置为 false。
                    List<CodeTable> child = getChild(random());
                    item.setChildren(child);
                    item.setLoadNext(false);
                    //将数据赋值到对应的控件上，nextLevel就是控件的位置。
                    dynamicPickerView.appendWheel(child, nextLevel);
                }

                @Override
                public void onChange(List<CodeTable> result) {
                    showChange(result);
                }

                @Override
                public void onSelect(List<CodeTable> result) {
                    StringBuffer buffer = new StringBuffer();
                    int size = result.size();
                    for (int i = 0; i < size; i++) {
                        if (i != 0) {
                            buffer.append("->");
                        }
                        buffer.append(result.get(i).getShowText());
                    }
                    mTvResult.setText(buffer.toString());
                }

                @Override
                public boolean isAddToResult(CodeTable selectValue) {
                    //是 0 的不能被选择
                    return !selectValue.getCode().equalsIgnoreCase("0");
                }
            })
                    .build();
            dynamicPickerView.setTitleText("行政区划");
            dynamicPickerView.setWheelItems(getChild(random()));

        }
        dynamicPickerView.show();
    }

```


具体用法可以看代码，在这里 [TestMultiWheelActivity](https://github.com/skymxc/Android-PickerView/blob/master/app/src/main/java/com/bigkoo/pickerviewdemo/TestMultiWheelActivity.java)



----

其他想法：

- 目前使用 LinearLayout 包裹的，是否可以换成 RecyclerView 呢，是否能更好的控制在一行超出多少个后换行，避免拥挤。
- 目前在动态追加滚轮时是很生硬的追加上去的，可以优化为使用动画平滑的过渡可能体验更好些。

目前把代码放在了这里 [Android-PickerView](https://github.com/skymxc/Android-PickerView)

我的实现方式就是这样，希望能和大家讨论更好的方式。
