package cn.hneao.notebook.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.hneao.notebook.R;
import cn.hneao.notebook.bean.Student;
import cn.hneao.notebook.bean.StudentInfo;
import cn.hneao.notebook.common.StringUtils;

public class ListViewStudentAdapter extends BaseAdapter {

	private Context context;// 运行上下文
	private List<StudentInfo> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;//自定义视图源

	static class ListItemView {
		public TextView title;
		public TextView date;
		public TextView count;
		public TextView bz;
		public ImageView type;
	}

	public ListViewStudentAdapter(Context context, List<StudentInfo> data,
			int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context);
		this.itemViewResource = resource;
		this.listItems = data;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 自定义的ListItem视图
		ListItemView listItemView=null;
		if (convertView == null) {
			// 获取ListItem布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			listItemView = new ListItemView();
			// 获取控件对象
			
			listItemView.title = (TextView)convertView.findViewById(R.id.student_listitem_title);
			listItemView.date = (TextView)convertView.findViewById(R.id.student_listitem_date);
			listItemView.count = (TextView)convertView.findViewById(R.id.student_listitem_dxfscs);
			listItemView.bz = (TextView)convertView.findViewById(R.id.student_listitem_bz);
			listItemView.type = (ImageView)convertView.findViewById(R.id.student_listitem_kslqzt);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		// 设置文字和图片
		StudentInfo sInfo = listItems.get(position);
		switch (StringUtils.toInt(sInfo.getKszt())) {
		case 1:
			listItemView.type.setImageResource(R.drawable.widget_original_icon);
			break;
		case 2:
			listItemView.type.setImageResource(R.drawable.widget_repaste_icon);
			break;
		default:
			listItemView.type.setImageResource(R.drawable.widget_today_icon);
		}
		listItemView.title.setText(sInfo.getTitle());
		
		listItemView.title.setTag(sInfo.getLsh());//将流水号存储在Tag中
		
		listItemView.date.setText(sInfo.getAddTimeStr());
		listItemView.count.setText(sInfo.getXxfszt().toString());
		listItemView.bz.setText(sInfo.getBz());
		
		//设置选中时背景颜色
		if (sInfo.isChecked()) {
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.blur));
		} else {
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.white));
		}
		return convertView;
	}

}
