// Dobmeier

// In this file, the XML is inflated (including the custom Follow-View class), and also creates a listener for the SeekBar,
//    and click-listener for all of the color buttons, and another click-listener for the undo button

package com.utd.drawasgnmnt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
{
    int seekBarProgress = 1;

    Button redButton;
    Button greenButton;
    Button blueButton;
    Button blackButton;

    Follow followView;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        redButton = findViewById(R.id.redBtn);
        greenButton = findViewById(R.id.greenBtn);
        blueButton = findViewById(R.id.blueBtn);
        blackButton = findViewById(R.id.blackBtn);
        followView = findViewById(R.id.follow1);

        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        SeekBar seekBar = findViewById(R.id.seekBar1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {  // set onChangeListener so that program can keep track of when user wants to change the radius of the circles/lines being drawn
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                seekBarProgress = seekBar.getProgress();
                followView.setCurrentCircleRadius(seekBarProgress);     // change circle/line radius size
            }
        });

        seekBarProgress = seekBar.getProgress();                        // default SeekBar progress of 15 is initialized in the XML code
        followView.setCurrentCircleRadius(seekBarProgress);             // tell the custom View object that by default, circles/lines of radius 15 are to be drawn
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (id){
            case R.id.ic_save_action:                               // when the SAVE icon on the activity bar is clicked
                Stack<ArrayList<Circle>> StackOfArrays = followView.StackOfArrays;
                Intent intent_1 = new Intent(this, SaveActivity.class);
                intent_1.putExtra("STACK", StackOfArrays);
                startActivityForResult(intent_1, 1001);
                return true;                                        // always return TRUE after an item on the activity bar is clicked

            case R.id.ic_open_action:                               // when the OPEN icon on the activity bar is clicked
                Intent intent_2 = new Intent(this, OpenActivity.class);
                startActivityForResult(intent_2, 1002);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }




    // when user comes BACK from SaveActivity, this is called even BEFORE onResume for MainActivity
    //      - only called when Activity's return that were invoked using startActivityForResult()
    //      - update main-menu's RecyclerView here...
    // ** The resultCode will be RESULT_CANCELED if the activity didn't return any result, explicitly returned that, or crashed during its operation
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 789)                                       // when the return from SaveActivity or OpenActivity was successful
        {

            if (requestCode == 1001)                                // returning from SaveActivity when it was called from SAVE button being pressed
            {
                Boolean didSave = (Boolean) data.getSerializableExtra("DID_SAVE");

                if (didSave == null) {    // if didSave is NULL, the user hit cancel (or back button) before ever trying to save anything at all
                    Snackbar.make(findViewById(R.id.mainActivity), "You never clicked SAVE", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GRAY).show();
                }
                else if(didSave == true){
                    Snackbar.make(findViewById(R.id.mainActivity), "SUCCESSFULLY Saved", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show();
                }
                else{
                    Snackbar.make(findViewById(R.id.mainActivity), "FAILED to Save", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show();
                }

            }
            else if(requestCode == 1002){
                ArrayList<ArrayList<Circle>> ArrayOfArrays = (ArrayList<ArrayList<Circle>>) data.getSerializableExtra("STACK");         // LOST ability to be a stack through the intent transfer
                if(ArrayOfArrays == null){
                    Snackbar.make(findViewById(R.id.mainActivity), "You never clicked LOAD", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GRAY).show();

                }else{
                        // if an ArrayOfArrays WAS passed back from the OpenActivity, put it on the Main Canvas/drawing board
                    Stack<ArrayList<Circle>> StackOfArrays = convertArrayListToStack(ArrayOfArrays);
                    followView.setStackOfArrays(StackOfArrays);
                    Snackbar.make(findViewById(R.id.mainActivity), "SUCCESSFULLY Loaded", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show();
                }
            }
        }
    }
    private Stack<ArrayList<Circle>> convertArrayListToStack(ArrayList<ArrayList<Circle>> ArrayOfArrays)
    {
        Stack<ArrayList<Circle>> StackOfArrays = new Stack<>();
        for(int i = 0; i < ArrayOfArrays.size(); i++)
        {
            int pointsInLayer = ArrayOfArrays.get(i).size();
            StackOfArrays.push(new ArrayList<Circle>(pointsInLayer));
            for(int g = 0; g < pointsInLayer; g++)
            {
                Circle circle = ArrayOfArrays.get(i).get(g);
                StackOfArrays.get(i).add(new Circle(circle.getRadius(), circle.getColor(), circle.getX(), circle.getY()));      // DEEP-copy
            }
        }
        return StackOfArrays;
    }





    public void colorOnClick(View view)
    {
        Button buttonClicked = (Button) view;
        if(buttonClicked.hashCode() == redButton.hashCode()){
            followView.setCurrentCircleColor(Color.rgb(0xFF, 0x00, 0x00));  // hex-code for RED
        }

        else if(buttonClicked.hashCode() == greenButton.hashCode()){
            followView.setCurrentCircleColor(Color.rgb(0x00, 0xFF, 0x00));  // hex-code for GREEN
        }

        else if(buttonClicked.hashCode() == blueButton.hashCode()){
            followView.setCurrentCircleColor(Color.rgb(0x00, 0x00, 0xFF));  // hex-code for BLUE
        }

        else if(buttonClicked.hashCode() == blackButton.hashCode()){
            followView.setCurrentCircleColor(Color.BLACK);
        }
    }

    public void undoOnClick(View view)
    {
        boolean didRemoveSomething = followView.removeLastDraw();
        if(didRemoveSomething == false){
            Toast.makeText(getApplicationContext(),"Nothing to UNDO",Toast.LENGTH_LONG).show();
        }
    }
}
