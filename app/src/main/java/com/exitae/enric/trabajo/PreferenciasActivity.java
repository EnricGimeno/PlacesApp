package com.exitae.enric.trabajo;

import android.os.Bundle;
import android.preference.Preference;

public class PreferenciasActivity extends android.preference.PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);


    }


}
