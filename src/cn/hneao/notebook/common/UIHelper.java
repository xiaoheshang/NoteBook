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
	 * �������ݵĴ������
	 * ��������Ϊ��ʱ��������
	 */
	public final static int LISTVIEW_ACTION_INIT = 0x01;
	/**
	 * ��������-ִ������ˢ��ʱ��ˢ�£� ǿ��ˢ�£�
	 */
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	/**
	 * ��������-������ListView�ײ�ʱˢ�� ��ǿ��ˢ�£�
	 */
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	/**
	 * ��������-����˶�����ťʱ��ˢ�£�������Ͷ����ť��
	 */
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;
	
	/**
	 * ListView�ؼ�����״̬ ���ɼ��ظ��������
	 * ��״̬�洢��ListView��tag��
	 */
	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	/**
	 * ListView�ؼ�����״̬-�Ѽ���ȫ������
	 */
	public final static int LISTVIEW_DATA_FULL = 0x03;
	/**
	 * ListView�ؼ�����״̬-����Ϊ��
	 */
	public final static int LISTVIEW_DATA_EMPTY = 0x04;
	
	/**
	 * ����Toast��Ϣ
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
	 * ����App�쳣��������
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
						// �����쳣����
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //ģ����
						i.setType("message/rfc822"); // ���
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "741852440@qq.com" });
						i.putExtra(Intent.EXTRA_SUBJECT,
								"¼ȡ���±�- ���󱨸�");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "���ʹ��󱨸�"));
						// �˳�
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// �˳�
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.show();
	}
	/**
	 * ��ʾ�û�������Ϣҳ��
	 * @param context
	 */
	public static void showUserInfo(Activity context){
		AppContext ac=(AppContext)context.getApplicationContext();
		Intent intent=new Intent();
		if(!ac.isLogin()){
			//ת���¼ҳ��
			intent.setClass(context, LoginActivity.class);
		}else{
			//��ʾ������Ϣҳ��
			intent.setClass(context, UserInfoActivity.class);
		}
		context.startActivity(intent);
	}
	/**
	 * ��ת����¼ҳ��
	 * @param context
	 */
	public static void locationToLogin(Activity context){
		Intent intent=new Intent(context,LoginActivity.class);
		context.startActivity(intent);
	}
}
