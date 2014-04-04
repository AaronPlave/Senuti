package com.example.senuti;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnPreparedListener {

	ArrayList<MediaPlayer> mediaPlayers;
	ArrayList<Button> buttons;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Bind atButton click to play AT file
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
	public static byte[] reverseWAV(final byte[] pArray) {
	    if (pArray == null) {
	      return null;
	    }
	    
	    //copy the 44-byte WAV header
	    byte[] newArray = new byte[pArray.length];
	    for (int i = 0; i < 0; i++){
	    	newArray[i] = pArray[i];
	    }
	    		
	    int ite = 0;
	    int frameSize = 4;
	    //Now we assume that the file is being played in 16 bit stereo, which
	    //means that we read 4-byte frames.
	    int newLen = pArray.length;
	    while (ite < newLen-44-frameSize) {
	      byte[] frame = Arrays.copyOfRange(pArray,newLen-ite-frameSize,newLen-ite);
	      for (int i = 0; i < frameSize; i++){
	    	  newArray[43+ite+i] = frame[i];
	      }
	      ite+=frameSize;
	    }
	    return newArray;
	  }

	private void playAT() {
		Thread t;
		t = new Thread() {
			public void run() {
				// int intSize =
				// android.media.AudioTrack.getMinBufferSize(44100,
				// AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				// AudioFormat.ENCODING_PCM_16BIT);

				AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC,
						44100, AudioFormat.CHANNEL_OUT_STEREO,
						AudioFormat.ENCODING_PCM_16BIT, 44100,
						AudioTrack.MODE_STREAM);

				if (at == null) {
					Log.d("TCAudio", "audio track is not initialised ");
					return;
				}

				int count = 4; // 512 kb
				// Reading the file..
				byte[] byteData = null;
				File file = null;
				String filePath = "/storage/emulated/legacy/sandstorm.wav";

				// Uri url = Uri.parse("android.resource://com.example.senuti/"
				// + R.raw.sandstorm2);
				// File file = new File(url.getPath());

				file = new File(filePath);
				file.toString();

				byteData = new byte[(int) count];
				FileInputStream in = null;
				try {
					in = new FileInputStream(file);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

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

				// try {
				// byte[] music = new byte[inStream.available()];
				// } catch (IOException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }

				// InputStream inStream =
				// getResources().openRawResource(R.raw.sandstorm2);
				// byte[] byteData2 = null;
				// try {
				// byteData2 = new byte[inStream.available()];
				// } catch (IOException e2) {
				// e2.printStackTrace();
				// }
				// try {
				// byteData2 = convertStreamToByteArray(inStream);
				// } catch (IOException e1) {
				// e1.printStackTrace();
				// }
				//
				Log.d("TAG_ACTIVITY", Integer.toString(output.length)
						+ " LENGTH");

				int bytesread = count*2, ret = 0;
				// int size = (int) file.length();
				// int size2 = (int) byteData2.length;
				// int pos = 0;

				
//				int playbackRate = (int) (Math.pow(2.0, (1.0 / 12.0)) * 44100);

//				at.setPlaybackRate(playbackRate);

				// TO MIX--
				for (int i = 44; i < output.length; i++) {
					float samplef1 = music1[i];
					// btw can mix samples by just adding them together

					// reduce the volume a bit:
//					float mixed = samplef1;
//					mixed *= 0.8;
					// hard clipping
//					if (mixed > 1.0f)
//						mixed = 1.0f;
//
//					if (mixed < -1.0f)
//						mixed = -1.0f;

					byte outputSample = (byte) (music1[i]);
					output[i] = outputSample;
				}
				
				
				// at.write(output, 0, output.length);
				Log.d("TAG_ACTIVITY",  (Float.toString((int) output[0])));
				Log.d("TAG_ACTIVITY",  (Float.toString((int) output[50])));
//				byte[] output2 =reverseWAV(output);
				byte[] output2 = output;
				Log.d("TAG_ACTIVITY",  (Float.toString((int) output2[output2.length-1])));
				Log.d("TAG_ACTIVITY",  (Float.toString((int) output2[output2.length-51])));
				
				at.play();
				while (bytesread < output.length) {
					// // try {
					// // ret = in.read(byteData, 0, count);
					// // } catch (IOException e) {
					// // e.printStackTrace();
					// // }
					// // if (ret != -1) {
					//
					// // Write the byte array to the track
					// //
					// // Log.d("TAG_ACTIVITY", Integer.toString(playbackRate)
					// // + " playback Rate");
//					 at.setPlaybackRate(playbackRate);
					// //suppressed a warning about api version for copyOfRange
//					// here
//					Log.d("TAG_ACTIVITY", Integer.toString(output.length)
//							+ " output length");
//					Log.d("TAG_ACTIVITY", Integer.toString(bytesread)
//							+ " bytes read");
					byte[] newArray = Arrays.copyOfRange(output, output.length-bytesread,
							output.length - bytesread + count);
					//to reverse an array
//					Collections.reverse(Arrays.asList(newArray));
					at.write(newArray, 0, count);
//					playbackRate += 500;
					// // Log.d("TAG_ACTIVITY", Integer.toString(ret));
					bytesread += count;
					// // } else
					// // break;
				}
				// try {
				// in.close();
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
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
