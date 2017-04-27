package com.totoro_fly.pets.data;

import android.provider.BaseColumns;

/**
 * Created by totoro-fly on 2017/3/3.
 */

public final class PetContract {
    public PetContract() {
    }

    public final static class PetEntry implements BaseColumns {
        public final static String TABLE_NAME = "pets";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PET_NAME = "name";
        public final static String COLUMN_PET_BREED = "breed";
        public final static String COLUME_PET_GENDER = "gender";
        public final static String COLUMN_PET_WEIGHT = "weight";

        public final static int GENDER_UNKNOEWN = 0;
        public final static int GENDER_MALE = 1;
        public final static int GENDER_FEMALE = 2;
    }
}
