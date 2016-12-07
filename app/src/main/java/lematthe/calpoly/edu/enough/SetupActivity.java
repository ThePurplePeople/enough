package lematthe.calpoly.edu.enough;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Shefali on 11/27/16.
 */
public class SetupActivity extends AppCompatActivity {

    Button setupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.click_to_setup);

        setupButton = (Button) findViewById(R.id.setupButton);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<String> savedContacts = dbHelper.getContacts();

        if (savedContacts.size() >= 1) {
            setupButton.setText("Edit Profile");
        }
        else {
            setupButton.setText("Click to Setup");
        }


        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("sup", "sup");
                context.startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        setupButton.setText("Edit Profile");
        super.onPause();
    }

}
