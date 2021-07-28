// Dobmeier

// This file gives the blueprint for the SQLite database, including all the tables and each field that belongs to each table.

package com.utd.drawasgnmnt;

public class DrawingSchema      // used for holding String values of the labels within all the tables of the database
{

        // Names TABLE
    public static final class Names
    {
        public static final String tblName = "names_table";             // name of the table

        public static final class Fields{
            public static final String id = "_id";
            public static final String name = "drawing_name";
        }
    }



        // Points TABLE
    public static final class Points
    {
        public static final String tblName = "points_table";            // name of the table

        public static final class Fields{
            public static final String id = "_id";
            public static final String foreignKey = "_id_FK_Names";     // foreign key (to Names table)

            public static final String layer = "layer";                 // which layer is the drawing on (so the UNDO button functionality is maintained)

            public static final String radius = "radius";
            public static final String color = "color";

            public static final String x = "x_coordinate";
            public static final String y = "y_coordinate";
        }
    }


}
