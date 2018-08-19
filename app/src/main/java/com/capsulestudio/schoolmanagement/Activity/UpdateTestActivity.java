package com.capsulestudio.schoolmanagement.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Marks;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.R;

import java.util.List;

public class UpdateTestActivity extends AppCompatActivity {

    Spinner spinner_subjects;
    ImageView profilePic;
    EditText editTextTotalMarks, editTextPassingMarks, editTextObtainedMarks, editTextDesc;
    private TextInputLayout inputLayoutSubject, inputLayoutExams;
    AutoCompleteTextView acTvSub, acTvExam;
    TextView textViewStatus, textViewStudentName;
    Button btn_update;

    private Marks marks;
    private DatabaseHandler db;

    private byte[] photo;
    private String studentName, studentRoll, subname, totalMarks, passMarks, obtainedMarks, res_status, description, class_name, section_name, exam_type, sub, exam;
    String Table_name = "tbl_result_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_test);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        marks = getIntent().getParcelableExtra("marks"); // Perceable Object
        db = new DatabaseHandler(this);

        // getting Views
        profilePic = (ImageView) findViewById(R.id.show_profile_image);
        //editTextSubject = (EditText) findViewById(R.id.sub_name);
        inputLayoutSubject = (TextInputLayout) findViewById(R.id.InputLayout_subject);
        inputLayoutExams = (TextInputLayout) findViewById(R.id.InputLayout_examtype);

        // Autocomplete for class
        final List<String> sub_data = db.getAllSubjectsInBinding();
        acTvSub = (AutoCompleteTextView) findViewById(R.id.sub_name);
        acTvSub.setEnabled(true);
        acTvSub.setTextColor(getResources().getColor(R.color.black));
        sub = marks.getSub_name();
        acTvSub.setText(sub);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_dropdown_item_1line,sub_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        acTvSub.setThreshold(1);
        acTvSub.setAdapter(adapter);

        // Autocomplete for class
        final List<String> exam_data = db.getAllExamTypeInBinding();
        acTvExam = (AutoCompleteTextView) findViewById(R.id.examType);
        acTvExam.setEnabled(true);
        acTvExam.setTextColor(getResources().getColor(R.color.black));
        exam = marks.getExam_type();
        acTvExam.setText(exam);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (this,android.R.layout.simple_dropdown_item_1line,exam_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        acTvExam.setThreshold(1);
        acTvExam.setAdapter(adapter1);

        editTextTotalMarks = (EditText) findViewById(R.id.total_marks);
        editTextPassingMarks = (EditText) findViewById(R.id.passing_marks);
        editTextObtainedMarks = (EditText) findViewById(R.id.obtained_marks);
        editTextDesc = (EditText) findViewById(R.id.description);
        textViewStatus = (TextView) findViewById(R.id.res_status);
        textViewStudentName = (TextView) findViewById(R.id.textViewStudentNameClass);
        btn_update = (Button) findViewById(R.id.btn_update);

        photo = marks.getPoto();  // if photo byte array is not update then we take the old byte array
        studentName = marks.getStudent_name();
        studentRoll = marks.getStudent_roll();
        subname = marks.getSub_name();
        totalMarks = marks.getTotal_marks();
        passMarks = marks.getPass_marks();
        obtainedMarks = marks.getObtained_marks();
        res_status = marks.getResult_status();
        description = marks.getDescription();
        exam_type = marks.getExam_type();

        class_name = marks.getClass_name().toLowerCase();
        section_name = marks.getSection().toLowerCase();

        // concatenate
        Table_name += class_name + "_" + section_name;

        profilePic.setImageBitmap(convertToBitmap(marks.getPoto()));
        textViewStudentName.setText(studentRoll + " - " + studentName + " - " + marks.getClass_name().toUpperCase() + "(" + marks.getSection().toUpperCase() + ")");
        textViewStatus.setText(res_status);
        editTextTotalMarks.setText(totalMarks);
        editTextPassingMarks.setText(passMarks);
        editTextObtainedMarks.setText(obtainedMarks);
        editTextDesc.setText(description);


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMarks();
            }
        });
    }

    private void updateMarks() {


        String sub_name, total_marks, pass_mark, obtained_marks, status, exam, desc, id;
        int id_student;
        id = String.valueOf(marks.getId());
        id_student = marks.getId_student();
        sub_name = acTvSub.getText().toString().trim();
        total_marks = editTextTotalMarks.getText().toString().trim();
        pass_mark = editTextPassingMarks.getText().toString().trim();
        obtained_marks = editTextObtainedMarks.getText().toString().trim();
        status = res_status;
        exam = acTvExam.getText().toString().trim();
        desc = editTextDesc.getText().toString().trim();


        int upResult = db.updateSingleStudentMarks(new Marks(sub_name, total_marks, pass_mark, obtained_marks, status, desc, exam, id_student), id, Table_name);
        if (upResult > 0) {
            finish();
            Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), ViewTestActivity.class));
        } else {
            Toast.makeText(this, "Something is Problem !", Toast.LENGTH_SHORT).show();
        }
    }

    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b){

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
