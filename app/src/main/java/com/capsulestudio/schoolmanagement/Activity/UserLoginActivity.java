package com.capsulestudio.schoolmanagement.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Api.ApiService;
import com.capsulestudio.schoolmanagement.Api.ApiURL;
import com.capsulestudio.schoolmanagement.Helper.NetworkConf;
import com.capsulestudio.schoolmanagement.Helper.PrefManager;
import com.capsulestudio.schoolmanagement.Helper.SharedPrefManager;
import com.capsulestudio.schoolmanagement.Model.Result;
import com.capsulestudio.schoolmanagement.Model.UserLogin;
import com.capsulestudio.schoolmanagement.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserLoginActivity extends AppCompatActivity {


    public CheckBox show_hide_password;
    public EditText editTextEmail, editTextPassWord;
    public Button login;

    public String email;
    public String password;

    private NetworkConf networkConf;
    private PrefManager prefManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        /** Checking for first time launch - before calling setContentView() **/
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            finish();
            startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
        }

        /**
         *  Network Connection Check
         */
        networkConf = new NetworkConf(this);

        if (!networkConf.isNetworkAvailable()) {
            networkConf.createNetErrorDialog();
            return;
        }

        // Getting Views
        login = (Button) findViewById(R.id.login);
        editTextEmail = (EditText) findViewById(R.id.loginEmail);
        editTextPassWord = (EditText) findViewById(R.id.loginPassword);
        show_hide_password = (CheckBox) findViewById(R.id.show_hide_password);

        //** Set check listener over checkbox for showing and hiding password **//*
        show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {

                // If it is checked then show password else hide password
                if (isChecked) {
                    show_hide_password.setText(R.string.hide_pwd);   // change checkbox text

                    editTextPassWord.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editTextPassWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
                } else {
                    show_hide_password.setText(R.string.show_pwd); // change checkbox text

                    editTextPassWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    editTextPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });


    }

    private void userLogin() {

        email = editTextEmail.getText().toString().trim();
        password = editTextPassWord.getText().toString().trim();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        final SweetAlertDialog progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setCancelable(false);
        progressDialog.setTitleText("Signing In...");
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiURL.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);


        Call<Result> call = service.userLogin(email, password);

        call.enqueue(new Callback<Result>() {
                         @Override
                         public void onResponse(Call<Result> call, Response<Result> response) {
                             progressDialog.dismiss();
                             if (!response.body().getError()) {

                                 /** Checking Licence Validity
                                  **/

                                 String reg_date = response.body().getUser().getReg_date(); //"2018-01-08"
                                 String dateParts[] = reg_date.split("-");
                                 String year = dateParts[0]; // 2018
                                 String month = dateParts[1]; // 01
                                 String day = dateParts[2]; // 08

                                 // Creates two calendars instances
                                 Calendar cal1 = Calendar.getInstance();

                                 Calendar cal2 = Calendar.getInstance();
                                 cal2.add(Calendar.DATE, 1);
                                 SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                                 String formatted = format1.format(cal2.getTime());
                                 String dateParts1[] = formatted.split("-");
                                 String year1 = dateParts1[0]; // 2018
                                 String month1 = dateParts1[1]; // 01
                                 String day1 = dateParts1[2]; // 08

                                 // Set the date for both of the calendar instance
                                 cal1.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                                 cal2.set(Integer.parseInt(year1), Integer.parseInt(month1), Integer.parseInt(day1));

                                 // Get the represented date in milliseconds
                                 long millis1 = cal1.getTimeInMillis();
                                 long millis2 = cal2.getTimeInMillis();

                                 // Calculate difference in milliseconds
                                 long diff = millis2 - millis1;

                                 // Calculate difference in days
                                 long diffDays = diff / (24 * 60 * 60 * 1000);
                                 Log.e("Diff: ", String.valueOf(diffDays));

                                 if (diffDays > 365) {
                                     final SweetAlertDialog sd = new SweetAlertDialog(UserLoginActivity.this, SweetAlertDialog.WARNING_TYPE);
                                     sd.setTitleText("Expired!");
                                     sd.setContentText("Your One Year Licence Expired.\nContact Provider for more information.");
                                     sd.setConfirmText("Ok");
                                     sd.setCancelable(true);
                                     sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                         @Override
                                         public void onClick(SweetAlertDialog sweetAlertDialog) {
                                             sweetAlertDialog.dismissWithAnimation();
                                         }
                                     });
                                     sd.show();
                                 } else {
                                     //storing the user in shared preferences
                                     SharedPrefManager.getInstance(UserLoginActivity.this).userLogin(response.body().getUser().getId_user(), response.body().getUser().getFirst_name(), response.body().getUser().getLast_name(),
                                             response.body().getUser().getEmail(), response.body().getUser().getPassword(), response.body().getUser().getMobile(), response.body().getUser().getSchool_name(),
                                             response.body().getUser().getStatus(), response.body().getUser().getReg_date());

                                     HashMap<String, String> user = SharedPrefManager.getInstance(UserLoginActivity.this).getUserDetails();
                                     Log.e("UserLogin: ",user.get(SharedPrefManager.KEY_REG_DATE));



                                     prefManager.setFirstTimeLaunch(true);

                                     Toast.makeText(getApplicationContext(), "Welcome " + response.body().getUser().getFirst_name() + " " + response.body().getUser().getLast_name(), Toast.LENGTH_SHORT).show();
                                     finish();
                                     startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
                                 }
                             } else {
                                 Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_LONG).show();
                             }
                         }
                         @Override
                         public void onFailure(Call<Result> call, Throwable t) {
                             progressDialog.dismiss();
                             Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                         }
                     }
        );
    }
}
