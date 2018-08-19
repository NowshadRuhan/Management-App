package com.capsulestudio.schoolmanagement.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shuvo on 12/13/2017.
 */

public class ClassAdapter  extends ArrayAdapter<Classes> {

    Context mCtx;
    int listLayoutRes;
    List<Classes> classList;
    List<Classes> searchList;
    SQLiteDatabase mDatabase;

    private int lastPosition = -1;


    public ClassAdapter(Context mCtx, int listLayoutRes, List<Classes> classList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, classList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.classList = classList;
        this.mDatabase = mDatabase;

        this.searchList = new ArrayList<>();
        this.searchList.addAll(classList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        //getting classes of the specified position
        Classes classes = classList.get(position);


        //getting views
        TextView textViewName = (TextView) view.findViewById(R.id.textViewClassName);

        // Animation effect part
        Animation animation = AnimationUtils.loadAnimation(mCtx,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        view.startAnimation(animation);
        lastPosition = position;

        //adding data to views
        textViewName.setText(classes.getId() + " - " + classes.getClass_name() + "(" + classes.getSection() + ")");
        return view;
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        classList.clear();
        if (charText.length() == 0) {
            classList.addAll(searchList);
        } else {
            for (Classes s : searchList) {
                if (s.getClass_name().toLowerCase(Locale.getDefault()).contains(charText) ||
                        s.getSection().toLowerCase(Locale.getDefault()).contains(charText)) {
                    classList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }
}
