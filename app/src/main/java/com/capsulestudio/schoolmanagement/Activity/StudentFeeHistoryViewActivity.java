package com.capsulestudio.schoolmanagement.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Adapter.StudentFeeAdapter;
import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.ExamType;
import com.capsulestudio.schoolmanagement.Model.FeePojo;
import com.capsulestudio.schoolmanagement.Model.FeeType;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StudentFeeHistoryViewActivity extends AppCompatActivity {


    private ListView listView;
    private ImageView profilePic;
    private TextView tvNameRollClass;
    Spinner spinnerfeeType, spinnerExamType;

    private static final String DataBaseName = "school_management";
    private DatabaseHandler db;
    SQLiteDatabase dbHelper;
    private List<FeePojo> feePojoLIst;

    private StudentFeeAdapter adapter;

    private int studentId;
    private String class_name;
    private String section;
    private String strStudentId;

    private String fee_table_name;
    private  String student_table_name;

    private String feeMonth;
    private String roll_name_class_section;
    private Students f;
    private String samount;
    private String feePaidDate;
    private String fee_id, fee_type;
    private String exam_id, exam_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_fee_history_view);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("");
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle("");

        studentId = getIntent().getIntExtra("studentID", 9999);
        class_name = getIntent().getStringExtra("className");
        section = getIntent().getStringExtra("section");

        fee_table_name = "tbl_fee_"+class_name.toLowerCase()+"_"+section.toLowerCase();
        student_table_name = "tbl_students_"+class_name.toLowerCase()+"_"+section.toLowerCase();

        strStudentId = String.valueOf(studentId); // Student Id


        db = new DatabaseHandler(this);

        dbHelper = openOrCreateDatabase(DataBaseName, MODE_PRIVATE, null);
        listView = (ListView) findViewById(R.id.listFeeHistory);
        profilePic = (ImageView) findViewById(R.id.show_profile_image_single);
        tvNameRollClass = (TextView) findViewById(R.id.tvRollNameClassSec);

        if (adapter != null){
            feePojoLIst.clear();
        }

        feePojoLIst = db.getsingleStudentFeeHistory(strStudentId, fee_table_name);

        //  set collapsing toolbar pic
        List<Students> singleData = db.getSingleStudentData(strStudentId, student_table_name);
        if (singleData.size() >= 1){
            f = singleData.get(0);
            profilePic.setImageBitmap(convertToBitmap(f.getPoto()));
        }

        // set collapsing toolbar Roll-name-Class_section
        if (feePojoLIst.size() >= 1){
            FeePojo single = feePojoLIst.get(0);
            roll_name_class_section =  single.getStudentNameRoll()+" ("+class_name.toUpperCase()+"-"+section.toUpperCase()+")";
            tvNameRollClass.setText(roll_name_class_section);
        }else{
            tvNameRollClass.setText("No Fee History!");
        }


        adapter = new StudentFeeAdapter(this, R.layout.fee_row, feePojoLIst);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final SweetAlertDialog sd = new SweetAlertDialog(StudentFeeHistoryViewActivity.this, SweetAlertDialog.WARNING_TYPE);
                sd.setTitleText("Update / Delete ?");
                //sd.setContentText("Are You Sure To Delete.");
                sd.setConfirmText("Delete");
                sd.setCanceledOnTouchOutside(true);
                sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        FeePojo getpaji = feePojoLIst.get(position);

                        String fee_id = String.valueOf(getpaji.getFeeID());

                        int delete = db.deleteSingleStudentFeeEntry(fee_id, fee_table_name);
                        if (delete > 0){
                            Toast.makeText(getApplicationContext(), "Fee Deleted Successfully !", Toast.LENGTH_LONG).show();
                            //adapter.notifyDataSetChanged();
                            reloadStudentTableAfterOperation();
                        }else {
                            Toast.makeText(getApplicationContext(), "Something Problem !", Toast.LENGTH_LONG).show();
                        }

                        sweetAlertDialog.dismiss();



//                        String student_tble_name = "tbl_students_"+class_name.toLowerCase()+"_"+section.toLowerCase();
//                        Students student = studentList.get(position);
//                        String id = String.valueOf(student.getId());
//                        int result = db.deleteSingleStudent(id, student_tble_name);
//
//                        if (result > 0){
//                            Toast.makeText(getApplicationContext(), "Student Deleted Successfully", Toast.LENGTH_LONG).show();
//                        }else{
//                            Toast.makeText(getApplicationContext(), "Something Wrong !", Toast.LENGTH_LONG).show();
//
//                        }
//
//                        sweetAlertDialog.dismiss();
//                        reloadStudentTableAfterOperation(student_tble_name);


                    }
                });
                sd.setCancelText("Edit");
                sd.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        //Toast.makeText(getApplicationContext(), "Select Edit", Toast.LENGTH_LONG).show();

                        sweetAlertDialog.dismiss();
                        final FeePojo getpaji = feePojoLIst.get(position);

                        final String feeId = String.valueOf(getpaji.getFeeID());
                        samount = getpaji.getAmount();
                        feePaidDate = getpaji.getFeeDate();
                        feeMonth = getpaji.getFeeMonth();

                        LayoutInflater layoutInflater = LayoutInflater.from(StudentFeeHistoryViewActivity.this);
                        View view =layoutInflater.inflate(R.layout.update_fee_history_custom_dialog, null);
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudentFeeHistoryViewActivity.this);
                        alertDialogBuilder.setView(view);

                        Button btnUpdate = (Button) view.findViewById(R.id.btn_UpdateFee);
                        Button btncancel = (Button) view.findViewById(R.id.btn_cancel);
                        final EditText etamount = (EditText) view.findViewById(R.id.amount);
                        etamount.setText(samount);
                        initDialogView(view);


                        alertDialogBuilder
                                .setCancelable(true);

                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        btnUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String getAmount = etamount.getText().toString();

                                if (getAmount.isEmpty()){
                                    etamount.setError("Field Must Not Be Empty");
                                }else {

                                    String sql = "UPDATE " +fee_table_name+ " SET feeMonth = ?, amount = ?, feeDate = ?, fee_type = ?, exam_type = ? WHERE feeID = ?";

                                    try {
                                        dbHelper.execSQL(sql, new String[]{feeMonth, getAmount, feePaidDate, fee_type, exam_type, feeId});
                                        reloadStudentTableAfterOperation();
                                        Toast.makeText(getApplicationContext(), "Update Successfully !", Toast.LENGTH_LONG).show();

                                    }catch (SQLException e){
                                        Toast.makeText(getApplicationContext(), "Message:"+e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    alertDialog.dismiss();

                                }


                            }
                        });

                        btncancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();

                            }
                        });

                    }
                });
                sd.show();

                return true;
            }
        });

    }

    private void initDialogView(View view) {

        ImageView proPic = (ImageView) view.findViewById(R.id.proPIC);
        TextView tvNameFix = (TextView) view.findViewById(R.id.tvName);
        Spinner  spinnerfeemonth = (Spinner) view.findViewById(R.id.spinner_fee_month);
        spinnerfeeType = (Spinner) view.findViewById(R.id.spinner_fee_type);
        spinnerExamType = (Spinner) view.findViewById(R.id.spinner_exam_type);
        final EditText feedate = (EditText) view.findViewById(R.id.feeDate);


        proPic.setImageBitmap(convertToBitmap(f.getPoto()));
        tvNameFix.setText(roll_name_class_section);
        //loadFeeMonth(spinnerfeemonth);
        feedate.setText(feePaidDate);

        loadSpinner(spinnerfeemonth);
        loadSpinnerFeeType();
        loadSpinnerExamType();

        feedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(StudentFeeHistoryViewActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(year) + "-" + String.valueOf(monthOfYear+1)
                                + "-" + String.valueOf(dayOfMonth);

                        feePaidDate = date;
                        feedate.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });
    }

    private void loadSpinner(Spinner spinnerfeemonth) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        final String[] feeMonthData = {feeMonth,"January - "+year,"February - "+year, "March - "+year,
                "April - "+year, "May - "+year, "June - "+year,
                "July - "+year, "August - "+year, "September - "+year,
                "October - "+year, "November - "+year, "December - "+year};


        final List<String> plantsList = new ArrayList<>(Arrays.asList(feeMonthData));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item_list,plantsList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerfeemonth.setAdapter(spinnerArrayAdapter);

        spinnerfeemonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //feeMonth = feeMonthData[position];
                if(position > 0){
                    // Notify the selected item text
                    feeMonth = (String) parent.getItemAtPosition(position);
//                    Toast.makeText
//                            (getApplicationContext(), "Selected : " + feeMonth, Toast.LENGTH_SHORT)
//                            .show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerFeeType() {
        final List<FeeType> data = db.getAllFeeTypes();
        final List<String> fileName = db.getAllFeeTypeInBinding();

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


    private void loadSpinnerExamType() {
        final List<ExamType> data = db.getAllExamTypes();
        final List<String> fileName = db.getAllExamTypeInBinding();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, fileName);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerExamType.setAdapter(dataAdapter);

        spinnerExamType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ExamType examType = data.get(position);

                exam_id = String.valueOf((examType.getExam_id()));
                exam_type = examType.getExam_type();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void reloadStudentTableAfterOperation() {

        String query = "SELECT * FROM "+ fee_table_name +" WHERE studentId = ? ";
        Cursor cursor = dbHelper.rawQuery(query, new String[]{strStudentId});

        if (cursor.moveToFirst()) {
            feePojoLIst.clear();
            do {
                FeePojo feePojo = new FeePojo();

                feePojo.setFeeID(cursor.getInt(0));
                feePojo.setStudentNameRoll(cursor.getString(1));
                feePojo.setStudentId(cursor.getInt(2));
                feePojo.setFeeMonth(cursor.getString(3));
                feePojo.setAmount(cursor.getString(4));
                feePojo.setFeeDate(cursor.getString(5));
                feePojo.setClassName(cursor.getString(6));
                feePojo.setClassId(cursor.getInt(7));

                feePojoLIst.add(feePojo);

            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();

    }

    private Bitmap convertToBitmap(byte[] b){
        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
