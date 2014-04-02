package com.example.senuti;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity 
implements OnPreparedListener {

	public MediaPlayer mp;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Bind button click to playMP3()

		
		Button playButton = (Button) findViewById(R.id.btnPlay);
		playButton.setOnClickListener(new OnClickListener() {

			
			@Override
			public void onClick(View v) {
				playMP3();
			}
		});
		
		Button stopButton = (Button) findViewById(R.id.btnStop);
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mp.stop();
			}
			
		});
	}

	private void playMP3() {
		if(mp.isPlaying())
			return;
		else
		try {
			mp =
					  MediaPlayer.create(this,
							R.raw.sandstorm);

			// MediaPlayer mp = new MediaPlayer();
			// mp.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath()+"/serenity.mp3");
			mp.setOnPreparedListener(this);
			mp.prepare();
		} catch (Exception e) {
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
	}

}