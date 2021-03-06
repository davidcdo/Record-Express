package cs371m.dcd954.recordexpress;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

public class HomeFrag extends Fragment {
    private final String startTag = "START";
    private final String resumeTag = "RESUME";
    private final String pauseTag = "PAUSE";

    protected MediaRecorder mr = null;

    protected View homeView;

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
    protected File currentDir;

    public static HomeFrag newInstance() {
        HomeFrag h = new HomeFrag();
        return h;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        File root = Environment.getExternalStorageDirectory();
        currentDir = new File(root.getAbsolutePath() + "/Record_Express");
        if (!currentDir.exists()) {
            currentDir.mkdir();
        }

        homeView = inflater.inflate(R.layout.home_layout, container, false);

        progressBar = homeView.findViewById(R.id.progressBar);
        progressTime = homeView.findViewById(R.id.progressTime);
        recordingStatus = homeView.findViewById(R.id.recordingStatus);
        reset = homeView.findViewById(R.id.reset);
        save = homeView.findViewById(R.id.save);
        start = homeView.findViewById(R.id.start);
        pause = homeView.findViewById(R.id.pause);

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
                saved = true;

                Intent settingsMenuIntent = new Intent(getActivity(), SaveOption.class);
                Bundle myExtras = new Bundle();
                myExtras.putString("currentPath", savePath);
                settingsMenuIntent.putExtras(myExtras);
                final int result = 1;
                startActivityForResult(settingsMenuIntent, result);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resumeTime!= 0) {
                    progressTime.setBase(progressTime.getBase()
                            + SystemClock.elapsedRealtime() - resumeTime);
                    onRecord(resumeTag);
                    Toast.makeText(getActivity(),
                            "Resuming current recording", Toast.LENGTH_SHORT).show();
                } else {
                    progressTime.setBase(SystemClock.elapsedRealtime());
                    onRecord(startTag);
                    Toast.makeText(getActivity(),
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
                Toast.makeText(getActivity(),
                        "Pausing current recording", Toast.LENGTH_SHORT).show();
            }
        });

        return homeView;
    }

    @Override
    public void onStop() {
        super.onStop();
        resetFrag();
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

            Toast.makeText(getActivity(), savePath.toString(), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(getActivity(),
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
}
