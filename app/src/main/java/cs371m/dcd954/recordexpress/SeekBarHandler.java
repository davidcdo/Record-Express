package cs371m.dcd954.recordexpress;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarHandler {
    /* Initializes key resources */
    protected MediaPlayer mp;
    protected SeekBar seekBar;
    TextView timePlayed, timeRemained;
    protected Handler handler;
    protected Runnable rateLimitRequest;
    protected final int rateLimitMillis = 200; // 1 sec

    public SeekBarHandler (MediaPlayer mp, SeekBar seekBar,
                           TextView timePlayed, TextView timeRemained) {
        this.mp = mp;
        this.seekBar = seekBar;
        this.timePlayed = timePlayed;
        this.timeRemained = timeRemained;

        handler = new Handler();

        setUpSeekBar();
    }

    /* Sets up the SeekBar for active usage by the user */
    public void setUpSeekBar() {
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mp.getDuration());
                reposition();
                mp.start();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mp.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /* Loop sequence to constantly update the SeekBar every rateLimitMillis seconds */
    public void reposition() {
        if (mp.isPlaying()) {
            seekBar.setProgress(mp.getCurrentPosition());

            timePlayed.setText(timeConvert(mp.getCurrentPosition()));
            timeRemained.setText(timeConvert(mp.getDuration()
                    - mp.getCurrentPosition()));

            rateLimitRequest = new Runnable() {
                @Override
                public void run() {
                    reposition();
                }
            };
        }
        handler.postDelayed(rateLimitRequest, rateLimitMillis);
    }

    /* Returns a String of 'MM:SS' using a long millisecond variable */
    public String timeConvert(long milliseconds) {
        String result;
        String secondVal;

        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (seconds < 10) {
            secondVal = "0" + seconds;
        } else {
            secondVal = "" + seconds;
        }

        result = "0" + minutes + ":" + secondVal;
        return result;
    }
}
