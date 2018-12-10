package cs371m.dcd954.recordexpress;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditFrag extends Fragment {

    private View editView;

    private File[] files;
    private List<SongDict> songDict;

    /* Key Data Structures */
    private ListView listView;
    private SwipeRefreshLayout swipeLayout;
    private SongAdaptor theAdaptor;
    private TextView textView;
    private Button editButton;

    /* Key Resources */
    private int track = -1;

    public static EditFrag newInstance() {
        EditFrag h = new EditFrag();
        return h;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        editView = inflater.inflate(R.layout.edit_layout, container, false);

        getFiles();
        createSongDict();
        initListView();
        setList();
        initResource();

        return editView;
    }

    private void getFiles() {
        String path = Environment.getExternalStorageDirectory().toString() + "/Record_Express";
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
        theAdaptor = new SongAdaptor(editView.getContext());
        theAdaptor.addAll(songDict);
        listView = editView.findViewById(R.id.listID);
        listView.setAdapter(theAdaptor);
        swipeLayout = editView.findViewById(R.id.swipe_container);
    }

    /* Sets up the list viewer and displays the selected song upon selection by user */
    private void setList() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String albumPicked = "You selected " + songDict.get(i).name;
                Toast.makeText(editView.getContext(), albumPicked, Toast.LENGTH_SHORT).show();

                track = i;
                textView.setText("Selected file: " + files[track].getName());
            }
        });

        swipeLayout.setEnabled(false);
    }

    private void initResource() {
        textView = editView.findViewById(R.id.itemSelected);
        editButton = editView.findViewById(R.id.editID);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (track != -1) {
                    Intent settingsMenuIntent = new Intent(editView.getContext(), EditOption.class);
                    Bundle myExtras = new Bundle();
                    myExtras.putString("currentPath", files[track].toString());
                    settingsMenuIntent.putExtras(myExtras);
                    final int result = 1;
                    startActivityForResult(settingsMenuIntent, result);
                } else {
                    Toast.makeText(editView.getContext(),
                            "Please select a file!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
