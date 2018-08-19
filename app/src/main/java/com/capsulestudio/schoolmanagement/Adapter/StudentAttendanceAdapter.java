package com.capsulestudio.schoolmanagement.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

public class StudentAttendanceAdapter extends ArrayAdapter<StudentsAttendance> {

    Context mCtx;
    int listLayoutRes;
    List<StudentsAttendance> studentsList;
    List<StudentsAttendance> searchList;
    SQLiteDatabase mDatabase;


    private int lastPosition = -1;

    public StudentAttendanceAdapter(Context mCtx, int listLayoutRes, List<StudentsAttendance> studentsList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, studentsList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.studentsList = studentsList;
        this.mDatabase = mDatabase;

        this.searchList = new ArrayList<>();
        this.searchList.addAll(studentsList);
    }

    static class ViewHolder {
        protected ImageView IvProfile;
        protected TextView textViewStudentName, textViewParentName, textViewRoll;
        protected AnimateCheckBox chkSelected;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        final StudentsAttendance students = studentsList.get(position);

        if (convertView == null) {
            final ViewHolder viewHolder = new ViewHolder();
            //getting students of the specified position


            //getting views
            viewHolder.IvProfile = (ImageView) view.findViewById(R.id.profile_image);
            viewHolder.textViewStudentName = (TextView) view.findViewById(R.id.tvName);
            viewHolder.textViewParentName = (TextView) view.findViewById(R.id.tvPName);
            viewHolder.textViewRoll = (TextView) view.findViewById(R.id.tvRoll);
            viewHolder.chkSelected = (AnimateCheckBox) view.findViewById(R.id.chkSelected);


            // Animation effect part
            Animation animation = AnimationUtils.loadAnimation(mCtx,
                    (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
            view.startAnimation(animation);
            lastPosition = position;

            /*//adding data to views
            Glide.with(mCtx).load(students.getPoto())
                    .into(viewHolder.IvProfile);
            //IvProfile.setImageBitmap(convertToBitmap(students.getPoto()));
            viewHolder.textViewStudentName.setText(students.getStudent_name());
            viewHolder.textViewParentName.setText(students.getParent_name());
            viewHolder.textViewRoll.setText("Roll Number : " + students.getStudent_roll());*/
            //viewHolder.chkSelected.setTag(students);

            viewHolder.chkSelected.setOnCheckedChangeListener(new AnimateCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(View buttonView, boolean isChecked) {
                    AnimateCheckBox cb = (AnimateCheckBox) buttonView;
                    StudentsAttendance contact = (StudentsAttendance)  cb.getTag();

                    contact.setSelected(cb.isChecked());
                    //students.setSelected(cb.isChecked());
                }
            });
            view.setTag(viewHolder);
            viewHolder.chkSelected.setTag(studentsList.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).chkSelected.setTag(studentsList.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.chkSelected.setChecked(students.isSelected());
        //adding data to views
        Glide.with(mCtx).load(students.getPoto())
                .into(holder.IvProfile);
        //IvProfile.setImageBitmap(convertToBitmap(students.getPoto()));
        holder.textViewStudentName.setText(students.getStudent_name());
        holder.textViewParentName.setText(students.getParent_name());
        holder.textViewRoll.setText("Roll Number : " + students.getStudent_roll());

        return view;
    }

    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b) {

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
                        s.getParent_name().toLowerCase(Locale.getDefault()).contains(charText)) {
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