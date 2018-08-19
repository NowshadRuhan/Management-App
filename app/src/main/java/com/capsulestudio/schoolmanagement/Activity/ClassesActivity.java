package com.capsulestudio.schoolmanagement.Activity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Adapter.ClassAdapter;
import com.capsulestudio.schoolmanagement.Helper.Config;
import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.R;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ClassesActivity extends AppCompatActivity {

    private LinearLayout LayEmpty;
    private FloatingActionButton fab;
    SQLiteDatabase dbHelper;
    private DatabaseHandler db;
    List<Classes> classList;
    ListView listViewClasses;
    ClassAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // getting views
        LayEmpty = (LinearLayout) findViewById(R.id.empty_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        dbHelper = openOrCreateDatabase(Config.DATABASE_NAME, MODE_PRIVATE, null);
        db = new DatabaseHandler(this);


        // Create Table Class
        // createClassTable();

        listViewClasses = (ListView) findViewById(R.id.listViewShowClasses);
        classList = new ArrayList<>();

        //this method will display the class in the list
        showClassesFromDatabase();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(ClassesActivity.this);
                View promptsView = li.inflate(R.layout.custom_class_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ClassesActivity.this);
                alertDialogBuilder.setTitle("Add New Class");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setView(promptsView);

                final EditText editTextClass = (EditText) promptsView.findViewById(R.id.editTextClass);
                final EditText editTextSection = (EditText) promptsView.findViewById(R.id.editTextSection);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        //getting the values
                                        String classname = editTextClass.getText().toString().trim();
                                        String section = editTextSection.getText().toString().trim();

                                        if (classname.isEmpty()) {
                                            Toast.makeText(getApplicationContext(), "Field Must be Empty", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        else if(section.isEmpty())
                                        {
                                            Toast.makeText(getApplicationContext(), "Default Section \"A\" added", Toast.LENGTH_SHORT).show();
                                            section = "A";
                                        }

                                        // Add Class Name and Section Name
                                        addClass(classname, section);
                                    }
                                })
                        .setNegativeButton("Close",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }


    private void showClassesFromDatabase() {
        classList = db.getAllClasses();
        //closing the cursor
        //cursorClasses.close();

        //creating the adapter object
        adapter = new ClassAdapter(this, R.layout.class_list, classList, dbHelper);

        // Checking adapter is empty or not
        if (adapter.isEmpty()) {
            LayEmpty.setVisibility(View.VISIBLE);
        } else {
            LayEmpty.setVisibility(View.GONE);
        }

        //adding the adapter to listview
        listViewClasses.setAdapter(adapter);

        listViewClasses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                // TODO Auto-generated method stub


                final SweetAlertDialog sd = new SweetAlertDialog(ClassesActivity.this, SweetAlertDialog.WARNING_TYPE);
                sd.setTitleText("Update OR Delete?");
                sd.setContentText("You can edit or delete any data.");
                sd.setConfirmText("Yes,delete it!");
                sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        String sql = "DELETE FROM tbl_classes WHERE id = ?";
                        dbHelper.execSQL(sql, new Integer[]{classList.get(position).getId()});
                        Toast.makeText(getApplicationContext(), "Class Deleted", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        reloadClassesFromDatabase();
                        sd.dismissWithAnimation();

                    }
                });
                sd.setCancelText("Yes,update it!");
                sd.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        LayoutInflater li = LayoutInflater.from(ClassesActivity.this);
                        View promptsView = li.inflate(R.layout.custom_class_dialog, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ClassesActivity.this);
                        alertDialogBuilder.setTitle("Add New Class");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setView(promptsView);

                        final EditText editTextClass = (EditText) promptsView.findViewById(R.id.editTextClass);
                        final EditText editTextSection = (EditText) promptsView.findViewById(R.id.editTextSection);

                        editTextClass.setText(classList.get(position).getClass_name());
                        editTextSection.setText(classList.get(position).getSection());

                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                //getting the values
                                                String classname = editTextClass.getText().toString().trim();
                                                String section = editTextSection.getText().toString().trim();

                                                // Add Class Name and Section Name
                                                updateClass(classname, section, classList.get(position).getId());
                                                sd.dismissWithAnimation();
                                            }
                                        })
                                .setNegativeButton("Close",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                sd.dismissWithAnimation();
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                });
                sd.show();

                //Toast.makeText(ClassesActivity.this, classList.get(position).getClass_name(), Toast.LENGTH_LONG).show();

                return true;
            }

        });

    }

    private void updateClass(String classname, String section, int id) {

        String sql = "UPDATE tbl_classes \n" +
                "SET class_name = ?, \n" +
                "section = ? \n" +
                "WHERE id = ?;\n";

        dbHelper.execSQL(sql, new String[]{classname, section, String.valueOf(id)});
        Toast.makeText(getApplicationContext(), "Class Updated", Toast.LENGTH_SHORT).show();
        reloadClassesFromDatabase();

    }

    private void reloadClassesFromDatabase() {

        Cursor cursorClasses = dbHelper.rawQuery("SELECT * FROM tbl_classes", null);
        if (cursorClasses.moveToFirst()) {
            classList.clear();
            do {
                classList.add(new Classes(
                        cursorClasses.getInt(0),
                        cursorClasses.getString(1),
                        cursorClasses.getString(2)
                ));
            } while (cursorClasses.moveToNext());
        }
        //cursorClasses.close();

        // Checking adapter is empty or not
        if(cursorClasses.moveToFirst())
        {
            LayEmpty.setVisibility(View.GONE);
        }
        else
        {
            LayEmpty.setVisibility(View.VISIBLE);
            listViewClasses.setVisibility(View.GONE);
        }
    }

    private void addClass(String classname, String section) {

        if(db.CheckClassExistence(classname, section))
        {
            Toast.makeText(getApplicationContext(), "Class is already exist", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            db.addClasses(new Classes(classname, section));
            Toast.makeText(this, "Class Added Successfully", Toast.LENGTH_SHORT).show();

            // Dynamically Create Attendance table of each class

            String class_section = classname.toLowerCase() + "_" + section.toLowerCase();


            String sql_student = "CREATE TABLE IF NOT EXISTS tbl_students_" + class_section + "(\n" +
                    "    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "    photo BLOB NOT NULL,\n" +
                    "    student_name varchar(200) NOT NULL,\n" +
                    "    student_roll varchar(200) NOT NULL,\n" +
                    "    student_dob date NOT NULL,\n" +
                    "    student_dob_day_month varchar(200) NOT NULL,\n" +
                    "    parent_name varchar(200) NOT NULL,\n" +
                    "    student_mobile varchar(200) NOT NULL,\n" +
                    "    address varchar(200) NOT NULL,\n" +
                    "    class_name varchar(200) NOT NULL,\n" +
                    "    section varchar(200) NOT NULL,\n" +
                    "    admission_date datetime NOT NULL,\n" +
                    "    classID INTEGER NOT NULL\n" +
                    ");";

            String sql_attendence = "CREATE TABLE IF NOT EXISTS tbl_attendance_" + class_section + "(\n" +
                    "    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "    atd_status varchar(200) NOT NULL,\n" +
                    "    atd_date datetime  NOT NULL,\n" +
                    "    month varchar(200) NOT NULL,\n" +
                    "    day INTEGER NOT NULL,\n" +
                    "    id_student INTEGER NOT NULL,\n" +
                    "    FOREIGN KEY(id_student) REFERENCES tbl_students_" + class_section + "(id)\n" +
                    ");";

            String sql_result = "CREATE TABLE IF NOT EXISTS tbl_result_" + class_section + "(\n" +
                    "    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "    test_date datetime NOT NULL,\n" +
                    "    sub_name varchar(200) NOT NULL,\n" +
                    "    total_marks varchar(200) NOT NULL,\n" +
                    "    pass_marks varchar(200) NOT NULL,\n" +
                    "    obtained_marks varchar (200) NOT NULL,\n" +
                    "    result_status varchar(200) NOT NULL,\n" +
                    "    description varchar(200) NOT NULL,\n" +
                    "    exam_type varchar(200) NOT NULL,\n" +
                    "    id_student INTEGER NOT NULL,\n" +
                    "    FOREIGN KEY(id_student) REFERENCES tbl_students_" + class_section + "(id)\n" +
                    ");";

            String sql_fee = "CREATE TABLE IF NOT EXISTS tbl_fee_" + class_section + "(\n" +
                    "    feeID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "    studentNameRoll varchar(200) NOT NULL,\n" +
                    "    studentId INTEGER NOT NULL,\n" +
                    "    feeMonth varchar(200) NOT NULL,\n" +
                    "    amount varchar(200) NOT NULL,\n" +
                    "    feeDate datetime  NOT NULL,\n" +
                    "    className varchar(200) NOT NULL,\n" +
                    "    fee_type varchar(200) NOT NULL,\n" +
                    "    classId INTEGER NOT NULL\n" +
                    ");";

            try {
                dbHelper.execSQL(sql_attendence);
                dbHelper.execSQL(sql_student);
                dbHelper.execSQL(sql_result);
                dbHelper.execSQL(sql_fee);

                //Toast.makeText(getApplicationContext(), class_section + " Attendance Table Created", Toast.LENGTH_LONG).show();
            } catch (SQLException e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            reloadClassesFromDatabase();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.search);
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
                    listViewClasses.clearTextFilter();
                } else {
                    adapter.filter(newText);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
