package cs371m.dcd954.recordexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;


public class EditOption extends AppCompatActivity {

    private Button front, back, rename, delete, cancel;

    private String filePath = "";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_option_layout);

        Intent activityThatCalled = getIntent();
        Bundle callingBundle = activityThatCalled.getExtras();
        if(callingBundle != null) {
            filePath = callingBundle.getString("currentPath");
        }

        initWidgets();
    }

    private void initWidgets() {
        front = findViewById(R.id.addFrontID);
        back = findViewById(R.id.addBackID);
        rename = findViewById(R.id.renameID);
        delete = findViewById(R.id.deleteID);
        cancel = findViewById(R.id.cancelID);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "Returning with no changes ...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File deleting = new File(filePath);
               Toast.makeText(getApplicationContext(),
                       "File: " + deleting.toString() + " has been deleted", Toast.LENGTH_SHORT).show();
                if (deleting.exists()) {
                    deleting.delete();
                }
                finish();
            }
        });

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsMenuIntent = new Intent(getApplicationContext(), RenameOption.class);
                Bundle myExtras = new Bundle();
                myExtras.putString("currentPath", filePath);
                settingsMenuIntent.putExtras(myExtras);
                final int result = 1;
                startActivityForResult(settingsMenuIntent, result);
                finish();
            }
        });

        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsMenuIntent = new Intent(getApplicationContext(), ExtendingOption.class);
                Bundle myExtras = new Bundle();
                myExtras.putBoolean("toEdit", true);
                myExtras.putString("currentPath", filePath);
                settingsMenuIntent.putExtras(myExtras);
                final int result = 1;
                startActivityForResult(settingsMenuIntent, result);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsMenuIntent = new Intent(getApplicationContext(), ExtendingOption.class);
                Bundle myExtras = new Bundle();
                myExtras.putBoolean("toEdit", false);
                myExtras.putString("currentPath", filePath);
                settingsMenuIntent.putExtras(myExtras);
                final int result = 1;
                startActivityForResult(settingsMenuIntent, result);
                finish();
            }
        });
    }
}
