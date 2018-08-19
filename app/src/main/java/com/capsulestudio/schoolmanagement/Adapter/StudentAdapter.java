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
import com.capsulestudio.schoolmanagement.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shuvo on 12/13/2017.
 */

public class StudentAdapter  extends ArrayAdapter<Students> {

    Context mCtx;
    int listLayoutRes;
    List<Students> studentsList;
    List<Students> searchList;
    SQLiteDatabase mDatabase;

    private int lastPosition = -1;




    public StudentAdapter(Context mCtx, int listLayoutRes, List<Students> studentsList, SQLiteDatabase mDatabase) {
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
        Students students = studentsList.get(position);


        //getting views
        ImageView IvProfile = (ImageView) view.findViewById(R.id.profile_image);
        TextView textViewStudentName = (TextView) view.findViewById(R.id.textViewStudentName);
        TextView textViewParentName = (TextView) view.findViewById(R.id.textViewParentName);
        TextView textViewClassName = (TextView) view.findViewById(R.id.textViewClassName);
        TextView textViewRoll = (TextView) view.findViewById(R.id.textViewRoll);


        /**
         *  Animation Part
         */

        Animation animation = AnimationUtils.loadAnimation(mCtx,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.bottom_from_up);
        view.startAnimation(animation);
        lastPosition = position;


        //adding data to views
        Glide.with(mCtx).load(students.getPoto())
                .into(IvProfile);
        //IvProfile.setImageBitmap(convertToBitmap(students.getPoto()));
        textViewStudentName.setText(students.getStudent_name());
        textViewParentName.setText(students.getParent_name());
        textViewClassName.setText(students.getClass_name() + "(" + students.getSection() + ")");
        textViewRoll.setText("Roll: " + students.getStudent_roll());
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
            for (Students s : searchList) {
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
}
