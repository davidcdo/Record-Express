package cs371m.dcd954.recordexpress;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class SaveOption extends AppCompatActivity {

    private Button cancel, ok;
    private EditText entered;

    //private boolean status = false;
    private String enteredString = "";
    private String filePath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_layout);

        Intent activityThatCalled = getIntent();
        Bundle callingBundle = activityThatCalled.getExtras();
        if(callingBundle != null) {
            filePath = callingBundle.getString("currentPath");
        }

        initWidgets();
    }

    private void initWidgets() {
        cancel = findViewById(R.id.cancel);
        ok = findViewById(R.id.ok);
        entered = findViewById(R.id.entered);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //status = false;
                //enteredString = "";
                //passBackResults();
                File a = new File(filePath);
                a.delete();

                Toast.makeText(getApplicationContext(),
                        "Cancelled, discarding file...",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entered.getText().toString().length() > 0) {
                    //status = true;
                    enteredString = entered.getText().toString() + ".mp3";
                    //passBackResults();

                    File root = Environment.getExternalStorageDirectory();
                    File currentDir = new File(root.getAbsolutePath() +
                            "/Record_Express");

                    File a = new File(filePath);
                    File b = new File(currentDir, enteredString);
                    a.renameTo(b);

                    Toast.makeText(getApplicationContext(),
                            "Successfully renamed file to " + enteredString,
                            Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter in a name for the file",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    private void passBackResults() {
        Intent goingBack = new Intent();
        goingBack.putExtra("a", status);
        goingBack.putExtra("b", enteredString);
        setResult(RESULT_OK, goingBack);
        finish();
    }
    */
}
