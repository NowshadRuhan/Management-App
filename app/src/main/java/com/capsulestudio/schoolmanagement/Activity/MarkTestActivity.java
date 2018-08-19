package com.capsulestudio.schoolmanagement.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.ExamType;
import com.capsulestudio.schoolmanagement.Model.Marks;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.Model.StudentsAttendance;
import com.capsulestudio.schoolmanagement.Model.Subjects;
import com.capsulestudio.schoolmanagement.R;
import com.nostra13.universalimageloader.utils.L;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MarkTestActivity extends AppCompatActivity {

    ImageView profilePic;
    EditText editTextTotalMarks, editTextPassingMarks, editTextObtainedMarks, editTextDesc;
    TextView textViewStatus, textViewTodaysDate, textViewAtdStatus;
    Spinner spinner_class, spinner_student, spinner_subjects, spinner_examType;
    Button button_save;

    String class_name, section_name, yearInString, month, dayLongName, currentDate;
    String stdName, studentRoll, stdMobile;
    String sub_id, sub_name, exam_id,  exam_type;
    int class_id, year, day, id_student;
    String TotalMarks, PassingMarks, ObtainedMarks, Subject, Description, Status, Table_Name, ExamType;

    private Students student;
    private DatabaseHandler db;

    List<StudentsAttendance> studentName;
    List<Classes> className;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_test);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // getting views
        profilePic = (ImageView) findViewById(R.id.profile_image);
        editTextTotalMarks = (EditText) findViewById(R.id.total_marks);
        editTextPassingMarks = (EditText) findViewById(R.id.passing_marks);
        editTextObtainedMarks = (EditText) findViewById(R.id.obtained_marks);
        editTextDesc = (EditText) findViewById(R.id.description);
        textViewStatus = (TextView) findViewById(R.id.res_status);
        textViewTodaysDate = (TextView) findViewById(R.id.txtDate);
        textViewAtdStatus = (TextView) findViewById(R.id.atd_status);
        spinner_class = (Spinner) findViewById(R.id.spinner_classes);
        spinner_student = (Spinner) findViewById(R.id.spinner_students);
        spinner_subjects = (Spinner) findViewById(R.id.spinner_subjects);
        spinner_examType = (Spinner) findViewById(R.id.spinner_examType);
        button_save = (Button) findViewById(R.id.btn_save);

        db = new DatabaseHandler(this);  // Database object create

        // load spinner Classes
        loadClasses();

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

        textViewTodaysDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(MarkTestActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        loadClasses();
                        Log.e("Time : ", date);
                        textViewTodaysDate.setText(monthName[monthOfYear] + " " + dayOfMonth + ", " + year);

                    }
                }, yy, mm, dd);
                datePicker.show();}
        });


        // Setting Current Date
        textViewTodaysDate.setText(dayLongName + ", " + month + " " + day + ", " + yearInString);


        // save button click listener
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add Mark
                addMarks();
            }
        });
    }

    private void addMarks() {

        TotalMarks = editTextTotalMarks.getText().toString().trim();
        PassingMarks = editTextPassingMarks.getText().toString().trim();
        ObtainedMarks = editTextObtainedMarks.getText().toString().trim();
        Subject = spinner_subjects.getSelectedItem().toString();
        Description = editTextDesc.getText().toString().trim();
        ExamType = spinner_examType.getSelectedItem().toString();

        // Validate add marks Fields
        if (!validate()) {
            return;
        }

        if (Integer.parseInt(ObtainedMarks) >= Integer.parseInt(PassingMarks)) {
            Status = "Pass";
            textViewStatus.setText("Pass");
        } else {
            Status = "Fail";
            textViewStatus.setText("Fail");
        }


        /*if(db.CheckMarkExistence(Table_Name, currentDate, Subject, String.valueOf(id_student)))
        {
            Toast.makeText(getApplicationContext(), "This subject is already added today for this student!", Toast.LENGTH_SHORT).show();
            return;
        }
        */



        long inserted = db.addMarks(new Marks(currentDate, Subject, TotalMarks, PassingMarks, ObtainedMarks, Status, Description, ExamType, id_student), Table_Name);

        if (inserted > 0) {
            editTextObtainedMarks.setText(null);
            editTextDesc.setText(null);
            textViewStatus.setText(null);

            Toast.makeText(getApplicationContext(), "Submitted Successfully", Toast.LENGTH_SHORT).show();

            final SweetAlertDialog sd = new SweetAlertDialog(MarkTestActivity.this, SweetAlertDialog.WARNING_TYPE);
            sd.setTitleText("SMS!");
            sd.setContentText("Send Confirmation message to: " + stdName);
            sd.setConfirmText("Yes!");
            sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {

                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", stdMobile);
                    smsIntent.putExtra("sms_body", "Name: " + stdName + "\nClass: " + class_name + "(" + section_name + ")\nRoll: " + studentRoll + "\nSubject: " + sub_name + "\nExam: " + exam_type + "\ngot " + ObtainedMarks + " marks out of " + TotalMarks + ".\nThank you.");
                    startActivity(smsIntent);

                    sd.dismissWithAnimation();
                }
            });
            sd.show();


            /*if (spinner_student.getSelectedItemPosition() + 1 == spinner_student.getAdapter().getCount()) {
                return;
            } else if (spinner_student.getSelectedItemPosition() < spinner_student.getAdapter().getCount()) {

                spinner_student.setSelection(spinner_student.getSelectedItemPosition() + 1);
            }*/
        } else {
            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadClasses() {

        className = db.getAllClasses();

        if (className.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("There is no class to show. Please add classes and students to provide marks.")
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
                loadStudent(class_name, section_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadStudent(final String class_name, final String section_name) {

        studentName = db.getAllStudentsAttendance(class_name, section_name, currentDate);

        if (studentName.isEmpty()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("No attendance data found on this date.\nGo to attendance section to mark attendance.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
            builder.show();
            return;
        }

        final List<String> data = db.getAllStudentsBinding(class_name, section_name);

        if (data.isEmpty()) {
            textViewAtdStatus.setVisibility(View.GONE);
            profilePic.setImageResource(R.mipmap.camera);
            Toast.makeText(getApplicationContext(), "No students found for this class", Toast.LENGTH_SHORT).show();
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
                textViewAtdStatus.setVisibility(View.VISIBLE);
                textViewAtdStatus.setText(students.getAttendance_status());
                profilePic.setImageBitmap(convertToBitmap(students.getPoto()));

                id_student = students.getId_student();
                Table_Name = "tbl_result_" + class_name.toLowerCase() + "_" + section_name.toLowerCase();
                stdName = students.getStudent_name();
                stdMobile = students.getStudent_mobile();
                studentRoll = students.getStudent_roll();

                Log.e("M_SName: ", stdName);
                Log.e("M_sRoll: ", studentRoll);

                loadSpinnerSubjects();
                loadSpinnerExamType();

                //Toast.makeText(getApplicationContext(), students.getStudent_name().toString().trim(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerExamType() {

        final List<ExamType> data = db.getAllExamTypes();
        final List<String> fileName = db.getAllExamTypeInBinding();

        if(fileName.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("No exam types found. Please add exam types to continue.")
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

        spinner_examType.setAdapter(dataAdapter);

        spinner_examType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ExamType examType = data.get(position);

                exam_id = String.valueOf((examType.getExam_id()));
                exam_type = examType.getExam_type();
                ExamType = exam_type;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerSubjects() {
        final List<Subjects> data = db.getAllSubjects();
        final List<String> fileName = db.getAllSubjectsInBinding();

        if(fileName.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("No Subjects Found. Please Add fee types to continue.")
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

        spinner_subjects.setAdapter(dataAdapter);

        spinner_subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Subjects subs = data.get(position);

                sub_id = String.valueOf((subs.getId()));
                sub_name = subs.getSub_name();
                Subject = sub_name;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Validating Data
    private boolean validate() {
        boolean valid = true;

        String TotalMarks = editTextTotalMarks.getText().toString().trim();
        String PassingMarks = editTextPassingMarks.getText().toString().trim();
        String ObtaindedMarks = editTextObtainedMarks.getText().toString().trim();
        String Desc = editTextDesc.getText().toString().trim();

        if (TotalMarks.isEmpty()) {
            editTextTotalMarks.setError("Field Must Not Be Empty");
            valid = false;
        } else {
            editTextTotalMarks.setError(null);
        }
        if (PassingMarks.isEmpty()) {
            editTextPassingMarks.setError("Field Must Not Be Empty");
            valid = false;
        } else {
            editTextPassingMarks.setError(null);
        }
        if (ObtaindedMarks.isEmpty()) {
            editTextObtainedMarks.setError("Field Must Not Be Empty");
            valid = false;
        } else {
            editTextObtainedMarks.setError(null);
        }
        if (Desc.isEmpty()) {
            editTextDesc.setError("Field Must Not Be Empty");
            valid = false;
        } else {
            editTextDesc.setError(null);
        }
        return valid;
    }

    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b) {

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
