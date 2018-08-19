package com.capsulestudio.schoolmanagement.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Helper.PrefManager;
import com.capsulestudio.schoolmanagement.Helper.SharedPrefManager;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.ExamType;
import com.capsulestudio.schoolmanagement.Model.FeeType;
import com.capsulestudio.schoolmanagement.Model.Subjects;
import com.capsulestudio.schoolmanagement.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    // Shuvo
    /*TextView textViewStdList, textViewClassList, textViewMarkAtd, textViewViewAtd, textViewMarkTest, textViewViewMark, textViewMarkHistory,
                 textViewSubmitFee, textViewFeeHistory, textViewWithFee, textViewWithNoFee, textViewAttendencepDF;*/
    LinearLayout textViewStdList, textViewClassList, textViewMarkAtd, textViewViewAtd, textViewMarkTest, textViewViewMark, textViewMarkHistory,
            textViewSubmitFee, textViewFeeHistory, textViewWithFee, textViewWithNoFee, textViewAttendencepDF, textViewNotice, textViewBirthday;
    SQLiteDatabase dbHelper;
    private static final String DataBaseName = "school_management";
    private static final int DB_Version = 1;
    private DatabaseHandler db;
    private Spinner spinnerclasses, spinnersubjects, spinnerExamtype, spinnerFeeType;
    private String class_id, sub_id;
    private String class_name, sub_name;
    private String exam_id,  exam_type;
    private String fee_id, fee_type;
    private String section;


    public EditText editTextName, editTextSchool;
    public String studentname, studentSchool, Reg_date;

    public Button btn_save;
    private PrefManager prefManager;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_activity_dashboard_design);

        db = new DatabaseHandler(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("school_management", 0); // O for Private Mode
        // get editor to edit in file
        editor = sharedPreferences.edit();

        /** Checking licence validity **/
        // get user data from session
        HashMap<String, String> user = SharedPrefManager.getInstance(this).getUserDetails();
        Reg_date = user.get(SharedPrefManager.KEY_REG_DATE);

        //Log.e("Main: ", Reg_date);

        String dateParts[] = Reg_date.split("-");
        String year = dateParts[0]; // 2018
        String month = dateParts[1]; // 01
        String day = dateParts[2]; // 08

        // Creates two calendars instances
        Calendar cal1 = Calendar.getInstance();

        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DATE, 1);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = format1.format(cal2.getTime());
        String dateParts1[] = formatted.split("-");
        String year1 = dateParts1[0]; // 2018
        String month1 = dateParts1[1]; // 01
        String day1 = dateParts1[2]; // 08

        // Set the date for both of the calendar instance
        cal1.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        cal2.set(Integer.parseInt(year1), Integer.parseInt(month1), Integer.parseInt(day1));

        // Get the represented date in milliseconds
        long millis1 = cal1.getTimeInMillis();
        long millis2 = cal2.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = millis2 - millis1;

        // Calculate difference in days
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays > 365) {
            final SweetAlertDialog sd = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
            sd.setTitleText("Expired!");
            sd.setContentText("Your One Year Licence Expired.\nContact Provider for more information.");
            sd.setConfirmText("Ok");
            sd.setCancelable(true);
            sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    finish();
                }
            });
            sd.show();
        }

        /** Checking for first time launch - before calling setContentView() **/
        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            loadDialog();
        }


        // Getting views
        textViewStdList = (LinearLayout) findViewById(R.id.tvStudentList);
        textViewClassList = (LinearLayout) findViewById(R.id.tvClassList);
        textViewMarkAtd = (LinearLayout) findViewById(R.id.tvMarkAttendance);
        textViewViewAtd = (LinearLayout) findViewById(R.id.tvViewAttendance);
        textViewMarkTest = (LinearLayout) findViewById(R.id.tvMarkTest);
        textViewViewMark = (LinearLayout) findViewById(R.id.tvViewTest);
        textViewMarkHistory = (LinearLayout) findViewById(R.id.tvViewTestHistory);
        textViewSubmitFee = (LinearLayout) findViewById(R.id.tvSubmitFee);
        textViewFeeHistory = (LinearLayout) findViewById(R.id.tvFeeHistory);
        textViewWithFee = (LinearLayout) findViewById(R.id.tvWithFee);
        textViewWithNoFee = (LinearLayout) findViewById(R.id.tvwithNoFee);
        textViewNotice = (LinearLayout) findViewById(R.id.tvNotice);
        textViewBirthday = (LinearLayout) findViewById(R.id.tvBirthday);


        dbHelper = openOrCreateDatabase(DataBaseName, MODE_PRIVATE, null);

        /**
         *   Check all table names
         *   for testing purpose
         */
        //checkAllTable();

        // Class Creation
        createClassTable();

        //Subs, Fees and Exam Table Creation
        createTablesOfSubs_Fees_Exams();

        textViewStdList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StudentsActivity.class));
            }
        });


        textViewClassList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ClassesActivity.class));
            }
        });

        textViewMarkAtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MarkAttendanceActivity.class));
            }
        });

        textViewViewAtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewAttendanceActivity.class));
            }
        });

        textViewMarkTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MarkTestActivity.class));
                //Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();
            }
        });

        textViewViewMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewTestActivity.class));
                //Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();
            }
        });

        textViewMarkHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SelectStudentActivity.class));
                //Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();
            }
        });

        textViewSubmitFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SubmitFeeActivity.class));
            }
        });

        textViewFeeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FeeHistoryActivity.class));
            }
        });
        textViewWithNoFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StudentWithNoFeeActivity.class));
            }
        });
        textViewWithFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StudentWithFeeActivity.class));
            }
        });
        textViewNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final List<Classes> data = db.getAllClasses();
                final List<String> fileName = db.getAllClassAndSentionInBinding();


                if (!fileName.isEmpty()) {
                    LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                    View view = layoutInflater.inflate(R.layout.custom_notice_layout, null);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setView(view);

                    Button btn_send = (Button) view.findViewById(R.id.btn_sendNotice);
                    Button btncancel = (Button) view.findViewById(R.id.btn_cancel);
                    final EditText editTextNotice = (EditText) view.findViewById(R.id.notice);
                    initDialogView(view);


                    alertDialogBuilder
                            .setCancelable(true);

                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    btn_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String getNotice = editTextNotice.getText().toString();
                            String class_section = class_name.toLowerCase() + "_" + section.toLowerCase();

                            ArrayList<String> numbersArrayList = db.getArrayList(class_section);


                            if (getNotice.isEmpty()) {
                                editTextNotice.setError("Field Must Not Be Empty");
                            }
                            if (numbersArrayList != null && !numbersArrayList.isEmpty()) {
                                try {
                                    String separator = "; ";

                                    if (android.os.Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
                                        separator = ", ";

                                        String toNumbers = "";
                                        for (String s : numbersArrayList) {
                                            toNumbers = toNumbers + s + ",";
                                        }
                                        toNumbers = toNumbers.substring(0, toNumbers.length() - 1);

                                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                        smsIntent.setType("vnd.android-dir/mms-sms");
                                        smsIntent.putExtra("address", toNumbers);
                                        smsIntent.putExtra("sms_body", getNotice);
                                        startActivity(smsIntent);
                                    } else {
                                        separator = "; ";

                                        String toNumbers = "";
                                        for (String s : numbersArrayList) {
                                            toNumbers = toNumbers + s + ";";
                                        }
                                        toNumbers = toNumbers.substring(0, toNumbers.length() - 1);

                                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                        smsIntent.setType("vnd.android-dir/mms-sms");
                                        smsIntent.putExtra("address", toNumbers);
                                        smsIntent.putExtra("sms_body", getNotice);
                                        startActivity(smsIntent);
                                    }
                                } catch (SQLException e) {
                                    Toast.makeText(getApplicationContext(), "Message:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "NO Students found.. Add Students in Students List Section", Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                                return;
                            }
                        }
                    });

                    btncancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();

                        }
                    });
                } else if (fileName.isEmpty()) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error")
                            .setMessage("There is no class to show. Please add classes to add students respectively.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    builder.show();
                }

            }
        });
        textViewBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BirthdayWishActivity.class);
                startActivity(intent);
            }
        });

    }

    private void createTablesOfSubs_Fees_Exams() {

        dbHelper.execSQL(
                "CREATE TABLE IF NOT EXISTS tbl_subjects (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT sub_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    sub_name varchar(200) NOT NULL\n" +
                        ");"
        );

        dbHelper.execSQL(
                "CREATE TABLE IF NOT EXISTS tbl_exam_type (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT exam_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    exam_type varchar(200) NOT NULL\n" +
                        ");"
        );

        dbHelper.execSQL(
                "CREATE TABLE IF NOT EXISTS tbl_fee_type (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT fee_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    fee_type varchar(200) NOT NULL\n" +
                        ");"
        );
    }

    private void checkAllTable() {
        Cursor c = dbHelper.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Toast.makeText(MainActivity.this, "Table Name=> " + c.getString(0), Toast.LENGTH_LONG).show();
                c.moveToNext();
            }
        }
    }

    private void createClassTable() {

        dbHelper.execSQL(
                "CREATE TABLE IF NOT EXISTS tbl_classes (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT class_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    class_name varchar(200) NOT NULL,\n" +
                        "    section varchar(200) NOT NULL\n" +
                        ");"
        );

    }

    private void initDialogView(View view) {


        spinnerclasses = (Spinner) view.findViewById(R.id.spinner_className);

        loadSpinnerClasses();

    }

    private void initSubDialogView(View view) {


        spinnersubjects = (Spinner) view.findViewById(R.id.spinner_subName);

        loadSpinnerSubjects();

    }

    private void initExamTypeDialogView(View view) {


        spinnerExamtype = (Spinner) view.findViewById(R.id.spinner_examType);

        loadSpinnerExamType();

    }

    private void initFeeTypeDialogView(View view) {


        spinnerFeeType = (Spinner) view.findViewById(R.id.spinner_feeType);

        loadSpinnerFeeType();

    }


    private void loadSpinnerClasses() {
        final List<Classes> data = db.getAllClasses();
        final List<String> fileName = db.getAllClassAndSentionInBinding();

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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerSubjects() {
        final List<Subjects> data = db.getAllSubjects();
        final List<String> fileName = db.getAllSubjectsInBinding();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, fileName);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnersubjects.setAdapter(dataAdapter);

        spinnersubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Subjects subs = data.get(position);

                sub_id = String.valueOf((subs.getId()));
                sub_name = subs.getSub_name();
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

        spinnerExamtype.setAdapter(dataAdapter);

        spinnerExamtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void loadSpinnerFeeType() {
        final List<FeeType> data = db.getAllFeeTypes();
        final List<String> fileName = db.getAllFeeTypeInBinding();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, fileName);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFeeType.setAdapter(dataAdapter);

        spinnerFeeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.edit_school_name) {
            final Dialog editdialog = new Dialog(MainActivity.this);
            editdialog.setTitle("Update Profile");
            editdialog.setContentView(R.layout.custom_institution_layout);
            editdialog.setCancelable(true);
            editdialog.show();

            editTextName = (EditText) editdialog.findViewById(R.id.input_Name);
            editTextSchool = (EditText) editdialog.findViewById(R.id.input_VersityName);

            editTextName.setText(sharedPreferences.getString("Name", ""));
            editTextSchool.setText(sharedPreferences.getString("School", ""));

            Button btn_save = (Button) editdialog.findViewById(R.id.btn_save);

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Validate Authority Sign Up Fields
                    if (!validate()) {
                        return;
                    }

                    String studentname = editTextName.getText().toString().trim();
                    String studentvesrsity = editTextSchool.getText().toString().trim();

                    // as now we have information in string. Lets stored them with the help of editor
                    editor.putString("Name", studentname);
                    editor.putString("School", studentvesrsity);
                    editor.commit();
                    editdialog.dismiss();

                    final SweetAlertDialog sd = new SweetAlertDialog(v.getContext(), SweetAlertDialog.SUCCESS_TYPE);
                    sd.setTitleText("Good Job!");
                    sd.setContentText("Successfully Saved");
                    sd.setConfirmText("Ok");
                    sd.setCancelable(true);
                    sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                    sd.show();

                }
            });
        } else if (id == R.id.logout) {
            final SweetAlertDialog sd = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
            sd.setTitleText("You are about to Log Out!");
            sd.setContentText("Are You Sure?");
            sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    finish();
                    SharedPrefManager.getInstance(getApplicationContext()).logout();
                    prefManager.setFirstTimeLaunch(true);
                }
            });
            sd.show();
        } else if (id == R.id.sub_cat) {

            final List<Subjects> data = db.getAllSubjects();
            final List<String> fileName = db.getAllSubjectsInBinding();


            if (fileName.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No subjects Found", Toast.LENGTH_SHORT).show();
            }

            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.custom_add_subject_layout, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setView(view);

            Button btn_add = (Button) view.findViewById(R.id.btn_add);
            Button btncancel = (Button) view.findViewById(R.id.btn_cancel);
            final EditText editTextAddSubject = (EditText) view.findViewById(R.id.etAddSub);
            initSubDialogView(view);


            alertDialogBuilder
                    .setCancelable(true);

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String sub_name = editTextAddSubject.getText().toString();

                    if (db.CheckSubjectExistence(sub_name)) {
                        Toast.makeText(getApplicationContext(), "Subject is already exist", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        db.addSubjects(sub_name);
                        Toast.makeText(getApplicationContext(), "Subject Added Successfully", Toast.LENGTH_SHORT).show();
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

        } else if (id == R.id.exam_cat) {

            final List<ExamType> data = db.getAllExamTypes();
            final List<String> fileName = db.getAllExamTypeInBinding();


            if (fileName.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No Exam Category Found", Toast.LENGTH_SHORT).show();
            }

            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.custom_add_exam_type_layout, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setView(view);

            Button btn_add = (Button) view.findViewById(R.id.btn_add);
            Button btncancel = (Button) view.findViewById(R.id.btn_cancel);
            final EditText editTextAddExamType = (EditText) view.findViewById(R.id.etAddExamType);
            initExamTypeDialogView(view);


            alertDialogBuilder
                    .setCancelable(true);

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String exam_type = editTextAddExamType.getText().toString();

                    if (db.CheckExamTypeExistence(exam_type)) {
                        Toast.makeText(getApplicationContext(), "Exam Type is already exist", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        db.addExamTypes(exam_type);
                        Toast.makeText(getApplicationContext(), "Exam Type Added Successfully", Toast.LENGTH_SHORT).show();
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

        } else if (id == R.id.fee_cat) {

            final List<FeeType> data = db.getAllFeeTypes();
            final List<String> fileName = db.getAllFeeTypeInBinding();


            if (fileName.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No Fee Category Found", Toast.LENGTH_SHORT).show();
            }

            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.custom_add_fee_type_layout, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setView(view);

            Button btn_add = (Button) view.findViewById(R.id.btn_add);
            Button btncancel = (Button) view.findViewById(R.id.btn_cancel);
            final EditText editTextAddFeeType = (EditText) view.findViewById(R.id.etAddFeeType);
            initFeeTypeDialogView(view);


            alertDialogBuilder
                    .setCancelable(true);

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String fee_type = editTextAddFeeType.getText().toString();

                    if (db.CheckFeeTypeExistence(fee_type)) {
                        Toast.makeText(getApplicationContext(), "Fee Type is already exist", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        db.addFeeTypes(fee_type);
                        Toast.makeText(getApplicationContext(), "Fee Type Added Successfully", Toast.LENGTH_SHORT).show();
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
        else if(id == R.id.clear)
        {
            android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
            adb.setTitle("Caution!");
            adb.setMessage("Are you sure you want to Perform Master Reset?");
            adb.setNegativeButton("Cancel", null);
            adb.setPositiveButton("Ok", new android.support.v7.app.AlertDialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO SET TEXTVIEWS To NULL

                    MainActivity.this.deleteDatabase("school_management");
                    Toast.makeText(getApplicationContext(), "All Data Erased", Toast.LENGTH_LONG).show();
                    //recreate();

                    // Refreshing Activity
                    Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                }
            });

            android.support.v7.app.AlertDialog alertDialog = adb.create();
            alertDialog.show();
        }
        else if(id == R.id.db_backUp)
        {
            //creating a new folder for the database to be backuped to
            File direct = new File(Environment.getExternalStorageDirectory() + "/School_Management_DB_BackUp");

            if(!direct.exists())
            {
                if(direct.mkdir())
                {
                    //directory is created;
                }
            }
            db.exportDB();
        }
        else if(id == R.id.restore_db)
        {
            db.importDB();
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadDialog() {

        final Dialog editdialog = new Dialog(MainActivity.this);
        editdialog.setTitle("Profile");
        editdialog.setContentView(R.layout.custom_institution_layout);
        editdialog.setCancelable(false);
        editdialog.show();

        editTextName = (EditText) editdialog.findViewById(R.id.input_Name);
        editTextSchool = (EditText) editdialog.findViewById(R.id.input_VersityName);
        btn_save = (Button) editdialog.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validate Authority Sign Up Fields
                if (!validate()) {
                    return;
                }

                studentname = editTextName.getText().toString().trim();
                studentSchool = editTextSchool.getText().toString().trim();

                // as now we have information in string. Lets stored them with the help of editor
                editor.putString("Name", studentname);
                editor.putString("School", studentSchool);
                editor.commit();

                prefManager.setFirstTimeLaunch(false);

                editdialog.dismiss();

                final SweetAlertDialog sd = new SweetAlertDialog(v.getContext(), SweetAlertDialog.SUCCESS_TYPE);
                sd.setTitleText("Good Job!");
                sd.setContentText("Successfully Saved");
                sd.setConfirmText("Ok");
                sd.setCancelable(true);
                sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
                sd.show();

            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        String Name = editTextName.getText().toString().trim();
        String Versity = editTextSchool.getText().toString().trim();

        if (Name.isEmpty() || Name.length() < 3) {
            editTextName.setError("At least 3 characters");
            valid = false;
        } else {
            editTextName.setError(null);
        }
        if (Versity.isEmpty() || Versity.length() < 3) {
            editTextSchool.setError("At least 3 characters");
            valid = false;
        } else {
            editTextSchool.setError(null);
        }

        return valid;
    }
}
