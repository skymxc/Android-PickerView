package com.bigkoo.pickerview.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.MultiWheelAdapter;
import com.bigkoo.pickerview.adapter.MultiWheelAdapter;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.MultiWheelSelectListener;
import com.contrarywind.interfaces.IWheelItem;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 多个滚轮的Picker
 * </p>
 *
 * @author skymxc
 * <p>
 * date: 2020/11/9
 */
public class MultiWheelPickerView<T extends IWheelItem> extends BasePickerView
implements View.OnClickListener {

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    protected List<T> wheelItems;
    protected MultiWheelSelectListener<T> listener;
    public MultiWheelPickerView(PickerOptions pickerOptions){
        this(pickerOptions,null);
    }
    public MultiWheelPickerView(PickerOptions pickerOptions, List<T> wheelItems) {
        super(pickerOptions.context);
        this.mPickerOptions = pickerOptions;
        this.wheelItems = wheelItems;
        initView(mPickerOptions.context);
    }

    public void setListener(MultiWheelSelectListener<T> listener) {
        this.listener = listener;
    }

    protected LinearLayout mLlContainer;
    private void initView(Context context) {
        setDialogOutSideCancelable();
        initViews();
        initAnim();
        initEvents();
        if (mPickerOptions.customListener == null) {
            LayoutInflater.from(context).inflate(mPickerOptions.layoutRes, contentContainer);

            //顶部标题
            TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
            RelativeLayout rv_top_bar = (RelativeLayout) findViewById(R.id.rv_topbar);

            //确定和取消按钮
            Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
            Button btnCancel = (Button) findViewById(R.id.btnCancel);

            btnSubmit.setTag(TAG_SUBMIT);
            btnCancel.setTag(TAG_CANCEL);
            btnSubmit.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

            //设置文字
            btnSubmit.setText(TextUtils.isEmpty(mPickerOptions.textContentConfirm) ? context.getResources().getString(R.string.pickerview_submit) : mPickerOptions.textContentConfirm);
            btnCancel.setText(TextUtils.isEmpty(mPickerOptions.textContentCancel) ? context.getResources().getString(R.string.pickerview_cancel) : mPickerOptions.textContentCancel);
            tvTitle.setText(TextUtils.isEmpty(mPickerOptions.textContentTitle) ? "" : mPickerOptions.textContentTitle);//默认为空

            //设置color
            btnSubmit.setTextColor(mPickerOptions.textColorConfirm);
            btnCancel.setTextColor(mPickerOptions.textColorCancel);
            tvTitle.setTextColor(mPickerOptions.textColorTitle);
            rv_top_bar.setBackgroundColor(mPickerOptions.bgColorTitle);

            //设置文字大小
            btnSubmit.setTextSize(mPickerOptions.textSizeSubmitCancel);
            btnCancel.setTextSize(mPickerOptions.textSizeSubmitCancel);
            tvTitle.setTextSize(mPickerOptions.textSizeTitle);
        } else {
            mPickerOptions.customListener.customLayout(LayoutInflater.from(context).inflate(mPickerOptions.layoutRes, contentContainer));
        }

        // ----滚轮布局
        mLlContainer = (LinearLayout) findViewById(R.id.ll_multi_picker);
        setupWheel();
        mLlContainer.setBackgroundColor(mPickerOptions.bgColorWheel);
        setOutSideCancelable(mPickerOptions.cancelable);

    }


    protected void setupWheel(){
        if (mLlContainer.getChildCount()>0){
            mLlContainer.removeAllViews();
        }
        if (null==wheelItems||wheelItems.size()==0) {
            return;
        }
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
    }
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
    List<T> resultList = new LinkedList<>();

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
    protected WheelView generateWheel() {
        WheelView wheelViewOne = new WheelView(mPickerOptions.context);
        wheelViewOne.setLayoutParams(generateParams());
        //是否循环
        wheelViewOne.setCyclic(mPickerOptions.cyclic);
        // //透明度渐变
        wheelViewOne.setAlphaGradient(mPickerOptions.isAlphaGradient);
        //分割线的颜色
        wheelViewOne.setDividerColor(mPickerOptions.dividerColor);
        //最大可见条目数
        wheelViewOne.setItemsVisibleCount(mPickerOptions.itemsVisibleCount);
        //分隔线类型
        wheelViewOne.setDividerType(mPickerOptions.dividerType);
        //字体样式
        wheelViewOne.setTypeface(mPickerOptions.font);
        //目间距倍数 默认1.6
        wheelViewOne.setLineSpacingMultiplier(mPickerOptions.lineSpacingMultiplier);
         //分割线以外的文字颜色
        wheelViewOne.setTextColorOut(mPickerOptions.textColorOut);
         //分割线之间的文字颜色
        wheelViewOne.setTextColorCenter(mPickerOptions.textColorCenter);
        //是否只显示中间的label,默认每个item都显示
        wheelViewOne.isCenterLabel(mPickerOptions.isCenterLabel);
        return wheelViewOne;
    }
    protected LinearLayout.LayoutParams generateParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        return params;
    }

    public List<T> getResultList() {
        return resultList;
    }

    public void setWheelItems(List<T> wheelItems) {
        this.wheelItems = wheelItems;
        setupWheel();
    }
    /**
     * 动态设置标题
     *
     * @param text 标题文本内容
     */
    public void setTitleText(String text) {
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText(text);
        }
    }
    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_SUBMIT)) {
            if (null!=listener){
                listener.onSelect(getResultList());
            }
        } else if (tag.equals(TAG_CANCEL)) {
            if (mPickerOptions.cancelListener != null) {
                mPickerOptions.cancelListener.onClick(v);
            }
        }
        dismiss();
    }
    @Override
    public boolean isDialog() {
        return mPickerOptions.isDialog;
    }
    class MultiWheelItemSelector  implements OnItemSelectedListener {

        @NonNull
        protected List<T> list;
        protected int level;

        public MultiWheelItemSelector(@NonNull List<T> list, int level) {
            this.list = list;
            this.level = level;
        }

        @Override
        public void onItemSelected(int index) {
            if (list.size() > index) {
                T codeTable = list.get(index);
                onCodeSelected(codeTable);
            }
        }

        void onCodeSelected(T wheelItem) {
            addToResult(wheelItem, level);
            //给下边的层级赋值
            int next = level + 1;
            List<T> children=wheelItem.getNextItems();
            initWheel(children, next);
        }

    }
}
