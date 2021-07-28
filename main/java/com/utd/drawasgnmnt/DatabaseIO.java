// Written by Nicholas Dobmeier for CS 4301.001, for assignment 6, starting May 1, 2021.
//        NetID: njd170130

// This class is designed to be the interface between the activities and the actual SQLite database, so the activities are not working on the DB directly

package com.utd.drawasgnmnt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Stack;

public class DatabaseIO
{
    private Context activity;

    DatabaseIO(Context context)
    {
        activity = context;
    }


    public boolean addElementToDB(String drawingName, ArrayList<ArrayList<Circle>> StackOfArrays)
    {
        SQLiteDatabase db = new DatabaseHelper(activity).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DrawingSchema.Names.Fields.name, drawingName);      // do NOT need to add _id field, because that column is AUTOINCREMENT
        long returnSuccessName = db.insert(DrawingSchema.Names.tblName, null, cv);     // returns the AUTO-INCREMENTED value (which is the primary key of Names tables here) of the item it JUST inserted


        if(returnSuccessName == -1){
            db.close();
            return false;
        }


        for(int i=0; i < StackOfArrays.size(); i++)
        {
            for(int g=0; g < StackOfArrays.get(i).size(); g++)
            {
                Circle currentCircle = StackOfArrays.get(i).get(g);

                cv = new ContentValues();

                    // do NOT need to add _id field, because that column is AUTOINCREMENT

                cv.put(DrawingSchema.Points.Fields.foreignKey, returnSuccessName);                  // add foreignKey
                cv.put(DrawingSchema.Points.Fields.layer, i);
                cv.put(DrawingSchema.Points.Fields.radius, currentCircle.getRadius());
                cv.put(DrawingSchema.Points.Fields.color, currentCircle.getColor());
                cv.put(DrawingSchema.Points.Fields.x, currentCircle.getX());
                cv.put(DrawingSchema.Points.Fields.y, currentCircle.getY());

                long returnSuccessPoint = db.insert(DrawingSchema.Points.tblName, null, cv);

                if(returnSuccessPoint == -1){
                    db.close();
                    return false;
                }
            }
        }
        db.close();
        return true;
    }



    public boolean overwriteElement(int idToOverwrite, ArrayList<ArrayList<Circle>> StackOfArrays)
    {
        delete(idToOverwrite);

        SQLiteDatabase db = new DatabaseHelper(activity).getWritableDatabase();

        for(int i=0; i < StackOfArrays.size(); i++)
        {
            for(int g=0; g < StackOfArrays.get(i).size(); g++)
            {
                Circle currentCircle = StackOfArrays.get(i).get(g);

                ContentValues cv = new ContentValues();

                // do NOT need to add _id field, because that column is AUTOINCREMENT

                cv.put(DrawingSchema.Points.Fields.foreignKey, idToOverwrite);                  // add foreignKey
                cv.put(DrawingSchema.Points.Fields.layer, i);
                cv.put(DrawingSchema.Points.Fields.radius, currentCircle.getRadius());
                cv.put(DrawingSchema.Points.Fields.color, currentCircle.getColor());
                cv.put(DrawingSchema.Points.Fields.x, currentCircle.getX());
                cv.put(DrawingSchema.Points.Fields.y, currentCircle.getY());

                long returnSuccessPoint = db.insert(DrawingSchema.Points.tblName, null, cv);

                if(returnSuccessPoint == -1){
                    db.close();
                    return false;
                }
            }
        }

        db.close();
        return true;
    }


    private boolean delete(int idToDelete)
    {
        SQLiteDatabase db = new DatabaseHelper(activity).getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);

        String where = "_id_FK_Names=?";        // _id values seem to start at 1 ?
        String whereArgs[] = {new Integer(idToDelete).toString()};

        int didDelete = db.delete(DrawingSchema.Points.tblName, where, whereArgs);

        db.close();
        return true;
    }



    public ArrayList<String> getNames()
    {
        SQLiteDatabase db = new DatabaseHelper(activity).getReadableDatabase();     // no need to write (which LOCKS the database from all other processes on device), so we just read

        String where = "_id>=?";        // _id values seem to start at 1 ?
        String whereArgs[] = {"0"};

        Cursor cursor = null;
        cursor = db.query(DrawingSchema.Names.tblName, null, where, whereArgs, null, null, null);

        ArrayList<String> drawNameList = new ArrayList<String>(5);

        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DrawingSchema.Names.Fields.name));
            //int id = cursor.getInt(cursor.getColumnIndexOrThrow(DrawingSchema.Names.Fields.id));
            drawNameList.add(name);
        }
        cursor.close();
        db.close();
        return drawNameList;
    }


    public Stack<ArrayList<Circle>> getStackOfArrays(int idOfDrawing)
    {
        SQLiteDatabase db = new DatabaseHelper(activity).getReadableDatabase();     // no need to write (which LOCKS the database from all other processes on device), so we just read

        // Determine how many layers there are in the selected drawing:

            // SELECT COUNT (DISTINCT layer)
            // FROM points_table
            // WHERE _id_FK_Names = idOfDrawing;
        String queryStr = "SELECT COUNT(DISTINCT " + DrawingSchema.Points.Fields.layer + ") FROM " + DrawingSchema.Points.tblName + " WHERE " + DrawingSchema.Points.Fields.foreignKey + " = " + idOfDrawing + ";";
        Cursor cursor = db.rawQuery(queryStr, null);

        int numLayers = -1;
        if(cursor.moveToFirst()){
            // int bs = cursor1.getInt(1); // error: only 1 column (single value) is returned from this query
            numLayers = cursor.getInt(0);
        }else{
            return null;    // how get here?
        }




        Stack<ArrayList<Circle>> StackOfArrays = new Stack<ArrayList<Circle>>();
        for(int i=0; i < numLayers; i++)
        {
            // Determine how many points are in each individual layer of the drawing:

                // SELECT COUNT (*)
                // FROM points_table
                // WHERE _id_FK_Names = idOfDrawing  AND  layer = i;
            queryStr = "SELECT COUNT(*) FROM " + DrawingSchema.Points.tblName + " WHERE " + DrawingSchema.Points.Fields.foreignKey + " = " + idOfDrawing + " AND layer = " + i + ";";
            Cursor cursor1 = db.rawQuery(queryStr, null);

            int numPointsInLayer = -1;
            if(cursor1.moveToFirst()){
                // int bs = cursor1.getInt(1); // error: only 1 column (single value) is returned from this query
                numPointsInLayer = cursor1.getInt(0);
            }else{
                return null;    // how get here?
            }

            cursor1.close();
            StackOfArrays.push(new ArrayList<Circle>(numPointsInLayer));
        }


        String where = "_id_FK_Names=?";        // _id values seem to start at 1 ?
        String whereArgs[] = {new Integer(idOfDrawing).toString()};
        cursor = db.query(DrawingSchema.Points.tblName, null, where, whereArgs, null, null, null);

        while (cursor.moveToNext())
        {
            int layer = cursor.getInt((cursor.getColumnIndexOrThrow(DrawingSchema.Points.Fields.layer)));

            float radius = cursor.getFloat(cursor.getColumnIndexOrThrow(DrawingSchema.Points.Fields.radius));
            int color = cursor.getInt(cursor.getColumnIndexOrThrow(DrawingSchema.Points.Fields.color));
            int xCoord = cursor.getInt(cursor.getColumnIndexOrThrow(DrawingSchema.Points.Fields.x));
            int yCoord = cursor.getInt(cursor.getColumnIndexOrThrow(DrawingSchema.Points.Fields.y));
            Circle currentCircle = new Circle(radius, color, xCoord, yCoord);


            StackOfArrays.get(layer).add(currentCircle);
        }
        cursor.close();
        db.close();
        return StackOfArrays;

    }



    /*
    public int getNumberOfDrawings()
    {
        SQLiteDatabase db = new DatabaseHelper(activity).getReadableDatabase();     // no need to write (which LOCKS the database from all other processes on device), so we just read

        String where = "_id>=?";        // _id values seem to start at 1 ?
        String whereArgs[] = {"0"};

        Cursor cursor = null;
        cursor = db.query(DrawingSchema.Names.tblName, null, where, whereArgs, null, null, null);

        int numDrawings = 0;

        while (cursor.moveToNext()){
            numDrawings++;
        }
        cursor.close();
        db.close();
        return numDrawings;
    }
     */


    /*  DELETES row in NAMES table and all rows in POINTS table that correspond to that row in NAMES (through foreign keyS)
    private boolean delete(int idToDelete)
    {
        SQLiteDatabase db = new DatabaseHelper(activity).getReadableDatabase();     // no need to write (which LOCKS the database from all other processes on device), so we just read
        db.setForeignKeyConstraintsEnabled(true);

        String where = "_id=?";        // _id values seem to start at 1 ?
        String whereArgs[] = {new Integer(idToDelete).toString()};

        int didDelete = db.delete(DrawingSchema.Names.tblName, where, whereArgs);

        db.close();
        return true;
    }
     */
}
