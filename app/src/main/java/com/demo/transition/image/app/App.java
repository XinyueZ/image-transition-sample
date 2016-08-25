/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱。
package com.demo.transition.image.app;

import android.support.multidex.MultiDexApplication;

import com.demo.transition.image.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.converter.gson.GsonConverterFactory;


public final class App extends MultiDexApplication {
	public static final String KEY_OPEN_DETAIL_ACTIVITY = "open-detail-activity";
	public static final String KEY_USE_SUPPORT_TRANSITION_SIMPLE = "use-support-transition-simple";
	public static final String PREFS = "App";
	public static final boolean DEFAULT_OPEN_DETAIL_ACTIVITY = true;
	public static final boolean DEFAULT_USE_SUPPORT_TRANSITION_SIMPLE = false;
	@SuppressWarnings("CanBeFinal") public static App Instance;


	{
		Instance = this;
	}


	@SuppressWarnings("WeakerAccess") public static final Gson Gson = new GsonBuilder().setDateFormat("yyyy-M-d")
	                                                                                   .create();

	@SuppressWarnings("WeakerAccess") public static retrofit2.Retrofit Retrofit;

	@Override
	public void onCreate() {
		super.onCreate();
		Retrofit = new retrofit2.Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(Gson))
		                                           .baseUrl(getString(R.string.api_images))
		                                           .build();
	}
}
