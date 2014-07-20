package cn.hneao.notebook.ui;

import cn.hneao.notebook.AppContext;
import cn.hneao.notebook.AppException;
import cn.hneao.notebook.R;
import cn.hneao.notebook.R.id;
import cn.hneao.notebook.R.layout;
import cn.hneao.notebook.R.menu;
import cn.hneao.notebook.api.ApiClient;
import cn.hneao.notebook.bean.ManagerInfo;
import cn.hneao.notebook.bean.Result;
import cn.hneao.notebook.bean.User;
import cn.hneao.notebook.common.StringUtils;
import cn.hneao.notebook.common.UIHelper;
import android.sax.TextElementListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.os.Build;

public class LoginActivity extends ActionBarActivity {
	
	private final String TAG="myTest";
	
	private AutoCompleteTextView et_login;// �û���
	private EditText et_password;// ���������
	private CheckBox chb_rememberMe;// �´��Զ���¼
	private Button btn_login;
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		et_login = (AutoCompleteTextView) findViewById(R.id.et_login);
		et_password = (EditText) findViewById(R.id.et_password);
		chb_rememberMe = (CheckBox) findViewById(R.id.chb_rememberMe);
		btn_login = (Button) findViewById(R.id.btn_login);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		btn_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// ���������
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				// У������
				String account = et_login.getText().toString();
				String pwd = et_password.getText().toString();
				boolean isRememberMe = chb_rememberMe.isChecked();

				if (StringUtils.isEmpty(account)) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_login_null));
					return;
				}
				if (StringUtils.isEmpty(pwd)) {
					UIHelper.ToastMessage(v.getContext(),
							getString(R.string.msg_pwd_null));
					return;
				}
				// ��ʾ���ڵ�¼...
				// ProgressDialog.show(v.getContext(), "��¼", "���ڵ�¼...");
				Log.i(TAG, "--account-->>" + account);
				Log.i(TAG, "--pwd-->>" + pwd);
				Log.i(TAG, "--isRememberMe-->>" + isRememberMe);
				// ��¼ϵͳ
				login(account, pwd, isRememberMe);
			}
		});
	}

	private void login(final String account, final String pwd,
			final boolean isRememberMe) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					User user = (User) msg.obj;
					if (user != null) {
						//���ԭ�ȵ�cookie
						ApiClient.cleanCookie();
						// ��ʾ��¼�ɹ�
						UIHelper.ToastMessage(LoginActivity.this,
								R.string.msg_login_success);
						// ��תҳ��
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(intent);
						finish();
					}
				} else if (msg.what == 0) {
					// ��¼ʧ�ܣ��û������������
					UIHelper.ToastMessage(LoginActivity.this,
							getString(R.string.msg_login_fail) + msg.obj);

				} else if (msg.what == -1) {
					// ��¼ʧ�ܣ��쳣
					((AppException) msg.obj).makeToast(LoginActivity.this);
				}
			}
		};
		// ���߳�ִ�е�¼����
		new Thread() {
			public void run() {
				// ��Ϣ
				Message msg = new Message();
				try {
					AppContext ac = (AppContext) getApplication();
					User user = ac.loginVerfy(account, pwd);
					user.setYhdm(account);
					user.setYhmm(pwd);
					user.setRememberMe(isRememberMe);
					Result result = user.getValidate();
					if (result.OK()) {
						// �����û���¼��Ϣ��Properties����
						ac.saveLoginInfo(user);
						msg.what = 1;// ��¼�ɹ�
						msg.obj = user;
					} else {
						msg.what = 0;// ��¼ʧ��
						// �����¼��Ϣ
						ac.cleanLoginInfo();
						msg.obj = result.getErrorMessage();// ���͵�¼ʧ����Ϣ��handler
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
