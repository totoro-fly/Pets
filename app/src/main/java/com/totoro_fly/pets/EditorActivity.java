package com.totoro_fly.pets;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

public class EditorActivity extends AppCompatActivity {

    @Bind(R.id.name_edit_view)
    EditText nameEditView;
    @Bind(R.id.Breed_edit_view)
    EditText BreedEditView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        setSpinner();
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
                insert();
                finish();
                break;
            case R.id.action_delete:
                break;
        }

        return true;
    }

    private void insert() {
        String nameStr = nameEditView.getText().toString().trim();
        String breedStr = BreedEditView.getText().toString().trim();
        String weightStr = weightEditView.getText().toString().trim();
        int gender = mGendeer;
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, nameStr);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, breedStr);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weightStr);
        values.put(PetContract.PetEntry.COLUME_PET_GENDER, gender);
        Uri uri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);
        if (uri == null)
            Toast.makeText(this, "插入失败", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "插入成功", Toast.LENGTH_LONG).show();
    }
}
