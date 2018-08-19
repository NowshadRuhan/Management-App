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

import com.capsulestudio.schoolmanagement.Model.Marks;
import com.capsulestudio.schoolmanagement.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shuvo on 12/25/2017.
 */

public class ViewAllTestAdapter extends ArrayAdapter<Marks> {

    Context mCtx;
    int listLayoutRes;
    List<Marks> markList;
    List<Marks> searchList;
    SQLiteDatabase mDatabase;

    private int lastPosition = -1;


    public ViewAllTestAdapter(Context mCtx, int listLayoutRes, List<Marks> markList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, markList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.markList = markList;
        this.mDatabase = mDatabase;

        this.searchList = new ArrayList<>();
        this.searchList.addAll(markList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        //getting marks of the specified position
        Marks marks = markList.get(position);


        //getting views
        TextView textViewStudentDate = (TextView) view.findViewById(R.id.textViewDate);
        TextView textViewSubject = (TextView) view.findViewById(R.id.textViewSubject);
        TextView textViewDesc = (TextView) view.findViewById(R.id.textViewDesc);
        TextView textViewTotalMarks = (TextView) view.findViewById(R.id.textViewTotalMarks);
        TextView textViewObtainedMarks = (TextView) view.findViewById(R.id.textViewObtainedMarks);

        // Animation effect part
        Animation animation = AnimationUtils.loadAnimation(mCtx,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        view.startAnimation(animation);
        lastPosition = position;

        //adding data to views
        textViewStudentDate.setText(marks.getTest_date());
        textViewSubject.setText(marks.getSub_name());
        textViewDesc.setText(marks.getDescription());
        textViewTotalMarks.setText(marks.getTotal_marks());
        textViewObtainedMarks.setText(marks.getObtained_marks());
        return view;
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        markList.clear();
        if (charText.length() == 0) {
            markList.addAll(searchList);
        } else {
            for (Marks s : searchList) {
                if (s.getSub_name().toLowerCase(Locale.getDefault()).contains(charText) ||
                        s.getTest_date().toLowerCase(Locale.getDefault()).contains(charText)) {
                    markList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }
}