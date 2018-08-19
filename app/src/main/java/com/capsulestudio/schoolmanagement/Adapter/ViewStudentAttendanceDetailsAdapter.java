package com.capsulestudio.schoolmanagement.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Activity.AttendanceDetailsActivity;
import com.capsulestudio.schoolmanagement.Activity.ViewAttendanceActivity;
import com.capsulestudio.schoolmanagement.Model.StudentsAttendance;
import com.capsulestudio.schoolmanagement.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Shuvo on 12/22/2017.
 */

public class ViewStudentAttendanceDetailsAdapter extends ArrayAdapter<StudentsAttendance> {

    Context mCtx;
    int listLayoutRes;
    String class_name, section;
    List<StudentsAttendance> studentsList;
    List<StudentsAttendance> searchList;
    SQLiteDatabase mDatabase;


    private int lastPosition = -1;

    public ViewStudentAttendanceDetailsAdapter(Context mCtx, int listLayoutRes, List<StudentsAttendance> studentsList, SQLiteDatabase mDatabase, String class_name, String section) {
        super(mCtx, listLayoutRes, studentsList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.studentsList = studentsList;
        this.mDatabase = mDatabase;
        this.class_name = class_name;
        this.section = section;
        this.searchList = new ArrayList<>();
        this.searchList.addAll(studentsList);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        //getting students of the specified position
        final StudentsAttendance students = studentsList.get(position);

        //getting views
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        TextView textViewDate = (TextView) view.findViewById(R.id.tvDate);
        TextView textViewStatus = (TextView) view.findViewById(R.id.tvStatus);

        // Animation effect part
        Animation animation = AnimationUtils.loadAnimation(mCtx,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        view.startAnimation(animation);
        lastPosition = position;

        //adding data to views
        textViewStatus.setText(String.valueOf(students.getAttendance_status()));
        textViewDate.setText(students.getAtd_date());
        if(students.getAttendance_status().equalsIgnoreCase("Present"))
        {
            linearLayout.setBackgroundResource(R.color.present_color);

        }
        else
        {
            linearLayout.setBackgroundResource(R.color.absent_color);
        }


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mCtx, students.getAttendance_status(), Toast.LENGTH_SHORT).show();

                final Dialog editdialog = new Dialog(mCtx);
                editdialog.setContentView(R.layout.custom_edit_dialog);
                editdialog.setCancelable(true);
                editdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                editdialog.show();

                final TextView textViewStatus = (TextView) editdialog.findViewById(R.id.tv_atdStatus);
                final CheckBox chkSelected = (CheckBox) editdialog.findViewById(R.id.chkSelected);

                textViewStatus.setText("Current Status : " + students.getAttendance_status());

                if(students.getAttendance_status().equalsIgnoreCase("Present"))
                {
                    chkSelected.setChecked(true);
                }
                else {
                    chkSelected.setChecked(false);
                }

                Button btn_update = (Button) editdialog.findViewById(R.id.btn_update);

                btn_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String status;

                        if(chkSelected.isChecked())
                        {
                            status = "Present";
                        }
                        else
                        {
                            status = "Absent";
                        }

                        // Update Attendance
                        updateAttendance(class_name.toLowerCase(), section.toLowerCase(), status, students.getAtd_date(), students.getMonth(), students.getDay(), students.getId_atd(), studentsList.get(position).getId_student());

                        editdialog.dismiss();
                    }
                });
            }
        });

        return view;
    }



    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        studentsList.clear();
        if (charText.length() == 0) {
            studentsList.addAll(searchList);
        } else {
            for (StudentsAttendance s : searchList) {
                if (s.getAtd_date().toLowerCase(Locale.getDefault()).contains(charText) ||
                        s.getAttendance_status().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    studentsList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    // method to access in activity after updating selection
    public List<StudentsAttendance> getStudentist() {
        return studentsList;

    }
    private void updateAttendance(String classname, String section, String status, String date, String month, int day, int id_atd, int id_student) {

        String class_section = classname.toLowerCase() + "_" + section.toLowerCase();
        String student_attendance_table = "tbl_attendance_" + class_section;

        String sql = "UPDATE " + student_attendance_table + " \n" +
                "SET atd_status = ?, \n" +
                "atd_date = ?, \n" +
                "month = ?, \n" +
                "day = ? \n" +
                "WHERE id = ?;\n";

        try {
            mDatabase.execSQL(sql, new String[]{status, date, month, String.valueOf(day), String.valueOf(id_atd)});
            Toast.makeText(mCtx, "Updated", Toast.LENGTH_SHORT).show();

            // Refreshing Activity
            Intent intent = new Intent(mCtx, AttendanceDetailsActivity.class);
            ((Activity)mCtx).overridePendingTransition(0, 0);
            intent.putExtra("ID_STUDENT", id_student);
            intent.putExtra("Class_name", classname);
            intent.putExtra("Section", section);
            ((Activity)mCtx).finish();
            ((Activity)mCtx).overridePendingTransition(0, 0);
            mCtx.startActivity(intent);
        }
        catch (SQLException e)
        {
            Toast.makeText(mCtx, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }



}