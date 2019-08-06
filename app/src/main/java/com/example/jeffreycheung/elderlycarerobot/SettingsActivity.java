package com.example.jeffreycheung.elderlycarerobot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    EditText ipEdit;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String ipAddress = "ipKey";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ipEdit = (EditText)findViewById(R.id.ipEdit);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String ipAddress = sharedPreferences.getString("ipKey", null);
        ipEdit.setText(ipAddress);
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        String i = ipEdit.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ipAddress, i);
        editor.commit();
        Toast.makeText(SettingsActivity.this, "Done", Toast.LENGTH_LONG).show();
    }
}
