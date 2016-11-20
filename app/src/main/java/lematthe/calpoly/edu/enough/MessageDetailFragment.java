package lematthe.calpoly.edu.enough;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


    public MessageDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Activity activity = this.getActivity();

        Log.d("ALEX database", "get from database message if any");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.message_detail, container, false);

        //get view items
        userMessage = (EditText) rootView.findViewById(R.id.user_message);
        Log.d("ALEX databse:", "if the database has a message set the EditText to that message");

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
            return;
        }
        else {
            Log.d("ALEX database:", "save the message to the database!");
            getActivity().finish();
        }
    }


    //If the back button is pressed instead of save then save the message if there is one
    @Override
    public void onPause(){
        super.onPause();
        Log.d("ALEX databse:", "save the message if there is one in the databse");
    }


}
