package com.example.senuti;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

public class BeatFragment extends Fragment {

	OnBeatPlayedListener mCallback;
	ArrayList<Button> buttons;
	Switch editSwitch;
	ArrayList<BeatItem> items;
	boolean recording;
	boolean editing; //if editing == true, then when you push a button, implicit intent fires to select which sound to bind
	

    // Container Activity must implement this interface
    public interface OnBeatPlayedListener {
        public MediaPlayer playDefaultSound(MediaPlayer mp, int rid);
        public void playCustomSound(MediaPlayer mp, String filename);
		public void stopMP3(MediaPlayer mediaPlayer);
    }
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnBeatPlayedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);	
    }
    
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.activity_main, container, false);
		Log.d("TAG_ACTIVITY", "In beat fragment, getting ready for buttons n stuff");
		
		recording = false;
		editing = false;
		//Bind button click to playMP3()
		
		editSwitch = (Switch) rootView.findViewById(R.id.edit_mode);
		editSwitch.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// Is the toggle on?
				boolean on = ((Switch) v).isChecked();

				if (on) {
					editing = true;
				} else {
					editing = false;
				}
				
			}
		});
		
				buttons = new ArrayList<Button>();
				items = new ArrayList<BeatItem>();

				 Button playButton = (Button) rootView.findViewById(R.id.btnPlay1);
				 buttons.add(playButton);
				 Button playButton2 = (Button) rootView.findViewById(R.id.btnPlay2);
				 buttons.add(playButton2);
				 Button playButton3 = (Button) rootView.findViewById(R.id.btnPlay3);
				 buttons.add(playButton3);
				 Button playButton4 = (Button) rootView.findViewById(R.id.btnPlay4);
				 buttons.add(playButton4);
				 Button playButton5 = (Button) rootView.findViewById(R.id.btnPlay5);
				 buttons.add(playButton5);
				 Button playButton6 = (Button) rootView.findViewById(R.id.btnPlay6);
				 buttons.add(playButton6);
				 Button playButton7 = (Button) rootView.findViewById(R.id.btnPlay7);
				 buttons.add(playButton7);
				 Button playButton8 = (Button) rootView.findViewById(R.id.btnPlay8);
				 buttons.add(playButton8);
				 Button playButton9 = (Button) rootView.findViewById(R.id.btnPlay9);
				 buttons.add(playButton9);
				 

				 
				 //create items that hold all the invformation
				 items.add(new BeatItem(buttons.get(0),new MediaPlayer(), "1", new MediaRecorder(),R.raw.sound1));
				 items.add(new BeatItem(buttons.get(1),new MediaPlayer(), "2", new MediaRecorder(),R.raw.sound2));
				 items.add(new BeatItem(buttons.get(2),new MediaPlayer(), "3", new MediaRecorder(),R.raw.sound3));
				 items.add(new BeatItem(buttons.get(3),new MediaPlayer(), "4", new MediaRecorder(),R.raw.sound4));
				 items.add(new BeatItem(buttons.get(4),new MediaPlayer(), "5", new MediaRecorder(),R.raw.sound5));
				 items.add(new BeatItem(buttons.get(5),new MediaPlayer(), "6", new MediaRecorder(),R.raw.sound6));
				 items.add(new BeatItem(buttons.get(6),new MediaPlayer(), "7", new MediaRecorder(),R.raw.sound7));
				 items.add(new BeatItem(buttons.get(7),new MediaPlayer(), "8", new MediaRecorder(),R.raw.sound8));
				 items.add(new BeatItem(buttons.get(8),new MediaPlayer(), "9", new MediaRecorder(),R.raw.sound9));
				 //for now, just puts in the same song saved 9 times as the sound. 
				 for (int i = 0; i < buttons.size(); i++)
				 {
					 
					 //once an item has a media player, we can go ahead and set its listener
					 setButtonListener(items.get(i));
				}
				int blah;
		return rootView;
		
	}
	
	//wrapper method for settting motion listener for the buttons with added functionality of specific sound/action
	public void setButtonListener(final BeatItem item)
	{
		item.getButton().setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Button b = item.getButton();
				if(editing)
				{
					//when editing, start recording sound on touch, stop on touch release
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						b.setPressed(true);
						if(!recording)
						{
							startRecording(item);
							recording = true;
							
						}
					}else
						if(event.getAction() == MotionEvent.ACTION_UP){
							b.setPressed(false);
							stopRecording(item);
							recording = false;
							
						}
				}else
				{
					//when not editing, play sound on touch, stop playing on release
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						b.setPressed(true);
						 if(item.getId()==15)
						 {
							 mCallback.playCustomSound(item.getPlayer(), item.getFileName());
							
						 }else
						 {
							 item.setPlayer(mCallback.playDefaultSound(item.getPlayer(), item.getId()));
							
						 }
					 } else if (event.getAction() == MotionEvent.ACTION_UP) {
						 b.setPressed(false);
						 MediaPlayer player = item.getPlayer();
					// mCallback.stopMP3(player);//stopMP3 calls mp. release
					 if(item.getPlayer()!=null)
					 {
						 
						 if(player.isPlaying())
						 {
							 Log.d("TAG_ACTIVITY", "really, telling it to stop");
							 player.stop();
							 player.release();
							 item.setPlayer(new MediaPlayer());
						 }
					 }
					 else
					 {
						 Log.d("TAG_ACTIVITY", "null media player?");
					 }
					 
					 }
				}
				item.setButton(b);
				return false;
			}
		});
	}
	
	private void startRecording(BeatItem item) {
		try {
			MediaRecorder mRecorder = item.getRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			Log.d("TAG_ACTIVITY", item.getFileName());
			File outputFile = new File(item.getFileName());
			if (outputFile.exists()){
				Log.d("TAG_ACTIVITY","deleting existing file");
				outputFile.delete();
			}
	    	if(outputFile.createNewFile())
	    		Log.d("TAG_ACTIVITY","created a new file");
			mRecorder.setOutputFile(item.getFileName());
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.prepare();
			mRecorder.start();
			item.setId(15);//ids are checked, 15 is bad iguess
		} catch (IOException e) {
			Log.e("TAG_ACTIVITY", "prepare() failed");
		}
	}

	private void stopRecording(BeatItem item) {
		MediaRecorder mRecorder = item.getRecorder();
		try {
		    mRecorder.stop();
		} catch(RuntimeException e) {
		     //you must delete the outputfile when the recorder stop failed.
		} finally {
		    mRecorder.release();
		    mRecorder = null;
		}
		item.setRecorder(new MediaRecorder());
	}
	
	
	public class BeatItem{
		public Button button;
		public MediaPlayer mp;
		public String fileName;
		public MediaRecorder mr;
		public int id;
		
		public BeatItem(Button b, MediaPlayer m, String file, MediaRecorder mr, int theId)
		{
			button = b;
			mp = m;
			fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
	        fileName+= "/senutiCustomSound";
			fileName+= file + ".3gp";//add the last custom bit we want
			id = theId;
			this.mr = mr;
		}
		
		public void setId(int i) {
			id = i;
			
		}

		public void setRecorder(MediaRecorder mediaRecorder) {
		
			mr = mediaRecorder;
		}

		public int getId() {
			return id;
		}

		public Button getButton()
		{
			return button;
		}
		
		public void setButton(Button b)
		{
			button = b;
		}
		
		public void setPlayer(MediaPlayer m)
		{
			mp = m;
		}
		
		public MediaRecorder getRecorder()
		{
			return mr;
		}
		
		public MediaPlayer getPlayer()
		{
			return mp;
		}
		
		public String getFileName()
		{
			return fileName;
		}
	}
	
	
}