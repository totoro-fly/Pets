package com.totoro_fly.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.totoro_fly.pets.data.PetContract;

/**
 * Created by totoro-fly on 2017/5/7.
 */

public class PetCursorAdapter extends CursorAdapter {


    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        TextView breedTextView = (TextView) view.findViewById(R.id.breed_text_view);
        int nameIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
        int breedIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
        String name = cursor.getString(nameIndex);
        String breed = cursor.getString(breedIndex);
        nameTextView.setText(name);
        breedTextView.setText(breed);


    }
}
