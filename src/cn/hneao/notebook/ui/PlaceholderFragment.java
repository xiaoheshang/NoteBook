package cn.hneao.notebook.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hneao.notebook.adapter.ListViewStudentAdapter;
import cn.hneao.notebook.AppContext;
import cn.hneao.notebook.AppException;
import cn.hneao.notebook.R;
import cn.hneao.notebook.bean.Notice;
import cn.hneao.notebook.bean.Student;
import cn.hneao.notebook.bean.StudentInfo;
import cn.hneao.notebook.bean.StudentInfoList;
import cn.hneao.notebook.common.StringUtils;
import cn.hneao.notebook.common.UIHelper;
import cn.hneao.notebook.widget.PullToRefreshListView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;

public class PlaceholderFragment extends Fragment {

	private final static String LSH="lsh";
	private final static String TAG = "PlaceholderFragment";
	private AppContext appContext;
	/**
	 * ��ǰ����Ŀ 1-δͶ�� 2-��Ͷ�� 3-�ѹ�ע
	 */
	private int currInofcatalog = StudentInfoList.CATALOG_WTD;
	private List<StudentInfo> infoList = new ArrayList<StudentInfo>();
	private List<StudentInfo> checkedList = new ArrayList<StudentInfo>();
	private PullToRefreshListView mListView;
	private ListViewStudentAdapter mAdapter;

	private View listView_footer;// ListView �ײ���ͼ
	private TextView listView_foot_more;
	private ProgressBar listView_foot_progress;
	/**
	 * ��ǰListView�е����ݼ�¼��
	 */
	private int listViewSumData;

	private Handler mListViewHandler;
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 * sectionNumber 1-δͶ�� 2-��Ͷ�� 3-�ѹ�ע
	 */
	public static PlaceholderFragment newInstance(int sectionNumber) {
		Log.i(TAG, "---->����Fragment");
		PlaceholderFragment fragment = new PlaceholderFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public PlaceholderFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		appContext = (AppContext) getActivity().getApplication();
		View rootView = null;
		int page = getArguments().getInt(ARG_SECTION_NUMBER);

		Log.i(TAG, "---page-->" + page);
		// ���õ�ǰҳ��
		currInofcatalog = page;

		rootView = inflater.inflate(R.layout.fragment_main, container, false);
		mListView = (PullToRefreshListView) rootView.findViewById(R.id.list);
		// ��ʼ��ListView�ؼ�
		initListView();
		// ��ʼ������
		initFrameListViewData();

		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.context_menu_list, menu);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_delete:
					infoList.removeAll(checkedList);
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
					Toast.makeText(getActivity(), R.string.menu_delete,
							Toast.LENGTH_SHORT).show();
					checkedList.clear();
					mode.finish();
					return true;
				case R.id.menu_send_message:
					Toast.makeText(getActivity(), R.string.menu_send_message,
							Toast.LENGTH_SHORT).show();
					mode.finish();
					return true;
				default:
					return false;
				}
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				StudentInfo book = infoList.get(position - 1);
				if (checked) {
					book.setChecked(true);
					checkedList.add(book);
				} else {
					checkedList.remove(book);
					book.setChecked(false);
				}
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				// ���¼�¼������ʾ
				setSubtitle(mode);
			}

			private void setSubtitle(ActionMode mode) {
				int checkedCount = checkedList.size();
				switch (checkedCount) {
				case 0:
					mode.setSubtitle(null);
					break;
				default:
					mode.setSubtitle("��" + checkedCount);
					break;
				}
			}
		});

		// TextView textView = (TextView) rootView
		// .findViewById(R.id.textView1);
		// textView.setText(Integer.toString(getArguments().getInt(
		// ARG_SECTION_NUMBER)));
		return rootView;
	}

	/**
	 * ��ʼ��ListView�ؼ� ��ListItem����¼��� ��ListView�����¼�-�������ײ�������һҳ����
	 */
	private void initListView() {
		mAdapter = new ListViewStudentAdapter(getActivity(), infoList,
				R.layout.student_listitem);
		// listView�ײ���ͼ
		listView_footer = getActivity().getLayoutInflater().inflate(
				R.layout.listview_footer, null);
		listView_foot_more = (TextView) listView_footer
				.findViewById(R.id.listview_foot_more);
		listView_foot_progress = (ProgressBar) listView_footer
				.findViewById(R.id.listview_foot_progress);

		// ��ʼ��ʾΪ�Ѽ���ȫ��
		listView_foot_more.setText(R.string.load_full);
		listView_foot_progress.setVisibility(View.GONE);

		// ��ӵײ���ͼ��ListView
		mListView.addFooterView(listView_footer);
		// ΪListview��������
		mListView.setAdapter(mAdapter);
		// ����¼�-��ʾ��ϸ����ҳ
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || view == listView_footer) {
					return;
				}
				String lsh = "";
				if (view instanceof TextView) {
					lsh = view.getTag().toString();
				} else {
					TextView tv = (TextView) view
							.findViewById(R.id.student_listitem_title);
					lsh = tv.getTag().toString();
				}
				if(lsh.equals("")){
					return;
				}
				// ת����ϸ����ҳ��.....
				Intent intent=new Intent(getActivity(),StudentDetailActivity.class);
				intent.putExtra(LSH, lsh);
				startActivity(intent);
			}
		});
		// �󶨹����������¼�
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.i(TAG, "����Scroll�����¼�");
				mListView.onScrollStateChanged(view, scrollState);
				if (infoList.isEmpty()) {
					return;
				}
				// �ж��Ƿ�������ײ�
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(listView_footer) == view
							.getLastVisiblePosition()) {
						scrollEnd = true;
					}
				} catch (Exception e) {
					scrollEnd = false;
				}
				// �˴�ListView�ؼ�tag�д洢��ֵ��Handler������
				int lvDataState = StringUtils.toInt(mListView.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					// �����������ListView�ؼ��ĵײ�����ListView�ؼ����ݵ�״̬Ϊ����δ���ص�����
					// �������һҳ����
					// �޸�ListView�ؼ�״̬Ϊ���ڼ�������
					mListView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					// ListView�ײ���ͼ��ʾ���ڼ���ͼƬ�Լ�����
					listView_foot_more.setText(R.string.load_ing);
					listView_foot_progress.setVisibility(View.VISIBLE);
					// �����ص����ݵ�ҳ��Ϊ��ǰ�Ѽ��ص����ݼ�¼����/ÿҳ��ʾ�ļ�¼��
					// ����ҳ���Ǵ�0��ʼ��
					int pageIndex = listViewSumData / AppContext.PAGE_SIZE;
					// ��������
					loadListViewData(currInofcatalog, pageIndex,
							mListViewHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mListView.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
		// ʵ������ˢ���е�OnRefreshListener�ӿ��е�onRefresh������
		// �������ط�����onRefresh�����Ǿ�ִ�иò��ִ���
		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						// ������ص�0��1��ҳ����
						Log.i(TAG, "����ˢ��");
						loadListViewData(currInofcatalog, 0, mListViewHandler,
								UIHelper.LISTVIEW_ACTION_REFRESH);
					}
				});
	}

	/**
	 * ��ʼ��Handler ΪListView�󶨳�ʼ���ݲ���ʾ Ĭ����ʾδͶ��������Ϣ
	 */
	private void initFrameListViewData() {
		mListViewHandler = getHandler(mListView, mAdapter, listView_foot_more,
				listView_foot_progress, AppContext.PAGE_SIZE);
		if (infoList.isEmpty()) {
			// ����δͶ����������
			loadListViewData(currInofcatalog, 0, mListViewHandler,
					UIHelper.LISTVIEW_ACTION_INIT);
		}
	}

	/**
	 * Handler����
	 * 
	 * @param what
	 * @param obj
	 * @param objtype
	 * @param actiontype
	 * @return
	 */
	private Notice handleLvData(int what, Object obj, int objtype,
			int actiontype) {
		Notice notice = null;
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newData = 0;
			StudentInfoList nList = (StudentInfoList) obj;
			notice = nList.getNotice();
			listViewSumData = what;
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
				if (infoList.size() > 0) {
					for (StudentInfo info1 : nList.getStudentInfoList()) {
						boolean b = false;
						for (StudentInfo info2 : infoList) {
							// ��ˮ����ͬ\����״̬��ͬ\����ʱ����ͬ\�Ų�����������
							if (info1.getLsh() == info2.getLsh()
									&& info1.getKszt() == info2.getKszt()
									&& info1.getAddTime() == info2.getAddTime()) {
								b = true;
								break;
							}
						}
						if (!b) {
							newData++;
						}
					}
					Log.i(TAG, "---newData-->" + newData);
				} else {
					newData = what;
				}
			}
			// �����ݴ洢��List������
			infoList.clear();
			infoList.addAll(nList.getStudentInfoList());
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			StudentInfoList list = (StudentInfoList) obj;
			notice = list.getNotice();
			listViewSumData += what;// �������ײ�������һҳ����ListView�ؼ����ܵļ�¼��������
			if (infoList.size() > 0) {
				for (StudentInfo info1 : list.getStudentInfoList()) {
					boolean b = false;
					for (StudentInfo info2 : infoList) {
						// ��ˮ����ͬ\����״̬��ͬ\����ʱ����ͬ\�Ų�����������
						if (info1.getLsh() == info2.getLsh()
								&& info1.getKszt() == info2.getKszt()
								&& info1.getAddTime() == info2.getAddTime()) {
							b = true;
							break;
						}
					}
					if (!b) {
						infoList.add(info1);
					}
				}
			} else {
				infoList.addAll(list.getStudentInfoList());
			}
			break;
		}
		return notice;
	}

	/**
	 * ��ȡHandler
	 * 
	 * @param lv
	 * @param adapter
	 * @param more
	 * @param progress
	 * @param pageSize
	 * @return
	 */
	private Handler getHandler(final PullToRefreshListView lv,
			final BaseAdapter adapter, final TextView more,
			final ProgressBar progress, final int pageSize) {
		return new Handler() {
			public void handleMessage(Message msg) {
				Log.i(TAG, "---msg.what--->" + msg.what);
				if (msg.what >= 0) {
					Notice notice = handleLvData(msg.what, msg.obj, msg.arg2,
							msg.arg1);
					if (msg.what < pageSize) {
						// �Ѽ���ȫ������
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					} else if (msg.what == pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
					} else if (msg.what == -1) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						more.setText(R.string.load_error);
						// ��ʾ����
						((AppException) msg.obj).makeToast(getActivity());

					}
					if (adapter.getCount() == 0) {
						lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
						more.setText(R.string.load_empty);
					}
					// ����progressBar�ؼ�
					progress.setVisibility(View.GONE);
					if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
						// ����ˢ�£�ˢ�º���������ˢ��ʱ��ʾ��ProgressBar�Ѿ���ʾ����
						lv.onRefreshComplete(getActivity().getString(
								R.string.pull_to_refresh_update)
								+ new Date().toLocaleString());
						lv.setSelection(0);
					} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
						lv.onRefreshComplete();
						lv.setSelection(0);
					}
				}
			}
		};
	}

	/**
	 * ��������
	 * 
	 * @param catalog
	 * @param pageIndex
	 * @param handler
	 * @param action
	 */
	private void loadListViewData(final int catalog, final int pageIndex,
			final Handler handler, final int action) {
		new Thread() {
			public void run() {
				Log.i(TAG, "���̼߳�������");

				Message msg = new Message();

				// Log.i(TAG, "--catalog-->" + catalog);
				// Log.i(TAG, "--pageIndex-->" + pageIndex);
				// Log.i(TAG, "--action-->" + action);

				boolean isRefresh = false;
				// boolean isRefresh = true;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL) {
					isRefresh = true;
				}
				try {
					Log.i(TAG, "--isRefresh--->" + String.valueOf(isRefresh));

					StudentInfoList slist = appContext.getStudentInfoList(
							catalog, pageIndex, isRefresh);
					msg.what = slist.getPageSize();// ���صļ�¼��
					msg.obj = slist;

				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = 1;// ��ʾ������Ϣ����δ�õ�

				Log.i(TAG, "---currInofcatalog--->" + currInofcatalog);
				Log.i(TAG, "---catalog--->" + catalog);

				if (currInofcatalog == catalog) {

					handler.sendMessage(msg);
				}
			}
		}.start();
	}
}
