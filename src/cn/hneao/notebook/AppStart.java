package cn.hneao.notebook;

import cn.hneao.notebook.AppContext;
import cn.hneao.notebook.api.ApiClient;
import cn.hneao.notebook.bean.User;
import cn.hneao.notebook.common.StringUtils;
import cn.hneao.notebook.common.UIHelper;
import cn.hneao.notebook.ui.LoginActivity;
import cn.hneao.notebook.ui.MainActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class AppStart extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);

		//兼容旧版本cookie
		AppContext appContext = (AppContext) getApplication();
		String cookie = appContext.getProperty("cookie");
		if (StringUtils.isEmpty(cookie)) {
			String cookie_name = appContext.getProperty("cookie_name");
			String cookie_value = appContext.getProperty("cookie_value");
			if (!StringUtils.isEmpty(cookie_name)
					&& !StringUtils.isEmpty(cookie_value)) {
				cookie = cookie_name + "=" + cookie_value;
				appContext.setProperty("cookie", cookie);
				appContext.removeProperty("cookie_domain", "cookie_name",
						"cookie_value", "cookie_version", "cookie_path");
			}
		}
		
		//渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		aa.setDuration(3000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}
			
		});
		
	}
	private void redirectTo() {
		//判断是否自动登录
		AppContext appContext = (AppContext) getApplication();
		Intent intent=new Intent();
		String yhdm=appContext.getProperty("user.yhdm");
		String isRememberMe=appContext.getProperty("user.isRememberMe");
		if(!StringUtils.isEmpty(yhdm) && isRememberMe.equals("true")){
			//设置为已登录状态
			appContext.setLogin(true);
			appContext.setYhdm(appContext.getProperty("user.yhdm"));
			//跳转的主页
			intent.setClass(AppStart.this, MainActivity.class);
		}else{
			//跳转到登录页面
			intent.setClass(AppStart.this, LoginActivity.class);
		}
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}
