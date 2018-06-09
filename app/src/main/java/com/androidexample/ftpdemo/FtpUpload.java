package com.androidexample.ftpdemo;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import java.io.File;

import com.kpbird.ftpdemo.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class FtpUpload extends Activity implements OnClickListener {
    
	
	/*********  work only for Dedicated IP ***********/
	static final String FTP_HOST= "ftp.androidthai.in.th";
	
	/*********  FTP USERNAME ***********/
	static final String FTP_USER = "gate@androidthai.in.th";
	
	/*********  FTP PASSWORD ***********/
	static final String FTP_PASS  ="Abc12345";

	private Uri uri;
	private String pathString;
	
	Button btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(this);
        
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			uri = data.getData();

			pathString =  findPath(uri);
			Log.d("9JuneV1", "Path = " + pathString);

			/********** Pick file from sdcard *******/
			File f = new File(pathString);

			// Upload sdcard file
			uploadFile(f);

		}

	}

	private String findPath(Uri uri) {

		String resultSting;
		String[] strings = new String[]{MediaStore.Images.Media.DATA};
		Cursor cursor = getContentResolver().query(uri, strings, null, null, null);
		if (cursor != null) {
            cursor.moveToFirst();
            int i = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            resultSting = cursor.getString(i);
        } else {
            resultSting = uri.getPath();
        }


		return resultSting;
	}

	public void onClick(View v) {

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Choose App"), 1);



		

		
	}
    
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void uploadFile(File fileName){

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);
    	
    	
		 FTPClient client = new FTPClient();
		 
		try {
			
			client.connect(FTP_HOST,21);
			client.login(FTP_USER, FTP_PASS);
			client.setType(FTPClient.TYPE_BINARY);
//			client.changeDirectory("/member/");
			client.changeDirectory("member");
			
			client.upload(fileName, new MyTransferListener());
			
		} catch (Exception e) {
			Log.d("9JuneV1", "e1 ==> " + e.toString());
			try {
				client.disconnect(true);	
			} catch (Exception e2) {
				Log.d("9JuneV1", "e2 ==> " + e.toString());
			}
		}
		
    }
    
    /*******  Used to file upload and show progress  **********/
    
    public class MyTransferListener implements FTPDataTransferListener {

    	public void started() {
    		
    		btn.setVisibility(View.GONE);
    		// Transfer started
    		Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
    		//System.out.println(" Upload Started ...");
    	}

    	public void transferred(int length) {
    		
    		// Yet other length bytes has been transferred since the last time this
    		// method was called
    		Toast.makeText(getBaseContext(), " transferred ..." + length, Toast.LENGTH_SHORT).show();
    		//System.out.println(" transferred ..." + length);
    	}

    	public void completed() {
    		
    		btn.setVisibility(View.VISIBLE);
    		// Transfer completed
    		
    		Toast.makeText(getBaseContext(), " completed ...", Toast.LENGTH_SHORT).show();
    		//System.out.println(" completed ..." );
    	}

    	public void aborted() {
    		
    		btn.setVisibility(View.VISIBLE);
    		// Transfer aborted
    		Toast.makeText(getBaseContext()," transfer aborted , please try again...", Toast.LENGTH_SHORT).show();
    		//System.out.println(" aborted ..." );
    	}

    	public void failed() {
    		
    		btn.setVisibility(View.VISIBLE);
    		// Transfer failed
    		System.out.println(" failed ..." );
    	}

    }
	

}