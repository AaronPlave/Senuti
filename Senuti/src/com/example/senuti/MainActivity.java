package com.example.senuti;

<<<<<<< HEAD
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
=======
>>>>>>> de9083d22476e72d4d41b6d1b50fe87124f748c8
import java.util.ArrayList;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;

public class MainActivity extends Activity 
implements OnPreparedListener {

	ArrayList<MediaPlayer> mediaPlayers;
	ArrayList<Button> buttons;
<<<<<<< HEAD
	double sliderval;
=======
	
>>>>>>> de9083d22476e72d4d41b6d1b50fe87124f748c8
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
<<<<<<< HEAD
		// setContentView(R.layout.activity_main);

		setContentView(R.layout.effects_layout);

		// create new instance of AudioTrackPlayer
		final AudioTrackPlayer atp = new AudioTrackPlayer();

		// Bind play AT file
		Button atPlay = (Button) findViewById(R.id.btnAudioTrackPlay);
		atPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				atp.play();
			}
		});

		// Bind pause AT file
		Button atPause = (Button) findViewById(R.id.btnAudioTrackPause);
		atPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				atp.pause();
			}
		});
		
		//Bind the pitch slider for AT
		SeekBar pitchSlider = (SeekBar) findViewById(R.id.pitchSlider);
		
        // create a listener for the slider bar;
        OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
          public void onStopTrackingTouch(SeekBar seekBar) { }
          public void onStartTrackingTouch(SeekBar seekBar) { }
          public void onProgressChanged(SeekBar seekBar, 
                                          int progress,
                                           boolean fromUser) {
              if(fromUser) {
            	  Log.d("TAG_ACTIVITY",Integer.toString(progress));
            	  sliderval = progress / (double)seekBar.getMax();
            	  Log.d("TAG_ACTIVITY",Double.toString(sliderval));
            	  atp.setPitch(sliderval);
            	  
              }
           }
        };

        // set the listener on the slider
        pitchSlider.setOnSeekBarChangeListener(listener);

		
		
		//Bind the reverse toggle for AT
		Switch atReverse = (Switch) findViewById(R.id.toggleReverse);
		atReverse.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
			    // Is the toggle on?
			    boolean on = ((Switch) view).isChecked();
			    
			    if (on) {
			        atp.setReverse(true);
			    } else {
			        atp.setReverse(false);
			    }
			}
		});
		

		// Bind backtrack AT file
		Button atBack = (Button) findViewById(R.id.btnAudioTrackBack);
		atBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: Add logic to determine whether or not to go back to
				// previous song or
				// go to beginning of song based on where in the song you
				// currently are.

			}
		});

		// Bind button click to playMP3()
=======
		setContentView(R.layout.activity_main);
		
		//Bind button click to playMP3()
>>>>>>> de9083d22476e72d4d41b6d1b50fe87124f748c8
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

<<<<<<< HEAD
	public static byte[] convertStreamToByteArray(InputStream is)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[10240];
		int i = Integer.MAX_VALUE;
		while ((i = is.read(buff, 0, buff.length)) > 0) {
			baos.write(buff, 0, i);
		}

		return baos.toByteArray(); // be sure to close InputStream in calling
									// function
	}

	public class AudioTrackPlayer {

		// TODO: implement methods: initialize, play, pause, rewind, load,
		// previous
		// next, seek, destroy, status (return playing, pause, stopped,
		// loading?)

		AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
				44100, AudioTrack.MODE_STREAM);

		AudioThread audioThread;

		public void play() {
			if (audioThread == null){
				Log.d("TAG_ACTIVITY","PLAY AUDIOTHREAD");
				audioThread = new AudioThread();    
				audioThread.setPriority(Thread.MAX_PRIORITY);
				audioThread.start();
			}
			else {
				Log.d("TAG_ACTIVITY","Play call disregarded, already playing/paused");
			}
			
		}

		public void pause() {
			if (!(audioThread.isPaused())){
				audioThread.pause();
			} else {
				audioThread.unPause();
			}
			
		}
		
		public void setPitch(double p) {
			audioThread.setPitchOffset(p);
		}
		
		public void setReverse(boolean r){
			audioThread.setReverse(r);
		}

		public void rewind() {
			return;
		}

		public void load() {
			return;
		}

		public void previous() {
			return;
		}

		public void next() {
			return;
		}

		public void seek() {
			return;
		}

		public void destroy() {
			audioThread.clear();
		};

		private class AudioThread extends Thread {

			boolean ALIVE = true;
			boolean PLAYING = false;
			boolean PAUSED = false;
			boolean REVERSE = false;
			boolean STOPPED = true;
			int PITCHOFFSET = 0;
			boolean CLEAR = false;
			
			int count = 16; // how many bytes to be read at a time
			//TODO: maybe read in 16 bytes and then split up into 4 byte sections. 
			//The more you read in at a time backwards, the worse it sounds, so try to reduce this.
			//However, with a low count like 4, the OS can't really handle higher playback rates because it
			//can't keep up with all of those reads. Is the solution to read 16 and reverse the frames?
		
			
			
			private byte[] newArray;

			@Override
			public void run() {
				Log.d("TAG_ACTIVITY","PLAYING");
				play();
			}
			
			double MAX_OFFSET = 48000.0;
			
			public void setPitchOffset(double p){
				if (p == 0.5) PITCHOFFSET = 0;
				else if (p < 0.5) PITCHOFFSET = (int) ((0.5-p)*-MAX_OFFSET);
				else PITCHOFFSET = (int) ((p-0.5)*MAX_OFFSET);
				Log.d("TAG_ACTIVITY",Integer.toString(PITCHOFFSET)+" PO");
			}
			
			public void setReverse(boolean r){
				REVERSE = r;
			}
			
			public void unPause() {
				PAUSED = false;
				PLAYING = true;
			}
			
			public boolean isPaused() {
				return PAUSED;
			}

			public void pause() {
				PAUSED = true;
				PLAYING = false;
			}

			public void clear() {
				CLEAR = true;
			}
			
			//This function reverses the frames in a byte array, assumes frames are 4 bytes long
			public void reverseFrames (byte[] audio){
				for (int i = 0; i < (Math.log(count)/Math.log(2)-2); i++){
					//the log calculations is to calculate how many swaps are needed in a count sized array
					for (int j = 0; j < 4; j++){
						//grab jth byte in frame A
						byte temp = audio[0+(i*4)+j];
						
						//set jth byte in frame B to jth byte in frame A
						audio[0+(i*4)+j] = audio[audio.length-(i*4)-4+j];
						
						//reverse of above
						audio[audio.length-(i*4)-4+j] = temp;
					}	
				}
			}
			
			//Currently unused really..
			public void clipAudio(byte[] audio){
				for (int i = 0; i < audio.length; i++) {
					float data = audio[i];
					// btw can mix samples by just adding them together

					// reduce the volume a bit:
					// float mixed = samplef1;
					// mixed *= 0.8;
					// hard clipping
//					 if (data > 1.0f)
//						 data = 1.0f;
//					//
//					 if (data < -1.0f)
//						 data = -1.0f;

					byte output = (byte) (data);
					audio[i] = output;
				}
			}

			public void play() {
				
				if (at == null) {
					Log.d("TAG_ACTIVITY", "audio track is not initialised ");
					return;
				}
				Log.d("TAG_ACTIVITY","IN PLAY");
				

				// Reading the file..
				InputStream in1 = getResources().openRawResource(
						R.raw.sandstorm2);
				byte[] music1 = null;
				try {
					music1 = new byte[in1.available()];
					music1 = convertStreamToByteArray(in1);
					in1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				byte[] output = new byte[music1.length];

				int bytesread = count * 2, ret = 0;

				// int playbackRate = (int) (Math.pow(2.0, (1.0 / 12.0)) *
				// 44100);

				// at.setPlaybackRate(playbackRate);

				// TO MIX--
				
				
				for (int i = 44; i < output.length; i++) {
					float samplef1 = music1[i];
					// btw can mix samples by just adding them together

					// reduce the volume a bit:
					// float mixed = samplef1;
					// mixed *= 0.8;
					// hard clipping
					// if (mixed > 1.0f)
					// mixed = 1.0f;
					//
					// if (mixed < -1.0f)
					// mixed = -1.0f;

					byte outputSample = (byte) (music1[i]);
					output[i] = outputSample;
				}

				at.play();
				Log.d("TAG_ACTIVITY","2");
				while (bytesread < output.length) {
					// now we're playing, should do necessary checks for pause,
					// stop, rewind, modify, etc.

					// case where we want to kill the song completely and stop
					// the player
					if (CLEAR) {
						Log.d("TAG_ACTIVITY","CLEAR");
						at.stop();
						at.release();
						return;
					}

					if (PAUSED) {
						at.pause();
						Log.d("TAG_ACTIVITY","PAUSED");
						while (PAUSED) {
							if (CLEAR) {
								at.stop();
								at.release();
								return;
							}
							continue;
						}
						at.play();

					}
//					TODO: if possible, don't read whole song into memory
					// // Write the byte array to the track
					at.setPlaybackRate(44100+PITCHOFFSET);
					byte[] newArray;
					if (REVERSE) {
						newArray = Arrays.copyOfRange(output, output.length
								- bytesread, output.length - bytesread + count);
						reverseFrames(newArray);
					} else {
						newArray = Arrays.copyOfRange(output, bytesread, bytesread+count);
					}
					
					clipAudio(newArray);

					at.write(newArray, 0, count);
					// playbackRate += 500;
					bytesread += count;

				}
				at.stop();
				// at.flush();
				at.release();
			}
		};

	}
}
=======
}
>>>>>>> de9083d22476e72d4d41b6d1b50fe87124f748c8
