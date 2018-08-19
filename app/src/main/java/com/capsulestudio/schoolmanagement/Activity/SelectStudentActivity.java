package com.capsulestudio.schoolmanagement.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.StudentsAttendance;
import com.capsulestudio.schoolmanagement.R;

import java.util.List;

public class SelectStudentActivity extends AppCompatActivity {

    private Spinner spinner_class, spinner_student;
    ImageView imageView;
    Button buttonSave;
    List<Classes> className;
    List<StudentsAttendance> studentName;
    String class_name, section_name, Table_Name;
    int id_student;

    private DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_student);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHandler(this);

        // getting views
        imageView = (ImageView) findViewById(R.id.profile_image);
        spinner_class = (Spinner) findViewById(R.id.spinner_classes);
        spinner_student = (Spinner) findViewById(R.id.spinner_students);
        buttonSave = (Button) findViewById(R.id.btn_save);

        // load spinner Classes
        loadClasses();

        // click listener
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectStudentActivity.this, ViewAllTestHistory.class);
                intent.putExtra("class_name", class_name);
                intent.putExtra("section_name", section_name);
                intent.putExtra("id_student", id_student);
                startActivity(intent);
            }
        });

    }

    private void loadClasses() {

        className = db.getAllClasses();

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
                class_name = students.getClass_name();
                section_name = students.getSection();

                // load student spinner
                loadStudent(class_name, section_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadStudent(final String class_name, final String section_name) {

        studentName = db.getAllStudents(class_name, section_name);
        final List<String> data = db.getAllStudentsBinding(class_name, section_name);

        if (data.isEmpty()) {
            imageView.setImageResource(R.mipmap.camera);
            Toast.makeText(getApplicationContext(), "No students found for this class", Toast.LENGTH_SHORT).show();
        }
        if(studentName.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "No attendance data found", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, data);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // attaching data adapter to spinner
        spinner_student.setAdapter(dataAdapter);

        spinner_student.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                StudentsAttendance students = studentName.get(position);
                imageView.setImageBitmap(convertToBitmap(students.getPoto()));

                id_student = students.getId_student();
                Table_Name = "tbl_result_" + class_name.toLowerCase() + "_" + section_name.toLowerCase();

                //Toast.makeText(getApplicationContext(), students.getStudent_name().toString().trim(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b) {

        return BitmapFactory.decodeByteArray(b, 0, b.length);

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
            Intent intent = new Intent(SelectStudentActivity.this, GenerateStudentsMarksPDFActivity.class);
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
