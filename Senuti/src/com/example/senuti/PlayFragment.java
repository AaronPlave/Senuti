package com.example.senuti;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PlayFragment extends Fragment{


	PlayControllerListener callBack;
	Button atPlay;
	Button atPause;
	Button atBack;
	Switch atReverse;
	SeekBar pitchSlider;
	Button randomButton;
	
	
	public interface PlayControllerListener {
        public void setReverse(boolean dir);
        public void setPitch(double sliderVal);
        public void play();
        public void pause();
        public void random();
        public void back();
        public void chooseFileFromIntent();
    }
	
	  @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        
	        // This makes sure that the container activity has implemented
	        // the callback interface. If not, it throws an exception
	        try {
	            callBack = (PlayControllerListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement OnHeadlineSelectedListener");
	        }
	    }
	
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.effects_layout, container, false);
			
			
			randomButton = (Button) rootView.findViewById(R.id.btnRandom);
			randomButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					callBack.random();
					// String songs = atp.getSongsOnDevice().toString();

					// Log.d("TAG_ACTIVITY","END OF SONGS");
				}
			});

			
			// Bind play AT file
			atPlay = (Button) rootView.findViewById(R.id.btnAudioTrackPlay);
			atPlay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					callBack.play();
				}
			});

			// Bind pause AT file
			atPause = (Button) rootView.findViewById(R.id.btnAudioTrackPause);
			atPause.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					callBack.pause();
				}
			});
			

			// Bind backtrack AT file
			atBack = (Button) rootView.findViewById(R.id.btnAudioTrackBack);
			atBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO: Add logic to determine whether or not to go back to
					// previous song or
					// go to beginning of song based on where in the song you
					// currently are.
					callBack.back();

				}
			});
			
			
			// Bind the reverse toggle for AT
			atReverse = (Switch) rootView.findViewById(R.id.toggleReverse);
			atReverse.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					// Is the toggle on?
					boolean on = ((Switch) view).isChecked();

					if (on) {
						callBack.setReverse(true);
					} else {
						callBack.setReverse(false);
					}
				}
			});
			
			
			
			// Bind the pitch slider for AT
			pitchSlider = (SeekBar) rootView.findViewById(R.id.pitchSlider);
			// create a listener for the slider bar;
			OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if (fromUser) {
						double sliderval;
						Log.d("TAG_ACTIVITY", Integer.toString(progress));
						sliderval = progress / (double) seekBar.getMax();
						Log.d("TAG_ACTIVITY", Double.toString(sliderval));
						callBack.setPitch(sliderval);

					}
				}
			};

	// set the listener on the slider
	pitchSlider.setOnSeekBarChangeListener(listener);
			
	Button btn = (Button) rootView.findViewById(R.id.btnChoose);
	if(btn!=null)
	btn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			callBack.chooseFileFromIntent();
		}
	});
			
			return rootView;
		}
		
		public void onCreate(Bundle savedInstanceState) {
	    	// TODO Auto-generated method stub
	    	super.onCreate(savedInstanceState);
			// Bind the random button click
			
			
			
		}
		
		
	
}