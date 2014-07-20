package cn.hneao.notebook.widget;

import cn.hneao.notebook.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class loadingDialog extends Dialog {
	private Context mContext;
	private LayoutInflater inflater;
	private LayoutParams lp;
	private TextView loadtext;

	public loadingDialog(Context context) {
		super(context, R.style.Dialog);

		this.mContext = context;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout=inflater.inflate(R.layout.loadingdialog, null);
		loadtext=(TextView)layout.findViewById(R.id.loading_text);
		setContentView(layout);
		
		// …Ë÷√window Ù–‘
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.dimAmount = 0; // »•±≥æ∞’⁄∏«
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);

	}
	public void setLoadText(String content){
		loadtext.setText(content);
	}
}
