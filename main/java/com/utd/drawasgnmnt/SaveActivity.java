// Written by Nicholas Dobmeier for CS 4301.001, for assignment 6, starting May 1, 2021.
//        NetID: njd170130

// In this activity file, drawings are passed from the MainActivity (through "STACK"), and have the option of being stored in the SQLite database

package com.utd.drawasgnmnt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;


public class SaveActivity extends AppCompatActivity implements adapterInterface
{
    private EditText editText;
    private Button saveButton;
    private RecyclerView recyclerViewDrawingList;
    int selectedItemActivity = -1;                                    // by default NONE of the items should be selected in the beginning. Should stay in sync with the Adapter's version of the variable
    ArrayList<ArrayList<Circle>> StackOfArrays;
    DatabaseIO databaseIO;
    Boolean didSave = null;
    HashMap<String, Integer> hashMapDrawings = null;                // hashmap for constant time performance when searching for a matching drawing name


    // Call setResult in finish(). Calling setResult in onPause() causes framework to override the return and nothing gets returned to caller actvity
    @Override
    public void finish()
    {
        // save return for when the User may press the "back" button on bottom bar of Andriod UI
        Intent intentReturn = new Intent();
        intentReturn.putExtra("DID_SAVE", didSave);
        setResult(789, intentReturn);

        super.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        Bundle b = getIntent().getExtras();
        if(b != null)
        {
            StackOfArrays = (ArrayList<ArrayList<Circle>>) b.getSerializable("STACK");      // LOST ability to be a stack through the intent transfer
        }


        databaseIO = new DatabaseIO(this);

        saveButton = findViewById(R.id.btnSave_save);                       // set to INVISIBLE by default on program load (in the XML code)
        editText = findViewById(R.id.EditTextDrawingName1);
        editText.addTextChangedListener(new ListenerOnTextChange(saveButton));


        recyclerViewDrawingList = findViewById(R.id.recyclerView1);

        ArrayList<String> drawNameArray = databaseIO.getNames();
        String [] strArray = convertListToArray(drawNameArray);
        setRecyclerAdapter(strArray);


        hashMapDrawings = new HashMap<>();
        for(int i=0; i < strArray.length; i++){
            hashMapDrawings.put(strArray[i], i);                    // save array values to class variable so that later when user enters a new drawing name, we can search and see if it already exists in the list
        }
    }




        // hook up the RecyclerView object to the RecyclerAdapter, each call to this function creates a NEW adapter
    public void setRecyclerAdapter(String [] quizStrNames)
    {
        // by default NONE of the items should be selected in the beginning
        //selectedItemMainAct = -1;       // need this when a previous recycler view adapter is replaced by a new one (toggling online/local)
        RecyclerAdapter<SaveActivity> recyclerAdapter1 = new RecyclerAdapter<SaveActivity>(quizStrNames, this, selectedItemActivity);    // RecyclerAdapter constructor takes an array of Quiz name strings as one of its arguments


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewDrawingList.setLayoutManager(layoutManager);
        recyclerViewDrawingList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDrawingList.setAdapter(recyclerAdapter1);
    }

    @Override
    public void setCurrentSelectedItem(int selection){
        this.selectedItemActivity = selection;
        saveButton.setVisibility(View.VISIBLE);             // once the user makes a SELECTION, they are opting to overwrite an already existing drawing
        editText.setText("<selection>");                    // indicate to the user that the drawing name will be the same as the selection they make, lock ability to change drawing name
        editText.setEnabled(false);
    }



    public void onClickSaveToDB(View view)
    {
        String drawingName = editText.getText().toString();
        if(drawingName.length() == 0 && selectedItemActivity == -1){    // if the user has not entered name NOR made a selection
            Snackbar.make(findViewById(R.id.saveActivity), "Must enter name for the drawing", Snackbar.LENGTH_LONG).show();
            return;
        }


        int index = doesNameExistAlready(drawingName);
        if( index != -1){                           // when list is empty, these first 2 conditions can NEVER be true
            didSave = databaseIO.overwriteElement(index+1, StackOfArrays);
            //return; //*******

        }
        else if(selectedItemActivity != -1){
            didSave = databaseIO.overwriteElement(selectedItemActivity+1, StackOfArrays);
            //return; //*******

        }else{
            didSave = databaseIO.addElementToDB(drawingName, StackOfArrays);        // the user is adding an entirely new drawing with a new name
        }

        finish();
    }

    public void onClickCancel(View view)
    {
        finish();
    }



    private int doesNameExistAlready(String name)
    {
        Integer index = hashMapDrawings.get(name);
        return (index != null) ? index : -1;
    }


    private String [] convertListToArray(ArrayList<String> arrayList)
    {
        int size = arrayList.size();
        String [] strArray = new String[size];
        for(int i=0; i < size; i++){
            strArray[i] = arrayList.get(i);
        }
        return strArray;
    }




    // used to determine if user has entered the name "professor" or not (if YES, display Create & Edit buttons)
    private class ListenerOnTextChange implements TextWatcher {
        private final Button save_button;        // final keyword means the variable must always reference the SAME object

        ListenerOnTextChange(Button saveButton) {
            super();
            this.save_button = saveButton;
        }

        @Override
        public void afterTextChanged(Editable s){ }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){ }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){    // find length of text INCLUDING the character/deletion the user just made
            if( (s.length() > 0) )
            {
                save_button.setVisibility(View.VISIBLE);
            }else{
                save_button.setVisibility(View.INVISIBLE);
            }
        }
    }
}