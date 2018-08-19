package com.capsulestudio.schoolmanagement.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.capsulestudio.schoolmanagement.Adapter.StudentAdapter;
import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.R;

import java.util.Calendar;
import java.util.List;

public class StudentWithNoFeeActivity extends AppCompatActivity {

    private DatabaseHandler db;
    SQLiteDatabase dbHelper;
    private static final String DataBaseName = "school_management";

    private String class_id;
    private String class_name;
    private String section;

    private TextView textViewCheckFee;
    private Spinner spinnerclasses;
    private Spinner spinnerfeemonth;
    private ListView listView;
    private String feeMonth;
    private StudentAdapter studentAdapter;
    private List<Students> studentsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_with_no_fee);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("No Fee Students");

        spinnerfeemonth = (Spinner) findViewById(R.id.spinner_month_fee);
        spinnerclasses = (Spinner) findViewById(R.id.spinner_classes);
        listView = (ListView) findViewById(R.id.withFeeList);
        textViewCheckFee = (TextView) findViewById(R.id.textViewCheckFee);

        db = new DatabaseHandler(this);
        dbHelper = openOrCreateDatabase(DataBaseName, MODE_PRIVATE, null);

        loadSpinnerClasses();
    }

    private void loadSpinnerClasses() {
        final List<Classes> data = db.getAllClasses();
        final List<String> fileName = db.getAllClassAndSentionInBinding();

        if(fileName.isEmpty())
        {
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, fileName);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerclasses.setAdapter(dataAdapter);

        spinnerclasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Classes classes = data.get(position);

                class_id = String.valueOf((classes.getId()));
                class_name = classes.getClass_name();
                section = classes.getSection();


                if (studentAdapter != null){
                    studentsList.clear();

                }

                loadFeeMonth(class_name, section);
                //showStudentsFromDatabase(class_name, section);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void loadFeeMonth(final String  class_name, final String section){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        final String[] feeMonthData = {"January - "+year,"February - "+year, "March - "+year,
                "April - "+year, "May - "+year, "June - "+year,
                "July - "+year, "August - "+year, "September - "+year,
                "October - "+year, "November - "+year, "December - "+year};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, feeMonthData);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerfeemonth.setAdapter(dataAdapter);

        spinnerfeemonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                feeMonth = feeMonthData[position];

                studentWithNoFee(class_name, section, feeMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void studentWithNoFee(String class_name, String section, String feeMonth) {

        studentsList = db.getAllStudentsMonthNoFeeHistory(class_name, section, feeMonth);

        if (studentsList.isEmpty()) {
            textViewCheckFee.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            textViewCheckFee.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        studentAdapter = new StudentAdapter(this, R.layout.student_list, studentsList, dbHelper);
        listView.setAdapter(studentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Students getPapi = studentsList.get(position);

                Intent intent = new Intent(getApplicationContext(), StudentFeeHistoryViewActivity.class);
                intent.putExtra("studentID", getPapi.getId());
                intent.putExtra("className", getPapi.getClass_name());
                intent.putExtra("section", getPapi.getSection());
                startActivity(intent);

            }
        });

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
            Intent intent = new Intent(StudentWithNoFeeActivity.this, GenerateStudentWithNoFeePDFActivity.class);
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
