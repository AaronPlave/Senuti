package com.example.senuti;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.MusicRetriever.MusicRetriever;
import com.example.MusicRetriever.PrepareMusicRetrieverTask;
import com.example.senuti.BeatFragment.OnBeatPlayedListener;
import com.example.senuti.PlayFragment.PlayControllerListener;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnPreparedListener,
		PrepareMusicRetrieverTask.MusicRetrieverPreparedListener,
		OnBeatPlayedListener, FragmentSwitcherListener, PlayControllerListener {

	boolean isDualPane;
	boolean musicRetrieverReady = false;
	MusicRetriever mRetriever;
	final AudioTrackPlayer atp = new AudioTrackPlayer();
	Button switchButton;
	int nextFrag;

	// TODO:LOCK ORIENTATION

	@Override
	public void onMusicRetrieverPrepared() {
		// Done retrieving!
		// If the flag indicates we should start playing after retrieving, let's
		// do that now.
		musicRetrieverReady = true;
		Log.d("TAG_ACTIVITY", "DONE RETRIEVING");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("TAG_ACTIVITY", "on create called");
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);

		setContentView(R.layout.main);

		// fill the screen with one or two fragments, depending on size of
		// device. we used some layout aliases to maket his easier

		if (findViewById(R.id.fragment_container_one) != null) {
			Log.d("TAG_ACTIVITY", "fragment container one not null");

			// Create a new Fragment to be placed in the activity layout
			PlayFragment pf = new PlayFragment();

			// Add the fragment to the 'fragment_container' FrameLayout
			getFragmentManager().beginTransaction()
					.add(R.id.fragment_container_one, pf).commit();
			View secondFrag = findViewById(R.id.fragment_container_two);

			isDualPane = (secondFrag != null && secondFrag.getVisibility() == View.VISIBLE);

			if (isDualPane) {
				Log.d("TAG_ACTIVITY", "Dual pane mode.");
				BeatFragment bf = new BeatFragment();
				getFragmentManager().beginTransaction()
						.add(R.id.fragment_container_two, bf).commit();
				Log.d("TAG_ACTIVITY", "added second fragment");
			} else
				nextFrag = 1;
		}

		if (!isDualPane) {
			Log.d("TAG_ACTIVITY", "setting a switch button");
			switchButton = (Button) findViewById(R.id.fragment_switch);
			switchButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showFragment(nextFrag);
				}
			});
		} else {
			Log.d("TAG_ACTIVITY", "no need for button, in dual pane mode");
		}

		// Create the retriever and start an asynchronous task that will prepare
		// it.

		mRetriever = new MusicRetriever(getContentResolver());
		AsyncTask<Void, Void, Void> execute = new PrepareMusicRetrieverTask(
				mRetriever, this);
		execute.execute();

		// create new instance of AudioTrackPlayer

	}

	// ///

	// ///

	// ////HERE ENDS ONcREATE

	// ////

	// /////

	void playSong(Uri uri) {
		atp.playSong(uri);
	}

	// this method lets you switch fragments within one pane back and forth
	public void showFragment(int fragment) {
		FragmentManager fm = getFragmentManager();
		android.app.FragmentTransaction ft = fm.beginTransaction();
		if (fragment == 1) {
			nextFrag = 0;
			BeatFragment bf = new BeatFragment();
			// Bundle args = new Bundle();
			// dont have any args at the moment
			// bf.setArguments(args);

			ft.replace(R.id.fragment_container_one, bf);
			ft.addToBackStack(null);
			ft.commit();
		} else if (fragment == 0) {
			nextFrag = 1;
			PlayFragment pf = new PlayFragment();

			// dont have any args at the moment
			// newFragment.setArguments(args);

			ft.replace(R.id.fragment_container_one, pf);
			ft.addToBackStack(null);
			ft.commit();
		}

	}

	protected static final int ACTIVITY_CHOOSE_FILE = 1;

	public void chooseFileFromIntent() {
		Intent chooseFile;
		Intent intent;
		chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
		chooseFile.setType("file/*");
		intent = Intent.createChooser(chooseFile, "Choose a file");
		startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTIVITY_CHOOSE_FILE: {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();

				atp.playSong(uri);
			}
		}
		}
	}

	// launcher method to implement interface so UI can be put in fragment
	public void setReverse(boolean dir) {
		atp.setReverse(dir);
	}

	public void play() {
		atp.play();
	}

	public void pause() {
		atp.pause();
	}

	public void setPitch(double sliderVal) {
		atp.setPitch(sliderVal);
	}

	public void stopMP3(MediaPlayer mp) {
		if (mp == null) {

		} else {
			if (mp.isPlaying())
				mp.stop();
		}
	}

	public void back() {
		try {
			atp.back();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void random() {
		if (musicRetrieverReady) {
			atp.playRandomSong();
		} else {
			Log.d("TAG_ACTIVITY", "NOT DONE RETRIEVING, should set spinner.");
		}
	}

	public void playMP3(MediaPlayer mp, int rid) {
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

	// public static void writeByteArrayToFile(byte[] byteArray,
	// FileOutputStream fos, BufferedOutputStream bos, int start, int end) {
	// try {
	// if (bos != null){
	// bos.write(byteArray,start,end);
	// }
	// }
	// }

	public class AudioTrackPlayer {

		// TODO: implement methods: initialize, play, pause, rewind, load,
		// previous
		// next, seek, destroy, status (return playing, pause, stopped,
		// loading?)

		AudioThread audioThread;

		public boolean ready() {
			return (audioThread == null);
		}

		public void play() {
			if (audioThread == null) {

				Log.d("TAG_ACTIVITY", "PLAY AUDIOTHREAD");
				audioThread = new AudioThread();
				audioThread.setPriority(Thread.MAX_PRIORITY);
				audioThread.start();
			} else if (audioThread.PAUSED) {
				// case where song is at beginning or end
				audioThread.unPause();
			} else {
				Log.d("TAG_ACTIVITY",
						"Play call disregarded, already playing/paused");
			}
		}

		public void pause() {
			if (!(audioThread.isPaused())) {
				audioThread.pause();
			} else {
				audioThread.unPause();
			}
		}

		public void back() throws IOException {
			audioThread.back();
		}

		public void setPitch(double p) {
			audioThread.setPitchOffset(p);
		}

		public void setReverse(boolean r) {
			audioThread.setReverse(r);
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

		// public List<String> getSongsOnDevice() {
		//
		// List<Item> mItems = new ArrayList<Item>();
		//
		// // Some audio may be explicitly marked as not being music
		// String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		//
		// String[] projection = { MediaStore.Audio.Media._ID,
		// MediaStore.Audio.Media.ARTIST,
		// MediaStore.Audio.Media.TITLE,
		// MediaStore.Audio.Media.DATA,
		// MediaStore.Audio.Media.DISPLAY_NAME,
		// MediaStore.Audio.Media.DURATION };
		//
		// Cursor cursor = getContentResolver().query(
		// MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		// projection, selection, null, null);
		//
		// List<String> songs = new ArrayList<String>();
		// while (cursor.moveToNext()) {
		// songs.add(cursor.getString(0) + "||" + cursor.getString(1)
		// + "||" + cursor.getString(2) + "||"
		// + cursor.getString(3) + "||" + cursor.getString(4)
		// + "||" + cursor.getString(5));
		// }
		//
		// return songs;
		// };

		public void playSong(Uri song) {
			// Check if audioThread is initialized
			if (audioThread != null) {
				audioThread.clear();
				audioThread = null;
			}

			if (song == null) {
				Log.d("TAG_ACTIVITY", "RANDOM SONG == NULL");
				return;
			} else {
				play();
				Log.d("TAG_ACTIVITY", "NOT NULL?");
				// Log.d("TAG_ACTIVITY", song.getTitle());
				audioThread.TRACK = song;
			}
		}

		public void playRandomSong() {
			// Check if audioThread is initialized
			if (audioThread != null) {
				audioThread.clear();
				audioThread = null;
			}

			Uri song = mRetriever.getRandomItem().getURI();
			if (song == null) {
				Log.d("TAG_ACTIVITY", "RANDOM SONG == NULL");
				return;
			} else {
				play();
				Log.d("TAG_ACTIVITY", "NOT NULL?");
				// Log.d("TAG_ACTIVITY", song.getTitle());
				audioThread.TRACK = song;
			}
		}

		private class AudioThread extends Thread {

			Uri TRACK;
			boolean ALIVE = true;
			boolean PLAYING = false;
			boolean PAUSED = false;
			boolean REVERSE = false;
			boolean STOPPED = true;
			int PITCHOFFSET = 0;
			boolean CLEAR = false;
			int position = 0;
			int count = 512; // how many bytes to be read at a time
			int TRACKLENGTH;
			RandomAccessFile ra = null;

			AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
					AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, 44100,
					AudioTrack.MODE_STREAM);

			@Override
			public void run() {
				Log.d("TAG_ACTIVITY", "PLAYING");
				try {
					while (TRACK == null) {
						if (CLEAR) {
							return;
						} else {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					prepareAndPlayTrack(TRACK);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			double MAX_OFFSET = 80000.0;

			public void setPitchOffset(double p) {
				if (p == 0.5)
					PITCHOFFSET = 0;
				else if (p < 0.5)
					PITCHOFFSET = (int) ((0.5 - p) * -MAX_OFFSET);
				else
					PITCHOFFSET = (int) ((p - 0.5) * MAX_OFFSET);
				Log.d("TAG_ACTIVITY", Integer.toString(PITCHOFFSET) + " PO");
			}

			public void setReverse(boolean r) {
				REVERSE = r;
			}

			public void unPause() {
				PAUSED = false;
				PLAYING = true;
			}

			public boolean isPaused() {
				return PAUSED;
			}

			public void back() throws IOException {

				boolean wasPaused = audioThread.PAUSED;
				PAUSED = true;
				at.stop();
				at.flush();
				// // if reversed, set to end of song, else set to beginning
				if (REVERSE) {
					audioThread.position = audioThread.TRACKLENGTH - count;
				} else {
					ra.seek(0);
				}
				// //
				at.play();
				PAUSED = wasPaused;
			}

			public void pause() {
				PAUSED = true;
				PLAYING = false;
			}

			public void clear() {
				CLEAR = true;
			}

			// This function reverses the frames in a byte array, assumes frames
			// are 4 bytes long
			public void reverseFrames(byte[] audio) {
				for (int i = 0; i < (Math.log(count) / Math.log(2) - 2); i++) {
					// the log calculations is to calculate how many swaps are
					// needed in a count sized array
					for (int j = 0; j < 4; j++) {
						// grab jth byte in frame A
						byte temp = audio[0 + (i * 4) + j];

						// set jth byte in frame B to jth byte in frame A
						audio[0 + (i * 4) + j] = audio[audio.length - (i * 4)
								- 4 + j];

						// reverse of above
						audio[audio.length - (i * 4) - 4 + j] = temp;
					}
				}
			}

			// Currently unused..
			public void clipAudio(byte[] audio) {
				for (int i = 0; i < audio.length; i++) {
					float data = audio[i];
					// btw can mix samples by just adding them together

					// reduce the volume a bit:
					// float mixed = samplef1;
					// mixed *= 0.8;
					// hard clipping
					// if (data > 1.0f)
					// data = 1.0f;
					// //
					// if (data < -1.0f)
					// data = -1.0f;

					byte output = (byte) (data);
					audio[i] = output;
				}
			}

			public byte[] decode(InputStream inputStream, int startMs, int maxMs)
					throws IOException {
				ByteArrayOutputStream outStream = new ByteArrayOutputStream(
						1024);

				float totalMs = 0;
				boolean seeking = true;

				try {
					Bitstream bitstream = new Bitstream(inputStream);
					Decoder decoder = new Decoder();

					boolean done = false;
					while (!done) {
						Header frameHeader = bitstream.readFrame();
						if (frameHeader == null) {
							done = true;
						} else {
							totalMs += frameHeader.ms_per_frame();

							if (totalMs >= startMs) {
								seeking = false;
							}

							if (!seeking) {
								// logger.debug("Handling header: " +
								// frameHeader.layer_string());
								SampleBuffer output = (SampleBuffer) decoder
										.decodeFrame(frameHeader, bitstream);

								if (output.getSampleFrequency() != 44100
										|| output.getChannelCount() != 2) {
									throw new IllegalArgumentException(
											"mono or non-44100 MP3 not supported");
								}

								short[] pcm = output.getBuffer();
								for (short s : pcm) {
									outStream.write(s & 0xff);
									outStream.write((s >> 8) & 0xff);
								}
							}

							if (totalMs >= (startMs + maxMs)) {
								done = true;
							}
						}
						bitstream.closeFrame();
					}

					return outStream.toByteArray();
				} catch (BitstreamException e) {
					throw new IOException("Bitstream error: " + e);
				} catch (DecoderException e) {
					throw new IOException("Decoder error: " + e);
				}
			}

			public void prepareAndPlayTrack(Uri TRACK) throws IOException {
				if (TRACK == null) {
					Log.d("TAG_ACTIVITY", "No track, returning");
					return;
				}

				if (at == null) {
					Log.d("TAG_ACTIVITY", "audio track is not initialized ");
					return;
				}
				Log.d("TAG_ACTIVITY", "IN PLAY");
				Log.d("TAG_ACTIVITY", TRACK.toString());

				// Reading the file..
				// InputStream in1 = getResources().openRawResource(
				// R.raw.warmwater);
				//
				// byte[] music1 = null;
				// try {
				// music1 = new byte[in1.available()];
				// music1 = convertStreamToByteArray(in1);
				// in1.close();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }

				// InputStream data2 =
				// getResources().openRawResource(R.raw.levels);

				// Get inputstream from uri
				Uri mp3URI = TRACK;
				if (mp3URI == null)
					return;
				InputStream data = getContentResolver().openInputStream(mp3URI);

				// File dataFile = new File(mp3URI.getPath()+".mp3");
				// long dataSize = (long) dataFile.length();
				// Log.d("TAG_ACTIVITY","IS FILE?!"+dataFile.isFile());

				Log.d("TAG_ACTIVITY", "DECODING MP3 TO WAV");
				long start = System.currentTimeMillis();

				// Method to decode all data at once, return the entire byte
				// array
				// byte[] output2 = decode(data, 0, Integer.MAX_VALUE);

				// ----------------------------------
				// Method to decode the data in chunks and write the chunks to
				// file as they are decoded.

				// create new file
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/test.wav";
				File file = null;
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;
				try {
					file = new File(path);
					fos = new FileOutputStream(file);
					bos = new BufferedOutputStream(fos);
				}

				catch (FileNotFoundException fnfe) {
					Log.d("TAG_ACTIVITY", "FILE NOT FOUND" + fnfe);
					return;
				}

				// decode and write to file
				// Log.d("TAG_ACTIVITY", "available=" + dataFile.isFile());
				Log.d("TAG_ACTIVITY", mp3URI.toString());

				int ite = 44100;
				// int chunkSize = 1024;
				// int iterSize = 0;
				// int sum = 0;
				Log.d("TAG_ACTIVITY", "data length " + data.available());
				while (ite < data.available()) {
					Log.d("TAG_ACTIVITY", "ITE = " + ite);
					byte[] output = decode(data, 0, ite);
					if (output == null) {
						Log.d("TAG_ACTIVITY", "NULL OUTPUT, DONE HERE");
						break;
					} else {
						// write to file
						try {
							// Log.d("TAG_ACTIVITY", "decoding from = " + ite +
							// " to "+(ite+chunkSize));
							// Log.d("TAG_ACTIVITY", "outLen = " +
							// output.length);
							bos.write(output);
							Log.d("TAG_ACTIVITY", "" + (output.length / 44100)
									* 1000);
							// sum += chunkSize;
							Log.d("TAG_ACTIVITY",
									"data length " + data.available());
							// Log.d("TAG_ACTIVITY", "ITE = "+ite);
							// ite = outLen;

						} catch (IOException e) {
							break;
						}
					}
				}

				// Log.d("TAG_ACTIVITY", "sum "+sum);
				Log.d("TAG_ACTIVITY", "data length " + data.available());

				// byte[] output2 = decode(data, 0, 1);

				// close the stream
				try {
					if (bos != null) {
						bos.flush();
						bos.close();
					}
				} catch (Exception e) {
					System.out.println("Error while closing streams" + e);
				}

				// ----------------------------------

				long end = System.currentTimeMillis();
				long total = (end - start) / 1000;
				Log.d("TAG_ACTIVITY", "DECODING TIME = " + Long.toString(total));

				// TODO: Run decode on only a portion of the song so that
				// playback can begin instantly
				// Then, get the rest (on a separate thread?) and feed back
				// somehow or maybe write
				// to a file? Probably not the file. This does make seeking much
				// harder/impossible
				// while the song is being decoded, but could just only let you
				// seek up to the place
				// where the buffer has loaded? That could work.

				// TODO: Use a thread in the background prepare the next song.

				// byte[] output = new byte[music1.length];
				play(path);
			}

			public void play(String path) throws IOException {
				int count = 512; // 512 kb
				int position = 0;
				// Reading the file..
				byte[] byteData = null;
				File file = null;
				file = new File(path);

				byteData = new byte[(int) count];
				// FileInputStream in = null;
				try {
					ra = new RandomAccessFile(file, "r");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// try {
				// in = new FileInputStream(file);
				//
				// } catch (FileNotFoundException e) {
				// e.printStackTrace();
				// }

				int ret = 0;
				int size = (int) file.length();
				at.play();

				at.setPlaybackRate(44100 + PITCHOFFSET);
				while (position < size && position >= 0) {

					// check paused
					if (PAUSED) {
						continue;
					}

					// Log.d("TAG_ACTIVITY", "" + position);

					try {
						if (REVERSE) {
							ra.seek(position); // IS COUNT = RET SIZE?
						}
						ret = ra.read(byteData, 0, count);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (ret != -1) {
						// Write the byte array to the track
						if (REVERSE) {
							reverseFrames(byteData);
							position -= byteData.length;
						} else {
							position += byteData.length;
						}

						at.setPlaybackRate(44100 + PITCHOFFSET);
						at.write(byteData, 0, ret);
						Log.d("TAG_ACTIVITY", Integer.toString(position));

					} else
						break;
				}
				try {
					ra.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				at.stop();
				at.release();
			}
			// public void play2(com.example.MusicRetriever.MusicRetriever.Item
			// TRACK) {
			// //Play from byte array
			//
			//
			// byte[] output = output2;
			// TRACKLENGTH = output.length;
			//
			// at.play();
			// Log.d("TAG_ACTIVITY", "2");
			//
			// boolean continuePlaying = true;
			// while (continuePlaying) {
			//
			// if (!(position <= output.length && position >= 0)) {
			// // reached end/beginning of song
			// PAUSED = true;
			// }
			//
			// // now we're playing, should do necessary checks for pause,
			// // stop, rewind, modify, etc.
			//
			// // case where we want to kill the song completely and stop
			// // the player
			// if (CLEAR) {
			// Log.d("TAG_ACTIVITY", "CLEAR");
			// at.stop();
			// at.release();
			// return;
			// }
			//
			// if (PAUSED) {
			// at.pause();
			// Log.d("TAG_ACTIVITY", "PAUSED");
			// boolean stayPaused = true;
			// while (stayPaused) {
			// if (CLEAR) {
			// at.stop();
			// at.release();
			// return;
			// } else if (!(PAUSED)) {
			// if (position <= 0 && !REVERSE) {
			// stayPaused = false;
			// position = 0;
			// Log.d("TAG_ACTIVITY",
			// "UNPAUSED, going forwards");
			// } else if (position >= output.length && REVERSE) {
			// stayPaused = false;
			// position = output.length - count;
			// Log.d("TAG_ACTIVITY",
			// "UNPAUSED, going backwards");
			// } else if (position >= 0
			// && position <= output.length - count) {
			// stayPaused = false;
			// }
			// } else {
			// try {
			// Thread.sleep(100);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// }
			// }
			// at.play();
			//
			// }
			// // TODO: if possible, don't read whole song into memory
			// // // Write the byte array to the track
			// at.setPlaybackRate(44100 + PITCHOFFSET);
			// byte[] newArray;
			// newArray = Arrays.copyOfRange(output, position, position
			// + count);
			// if (REVERSE) {
			// reverseFrames(newArray);
			// position -= count;
			// } else {
			// position += count;
			// }
			//
			// clipAudio(newArray);
			//
			// at.write(newArray, 0, count);
			// // playbackRate += 500;
			//
			// }
			// at.stop();
			// // at.flush();
			// at.release();
			// PLAYING = false;
			// }

		}
	}
}

// TODO: Some sort of indicator for song speed i.e. normal, 1.3x...
// TODO: Add current song info to player
// TODO: Make play and pause one button
// TODO: Add Play, Pause, Back, Forward icons.
// TODO: Make sure the selected audio file is an MP3.
// TODO: Make a waiting animation while the song is decoding.
// TODO: Maybe add a volume control to the AudioTrack bit, in case
// the synth samples are too loud/soft in comparison to the AT.

// TODO: Maybe fix that decoding/saving to file blip..
// TODO: Load next song in new thread