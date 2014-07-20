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
	
	private AutoCompleteTextView et_login;// 用户名
	private EditText et_password;// 密码输入框
	private CheckBox chb_rememberMe;// 下次自动登录
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
				// 隐藏软键盘
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				// 校验输入
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
				// 显示正在登录...
				// ProgressDialog.show(v.getContext(), "登录", "正在登录...");
				Log.i(TAG, "--account-->>" + account);
				Log.i(TAG, "--pwd-->>" + pwd);
				Log.i(TAG, "--isRememberMe-->>" + isRememberMe);
				// 登录系统
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
						//清除原先的cookie
						ApiClient.cleanCookie();
						// 提示登录成功
						UIHelper.ToastMessage(LoginActivity.this,
								R.string.msg_login_success);
						// 跳转页面
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(intent);
						finish();
					}
				} else if (msg.what == 0) {
					// 登录失败，用户名或密码错误
					UIHelper.ToastMessage(LoginActivity.this,
							getString(R.string.msg_login_fail) + msg.obj);

				} else if (msg.what == -1) {
					// 登录失败，异常
					((AppException) msg.obj).makeToast(LoginActivity.this);
				}
			}
		};
		// 新线程执行登录操作
		new Thread() {
			public void run() {
				// 消息
				Message msg = new Message();
				try {
					AppContext ac = (AppContext) getApplication();
					User user = ac.loginVerfy(account, pwd);
					user.setYhdm(account);
					user.setYhmm(pwd);
					user.setRememberMe(isRememberMe);
					Result result = user.getValidate();
					if (result.OK()) {
						// 保存用户登录信息到Properties对象
						ac.saveLoginInfo(user);
						msg.what = 1;// 登录成功
						msg.obj = user;
					} else {
						msg.what = 0;// 登录失败
						// 清除登录信息
						ac.cleanLoginInfo();
						msg.obj = result.getErrorMessage();// 发送登录失败信息给handler
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
