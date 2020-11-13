package com.bigkoo.pickerviewdemo.bean;

import com.bigkoo.pickerview.entity.IDynamicWheelItem;
import com.contrarywind.interfaces.IWheelItem;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author skymxc
 * <p>
 * date: 2020/11/6
 */
public class CodeTable implements IDynamicWheelItem {
    private String code;
    private String name;
    private List<CodeTable> children;
    private boolean loadNext =true;

    public CodeTable() {
    }

    public CodeTable(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public CodeTable(String code, String name, List<CodeTable> children) {
        this.code = code;
        this.name = name;
        this.children = children;
    }

    public boolean hasChild() {
        return children != null && children.size() > 0;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLoadNext(boolean loadNext) {
        this.loadNext = loadNext;
    }

    @Override
    public String getShowText() {
        return name;
    }

    @Override
    public boolean isLoadNext() {
        return loadNext;
    }

    public List<CodeTable> getChildren() {
        return children;
    }

    public void setChildren(List<CodeTable> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public  List<CodeTable> getNextItems() {
        return getChildren();
    }
}
