package com.example.languageswitchpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CheckBox checkBox;
    private Spinner spinner;
    private final String CHECK = "check";
    private static final String LOGIN_FILE_NAME = "login_text";
    private static final String PASS_FILE_NAME = "pass_text";
    private static final String MY_LOG_PAS = "my log pas";
    private EditText loginTextView;
    private EditText passwordTextView;
    Button loginButton;
    Button registrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSpinnerLang();
        setCheckChange();
        loadView();
    }

    void loadView() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        boolean sPrefBoolean = sPref.getBoolean(CHECK, true);
        checkBox.setChecked(sPrefBoolean);
        // Toast.makeText(this, "CHECK loaded", Toast.LENGTH_SHORT).show();
    }


    private void initViews() { //инициализация
        checkBox = findViewById(R.id.checkbox);
        spinner = findViewById(R.id.spinner_lang);
        spinner.setOnItemSelectedListener(itemSelectedListener);
        loginTextView = findViewById(R.id.login);
        passwordTextView = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);
        registrationButton = findViewById(R.id.btn_registration);

    }

    public void setCheckChange() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    // checked
                    registrationButton.setOnClickListener(new View.OnClickListener() {  //сохраняем файл
                        @Override
                        public void onClick(View v) {
                            String loginStr = loginTextView.getText().toString();
                            String passwordStr = passwordTextView.getText().toString();

                            if (loginStr.equals("") || passwordStr.equals("")) {
                                Toast.makeText(MainActivity.this, (R.string.nou_pass_nou_login), Toast.LENGTH_LONG).show();
                            } else {

                                saveData(LOGIN_FILE_NAME, loginStr);
                                saveData(PASS_FILE_NAME, passwordStr);
                            }
                        }
                    });

                    loginButton.setOnClickListener(new View.OnClickListener() {   //читаем файл
                        @Override
                        public void onClick(View v) { //--------------------------------------------------------------

                            readLineFromFile(LOGIN_FILE_NAME);
                            readLineFromFile(PASS_FILE_NAME);
                        }
                    });
                    Toast.makeText(MainActivity.this, (R.string.checked_text), Toast.LENGTH_LONG).show();
                } else {
                    // not checked
                    Toast.makeText(MainActivity.this, (R.string.not_checked_text), Toast.LENGTH_LONG).show();

                    File myFile = new File(Environment.getExternalStorageDirectory(), MY_LOG_PAS);
                    if (myFile.canRead()) {
                        try {
                            FileReader fileReader = new FileReader(myFile);
                            BufferedReader bufferedReader = new BufferedReader(fileReader);
                            bufferedReader.readLine();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (myFile.canWrite()) {
                        try {
                            FileWriter fileWriter = new FileWriter(myFile);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            bufferedWriter.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        });
    }

    private String readLineFromFile(String fileName) {   //read
        FileInputStream fis = null;
        try {
            fis = openFileInput(fileName);
            final InputStreamReader streamReader = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(streamReader);

            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveData(String fileName, String text) {  //save
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(text.getBytes());
            //  Toast.makeText(MainActivity.this, (R.string.save_text), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initSpinnerLang() {  //адаптер

        ArrayAdapter<CharSequence> adapterCountries = ArrayAdapter.createFromResource(this, R.array.language, android.R.layout.simple_spinner_item);
        adapterCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterCountries);
    }

    AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            // Получаем выбранный объект
            String item = (String) parent.getItemAtPosition(position);

            if (item.equals("английский")) {
                Locale locale = new Locale("en");
                Configuration config = new Configuration();
                config.setLocale(locale);
                getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                recreate();
            }
            if (item.equals("russian")) {
                Locale locale = new Locale("ru");
                Configuration config = new Configuration();
                config.setLocale(locale);
                getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                recreate();
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}

