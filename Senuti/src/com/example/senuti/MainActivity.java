package com.example.senuti;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnPreparedListener {

	ArrayList<MediaPlayer> mediaPlayers;
	ArrayList<Button> buttons;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		mediaPlayers = new ArrayList<MediaPlayer>();
		buttons = new ArrayList<Button>();

		// for (int i = 0; i < 9; i++)
		// mediaPlayers.add(new MediaPlayer());
		// Button playButton = (Button) findViewById(R.id.btnPlay1);
		// buttons.add(playButton);
		// Button playButton2 = (Button) findViewById(R.id.btnPlay2);
		// buttons.add(playButton2);
		// Button playButton3 = (Button) findViewById(R.id.btnPlay3);
		// buttons.add(playButton3);
		// Button playButton4 = (Button) findViewById(R.id.btnPlay4);
		// buttons.add(playButton4);
		// Button playButton5 = (Button) findViewById(R.id.btnPlay5);
		// buttons.add(playButton5);
		// Button playButton6 = (Button) findViewById(R.id.btnPlay6);
		// buttons.add(playButton6);
		// Button playButton7 = (Button) findViewById(R.id.btnPlay7);
		// buttons.add(playButton7);
		// Button playButton8 = (Button) findViewById(R.id.btnPlay8);
		// buttons.add(playButton8);
		// Button playButton9 = (Button) findViewById(R.id.btnPlay9);
		// buttons.add(playButton9);
		//
		// for (int i = 0; i < buttons.size(); i++) {
		// final int j = i;
		// buttons.get(i).setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// int id;
		// if (j < 5)
		// id = R.raw.sandstorm;
		// else
		// id = R.raw.levels;
		// playMP3(mediaPlayers.get(j), id);
		// return true;
		// } else if (event.getAction() == MotionEvent.ACTION_UP) {
		// stopMP3(mediaPlayers.get(j));
		//
		// }
		// return false;
		// }
		// });
		// }
	}

	private void stopMP3(MediaPlayer mp) {
		if (mp == null) {

		} else {
			if (mp.isPlaying())
				mp.stop();
		}
	}

	private void playMP3(MediaPlayer mp, int rid) {
		if (mp == null) {

		} else

		if (mp.isPlaying()) {
			return;
		} else
			mp.release();
		try {
			mp = MediaPlayer.create(this, rid);

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
			Log.d("TAG_ACTIVITY","PLAY AUDIOTHREAD");
			audioThread = new AudioThread();    
			audioThread.start();
		}

		public void pause() {
			if (!(audioThread.isPaused())){
				audioThread.pause();
			} else {
				audioThread.unPause();
			}
			
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
			boolean REVERSE = true;
			boolean STOPPED = true;
			int PITCHOFFSET = 0;
			boolean CLEAR = false;
			private byte[] newArray;

			@Override
			public void run() {
				Log.d("TAG_ACTIVITY","PLAYING");
				play();
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

			public void play() {
				
				if (at == null) {
					Log.d("TAG_ACTIVITY", "audio track is not initialised ");
					return;
				}
				Log.d("TAG_ACTIVITY","IN PLAY");
				int count = 4; // how many bytes to be read at a time

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

					// // Write the byte array to the track
					 at.setPlaybackRate(44100);
					byte[] newArray;
					if (!REVERSE) {
						newArray = Arrays.copyOfRange(output, output.length
								- bytesread, output.length - bytesread + count);
					} else {
						newArray = Arrays.copyOfRange(output, bytesread, bytesread+count);
					}

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
