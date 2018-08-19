package com.capsulestudio.schoolmanagement.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Adapter.StudentAdapter;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StudentsActivity extends AppCompatActivity {

    private LinearLayout LayEmpty;
    private Spinner spinner_class;
    private FloatingActionMenu menu;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabMessage;
    private ListView listViewShowStudents;
    List<Students> studentList;
    StudentAdapter adapter;

    public String test, test2;
    public String class_name, section;

    SQLiteDatabase dbHelper;
    private static final String DataBaseName = "school_management";
    private static final int DB_Version = 1;
    private String class_id;
    String student_table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = openOrCreateDatabase(DataBaseName, MODE_PRIVATE, null);

        // Create Student Table
        //createStudentTable();

        // getting views
        LayEmpty = (LinearLayout) findViewById(R.id.empty_view);
        spinner_class = (Spinner) findViewById(R.id.spinner_classes);
        listViewShowStudents = (ListView) findViewById(R.id.listViewShowStudents);
        menu = (FloatingActionMenu) findViewById(R.id.menu_item);
        fabAdd = (FloatingActionButton) findViewById(R.id.add);

        // load spinner classes
        loadSpinnerClasses();  // Test

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // collapse fab menu after clicking search
                if (menu.isOpened()) {
                    menu.close(true);
                }
                Intent intent = new Intent(StudentsActivity.this, AddStudentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listViewShowStudents = (ListView) findViewById(R.id.listViewShowStudents);
        studentList = new ArrayList<>();

    }

    private void showStudentsFromDatabase(final String classNameL, String SectionL) {

        student_table = "tbl_students_"+classNameL.toLowerCase()+"_"+SectionL.toLowerCase();

        String query = "SELECT * FROM "+student_table + " ORDER BY student_roll ASC";
        Cursor cursorStudents = dbHelper.rawQuery(query, null);


        //if the cursor has some data
        if (cursorStudents.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the class list
                studentList.add(new Students(
                        cursorStudents.getInt(0),
                        cursorStudents.getBlob(1),
                        cursorStudents.getString(2),
                        cursorStudents.getString(3),
                        cursorStudents.getString(4),
                        cursorStudents.getString(5),
                        cursorStudents.getString(6),
                        cursorStudents.getString(7),
                        cursorStudents.getString(8),
                        cursorStudents.getString(9),
                        cursorStudents.getString(10),
                        cursorStudents.getString(11),
                        cursorStudents.getInt(12)

                ));
            } while (cursorStudents.moveToNext());
        }

        //closing the cursor
        cursorStudents.close();
        //creating the adapter object
        adapter = new StudentAdapter(this, R.layout.student_list, studentList, dbHelper);
        Toast.makeText(getApplicationContext(), studentList.toString(), Toast.LENGTH_LONG);

        // Checking adapter is empty or not
        if (adapter.isEmpty()) {
            LayEmpty.setVisibility(View.VISIBLE);
        } else {
            LayEmpty.setVisibility(View.GONE);
        }

        // On click listener
        listViewShowStudents.setAdapter(adapter);
        listViewShowStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Students student = studentList.get(position);

                Intent intent = new Intent(getApplicationContext(), UpdateStudentActivity.class);
                intent.putExtra("person", student);  // pass the object of a student class
                startActivity(intent);
                finish();
            }
        });


        listViewShowStudents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                // TODO Auto-generated method stub


                final SweetAlertDialog sd = new SweetAlertDialog(StudentsActivity.this, SweetAlertDialog.WARNING_TYPE);
                sd.setTitleText("Delete !");
                sd.setContentText("Are You Sure To Delete.");
                sd.setConfirmText("Yes, Delete It!");
                sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        String sql = "DELETE FROM " + student_table + " WHERE id = ?";
                        dbHelper.execSQL(sql, new Integer[]{studentList.get(position).getId()});
                        Toast.makeText(getApplicationContext(), "Student Deleted", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        reloadFromDatabase();
                        sd.dismissWithAnimation();

                    }
                });
                sd.show();


                return true;
            }

        });
    }

    private void createStudentTable() {
        dbHelper.execSQL(
                "CREATE TABLE IF NOT EXISTS tbl_all_students (\n" +
                        "    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                        "    student_name varchar(200) NOT NULL,\n" +
                        "    student_roll varchar(200) NOT NULL,\n" +
                        "    student_dob date NOT NULL,\n" +
                        "    parent_name varchar(200) NOT NULL,\n" +
                        "    student_mobile varchar(200) NOT NULL,\n" +
                        "    address varchar(200) NOT NULL,\n" +
                        "    class_name varchar(200) NOT NULL,\n" +
                        "    section varchar(200) NOT NULL,\n" +
                        "    admission_date datetime NOT NULL,\n" +
                        "    classID INTEGER NOT NULL,\n" +
                        "    CONSTRAINT fk_student_class\n" +
                        "    FOREIGN KEY (classID)\n" +
                        "    REFERENCES tbl_classes (id)\n" +
                        "    ON DELETE CASCADE" +
                        ");"
        );
    }

    private void loadSpinnerClasses() {
        final List<Classes> data = new ArrayList<>();
        final List<String> fileName = new ArrayList<>();
        Cursor cursor = dbHelper.rawQuery("SELECT * FROM tbl_classes", null);


        if(cursor.getCount() == 0)
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


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //fileName.add(cursor.getInt(0) + "-" + cursor.getString(1) + "(" + cursor.getString(2) + ")");
                fileName.add(cursor.getString(1).toUpperCase()+"("+cursor.getString(2).toUpperCase()+")");
                data.add(new Classes(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)));
            } while (cursor.moveToNext());
        }

        // closing connection
        //cursor.close();
        //dbHelper.close();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, fileName);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_class.setAdapter(dataAdapter);

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Classes classes = data.get(position);
                class_id = String.valueOf((classes.getId()));
                class_name = classes.getClass_name();
                section = classes.getSection();

                if (adapter != null){
                    studentList.clear();
                }
                showStudentsFromDatabase(class_name, section);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void reloadFromDatabase() {

        Cursor cursorStudent = dbHelper.rawQuery("SELECT * FROM "+student_table, null);
        if (cursorStudent.moveToFirst()) {
            studentList.clear();
            do {
                studentList.add(new Students(
                        cursorStudent.getInt(0),
                        cursorStudent.getBlob(1),
                        cursorStudent.getString(2),
                        cursorStudent.getString(3),
                        cursorStudent.getString(4),
                        cursorStudent.getString(5),
                        cursorStudent.getString(6),
                        cursorStudent.getString(7),
                        cursorStudent.getString(8),
                        cursorStudent.getString(9),
                        cursorStudent.getString(10),
                        cursorStudent.getString(11),
                        cursorStudent.getInt(12)

                ));
            } while (cursorStudent.moveToNext());
        }
        //cursorStudent.close();

        // Checking adapter is empty or not
        if(cursorStudent.moveToFirst())
        {
            LayEmpty.setVisibility(View.GONE);
        }
        else
        {
            LayEmpty.setVisibility(View.VISIBLE);
            listViewShowStudents.setVisibility(View.GONE);
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
                    listViewShowStudents.clearTextFilter();
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
