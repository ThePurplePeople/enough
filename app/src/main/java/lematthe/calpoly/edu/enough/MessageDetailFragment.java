package lematthe.calpoly.edu.enough;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.fragment;

/**
 * Created by laurenmatthews on 11/18/16.
 */

public class MessageDetailFragment extends Fragment {

    public EditText userMessage;
    public TextView message1;
    public TextView message2;
    public TextView message3;
    public TextView message4;
    public TextView message5;
    public Button saveMessage;
    public DatabaseHelper dbHelper; // The database helper(manager)



    public MessageDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dbHelper = new DatabaseHelper(getActivity());
        Activity activity = this.getActivity();
        if (savedInstanceState != null) {
            Log.d("saved instance", "saved");
        }
        Log.d("ALEX database", "get from database message if any");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.message_detail, container, false);
        Log.d("on create view", "here");

        rootView.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event ) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.d("back pressed", "in here");
                }
                return true;
            }
        });
        //get view items
        userMessage = (EditText) rootView.findViewById(R.id.user_message);
        String message = dbHelper.getMessage();
        if (!message.isEmpty()) {
            userMessage.setText(message);
        }

        message1 = (TextView) rootView.findViewById(R.id.message1);
        message2 = (TextView) rootView.findViewById(R.id.message2);
        message3 = (TextView) rootView.findViewById(R.id.message3);
        message4 = (TextView) rootView.findViewById(R.id.message4);
        message5 = (TextView) rootView.findViewById(R.id.message4);

        setPresetMessageClick();

        saveMessage = (Button) rootView.findViewById(R.id.save_message);

        saveMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageandFinish();
            }
        });

        return rootView;
    }



    //this method sets the onClickListeners for the textViews to make sure that the Edit text is set
    void setPresetMessageClick(){
        message1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMessage.setText(message1.getText());
            }
        });
        message2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMessage.setText(message2.getText());
            }
        });
        message3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMessage.setText(message3.getText());

            }
        });
        message4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMessage.setText(message4.getText());

            }
        });
        message5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMessage.setText(message5.getText());
            }
        });
    }


    //when the save button is pressed the previous activity will be instianted
    public void saveMessageandFinish(){
        //check if there is text if there is then save if not prompt user
        String message_entered;
        message_entered = userMessage.getText().toString();
        if (message_entered.matches("")) {
            Toast.makeText(this.getActivity(), "You did not enter a message", Toast.LENGTH_SHORT).show();
            dbHelper.deleteMessage();
            return;
        }
        else {
            dbHelper.deleteMessage();
            dbHelper.insertMessage(message_entered);
            getActivity().finish();
        }
    }


    //If the back button is pressed instead of save then save the message if there is one
    @Override
    public void onStop(){
        saveMessageandFinish();
        super.onStop();
    }

}
