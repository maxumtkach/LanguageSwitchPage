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
    private final String IS_CHECK = " is checked";
    private final String NOT_CHECK = "not checked";
    private static final String LOGIN_FILE_NAME = "login text";
    private static final String PASS_FILE_NAME = "pass text";
    private String filePath = "MyFileStorage";
    private String fileName = "mFile.txt";
    private File mFile;

    private EditText loginTextView;
    private EditText passwordTextView;
    private Button loginButton;
    private Button registrationButton;
    private String loginStr;
    private String passwordStr;
    private String loginReturn;
    private String passReturn;
    private Editable loginEditable;
    private Editable passEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        loginEditable = loginTextView.getText();
        passEditable = passwordTextView.getText();

        initSpinnerLang();
        setCheckChange();
        noCheckLoadView();
    }

    void noCheckLoadView() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);   //при включ-ии прил. чек активен
        boolean sPrefBoolean = sharedPreferences.getBoolean(NOT_CHECK, false);
        boolean sPref = sharedPreferences.getBoolean(IS_CHECK, true);
        checkBox.setChecked(sPref);
        checkBox.setChecked(sPrefBoolean);
    }

    public void setCheckChange() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isChecked()) {

                    registrationButton.setOnClickListener(new View.OnClickListener() {  // onclick  сохраняем файл
                        @Override
                        public void onClick(View v) {

                            loginStr = loginTextView.getText().toString();
                            passwordStr = passwordTextView.getText().toString();

                            if (textUtilsValue(loginStr, passwordStr)) {
                                showToast(getString(R.string.nou_pass_nou_login));
                            } else {

                                saveData(LOGIN_FILE_NAME, loginStr);  ///сохраняем файл во внутр. хр.
                                saveData(PASS_FILE_NAME, passwordStr);
                                showToast(getString(R.string.saving_to_internal_storage));
                            }
                        }
                    });

                    loginButton.setOnClickListener(new View.OnClickListener() {   // onclick читаем файл
                        @Override
                        public void onClick(View v) { //-----------------------------------

                            if (textUtilsValue(loginStr, passwordStr)) {
                                showToast(getString(R.string.nou_pass_nou_login));
                            } else {
                                loginReturn = readLineFromFile(LOGIN_FILE_NAME);   //читаем файл
                                passReturn = readLineFromFile(PASS_FILE_NAME);
                            }

                            if (loginEditable.toString().equals(loginReturn) &&
                                    passEditable.toString().equals(passReturn)) {
                                showToast(getString(R.string.access));
                            } else {
                                showToast(getString(R.string.no_access));
                            }
                        }
                    });

                } else {
                    // no checked   работаем с внешним хр.
                    if (!isAvailable() || isReadOnly()) {
                        // если доступа нет, то делаем кнопки для сохранения не актив.
                        // и считывания с внешней памяти неактивными
                        registrationButton.setEnabled(false);
                        loginButton.setEnabled(false);
                    } else {
                        // если доступ есть, то создаем файл в ExternalStorage
                        mFile = new File(getExternalFilesDir(filePath), fileName);
                    }

                    registrationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {  // онклик сохранения

                            loginStr = loginTextView.getText().toString();
                            passwordStr = passwordTextView.getText().toString();

                            if (textUtilsValue(loginStr, passwordStr)) {
                                showToast(getString(R.string.nou_pass_nou_login));
                            } else {

                                saveExt(loginStr + passwordStr);  ///сохраняем файл
                                //  saveExt(passwordStr);
                                showToast(getString(R.string.saving_to_external_storage));
                            }
                        }
                    });

                    loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {  //онклик чтения
                            String massage = null;

                            if (textUtilsValue(loginStr, passwordStr)) {
                                showToast(getString(R.string.nou_pass_nou_login));
                            } else {  //считываем из внешнее
                                massage = readLineFromFileExt(mFile);
                                showToast(getString(R.string.data_obtained_from_external_memory));
                            }

                            if ((loginEditable.toString() + passEditable.toString()).equals(massage)) {
                                showToast(getString(R.string.access));
                            } else {
                                showToast(getString(R.string.no_access));
                            }
                        }
                    });
                }
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
    }     //ф-я чтения. во внутр. ф-л

    private void saveExt(String editText) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(mFile);
            fos.write(editText.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }     //ф-я сохр. во внешн. ф-л

    private void saveData(String fileName, String text) {  //save во внутр.
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
    }  //ф-я сохр. во внутр. ф-л

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

}

