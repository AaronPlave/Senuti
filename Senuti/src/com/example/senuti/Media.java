package com.example.senuti;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class Media extends Activity {
	Thread t;
	int sr = 44100;
	boolean isRunning = true;

	protected void onCreate(Bundle savedInstanceState) {

		// start a new thread for audio processing
		t = new Thread() {
			public void run() {
				Log.d("TAG_ACTIVITY", "Starting ");
				// set process priority
				setPriority(Thread.MAX_PRIORITY);
				Log.d("TAG_ACTIVITY", "1");
				int buffsize = AudioTrack.getMinBufferSize(sr,
						AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT);
				// create an audiotrack obj
				Log.d("TAG_ACTIVITY", "2");
				AudioTrack audiotrack = new AudioTrack(
						AudioManager.STREAM_MUSIC, sr,
						AudioFormat.CHANNEL_OUT_STEREO,
						AudioFormat.ENCODING_PCM_16BIT, buffsize,
						AudioTrack.MODE_STREAM);
				Log.d("TAG_ACTIVITY", "3");

				if (audiotrack == null) {
					Log.d("TAG_ACTIVITY", "audio track not initialized");
				}
				Log.d("TAG_ACTIVITY", "2");

				int count = 512 * 1024; // 512 kb

				// TODO: get rid of hardcoded filepath
				String filePath = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/goat.wav";
				Log.d("TAG_ACTIVITY", "1");
				// Read in the file by bytes
				byte[] byteData = null;
				File file = null;
				file = new File(filePath);

				byteData = new byte[(int) count];
				FileInputStream in = null;
				try {
					in = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				int bytesread = 0;
				int ret = 0;
				int size = (int) file.length();
				audiotrack.play();
				while (bytesread < size){
					try {
						ret = in.read(byteData,0,count);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (ret != -1){
						//write the byte array to the track
						audiotrack.write(byteData,0,ret);
					}
				}
						
			}
		};
	};
}
