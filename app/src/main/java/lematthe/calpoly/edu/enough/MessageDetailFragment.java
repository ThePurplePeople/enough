package lematthe.calpoly.edu.enough;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import static android.R.attr.fragment;
import static lematthe.calpoly.edu.enough.R.id.toolbar;

/**
 * Created by laurenmatthews on 11/18/16.
 */

public class MessageDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";

    public EditText userMessage;
    public TextView message1;
    public TextView message2;
    public TextView message3;
    public Button   save;
    public boolean twoPane;
    public Toolbar tool;
    public DatabaseHelper dbHelper; // The database helper(manager)



    public MessageDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dbHelper = new DatabaseHelper(getActivity());
        Activity activity = this.getActivity();

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            twoPane = getArguments().getBoolean(ARG_ITEM_ID);
//            Log.d("argument", String.valueOf(twoPane));
//        }

        View rootView = inflater.inflate(R.layout.message_detail, container, false);
        userMessage = (EditText) rootView.findViewById(R.id.user_message);
        String message = dbHelper.getMessage();
        if (!message.isEmpty()) {
            userMessage.setText(message);
        }
//        if (twoPane) {
//            save = (Button) rootView.findViewById(R.id.saveButton);
//        }
        message1 = (TextView) rootView.findViewById(R.id.message1);
        message2 = (TextView) rootView.findViewById(R.id.message2);
        message3 = (TextView) rootView.findViewById(R.id.message3);

        setPresetMessageClick();
//        if (twoPane) {
//            save.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    saveMessageandFinish();
//                }
//            });
//        }

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        if (!twoPane) {
//            inflater.inflate(R.menu.messgage_detail_menu, menu);
//        }
        inflater.inflate(R.menu.messgage_detail_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_submit:
                saveMessageandFinish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
//            if (!twoPane) {
//                getActivity().finish();
//            }
            getActivity().finish();
        }
//        if (twoPane) {
//            if (getActivity().getLocalClassName().equals("MainActivity")) {
//                ((MainActivity) getActivity()).messageChanged(message_entered);
//            }
//        }

    }


    //If the back button is pressed DONT save just go back
    @Override
    public void onStop(){

        super.onStop();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
