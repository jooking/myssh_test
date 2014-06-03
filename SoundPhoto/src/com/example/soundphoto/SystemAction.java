package com.example.soundphoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;

public class SystemAction {
	
	public static void dispatchTakePictureIntent(Activity mContent,int requestCode) {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(mContent.getPackageManager()) != null) {
	    	mContent.startActivityForResult(takePictureIntent, requestCode);
	    }
	}
	public static void dispatchTakeVideoIntent(Activity mContent,int requestCode) {
	    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	    if (takeVideoIntent.resolveActivity(mContent.getPackageManager()) != null) {
	    	mContent.startActivityForResult(takeVideoIntent, requestCode);
	    }
	}
	
	/** 
	 * 检查设备是否有摄像装置 
	 * */
	public static boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	       
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	public static void sendEmailBySystem(Context c,String address,String title,String content){
		Intent data=new Intent(Intent.ACTION_SENDTO); 	
		data.setData(Uri.parse("mailto:"+address)); 	
		data.putExtra(Intent.EXTRA_SUBJECT, title); 	
		data.putExtra(Intent.EXTRA_TEXT, content);

		c.startActivity(data);
	
	}
	public static void audioBySystem(Activity context,int requestCode){
	
	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType( "audio/amr"); 
		context.startActivityForResult(intent, requestCode);
	}

}
