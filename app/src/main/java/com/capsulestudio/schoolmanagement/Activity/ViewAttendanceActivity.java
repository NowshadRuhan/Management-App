package com.capsulestudio.schoolmanagement.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.capsulestudio.schoolmanagement.Adapter.ViewStudentsAttendanceAdapter;
import com.capsulestudio.schoolmanagement.Helper.Config;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.StudentsAttendance;
import com.capsulestudio.schoolmanagement.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewAttendanceActivity extends AppCompatActivity {

    private Spinner spinner_class;
    private EditText editTextSearch;
    private ListView listViewAttendance;
    private LinearLayout LayEmpty, Laylist;
    private TextView textViewDate;

    List<StudentsAttendance> studentList;
    ViewStudentsAttendanceAdapter adapter;

    String class_id, class_name, section, student_table, student_attendance_table, currentDate,  yearInString, month, dayLongName;
    int day, year;
    SQLiteDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // getting views
        spinner_class = (Spinner) findViewById(R.id.spinner_classes);
        editTextSearch = (EditText) findViewById(R.id.edit_search);
        LayEmpty = (LinearLayout) findViewById(R.id.empty_view);
        Laylist = (LinearLayout) findViewById(R.id.laylist);
        textViewDate = (TextView) findViewById(R.id.txtDate);
        listViewAttendance = (ListView) findViewById(R.id.listViewStudentsAttendance);

        // Opening Database
        dbHelper = openOrCreateDatabase(Config.DATABASE_NAME, MODE_PRIVATE, null);

        studentList = new ArrayList<>();

        final String[] monthName = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        //getting the current Year, Month, Day and Day of Week
        final Calendar now = Calendar.getInstance();
        //now.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentDate = sdf.format(now.getTime());
        Log.e("DTime : ", currentDate);
        year = now.get(Calendar.YEAR);
        yearInString = String.valueOf(year);
        month = monthName[now.get(Calendar.MONTH)];
        day = now.get(Calendar.DAY_OF_MONTH);
        dayLongName = now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(ViewAttendanceActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date, day = null, month = null;
                        
                        if(dayOfMonth < 10)
                        {
                            day = "0" + String.valueOf(dayOfMonth);

                        }
                        else
                        {
                            day = String.valueOf(dayOfMonth);
                        }
                        if(monthOfYear+1 < 10)
                        {
                            month = "0" + String.valueOf(monthOfYear+1);
                        }
                        else
                        {
                            month = String.valueOf(monthOfYear+1);
                        }

                        date = String.valueOf(year) + "-" + month
                                + "-" + day;

                        currentDate = date;
                        Log.e("Time : ", date);
                        textViewDate.setText(monthName[monthOfYear] + " " + dayOfMonth + ", " + year);
                        adapter.clear();
                        showStudentsFromDatabase(class_name, section);
                    }
                }, yy, mm, dd);
                datePicker.show();}
        });


        // setting current date
        textViewDate.setText(dayLongName + ", " + month + " " + day + ", " + yearInString);

        // load spinner students
        loadSpinnerClasses();

    }

    private void loadSpinnerClasses() {
        final List<Classes> data = new ArrayList<>();
        final List<String> fileName = new ArrayList<>();
        Cursor cursor = dbHelper.rawQuery("SELECT * FROM tbl_classes", null);

        if(cursor.getCount() == 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("There is no class to show. Please add classes and students to view attendance.")
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
                fileName.add(cursor.getString(1).toUpperCase()+"("+cursor.getString(2).toUpperCase()+")");
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
                Classes students = data.get(position);
                class_id = String.valueOf((students.getId()));
                class_name = students.getClass_name();
                section = students.getSection();

                if (adapter != null){
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

        String class_section = classNameL.toLowerCase() + "_" + section.toLowerCase();

        student_table = "tbl_students_" + class_section;
        student_attendance_table = "tbl_attendance_"+classNameL.toLowerCase()+"_"+SectionL.toLowerCase();

        //String query = "SELECT * FROM " + student_attendance_table + " WHERE atd_date = ?";

        String query = "SELECT * FROM " + student_table + " a INNER JOIN " + student_attendance_table + " b ON a.id=b.id_student WHERE b.atd_date=?";

        Cursor cursorStudents = dbHelper.rawQuery(query, new String[]{currentDate});


        if(cursorStudents!=null && cursorStudents.getCount() > 0) {
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
                            cursorStudents.getInt(12),
                            cursorStudents.getInt(13),
                            cursorStudents.getString(14),
                            cursorStudents.getString(15),
                            cursorStudents.getString(16),
                            cursorStudents.getInt(17),
                            cursorStudents.getInt(18)
                            ));

                } while (cursorStudents.moveToNext());
            }
        }
        //closing the cursor
        cursorStudents.close();
        //creating the adapter object


        adapter = new ViewStudentsAttendanceAdapter(this, R.layout.student_view_attendance_list, studentList, dbHelper);

        // Checking adapter is empty or not
        if (adapter.isEmpty()) {
            LayEmpty.setVisibility(View.VISIBLE);
        } else {
            LayEmpty.setVisibility(View.GONE);
        }
        //adding the adapter to listview
        listViewAttendance.setAdapter(adapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gen_pdf, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();
        if (id == R.id.gen_atd_pdf){
            Intent intent = new Intent(ViewAttendanceActivity.this, GenerateAttendencePDFActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
