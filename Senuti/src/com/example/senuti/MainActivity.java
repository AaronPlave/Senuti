package com.example.senuti;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnPreparedListener {

	ArrayList<MediaPlayer> mediaPlayers;
	ArrayList<Button> buttons;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		 Bind atButton click to play AT file
		Button atPlay = (Button) findViewById(R.id.btnAT);
		atPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playAT();
			}
		});

		// Bind button click to playMP3()
		mediaPlayers = new ArrayList<MediaPlayer>();
		buttons = new ArrayList<Button>();

		for (int i = 0; i < 9; i++)
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

		for (int i = 0; i < buttons.size(); i++) {
			final int j = i;
			buttons.get(i).setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						int id;
						if (j < 5)
							id = R.raw.sandstorm;
						else
							id = R.raw.levels;
						playMP3(mediaPlayers.get(j), id);
						return true;
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						stopMP3(mediaPlayers.get(j));

					}
					return false;
				}
			});
		}
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

	private void playAT() {
		Thread t;
		t = new Thread() {
			public void run() {
				int intSize = android.media.AudioTrack.getMinBufferSize(44100,
						AudioFormat.CHANNEL_CONFIGURATION_STEREO,
						AudioFormat.ENCODING_PCM_16BIT);

				AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC,
						44100, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
						AudioFormat.ENCODING_PCM_16BIT, intSize,
						AudioTrack.MODE_STREAM);

				if (at == null) {
					Log.d("TCAudio", "audio track is not initialised ");
					return;
				}

				int count = 512 * 1024; // 512 kb
				// Reading the file..
				byte[] byteData = null;
				File file = null;
				String filePath = "/storage/emulated/legacy/sandstorm.wav";
				
//				Uri url = Uri.parse("android.resource://com.example.senuti/" + R.raw.sandstorm2);
//				File file = new File(url.getPath());
				
				
				file = new File(filePath);
				file.toString();

				byteData = new byte[(int) count];
				FileInputStream in = null;
				try {
					in = new FileInputStream(file);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				int bytesread = 0, ret = 0;
				int size = (int) file.length();
				at.play();
				int playbackRate = (int) (Math.pow(2.0, (1.0 / 12.0)) * 44100);

				at.setPlaybackRate(playbackRate);
				while (bytesread < size) {
					try {
						ret = in.read(byteData, 0, count);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (ret != -1) {
						// Write the byte array to the track
						playbackRate += 500;
						Log.d("TAG_ACTIVITY", Integer.toString(playbackRate)
								+ " playback Rate");
						at.setPlaybackRate(playbackRate);
						at.write(byteData, 0, ret);
						Log.d("TAG_ACTIVITY", Integer.toString(ret));
						bytesread += ret;
					} else
						break;
				}
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				at.stop();
				at.release();
			}
		};
		t.start();
	}
}

// protected void playAT2() {
// Thread t;
// Log.d("TAG_ACTIVITY", "Starting at??");
//
// boolean isRunning = true;
//
// // start a new thread for audio processing
// t = new Thread() {
// public void run() {
// Log.d("TAG_ACTIVITY", "NEW THREAD");
// // Log.d("TAG_ACTIVITY", "??");
// int sr = 44100;
// // set process priority
// setPriority(Thread.MAX_PRIORITY);
// int buffsize = AudioTrack.getMinBufferSize(sr,
// AudioFormat.CHANNEL_OUT_MONO,
// AudioFormat.ENCODING_PCM_8BIT);
// // create an audiotrack obj
// AudioTrack audiotrack = new AudioTrack(
// AudioManager.STREAM_MUSIC, sr,
// AudioFormat.CHANNEL_OUT_STEREO,
// AudioFormat.ENCODING_PCM_8BIT, buffsize,
// AudioTrack.MODE_STREAM);
//
// if (audiotrack == null) {
// Log.d("TAG_ACTIVITY", "audio track not initialized");
// }
//
// int count = 512; // 512 kb
// // int count = 16 * 1024;
// // TODO: get rid of hardcoded filepath
// String filePath = "/storage/emulated/legacy/goat.wav";
// // + "goat.wav";
// Log.d("TAG_ACTIVITY", filePath);
// // Read in the file by bytes
// byte[] byteData = null;
// File file = null;
// file = new File(filePath);
// byteData = new byte[(int) count];
// FileInputStream in = null;
// try {
// in = new FileInputStream(file);
// } catch (FileNotFoundException e) {
// e.printStackTrace();
// }
//
// int bytesread = 0;
// int ret = 0;
// int size = (int) file.length();
// audiotrack.play();
// // Log.d("TAG_ACTIVITY",size);
// Log.d("TAG_ACTIVITY", Integer.toString(size) + " size");
// while (bytesread < size) {
// Log.d("TAG_ACTIVITY", Integer.toString(bytesread)
// + " bytesread");
// Log.d("TAG_ACTIVITY", Integer.toString(count) + " count");
// try {
// ret = in.read(byteData, 0, count);
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// Log.d("TAG_ACTIVITY", "PROBLEM");
// }
// if (ret != -1) {
// // write the byte array to the track
// audiotrack.write(byteData, 0, ret);
// bytesread += ret;
// Log.d("TAG_ACTIVITY", Integer.toString(ret) + " ret");
// } else {
// break;
// }
// try {
// in.close();
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// Log.d("TAG_ACTIVITY", "PROBLEM");
// }
// audiotrack.stop();
// audiotrack.release();
// Log.d("TAG_ACTIVITY", "READING");
// }
//
// }
// };
// t.start();
// };
