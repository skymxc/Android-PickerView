package com.bigkoo.pickerview.builder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.entity.IDynamicWheelItem;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.DynamicWheelSelectListener;
import com.bigkoo.pickerview.view.DynamicWheelPickerView;
import com.contrarywind.interfaces.IWheelItem;
import com.contrarywind.view.WheelView;

/**
 * <p>
 *
 * </p>
 *
 * @author skymxc
 * <p>
 * date: 2020/11/9
 */
public class DynamicWheelPickerBuilder<T extends IDynamicWheelItem> {
    protected PickerOptions mPickerOptions;
    DynamicWheelSelectListener<T> listener;

    public DynamicWheelPickerBuilder(Context context, DynamicWheelSelectListener<T> listener) {
        mPickerOptions = new PickerOptions(PickerOptions.TYPE_PICKER_MULTI);
        mPickerOptions.context = context;
        this.listener = listener;
    }

    //Option
    public DynamicWheelPickerBuilder<T> setSubmitText(String textContentConfirm) {
        mPickerOptions.textContentConfirm = textContentConfirm;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setCancelText(String textContentCancel) {
        mPickerOptions.textContentCancel = textContentCancel;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setTitleText(String textContentTitle) {
        mPickerOptions.textContentTitle = textContentTitle;
        return this;
    }

    public DynamicWheelPickerBuilder<T> isDialog(boolean isDialog) {
        mPickerOptions.isDialog = isDialog;
        return this;
    }

    public DynamicWheelPickerBuilder<T> addOnCancelClickListener(View.OnClickListener cancelListener) {
        mPickerOptions.cancelListener = cancelListener;
        return this;
    }


    public DynamicWheelPickerBuilder<T> setSubmitColor(int textColorConfirm) {
        mPickerOptions.textColorConfirm = textColorConfirm;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setCancelColor(int textColorCancel) {
        mPickerOptions.textColorCancel = textColorCancel;
        return this;
    }


    /**
     * 显示时的外部背景色颜色,默认是灰色
     *
     * @param outSideColor color resId.
     * @return
     */
    public DynamicWheelPickerBuilder<T> setOutSideColor(int outSideColor) {
        mPickerOptions.outSideColor = outSideColor;
        return this;
    }

    /**
     * ViewGroup 类型
     * 设置PickerView的显示容器
     *
     * @param decorView Parent View.
     * @return
     */
    public DynamicWheelPickerBuilder<T> setDecorView(ViewGroup decorView) {
        mPickerOptions.decorView = decorView;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setLayoutRes(int res, CustomListener listener) {
        mPickerOptions.layoutRes = res;
        mPickerOptions.customListener = listener;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setBgColor(int bgColorWheel) {
        mPickerOptions.bgColorWheel = bgColorWheel;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setTitleBgColor(int bgColorTitle) {
        mPickerOptions.bgColorTitle = bgColorTitle;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setTitleColor(int textColorTitle) {
        mPickerOptions.textColorTitle = textColorTitle;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setSubCalSize(int textSizeSubmitCancel) {
        mPickerOptions.textSizeSubmitCancel = textSizeSubmitCancel;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setTitleSize(int textSizeTitle) {
        mPickerOptions.textSizeTitle = textSizeTitle;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setContentTextSize(int textSizeContent) {
        mPickerOptions.textSizeContent = textSizeContent;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setOutSideCancelable(boolean cancelable) {
        mPickerOptions.cancelable = cancelable;
        return this;
    }


    public DynamicWheelPickerBuilder<T> setLabels(String label1, String label2, String label3) {
        mPickerOptions.label1 = label1;
        mPickerOptions.label2 = label2;
        mPickerOptions.label3 = label3;
        return this;
    }

    /**
     * 设置Item 的间距倍数，用于控制 Item 高度间隔
     *
     * @param lineSpacingMultiplier 浮点型，1.0-4.0f 之间有效,超过则取极值。
     */
    public DynamicWheelPickerBuilder<T> setLineSpacingMultiplier(float lineSpacingMultiplier) {
        mPickerOptions.lineSpacingMultiplier = lineSpacingMultiplier;
        return this;
    }

    /**
     * Set item divider line type color.
     *
     * @param dividerColor color resId.
     */
    public DynamicWheelPickerBuilder<T> setDividerColor(@ColorInt int dividerColor) {
        mPickerOptions.dividerColor = dividerColor;
        return this;
    }

    /**
     * Set item divider line type.
     *
     * @param dividerType enum Type {@link WheelView.DividerType}
     */
    public DynamicWheelPickerBuilder<T> setDividerType(WheelView.DividerType dividerType) {
        mPickerOptions.dividerType = dividerType;
        return this;
    }

    /**
     * Set the textColor of selected item.
     *
     * @param textColorCenter color res.
     */
    public DynamicWheelPickerBuilder<T> setTextColorCenter(int textColorCenter) {
        mPickerOptions.textColorCenter = textColorCenter;
        return this;
    }

    /**
     * Set the textColor of outside item.
     *
     * @param textColorOut color resId.
     */
    public DynamicWheelPickerBuilder<T> setTextColorOut(@ColorInt int textColorOut) {
        mPickerOptions.textColorOut = textColorOut;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setTypeface(Typeface font) {
        mPickerOptions.font = font;
        return this;
    }

    public DynamicWheelPickerBuilder<T> setCyclic(boolean cyclic) {
        mPickerOptions.cyclic = cyclic;
        return this;
    }


    public DynamicWheelPickerBuilder<T> setTextXOffset(int xoffset_one, int xoffset_two, int xoffset_three) {
        mPickerOptions.x_offset_one = xoffset_one;
        mPickerOptions.x_offset_two = xoffset_two;
        mPickerOptions.x_offset_three = xoffset_three;
        return this;
    }

    public DynamicWheelPickerBuilder<T> isCenterLabel(boolean isCenterLabel) {
        mPickerOptions.isCenterLabel = isCenterLabel;
        return this;
    }


    /**
     * 设置最大可见数目
     *
     * @param count 建议设置为 3 ~ 9之间。
     */
    public DynamicWheelPickerBuilder<T> setItemVisibleCount(int count) {
        mPickerOptions.itemsVisibleCount = count;
        return this;
    }

    /**
     * 透明度是否渐变
     *
     * @param isAlphaGradient true of false
     */
    public DynamicWheelPickerBuilder<T> isAlphaGradient(boolean isAlphaGradient) {
        mPickerOptions.isAlphaGradient = isAlphaGradient;
        return this;
    }


    /**
     * @param listener 切换item项滚动停止时，实时回调监听。
     * @return
     */
    public DynamicWheelPickerBuilder<T> setMultiWheelChangeListener(DynamicWheelSelectListener<T> listener) {
        this.listener = listener;
        return this;
    }


    public DynamicWheelPickerView<T> build() {
        DynamicWheelPickerView<T> multiWheelPickerView = new DynamicWheelPickerView<>(mPickerOptions);
        multiWheelPickerView.setListener(listener);
        return multiWheelPickerView;
    }
}
