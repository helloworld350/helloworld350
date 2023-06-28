package com.school.assistant.fragment.navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import com.school.assistant.R;
import com.school.assistant.adapter.entity.Lost;
import com.school.assistant.adapter.lostandfoundnav.LostDetailAdapter;

import com.school.assistant.core.BaseFragment;
import com.school.assistant.fragment.navigation.content.AddLostFragment;
import com.school.assistant.fragment.navigation.content.LostDetailFragment;
import com.school.assistant.utils.Utils;
import com.school.assistant.utils.internet.OkHttpCallback;
import com.school.assistant.utils.internet.OkhttpUtils;
import com.school.assistant.utils.service.JsonOperate;
import com.school.assistant.databinding.FragmentLostBinding;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

@Page
public class LostFragment extends BaseFragment<FragmentLostBinding> {

    /**
     * 构建ViewBinding
     *
     * @param inflater  inflater
     * @param container 容器
     * @return ViewBinding
     */

    public static final String KEY_TITLE_NAME = "title_name";
    private static String tabTitle;//选项卡标题
    /**
     * 自动注入参数，不能是private
     */
    @AutoWired(name = KEY_TITLE_NAME)
    String title;
    private String[] tabs_data = new String[]{};//选项卡组
    private int currentPosition;//当前选项卡的位置
    private LostDetailAdapter lostDetailAdapter;//丢失物品详情adapter
    private List<Lost> detailList = new ArrayList<>();//数据list

    @NonNull
    @Override
    protected FragmentLostBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentLostBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initArgs() {
        // 自动注入参数必须在initArgs里进行注入
        XRouter.getInstance().inject(this);
    }

    @Override
    protected String getPageTitle() {
        return title;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.add) {
            @Override
            public void performAction(View view) {
                openPage(AddLostFragment.class);
            }
        });
        return titleBar;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        startAnim();//显示加载动画
        String[] types = getResources().getStringArray(R.array.type_titles);//根据app语言获取不同的数据
        operate_tabs(types);//选项卡
        tabTitle = types[0];//初始值
        lostDetailAdapter = new LostDetailAdapter(getContext());
        binding.listview.setAdapter(lostDetailAdapter);
        getTypeDetailList();

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        //跳转丢失物品详情页面
        binding.listview.setOnItemClickListener((parent, view, position, id) -> {
            Lost lost = lostDetailAdapter.getItem(position);//获取lost实例
            openPage(LostDetailFragment.class, LostDetailFragment.KEY_LOST, lost);
        });

    }

    //对选项卡进行操作
    private void operate_tabs(String[] tabs_datas) {
        currentPosition = 0;//选项卡当前位置
        tabs_data = tabs_datas;
        //选项卡内容
        for (String tab : tabs_data) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(tab));
        }
        stopAnim();//结束加载动画
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentPosition = tab.getPosition();
                tabTitle = tabs_data[currentPosition];
                //数据更新
                lostDetailAdapter = new LostDetailAdapter(getContext());
                binding.listview.setAdapter(lostDetailAdapter);
                getTypeDetailList();
                tab.setText(tabTitle);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    //发送请求获取分类下的所有内容
    private void getTypeDetailList() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkhttpUtils.get(Utils.rebuildUrl("/DetailByTitle?title=" + tabTitle, getContext()), new OkHttpCallback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        super.onResponse(call, response);
                        //获取返回结果转换list
                        detailList = JsonOperate.getList(result, Lost.class);
                        //回调主线程
                        getActivity().runOnUiThread(() -> lostDetailAdapter.setData(detailList, 1));
                    }

                });
            }
        }.start();
    }

    //显示加载动画
    private void startAnim() {
        binding.avLoad.show();
    }

    //结束加载动画
    private void stopAnim() {
        binding.avLoad.hide();
    }


}