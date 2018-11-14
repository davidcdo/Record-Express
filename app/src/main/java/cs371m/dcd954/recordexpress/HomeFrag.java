package cs371m.dcd954.recordexpress;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeFrag extends Fragment {

    protected View homeView;

    protected ProgressBar progressBar;
    protected Chronometer progressTime;
    protected TextView recordingStatus;
    protected Button reset;
    protected Button save;
    protected Button start;
    protected Button pause;

    protected long resumeTime;
    protected Boolean recording;

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
        //return super.onCreateView(inflater, container, savedInstanceState);
        homeView = inflater.inflate(R.layout.home_layout, container, false);

        progressBar = homeView.findViewById(R.id.progressBar);
        progressTime = homeView.findViewById(R.id.progressTime);
        recordingStatus = homeView.findViewById(R.id.recordingStatus);
        reset = homeView.findViewById(R.id.reset);
        save = homeView.findViewById(R.id.save);
        start = homeView.findViewById(R.id.start);
        pause = homeView.findViewById(R.id.pause);

        progressBar.setVisibility(View.INVISIBLE);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressTime.stop();
                progressTime.setBase(SystemClock.elapsedRealtime());
                resumeTime = 0;
                pause.setEnabled(false);
                start.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resumeTime!= 0) {
                    progressTime.setBase(progressTime.getBase() + SystemClock.elapsedRealtime() - resumeTime);
                } else {
                    progressTime.setBase(SystemClock.elapsedRealtime() );
                }

                progressTime.start();
                start.setEnabled(false);
                pause.setEnabled(true);
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
            }
        });

        return homeView;
    }
}
