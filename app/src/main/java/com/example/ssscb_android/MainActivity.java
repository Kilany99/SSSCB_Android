package com.example.ssscb_android;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {
    String a1,a3;
    String a2;
    Bitmap img1;
    static boolean active = false;
    int currentPostedDataID,newPostedDataID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_type = findViewById(R.id.tv_crimeType);
        TextView tv_dt = findViewById(R.id.tv_datetime);
        ImageView iv_crime_img = findViewById(R.id.iv_crime_image);
        Button btn = findViewById(R.id.btn_get_data);
        TextView tv_camIP = findViewById(R.id.tv_cameraIP);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView tv_loading = findViewById(R.id.tv_loading);
        Button clear_btn = findViewById(R.id.btn_clear);
        while (active){
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (currentPostedDataID != newPostedDataID) {
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }
                                        String token = task.getResult();
                                        GetFromToken(token);

                                    }
                                });

                        iv_crime_img.setVisibility(View.VISIBLE);
                        iv_crime_img.setImageBitmap(img1);
                        tv_dt.setText("Capture Date: "+a1);
                        tv_camIP.setText("Zone: "+ a2);
                        tv_type.setText("Anomaly Type:" + a3 );
                        progressBar.setVisibility(View.INVISIBLE);
                        tv_loading.setVisibility(View.INVISIBLE);
                        currentPostedDataID= newPostedDataID;
                    }
                    handler.postDelayed(this, 3000); // Optional, to repeat the task.
                }
            };
            handler.postDelayed(runnable, 3000);

        }
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_crime_img.setVisibility(View.INVISIBLE);
                tv_dt.setText("Capture Date: ");
                tv_camIP.setText("Zone: ");
                tv_type.setText("Anomaly Type:" );
            }
        });


//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//
//                        // Log and toast
//
//                        Log.d("TAG", token);
//                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_LONG).show();
//                        et_token.setText(token);
//                    }
//                });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }
                                String token = task.getResult();
                                GetFromToken(token);

                            }
                        });
                iv_crime_img.setVisibility(View.VISIBLE);
                iv_crime_img.setImageBitmap(img1);
                tv_dt.setText("Capture Date: "+a1);
                tv_camIP.setText("Zone: "+ "Zone "+ a2);
                tv_type.setText("Anomaly Type:" + a3 );
                progressBar.setVisibility(View.INVISIBLE);
                tv_loading.setVisibility(View.INVISIBLE);




            }
        });

    }

    @Override
    public void onStart(){
        active = true;
        super.onStart();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        GetIdFromToken(token);
                        GetFromToken(token);

                    }
                });


    }

    @Override
    public void onStop(){
    super.onStop();
    active=false;
    }
    private void getValues(@Path("id") String id) {

        Call<Results> call = RetrofitClient.getInstance().getMyApi().getValues(id);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(@NonNull Call<Results> call, @NonNull Response<Results> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String successResponse = gson.toJson(response.body());
                    byte[] decodedString = Base64.decode(response.body().CrimeScreenshot, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    img1 = decodedByte;
                    a1 = response.body().AnomalyDateTime;
                    a2 = String.valueOf(response.body().ZoneID);
                    Log.e("keshav", "getResponseCode  -->  " + response.body().ZoneID);
                    a3 = response.body().anomalyType;
                    Toast tas = Toast.makeText(getApplicationContext()," Data fetched successfully! ",Toast.LENGTH_LONG);
                    tas.show();
                    currentPostedDataID = response.body().PostedDataId;
                } else {
                    try {
                        if (null != response.errorBody()) {
                            String errorResponse = response.errorBody().string();
                            Toast ts = Toast.makeText(getApplicationContext(),errorResponse +" 2 ",Toast.LENGTH_LONG);
                            ts.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Results> call, @NonNull Throwable t) {
                Toast ts = Toast.makeText(getApplicationContext(),t.toString() +" 3 ",Toast.LENGTH_LONG);

                ts.show();
            }
        });

    }






    private void GetFromToken(@Path("id") String id) {

        Call<Results> call = RetrofitClient.getInstance().getMyApi().GetFromToken(id);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(@NonNull Call<Results> call, @NonNull Response<Results> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String successResponse = gson.toJson(response.body());
                    byte[] decodedString = Base64.decode(response.body().CrimeScreenshot, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    img1 = decodedByte;
                    a1 = response.body().AnomalyDateTime;
                    a2 = String.valueOf(response.body().ZoneID);
                    Log.e("keshav", "getResponseCode  -->  " + response.body().ZoneID);
                    a3 = response.body().anomalyType;
                    Toast tas = Toast.makeText(getApplicationContext()," Data fetched successfully! ",Toast.LENGTH_LONG);
                    tas.show();
                    newPostedDataID = response.body().PostedDataId;
                    Log.e("asdghalb","ZoneID"+response.body().ZoneID);
                } else {
                    try {
                        if (null != response.errorBody()) {
                            String errorResponse = response.errorBody().string();
                            Toast ts = Toast.makeText(getApplicationContext(),errorResponse +" 2 ",Toast.LENGTH_LONG);
                            ts.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Results> call, @NonNull Throwable t) {
                Toast ts = Toast.makeText(getApplicationContext(),t.toString() +" 3 ",Toast.LENGTH_LONG);

                ts.show();
            }
        });

    }



    private void GetIdFromToken(@Path("id") String id) {

        Call<IDModel> call = RetrofitClient.getInstance().getMyApi().GetIdFromToken(id);
        call.enqueue(new Callback<IDModel>() {
            @Override
            public void onResponse(@NonNull Call<IDModel> call, @NonNull Response<IDModel> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String successResponse = gson.toJson(response.body());

                    currentPostedDataID = response.body().PostedDataId;
                } else {
                    try {
                        if (null != response.errorBody()) {
                            String errorResponse = response.errorBody().string();
                            Toast ts = Toast.makeText(getApplicationContext(),errorResponse +" 2 ",Toast.LENGTH_LONG);
                            ts.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<IDModel> call, @NonNull Throwable t) {
                Toast ts = Toast.makeText(getApplicationContext(),t.toString() +" 3 ",Toast.LENGTH_LONG);

                ts.show();
            }
        });

    }


}
