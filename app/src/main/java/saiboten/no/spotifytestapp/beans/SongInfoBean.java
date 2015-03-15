package saiboten.no.spotifytestapp.beans;

/**
 * Created by Tobias on 15.03.2015.
 */
public class SongInfoBean {


    private int duration;
    private String artist;
    private String song;
    private int secondsPlayedTotal;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public int getSecondsPlayedTotal() {
        return secondsPlayedTotal;
    }

    public void setSecondsPlayedTotal(int secondsPlayedTotal) {
        this.secondsPlayedTotal = secondsPlayedTotal;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
