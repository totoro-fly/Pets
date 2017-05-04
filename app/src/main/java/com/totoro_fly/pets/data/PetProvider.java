package com.totoro_fly.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by totoro-fly on 2017/4/30.
 */

public class PetProvider extends ContentProvider {
    private PetDbHelper petDbHelper;
    private static final int PETS = 1;
    private static final int PETS_ID = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);
    }

    @Override
    public boolean onCreate() {
        petDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = petDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PETS_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.PetEntry.CENTENT_LIST_TYPE;
            case PETS_ID:
                return PetContract.PetEntry.CENTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("gettype");
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                if (isValue(values)) {
                    return insertPet(uri, values);
                }
                return null;
            default:
                throw new IllegalArgumentException("Insertion is not supprted for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {
        SQLiteDatabase db = petDbHelper.getReadableDatabase();
        long id = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.d("TAG", "Faild to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private boolean isValue(ContentValues values) {
        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "name", Toast.LENGTH_LONG).show();
            return false;
        }
        Integer gender = values.getAsInteger(PetContract.PetEntry.COLUME_PET_GENDER);
        if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
            Toast.makeText(getContext(), "gender", Toast.LENGTH_LONG).show();
            return false;
        }
        Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            Toast.makeText(getContext(), "weight", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = petDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            case PETS_ID:
                selection = PETS_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("delete" + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);
            case PETS_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for" + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
            if (name.isEmpty()) {
                throw new IllegalArgumentException("name");
            }

        }
        if (values.containsKey(PetContract.PetEntry.COLUME_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetContract.PetEntry.COLUME_PET_GENDER);
            if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("gender");

            }
        }
        if (values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("weight");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db = petDbHelper.getReadableDatabase();
        return db.update(PetContract.PetEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
