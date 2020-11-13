package com.bigkoo.pickerviewdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.DynamicWheelPickerBuilder;
import com.bigkoo.pickerview.builder.MultiWheelPickerBuilder;
import com.bigkoo.pickerview.listener.DynamicWheelSelectListener;
import com.bigkoo.pickerview.listener.MultiWheelSelectListener;
import com.bigkoo.pickerview.view.DynamicWheelPickerView;
import com.bigkoo.pickerview.view.MultiWheelPickerView;
import com.bigkoo.pickerviewdemo.bean.CodeTable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 测试多滚轮
 * </p>
 *
 * @author skymxc
 * <p>
 * date: 2020/11/13
 */
public class TestMultiWheelActivity extends AppCompatActivity {

    TextView mTvResult;
    TextView mTvObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_multi_wheel);
        mTvResult = findViewById(R.id.tv_result);
        mTvObserver = findViewById(R.id.tv_observer);
        findViewById(R.id.btn_fixed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fixedPicker();
            }
        });
        findViewById(R.id.btn_dynamic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dynamicPicker();
            }
        });
    }
    DynamicWheelPickerView<CodeTable> dynamicPickerView;
    private void dynamicPicker() {
        if (null == dynamicPickerView) {
            dynamicPickerView =new DynamicWheelPickerBuilder<CodeTable>(this,new DynamicWheelSelectListener<CodeTable>() {
                @Override
                public void loadNextItems(CodeTable item, int nextLevel) {
                    List<CodeTable> child = getChild(random());
                    item.setChildren(child);
                    item.setLoadNext(false);
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



    MultiWheelPickerView<CodeTable> fixedPickerView;

    private void fixedPicker() {
        if (null == fixedPickerView) {
            MultiWheelPickerBuilder<CodeTable> builder = new MultiWheelPickerBuilder<>(this,
                    new MultiWheelSelectListener<CodeTable>() {
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
                            return !selectValue.getCode().equalsIgnoreCase("all");
                        }
                    });
            fixedPickerView = builder.build();
            fixedPickerView.setTitleText("行政区划");
            fixedPickerView.setWheelItems(getPickerData());
        }
        fixedPickerView.show();
    }

    protected List<CodeTable> getPickerData() {
        List<CodeTable> villages1 = new ArrayList<>();
        villages1.add(new CodeTable("all", ""));
        villages1.add(new CodeTable("1112", "李庙"));
        villages1.add(new CodeTable("1112", "赵庙"));
        villages1.add(new CodeTable("1112", "王庙"));
        villages1.add(new CodeTable("1112", "秦庙"));
        villages1.add(new CodeTable("1112", "张庙"));

        List<CodeTable> villages2 = new ArrayList<>();
        villages2.add(new CodeTable("all", ""));
        villages2.add(new CodeTable("1112", "李村"));
        villages2.add(new CodeTable("1112", "赵村"));
        villages2.add(new CodeTable("1112", "王村"));
        villages2.add(new CodeTable("1112", "秦村"));
        villages2.add(new CodeTable("1112", "张村"));

        List<CodeTable> villages3 = new ArrayList<>();
        villages3.add(new CodeTable("all", ""));
        villages3.add(new CodeTable("1112", "李屯"));
        villages3.add(new CodeTable("1112", "赵屯"));
        villages3.add(new CodeTable("1112", "王屯"));
        villages3.add(new CodeTable("1112", "秦屯"));
        villages3.add(new CodeTable("1112", "张屯"));

        List<CodeTable> townList1 = new ArrayList<>();
        townList1.add(new CodeTable("all", ""));
        townList1.add(new CodeTable("112", "风力乡", villages1));
        townList1.add(new CodeTable("112", "智力街道"));
        townList1.add(new CodeTable("112", "化力乡", villages2));

        List<CodeTable> townList2 = new ArrayList<>();
        townList2.add(new CodeTable("all", ""));
        townList2.add(new CodeTable("22", "风雷镇", villages3));
        townList2.add(new CodeTable("22", "风牛镇"));
        townList2.add(new CodeTable("22", "风叟街道"));

        List<CodeTable> countyList1 = new ArrayList<>();
        countyList1.add(new CodeTable("all", ""));
        countyList1.add(new CodeTable("11", "狗县", townList1));
        countyList1.add(new CodeTable("11", "猫县", townList2));

        List<CodeTable> countyList2 = new ArrayList<>();
        countyList2.add(new CodeTable("all", ""));
        countyList2.add(new CodeTable("11", "置里区"));
        countyList2.add(new CodeTable("11", "利兰县"));

        List<CodeTable> countyList3 = new ArrayList<>();
        countyList3.add(new CodeTable("all", ""));
        countyList3.add(new CodeTable("11", "华丽县"));
        countyList3.add(new CodeTable("11", "四五县"));
        countyList3.add(new CodeTable("11", "物流县"));

        List<CodeTable> cityList1 = new ArrayList<>();
        cityList1.add(new CodeTable("all", ""));
        cityList1.add(new CodeTable("11", "胡市", countyList1));
        cityList1.add(new CodeTable("11", "上市"));
        cityList1.add(new CodeTable("11", "二市", countyList2));

        List<CodeTable> cityList2 = new ArrayList<>();
        cityList2.add(new CodeTable("all", ""));
        cityList2.add(new CodeTable("11", "偶市"));
        cityList2.add(new CodeTable("11", "左市"));
        cityList2.add(new CodeTable("11", "右市", countyList3));

        List<CodeTable> cityList3 = new ArrayList<>();
        cityList3.add(new CodeTable("all", ""));
        cityList3.add(new CodeTable("11", "其市"));
        cityList3.add(new CodeTable("11", "里市"));
        cityList3.add(new CodeTable("11", "而市"));

        List<CodeTable> cityList4 = new ArrayList<>();
        cityList4.add(new CodeTable("all", ""));
        cityList4.add(new CodeTable("11", "来市"));
        cityList4.add(new CodeTable("11", "莫市"));
        cityList4.add(new CodeTable("11", "吖市"));

        List<CodeTable> cityList5 = new ArrayList<>();
        cityList5.add(new CodeTable("all", ""));
        cityList5.add(new CodeTable("11", "个市"));
        cityList5.add(new CodeTable("11", "零市"));
        cityList5.add(new CodeTable("11", "比市"));

        List<CodeTable> provinceList = new ArrayList<>();
        provinceList.add(new CodeTable("98", "山西", cityList1));
        provinceList.add(new CodeTable("98", "广东", cityList2));
        provinceList.add(new CodeTable("98", "四川", cityList3));
        provinceList.add(new CodeTable("98", "湖北", cityList4));
        provinceList.add(new CodeTable("98", "湖南", cityList5));

        return provinceList;


    }
    protected List<CodeTable> getChild(int num) {
        List<CodeTable> list = new ArrayList<>(num);
        int start = num % 2 == 0 ? 0 : 1;
        for (int i = start; i < num; i++) {
            CodeTable codeTable = new CodeTable(String.valueOf(i), String.valueOf(i * num));
            codeTable.setLoadNext(i%3!=0); //3的倍数不能有下一级。
            list.add(codeTable);
        }
        return list;
    }

    protected int random() {
        return (int) (Math.random() * 10);
    }

    protected void showChange(List<CodeTable> resultList) {
        StringBuffer buffer = new StringBuffer();
        int size = resultList.size();
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                buffer.append("->");
            }
            buffer.append(resultList.get(i).getShowText());
        }
        mTvObserver.setText(buffer.toString());
    }
}
