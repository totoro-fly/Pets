package com.totoro_fly.pets;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.totoro_fly.pets.data.PetContract;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.name_edit_view)
    EditText nameEditView;
    @Bind(R.id.Breed_edit_view)
    EditText breedEditView;
    @Bind(R.id.gender_spinner)
    Spinner genderSpinner;
    @Bind(R.id.container_gender)
    LinearLayout containerGender;
    @Bind(R.id.weight_edit_view)
    EditText weightEditView;
    @Bind(R.id.weight_units_edit_view)
    TextView weightUnitsEditView;
    @Bind(R.id.container_measurement)
    LinearLayout containerMeasurement;
    @Bind(R.id.activity_editor)
    LinearLayout activityEditor;
    private int mGendeer = PetContract.PetEntry.GENDER_UNKNOEWN;
    private Uri mCurrentUri;
    private static final int EXISTING_PET_LOADER = 0;
    private boolean mPetHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        if (mCurrentUri == null) {
            setTitle("Add A Pet");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Pet");
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }
        breedEditView.setOnTouchListener(touchListener);
        nameEditView.setOnTouchListener(touchListener);
        weightEditView.setOnTouchListener(touchListener);
        genderSpinner.setOnTouchListener(touchListener);
        setSpinner();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void setSpinner() {
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender_options, R.layout.myspinner);
        arrayAdapter.setDropDownViewResource(R.layout.myspinner);
        genderSpinner.setAdapter(arrayAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setForegroundGravity(Gravity.CENTER);
                }
                String seletion = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(seletion)) {
                    if (seletion.equals("Unkown"))
                        mGendeer = PetContract.PetEntry.GENDER_UNKNOEWN;
                    else if (seletion.equals("Male"))
                        mGendeer = PetContract.PetEntry.GENDER_MALE;
                    else if (seletion.equals("Female"))
                        mGendeer = PetContract.PetEntry.GENDER_FEMALE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mGendeer = PetContract.PetEntry.GENDER_UNKNOEWN;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                finish();
                break;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("delete ani");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentUri != null) {
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "editor_delete_pet_failed", Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "editor_delete_pet_successful", Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }


    private void save() {
        String nameStr = nameEditView.getText().toString().trim();
        String breedStr = breedEditView.getText().toString().trim();
        String weightStr = weightEditView.getText().toString().trim();
        int gender = mGendeer;
        if (mCurrentUri == null && nameStr.isEmpty() && breedStr.isEmpty() && weightStr.isEmpty() && mGendeer == PetContract.PetEntry.GENDER_UNKNOEWN) {
            Toast.makeText(this, "请输入", Toast.LENGTH_LONG).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, nameStr);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, breedStr);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weightStr);
        values.put(PetContract.PetEntry.COLUME_PET_GENDER, gender);
        if (mCurrentUri == null) {
            Uri uri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);
            if (uri == null)
                Toast.makeText(this, "插入失败", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "插入成功", Toast.LENGTH_LONG).show();
        } else {
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);
//            Log.d("tag", String.valueOf(rowsAffected));
            if (rowsAffected == 0)
                Toast.makeText(this, "无更新", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "更新成功", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUME_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT,
        };
        return new CursorLoader(
                this,
                mCurrentUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1)
            return;
        if (data.moveToFirst()) {
            int nameIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
            int breedIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
            int genderIndex = data.getColumnIndex(PetContract.PetEntry.COLUME_PET_GENDER);
            int weightIndex = data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            String name = data.getString(nameIndex);
            String breed = data.getString(breedIndex);
            int gender = data.getInt(genderIndex);
            int weight = data.getInt(weightIndex);
            nameEditView.setText(name);
            breedEditView.setText(breed);
            weightEditView.setText(Integer.toString(weight));
            switch (gender) {
                case PetContract.PetEntry.GENDER_MALE:
                    genderSpinner.setSelection(1);
                    break;
                case PetContract.PetEntry.GENDER_FEMALE:
                    genderSpinner.setSelection(2);
                    break;
                default:
                    genderSpinner.setSelection(0);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        nameEditView.setText("");
        breedEditView.setText("");
        weightEditView.setText("");
        genderSpinner.setSelection(0);

    }

}
