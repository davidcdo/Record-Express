package cs371m.dcd954.recordexpress;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ExtendingOption extends AppCompatActivity {
    private final String startTag = "START";
    private final String resumeTag = "RESUME";
    private final String pauseTag = "PAUSE";

    protected MediaRecorder mr = null;

    protected ProgressBar progressBar;
    protected Chronometer progressTime;
    protected TextView recordingStatus;
    protected Button reset;
    protected Button save;
    protected Button start;
    protected Button pause;

    protected boolean saved = false;
    protected long resumeTime;
    protected String savePath = null;

    private boolean front;
    private String currentPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        Intent activityThatCalled = getIntent();
        Bundle callingBundle = activityThatCalled.getExtras();
        if(callingBundle != null) {
            front = callingBundle.getBoolean("toEdit");
            currentPath = callingBundle.getString("currentPath");
        }

        progressBar = findViewById(R.id.progressBar);
        progressTime = findViewById(R.id.progressTime);
        recordingStatus = findViewById(R.id.recordingStatus);
        reset = findViewById(R.id.reset);
        save = findViewById(R.id.save);
        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);

        progressBar.setVisibility(View.INVISIBLE);

        resetCorrectButtons();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFrag();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savePath != null) {
                    mr.stop();
                    mr.release();
                    mr = null;

                    if (front) {
                        mergeAudio(front, savePath, currentPath, currentPath);
                    } else {
                        mergeAudio(front, currentPath, savePath, currentPath);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Did not save??", Toast.LENGTH_SHORT).show();
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resumeTime!= 0) {
                    progressTime.setBase(progressTime.getBase()
                            + SystemClock.elapsedRealtime() - resumeTime);
                    onRecord(resumeTag);
                    Toast.makeText(getApplicationContext(),
                            "Resuming current recording", Toast.LENGTH_SHORT).show();
                } else {
                    progressTime.setBase(SystemClock.elapsedRealtime());
                    onRecord(startTag);
                    Toast.makeText(getApplicationContext(),
                            "Starting new recording", Toast.LENGTH_SHORT).show();
                }

                progressTime.start();
                start.setEnabled(false);
                pause.setEnabled(true);
                save.setEnabled(true);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeTime = SystemClock.elapsedRealtime();
                progressTime.stop();
                pause.setEnabled(false);
                start.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                onRecord(pauseTag);
                Toast.makeText(getApplicationContext(),
                        "Pausing current recording", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void resetCorrectButtons() {
        pause.setEnabled(false);
        save.setEnabled(false);
        start.setEnabled(true);
    }

    private void onRecord(String a) {
        if (startTag == a) {
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Record_Express/" + UUID.randomUUID().toString() + ".mp3";

            Toast.makeText(getApplicationContext(), savePath.toString(), Toast.LENGTH_SHORT).show();

            setMediaRecorder();

            try {
                mr.prepare();
                mr.start();
            } catch (Exception e) {
                Log.d("XXX", "onRecord: Recording failed");
            }
        } else if (resumeTag == a) {
            try {
                mr.resume();
            } catch (Exception e) {
                Log.d("XXX", "onRecord: Resume failed");
            }
        } else if (pauseTag == a){
            try {
                mr.pause();
            } catch (Exception e) {
                Log.d("XXX", "onRecord: Pause failed");
            }
        } else {
            Log.d("XXX", "Error at onRecord");
            Toast.makeText(getApplicationContext(),
                    "Error with MediaRecorder", Toast.LENGTH_SHORT).show();
        }
    }

    private void setMediaRecorder() {
        mr = new MediaRecorder();
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mr.setOutputFile(savePath);
    }

    private void resetFrag() {
        if (mr != null) {
            try {
                mr.stop();
            } catch (Exception e) {
                Log.d("XXX", "onClick reset, stopping recorder failed");
            } finally {
                mr.release();
                mr = null;
            }
        }

        progressTime.stop();
        progressTime.setBase(SystemClock.elapsedRealtime());
        resumeTime = 0;
        pause.setEnabled(false);
        start.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);

        if (savePath != null && !saved) {
            File a = new File(savePath);
            a.delete();
        }

        savePath = null;
        saved = false;

        resetCorrectButtons();
    }

    private void mergeAudio(boolean front, String first, String second, String output) {
        try {
            String[] videoUris = new String[]{first, second};

            List<Movie> inMovies = new ArrayList<Movie>();
            for (String videoUri : videoUris) {
                inMovies.add(MovieCreator.build(videoUri));
            }

            List<Track> audioTracks = new LinkedList<Track>();

            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();

            if (!audioTracks.isEmpty()) {
                result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }

            Container out = new DefaultMp4Builder().build(result);

            FileChannel fc = new RandomAccessFile(output, "rw").getChannel();
            out.writeContainer(fc);
            fc.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (front) {
            deleter(first);
        } else {
            deleter(second);
        }

        saved = true;
        finish();
    }

    private void deleter(String a) {
        File deleting = new File(a);
        if (deleting.exists()) {
            deleting.delete();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
