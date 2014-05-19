package com.example.senuti;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

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
	BeatFragment beatPad;
	PlayFragment playFrag;
	boolean songReady = false;
	List<Uri> blackListedSongs = new ArrayList();

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
		// device. we used some layout aliases to make this easier

		if (findViewById(R.id.fragment_container_one) != null) {
			Log.d("TAG_ACTIVITY", "fragment container one not null");

			// Create a new Fragment to be placed in the activity layout
			playFrag = new PlayFragment();
			// playFrag.setLoading(false);
			beatPad = new BeatFragment();
			// Add the fragment to the 'fragment_container' FrameLayout
			getFragmentManager().beginTransaction()
					.add(R.id.fragment_container_one, playFrag).commit();
			View secondFrag = findViewById(R.id.fragment_container_two);

			isDualPane = (secondFrag != null && secondFrag.getVisibility() == View.VISIBLE);

			if (isDualPane) {
				Log.d("TAG_ACTIVITY", "Dual pane mode.");

				getFragmentManager().beginTransaction()
						.add(R.id.fragment_container_two, beatPad).commit();
				Log.d("TAG_ACTIVITY", "added second fragment");
			} else {
				nextFrag = 1;
			}
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
	Toast toast;

	public void makeToast(String str) {
		Context context = getApplicationContext();
		CharSequence text = str;
		int duration = Toast.LENGTH_LONG;

		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	void playSong(Uri uri) {
		atp.playSong(uri);
	}

	public boolean checkSongReady() {
		return songReady;
	}

	void setPlaying(final boolean playing) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// This code will always run on the UI thread, therefore is safe
				// to modify UI elements.
				playFrag.setPlaying(playing);
			}
		});
	}

	void setSongTitle(final String title) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// This code will always run on the UI thread, therefore is safe
				// to modify UI elements.
				playFrag.setSongTitle(title);
			}
		});
	}

	void setDecodeProgress(int prog) {
		playFrag.setDecodeProgress(prog);
	}

	void setSongProgress(int prog) {
		playFrag.setSongProgress(prog);
	}

	void setLoading(boolean loading) {
		playFrag.setLoading(loading);
	}

	void enableReverseSwitch(boolean s) {
		playFrag.enableReverseSwitch(s);
	}

	public boolean isPlaying() {
		return atp.isPlaying();
	}

	void setPitchOffsetLabel(int amount) {
		playFrag.setPitchOffsetLabel(amount);
	}

	// this method lets you switch fragments within one pane back and forth
	public void showFragment(int fragment) {
		FragmentManager fm = getFragmentManager();
		android.app.FragmentTransaction ft = fm.beginTransaction();
		if (fragment == 1) {
			nextFrag = 0;
			// Bundle args = new Bundle();
			// dont have any args at the moment
			// bf.setArguments(args);

			ft.replace(R.id.fragment_container_one, beatPad);
			ft.addToBackStack(null);
			ft.commit();
		} else if (fragment == 0) {
			nextFrag = 1;
			// PlayFragment pf = new PlayFragment();

			// dont have any args at the moment
			// newFragment.setArguments(args);

			ft.replace(R.id.fragment_container_one, playFrag);
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

				// now try to get the name of the mp3 file
				String path = getRealPathFromURI(getApplicationContext(), uri);
				int pos = path.lastIndexOf("/") + 1;
				Log.d("TAG_ACTIVITY", "" + pos + " " + path);
				if (pos == -1) {
					Log.d("TAG_ACTIVITY", "NO /");
					makeToast("Unable to open " + path);
					blackListedSongs.add(uri);
					return;
				}
				String filename = path.substring(pos, path.length());
				if (filename.lastIndexOf(".") == -1) {
					Log.d("TAG_ACTIVITY",
							"NO . extension in filename, unable to open");
					makeToast("Unable to open " + path);
					blackListedSongs.add(uri);
					return;
				}
				String[] splits = filename.split("\\.");
				Log.d("TAG_ACTIVITY", splits[0]);
				if (splits.length != 2) {
					Log.d("TAG_ACTIVITY", "SPLITS != 2" + splits[0] + splits[1]);
					blackListedSongs.add(uri);
					return;
				} else {
					Log.d("TAG_ACTIVITY", "CHECK5");
					String extention = splits[1];
					String mp3String = "mp3";
					Log.d("TAG_ACTIVITY", "FILETYPE = " + extention);
					if (!(extention.equals(mp3String))) {
						// TODO: make this into a toast!
						Log.d("TAG_ACTIVITY",
								"INVALID FILE TYPE, TRY AGAIN, FILETYPE = "
										+ extention);
						makeToast("Please select a song in mp3 format");
						blackListedSongs.add(uri);
						return;
					} else {
						String songTitle = splits[0];

						Log.d("TAG_ACTIVITY", "SONG TITLE = " + songTitle);
						setSongTitle(songTitle);
						atp.playSong(uri);
					}
				}
			}
		}
		}
	}

	// launcher method to implement interface so UI can be put in fragment
	public void setReverse(boolean dir) {
		if (atp == null) {
			return;
		}
		atp.setReverse(dir);
	}

	public void play() {
		atp.play();
	}

	public void pause() {
		atp.pause();
	}

	public void seek(double sliderval) {
		if (atp == null) {
			return;
		}
		if (!(songReady)) {
			return;
		} else {
			atp.setPosition(sliderval);
		}
	}

	public void setPitch(double sliderVal) {
		if (atp == null) {
			return;
		}
		atp.setPitch(sliderVal);
	}

	public void stopMP3(MediaPlayer mp) {
		if (mp == null) {

		} else {
			if (mp.isPlaying()) {
				Log.d("TAG_ACTIVITY",
						"media player is playing, preparing to stop and release");
				mp.stop();
				mp.release();
				mp = null;
			}
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

	public MediaPlayer playDefaultSound(MediaPlayer mp, int rid) {
		if (mp == null) {
			return null;
		}

		if (mp.isPlaying()) {
			return mp;
		}
		try {
			Log.d("TAG_ACTIVITY", "in default play");
			mp = MediaPlayer.create(this, rid);

			// MediaPlayer mp = new MediaPlayer();
			// mp.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath()+"/serenity.mp3");
			mp.setOnPreparedListener(this);
			return mp;
			// mp.prepare();

		} catch (Exception e) {
		}
		return null;
	}

	public void playCustomSound(MediaPlayer mp, String filename) {
		if (mp == null) {

		} else {
			try {
				Log.d("TAG_ACTIVITY", "in custom play?");
				mp.setDataSource(filename);
				mp.prepare();
				mp.start();
			} catch (Exception e) {
			}
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

		public void setPosition(double p) {
			if (audioThread == null) {
				return;
			}
			audioThread.setPosition(p);
		}

		public void setPitch(double p) {
			if (audioThread == null) {
				return;
			}
			audioThread.setPitchOffset(p);
		}

		public void setReverse(boolean r) {
			if (audioThread == null) {
				return;
			}
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

		public boolean isPlaying() {
			return (!(audioThread.isPaused()));
		}

		public byte[] decode(InputStream inputStream, int startMs, int maxMs)
				throws IOException {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);

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

		DecodeMp3Thread task;

		public boolean cancelDecode() {
			enableReverseSwitch(true);
			return task.cancel(true);

		}

		public void playSong(Uri song) {
			// Check if audioThread is initialized
			if (audioThread != null) {
				audioThread.clear();
				audioThread = null;
			}

			if (task != null) {
				cancelDecode();
			}

			if (song == null) {
				Log.d("TAG_ACTIVITY", "RANDOM SONG == NULL");
				return;
			} else {
				// play audiotrack thread
				task = new DecodeMp3Thread();
				task.execute(song);
			}
		}

		// TODO: maybe make this async since it could potentially take time?
		public void playRandomSong() {
			// Check if audioThread is initialized
			if (audioThread != null) {
				audioThread.clear();
				audioThread = null;
			}

			if (task != null) {
				cancelDecode();
			}

			com.example.MusicRetriever.MusicRetriever.Item song = mRetriever
					.getRandomItem();

			int numberSongs = mRetriever.getMediaSize();
			Log.d("TAG_ACTIVITY", "NUM SONGS = " + numberSongs);
			Uri songUri = song.getURI();

			boolean found = false;

			while (!(found)) {

				// if in blacklist, recurse and try again. If we see everything
				// in
				// the
				if (blackListedSongs.contains(songUri)) {
					Log.d("TAG_ACTIVITY", "BLACKLISTED SONG" + songUri);
					// if blacklistedsongs contains all possible songs, return,
					// nothing to play
					if (blackListedSongs.size() == numberSongs) {
						makeToast("No mp3 files found on your device");
						return;
					} else {
						song = mRetriever
								.getRandomItem();
						songUri = song.getURI();
						continue;
					}
				}

				Log.d("TAG_ACTIVITY", "SONG TITLE = " + song.getTitle());
				if (songUri == null) {
					Log.d("TAG_ACTIVITY", "RANDOM SONG == NULL");
					blackListedSongs.add(songUri);
					//TODO: this probably needs some more thinking through...
					song = mRetriever
							.getRandomItem();
					songUri = song.getURI();
					continue;
				} else {
					// CHECK VALID FILE
					// TODO: repeated code, put into function...
					// now try to get the name of the mp3 file
					String path = getRealPathFromURI(getApplicationContext(),
							songUri);
					int pos = path.lastIndexOf("/") + 1;
					Log.d("TAG_ACTIVITY", "" + pos + " " + path);
					if (pos == -1) {
						Log.d("TAG_ACTIVITY", "NO /");
						blackListedSongs.add(songUri);
						song = mRetriever
								.getRandomItem();
						songUri = song.getURI();
						continue;
					}
					String filename = path.substring(pos, path.length());
					if (filename.lastIndexOf(".") == -1) {
						Log.d("TAG_ACTIVITY",
								"NO . extension in filename, unable to open");
						blackListedSongs.add(songUri);
						song = mRetriever
								.getRandomItem();
						songUri = song.getURI();
						continue;
					}
					String[] splits = filename.split("\\.");
					Log.d("TAG_ACTIVITY", splits[0]);
					if (splits.length != 2) {
						Log.d("TAG_ACTIVITY", "SPLITS != 2" + splits[0]
								+ splits[1]);
						blackListedSongs.add(songUri);
						song = mRetriever
								.getRandomItem();
						songUri = song.getURI();
						continue;
					} else {
						Log.d("TAG_ACTIVITY", "CHECK5");
						String extention = splits[1];
						String mp3String = "mp3";
						Log.d("TAG_ACTIVITY", "FILETYPE = " + extention);
						if (!(extention.equals(mp3String))) {
							// TODO: make this into a toast!
							Log.d("TAG_ACTIVITY",
									"INVALID FILE TYPE, TRY AGAIN, FILETYPE = "
											+ extention);
							blackListedSongs.add(songUri);
							song = mRetriever
									.getRandomItem();
							songUri = song.getURI();
							continue;
						} else {
							Log.d("TAG_ACTIVITY",
									"SONG TITLE = " + song.getTitle());
							setSongTitle(song.getTitle());
							task = new DecodeMp3Thread();
							task.execute(songUri);
							found = true;
						}
					}
				}

			}
		}

		private class DecodeMp3Thread extends AsyncTask<Uri, Integer, String> {

			@Override
			protected void onPreExecute() {
				setLoading(true);
				songReady = false;
				enableReverseSwitch(false);
			}

			@Override
			protected String doInBackground(Uri... params) {
				Uri TRACK = params[0];
				if (TRACK == null) {
					Log.d("TAG_ACTIVITY", "No track, returning");
					return null;
				}

				Log.d("TAG_ACTIVITY", "Decoding " + TRACK.toString());

				// Get inputstream from uri
				Uri mp3URI = TRACK;

				InputStream data;
				try {
					data = getContentResolver().openInputStream(mp3URI);
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					return null;
				}

				Log.d("TAG_ACTIVITY", "DECODING MP3 TO WAV");
				long start = System.currentTimeMillis();

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
					return null;
				}

				// decode and write to file
				Log.d("TAG_ACTIVITY", mp3URI.toString());
				int dataLength = 1;
				try {
					dataLength = data.available();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				int progress = 0;
				int ite = 44100;
				try {
					while (ite < data.available()) {
						Log.d("TAG_ACTIVITY", "ITE = " + ite);
						byte[] output = decode(data, 0, ite);
						if (output == null) {
							Log.d("TAG_ACTIVITY", "NULL OUTPUT, DONE HERE");
							break;
						} else {
							// write to file
							try {
								bos.write(output);
								Log.d("TAG_ACTIVITY", ""
										+ (output.length / 44100) * 1000);
								Log.d("TAG_ACTIVITY",
										"data length " + data.available());
								progress += output.length;
								publishProgress((int) ((float) progress / (float) dataLength) * 10);
							} catch (IOException e) {
								break;
							}
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

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
				return path;
			}

			protected void onProgressUpdate(Integer... progress) {
				Log.d("TAG_ACTIVITY", "progress = " + progress[0]);
				setDecodeProgress(progress[0]);

			}

			@Override
			protected void onPostExecute(String song) {
				setLoading(false);
				if (song == null) {
					Log.d("TAG_ACTIVITY", "NO SONG FROM DECODE!");
					return;
				}
				play();
				Log.d("TAG_ACTIVITY", "SUCCESSFUL DECODING, NOW PLAY");
				// Log.d("TAG_ACTIVITY", song.getTitle());
				audioThread.TRACK = song;

			}
		}

		private class AudioThread extends Thread {

			String TRACK;
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
					play(TRACK);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void setPosition(double p) {

				boolean wasPaused = audioThread.PAUSED;
				PAUSED = true;

				position = (int) (((float) p * (float) TRACKLENGTH));
				Log.d("TAG_ACTIVITY", "TRACKLENGTH = " + TRACKLENGTH);
				Log.d("TAG_ACTIVITY", "P =  " + p);
				Log.d("TAG_ACTIVITY", "POSITION SET TO " + position);

				if (at != null) {
					at.pause();
					at.flush();
				}

				try {
					ra.seek(position);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (at != null) {
					at.play();
				}

				PAUSED = wasPaused;

			}

			// max of the pitch offset (from 0%)
			double MAX_OFFSET = 88200.0;

			public void setPitchOffset(double p) {
				if (p == 0.5)
					PITCHOFFSET = 0;
				else if (p < 0.5)
					PITCHOFFSET = (int) ((0.5 - p) * -MAX_OFFSET);
				else
					PITCHOFFSET = (int) ((p - 0.5) * MAX_OFFSET);
				// Log.d("TAG_ACTIVITY", Integer.toString(PITCHOFFSET) + " PO");
				int offsetPercent = (int) (PITCHOFFSET / (MAX_OFFSET / 2) * 100);
				setPitchOffsetLabel(offsetPercent);

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

				// // if reversed, set to end of song, else set to beginning
				if (REVERSE) {
					// Log.d("TAG_ACTIVITY","TrackLength = " +TRACKLENGTH );
					position = TRACKLENGTH - count;
				} else {
					ra.seek(0);
					position = 0;
				}
				if (at != null) {
					at.stop();
					at.flush();
					at.play();
				}
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
				for (int i = 0; i <= (count / 8) - 1; i++) {
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

			public void play(String path) throws IOException {
				int count = 512; // 512 kb
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

				int ret = 0;
				int size = (int) file.length();
				at.play();

				at.setPlaybackRate(44100 + PITCHOFFSET);
				TRACKLENGTH = size;
				boolean continuePlaying = true;

				// NOW set songReady and enable clicking.. same thing?
				songReady = true;
				enableReverseSwitch(true);

				while (continuePlaying) {
					if (CLEAR) {
						Log.d("TAG_ACTIVITY", "CLEAR");
						at.stop();
						at.release();
						setPlaying(false);
						return;
					}

					if (!(position <= size && position >= 0)) {
						PAUSED = true;
						setPlaying(false);
					}
					// check paused
					if (PAUSED) {
						setPlaying(false);
						continue;
					}

					try {
						if (REVERSE) {
							ra.seek(position); // IS COUNT = RET SIZE?
							// Log.d("TAG_ACTIVITY",
							// Integer.toString(position));
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
						setPlaying(true);

						setSongProgress((int) ((((float) position / (float) size)) * 100));
						// Log.d("TAG_ACTIVITY", "Pos " + position);
						// Log.d("TAG_ACTIVITY", "Size" + size);

						// Log.d("TAG_ACTIVITY",
						// ""
						// + (int) ((((float) position / (float) size)) * 100));

					} else {
						Log.d("TAG_ACTIVITY", "BREAKING FROM SONG");
						break;
					}

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
		}
	}

}

// TODO: Maybe add a volume control to the AudioTrack bit, in case
// the synth samples are too loud/soft in comparison to the AT.

// TODO: Maybe fix that decoding/saving to file blip..
// TODO: Load next song in new thread
// TODO: Make UI better.