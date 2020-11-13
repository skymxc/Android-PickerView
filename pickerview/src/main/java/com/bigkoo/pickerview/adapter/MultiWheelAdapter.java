package com.bigkoo.pickerview.adapter;

import com.contrarywind.adapter.WheelAdapter;
import com.contrarywind.interfaces.IWheelItem;

import java.util.List;

/**
 * <p>
 * 多级滚轮适配器
 * </p>
 *
 * @author skymxc
 * <p>
 * date: 2020/11/9
 */
public class MultiWheelAdapter<T extends IWheelItem> implements WheelAdapter<T> {
    List<T> items;

    public MultiWheelAdapter(List<T> items) {
        this.items = items;
    }

    @Override
    public int getItemsCount() {
        return items==null?0:items.size();
    }

    @Override
    public T getItem(int index) {
        if (index>=0&&index<items.size()) {
            return items.get(index);
        }
        return null;
    }

    @Override
    public int indexOf(IWheelItem o) {
        return items.indexOf(o);
    }
}
