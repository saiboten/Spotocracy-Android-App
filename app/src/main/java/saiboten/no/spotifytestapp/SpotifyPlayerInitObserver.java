package saiboten.no.spotifytestapp;

import android.util.Log;

import com.spotify.sdk.android.playback.Player;

/**
 * Created by Tobias on 15.03.2015.
 */
public class SpotifyPlayerInitObserver implements Player.InitializationObserver {

    SpotifyIntegrationComponent spotifyIntegrationComponent;

    Player player;

    public SpotifyPlayerInitObserver(Player player, SpotifyIntegrationComponent spotifyIntegrationComponent) {
        this.spotifyIntegrationComponent = spotifyIntegrationComponent;
        this.player = player;
    }

    @Override
    public void onInitialized(Player player) {
        player.addConnectionStateCallback(spotifyIntegrationComponent);
        player.addPlayerNotificationCallback(spotifyIntegrationComponent);
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
    }
}
