package com.example.senuti;

import java.util.ArrayList;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class MainActivity extends Activity 
implements OnPreparedListener {

	ArrayList<MediaPlayer> mediaPlayers;
	ArrayList<Button> buttons;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Bind button click to playMP3()
		mediaPlayers = new ArrayList<MediaPlayer>();
		buttons = new ArrayList<Button>();
		
		for(int i=0;i<9;i++)
			mediaPlayers.add(new MediaPlayer());
		
		Button playButton = (Button) findViewById(R.id.btnPlay1);
		buttons.add(playButton);
		Button playButton2 = (Button) findViewById(R.id.btnPlay2);
		buttons.add(playButton2);
		Button playButton3 = (Button) findViewById(R.id.btnPlay3);
		buttons.add(playButton3);
		Button playButton4 = (Button) findViewById(R.id.btnPlay4);
		buttons.add(playButton4);
		Button playButton5 = (Button) findViewById(R.id.btnPlay5);
		buttons.add(playButton5);
		Button playButton6 = (Button) findViewById(R.id.btnPlay6);
		buttons.add(playButton6);
		Button playButton7 = (Button) findViewById(R.id.btnPlay7);
		buttons.add(playButton7);
		Button playButton8 = (Button) findViewById(R.id.btnPlay8);
		buttons.add(playButton8);
		Button playButton9 = (Button) findViewById(R.id.btnPlay9);
		buttons.add(playButton9);
		
		for(int i=0;i<buttons.size();i++)
		{
			final int j = i;
			buttons.get(i).setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction() == MotionEvent.ACTION_DOWN)
					{
						int id;
						if(j<5)
							id = R.raw.sandstorm;
						else
							id = R.raw.levels;
						playMP3(mediaPlayers.get(j),id);
						return true;
					}else
						if(event.getAction() == MotionEvent.ACTION_UP)
					{
						stopMP3(mediaPlayers.get(j));
					
					}
					return false;
				}});
		}	
	}
	
	private void stopMP3(MediaPlayer mp){
		if(mp!= null)
		{
			try{
			mp.stop();
			mp.release();
			mp = new MediaPlayer();
			}
			catch(Exception e){
				
			}
		}
	}

	private void playMP3(MediaPlayer mp, int rid) {
		if(mp==null)
		{
			
		}
		else
			
			if(mp.isPlaying())
			{
				return;
			}
			else
				mp.release();
		try {
			mp =
					  MediaPlayer.create(this,
							rid);

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