package cn.hneao.notebook.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.acl.LastOwnerException;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.jar.Pack200.Packer;

import cn.hneao.notebook.AppContext;
import cn.hneao.notebook.AppException;
import cn.hneao.notebook.R;
import cn.hneao.notebook.common.UpdateManager;
import cn.hneao.notebook.api.ApiClient;
import cn.hneao.notebook.bean.Update;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateManager {
	/**
	 * û���ҵ�SD��
	 */
	private static final int DOWN_NOSDCARD = 0;
	/**
	 * ��������
	 */
	private static final int DOWN_UPDATE = 1;
	/**
	 * �������
	 */
	private static final int DOWN_OVER = 2;

	private static final int DIALOG_TYPE_LATEST = 0;
	private static final int DIALOG_TYPE_FAIL = 1;

	private static UpdateManager mUpdateManager;
	private Context mContext;
	// ֪ͨ�Ի���
	private Dialog noticeDialog;
	// ���ضԻ���
	private Dialog downloadDialog;
	// '�Ѿ�������' ���� '�޷���ȡ���°汾' �ĶԻ���
	private Dialog latestOrFailDialog;
	// ������
	private ProgressBar mProgress;
	// ��ʾ������ֵ
	private TextView mProgressText;
	// ��ѯ����
	private ProgressDialog mProDialog;
	// ����ֵ
	private int progress;
	// �����߳�
	private Thread downLoadThread;
	// ��ֹ���
	private boolean interceptFlag;
	// ��ʾ��
	private String updateMsg = "";
	// ���صİ�װ��url
	private String apkUrl = "";
	// ���ذ�����·��
	private String savePath = "";
	// apk��������·��
	private String apkFilePath = "";
	// ��ʱ�����ļ�·��
	private String tmpFilePath = "";
	// �����ļ���С
	private String apkFileSize;
	// �������ļ���С
	private String tmpFileSize;

	private String curVersionName = "";
	private int curVersionCode;
	private Update mUpdate;

	/**
	 * ����һ��UpdateManager����
	 * 
	 * @return
	 */
	public static UpdateManager getUpdateManager() {
		if (mUpdateManager == null) {
			mUpdateManager = new UpdateManager();
		}
		mUpdateManager.interceptFlag = false;
		return mUpdateManager;
	}

	/**
	 * �������ؽ�����ʾ���������������װ
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				Toast.makeText(mContext, "�޷����أ�����SDCard�Ƿ����", 3000).show();
				break;
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				mProgressText.setText(tmpFileSize + "/" + apkFileSize);
				break;
			case DOWN_OVER:
				// �������
				downloadDialog.dismiss();
				// ��װapk
				installApk();
			}
		}
	};

	/**
	 * ��ȡ��װ���汾��Ϣ
	 */
	private void getCurrentVersion() {
		try {
			PackageInfo info = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			curVersionCode = info.versionCode;
			curVersionName = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * ��ʾ��û�и��¡����ߡ�������ʧ�ܡ��Ի���
	 */
	private void showLatestOrFailDialog(int dialogType) {
		if (latestOrFailDialog != null) {
			latestOrFailDialog.dismiss();
			latestOrFailDialog = null;
		}
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("ϵͳ��ʾ");
		if (dialogType == DIALOG_TYPE_LATEST) {
			// �����û�и���
			builder.setMessage("��ǰ�汾Ϊ���°汾");
		} else if (dialogType == DIALOG_TYPE_FAIL) {
			builder.setMessage("�����³���");
		}
		builder.setPositiveButton("ȷ��", null);
		latestOrFailDialog = builder.create();
		latestOrFailDialog.show();
	}

	/**
	 * ��ʾ���ؽ��ȶԻ���
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("���ظ���");
		// �Զ���Ի���
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar) view.findViewById(R.id.update_progress);
		mProgressText = (TextView) view.findViewById(R.id.update_progress_text);
		builder.setView(view);
		builder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		// ��ȡ�������¼�
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.setCanceledOnTouchOutside(false);// ����ǶԻ�������ȡ���Ի���
		downloadDialog.show();
		// ���ظ��°� apk
		downloadApk();
	}

	/**
	 * ��ʾ������Ϣ�Ի���
	 */
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("�������");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("��������", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ���ظ���
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("�Ժ���˵", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		// ��ʾ������Ϣ�Ի���
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * ������
	 * 
	 * @param context
	 * @param isShowMsg
	 */
	public void checkAppUpdate(Context context, final boolean isShowMsg) {
		this.mContext = context;
		getCurrentVersion();
		if (isShowMsg) {
			// ��ʾ�����ڼ��...����Ϣ
			if (mProDialog == null) {
				mProDialog = ProgressDialog.show(mContext, "��ʾ", "���ڼ�����...");
			} else if (mProDialog.isShowing()
					|| (latestOrFailDialog != null && latestOrFailDialog
							.isShowing())) {
				return;
			}
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				// ��Ϊ���������ʱ����������˼�⣩��Ȼ����ʾ�����ڼ��..���Ի���
				// �����жϳ�ʼ���ˡ����ڼ��..���Ի��򣬵��öԻ���δ��ʾ��֤�������ڼ����£�ֱ�ӷ��ء�
				if (mProDialog != null && !mProDialog.isShowing()) {
					return;
				}
				// �رնԻ����ͷ���Դ
				if (isShowMsg && mProDialog != null) {
					mProDialog.dismiss();
					mProDialog = null;
				}
				if (msg.what == 1) {
					mUpdate = (Update) msg.obj;
					if (mUpdate != null) {
						if (curVersionCode < mUpdate.getVersionCode()) {
							// ��ʾ����ʾ������Ϣ
							apkUrl = mUpdate.getDownloadUrl();
							updateMsg = mUpdate.getUpdateLog();
							showNoticeDialog();
						} else {
							// ��ʾû�и��¶Ի���
							showLatestOrFailDialog(DIALOG_TYPE_LATEST);
						}
					}
				} else if (isShowMsg) {
					// ��ʾ������ʧ�ܶԻ���
					showLatestOrFailDialog(DIALOG_TYPE_FAIL);
				}
			}
		};
		new Thread() {
			// ����Ƿ��и���
			public void run() {
				Message message = new Message();
				try {
					Update update = ApiClient
							.checkVersion((AppContext) mContext
									.getApplicationContext());
					message.what = 1;
					message.obj = update;
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * ���ظ��°�ִ����
	 */
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				// ִ�����ز���
				String apkName = "NoteBook_" + mUpdate.getVersionName()
						+ ".apk";
				String tmpName = "NoteBook_" + mUpdate.getVersionName()
						+ ".tmp";
				// �ж��Ƿ����sdcard
				String storageState = Environment.getExternalStorageState();
				if (storageState.equals(Environment.MEDIA_MOUNTED)) {
					// sdcard�ѹ���
					savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/NoteBook/Update/";
					File file = new File(savePath);
					if (!file.exists()) {
						file.mkdirs();//���ﲻ��ʹ��mkdir()
					}
					apkFilePath = savePath + apkName;
					tmpFilePath = savePath + tmpName;
				}
				// û�й���SD�����޷������ļ�
				if (apkFilePath == null || apkFilePath == "") {
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}

				File apkFile = new File(apkFilePath);
				// ����Ѿ�����apk�ļ� ֱ�Ӱ�װ
				if (apkFile.exists()) {
					downloadDialog.dismiss();
					installApk();
					return;
				}
				// ����apk�ļ�
				File tmpFile = new File(tmpFilePath);
				FileOutputStream ops = new FileOutputStream(tmpFile);
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();// ���ӷ����
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				// ������ʾ��ʽ
				DecimalFormat df = new DecimalFormat("0.00");
				// ������������ʾ�����ļ���С
				apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read();
					count += numread;
					tmpFileSize = df.format((float) count / 1024 / 1024);
					// ��ǰ����ֵ
					progress = (int) (((float) count / length) * 100);
					// ���½���
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// ������� - ����ʱ�����ļ�ת��APK�ļ�
						if (tmpFile.renameTo(apkFile)) {
							// ֪ͨ��װ
							mHandler.sendEmptyMessage(DOWN_OVER);
						}
						break;
					}
					ops.write(buf, 0, numread);
				} while (!interceptFlag);// �����û����ȡ����ť

				ops.close();
				is.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**
	 * ����apk
	 */
	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * ��װapk
	 */
	private void installApk() {
		File apkfile = new File(apkFilePath);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}

}
