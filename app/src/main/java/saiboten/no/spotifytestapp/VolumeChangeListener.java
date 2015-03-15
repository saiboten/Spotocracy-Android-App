package saiboten.no.spotifytestapp;

import android.media.AudioManager;
import android.widget.SeekBar;

/**
 * Created by Tobias on 15.03.2015.
 */
public class VolumeChangeListener implements SeekBar.OnSeekBarChangeListener {

    private AudioManager audioManager;

    private int maxVolume;

    public VolumeChangeListener(int maxVolume, AudioManager audioManager) {
        this.audioManager = audioManager;
        this.maxVolume = maxVolume;

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (i * maxVolume) / 100, AudioManager.FLAG_PLAY_SOUND);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
