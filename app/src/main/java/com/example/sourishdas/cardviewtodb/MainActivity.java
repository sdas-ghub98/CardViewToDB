package com.example.sourishdas.cardviewtodb;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    int[] imageIDs = {
            R.drawable.bug,
            R.drawable.down,
            R.drawable.fish,
            R.drawable.heart,
            R.drawable.help,
            R.drawable.lightning,
            R.drawable.star,
            R.drawable.up
    };
    int nextImageIndex = 0;
    DBAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openDB();
        populateListViewFromDB();
        registerListClickCallback();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        closeDB();
    }
    private void openDB()
    {
        myDb = new DBAdapter(this);
        myDb.open();
    }
    private void closeDB()
    {
        myDb.close();
    }
    public void onClick_AddRecord(View v)
    {
        int imageId = imageIDs[nextImageIndex];
        nextImageIndex = (nextImageIndex + 1)%imageIDs.length;
        myDb.insertRow("Jenny" + nextImageIndex,imageId,"Green");
        populateListViewFromDB();
    }
    public void onClick_ClearAll(View v)
    {
        myDb.deleteAll();
        populateListViewFromDB();
    }
    private void populateListViewFromDB()
    {
        Cursor cursor = myDb.getAllRows();
        startManagingCursor(cursor);

        String[] fromFieldNames = new String[]{DBAdapter.KEY_NAME,DBAdapter.KEY_FAVCOLOUR, DBAdapter.KEY_STUDENTNUM};
        int[] toViewIDs = new int[]
                {
                        R.id.item_name,
                        R.id.item_favcolor,
                        R.id.item_studentnum

                };
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(this,R.layout.item_layout,cursor,fromFieldNames,toViewIDs);

        // Set Adapter for the list view
        ListView myList = (ListView) findViewById(R.id.ListView);
        myList.setAdapter(myCursorAdapter);
    }
    private void registerListClickCallback()
    {
        ListView myList = (ListView) findViewById(R.id.ListView);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long idInDB)
            {
                updateItemForId(idInDB);
                displayToastForId(idInDB);
            }
        });
    }
    private void updateItemForId(long idInDB)
    {
        Cursor cursor = myDb.getRow(idInDB);
        if(cursor.moveToFirst())
        {
            long idDB = cursor.getLong(DBAdapter.COL_ROWID);
            String name = cursor.getString(DBAdapter.COL_NAME);
            int studentNum = cursor.getInt(DBAdapter.COL_STUDENTNUM);
            String favColour = cursor.getString(DBAdapter.COL_FAVCOLOUR);
            favColour += "!";
            myDb.updateRow(idInDB, name, studentNum, favColour);
        }
        cursor.close();
        populateListViewFromDB();
    }

    private void displayToastForId(long idInDB)
    {
        Cursor cursor = myDb.getRow(idInDB);
        if(cursor.moveToFirst())
        {
            long idDB = cursor.getLong(DBAdapter.COL_ROWID);
            String name = cursor.getString(DBAdapter.COL_NAME);
            int studentNum = cursor.getInt(DBAdapter.COL_STUDENTNUM);
            String favColour = cursor.getString(DBAdapter.COL_FAVCOLOUR);
            String message = "ID: " + idDB + "\nName: " + name + "\nStd#: "+studentNum + "\nFavColour: "+favColour;
            Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG);
        }
    }
}
