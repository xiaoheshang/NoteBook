package cn.hneao.notebook.ui;

import cn.hneao.notebook.AppContext;
import cn.hneao.notebook.AppException;
import cn.hneao.notebook.R;
import cn.hneao.notebook.R.id;
import cn.hneao.notebook.R.layout;
import cn.hneao.notebook.R.menu;
import cn.hneao.notebook.bean.Result;
import cn.hneao.notebook.bean.StudentInfo;
import cn.hneao.notebook.common.UIHelper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Build;

public class AddStudentActivity extends ActionBarActivity {
	/**
	 * 是否超时
	 */
	private boolean IS_OVERTIME=false;
	private AppContext ac;
	private StudentInfo info;
	private LinearLayout mLinearLayout;
	private RelativeLayout mRelativeLayout;
	
	private CheckBox mAddToStar;
	private EditText mKsh;
	private EditText mLxdh;
	private EditText mBz;
	private CheckBox mAutoSendMsg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_student);
		//logo作为返回上层按钮
		ActionBar actionBar=getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		initView();
	}
	
	private void initView(){
		ac=(AppContext)getApplication();
		mLinearLayout=(LinearLayout) findViewById(R.id.add_student_message);
		mRelativeLayout=(RelativeLayout) findViewById(R.id.add_student);
		mAddToStar=(CheckBox)findViewById(R.id.add_to_star);
		mKsh=(EditText)findViewById(R.id.ksh);
		mLxdh=(EditText)findViewById(R.id.lxdh);
		mBz=(EditText)findViewById(R.id.bz);
		mAutoSendMsg=(CheckBox)findViewById(R.id.auto_send_message);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_student, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_save) {
			//保存添加考生信息
			addStudent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * 添加考生
	 */
	@SuppressLint("HandlerLeak")
	private void addStudent(){
		IS_OVERTIME=false;
		if (!ac.isLogin()) {
			UIHelper.locationToLogin(this);
			return;
		}
		mLinearLayout.setVisibility(View.VISIBLE);
		mRelativeLayout.setVisibility(View.GONE);
		info=new StudentInfo();
		String ksh=mKsh.getText().toString();
		String lsh=ksh.substring(0, 4)+ksh.substring(6,ksh.length());
		info.setYhdm(ac.getYhdm());
		info.setLsh(lsh);
		info.setKsh(ksh);
		info.setDxlxdh(mLxdh.getText().toString());
		info.setSfgz(mAddToStar.isChecked()?"1":"0");
		info.setZdfsxx(mAutoSendMsg.isChecked()?"1":"0");
		info.setBz(mBz.getText().toString());
		
		final Handler handler=new Handler(){
			public void handleMessage(Message msg){
				if(msg.what==1){
					//添加成功关闭页面
					finish();
				}else{
					mLinearLayout.setVisibility(View.GONE);
					mRelativeLayout.setVisibility(View.VISIBLE);
					UIHelper.ToastMessage(AddStudentActivity.this, "添加失败", Toast.LENGTH_LONG);
				}
			}
		};
		
		new Thread(){
			//添加考生
			public void run(){
				Message msg=new Message();
				Result rst=null;
				try {
					rst=ac.addStudent(info);
					msg.what=1;
					msg.obj=rst;
					
				} catch (AppException e) {
					e.printStackTrace();
					msg.what=-1;
					msg.obj=e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

}
