package cn.hneao.notebook.ui;

import cn.hneao.notebook.AppContext;
import cn.hneao.notebook.AppManager;
import cn.hneao.notebook.R;
import cn.hneao.notebook.R.id;
import cn.hneao.notebook.R.layout;
import cn.hneao.notebook.R.menu;
import cn.hneao.notebook.common.UIHelper;
import cn.hneao.notebook.common.UpdateManager;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SetActivity extends PreferenceActivity {

	private SharedPreferences mPreferencs;

	private Preference account;// 注销用户
	private Preference myInfo;// 用户资料
	private Preference msgTemplate;// 短信
	private Preference update;// 检查更新
	private Preference about;// 软件说明

	private CheckBoxPreference autoSendMsg;// 自动发送短信
	private CheckBoxPreference voice;// 提示声音
	private CheckBoxPreference autoCheckUpdate;// 是否检查更新

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 添加当前activity到堆栈
		AppManager.getAppManager().addActivity(this);
		// 设置显示Preferences
		addPreferencesFromResource(R.xml.preferences);
		// 设置logo返回
		android.app.ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		mPreferencs = PreferenceManager.getDefaultSharedPreferences(this);

		final AppContext ac = (AppContext) getApplicationContext();
		final AppManager am = AppManager.getAppManager();
		// 注销登录
		account = (Preference) findPreference("account");
		account.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// 弹出提示框提示是否退出系统
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SetActivity.this);
				builder.setMessage(R.string.dialog_loginout).setTitle(
						R.string.dialog_info);
				// 确定按钮
				builder.setPositiveButton(R.string.dialog_sure,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// 清除用户信息，退出系统
								ac.cleanLoginInfo();
								am.AppExit(ac);
							}
						});
				builder.setNegativeButton(R.string.dialog_cancel,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 关闭dialog
								dialog.dismiss();
							}
						});
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
		});
		// 用户信息
		myInfo = (Preference) findPreference("myinfo");
		myInfo.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.showUserInfo(SetActivity.this);
				return true;
			}
		});

		// 短信模板设置
		msgTemplate = (Preference) findPreference("messagetemplate");
		msgTemplate
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(SetActivity.this,
								MessageTemplateSetActivity.class);
						startActivity(intent);
						return true;
					}
				});
		// 自动发送短信息
		autoSendMsg = (CheckBoxPreference) findPreference("autosendmessage");
		autoSendMsg.setChecked(ac.isAutoSendMessage());
		if (ac.isAutoSendMessage()) {
			autoSendMsg.setSummary("有新的考生被录取后自动发送信息通知考生");
		} else {
			autoSendMsg.setSummary("不自动发送短信通知考生录取情况");
		}
		autoSendMsg
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						ac.setAutoSendMessage(autoSendMsg.isChecked());
						if (autoSendMsg.isChecked()) {
							autoSendMsg.setSummary("有新的考生被录取后自动发送信息通知考生");
						} else {
							autoSendMsg.setSummary("不自动发送短信通知考生录取情况");
						}
						return true;
					}
				});
		// 提示声音
		voice = (CheckBoxPreference) findPreference("voice");
		// 根据配置信息设置显示
		voice.setChecked(ac.isVoice());
		if (ac.isVoice()) {
			voice.setSummary("已开启提示声音");
		} else {
			voice.setSummary("已关闭提示声音");
		}
		voice.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				ac.setConfigVoice(voice.isChecked());
				if (voice.isChecked()) {
					voice.setSummary("已开启提示声音");
				} else {
					voice.setSummary("已关闭提示声音");
				}
				return true;
			}
		});
		//启动app时是否检查更新
		autoCheckUpdate = (CheckBoxPreference) findPreference("checkup");
		autoCheckUpdate.setChecked(ac.isCheckUp());
		if (ac.isCheckUp()) {
			autoCheckUpdate.setSummary("启动程序时检查更新");
		} else {
			autoCheckUpdate.setSummary("启动程序时不检查更新");
		}
		autoCheckUpdate
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						ac.setConfigCheckUp(autoCheckUpdate.isChecked());
						if (autoCheckUpdate.isChecked()) {
							autoCheckUpdate.setSummary("启动程序时检查更新");
						} else {
							autoCheckUpdate.setSummary("启动程序时不检查更新");
						}
						return true;
					}
				});
		//检测版本更新
		update=(Preference)findPreference("update");
		update.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				UpdateManager.getUpdateManager().checkAppUpdate(SetActivity.this, true);
				return true;
			}
		});
		//软件说明
		about=(Preference)findPreference("about");
		about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				//显示软件说明页面
				Intent intent=new Intent(SetActivity.this,AboutActivity.class);
				startActivity(intent);
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

}
