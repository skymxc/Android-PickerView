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
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.adapter.MultiWheelAdapter;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.entity.IDynamicWheelItem;
import com.bigkoo.pickerview.listener.DynamicWheelSelectListener;
import com.contrarywind.interfaces.IWheelItem;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 动态添加多个滚轮的 Picker
 * </p>
 *
 * @author skymxc
 * <p>
 * date: 2020/11/9
 */
public class DynamicWheelPickerView<T extends IDynamicWheelItem> extends BasePickerView
        implements View.OnClickListener {

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    protected List<T> wheelItems;
    protected DynamicWheelSelectListener<T> listener;

    public DynamicWheelPickerView(PickerOptions pickerOptions) {
        this(pickerOptions, null);
    }

    public DynamicWheelPickerView(PickerOptions pickerOptions, List<T> wheelItems) {
        super(pickerOptions.context);
        this.mPickerOptions = pickerOptions;
        this.wheelItems = wheelItems;
        initView(mPickerOptions.context);
    }

    public void setListener(DynamicWheelSelectListener<T> listener) {
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
        mLlContainer.setBackgroundColor(mPickerOptions.bgColorWheel);
        setOutSideCancelable(mPickerOptions.cancelable);
        setupWheel();

    }

    private boolean canSelect(T item) {
        boolean select = true;
        if (null != listener) {
            select = listener.isAddToResult(item);
        }
        return select;
    }

    protected void setupWheel() {
        if (mLlContainer.getChildCount() > 0) {
            mLlContainer.removeAllViews();
        }
        if (null == wheelItems || wheelItems.size() == 0) {
            return;
        }
        generateWheel(wheelItems);
    }

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


    List<T> resultList = new LinkedList<>();

    protected void addToResult(T value, int index) {
        //  检测是否发生了变化，需要对外释放信号
        int size = resultList.size();
        Log.d(DynamicWheelPickerView.class.getSimpleName(), "addToResult: " + index + "-->" + value + "; size->" + size);
        //上级换了人，自己全部移除掉
        while (index < size) {
            resultList.remove(index);
            size = resultList.size();
        }
        //已经把之后的删除了,直接添加就行了
        boolean isAddToResult = canSelect(value);
        if (isAddToResult) {
            resultList.add(value);
        }
        if (null != listener) {
            listener.onChange(resultList);
        }
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
            if (null != listener) {
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

    protected void loadNext(T wheelItem, int level) {
        if (null!=listener){
            listener.loadNextItems(wheelItem,level);
        }
    }

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


    class DynamicWheelItemSelector implements OnItemSelectedListener {

        @NonNull
        protected List<T> list;
        protected int level;

        public DynamicWheelItemSelector(@NonNull List<T> list, int level) {
            this.list = list;
            this.level = level;
        }

        @Override
        public void onItemSelected(int index) {
            if (list.size() > index) {
                T codeTable = list.get(index);
                addToResult(codeTable, level);
                //给下边的层级赋值
                int next = level + 1;
                setupChildWheel(codeTable,next);
            }
        }

    }
}
