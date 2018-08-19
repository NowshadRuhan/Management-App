package com.capsulestudio.schoolmanagement.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.R;

import java.util.List;

public class FeeHistoryActivity extends AppCompatActivity {

    private ImageView showprofileimage;
    private Spinner spinnerclasses;
    private Spinner spinnerstudent;
    private Button btnFindHistory;

    private DatabaseHandler db;
    private String class_id;
    private String class_name;
    private String section;
    private int studentId;
    private String nameAndRoll;
    private boolean noStudent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_history);
        db = new DatabaseHandler(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Student");

        this.btnFindHistory = (Button) findViewById(R.id.btn_Find_History);
        this.spinnerstudent = (Spinner) findViewById(R.id.spinner_student);
        this.spinnerclasses = (Spinner) findViewById(R.id.spinner_classes);
        this.showprofileimage = (ImageView) findViewById(R.id.show_profile_image);

        loadSpinnerClasses();

        btnFindHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noStudent){                    //String fee_table = "tbl_fee_"+class_name.toLowerCase()+"_"+section.toLowerCase();
                    Intent intent = new Intent(getApplicationContext(), StudentFeeHistoryViewActivity.class);
                    intent.putExtra("studentID", studentId);
                    intent.putExtra("className", class_name);
                    intent.putExtra("section", section);
                    startActivity(intent);

                    }else{

                    Toast.makeText(getApplicationContext(), "No Students Into Class: "+class_name.toUpperCase()+"("+section.toUpperCase()+")", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void showStudentsFromDatabase(final String classNameL, final String SectionL) {

        String student_table = "tbl_students_"+classNameL.toLowerCase()+"_"+SectionL.toLowerCase();

        final List<Students>  studentAllInfo = db.getAllStudentInfo(student_table);
        final List<String> nameRoll = db.getStudentWithRollNumberBinding(student_table);

        if ( nameRoll.size() < 1){
            noStudent = true;
            showprofileimage.setImageResource(R.mipmap.camera);
            //Toast.makeText(getApplicationContext(), "No Students into Class: "+classNameL.toUpperCase()+"("+SectionL.toUpperCase()+")", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, nameRoll);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerstudent.setAdapter(dataAdapter);

        spinnerstudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Students students = studentAllInfo.get(position);
                studentId = students.getId();
                nameAndRoll = students.getStudent_roll()+" - "+students.getStudent_name();
                showprofileimage.setImageBitmap(convertToBitmap(students.getPoto()));

//                if (adapter != null){
//                    studentList.clear();
//
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private Bitmap convertToBitmap(byte[] b){
        return BitmapFactory.decodeByteArray(b, 0, b.length);

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

                noStudent = false;

//                if (adapter != null){
//                    studentList.clear();
//
//                }
                showStudentsFromDatabase(class_name, section);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
