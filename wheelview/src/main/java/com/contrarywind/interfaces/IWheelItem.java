package com.contrarywind.interfaces;

import java.util.List;

/**
 * <p>
 * Wheel 数据item
 * </p>
 *
 * @author skymxc
 * <p>
 * date: 2020/11/9
 */
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
