package com.example.languageswitchpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
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
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CheckBox checkBox;
    private Spinner spinner;
    private static final String IS_CHECK = " is checked";
    private static final String PREFS_FILE = "Account";

    private static final String LOGIN_FILE_NAME = "login text";
    private static final String PASS_FILE_NAME = "pass text";
    private String filePath = "MyFileStorage";
    private String fileName = "mFile.txt";
    private File mFile;

    private EditText loginTextView;
    private EditText passwordTextView;
    private Button loginButton;
    private Button registrationButton;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        initSpinnerLang();
        setCheckChange();
        saveSharedPreferences();
        checkBol();
    }

    private void saveSharedPreferences() {

        sharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_CHECK, true);
        editor.apply();
    }

    private void checkBol() {

        boolean booleanCheck = sharedPreferences.getBoolean(IS_CHECK, true);
        checkBox.setChecked(booleanCheck);
    }

    public void setCheckChange() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences
                        .edit()
                        .putBoolean(IS_CHECK, isChecked)
                        .apply();
            }
        });
    }


    private static boolean isReadOnly() {      // проверяем есть ли доступ к внешнему хранилищу
        String storageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState);
    }

    // проверяем есть ли доступ к внешнему хранилищу
    private static boolean isAvailable() {
        String storageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(storageState);
    }

    private static boolean textUtilsValue(String str1, String str2) {  // условие не пустых полей строк

        if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2)) {
            return true;
        } else {
            return false;
        }
    }

    public void showToast(String message) {  // тост ф-я
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String readLineFromFileExt(File file) {   //read   внешний
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
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

    private String readLineFromFile(String fileName) {   //read   внутренний
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

    private void saveExtData(String editText) {    //сохр во внешний файл
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mFile);
            fos.write(editText.getBytes());
            fos.close();
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

    private void saveIntData(String fileName, String text) {  //save во внутр.
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(text.getBytes());
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
        @Override             // перекл. языка
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
        public void onNothingSelected(AdapterView<?> parent) {  //смена языка

        }
    };

    private void initViews() { //инициализация
        checkBox = findViewById(R.id.checkbox);
        spinner = findViewById(R.id.spinner_lang);
        spinner.setOnItemSelectedListener(itemSelectedListener);
        loginTextView = findViewById(R.id.login);
        passwordTextView = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);
        registrationButton = findViewById(R.id.btn_registration);

    }

    public void onClickRegistration(View view) {           //  сохраняем
        String loginStr = loginTextView.getText().toString();
        String passwordStr = passwordTextView.getText().toString();
        if (textUtilsValue(loginStr, passwordStr)) {
            showToast(getString(R.string.nou_pass_nou_login));
        } else {
            if (checkBox.isChecked()) {
                saveIntData(LOGIN_FILE_NAME, loginStr);
                saveIntData(PASS_FILE_NAME, passwordStr);
                showToast(getString(R.string.saving_to_internal_storage));
            } else {

                if (!isAvailable() || isReadOnly()) {

                    registrationButton.setEnabled(false);
                    loginButton.setEnabled(false);
                } else {
                    // если доступ есть, то создаем файл в ExternalStorage
                    mFile = new File(getExternalFilesDir(filePath), fileName);
                }
                saveExtData(loginStr + passwordStr);
                showToast(getString(R.string.saving_to_external_storage));

            }
        }
    }

    public void onClickLogin(View view) {    // читаем
        String loginStr = loginTextView.getText().toString();
        String passwordStr = passwordTextView.getText().toString();
        if (textUtilsValue(loginStr, passwordStr)) {
            showToast(getString(R.string.nou_pass_nou_login));
        } else {
            if (checkBox.isChecked()) {
                String savedLogin = readLineFromFile(LOGIN_FILE_NAME);
                String savedPassword = readLineFromFile(PASS_FILE_NAME);
                final boolean loginEquals = loginTextView.getText().toString().equals(savedLogin);
                final boolean passwordEquals = passwordTextView.getText().toString().equals(savedPassword);
                if (loginEquals && passwordEquals) {

                    Toast.makeText(MainActivity.this, (R.string.access), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, (R.string.no_access), Toast.LENGTH_LONG).show();

                }

            } else {
                String massage = readLineFromFileExt(mFile);
                showToast(getString(R.string.data_obtained_from_external_memory));
                final boolean loginEquals = (loginTextView.getText().toString()
                        + passwordTextView.getText().toString()).equals(massage);

                if (loginEquals) {
                    showToast(getString(R.string.access));
                } else {
                    showToast(getString(R.string.no_access));
                }
            }
        }
    }
}

