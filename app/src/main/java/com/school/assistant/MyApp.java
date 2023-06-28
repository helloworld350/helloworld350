

package com.school.assistant;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.mob.MobSDK;
import com.school.assistant.utils.sdkinit.ANRWatchDogInit;
import com.school.assistant.utils.sdkinit.UMengInit;
import com.school.assistant.utils.sdkinit.XBasicLibInit;
import com.school.assistant.utils.sdkinit.XUpdateInit;
import com.xuexiang.xui.BuildConfig;


public class MyApp extends Application {

    /**
     * @return 当前app是否是调试开发模式
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决4.x运行崩溃的问题
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLibs();
    }

    /**
     * 初始化基础库
     */
    private void initLibs() {
        // X系列基础库初始化
        XBasicLibInit.init(this);
        // 版本更新初始化
        XUpdateInit.init(this);
        // 运营统计数据
        UMengInit.init(this);
        // ANR监控
        ANRWatchDogInit.init();
        //mob隐私政策
        MobSDK.submitPolicyGrantResult(true);
    }


}