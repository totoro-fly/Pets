package com.totoro_fly.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.totoro_fly.pets.data.PetContract;
import com.totoro_fly.pets.data.PetDbHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CatalogActivity extends AppCompatActivity {

    @Bind(R.id.pet_text_view)
    TextView petTextView;
    @Bind(R.id.floating_button)
    FloatingActionButton floatingButton;
    @Bind(R.id.activity_catalog)
    RelativeLayout activityCatalog;
    PetDbHelper petDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        ButterKnife.bind(this);
        petDbHelper = new PetDbHelper(this);

    }

    @OnClick(R.id.floating_button)
    public void onClick() {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                insetPet();
                displayDatabaseInfo();
                return true;
            case R.id.delete_all:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insetPet() {
        SQLiteDatabase db = petDbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Tom");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Xo");
        values.put(PetContract.PetEntry.COLUME_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 8);
        long str = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        Log.d("TAG", String.valueOf(str));
    }

    @Override
    protected void onStart() {
        displayDatabaseInfo();
        super.onStart();
    }

    private void displayDatabaseInfo() {
        TextView textView = (TextView) findViewById(R.id.pet_text_view);
        SQLiteDatabase db = petDbHelper.getReadableDatabase();
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUME_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT};

        Cursor cursor = db.query(
                PetContract.PetEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        try {
            textView.setText(cursor.getCount() + "\n");
            textView.append(PetContract.PetEntry._ID + " " +
                    PetContract.PetEntry.COLUMN_PET_NAME + " " +
                    PetContract.PetEntry.COLUME_PET_GENDER + " " +
                    PetContract.PetEntry.COLUMN_PET_BREED + " " +
                    PetContract.PetEntry.COLUMN_PET_WEIGHT + " \n"
            );

            int idColumnIndex = cursor.getColumnIndex(PetContract.PetEntry._ID);
            int nameColumIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUME_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumIndex);
                String currentBreed = cursor.getString(breedColumnIndex);
                String currentGender = cursor.getString(genderColumnIndex);
                String currentWeight = cursor.getString(weightColumnIndex);
                textView.append(
                        currentID + " " +
                                currentName + " " +
                                currentBreed + " " +
                                currentGender + " " +
                                currentWeight + " \n"
                );
            }
        } finally {
            cursor.close();
        }
    }
}
