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
	 * 没有找到SD卡
	 */
	private static final int DOWN_NOSDCARD = 0;
	/**
	 * 正在下载
	 */
	private static final int DOWN_UPDATE = 1;
	/**
	 * 下载完成
	 */
	private static final int DOWN_OVER = 2;

	private static final int DIALOG_TYPE_LATEST = 0;
	private static final int DIALOG_TYPE_FAIL = 1;

	private static UpdateManager mUpdateManager;
	private Context mContext;
	// 通知对话框
	private Dialog noticeDialog;
	// 下载对话框
	private Dialog downloadDialog;
	// '已经是最新' 或者 '无法获取最新版本' 的对话框
	private Dialog latestOrFailDialog;
	// 进度条
	private ProgressBar mProgress;
	// 显示下载数值
	private TextView mProgressText;
	// 查询动画
	private ProgressDialog mProDialog;
	// 进度值
	private int progress;
	// 下载线程
	private Thread downLoadThread;
	// 终止标记
	private boolean interceptFlag;
	// 提示语
	private String updateMsg = "";
	// 返回的安装包url
	private String apkUrl = "";
	// 下载包保存路径
	private String savePath = "";
	// apk保存完整路径
	private String apkFilePath = "";
	// 临时下载文件路径
	private String tmpFilePath = "";
	// 下载文件大小
	private String apkFileSize;
	// 已下载文件大小
	private String tmpFileSize;

	private String curVersionName = "";
	private int curVersionCode;
	private Update mUpdate;

	/**
	 * 返回一个UpdateManager对象
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
	 * 监听下载进度显示进度条，下载完后安装
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				Toast.makeText(mContext, "无法下载，请检查SDCard是否挂载", 3000).show();
				break;
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				mProgressText.setText(tmpFileSize + "/" + apkFileSize);
				break;
			case DOWN_OVER:
				// 下载完成
				downloadDialog.dismiss();
				// 安装apk
				installApk();
			}
		}
	};

	/**
	 * 获取安装包版本信息
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
	 * 显示“没有更新”或者“检测更新失败”对话框
	 */
	private void showLatestOrFailDialog(int dialogType) {
		if (latestOrFailDialog != null) {
			latestOrFailDialog.dismiss();
			latestOrFailDialog = null;
		}
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("系统提示");
		if (dialogType == DIALOG_TYPE_LATEST) {
			// 如果是没有更新
			builder.setMessage("当前版本为最新版本");
		} else if (dialogType == DIALOG_TYPE_FAIL) {
			builder.setMessage("检测更新出错");
		}
		builder.setPositiveButton("确定", null);
		latestOrFailDialog = builder.create();
		latestOrFailDialog.show();
	}

	/**
	 * 显示下载进度对话框
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("下载更新");
		// 自定义对话框
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar) view.findViewById(R.id.update_progress);
		mProgressText = (TextView) view.findViewById(R.id.update_progress_text);
		builder.setView(view);
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		// 绑定取消下载事件
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.setCanceledOnTouchOutside(false);// 点击非对话框区域不取消对话框
		downloadDialog.show();
		// 下载更新包 apk
		downloadApk();
	}

	/**
	 * 显示更新信息对话框
	 */
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件更新");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载更新
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		// 显示更新信息对话框
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 检测更新
	 * 
	 * @param context
	 * @param isShowMsg
	 */
	public void checkAppUpdate(Context context, final boolean isShowMsg) {
		this.mContext = context;
		getCurrentVersion();
		if (isShowMsg) {
			// 显示“正在检查...”信息
			if (mProDialog == null) {
				mProDialog = ProgressDialog.show(mContext, "提示", "正在检测更新...");
			} else if (mProDialog.isShowing()
					|| (latestOrFailDialog != null && latestOrFailDialog
							.isShowing())) {
				return;
			}
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				// 因为点击检测更新时（如果进行了检测）必然会显示“正在检测..”对话框
				// 这里判断初始化了“正在检测..”对话框，但该对话框未显示则（证明不是在检测更新）直接返回。
				if (mProDialog != null && !mProDialog.isShowing()) {
					return;
				}
				// 关闭对话框并释放资源
				if (isShowMsg && mProDialog != null) {
					mProDialog.dismiss();
					mProDialog = null;
				}
				if (msg.what == 1) {
					mUpdate = (Update) msg.obj;
					if (mUpdate != null) {
						if (curVersionCode < mUpdate.getVersionCode()) {
							// 提示并显示更新信息
							apkUrl = mUpdate.getDownloadUrl();
							updateMsg = mUpdate.getUpdateLog();
							showNoticeDialog();
						} else {
							// 显示没有更新对话框
							showLatestOrFailDialog(DIALOG_TYPE_LATEST);
						}
					}
				} else if (isShowMsg) {
					// 显示检测更新失败对话框
					showLatestOrFailDialog(DIALOG_TYPE_FAIL);
				}
			}
		};
		new Thread() {
			// 检测是否有更新
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
	 * 下载更新包执行体
	 */
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				// 执行下载操作
				String apkName = "NoteBook_" + mUpdate.getVersionName()
						+ ".apk";
				String tmpName = "NoteBook_" + mUpdate.getVersionName()
						+ ".tmp";
				// 判断是否挂载sdcard
				String storageState = Environment.getExternalStorageState();
				if (storageState.equals(Environment.MEDIA_MOUNTED)) {
					// sdcard已挂载
					savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/NoteBook/Update/";
					File file = new File(savePath);
					if (!file.exists()) {
						file.mkdirs();//这里不能使用mkdir()
					}
					apkFilePath = savePath + apkName;
					tmpFilePath = savePath + tmpName;
				}
				// 没有挂载SD卡，无法下载文件
				if (apkFilePath == null || apkFilePath == "") {
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}

				File apkFile = new File(apkFilePath);
				// 如果已经存在apk文件 直接安装
				if (apkFile.exists()) {
					downloadDialog.dismiss();
					installApk();
					return;
				}
				// 下载apk文件
				File tmpFile = new File(tmpFilePath);
				FileOutputStream ops = new FileOutputStream(tmpFile);
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();// 连接服务端
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				// 数字显示格式
				DecimalFormat df = new DecimalFormat("0.00");
				// 进度条下面显示的总文件大小
				apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read();
					count += numread;
					tmpFileSize = df.format((float) count / 1024 / 1024);
					// 当前进度值
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成 - 将临时下载文件转成APK文件
						if (tmpFile.renameTo(apkFile)) {
							// 通知安装
							mHandler.sendEmptyMessage(DOWN_OVER);
						}
						break;
					}
					ops.write(buf, 0, numread);
				} while (!interceptFlag);// 除非用户点击取消按钮

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
	 * 下载apk
	 */
	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
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
