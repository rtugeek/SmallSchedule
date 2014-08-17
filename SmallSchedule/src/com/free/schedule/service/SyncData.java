package com.free.schedule.service;

import java.io.File;

import com.baidu.frontia.FrontiaUser.FrontiaUserDetail;
import com.baidu.frontia.api.FrontiaAuthorization;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaAuthorizationListener.UserInfoListener;
import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.request.DeleteObjectRequest;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.free.schedule.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SyncData {
	private Context context;
	public final static String UPLOAD_PATH_1 = "backup/";
	public final static String UPLOAD_PATH_2 = "share/";
	public final static String BUCKET = "smallschedule-data";
	public final static String host = "bcs.duapp.com";
	public final static String secretKey = "iD8pFGmYR1NSgTvXjzYkQdxlz2BOIaqm";
	public final static String accessKey = "hTNsI3k2Y5Hy8uO2n9FAEgng";
	private final static String BACKUP_FILE_NAME_ONE = "settings.xml";
	public final static String BACKUP_FILE_NAME_TWO = "schedule.db";
	private final static String BACKUP_FILE_NAME_THREE = "note.xml";
	private String TO_BACKUP_FILE_ONE;
	private String TO_BACKUP_FILE_TWO;
	private String TO_BACKUP_FILE_THREE;
	private FrontiaAuthorization authorization;
	public static String USER_ID_PATH = "/"; 
	private BCSCredentials credentials;
	private BaiduBCS baiduBCS;
	private ProgressDialog progressDialog;
	public SyncData(Context context,FrontiaAuthorization authorization){
		this.context = context;
		this.authorization = authorization;
		TO_BACKUP_FILE_ONE = context.getFilesDir().getParent() +"/shared_prefs/" + BACKUP_FILE_NAME_ONE;
		TO_BACKUP_FILE_TWO = context.getFilesDir().getParent() + "/databases/"+BACKUP_FILE_NAME_TWO;
		TO_BACKUP_FILE_THREE = context.getFilesDir().getParent() +"/shared_prefs/" + BACKUP_FILE_NAME_THREE;
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		credentials = new BCSCredentials(accessKey, secretKey);
		baiduBCS = new BaiduBCS(credentials, host);
	}
	
	public void backupDate(){
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				if(isDbExist()){
					uploadData(false);
				}else{
					handler.sendEmptyMessage(6);
				}
			}
			
		}.start();
	}
	
	public void syncData(){
		new AlertDialog.Builder(context).setMessage(R.string.isSyncCover).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(-1);
			}
		}).setPositiveButton(R.string.yes,	new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.show();
				UserInfoListener uil = new UserInfoListener() {
					
					@Override
					public void onSuccess(final FrontiaUserDetail arg0) {
						// TODO Auto-generated method stub
						new Thread(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								super.run();
								USER_ID_PATH = "/" + arg0.getId()+"/";
								boolean exited = false;
								try {
									if(baiduBCS.doesObjectExist(BUCKET,USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_ONE)){
										GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET, USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_ONE);
										File f = new File(TO_BACKUP_FILE_ONE);
										if(f.exists())f.delete();
										baiduBCS.getObject(getObjectRequest ,f);
										exited = true;
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
								
								try {
									if(baiduBCS.doesObjectExist(BUCKET,USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_TWO)){
										GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET, USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_TWO);
										File f = new File(TO_BACKUP_FILE_TWO);
										if(f.exists())f.delete();
										baiduBCS.getObject(getObjectRequest ,f);
										exited = true;
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
								
								try {
									if(baiduBCS.doesObjectExist(BUCKET,USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_THREE)){
										GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET, USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_THREE);
										File f = new File(TO_BACKUP_FILE_THREE);
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
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(context, R.string.getUserInfoFailure, Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(-1);
					}
				};
				if(authorization.isAuthorizationReady(MediaType.QZONE.toString())){
					authorization.getUserInfo(MediaType.QZONE.toString(), uil);
				}else if(authorization.isAuthorizationReady(MediaType.BAIDU.toString())){
					authorization.getUserInfo(MediaType.BAIDU.toString(), uil);
				}else{
					Toast.makeText(context, R.string.getUserInfoFailure, Toast.LENGTH_SHORT).show();
					handler.sendEmptyMessage(-1);
				}
			}
		}).show();
	}
	
	private Handler handler = new Handler(){

		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			switch (msg.what) {
			case 0://没有文件
				Toast.makeText(context, R.string.cloudNoneFile, Toast.LENGTH_SHORT).show();
				break;
			case 1://成功
				System.exit(0);
				Toast.makeText(context, R.string.syncSuccess, Toast.LENGTH_SHORT).show();
				break;
			case 2://失败
				Toast.makeText(context, R.string.syncFailure, Toast.LENGTH_SHORT).show();
				break;
			case 3://成功
				Toast.makeText(context, R.string.backupSuccess, Toast.LENGTH_SHORT).show();
				break;
			case 4://失败
				Toast.makeText(context, R.string.backupFailure, Toast.LENGTH_SHORT).show();
				break;
			case 5://备份文件不存在
				Toast.makeText(context, R.string.fileNotExited, Toast.LENGTH_SHORT).show();
				break;
			case 6://存在文件
				new AlertDialog.Builder(context).setMessage(R.string.isBackupCover).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(-1);
					}
				}).setPositiveButton(R.string.yes,	new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						uploadData(false);
					}
				}).show();
				break;
			}
		}
		
	};
	
	
	public void downloadDb(String id){
		try {
			if(baiduBCS.doesObjectExist(BUCKET,USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_TWO)){
				GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET, USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_TWO);
				File f = new File(TO_BACKUP_FILE_TWO);
				if(f.exists())f.delete();
				baiduBCS.getObject(getObjectRequest ,f);
				handler.sendEmptyMessage(1);
			}else{
				handler.sendEmptyMessage(0);
			}
		} catch (Exception e) {
			// TODO: handle exception
			handler.sendEmptyMessage(-1);
		}
	}
	
	public void uploadData(final boolean toshared){
		UserInfoListener uil = new UserInfoListener() {
			
			@Override
			public void onSuccess(final FrontiaUserDetail arg0) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(toshared){
							USER_ID_PATH = "/" + arg0.getId()+"/";
							try {
								if(baiduBCS.doesObjectExist(BUCKET, USER_ID_PATH + UPLOAD_PATH_2 + BACKUP_FILE_NAME_TWO)){
									DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(BUCKET,USER_ID_PATH + UPLOAD_PATH_2 + BACKUP_FILE_NAME_TWO);
									baiduBCS.deleteObject(deleteObjectRequest);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							File file = new File(TO_BACKUP_FILE_TWO);
							if(file.exists()){
								PutObjectRequest request2 = new PutObjectRequest(BUCKET, USER_ID_PATH + UPLOAD_PATH_2 + BACKUP_FILE_NAME_TWO,file);
								ObjectMetadata metadata = new ObjectMetadata();
								request2.setMetadata(metadata);
								baiduBCS.putObject(request2);
							}
						}else{
							USER_ID_PATH = "/" + arg0.getId()+"/";
							try {
								if(baiduBCS.doesObjectExist(BUCKET, USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_ONE)){
									DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(BUCKET,USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_ONE);
									baiduBCS.deleteObject(deleteObjectRequest);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							
							try {
								if(baiduBCS.doesObjectExist(BUCKET, USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_TWO)){
									DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(BUCKET,USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_TWO);
									baiduBCS.deleteObject(deleteObjectRequest);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							
							try {
								if(baiduBCS.doesObjectExist(BUCKET, USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_THREE)){
									DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(BUCKET,USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_THREE);
									baiduBCS.deleteObject(deleteObjectRequest);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							File file = new File(TO_BACKUP_FILE_ONE);
							if(file.exists()){
								PutObjectRequest request1 = new PutObjectRequest(BUCKET, USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_ONE,file);
								baiduBCS.putObject(request1);
								file = new File(TO_BACKUP_FILE_TWO);
								if(file.exists()){
									PutObjectRequest request2 = new PutObjectRequest(BUCKET, USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_TWO,file);
									ObjectMetadata metadata = new ObjectMetadata();
									metadata.setContentMD5(request1.getMetadata().getContentMD5());
									request2.setMetadata(metadata);
									baiduBCS.putObject(request2);
								}
								file = new File(TO_BACKUP_FILE_THREE);
								if(file.exists()){
									PutObjectRequest request2 = new PutObjectRequest(BUCKET, USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_THREE,file);
									ObjectMetadata metadata = new ObjectMetadata();
									metadata.setContentMD5(request1.getMetadata().getContentMD5());
									request2.setMetadata(metadata);
									baiduBCS.putObject(request2);
								}
								handler.sendEmptyMessage(3);
							}else{
								handler.sendEmptyMessage(5);
							}
						}
					}
				}).start();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(context, R.string.getUserInfoFailure, Toast.LENGTH_SHORT).show();
				handler.sendEmptyMessage(-1);
			}
		};
		
		if(authorization.isAuthorizationReady(MediaType.QZONE.toString())){
			authorization.getUserInfo(MediaType.QZONE.toString(), uil);
		}else if(authorization.isAuthorizationReady(MediaType.BAIDU.toString())){
			authorization.getUserInfo(MediaType.BAIDU.toString(), uil);
		}else{
			Toast.makeText(context, R.string.getUserInfoFailure, Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(-1);
		}
	}
	
	private boolean isDbExist(){
		boolean yes = false;
		try {
			if(baiduBCS.doesObjectExist(BUCKET,USER_ID_PATH + UPLOAD_PATH_1 + BACKUP_FILE_NAME_TWO)){
				yes = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		Log.i("DbExist?", yes+"");
		return yes;
	}
	
}
