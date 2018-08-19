package com.capsulestudio.schoolmanagement.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Adapter.ViewAllTestAdapter;
import com.capsulestudio.schoolmanagement.Adapter.ViewTestAdapter;
import com.capsulestudio.schoolmanagement.Helper.Config;
import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Marks;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.Model.StudentsAttendance;
import com.capsulestudio.schoolmanagement.R;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewAllTestHistory extends AppCompatActivity {

    ImageView profile_pic;
    TextView textViewRollNameClass;
    ListView listViewStudentTestHistory;
    String class_name, section_name;
    int id_student;
    SQLiteDatabase dbHelper;
    private DatabaseHandler db;

    List<Marks> marksList;
    ViewAllTestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_test_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        class_name = getIntent().getStringExtra("class_name");
        section_name = getIntent().getStringExtra("section_name");
        id_student = getIntent().getIntExtra("id_student", 0);

        dbHelper = openOrCreateDatabase(Config.DATABASE_NAME, MODE_PRIVATE, null);
        db = new DatabaseHandler(this);

        // getting views
        profile_pic = (ImageView) findViewById(R.id.profile_image);
        textViewRollNameClass = (TextView) findViewById(R.id.textViewRollNameClass);
        listViewStudentTestHistory = (ListView) findViewById(R.id.listViewStudentsTestHistory);


        // show test history
        showTestHistory();

    }

    private void showTestHistory() {

        marksList = db.getAllStudentsTestHistory(class_name, section_name, String.valueOf(id_student));


        if(marksList.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "No Test History Found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        profile_pic.setImageBitmap(convertToBitmap(marksList.get(0).getPoto()));
        textViewRollNameClass.setText(marksList.get(0).getStudent_roll() + " - " + marksList.get(0).getStudent_name() + " " + class_name + "(" + section_name + ")");


        //creating the adapter object
        adapter = new ViewAllTestAdapter(this, R.layout.student_all_test_history, marksList, dbHelper);
        listViewStudentTestHistory.setAdapter(adapter);


        listViewStudentTestHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Marks marks = marksList.get(position);
                Intent intent = new Intent(getApplicationContext(), UpdateTestActivity.class);
                intent.putExtra("marks", marks);  // pass the object of a student class
                startActivity(intent);

            }
        });

        listViewStudentTestHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final SweetAlertDialog sd = new SweetAlertDialog(ViewAllTestHistory.this, SweetAlertDialog.WARNING_TYPE);
                sd.setTitleText("Delete !");
                sd.setContentText("Are You Sure To Delete.");
                sd.setConfirmText("Yes, Delete It!");
                sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        Marks marks = marksList.get(position);
                        String Table_name = "tbl_result_";
                        // concatenate
                        Table_name += class_name + "_" + section_name;
                        String sql = "DELETE FROM " + Table_name + " WHERE id = ?";
                        dbHelper.execSQL(sql, new Integer[]{marks.getId()});
                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        showTestHistory();
                        sd.dismissWithAnimation();

                    }
                });
                sd.show();


                return true;
            }
        });
    }

    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b) {

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.menu_search, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.search);
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
                    listViewStudentTestHistory.clearTextFilter();
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
