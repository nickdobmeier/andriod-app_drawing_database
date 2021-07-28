// Written by Nicholas Dobmeier for CS 4301.001, for assignment 6, starting May 1, 2021.
//        NetID: njd170130

// This class acts as the actual SQLite database itself. It creates the database in onCreate(), and supplies getWritableDatabase() & getReadableDatabase() functions for working directly with the DB

package com.utd.drawasgnmnt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper
{

    public DatabaseHelper(@Nullable Context context) {
        // "drawing.db" is the name of the entire database (which includes all tables)
        super(context, "drawing.db", null, 1);
    }

    // this is called the FIRST time you try to access a database object. This should have code to generate a new table(s)
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //db.setForeignKeyConstraintsEnabled(true);

        String createTableStatementStr =
                "CREATE TABLE " + DrawingSchema.Names.tblName + " (" +
                        DrawingSchema.Names.Fields.id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DrawingSchema.Names.Fields.name + " VARCHAR(100) NOT NULL)";

        db.execSQL(createTableStatementStr);

        createTableStatementStr =
                "CREATE TABLE " + DrawingSchema.Points.tblName + " (" +
                        DrawingSchema.Points.Fields.id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        DrawingSchema.Points.Fields.foreignKey + " INTEGER NOT NULL , " +

                        DrawingSchema.Points.Fields.layer + " INTEGER NOT NULL, " +
                        DrawingSchema.Points.Fields.radius + " REAL NOT NULL, " +
                        DrawingSchema.Points.Fields.color + " INTEGER NOT NULL, " +
                        DrawingSchema.Points.Fields.x + " INTEGER NOT NULL, " +
                        DrawingSchema.Points.Fields.y + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + DrawingSchema.Points.Fields.foreignKey + ") REFERENCES " + DrawingSchema.Names.tblName + "(" + DrawingSchema.Names.Fields.id +") ON DELETE CASCADE" +
                        ");";
                                                            // 'ON DELETE CASCADE'  -> when a row (which has a primary key) in NAME table is deleted, all rows in POINTS table with that same foreign key are also deleted
        /*

        CREATE TABLE points_table (
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            _id_FK_Names INTEGER NOT NULL,
            layer INTEGER NOT NULL,
            radius REAL NOT NULL,
            color INTEGER NOT NULL,
            x_coordinate INTEGER NOT NULL,
            y_coordinate INTEGER NOT NULL,
            FOREIGN KEY(_id_FK_Names) REFERENCES names_table(_id)
        );

         */

        db.execSQL(createTableStatementStr);
    }


        // called whenever the version number of your database changes. Allows for foward/backward compatability
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    
    // helpful resource: https://www.youtube.com/watch?v=312RhjfetP8&ab_channel=freeCodeCamp.org
}
