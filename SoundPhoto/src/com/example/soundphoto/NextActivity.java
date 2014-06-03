package com.example.soundphoto;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.soundphoto.camera.CameraActivity;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NextActivity extends ActionBarActivity{
	
	 private static final String LOG_TAG = "AudioRecordTest";
	 
	 private static final String FILE_EXTENSION_AMR = ".amr";
	 
	 private MediaRecorder mRecorder = null;
	 
	 private Button record_start,record_stop;
	 
	 private boolean isRecording=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_next);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(true);
		 
		record_start=(Button) findViewById(R.id.record_start);
		//record_stop=(Button) findViewById(R.id.record_stop);
		
		record_start.setEnabled(true);
		//record_stop.setEnabled(false);
		
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
		 case android.R.id.home:
	          // This ID represents the Home or Up button. In the case of this
	          // activity, the Up button is shown. Use NavUtils to allow users
	          // to navigate up one level in the application structure. For
	          // more details, see the Navigation pattern on Android Design:
	          //
	          // http://developer.android.com/design/patterns/navigation.html#up-vs-back
	          //
	          NavUtils.navigateUpFromSameTask(this);
	          return true;

		default:
			break;
		}
		
	 
		return super.onOptionsItemSelected(item);
	}
    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
        	mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

    }
	
	public void onStartRecordingClick(View v) {
		// TODO Auto-generated method stub
	/*	record_start.setEnabled(false);
		record_stop.setEnabled(true);*/
		if (isRecording) {
			record_start.setText("¿ªÊ¼Â¼Òô");
			isRecording=false;
			stopRecording();
			//Toast.makeText(this, "ÕýÔÚÂ¼Òô¡£¡£", Toast.LENGTH_SHORT).show();
		}else {			
			isRecording=true;
			record_start.setText("Í£Ö¹Â¼Òô");
			startRecording();			
		}
		
		
	}
	
/*	public void onStopRecordingClick(View v) {
		// TODO Auto-generated method stub
		record_start.setEnabled(true);
		record_stop.setEnabled(false);
		stopRecording();
	}*/
	public void onCapturePicClick(View v) {
		startActivity(new Intent(NextActivity.this, CameraActivity.class));

	}
	
	private void startRecording() {		
      
    	   mRecorder = new MediaRecorder();       
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        
        mRecorder.setOutputFile(AudioRecordOutput());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }
	
	private void stopRecording() {
		if (null!=mRecorder) {
			
			mRecorder.stop();
	        mRecorder.release();
	        mRecorder = null;
		}
		
    }
	private String AudioRecordOutput() {
		
		File audioStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyAudio");
		if (! audioStorageDir.exists()){
	        if (! audioStorageDir.mkdirs()){
	            Log.e("MyAudio", "failed to create directory");
	            return null;
	        }
	    }
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		
	    return audioStorageDir.getAbsolutePath()+File.separator+"VID_"+timeStamp+FILE_EXTENSION_AMR;
	}
	
}
