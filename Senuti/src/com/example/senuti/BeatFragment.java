package com.example.senuti;


import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

public class BeatFragment extends Fragment {

	OnBeatPlayedListener mCallback;
	ArrayList<MediaPlayer> mediaPlayers;
	ArrayList<Button> buttons;
	ArrayList<BeatItem> items;
	boolean editing; //if editing == true, then when you push a button, implicit intent fires to select which sound to bind
	

    // Container Activity must implement this interface
    public interface OnBeatPlayedListener {
        public void playMP3(MediaPlayer mp, int rid);

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
		
		
		//Bind button click to playMP3()
				mediaPlayers = new ArrayList<MediaPlayer>();
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
				 

				 for (int i = 0; i < buttons.size(); i++)
				 {
					 mediaPlayers.add(new MediaPlayer());
					 items.add(new BeatItem(buttons.get(i),mediaPlayers.get(i), null));
					 //TODO
					 //set uris to a default so the buttons actually make a noise or do something
					 
				 }
				 

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
				 mCallback.playMP3(mediaPlayers.get(j), id);
				 return true;
				 } else if (event.getAction() == MotionEvent.ACTION_UP) {
				 mCallback.stopMP3(mediaPlayers.get(j));
				
				 }
				 return false;
				 }
				 });
				 }
				
		
		return rootView;
		
	}
	
	
	public class BeatItem{
		public Button button;
		public MediaPlayer mp;
		public Uri uri;
		
		
		public BeatItem(Button b, MediaPlayer m, Uri u)
		{
			button = b;
			mp = m;
			uri=u;
		}
		
		public Button getButton()
		{
			return button;
		}
		
		public MediaPlayer getPlayer()
		{
			return mp;
		}
		
		public Uri getUri()
		{
			return uri;
		}
	}
	
	
}