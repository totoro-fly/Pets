package com.totoro_fly.pets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.totoro_fly.pets.data.PetContract;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CatalogActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.floating_button)
    FloatingActionButton floatingButton;
    @Bind(R.id.activity_catalog)
    RelativeLayout activityCatalog;
    @Bind(R.id.pet_list_view)
    ListView petListView;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.textView3)
    TextView textView3;
    @Bind(R.id.empty_view)
    ConstraintLayout emptyView;
    private static final int PET_LOADER = 0;
    private PetCursorAdapter mPetCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        ButterKnife.bind(this);
        mPetCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setEmptyView(emptyView);
        petListView.setAdapter(mPetCursorAdapter);
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentPeturi = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);
                intent.setData(currentPeturi);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(PET_LOADER, null, this);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                insetPet();
                return true;
            case R.id.delete_all:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPets() {
        int rowsDelete=getContentResolver().delete(PetContract.PetEntry.CONTENT_URI,null,null);
        Toast.makeText(this,"刪除全部數據 "+rowsDelete+" 行",Toast.LENGTH_LONG).show();
    }

    private void insetPet() {
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Tom");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Xo");
        values.put(PetContract.PetEntry.COLUME_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 8);
        Uri uri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);
        if (uri == null)
            Toast.makeText(this, "插入失败", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "插入成功", Toast.LENGTH_LONG).show();
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUME_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT
        };
        return new android.content.CursorLoader(
                this,
                PetContract.PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }


    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mPetCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mPetCursorAdapter.swapCursor(null);
    }

}
