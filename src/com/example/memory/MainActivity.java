package com.example.memory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint.Join;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.webkit.WebView.FindListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks{
	ActionBarDrawerToggle drawerToggle;

	public final static int JOB_NUMBER=5;
	public final static int RAM_MOMERY=7;
	public final static int NF=1;//调度算法
	public final static int BF=2;
	
	private static int scheduleAlg=-1;
	private static Button btnJob1;
	private static boolean isProgramRun=false;
	private static Button btnJob2;
	private static Button btnJob3;
	private static Button btnJob4;
	private static Button btnJob5;
	private static int jobNum=0;
	private static int curLocation=0;//当前指针
	
	private static ArrayList<String> printStrings;
	private static LinkedList<MemBlock> listBlocks;
	private static LinkedList<JCB> listJCB;
	private static LinkedList<JCB> finishedJCB;
	
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		printStrings=new ArrayList<String>();
		
	}

	

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int position) {
		this.scheduleAlg=position;
		switch (position) {
		case NF:
			mTitle = getString(R.string.title_section1);
			alert( this,"您所选择的是首次适应算法");
			break;
		case BF:
			mTitle = getString(R.string.title_section2);
			alert(this,"您所选择的是最佳适应算法");
			break;
		default:
			break;
		}
		isProgramRun=false;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (item.getItemId() == R.id.action_run&&!isProgramRun) {
			if(scheduleAlg==-1){
				Toast.makeText(this, "未选择调度算法！请选择！", Toast.LENGTH_LONG)
						.show();
			}
			else{
				alert(this,"CPU开始运转……");
				createQueue();
				isProgramRun=true;
				return true;
			}
			
		}
		else if (item.getItemId() == R.id.action_stop&&isProgramRun) {
			alert(this,"停止调度……");
			isProgramRun=false;
			Button[] buttons={btnJob1,btnJob2,btnJob3,btnJob4,btnJob5};
			for(int i=0;i<buttons.length;i++){
				buttons[i].setBackgroundColor(getResources().getColor(R.color.gray));
				if(i>0){
					buttons[i].setClickable(true);
				}
			}
			
			return true;
		}

		else if (item.getItemId() == R.id.action_print) {
			Intent intent=new Intent();
			intent.setClass(this, PrintActivity.class);
			intent.putStringArrayListExtra("print", printStrings);
			startActivity(intent);
			return true;
		}
		else if(item.getItemId() == R.id.action_clean){
			alert(this,"清除打印记录！");
			printStrings.clear();
		}
		return super.onOptionsItemSelected(item);
	}

	private void createQueue() {
		
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener{
		
		private ListView lvJobInfo;
		private ListView lvMemory;
		private JobAdapter jobAdapter;
		private MemoryAdapter memoryAdapter;

		private Button btnShowLv;
		
		private JCB job1;
		private JCB job2;
		private JCB job3;
		private JCB job4;
		private JCB job5;
		


		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			return rootView;
		}
		
		@Override
		public void onStart() {
			super.onStart();
			initData();
			getJCB();
			getMemBlock();
			jobAdapter=new JobAdapter();
			lvJobInfo.setAdapter(jobAdapter);
			memoryAdapter=new MemoryAdapter();
			lvMemory.setAdapter(memoryAdapter);
			
		}

		private void getMemBlock() {
			int i=0;
			MemBlock block1=new MemBlock(1, 0, 30);
			MemBlock block2=new MemBlock(block1.parNum+1, block1.addr+block1.size, 20);
			MemBlock block3=new MemBlock(block2.parNum+1, block2.addr+block2.size, 50);
			MemBlock block4=new MemBlock(block3.parNum+1, block3.addr+block3.size, 80);
			MemBlock block5=new MemBlock(block4.parNum+1, block4.addr+block4.size, 100);
			MemBlock[] blocks={block1,block2,block3,block4,block5};
			listBlocks=new LinkedList<MemBlock>();
			for(i=0;i<blocks.length;i++){
				listBlocks.offer(blocks[i]);
			}
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
		private void initData() {
			btnJob1=(Button) getActivity().findViewById(R.id.btnJob1);
			btnJob2=(Button) getActivity().findViewById(R.id.btnJob2);
			btnJob3=(Button) getActivity().findViewById(R.id.btnJob3);
			btnJob4=(Button) getActivity().findViewById(R.id.btnJob4);
			btnJob5=(Button) getActivity().findViewById(R.id.btnJob5);
			btnShowLv=(Button) getActivity().findViewById(R.id.btnShowLv);
			lvJobInfo=(ListView) getActivity().findViewById(R.id.lvJob);
			lvMemory=(ListView) getActivity().findViewById(R.id.lvMemory);
			
			btnJob1.setOnClickListener(this);
			btnJob2.setOnClickListener(this);
			btnJob3.setOnClickListener(this);
			btnJob4.setOnClickListener(this);
			btnJob5.setOnClickListener(this);
			btnShowLv.setOnClickListener(this);
			
		}

		private void getJCB() {
			job1=new JCB("A",20,btnJob1);
			job2=new JCB("B",28,btnJob2);
			job3=new JCB("C",75,btnJob3);
			job4=new JCB("D",58,btnJob4);
			job5=new JCB("E",120,btnJob5);
			JCB[] jobs={job1,job2,job3,job4,job5};
			btnJob1.setText("A\n("+job1.getMemory()+"kB)");
			btnJob2.setText("B\n("+job2.getMemory()+"kB)");
			btnJob3.setText("C\n("+job3.getMemory()+"kB)");
			btnJob4.setText("D\n("+job4.getMemory()+"kB)");
			btnJob5.setText("E\n("+job5.getMemory()+"kB)");
			listJCB=new LinkedList<JCB>();
			finishedJCB=new LinkedList<JCB>();
			for(int i=0;i<jobs.length;i++){
				listJCB.offer(jobs[i]);
			}
		}

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btnShowLv){
				if(lvJobInfo.getVisibility()==View.GONE){
					lvJobInfo.setVisibility(View.VISIBLE);
					btnShowLv.setText("隐藏结果");
				}else{
					lvJobInfo.setVisibility(View.GONE);
					btnShowLv.setText("显示完成作业");
				}
				return;
			}
			if(!isProgramRun){
				Toast.makeText(getActivity(), "程序未运行！请点击RUN", Toast.LENGTH_LONG).show();
				return;
			}
			switch (v.getId()) {
			case R.id.btnJob1:
			{
				handleBtnClick(job1);
				break;
			}
			case R.id.btnJob2:
			{
				handleBtnClick(job2);
				break;
			}
			case R.id.btnJob3:
			{
				handleBtnClick(job3);
				break;
			}
			case R.id.btnJob4:
			{
				handleBtnClick(job4);
				break;
			}
			case R.id.btnJob5:
			{
				handleBtnClick(job5);
				break;
			}
			default:
				break;
			}
		}
		

		
		public void handleBtnClick(JCB jcb) {
			jcb.btn.setBackgroundColor(getResources().getColor(R.color.yellow));
			jcb.btn.setClickable(false);
			schedule(jcb);
			
		}
		public void schedule(JCB jcb) {
			if(isProgramRun){
				if(scheduleAlg==NF){//循环首次适应算法
					for(int location=curLocation;;){   //指向上次遍历的低点
						if(jcb.getMemory()<=listBlocks.get(location).size
								&&listBlocks.get(location).isFree){
							MemBlock block=listBlocks.get(location);
							jcb.setAddress(block.addr);
							jcb.btn.setBackgroundColor(getActivity()
									.getResources().getColor(R.color.blue));
							block.setJcb(jcb);
							alert(getActivity(), "作业"+jcb.getName()+
									"被分到"+block.parNum+"分区!");
							block.isFree=false;
							if(block.minSizeLeft<=block.size-jcb.getMemory()){//如果剩余块足够打，分割
								MemBlock newBlock=new MemBlock(listBlocks.size()+1, 
										block.addr+jcb.getMemory(), block.size-jcb.getMemory());
								alert(getActivity(), "新建分区"+newBlock.parNum+"!");
								block.size-=newBlock.size;
								listBlocks.add(location+1, newBlock);
								
							}
							curLocation=location;
							break;
						}
						location=(location+1)%listBlocks.size();//模运算防止溢出
						if(location==curLocation){//遍历完一遍链表
							alertMemoryNotEnough(jcb);
							break;
						}
					}
				}else if(scheduleAlg==BF){//最佳适应算法
					int minSize;
					int min=0;
					for(int location=0;location<listBlocks.size();location++){
						if(listBlocks.get(location).size>=jcb.getMemory()){
							min=location;
							break;
						}
					}
					if(min==listBlocks.size()){//如果遍历完一遍还没找到足够大的块
						alertMemoryNotEnough(jcb);
					}else{
						for(int location=min;location<listBlocks.size();location++){
							if(listBlocks.get(location).size>=jcb.getMemory()&&listBlocks.get(location).isFree){
								if(listBlocks.get(location).size<listBlocks.get(min).size)
									min=location;
							}
						}
						MemBlock block=listBlocks.get(min);
						jcb.setAddress(block.addr);
						jcb.btn.setBackgroundColor(getActivity()
								.getResources().getColor(R.color.blue));
						block.setJcb(jcb);
						alert(getActivity(), "作业"+jcb.getName()+
								"被分到"+block.parNum+"分区!");
						block.isFree=false;
						if(block.minSizeLeft<=block.size-jcb.getMemory()){
							MemBlock newBlock=new MemBlock(listBlocks.size()+1, 
									block.addr+jcb.getMemory(), block.size-jcb.getMemory());
							alert(getActivity(), "新建分区"+newBlock.parNum+"!");
							block.size-=newBlock.size;
							listBlocks.add(min+1, newBlock);
						}
					}
				}
				memoryAdapter.notifyDataSetChanged();
				jobAdapter.notifyDataSetChanged();
			}
			
		}
		
		
		public void alertMemoryNotEnough(JCB jcb){
			alert(getActivity(),"没有找到足够大的分区块！");
			jcb.btn.setBackgroundColor(getActivity().getResources().getColor(R.color.gray));
			jcb.btn.setClickable(true);
		}
		
		private void recycle(MemBlock block) {
			int location=0;
			for(location=0;location<listBlocks.size();location++){
				if(block==listBlocks.get(location)){
					break;
				}
			}
			curLocation=location;
			listBlocks.get(location).getJcb().btn
				.setBackgroundColor(getActivity()
						.getResources().getColor(R.color.gray));
			listBlocks.get(location).getJcb().btn.setClickable(true);
			alert(getActivity(), "已回收内存分区"+block.parNum);
			if(location==0){//如果是第一个分区
				MemBlock curBlock=listBlocks.get(location);
				if(location<listBlocks.size()-1
						&&listBlocks.get(location+1).isFree){//后一个分区可用，合并
					curBlock.isFree=true;
					finishedJCB.offer(curBlock.getJcb());
					
					curBlock.setJcb(null);
					curBlock.size+=listBlocks.get(location+1).size;
					listBlocks.remove(location+1);
				}else{				//如果后一个分区不可用,直接回收第一个
					curBlock.isFree=true;
					finishedJCB.offer(curBlock.getJcb());
					curBlock.setJcb(null);
				}
			}else{//如果不是第一个分区
				if(listBlocks.get(location-1).isFree){//如果前一个分区可用，合并
					MemBlock curBlock=listBlocks.get(location-1);
					finishedJCB.offer(listBlocks.get(location).getJcb());
					curBlock.size+=listBlocks.get(location).size;
					listBlocks.remove(location);
					if(location<listBlocks.size()-1){//如果不是最后一个分区
						if(listBlocks.get(location+1).isFree){//如果后一个分区也可用，合并
							curBlock.size+=listBlocks.get(location+1).size;
							listBlocks.remove(location+1);
						}
					}
					
				}else{//如果前一个分区不可用
					if(location<listBlocks.size()-1){//如果不是最后一个分区
						if(listBlocks.get(location+1).isFree){//如果后一个分区可用，合并
							MemBlock curBlock=listBlocks.get(location);
							curBlock.isFree=true;
							curBlock.size+=listBlocks.get(location+1).size;
							finishedJCB.offer(listBlocks.get(location).getJcb());
							listBlocks.remove(location+1);
							return;
						}
					}
					MemBlock curBlock=listBlocks.get(location);
					curBlock.isFree=true;
					finishedJCB.offer(curBlock.getJcb());
					curBlock.setJcb(null);
				}
				
			}
			update();
			memoryAdapter.notifyDataSetChanged();
			jobAdapter.notifyDataSetChanged();
			
		}

		public void update() {
			for(int location=1;location<listBlocks.size();location++){
				listBlocks.get(location).addr=listBlocks.get(location-1).addr
						+listBlocks.get(location-1).size;
			}
		}

		public class JobAdapter extends BaseAdapter{
			
			View view=null;

			@Override
			public int getCount() {
				return finishedJCB.size()+1;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public View getView(int position, View contentView, ViewGroup parent) {
				ViewHolder holder=null;
				if(contentView==null){
					contentView=LayoutInflater
							.from(getActivity())
							.inflate(R.layout.layout_job_info, null);
					holder=new ViewHolder();
					holder.txtJName=(TextView) contentView.findViewById(R.id.txtJobName);
					holder.txtJSize=(TextView) contentView.findViewById(R.id.txtJobAddr);
					holder.txtJMemory=(TextView) contentView.findViewById(R.id.txtJobPiority);
					contentView.setTag(holder);
				}else{
					holder=(ViewHolder) contentView.getTag();
				}
				if(position==0){    //第一行默认显示
					return contentView;
				}else{
					holder.txtJName.setText(finishedJCB.get(position-1).getName()+"");
					holder.txtJSize.setText(finishedJCB.get(position-1).getAddress()+"");
					holder.txtJMemory.setText(finishedJCB.get(position-1).getMemory()+"kB");
					return contentView;
				}
				
			}
			private class ViewHolder{
				private TextView txtJName;
				private TextView txtJSize;
				private TextView txtJMemory;
			}
		}
		
		
		
		public class MemoryAdapter extends BaseAdapter{
			
			View view=null;

			@Override
			public int getCount() {
				return listBlocks.size()+1;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public View getView(int position, View contentView, ViewGroup parent) {
				ViewHolder holder=new ViewHolder();
				View view=LayoutInflater
						.from(getActivity())
						.inflate(R.layout.layout_memory_info, null);
				
				holder.txtBName=(TextView) view.findViewById(R.id.txtMemName);
				holder.txtBAddr=(TextView) view.findViewById(R.id.txtMemAddr);
				holder.txtBSize=(TextView) view.findViewById(R.id.txtMemSize);
				holder.btn=(Button) view.findViewById(R.id.btnMemRecycle);
				view.setTag(holder);
				if(position>0){
					MemBlock block=listBlocks.get(position-1);
					holder.txtBName.setText(block.parNum+"");
					holder.txtBAddr.setText(block.addr+"");
					holder.txtBSize.setText(block.size+"kB");
					if(!block.isFree){
						holder.btn.setBackgroundColor(getResources().getColor(R.color.blue));
						holder.btn.setClickable(true);
						holder.btn.setText("回收"+block.getJcb().getName());
						holder.btn.setTag(listBlocks.get(position-1));
						holder.btn.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								switch (arg0.getId()) {
								case R.id.btnMemRecycle:
									recycle((MemBlock)arg0.getTag());
									break;

								default:
									break;
								}
							}
						});
					}else{
						holder.btn.setText("");
					}
				}
				return view;
				/*
				if(contentView==null){
					contentView=LayoutInflater
							.from(getActivity())
							.inflate(R.layout.layout_memory_info, null);
					holder=new ViewHolder();
					holder.txtBName=(TextView) contentView.findViewById(R.id.txtMemName);
					holder.txtBAddr=(TextView) contentView.findViewById(R.id.txtMemAddr);
					holder.txtBSize=(TextView) contentView.findViewById(R.id.txtMemSize);
					holder.btn=(Button) contentView.findViewById(R.id.btnMemRecycle);
					holder.btn.setText("");
					contentView.setTag(holder);
				}else{
					holder=(ViewHolder) contentView.getTag();
				}
				if(position==0){    //第一行默认显示
					holder.txtBName.setText("分区号");
					holder.txtBAddr.setText("始地址");
					holder.txtBSize.setText("大小");
					holder.btn.setBackgroundColor(getResources().getColor(R.color.gray));
					holder.btn.setClickable(false);
					return contentView;
				}else{
					MemBlock block=listBlocks.get(position-1);
					holder.txtBName.setText(block.parNum+"");
					holder.txtBAddr.setText(block.addr+"");
					holder.txtBSize.setText(block.size+"kB");
					if(!block.isFree){
						holder.btn.setBackgroundColor(getResources().getColor(R.color.blubg));
						holder.btn.setClickable(true);
						holder.btn.setText("回收");
						holder.btn.setTag(listBlocks.get(position-1));
					}else{
						holder.btn.setText("");
					}
					return contentView;
				}
				*/
				
			}
			private class ViewHolder{
				private TextView txtBName;
				private TextView txtBAddr;
				private TextView txtBSize;
				private Button btn;
			}
		}
		
		
	}
	public static void alert(Context context,String string){
		Toast.makeText(context, string, Toast.LENGTH_SHORT)
		.show();
		printStrings.add(string);
	}
		
}
