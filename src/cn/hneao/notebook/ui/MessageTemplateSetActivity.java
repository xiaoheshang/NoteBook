package cn.hneao.notebook.ui;

import cn.hneao.notebook.R;
import cn.hneao.notebook.R.id;
import cn.hneao.notebook.R.layout;
import cn.hneao.notebook.R.menu;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.os.Build;

public class MessageTemplateSetActivity extends ActionBarActivity {

	private EditText et_mbnr;//短信模板内容
	private Button btn_xm;
	private Button btn_yxmc;
	private Button btn_time;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_template_set);
		
		et_mbnr=(EditText) findViewById(R.id.et_mbnr);
		btn_xm=(Button)findViewById(R.id.btn_xm);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message_template_set, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_savetemplate) {
			//保存短信模板
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
