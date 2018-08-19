package com.capsulestudio.schoolmanagement.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capsulestudio.schoolmanagement.Activity.AttendanceDetailsActivity;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.Model.StudentsAttendance;
import com.capsulestudio.schoolmanagement.R;
import com.hanks.library.AnimateCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shuvo on 12/16/2017.
 */

public class ViewStudentsAttendanceAdapter extends ArrayAdapter<StudentsAttendance> {

    Context mCtx;
    int listLayoutRes;
    List<StudentsAttendance> studentsList;
    List<StudentsAttendance> searchList;
    SQLiteDatabase mDatabase;

    private int lastPosition = -1;


    public ViewStudentsAttendanceAdapter(Context mCtx, int listLayoutRes, List<StudentsAttendance> studentsList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, studentsList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.studentsList = studentsList;
        this.mDatabase = mDatabase;

        this.searchList = new ArrayList<>();
        this.searchList.addAll(studentsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        //getting students of the specified position
        final StudentsAttendance students = studentsList.get(position);


        //getting views
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        ImageView IvProfile = (ImageView) view.findViewById(R.id.profile_image);
        TextView textViewStudentName = (TextView) view.findViewById(R.id.tvName);
        TextView textViewRoll = (TextView) view.findViewById(R.id.tvRoll);
        TextView textViewStatus = (TextView) view.findViewById(R.id.textViewStatus);


        // Animation effect part
        Animation animation = AnimationUtils.loadAnimation(mCtx,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        view.startAnimation(animation);
        lastPosition = position;

        //adding data to views
        Glide.with(mCtx).load(students.getPoto())
                .into(IvProfile);
        //IvProfile.setImageBitmap(convertToBitmap(students.getPoto()));
        textViewStudentName.setText(students.getStudent_name());
        textViewRoll.setText("Roll No : " + students.getStudent_roll());
        textViewStatus.setText(String.valueOf(students.getAttendance_status()));

        if(students.getAttendance_status().equalsIgnoreCase("Present"))
        {
            textViewStatus.setTextColor(Color.parseColor("#388E3C"));
        }
        else
        {
            textViewStatus.setTextColor(Color.parseColor("#e53935"));
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mCtx, students.getStudent_name(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mCtx, AttendanceDetailsActivity.class);
                intent.putExtra("ID_STUDENT", students.getId_student());
                intent.putExtra("Class_name", students.getClass_name());
                intent.putExtra("Section", students.getSection());
                ((Activity)mCtx).finish();
                mCtx.startActivity(intent);
            }
        });

        return view;
    }

    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b){

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        studentsList.clear();
        if (charText.length() == 0) {
            studentsList.addAll(searchList);
        } else {
            for (StudentsAttendance s : searchList) {
                if (s.getClass_name().toLowerCase(Locale.getDefault()).contains(charText) ||
                        s.getSection().toLowerCase(Locale.getDefault()).contains(charText) ||
                        s.getStudent_name().toLowerCase(Locale.getDefault()).contains(charText) ||
                        s.getParent_name().toLowerCase(Locale.getDefault()).contains(charText))
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
}