// Written by Nicholas Dobmeier for CS 4301.001, for assignment 6, starting May 1, 2021.
//        NetID: njd170130

// In this activity file, the list of drawings are pulled from the database, and then the user has the option of loading one into the MainActivity canvas

package com.utd.drawasgnmnt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Stack;

public class OpenActivity extends AppCompatActivity implements adapterInterface
{
    private Button loadButton;
    private RecyclerView recyclerViewDrawingList;
    int selectedItemActivity = -1;                                    // by default NONE of the items should be selected in the beginning. Should stay in sync with the Adapter's version of the variable
    Stack<ArrayList<Circle>> StackOfArrays = null;
    DatabaseIO databaseIO;



    @Override
    public void finish()
    {
        // save return for when the User may press the "back" button on bottom bar of Andriod UI
        Intent intentReturn = new Intent();
        intentReturn.putExtra("STACK", StackOfArrays);
        setResult(789, intentReturn);

        super.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);


        databaseIO = new DatabaseIO(this);

        loadButton = findViewById(R.id.btnLoad_open);                       // set to INVISIBLE by default on program load (in the XML code)


        recyclerViewDrawingList = findViewById(R.id.recyclerView2);

        ArrayList<String> drawNameArray = databaseIO.getNames();
        String [] strArray = convertListToArray(drawNameArray);
        setRecyclerAdapter(strArray);
    }


    // hook up the RecyclerView object to the RecyclerAdapter, each call to this function creates a NEW adapter
    public void setRecyclerAdapter(String [] quizStrNames)
    {
        // by default NONE of the items should be selected in the beginning
        //selectedItemMainAct = -1;       // need this when a previous recycler view adapter is replaced by a new one (toggling online/local)
        RecyclerAdapter<OpenActivity> recyclerAdapter1 = new RecyclerAdapter<OpenActivity>(quizStrNames, this, selectedItemActivity);    // RecyclerAdapter constructor takes an array of Quiz name strings as one of its arguments


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewDrawingList.setLayoutManager(layoutManager);
        recyclerViewDrawingList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDrawingList.setAdapter(recyclerAdapter1);
    }


    @Override
    public void setCurrentSelectedItem(int selection) {
        this.selectedItemActivity = selection;
        loadButton.setVisibility(View.VISIBLE);                 // make the load button visible only AFTER the user has selected an item in the list to load
    }


    public void onClickLoad(View view)
    {
        if(selectedItemActivity != -1) {
            StackOfArrays = databaseIO.getStackOfArrays(selectedItemActivity+1);
            finish();
        }else{
            Snackbar.make(findViewById(R.id.openActivity), "Must make a selection", Snackbar.LENGTH_LONG).show();
        }
    }

    public void onClickCancel(View view)
    {
        finish();
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

}