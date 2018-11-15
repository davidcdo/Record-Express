package cs371m.dcd954.recordexpress;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayFrag extends Fragment {
    public interface TransformImageButton {
        void transform(ImageButton imageButton);
    }

    final int REQUEST_PERMISSION_CODE = 1000;

    private View playView;
    private File[] files;

    private List<SongDict> songDict;

    /* Key Interface or Class Variables */
    protected TransformImageButton transformImageButton;
    protected ClickPlay clickPlay;
    protected SeekBarHandler s;

    /* Key Data Structures */
    private ListView listView;
    private SwipeRefreshLayout swipeLayout;
    private MediaPlayer mp;
    private SongAdaptor theAdaptor;
    private ImageButton playButton, backButton, forwardButton;
    private TextView currentText, nextText, timePlayed, timeRemained;
    private SeekBar seekBar;

    /* Key Resources */
    private int track;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkPermissionFromDevice()) {

        } else {
            request_permission();
        }
    }

    private boolean checkPermissionFromDevice() {
        int read_external_storage_result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        return read_external_storage_result == PackageManager.PERMISSION_GRANTED;
    }

    private void request_permission() {
        ActivityCompat.requestPermissions(getActivity(), new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(),
                            "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),
                            "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        playView = inflater.inflate(R.layout.play_layout, container, false);

        getFiles();
        createSongDict();
        initListView();
        initResources();
        setFirstSong();
        setList();
        setButtons();

        return playView;
    }

    private void getFiles() {
        String path = Environment.getExternalStorageDirectory().toString()+ "/Record_Express";
        File directory = new File(path);
        files = directory.listFiles();
    }

    private void createSongDict() {
        songDict = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            songDict.add(new SongDict(files[i].getName(), files[i].getPath()));
        }
    }

    private void initListView() {
        theAdaptor = new SongAdaptor(playView.getContext());
        theAdaptor.addAll(songDict);
        listView = playView.findViewById(R.id.listID);
        listView.setAdapter(theAdaptor);
        swipeLayout = playView.findViewById(R.id.swipe_container);
    }

    private void initResources() {
        /* Initialize values */
        track = 0;
        mp = new MediaPlayer();

        /* All IDs for changing the image button */
        clickPlay = new ClickPlay();
        transformImageButton = clickPlay;

        /* All IDs for music buttons */
        backButton = playView.findViewById(R.id.backID);
        playButton = playView.findViewById(R.id.playID);
        forwardButton = playView.findViewById(R.id.forwardID);

        /* All IDs for song displays */
        currentText = playView.findViewById(R.id.textView5);
        nextText = playView.findViewById(R.id.textView6);

        /* All IDs for time displays */
        seekBar = playView.findViewById(R.id.seekBar);
        timePlayed = playView.findViewById(R.id.timePlayedID);
        timeRemained = playView.findViewById(R.id.timeRemainID);
    }

    /* Immediately starts the app off by playing the first song*/
    private void setFirstSong() {
        playSongs(track);
        updateText();
    }

    /* Sets up the list viewer and displays the selected song upon selection by user */
    private void setList() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String albumPicked = "You selected " + songDict.get(i).name;
                Toast.makeText(playView.getContext(), albumPicked, Toast.LENGTH_SHORT).show();

                playSongs(i);
            }
        });

        swipeLayout.setEnabled(false);
    }

    /* Sets up the back, forward, and play buttons and connects it with the media player */
    private void setButtons() {
        backButton.setOnClickListener(v -> {
            track = (track - 1);
            playSongs(track);
            Toast.makeText(playView.getContext(),
                    "Playing previous song", Toast.LENGTH_SHORT).show();
        });

        playButton.setOnClickListener(v -> {
            if (mp.isPlaying()) {
                mp.pause();
                clickPlay.onPlay = true;
            } else {
                mp.start();
                clickPlay.onPlay = false;
            }

            transformImageButton.transform(playButton);
        });

        forwardButton.setOnClickListener(v -> {
            track = (track + 1);
            playSongs(track);
            Toast.makeText(playView.getContext(),
                    "Playing next song", Toast.LENGTH_SHORT).show();
        });
    }

    /* Helper method to play songs */
    public void playSongs(int position) {
        if (position >= files.length - 1) {
            track = position % files.length;
        }

        /* If media player is in usage, clears it for another song */
        if (mp != null) {
            mp.stop();
            mp.reset();
        }

        //mp = MediaPlayer.create(playView.getContext(), songs[track]);
        try {
            //mp = new MediaPlayer();
            mp.setDataSource(files[track].getPath());
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            Toast.makeText(playView.getContext(),
                    "playSongs error: MediaPlayer could not start",
                    Toast.LENGTH_SHORT).show();
        }

        /* Sets the seek bar to this new song */
        s = new SeekBarHandler(mp, seekBar, timePlayed, timeRemained);

        clickPlay.onPlay = false;
        transformImageButton.transform(playButton);

        updateText();
    }

    /* Helper method to update the texts and controls whether or not a song should loop */
    public void updateText() {
        int result;
        if (track == (files.length - 1)) {
            result = 0;
        } else {
            result = track + 1;
        }

        currentText.setText(files[track].getName());
        nextText.setText(files[result].getName());

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                track = (track + 1);
                playSongs(track);
                Toast.makeText(playView.getContext(),
                        "Automatically playing next song ...",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*
    @Override
    public void onStop() {
        super.onStop();
        mp.release(); // Releases the media player
    }
    */

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release(); // Releases the media player
    }
}
