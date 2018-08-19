package com.capsulestudio.schoolmanagement.Activity;

import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Adapter.StudentAdapter;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.capsulestudio.schoolmanagement.R.id.textViewDate;

public class BirthdayWishActivity extends AppCompatActivity {

    private Spinner spinner_class;
    private TextView textViewDate;
    private LinearLayout LayEmpty;
    private ListView listViewShowStudents;
    List<Students> studentList;
    StudentAdapter adapter;
    SQLiteDatabase dbHelper;
    private static final String DataBaseName = "school_management";
    public String class_name, section, class_id, student_table;
    String yearInString, month, dayLongName, currentDate, student_attendance_table, student_dob_day_month;
    int day, year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_wish);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHelper = openOrCreateDatabase(DataBaseName, MODE_PRIVATE, null);

        //getting views
        LayEmpty = (LinearLayout) findViewById(R.id.empty_view);
        spinner_class = (Spinner) findViewById(R.id.spinner_classes);
        listViewShowStudents = (ListView) findViewById(R.id.listViewShowStudents);
        textViewDate = (TextView) findViewById(R.id.txtDate);

        final String[] monthName = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};

        //getting the current Year, Month, Day and Day of Week
        final Calendar now = Calendar.getInstance();
        //now.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        //currentDate = sdf.format(now.getTime());
        student_dob_day_month = simpleDateFormat.format(now.getTime());
        Log.e("Date", student_dob_day_month);
        year = now.get(Calendar.YEAR);
        yearInString = String.valueOf(year);
        month = monthName[now.get(Calendar.MONTH)];
        day = now.get(Calendar.DAY_OF_MONTH);
        dayLongName = now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        // setting current date
        textViewDate.setText(dayLongName + ", " + month + " " + day + ", " + yearInString);

        // load spinner classes
        loadSpinnerClasses();

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(BirthdayWishActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date, day = null, month = null;

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


                        //currentDate = date;
                        student_dob_day_month = month + "-" + day;
                        loadSpinnerClasses();
                        Log.e("Time : ", student_dob_day_month);
                        textViewDate.setText(monthName[monthOfYear] + " " + dayOfMonth + ", " + year);

                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });


        listViewShowStudents = (ListView) findViewById(R.id.listViewShowStudents);
        studentList = new ArrayList<>();

    }

    private void loadSpinnerClasses() {
        final List<Classes> data = new ArrayList<>();
        final List<String> fileName = new ArrayList<>();
        Cursor cursor = dbHelper.rawQuery("SELECT * FROM tbl_classes", null);


        if (cursor.getCount() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("There is no class to show. Please add classes to add students respectively.")
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

    private void showStudentsFromDatabase(final String classNameL, String SectionL) {

        student_table = "tbl_students_" + classNameL.toLowerCase() + "_" + SectionL.toLowerCase();

        String query = "SELECT * FROM " + student_table + " WHERE student_dob_day_month = ?";
        Cursor cursorStudents = dbHelper.rawQuery(query, new String[]{student_dob_day_month});


        //if the cursor has some data
        if (cursorStudents.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the class list
                studentList.add(new Students(
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
        adapter = new StudentAdapter(this, R.layout.student_list, studentList, dbHelper);
        //Toast.makeText(getApplicationContext(), studentList.toString(), Toast.LENGTH_LONG).show();

        // Checking adapter is empty or not
        if (adapter.isEmpty()) {
            LayEmpty.setVisibility(View.VISIBLE);
        } else {
            LayEmpty.setVisibility(View.GONE);
        }

        // On click listener
        listViewShowStudents.setAdapter(adapter);
        listViewShowStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Students student = studentList.get(position);

                final SweetAlertDialog sd = new SweetAlertDialog(BirthdayWishActivity.this, SweetAlertDialog.WARNING_TYPE);
                sd.setTitleText("Birthday Wish!");
                sd.setContentText("Are You Sure To Wish: " + student.getStudent_name());
                sd.setConfirmText("Yes!");
                sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", student.getStudent_mobile());
                        smsIntent.putExtra("sms_body", "Happy Birthday to " + student.getStudent_name());
                        startActivity(smsIntent);

                        sd.dismissWithAnimation();

                    }
                });
                sd.show();
            }
        });
    }

    private void reloadFromDatabase() {

        Cursor cursorStudent = dbHelper.rawQuery("SELECT * FROM " + student_table, null);
        if (cursorStudent.moveToFirst()) {
            studentList.clear();
            do {
                studentList.add(new Students(
                        cursorStudent.getInt(0),
                        cursorStudent.getBlob(1),
                        cursorStudent.getString(2),
                        cursorStudent.getString(3),
                        cursorStudent.getString(4),
                        cursorStudent.getString(5),
                        cursorStudent.getString(6),
                        cursorStudent.getString(7),
                        cursorStudent.getString(8),
                        cursorStudent.getString(9),
                        cursorStudent.getString(10),
                        cursorStudent.getString(11),
                        cursorStudent.getInt(12)

                ));
            } while (cursorStudent.moveToNext());
        }
        //cursorStudent.close();

        // Checking adapter is empty or not
        if (cursorStudent.moveToFirst()) {
            LayEmpty.setVisibility(View.GONE);
        } else {
            LayEmpty.setVisibility(View.VISIBLE);
            listViewShowStudents.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
