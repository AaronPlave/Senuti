package com.example.senuti;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;

import com.example.MusicRetriever.MusicRetriever;
import com.example.MusicRetriever.PrepareMusicRetrieverTask;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnPreparedListener,
		PrepareMusicRetrieverTask.MusicRetrieverPreparedListener {

	ArrayList<MediaPlayer> mediaPlayers;
	ArrayList<Button> buttons;
	double sliderval;
	boolean musicRetrieverReady = false;
	MusicRetriever mRetriever;

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
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);

		setContentView(R.layout.effects_layout);

		// Create the retriever and start an asynchronous task that will prepare
		// it.

		mRetriever = new MusicRetriever(getContentResolver());
		AsyncTask<Void, Void, Void> execute = new PrepareMusicRetrieverTask(
				mRetriever, this);
		execute.execute();

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

		// Bind the random button click
		Button randomButton = (Button) findViewById(R.id.btnRandom);
		randomButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (musicRetrieverReady) {
					atp.playRandomSong();
				} else {
					Log.d("TAG_ACTIVITY",
							"NOT DONE RETRIEVING, should set spinner.");
				}
				// String songs = atp.getSongsOnDevice().toString();

				// Log.d("TAG_ACTIVITY","END OF SONGS");
			}
		});

		// Bind the pitch slider for AT
		SeekBar pitchSlider = (SeekBar) findViewById(R.id.pitchSlider);
		// create a listener for the slider bar;
		OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					Log.d("TAG_ACTIVITY", Integer.toString(progress));
					sliderval = progress / (double) seekBar.getMax();
					Log.d("TAG_ACTIVITY", Double.toString(sliderval));
					atp.setPitch(sliderval);

				}
			}
		};

		// set the listener on the slider
		pitchSlider.setOnSeekBarChangeListener(listener);

		// Bind the reverse toggle for AT
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
				atp.back();

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

	// public static void writeByteArrayToFile(byte[] byteArray,
	// FileOutputStream fos, BufferedOutputStream bos, int start, int end) {
	// try {
	// if (bos != null){
	// bos.write(byteArray,start,end);
	// }
	// }
	// }

	public static class Item {
		long id;
		String artist;
		String title;
		String album;
		long duration;

		public Item(long id, String artist, String title, String album,
				long duration) {
			this.id = id;
			this.artist = artist;
			this.title = title;
			this.album = album;
			this.duration = duration;
		}

		public long getId() {
			return id;
		}

		public String getArtist() {
			return artist;
		}

		public String getTitle() {
			return title;
		}

		public String getAlbum() {
			return album;
		}

		public long getDuration() {
			return duration;
		}

		public Uri getURI() {
			return ContentUris
					.withAppendedId(
							android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							id);
		}
	}

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

		public void back() {
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

		public void playRandomSong() {
			// Check if audioThread is initialized
			if (audioThread != null) {
				audioThread.clear();
				audioThread = null;
			}

			com.example.MusicRetriever.MusicRetriever.Item song = mRetriever
					.getRandomItem();
			if (song == null) {
				Log.d("TAG_ACTIVITY", "RANDOM SONG == NULL");
				return;
			} else {
				play();
				Log.d("TAG_ACTIVITY", "NOT NULL?");
				Log.d("TAG_ACTIVITY", song.getTitle());
				audioThread.TRACK = song;
			}
		}

		private class AudioThread extends Thread {

			com.example.MusicRetriever.MusicRetriever.Item TRACK;
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

			double MAX_OFFSET = 48000.0;

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

			public void back() {

				boolean wasPaused = audioThread.PAUSED;
				PAUSED = true;
				at.stop();
				at.flush();
				// if reversed, set to end of song, else set to beginning
				if (REVERSE) {
					audioThread.position = audioThread.TRACKLENGTH - count;
				} else {
					audioThread.position = 0;
				}
				//
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
			public void prepareAndPlayTrack(com.example.MusicRetriever.MusicRetriever.Item TRACK) throws IOException {
				play(TRACK);
			}
			public void play(
					com.example.MusicRetriever.MusicRetriever.Item TRACK)
					throws IOException {

				if (TRACK == null) {
					Log.d("TAG_ACTIVITY", "No track, returning");
					return;
				}

				if (at == null) {
					Log.d("TAG_ACTIVITY", "audio track is not initialised ");
					return;
				}
				Log.d("TAG_ACTIVITY", "IN PLAY");
				Log.d("TAG_ACTIVITY", TRACK.getURI().toString());

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
				Uri mp3URI = TRACK.getURI();
				if (mp3URI == null)
					return;
				InputStream data = getContentResolver().openInputStream(mp3URI);

				Log.d("TAG_ACTIVITY", "DECODING MP3 TO WAV");
				long start = System.currentTimeMillis();

				// Method to decode all data at once, return the entire byte
				// array
				// byte[] output2 = decode(data, 0, Integer.MAX_VALUE);

				// ----------------------------------
				// Method to decode the data in chunks and write the chunks to
				// file as they are decoded.
				int ite = 0;
				int chunkSize = Integer.MAX_VALUE;

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

				int fIte = 0;

				while (ite < Integer.MAX_VALUE) {
					byte[] output = decode(data, ite, ite + chunkSize);
					if (output == null) {
						Log.d("TAG_ACTIVITY", "NULL OUTPUT, DONE HERE");
						break;
					} else {
						// write to file
						try {
							Log.d("TAG_ACTIVITY", "ite = " + ite);
							Log.d("TAG_ACTIVITY", "outLen = " + output.length);
							bos.write(output);
							ite += chunkSize;
							fIte += output.length;
							
						} catch (IOException e) {
							break;
						}
					}
				}
				
				Log.d("TAG_ACTIVITY", "bos len");

				byte[] output2 = decode(data, 0, 1);

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

				byte[] output = output2;
				TRACKLENGTH = output.length;

				at.play();
				Log.d("TAG_ACTIVITY", "2");

				boolean continuePlaying = true;
				while (continuePlaying) {

					if (!(position <= output.length && position >= 0)) {
						// reached end/beginning of song
						PAUSED = true;
					}

					// now we're playing, should do necessary checks for pause,
					// stop, rewind, modify, etc.

					// case where we want to kill the song completely and stop
					// the player
					if (CLEAR) {
						Log.d("TAG_ACTIVITY", "CLEAR");
						at.stop();
						at.release();
						return;
					}

					if (PAUSED) {
						at.pause();
						Log.d("TAG_ACTIVITY", "PAUSED");
						boolean stayPaused = true;
						while (stayPaused) {
							if (CLEAR) {
								at.stop();
								at.release();
								return;
							} else if (!(PAUSED)) {
								if (position <= 0 && !REVERSE) {
									stayPaused = false;
									position = 0;
									Log.d("TAG_ACTIVITY",
											"UNPAUSED, going forwards");
								} else if (position >= output.length && REVERSE) {
									stayPaused = false;
									position = output.length - count;
									Log.d("TAG_ACTIVITY",
											"UNPAUSED, going backwards");
								} else if (position >= 0
										&& position <= output.length - count) {
									stayPaused = false;
								}
							} else {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						at.play();

					}
					// TODO: if possible, don't read whole song into memory
					// // Write the byte array to the track
					at.setPlaybackRate(44100 + PITCHOFFSET);
					byte[] newArray;
					newArray = Arrays.copyOfRange(output, position, position
							+ count);
					if (REVERSE) {
						reverseFrames(newArray);
						position -= count;
					} else {
						position += count;
					}

					clipAudio(newArray);

					at.write(newArray, 0, count);
					// playbackRate += 500;

				}
				at.stop();
				// at.flush();
				at.release();
				PLAYING = false;
			}
		};

	}

}
