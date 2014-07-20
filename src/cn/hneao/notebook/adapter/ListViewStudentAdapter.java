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

	private Context context;// ����������
	private List<StudentInfo> listItems;// ���ݼ���
	private LayoutInflater listContainer;// ��ͼ����
	private int itemViewResource;//�Զ�����ͼԴ

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
		// �Զ����ListItem��ͼ
		ListItemView listItemView=null;
		if (convertView == null) {
			// ��ȡListItem�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);
			listItemView = new ListItemView();
			// ��ȡ�ؼ�����
			
			listItemView.title = (TextView)convertView.findViewById(R.id.student_listitem_title);
			listItemView.date = (TextView)convertView.findViewById(R.id.student_listitem_date);
			listItemView.count = (TextView)convertView.findViewById(R.id.student_listitem_dxfscs);
			listItemView.bz = (TextView)convertView.findViewById(R.id.student_listitem_bz);
			listItemView.type = (ImageView)convertView.findViewById(R.id.student_listitem_kslqzt);

			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		// �������ֺ�ͼƬ
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
		
		listItemView.title.setTag(sInfo.getLsh());//����ˮ�Ŵ洢��Tag��
		
		listItemView.date.setText(sInfo.getAddTimeStr());
		listItemView.count.setText(sInfo.getXxfszt().toString());
		listItemView.bz.setText(sInfo.getBz());
		
		//����ѡ��ʱ������ɫ
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
