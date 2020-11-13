package com.bigkoo.pickerview.listener;

import com.contrarywind.interfaces.IWheelItem;

import java.util.List;

/**
 * <p>
 * 多滚轮侦听器
 * </p>
 *
 * @author skymxc
 * <p>
 * date: 2020/11/9
 */
public interface MultiWheelSelectListener<T extends IWheelItem> {
    /**
     * 层级分明的结果
     * 第一个是第一层的结果，第二个是第二层的结果，以此类推
     * @param result 当前选中的结果
     */
    void onChange(List<T> result);

    /**
     * 层级分明的结果
     * 第一个是第一层的结果，第二个是第二层的结果，以此类推
     * @param result 当前选中的结果
     */
    void onSelect(List<T> result);

    /**
     * 是否加入到选择结果
     * 在业务中经常出现，第一个是表示全部，不能加入到选择里，可以在这里特殊处理。
     * @param selectValue 当前这个
     * @return true 表示能加入到选择结果
     */
    boolean isAddToResult(T selectValue);
}
