package com.capsulestudio.schoolmanagement.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.capsulestudio.schoolmanagement.Model.FeePojo;
import com.capsulestudio.schoolmanagement.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Juman on 12/27/2017.
 */


public class StudentFeeAdapter  extends ArrayAdapter<FeePojo> {

    Context mCtx;
    int listLayoutRes;
    List<FeePojo> feeList;



    public StudentFeeAdapter(Context mCtx, int listLayoutRes, List<FeePojo> feeList) {
        super(mCtx, listLayoutRes, feeList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.feeList = feeList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        //getting classes of the specified position
        FeePojo feePojo = feeList.get(position);


        //getting views
        TextView tvMonth = (TextView) view.findViewById(R.id.tvMonth);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvAmount = (TextView) view.findViewById(R.id.tvAmount);
        TextView tvFeeType = (TextView) view.findViewById(R.id.tvFeeType);

        //adding data to views
        tvMonth.setText(feePojo.getFeeMonth());
        tvDate.setText(feePojo.getFeeDate());
        tvAmount.setText(feePojo.getAmount());
        tvFeeType.setText(feePojo.getFee_type());

        return view;
    }


}