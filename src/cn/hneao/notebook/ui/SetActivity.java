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

	private Preference account;// ע���û�
	private Preference myInfo;// �û�����
	private Preference msgTemplate;// ����
	private Preference update;// ������
	private Preference about;// ���˵��

	private CheckBoxPreference autoSendMsg;// �Զ����Ͷ���
	private CheckBoxPreference voice;// ��ʾ����
	private CheckBoxPreference autoCheckUpdate;// �Ƿ������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ӵ�ǰactivity����ջ
		AppManager.getAppManager().addActivity(this);
		// ������ʾPreferences
		addPreferencesFromResource(R.xml.preferences);
		// ����logo����
		android.app.ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		mPreferencs = PreferenceManager.getDefaultSharedPreferences(this);

		final AppContext ac = (AppContext) getApplicationContext();
		final AppManager am = AppManager.getAppManager();
		// ע����¼
		account = (Preference) findPreference("account");
		account.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// ������ʾ����ʾ�Ƿ��˳�ϵͳ
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SetActivity.this);
				builder.setMessage(R.string.dialog_loginout).setTitle(
						R.string.dialog_info);
				// ȷ����ť
				builder.setPositiveButton(R.string.dialog_sure,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								// ����û���Ϣ���˳�ϵͳ
								ac.cleanLoginInfo();
								am.AppExit(ac);
							}
						});
				builder.setNegativeButton(R.string.dialog_cancel,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// �ر�dialog
								dialog.dismiss();
							}
						});
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
		});
		// �û���Ϣ
		myInfo = (Preference) findPreference("myinfo");
		myInfo.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.showUserInfo(SetActivity.this);
				return true;
			}
		});

		// ����ģ������
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
		// �Զ����Ͷ���Ϣ
		autoSendMsg = (CheckBoxPreference) findPreference("autosendmessage");
		autoSendMsg.setChecked(ac.isAutoSendMessage());
		if (ac.isAutoSendMessage()) {
			autoSendMsg.setSummary("���µĿ�����¼ȡ���Զ�������Ϣ֪ͨ����");
		} else {
			autoSendMsg.setSummary("���Զ����Ͷ���֪ͨ����¼ȡ���");
		}
		autoSendMsg
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						ac.setAutoSendMessage(autoSendMsg.isChecked());
						if (autoSendMsg.isChecked()) {
							autoSendMsg.setSummary("���µĿ�����¼ȡ���Զ�������Ϣ֪ͨ����");
						} else {
							autoSendMsg.setSummary("���Զ����Ͷ���֪ͨ����¼ȡ���");
						}
						return true;
					}
				});
		// ��ʾ����
		voice = (CheckBoxPreference) findPreference("voice");
		// ����������Ϣ������ʾ
		voice.setChecked(ac.isVoice());
		if (ac.isVoice()) {
			voice.setSummary("�ѿ�����ʾ����");
		} else {
			voice.setSummary("�ѹر���ʾ����");
		}
		voice.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				ac.setConfigVoice(voice.isChecked());
				if (voice.isChecked()) {
					voice.setSummary("�ѿ�����ʾ����");
				} else {
					voice.setSummary("�ѹر���ʾ����");
				}
				return true;
			}
		});
		//����appʱ�Ƿ������
		autoCheckUpdate = (CheckBoxPreference) findPreference("checkup");
		autoCheckUpdate.setChecked(ac.isCheckUp());
		if (ac.isCheckUp()) {
			autoCheckUpdate.setSummary("��������ʱ������");
		} else {
			autoCheckUpdate.setSummary("��������ʱ��������");
		}
		autoCheckUpdate
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						ac.setConfigCheckUp(autoCheckUpdate.isChecked());
						if (autoCheckUpdate.isChecked()) {
							autoCheckUpdate.setSummary("��������ʱ������");
						} else {
							autoCheckUpdate.setSummary("��������ʱ��������");
						}
						return true;
					}
				});
		//���汾����
		update=(Preference)findPreference("update");
		update.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				UpdateManager.getUpdateManager().checkAppUpdate(SetActivity.this, true);
				return true;
			}
		});
		//���˵��
		about=(Preference)findPreference("about");
		about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				//��ʾ���˵��ҳ��
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
