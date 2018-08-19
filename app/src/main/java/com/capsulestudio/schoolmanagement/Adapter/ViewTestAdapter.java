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
import com.capsulestudio.schoolmanagement.Model.Marks;
import com.capsulestudio.schoolmanagement.Model.Marks;
import com.capsulestudio.schoolmanagement.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shuvo on 12/25/2017.
 */

public class ViewTestAdapter extends ArrayAdapter<Marks> {

    Context mCtx;
    int listLayoutRes;
    List<Marks> markList;
    List<Marks> searchList;
    SQLiteDatabase mDatabase;

    private int lastPosition = -1;


    public ViewTestAdapter(Context mCtx, int listLayoutRes, List<Marks> markList, SQLiteDatabase mDatabase) {
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
        ImageView IvProfile = (ImageView) view.findViewById(R.id.profile_image);
        TextView textViewStudentName = (TextView) view.findViewById(R.id.textViewStudentName);
        TextView textViewParentName = (TextView) view.findViewById(R.id.textViewParentName);
        TextView textViewSubname = (TextView) view.findViewById(R.id.textViewSubname);
        TextView textViewStatus = (TextView) view.findViewById(R.id.textViewResultStatus);
        TextView textViewClassName = (TextView) view.findViewById(R.id.textViewClassName);
        TextView textViewRoll = (TextView) view.findViewById(R.id.textViewRoll);

        // Animation effect part
        Animation animation = AnimationUtils.loadAnimation(mCtx,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        view.startAnimation(animation);
        lastPosition = position;

        //adding data to views
        Glide.with(mCtx).load(marks.getPoto())
                .into(IvProfile);
        //IvProfile.setImageBitmap(convertToBitmap(marks.getPoto()));
        textViewStudentName.setText(marks.getStudent_name());
        textViewParentName.setText(marks.getParent_name());
        textViewStatus.setText(marks.getResult_status() + " : (" + marks.getObtained_marks() + "/" + marks.getTotal_marks() + ")");
        textViewSubname.setText(marks.getSub_name());
        textViewClassName.setText(marks.getClass_name() + "(" + marks.getSection() + ")");
        textViewRoll.setText("Roll : " + marks.getStudent_roll());
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
                if (s.getClass_name().toLowerCase(Locale.getDefault()).contains(charText) ||
                        s.getSection().toLowerCase(Locale.getDefault()).contains(charText)) {
                    markList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b){

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }
}