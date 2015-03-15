package saiboten.no.spotifytestapp;

import android.util.Log;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.playback.Config;
import com.spotify.sdk.android.playback.Player;

/**
 * Created by Tobias on 15.03.2015.
 */
public class SpotifyPlayerHandler {

    private static final String CLIENT_ID = "b60120e0052b4973b2a89fab00925019";

    private Player mPlayer;

    private MainActivity mainActivity;

    SpotifyIntegrationComponent spotifyIntegrationComponent;

    public SpotifyPlayerHandler(MainActivity mainActivity, SpotifyIntegrationComponent spotifyIntegrationComponent) {
        this.mainActivity = mainActivity;
        this.spotifyIntegrationComponent = spotifyIntegrationComponent;
    }

    public Player getPlayer() {
        return mPlayer;
    }
    public void resume() {
        Log.d("SpotifyPlayerHandler", "Resuming");
        mPlayer.resume();
    }

    public void pause() {
        Log.d("SpotifyPlayerHandler", "Pausing");
        mPlayer.pause();
    }

    public void playerEnabled(AuthenticationResponse response) {
        Log.d("SpotifyPlayerHandler", "Player enabled");

        // Handle successful response
        Config playerConfig = new Config(mainActivity, response.getAccessToken(), CLIENT_ID);
        mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayerInitObserver(mPlayer, spotifyIntegrationComponent));
    }
}
