package cn.hneao.notebook.common;

import cn.hneao.notebook.AppContext;
import cn.hneao.notebook.AppManager;
import cn.hneao.notebook.R;
import cn.hneao.notebook.ui.LoginActivity;
import cn.hneao.notebook.ui.UserInfoActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

public class UIHelper {
	/**
	 * 加载数据的触发情况
	 * 以往内容为空时加载数据
	 */
	public final static int LISTVIEW_ACTION_INIT = 0x01;
	/**
	 * 动作类型-执行下拉刷新时的刷新（ 强制刷新）
	 */
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	/**
	 * 动作类型-滚动到ListView底部时刷新 （强制刷新）
	 */
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	/**
	 * 动作类型-点击了顶部按钮时的刷新（如点击已投档按钮）
	 */
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;
	
	/**
	 * ListView控件数据状态 ，可加载更多的数据
	 * 此状态存储在ListView的tag中
	 */
	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	/**
	 * ListView控件数据状态-已加载全部数据
	 */
	public final static int LISTVIEW_DATA_FULL = 0x03;
	/**
	 * ListView控件数据状态-数据为空
	 */
	public final static int LISTVIEW_DATA_EMPTY = 0x04;
	
	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}
	
	
	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //模拟器
						i.setType("message/rfc822"); // 真机
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "741852440@qq.com" });
						i.putExtra(Intent.EXTRA_SUBJECT,
								"录取记事本- 错误报告");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "发送错误报告"));
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.show();
	}
	/**
	 * 显示用户个人信息页面
	 * @param context
	 */
	public static void showUserInfo(Activity context){
		AppContext ac=(AppContext)context.getApplicationContext();
		Intent intent=new Intent();
		if(!ac.isLogin()){
			//转向登录页面
			intent.setClass(context, LoginActivity.class);
		}else{
			//显示个人信息页面
			intent.setClass(context, UserInfoActivity.class);
		}
		context.startActivity(intent);
	}
	/**
	 * 跳转到登录页面
	 * @param context
	 */
	public static void locationToLogin(Activity context){
		Intent intent=new Intent(context,LoginActivity.class);
		context.startActivity(intent);
	}
}
