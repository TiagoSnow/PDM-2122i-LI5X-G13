package pt.isel.pdm.chess4android;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    MediaPlayer player;
    private int length = 0;
    public IBinder onBind(Intent arg0) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.background);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return Service.START_STICKY;
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void resumeMusic()
    {
        if(!player.isPlaying())
        {
            player.seekTo(length);
            player.start();
        }
    }

    public void onStop() {
        player.stop();
        player.release();
        player = null;
    }
    public void onPause() {
        if(player.isPlaying())
        {
            player.pause();
            length=player.getCurrentPosition();
        }
    }
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}
