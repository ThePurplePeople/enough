package lematthe.calpoly.edu.enough;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Shefali on 11/27/16.
 */
public class ScreenSaverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_screen);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_options_menu, menu);

        findViewById(R.id.edit_profile);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_profile:
                Context context = getApplicationContext();
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("sup", "sup");
                context.startActivity(intent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
