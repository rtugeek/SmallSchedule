package com.free.schedule;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.free.schedule.service.SyncData;
import com.free.schedule.view.ScheduleView;
import com.qy.commons.qrcode.CaptureActivity;
import com.qy.commons.qrcode.Intents;

public class Main extends Activity {
	public ScheduleView mySchedule;
	private SharedPreferences sp;
	private static Activity ac;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		sp = getSharedPreferences(All.sharedName, 0);
		ac = this;
		//设置全屏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
        
        setContentView(R.layout.load);
        ((ImageView)findViewById(R.id.light)).startAnimation(AnimationUtils.loadAnimation(this, R.anim.light_scale));//设置发光动画
        
        if(sp.getInt(All.dayOfMonth, 55)==55){//是否为第一次打开,
        	Intent it =new Intent(this,GuideYou.class);
        	startActivity(it);
        }
        
		
		 new Thread(){//新建线程加载UI数据

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					mySchedule = new ScheduleView(Main.this);
					mySchedule.initView();
					
				}
	        	
	        }.start();
	        finishInitView.postDelayed(uiRunnable,3000);
	}
	
	
	public Handler finishInitView = new Handler(){//初始化视图完毕

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == 0){
				mySchedule.addAllView();
				setContentView(mySchedule);
				//取消全屏
				WindowManager.LayoutParams attrs = getWindow().getAttributes();
				attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
				getWindow().setAttributes(attrs);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			}
			super.handleMessage(msg);
		}
		
	};
	
	private Runnable uiRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			finishInitView.sendEmptyMessage(0);
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_MENU){
			mySchedule.showSettings();
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}
	
	
	private static ProgressDialog progressDialog;
	@Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // 二维码返回结果
            	downloadDb(data.getStringExtra(Intents.Scan.RESULT));
            }
        }
    }
	
	/**下载数据库
	 * @param id ID号
	 */
	private static void downloadDb(final String id){
		progressDialog = new ProgressDialog(ac);
		progressDialog.setCancelable(false);
		progressDialog.show();
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				BCSCredentials credentials= new BCSCredentials(SyncData.accessKey, SyncData.secretKey);
				BaiduBCS baiduBCS = new BaiduBCS(credentials, SyncData.host);
				SyncData.USER_ID_PATH = "/" + id+"/";
				boolean exited = false;
				try {
					if(baiduBCS.doesObjectExist(SyncData.BUCKET,SyncData.USER_ID_PATH + SyncData.UPLOAD_PATH_2 + SyncData.BACKUP_FILE_NAME_TWO)){
						GetObjectRequest getObjectRequest = new GetObjectRequest(SyncData.BUCKET, SyncData.USER_ID_PATH + SyncData.UPLOAD_PATH_2 + SyncData.BACKUP_FILE_NAME_TWO);
						File f = new File(ac.getFilesDir().getParent() + "/databases/"+SyncData.BACKUP_FILE_NAME_TWO);
						if(f.exists())f.delete();
						baiduBCS.getObject(getObjectRequest ,f);
						exited = true;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if(exited){
					handler.sendEmptyMessage(1);
				}else{
					handler.sendEmptyMessage(0);
				}
			}
			
		}.start();
	}
	
	public static Handler handler = new Handler(){

		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			switch (msg.what) {
			case 0://没有文件
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				Toast.makeText(ac, R.string.fileNotExited1, Toast.LENGTH_SHORT).show();
				break;
			case 1://成功
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				System.exit(0);
				break;
			case 2://扫一扫
				Intent i = new Intent(ac, CaptureActivity.class);
				ac.startActivityForResult(i, 0);
				break;
			case 3://ID
				final EditText etx=new EditText(ac);
				etx.setInputType(InputType.TYPE_CLASS_NUMBER);
				InputFilter[] ift={new InputFilter.LengthFilter(15)};
				etx.setFilters(ift);
				final AlertDialog adx=new AlertDialog.Builder(ac).setTitle(R.string.inputHint).setView(etx).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						downloadDb(etx.getText().toString());
					}
				}).setNegativeButton(R.string.cancel, null).show();
				adx.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
				etx.addTextChangedListener(new TextWatcher(){

					@Override
					public void afterTextChanged(Editable arg0) {
						// TODO Auto-generated method stub
						if(etx.getText().toString().trim().length()==0){
							adx.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
						}else{
							adx.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
						}
					}

					@Override
					public void beforeTextChanged(
							CharSequence arg0, int arg1,
							int arg2, int arg3) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onTextChanged(
							CharSequence arg0, int arg1,
							int arg2, int arg3) {
						// TODO Auto-generated method stub
						
					}
					
				});
				break;
			}
		}
		
	};
	
}
