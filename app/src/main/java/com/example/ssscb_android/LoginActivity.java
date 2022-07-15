package com.example.ssscb_android;


import static com.example.ssscb_android.Api.BASE_URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.content.SharedPreferences;

import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private int ResponeCode ;
    private String userId ;
    private String password;
    private Button login_btn ;
    private EditText et_Email;
    private EditText et_Pass ;
    private CheckBox cb_remeberMe;
    private Boolean saveLogin = false;
    SharedPreferences savePreferences = new SharedPreferences() {
        @Override
        public Map<String, ?> getAll() {
            return null;
        }

        @Nullable
        @Override
        public String getString(String key, @Nullable String defValue) {
            return null;
        }

        @Nullable
        @Override
        public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
            return null;
        }

        @Override
        public int getInt(String key, int defValue) {
            return 0;
        }

        @Override
        public long getLong(String key, long defValue) {
            return 0;
        }

        @Override
        public float getFloat(String key, float defValue) {
            return 0;
        }

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            return false;
        }

        @Override
        public boolean contains(String key) {
            return false;
        }

        @Override
        public Editor edit() {
            return null;
        }

        @Override
        public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

        }

        @Override
        public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

        }
    };
    SharedPreferences.Editor savePrefsEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn = findViewById(R.id.btn_login);
        et_Email = findViewById(R.id.et_username);
        et_Pass = findViewById(R.id.et_password);
        saveLogin = savePreferences.getBoolean("saveLogin", false);
        if (saveLogin == true)
        {
            et_Email.setText(savePreferences.getString("username", ""));
            et_Pass.setText(savePreferences.getString("password", ""));
            cb_remeberMe.setChecked(true);
        }

        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                userId = et_Email.getText().toString();
                password = et_Pass.getText().toString();
                if (checkValidation()) {
                    if (CommonMethod.isNetworkAvailable(LoginActivity.this))
                    {
                        if (loginRetrofit2Api(userId, password)==200) {

                            savePreferences = getSharedPreferences("savePrefs", 0);
                            savePrefsEditor = savePreferences.edit();
                            if (cb_remeberMe.isChecked()) {
                                //Set the data
                                savePrefsEditor.putBoolean("saveLogin", true);
                                savePrefsEditor.putString("username", userId);
                                savePrefsEditor.putString("password", password);
                                //Commit the changes
                                savePrefsEditor.commit();
                                Toast.makeText(getApplicationContext(), "Your details have been saved.", Toast.LENGTH_LONG).show();
                            }
                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            myIntent.putExtra("key", userId);
                            startActivity(myIntent);
                        }
                        else
                            Toast.makeText(LoginActivity.this, "Invalid Login Details \n Please try again", Toast.LENGTH_SHORT).show();

                    }
                    else
                        CommonMethod.showAlert("Internet Connectivity Failure", LoginActivity.this);
                }
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();
                                // Log and toast

                                Log.d("TAG", token);
                                Toast.makeText(LoginActivity.this, token, Toast.LENGTH_LONG).show();
                                putResults(token,userId);

                            }
                        });


            }


        });
    }
    private int loginRetrofit2Api(String userId, String password) {
        LoginResponse login = new LoginResponse(userId, password);
        Call<LoginResponse> call1 = RetrofitClient.getInstance().getMyApi().createUser(login);
        call1.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = new LoginResponse(response.body().UserId,response.body().Password);
                loginResponse.ResponseMessage = response.body().ResponseMessage;
                loginResponse.ResponseCode = response.body().ResponseCode;

                Log.e("keshav", "loginResponse 1 --> " + loginResponse.ResponseMessage);
                if (loginResponse.ResponseCode != null) {
                    Log.e("keshav", "getUserId      -->  " + loginResponse.getUserId());

                    String responseCode = loginResponse.getResponseCode();
                    Log.e("keshav", "getResponseCode  -->  " + loginResponse.getResponseCode());
                    Log.e("keshav", "getResponseMessage  -->  " + loginResponse.getResponseMessage());
                    if (responseCode != null && responseCode.toString().compareTo("401")==0) {
                        {
                            Toast.makeText(LoginActivity.this, "Invalid Login Details \n Please try again 11", Toast.LENGTH_SHORT).show();
                            ResponeCode = 401;
                        }
                    } else if (responseCode != null && responseCode.toString().compareTo("200")==0) {
                        Toast.makeText(LoginActivity.this, "Welcome " + loginResponse.getUserId(), Toast.LENGTH_SHORT).show();
                        ResponeCode = 200;
                    }
                    else {
                        ResponeCode = 401;
                        Toast.makeText(LoginActivity.this, "an error occurred! ", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure called", Toast.LENGTH_SHORT).show();
                Log.e("asdghalb",t.toString());
                call.cancel();
                ResponeCode = 401;
            }
        });
        return ResponeCode;
    }

    public boolean checkValidation() {
        userId = et_Email.getText().toString();
        password = et_Pass.getText().toString();

        Log.e("Keshav", "userId is -> " + userId);
        Log.e("Keshav", "password is -> " + password);

        if (et_Email.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("UserId Cannot be left blank", LoginActivity.this);
            return false;
        } else if (et_Pass.getText().toString().trim().equals("")) {
            CommonMethod.showAlert("password Cannot be left blank", LoginActivity.this);
            return false;
        }
        return true;
    }



    private void putResults(String devicetoken,String userName) {

        Call<PutModel> call1 = RetrofitClient.getInstance().getMyApi().putResults(userName,devicetoken);
        call1.enqueue(new Callback<PutModel>() {
            @Override
            public void onResponse(Call<PutModel> call, Response<PutModel> response) {
                String Response = response.body().ResponseCode;

                if (Response != null) {
                    Log.e("keshav", "The response : " + Response);


                }
            }

            @Override
            public void onFailure(Call<PutModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "onFailure called 2", Toast.LENGTH_SHORT).show();
                call.cancel();
                Log.e("keshav", "The error in put request : " + t.toString());

            }
        });
    }

}