package com.capsulestudio.schoolmanagement.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.capsulestudio.schoolmanagement.Adapter.ViewStudentAttendanceDetailsAdapter;
import com.capsulestudio.schoolmanagement.Adapter.ViewStudentsAttendanceAdapter;
import com.capsulestudio.schoolmanagement.Helper.Config;
import com.capsulestudio.schoolmanagement.Model.StudentsAttendance;
import com.capsulestudio.schoolmanagement.R;

import java.util.ArrayList;
import java.util.List;

public class AttendanceDetailsActivity extends AppCompatActivity {

    int id_student;
    String class_name, section, student_table, student_attendance_table;
    private TextView textViewName, textViewClass, textViewAtd;
    private ImageView profilePic;
    private ListView listViewShowAttendance;
    private LinearLayout LayEmpty;
    SQLiteDatabase dbHelper;

    List<StudentsAttendance> studentAttendanceList;
    List<StudentsAttendance> studentList;
    ViewStudentAttendanceDetailsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Opening Database
        dbHelper = openOrCreateDatabase(Config.DATABASE_NAME, MODE_PRIVATE, null);

        studentAttendanceList = new ArrayList<>();
        studentList = new ArrayList<>();

        // get intent values
        Intent intent = getIntent();
        id_student = intent.getIntExtra("ID_STUDENT", 0);
        class_name = intent.getStringExtra("Class_name");
        section = intent.getStringExtra("Section");


        // getting views
        profilePic = (ImageView) findViewById(R.id.profile_image);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewClass = (TextView) findViewById(R.id.textViewClassRoll);
        textViewAtd = (TextView) findViewById(R.id.textViewAtd);
        LayEmpty = (LinearLayout) findViewById(R.id.empty_view);
        listViewShowAttendance = (ListView) findViewById(R.id.listViewStudentsAttendance);

        showStudentsFromDatabase(class_name, section, id_student);
    }

    private void showStudentsFromDatabase(String class_name, String section, int id_student) {

        String class_section = class_name.toLowerCase() + "_" + section.toLowerCase();
        student_table = "tbl_students_" + class_section;
        student_attendance_table = "tbl_attendance_" + class_name.toLowerCase() + "_" + section.toLowerCase();

        String query = "SELECT * FROM " + student_table + " WHERE id = ?";
        Cursor cursorStudents = dbHelper.rawQuery(query, new String[]{String.valueOf(id_student)});

        if (cursorStudents != null && cursorStudents.getCount() >= 0) {
            //if the cursor has some data
            if (cursorStudents.moveToFirst()) {
                //looping through all the records
                do {
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
        }
        //closing the cursor
        cursorStudents.close();

        StudentsAttendance students = studentList.get(0);

        profilePic.setImageBitmap(convertToBitmap(students.getPoto()));
        textViewName.setText(students.getStudent_name());
        textViewClass.setText(students.getStudent_roll() + " - " + students.getClass_name() + "(" + students.getSection() + ")");


        String atdQuery = "SELECT * FROM " + student_attendance_table + " WHERE id_student = ? ORDER BY atd_date DESC";
        Cursor cursorAttendance = dbHelper.rawQuery(atdQuery, new String[]{String.valueOf(id_student)});

        if (cursorAttendance != null && cursorAttendance.getCount() > 0) {
            //if the cursor has some data
            if (cursorAttendance.moveToFirst()) {
                //looping through all the records
                do {
                    //pushing each record in the class list
                    studentAttendanceList.add(new StudentsAttendance(
                            cursorAttendance.getInt(0),
                            cursorAttendance.getString(1),
                            cursorAttendance.getString(2),
                            cursorAttendance.getString(3),
                            cursorAttendance.getInt(4),
                            cursorAttendance.getInt(5)
                    ));
                } while (cursorAttendance.moveToNext());
            }
        }

        int present = 0, absent = 0, total=  0;
        total = studentAttendanceList.size();
        for (StudentsAttendance d : studentAttendanceList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getAttendance_status().trim().contains("Present")) {
                present++;
            } else if (d.getAttendance_status().trim().contains("Absent")) {
                absent++;
            }
        }

        //closing the cursor
        cursorAttendance.close();
        //creating the adapter object
        adapter = new ViewStudentAttendanceDetailsAdapter(this, R.layout.student_view_attendance_details_list, studentAttendanceList, dbHelper, class_name, section);

        // Checking adapter is empty or not
        if (adapter.isEmpty()) {
            LayEmpty.setVisibility(View.VISIBLE);
        } else {
            LayEmpty.setVisibility(View.GONE);
        }
        //adding the adapter to listview
        listViewShowAttendance.setAdapter(adapter);
        textViewAtd.setText("Total Class: " + total + " Present : " + present + " Absent : " + absent);


    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.menu_search, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    listViewShowAttendance.clearTextFilter();
                } else {
                    adapter.filter(newText);
                }
                return true;
            }
        });
        return true;
    }


    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b){

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        startActivity(new Intent(AttendanceDetailsActivity.this, ViewAttendanceActivity.class));
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(AttendanceDetailsActivity.this, ViewAttendanceActivity.class));
    }
}
