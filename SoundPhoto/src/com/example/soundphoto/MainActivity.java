package com.example.soundphoto;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import android.os.Build;
import android.provider.MediaStore;

public class MainActivity extends ActionBarActivity implements OnTouchListener{
	private static final String TAG="tag";
	
	Recorder mRecorder;
	
	private RecorderReceiver mReceiver;
	
	private RemainingTimeCalculator mRemainingTimeCalculator;
	
	private static final String FILE_EXTENSION_AMR = ".amr";
	
	private String mErrorUiMessage = null;
	
	 private BroadcastReceiver mSDCardMountEventReceiver = null;

	 private Uri fileUri;

		 
	 static final int REQUEST_IMAGE_CAPTURE = 1;
	 static final int REQUEST_VIDEO_CAPTURE = 2;
	 static final int REQUEST_AUDIO_CAPTURE = 3;
	 
	
	 
	 public static final int BITRATE_AMR = 2 * 1024 * 8; // bits/sec
	 
	   ImageView mImageView;
	  VideoView  mVideoView;
	   String mCurrentPhotoPath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		mImageView=(ImageView) findViewById(R.id.image);
		mVideoView=(VideoView) findViewById(R.id.video);
		mVideoView.setOnTouchListener(this);
		 mRecorder = new Recorder(this);
		 mReceiver = new RecorderReceiver();
		 mRemainingTimeCalculator = new RemainingTimeCalculator();
		 registerExternalStorageListener();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter = new IntentFilter();
        filter.addAction(RecorderService.RECORDER_SERVICE_BROADCAST_NAME);
		registerReceiver(mReceiver, filter);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
		
	}
	
	public void onStartRecordingClick(View v) {
		// TODO Auto-generated method stub
		
		Log.e(TAG, "开始录音");
		//startRecording();
		SystemAction.audioBySystem(MainActivity.this, REQUEST_AUDIO_CAPTURE);
	

	}
	/*public void onStopRecordingClick(View v) {
		// TODO Auto-gene																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																	rated method stub
		Log.e(TAG, "停止录音");
		mRecorder.stop();
	}*/
	
	public void onTakePhotoClick(View v) {
		// TODO Auto-generated method stub
		Log.e(TAG, "拍照");
		if (SystemAction.checkCameraHardware(this)) {
			SystemAction.dispatchTakePictureIntent(this,REQUEST_IMAGE_CAPTURE);//callSystemCamera();
		}else {
			Toast.makeText(this, "没有照相装置，请安装！", Toast.LENGTH_SHORT).show();
		}		
		
	}
	public void onEmailClick(View v) {
		// TODO Auto-generated method stub
		SystemAction.sendEmailBySystem(this, "309928507@qq.com", "fdsds", "dfffffffff");
	}


	public void onShootClick(View v) {
		// TODO Auto-generated method stub
		Log.e(TAG, "摄像");
		if (SystemAction.checkCameraHardware(this)) {
			SystemAction.dispatchTakeVideoIntent(this,REQUEST_VIDEO_CAPTURE);//callSystemShoot();
		}else {
			Toast.makeText(this, "没有摄像装置，请安装！", Toast.LENGTH_SHORT).show();
		}		
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e(TAG, "requestCode="+requestCode+";resultCode="+resultCode);

	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        mVideoView.setVisibility(View.GONE);
	        mImageView.setVisibility(View.VISIBLE);
			mImageView.setImageBitmap(imageBitmap);
	    }
	    if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
	        Uri videoUri = data.getData();
	        Log.e("", videoUri.getPath());
	        mImageView.setVisibility(View.GONE);
	        mVideoView.setVisibility(View.VISIBLE);
	        mVideoView.setVideoURI(videoUri);
	    } if (requestCode == REQUEST_AUDIO_CAPTURE && resultCode == RESULT_OK) {
	        Uri videoUri = data.getData();
	        Log.e("", videoUri.getPath());
	       
	    }
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
        case R.id.action_settings:
           
            return true;
        case R.id.action_next:
            startActivity(new Intent(MainActivity.this,NextActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
	}
	
	 private class RecorderReceiver extends BroadcastReceiver {

	        @Override
	        public void onReceive(Context context, Intent intent) {
	            if (intent.hasExtra(RecorderService.RECORDER_SERVICE_BROADCAST_STATE)) {
	                boolean isRecording = intent.getBooleanExtra(
	                        RecorderService.RECORDER_SERVICE_BROADCAST_STATE, false);
	                mRecorder.setState(isRecording ? Recorder.RECORDING_STATE : Recorder.IDLE_STATE);
	            } else if (intent.hasExtra(RecorderService.RECORDER_SERVICE_BROADCAST_ERROR)) {
	                int error = intent.getIntExtra(RecorderService.RECORDER_SERVICE_BROADCAST_ERROR, 0);
	                mRecorder.setError(error);
	            }
	        }
	    }
	 
	 
	 
	 
	 
	 
/*	 //
	 
	  private void showOverwriteConfirmDialogIfConflicts() {
	        String fileName = FILE_EXTENSION_AMR;

	        if (mRecorder.isRecordExisted(fileName) ) {
	            // file already existed and it's not a recording request from other
	            // app
	            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
	            dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
	            dialogBuilder.setTitle("已经存在，确定要覆盖它么？");
	            dialogBuilder.setPositiveButton(android.R.string.ok,
	                    new DialogInterface.OnClickListener() {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            startRecording();
	                        }
	                    });
	            dialogBuilder.setNegativeButton(android.R.string.cancel,
	                    new DialogInterface.OnClickListener() {
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                        
	                        }
	                    });
	            dialogBuilder.show();
	        } else {
	            startRecording();
	        }
	    }*/
	  
	  private void startRecording() {
	        mRemainingTimeCalculator.reset();
	        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {	           
	            mErrorUiMessage = "没有sd卡";
	           
	        } else if (!mRemainingTimeCalculator.diskSpaceAvailable()) {	           
	            mErrorUiMessage ="存储空间不足";	          
	        } else {
	            

	          
	         String EXTRA_MAX_BYTES = android.provider.MediaStore.Audio.Media.EXTRA_MAX_BYTES;
	           mRemainingTimeCalculator.setBitRate(BITRATE_AMR);
	            int outputfileformat =  MediaRecorder.OutputFormat.AMR_NB;
	                mRecorder.startRecording(outputfileformat,"name",
	                        FILE_EXTENSION_AMR, false, Long.parseLong(EXTRA_MAX_BYTES));
	            

	        
	        }
	    }
	  	@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			if (mSDCardMountEventReceiver != null) {
	            unregisterReceiver(mSDCardMountEventReceiver);
	            mSDCardMountEventReceiver = null;
	        }
		}
	  
	    private void registerExternalStorageListener() {
	        if (mSDCardMountEventReceiver == null) {
	            mSDCardMountEventReceiver = new BroadcastReceiver() {
	                @Override
	                public void onReceive(Context context, Intent intent) {
	                  
	                    mRecorder.reset();
	                    //resetFileNameEditText();
	                   
	                }
	            };
	            IntentFilter iFilter = new IntentFilter();
	            iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
	            iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
	            iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
	            iFilter.addDataScheme("file");
	            registerReceiver(mSDCardMountEventReceiver, iFilter);
	        }	
	    }
	    
	    private void updateTimeRemaining() {
	        long t = mRemainingTimeCalculator.timeRemaining();

	        if (t <= 0) {
	           

	            int limit = mRemainingTimeCalculator.currentLowerLimit();
	            switch (limit) {
	                case RemainingTimeCalculator.DISK_SPACE_LIMIT:
	                    mErrorUiMessage = "存储空间已满！";
	                    break;
	                case RemainingTimeCalculator.FILE_SIZE_LIMIT:
	                    mErrorUiMessage = "已达到最大长度";
	                    break;
	                default:
	                    mErrorUiMessage = null;
	                    break;
	            }

	            mRecorder.stop();
	            return;
	        }
	    }



		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			
			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				
				if (null != mVideoView && !mVideoView.isPlaying()) {				
					mVideoView.start();					
				}else if (null != mVideoView &&mVideoView.isPlaying()) {
					mVideoView.stopPlayback();
				}
				break;
			default:
				break;
			}

			return true;
		}

}
