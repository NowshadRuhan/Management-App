package com.capsulestudio.schoolmanagement.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Adapter.StudentAttendanceAdapter;
import com.capsulestudio.schoolmanagement.Helper.Config;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.Model.StudentsAttendance;
import com.capsulestudio.schoolmanagement.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MarkAttendanceActivity extends AppCompatActivity {

    private Spinner spinner_class;
    private LinearLayout LayEmpty;
    private ListView listViewShowStudents;
    private TextView textViewDate, textViewTotal, textViewCheckAttendance;
    private Button buttonPost;

    String yearInString, month, dayLongName, currentDate, class_id, class_name, section, student_table, student_attendance_table;
    int dayIn, year;

    List<StudentsAttendance> studentList;
    StudentAttendanceAdapter adapter;

    SQLiteDatabase dbHelper;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    private static final int PERMISSION_REQUEST_CODE = 1;

    int sms = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getApplicationContext().getSharedPreferences("school_management", 0); // O for Private Mode
        // get editor to edit in file
        editor = sharedPreferences.edit();

        // getting views
        LayEmpty = (LinearLayout) findViewById(R.id.empty_view);
        spinner_class = (Spinner) findViewById(R.id.spinner_classes);
        listViewShowStudents = (ListView) findViewById(R.id.listViewShowStudents);
        buttonPost = (Button) findViewById(R.id.btnShow);
        textViewDate = (TextView) findViewById(R.id.txtDate);
        textViewTotal = (TextView) findViewById(R.id.tvTotal);
        textViewCheckAttendance = (TextView) findViewById(R.id.textViewCheckAttendance);

        studentList = new ArrayList<>();

        dbHelper = openOrCreateDatabase(Config.DATABASE_NAME, MODE_PRIVATE, null);

        final String[] monthName = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};

        //getting the current Year, Month, Day and Day of Week
        final Calendar now = Calendar.getInstance();
        //now.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentDate = sdf.format(now.getTime());
        //Log.e("Date", currentDate);
        year = now.get(Calendar.YEAR);
        yearInString = String.valueOf(year);
        month = monthName[now.get(Calendar.MONTH)];
        dayIn = now.get(Calendar.DAY_OF_MONTH);
        Log.e("day", String.valueOf(dayIn));
        dayLongName = now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        // setting current date
        textViewDate.setText(dayLongName + ", " + month + " " + dayIn + ", " + yearInString);

        //
        requestPermissionsCheck();

        // load spinner classes
        loadSpinnerClasses();

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(MarkAttendanceActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date, day = null, month = null;

                        dayIn = dayOfMonth;

                        if (dayOfMonth < 10) {
                            day = "0" + String.valueOf(dayOfMonth);

                        } else {
                            day = String.valueOf(dayOfMonth);
                        }
                        if (monthOfYear + 1 < 10) {
                            month = "0" + String.valueOf(monthOfYear + 1);
                        } else {
                            month = String.valueOf(monthOfYear + 1);
                        }

                        date = String.valueOf(year) + "-" + month
                                + "-" + day;

                        currentDate = date;
                        loadSpinnerClasses();
                        //Log.e("Time : ", date);
                        textViewDate.setText(monthName[monthOfYear] + " " + dayOfMonth + ", " + year);

                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });


        // Post Button Click Listener
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SweetAlertDialog sd = new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE);
                sd.setTitleText("Are you sure?");
                sd.setContentText("You can edit this in View Attendance section.");
                sd.setConfirmText("Yes,submit it.");
                sd.setCancelable(true);
                sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        String data = "";
                        List<StudentsAttendance> stList = ((StudentAttendanceAdapter) adapter).getStudentist();

                        int present = 0, j = 0;
                        int[] result = new int[stList.size()];
                        for (int i = 0; i < stList.size(); i++) {
                            final StudentsAttendance singleStudent = stList.get(i);

                            if (singleStudent.isSelected() == true) {
                                result[j] = singleStudent.getId();
                                present++;
                                //Toast.makeText(AttendanceActivity.this, String.valueOf(result[j]) , Toast.LENGTH_LONG).show();
                                j++;
                                data = data + "\n" + singleStudent.getStudent_name().toString();
                                // Insert Attendance
                                String insertSQL = "INSERT INTO " + student_attendance_table + "\n" +
                                        "(atd_status, atd_date, month, day, id_student)\n" +
                                        "VALUES \n" +
                                        "(?, ?, ?, ?, ?);";

                                //Log.e("day", String.valueOf(day));


                                dbHelper.execSQL(insertSQL, new String[]{"Present", currentDate, month, String.valueOf(dayIn), String.valueOf(singleStudent.getId())});

                            } else if (singleStudent.isSelected() == false) {
                                result[j] = singleStudent.getId();
                                present++;
                                //Toast.makeText(AttendanceActivity.this, String.valueOf(result[j]) , Toast.LENGTH_LONG).show();
                                j++;
                                data = data + "\n" + singleStudent.getStudent_name().toString();
                                // Insert Attendance
                                String insertSQL = "INSERT INTO " + student_attendance_table + "\n" +
                                        "(atd_status, atd_date, month, day, id_student)\n" +
                                        "VALUES \n" +
                                        "(?, ?, ?, ?, ?);";

                                dbHelper.execSQL(insertSQL, new String[]{"Absent", currentDate, month, String.valueOf(dayIn), String.valueOf(singleStudent.getId())});

                            }
                        }
                        sd.setTitleText("Posted!")
                                .setContentText("Attendance Report Successfully Submitted to the Server")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        showDialog();
                                        sd.dismissWithAnimation();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                        //Toast.makeText(AttendanceActivity.this, "Selected Students: " + present + "\n" + data, Toast.LENGTH_LONG).show();
                    }

                });
                sd.show();
            }
        });

    }

    private void requestPermissionsCheck() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }

    }

    private void selectStudent() {

        String class_section = class_name.toLowerCase() + "_" + section.toLowerCase();

        student_table = "tbl_students_" + class_section;
        student_attendance_table = "tbl_attendance_" + class_section;

        String status = null;
        if (sms == 1) {
            status = "Present";
        } else if (sms == 2) {
            status = "Absent";
        }

        String query = "SELECT * FROM " + student_table + " a INNER JOIN " + student_attendance_table + " b ON a.id=b.id_student WHERE b.atd_date=? AND b.atd_status=?";

        Cursor cursorStudents = dbHelper.rawQuery(query, new String[]{currentDate, status});

        if (cursorStudents != null && cursorStudents.getCount() > 0) {
            //if the cursor has some data
            if (cursorStudents.moveToFirst()) {
                //looping through all the records
                do {
                    //pushing each record in the class list

                    //Log.e("Mobile Number:", cursorStudents.getString(7));
                    //Log.e("SMS:", cursorStudents.getString(2) + " is " + status + " on " + currentDate);


                    sendSMS(cursorStudents.getString(7), cursorStudents.getString(2) + " is " + status + " on " + currentDate + "\n" + sharedPreferences.getString("School", ""));

                } while (cursorStudents.moveToNext());
            }
        }
        //closing the cursor
        cursorStudents.close();

        // Refreshing Activity
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    private void showDialog() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Send Message!");
        String[] items = {"To Present Only", "To Absent Only", "None"};
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        sms = 1;
                        break;
                    case 1:
                        sms = 2;
                        break;
                    case  2:
                        sms = 3;
                        break;
                }
                if(sms<3)
                {
                    selectStudent();
                }
                else if(sms == 3)
                {
                    // Refreshing Activity
                    Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                }

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void checkAttendanceSubmission() {

        String class_section = class_name.toLowerCase() + "_" + section.toLowerCase();
        String student_attendance_table = "tbl_attendance_" + class_section;

        Cursor cursor = dbHelper.rawQuery("SELECT * FROM " + student_attendance_table + " WHERE atd_date = ?", new String[]{currentDate});

        if (cursor.getCount() > 0) {
            textViewCheckAttendance.setVisibility(View.VISIBLE);
        } else {
            textViewCheckAttendance.setVisibility(View.GONE);
        }
    }

    private void loadSpinnerClasses() {
        final List<Classes> data = new ArrayList<>();
        final List<String> fileName = new ArrayList<>();
        Cursor cursor = dbHelper.rawQuery("SELECT * FROM tbl_classes", null);

        if (cursor.getCount() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("There is no class to show. Please add classes and students to provide attendance.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        }

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //fileName.add(cursor.getInt(0) + "-" + cursor.getString(1) + "(" + cursor.getString(2) + ")");
                fileName.add(cursor.getString(1).toUpperCase() + "(" + cursor.getString(2).toUpperCase() + ")");
                data.add(new Classes(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        // closing connection
        //cursor.close();
        //dbHelper.close();


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, fileName);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_class.setAdapter(dataAdapter);

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Classes classes = data.get(position);
                class_id = String.valueOf((classes.getId()));
                class_name = classes.getClass_name();
                section = classes.getSection();

                if (adapter != null) {
                    studentList.clear();
                }
                showStudentsFromDatabase(class_name, section);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showStudentsFromDatabase(String classNameL, String SectionL) {

        // Check attendance is already submitted or not
        checkAttendanceSubmission();

        student_attendance_table = "tbl_attendance_" + classNameL.toLowerCase() + "_" + SectionL.toLowerCase();
        student_table = "tbl_students_" + classNameL.toLowerCase() + "_" + SectionL.toLowerCase();

        String query = "SELECT * FROM " + student_table;
        Cursor cursorStudents = dbHelper.rawQuery(query, null);

        //if the cursor has some data
        if (cursorStudents.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the class list
                studentList.add(new StudentsAttendance(
                        cursorStudents.getInt(0),
                        cursorStudents.getBlob(1),
                        cursorStudents.getString(2),
                        cursorStudents.getString(3),
                        cursorStudents.getString(4),
                        cursorStudents.getString(5),
                        cursorStudents.getString(6),
                        cursorStudents.getString(7),
                        cursorStudents.getString(8),
                        cursorStudents.getString(9),
                        cursorStudents.getString(10),
                        cursorStudents.getString(11),
                        cursorStudents.getInt(12)

                ));
            } while (cursorStudents.moveToNext());
        }
        //closing the cursor
        cursorStudents.close();
        //creating the adapter object
        adapter = new StudentAttendanceAdapter(this, R.layout.student_take_attendance_list, studentList, dbHelper);
        Toast.makeText(getApplicationContext(), studentList.toString(), Toast.LENGTH_LONG);

        // Checking adapter is empty or not
        if (adapter.isEmpty()) {
            LayEmpty.setVisibility(View.VISIBLE);
        } else {
            LayEmpty.setVisibility(View.GONE);
        }
        //adding the adapter to listview
        listViewShowStudents.setAdapter(adapter);
        textViewTotal.setVisibility(View.VISIBLE);
        textViewTotal.setText("Total Students: " + adapter.getCount());
    }


    private void sendSMS(String phoneNumber, String message) {
        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

        ArrayList<String> smsBodyParts = smsManager.divideMessage(message);
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

        for (int i = 0; i < smsBodyParts.size(); i++) {
            sentPendingIntents.add(sentPendingIntent);
            deliveredPendingIntents.add(deliveredPendingIntent);
        }

        // ---when the SMS has been sent---

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {

                switch (getResultCode()) {

                    case Activity.RESULT_OK:

                        Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();

                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();

                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:

                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();

                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:

                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();

                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:

                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

        // ---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {

                switch (getResultCode()) {

                    case Activity.RESULT_OK:

                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:

                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));


        //   sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        smsManager.sendMultipartTextMessage(phoneNumber, null, smsBodyParts, sentPendingIntents, deliveredPendingIntents);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
