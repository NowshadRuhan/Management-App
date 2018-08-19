package com.capsulestudio.schoolmanagement.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Adapter.StudentAdapter;
import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.ExamType;
import com.capsulestudio.schoolmanagement.Model.FeePojo;
import com.capsulestudio.schoolmanagement.Model.FeeType;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.R;

import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SubmitFeeActivity extends AppCompatActivity {

    private Spinner spinnerclasses, spinnerStudents, spinnerfeemonth, spinnerfeeType;
    private TextInputLayout InputLayoutamount, InputLayoutfeeDate;
    private EditText etamount, etfeeDate;
    private Button btnSubmitFee;
    private ImageView showprofileimage;

    private static final String DataBaseName = "school_management";
    private DatabaseHandler db;
    private SQLiteDatabase dbHelper;
    private List<Students> studentList;
    private StudentAdapter adapter;

    private String class_id;
    private String class_name;
    private String section;
    private String feePaidDate;
    private String amount;
    private String fee_id, fee_type;
    private int studentId;
    private String nameAndRoll, name, roll, class_section;
    private String studentMobile;
    private boolean noStudent = false;
    private String feeMonth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_fee);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHandler(this);
        dbHelper = openOrCreateDatabase(DataBaseName, MODE_PRIVATE, null);
        initView();

        loadSpinnerClasses(); // Load Class Spinner Data
        loadFeeMonth();       // Load Fee Month Spinner Data
        loadSpinnerFeeType(); // Load Fee type Spinner Data
    }


    public void loadFeeMonth() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        final String[] feeMonthData = {"January - " + year, "February - " + year, "March - " + year,
                "April - " + year, "May - " + year, "June - " + year,
                "July - " + year, "August - " + year, "September - " + year,
                "October - " + year, "November - " + year, "December - " + year};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, feeMonthData);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerfeemonth.setAdapter(dataAdapter);

        spinnerfeemonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                feeMonth = feeMonthData[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void loadSpinnerFeeType() {
        final List<FeeType> data = db.getAllFeeTypes();
        final List<String> fileName = db.getAllFeeTypeInBinding();

        if (fileName.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("No fee type data found.")
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

        spinnerfeeType.setAdapter(dataAdapter);

        spinnerfeeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FeeType feeType = data.get(position);

                fee_id = String.valueOf((feeType.getFee_id()));
                fee_type = feeType.getFee_type();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void showStudentsFromDatabase(final String classNameL, final String SectionL) {

        String student_table = "tbl_students_" + classNameL.toLowerCase() + "_" + SectionL.toLowerCase();

        final List<Students> studentAllInfo = db.getAllStudentInfo(student_table);
        final List<String> nameRoll = db.getStudentWithRollNumberBinding(student_table);

        if (nameRoll.size() < 1) {
            noStudent = true;
            showprofileimage.setImageResource(R.mipmap.camera);
            Toast.makeText(getApplicationContext(), "No Students into Class: " + classNameL.toUpperCase() + "(" + SectionL.toUpperCase() + ")", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, nameRoll);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudents.setAdapter(dataAdapter);

        spinnerStudents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Students students = studentAllInfo.get(position);
                name = studentAllInfo.get(position).getStudent_name();
                roll = studentAllInfo.get(position).getStudent_roll();
                class_section = studentAllInfo.get(position).getClass_name() + "(" + studentAllInfo.get(position).getSection() + ")";

                studentId = students.getId();
                nameAndRoll = students.getStudent_roll() + " - " + students.getStudent_name();
                showprofileimage.setImageBitmap(convertToBitmap(students.getPoto()));
                studentMobile = students.getStudent_mobile();

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

    private void loadSpinnerClasses() {
        final List<Classes> data = db.getAllClasses();
        final List<String> fileName = db.getAllClassAndSentionInBinding();

        if (fileName.isEmpty()) {
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

    private void initView() {

        this.btnSubmitFee = (Button) findViewById(R.id.btn_submitFee);
        this.InputLayoutfeeDate = (TextInputLayout) findViewById(R.id.InputLayout_feeDate);
        this.etfeeDate = (EditText) findViewById(R.id.feeDate);
        this.InputLayoutamount = (TextInputLayout) findViewById(R.id.InputLayout_amount);
        this.etamount = (EditText) findViewById(R.id.amount);
        this.spinnerfeemonth = (Spinner) findViewById(R.id.spinner_fee_month);
        this.spinnerclasses = (Spinner) findViewById(R.id.spinner_classes);
        this.showprofileimage = (ImageView) findViewById(R.id.show_profile_image);
        this.spinnerStudents = (Spinner) findViewById(R.id.spinner_student);
        this.spinnerfeeType = (Spinner) findViewById(R.id.spinner_fee_type);

        etfeeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(SubmitFeeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1)
                                + "-" + String.valueOf(dayOfMonth);

                        feePaidDate = date;

                        etfeeDate.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });

        btnSubmitFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFee();
            }
        });

    }

    private void submitFee() {

        String fee_tble_name = "tbl_fee_" + class_name.toLowerCase() + "_" + section.toLowerCase();

        boolean isValid = true;

        // Amount
        if (etamount.getText().toString().isEmpty()) {
            InputLayoutamount.setError("Field Must Not be Empty");
            isValid = false;
        } else {
            amount = etamount.getText().toString().trim();
            InputLayoutamount.setErrorEnabled(false);
        }

        // Submit date
        if (etfeeDate.getText().toString().isEmpty()) {
            InputLayoutfeeDate.setError("Field Must Not be Empty");
            isValid = false;
        } else {
            InputLayoutfeeDate.setErrorEnabled(false);
        }

        // class
        if (class_name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You should add a class first", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        // student
        if (noStudent) {
            Toast.makeText(getApplicationContext(), "There is no students into class: " + class_name.toUpperCase() + "(" + section.toUpperCase() + ")", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (isValid) {
            int class_Id_int = Integer.parseInt(class_id);
            if (db.CheckStudentFeeExistence(fee_tble_name, String.valueOf(studentId), feeMonth, fee_type)) {
                Toast.makeText(this, feeMonth + " " + fee_type + " fee already submitted", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    long r = db.addStudenFee(new FeePojo(nameAndRoll, studentId, feeMonth, amount, feePaidDate, class_name, fee_type, class_Id_int), fee_tble_name);
                    if (r < 0) {
                        Toast.makeText(getApplicationContext(), "Something Problem !", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Fee Submitted Successfully !", Toast.LENGTH_LONG).show();
                        final SweetAlertDialog sd = new SweetAlertDialog(SubmitFeeActivity.this, SweetAlertDialog.WARNING_TYPE);
                        sd.setTitleText("SMS!");
                        sd.setContentText("Send Confirmation message to: " + nameAndRoll);
                        sd.setConfirmText("Yes!");
                        sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                smsIntent.setType("vnd.android-dir/mms-sms");
                                smsIntent.putExtra("address", studentMobile);
                                smsIntent.putExtra("sms_body", name + ", " + class_section + ", " + roll + " has been submitted his/her monthly (" + feeMonth + ") fee of " + amount + " BDT on " + feePaidDate + " successfully.\nThank You");
                                startActivity(smsIntent);

                                sd.dismissWithAnimation();
                            }
                        });
                        sd.show();
                    }
                    etamount.setText("");
                    etfeeDate.setText("");
                } catch (SQLiteException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }

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
