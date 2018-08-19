package com.capsulestudio.schoolmanagement.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Adapter.ViewStudentsAttendanceAdapter;
import com.capsulestudio.schoolmanagement.Adapter.ViewTestAdapter;
import com.capsulestudio.schoolmanagement.Helper.Config;
import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.Marks;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewTestActivity extends AppCompatActivity {

    private Spinner spinner_class;
    private TextView textViewDate;
    private ListView listViewShowStudentsTest;
    private LinearLayout LayEmpty;
    ViewTestAdapter adapter;
    List<Marks> studentName;
    private DatabaseHandler db;
    SQLiteDatabase dbHelper;

    String class_name, section_name, yearInString, month, dayLongName, currentDate;
    int day, year, class_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = openOrCreateDatabase(Config.DATABASE_NAME, MODE_PRIVATE, null);
        db = new DatabaseHandler(this);  // Database object create

        // getting views
        LayEmpty = (LinearLayout) findViewById(R.id.empty_view);
        spinner_class = (Spinner) findViewById(R.id.spinner_classes);
        textViewDate = (TextView) findViewById(R.id.txtDate);
        listViewShowStudentsTest = (ListView) findViewById(R.id.listViewShowStudentsTest);

        final String[] monthName = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};

        //getting the current Year, Month, Day and Day of Week
        final Calendar now = Calendar.getInstance();
        //now.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentDate = sdf.format(now.getTime());
        Log.e("Date", currentDate);
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
                DatePickerDialog datePicker = new DatePickerDialog(ViewTestActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        textViewDate.setText(monthName[monthOfYear] + " " + dayOfMonth + ", " + year);
                        adapter.clear();
                        // load student spinner
                        loadStudentTest(class_name, section_name);
                    }
                }, yy, mm, dd);
                datePicker.show();}
        });

        // Setting Current Date
        textViewDate.setText(dayLongName + ", " + month + " " + day + ", " + yearInString);

        // load spinner Classes
        loadClasses();


    }

    private void loadClasses() {

        final List<Classes> className = db.getAllClasses();

        if(className.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("There is no class to show. Please add classes and students to view marks.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        }

        final List<String> data = db.getAllClassAndSentionInBinding();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, data);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // attaching data adapter to spinner
        spinner_class.setAdapter(dataAdapter);

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Classes students = className.get(position);
                class_id = students.getId();
                class_name = students.getClass_name();
                section_name = students.getSection();

                // load student spinner
                loadStudentTest(class_name, section_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadStudentTest(final String class_name, final String section_name) {

        studentName = db.getAllStudentsTest(class_name, section_name, currentDate);

        //creating the adapter object
        adapter = new ViewTestAdapter(this, R.layout.student_marks_list, studentName, dbHelper);

        // Checking adapter is empty or not
        if (adapter.isEmpty()) {
            LayEmpty.setVisibility(View.VISIBLE);
            listViewShowStudentsTest.setVisibility(View.GONE);
        } else {
            LayEmpty.setVisibility(View.GONE);
            listViewShowStudentsTest.setVisibility(View.VISIBLE);
        }

        listViewShowStudentsTest.setAdapter(adapter);
        listViewShowStudentsTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Marks marks = studentName.get(position);
                Intent intent = new Intent(getApplicationContext(), UpdateTestActivity.class);
                intent.putExtra("marks", marks);  // pass the object of a student class
                startActivity(intent);
            }
        });

        listViewShowStudentsTest.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final SweetAlertDialog sd = new SweetAlertDialog(ViewTestActivity.this, SweetAlertDialog.WARNING_TYPE);
                sd.setTitleText("Delete !");
                sd.setContentText("Are You Sure To Delete.");
                sd.setConfirmText("Yes, Delete It!");
                sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        Marks marks = studentName.get(position);
                        String Table_name = "tbl_result_";
                        // concatenate
                        Table_name += class_name + "_" + section_name;
                        String sql = "DELETE FROM " + Table_name + " WHERE id = ?";
                        dbHelper.execSQL(sql, new Integer[]{marks.getId()});
                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        loadClasses();
                        sd.dismissWithAnimation();

                    }
                });
                sd.show();


                return true;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
