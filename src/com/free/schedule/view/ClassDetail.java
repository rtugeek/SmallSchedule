package com.free.schedule.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.free.schedule.All;
import com.free.schedule.R;

public class ClassDetail extends PopupWindow{
	private LayoutInflater li;
	private View classDetailView;
	private Context cx;
	private Button saveOrDelete;//删除或保存按钮
	private EditText name,place,teacher;
	private int action;//设置对课程的操作  0，保存课程,1，删除课程
	private ClassTextView classToShow;//要显示的课程
	private ToggleButton noteShow;//显示笔记的按钮
	private SharedPreferences sp;
	private SharedPreferences.Editor spe;
	private EditText noteContent;//笔记内容
	private String[] typeContent = new String[3];//用于储存3种类型的笔记  笔记，作业，其他
	private RadioButton[] typeRadio = new RadioButton[3];
	private RadioGroup type;
	private int nowFocusType = 0;//现在笔记选中的类型
	private ImageView noteImage;//笔记图标
	private String noteIndex;//笔记索引
	private boolean detailChange = false;
	
	/**此构造方法用于协调2.3.7以下版本,PopupWindow.showAtLocation不能显示的问题
	 * @param parent 任意一个显示在屏幕上的View
	 * @param w 固定为LayoutParmas.FILL_PARNENT = -1
	 * @param h 固定为LayoutParmas.WRAP_PARNENT = -2
	 * @param context
	 * @param classToShow 要显示或操作的classText
	 */
	public ClassDetail(View parent,int width,int height,Context context,final ClassTextView classToShow){
		super(parent,width,height);
		cx = context;
		sp = context.getSharedPreferences(All.noteShared, 0);
		spe = sp.edit();
		this.classToShow = classToShow;
		initClassDetail(context);
	}
	
	/**
	 * @param action
	 * 0 :添加或保存课程
	 * 1 :显示,删除课程
	 * 
	 */
	public void setAction(int action){
		this.action = action;
		switch(action){
		case 0:
			saveOrDelete.setText(R.string.saveClass);
			saveOrDelete.setBackgroundResource(R.drawable.save);
			name.setText("");
			place.setText("");
			teacher.setText("");
			noteShow.setVisibility(8);
			noteImage.setVisibility(8);
			noteShow.setChecked(false);
			break;
		case 1:
			saveOrDelete.setText(R.string.deleteClass);
			saveOrDelete.setBackgroundResource(R.drawable.delete_button);
			name.setText(classToShow.name);
			name.setSelection(name.getText().toString().length());
			place.setText(classToShow.place);
			teacher.setText(classToShow.teacher);
			noteShow.setVisibility(0);
			noteImage.setVisibility(0);
			break;
		}
		name.addTextChangedListener(new DetailChangeListener());
		place.addTextChangedListener(new DetailChangeListener());
		teacher.addTextChangedListener(new DetailChangeListener());
		showAtLocation(classToShow, Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private class checkChangeListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){ 
				noteContent.removeTextChangedListener(noteWatcher);//先移出监听
				nowFocusType = Integer.parseInt(arg0.getTag().toString());
				noteContent.setText(typeContent[nowFocusType]);
				noteContent.addTextChangedListener(noteWatcher);
			}
		}
		
	}
	
	private class DetailChangeListener implements TextWatcher{

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			action = 0;
			saveOrDelete.setText(R.string.saveClass);
			saveOrDelete.setBackgroundResource(R.drawable.save);
			detailChange =true;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private TextWatcher noteWatcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			typeContent[nowFocusType] = s.toString();
			saveOrDelete.setText(R.string.saveClass);
			saveOrDelete.setBackgroundResource(R.drawable.save);
			action =  0;
			if(!detailChange){
				action =  2;
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	/**
	 * 初始化classDetail
	 */
	private void initClassDetail(Context context){
		//设置popouWindow属性
		li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		classDetailView = li.inflate(R.layout.class_detail, null);
		setContentView(classDetailView);
		setTouchable(true);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_full_bright));
		setFocusable(true);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setAnimationStyle(R.style.popupStyle);
		//加载控件
		saveOrDelete = (Button)classDetailView.findViewById(R.id.saveOrDelete);
		ImageButton cancel = (ImageButton)classDetailView.findViewById(R.id.cancel);
		name = (EditText)classDetailView.findViewById(R.id.name);
		place = (EditText)classDetailView.findViewById(R.id.place);
		teacher = (EditText)classDetailView.findViewById(R.id.teacher);
		noteShow = (ToggleButton)classDetailView.findViewById(R.id.noteShow);
		noteImage = (ImageView)classDetailView.findViewById(R.id.noteImage);
		type = (RadioGroup)classDetailView.findViewById(R.id.type);
		noteContent = (EditText)classDetailView.findViewById(R.id.content);
		typeRadio[0] = (RadioButton)classDetailView.findViewById(R.id.note);
		typeRadio[1] = (RadioButton)classDetailView.findViewById(R.id.homework);
		typeRadio[2] = (RadioButton)classDetailView.findViewById(R.id.other);
		
		noteIndex = classToShow.dayOfWeek+"" + classToShow.startSection;
		
		for(int i = 0;i < 3;i++){
			typeContent[i] = sp.getString( i + noteIndex, "");
			typeRadio[i].setOnCheckedChangeListener(new checkChangeListener());
		}
		
		noteContent.setText(typeContent[0]);
		noteContent.addTextChangedListener(noteWatcher);
		
		noteShow.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					type.setVisibility(View.VISIBLE);
					noteContent.setVisibility(View.VISIBLE);
					noteImage.setImageResource(R.drawable.note1);
				}else{
					type.setVisibility(View.GONE);
					noteContent.setVisibility(View.GONE);
					noteImage.setImageResource(R.drawable.note0);
				}
			}
		});
		saveOrDelete.setOnClickListener(new OnClickListener() {
			
			@Override 
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(action == 0){
					if(name.getText().toString().trim().length() != 0){
						for(int i = 0;i < ScheduleView.list;i++){//遍历所有课程格，并找出被选中的课程格
							for(int i2 = 0;i2 < ScheduleView.row ;i2++){
								if(ScheduleView.classText[i][i2].isSelected){
										if(i2 - 1 < 0 || !ScheduleView.classText[i][i2 - 1].isSelected){
											int shu = 0;
											if(ScheduleView.classText[i][i2].isClass){//如果有课,先删除
												ClassTextView.classManager.deleteClass(ScheduleView.classText[i][i2]);
											}
											while(i2 + shu + 1 < ScheduleView.row && ScheduleView.classText[i][i2 + shu + 1].isSelected){
												if(ScheduleView.classText[i][i2 + shu + 1].isClass){
													ClassTextView.classManager.deleteClass(ScheduleView.classText[i][i2 + shu + 1]);
												}
												shu++;
											}
											ContentValues cv =new ContentValues();
											cv.put("name", name.getText().toString());
											cv.put("place", place.getText().toString());
											cv.put("dayOfWeek", i);
											cv.put("startSection", i2);
											cv.put("endSection", i2 + shu);
											cv.put("teacher", teacher.getText().toString());
											ClassTextView.classManager.insertClass(cv);
										}
								}
							}
						}
						saveNote();
						dismiss();
					}else{
						Toast.makeText(cx, cx.getString(R.string.noName), Toast.LENGTH_SHORT).show();
					}
				}else if(action == 1){
					clearContentNote();
					saveNote();
					classToShow.animationDelete();
					dismiss();
				}else if(action == 2){
					saveNote();
					dismiss();
				}
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		
		setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				Log.i("action", action+"");
				classToShow.setSelected(false);
				if(action != 1){
					ScheduleView.refreshSchedule();
				}
			}
		});
	}
	
	/**
	 * 保存笔记
	 */
	private void saveNote(){
		boolean isNullNote = true;//判断记事本内容是否为空
		for(int i = 0;i < 3;i++){
			spe.putString(i + noteIndex, typeContent[i]).commit();
			Log.i("noteContent", typeContent[i]);
			if(typeContent[i].equals("")){ 
				spe.remove(i + noteIndex);
			}else{
				isNullNote = false;
			}
		}
		Log.i("saveIndex",  classToShow.dayOfWeek+"" + classToShow.startSection+"");
		Log.i("saveNote",  !isNullNote +"");
		if(isNullNote){
			spe.remove(noteIndex).commit();
		}else{
			spe.putBoolean(noteIndex, true).commit();
		}
	}
	
	public void clearContentNote(){
		for(int i = 0;i < 3;i++){
			typeContent[i] = "";
		}
	}
	
}
