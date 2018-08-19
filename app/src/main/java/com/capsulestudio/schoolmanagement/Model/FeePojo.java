package com.capsulestudio.schoolmanagement.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Juman on 12/26/2017.
 */

public class FeePojo implements Parcelable{

    private int feeID;
    private String studentNameRoll;
    private int studentId;
    private String feeMonth;
    private String amount;
    private String feeDate;
    private String className;
    private String fee_type;
    private int classId;

    public FeePojo() {
    }

    public FeePojo(String studentNameRoll, int studentId, String feeMonth, String amount, String feeDate, String className, String fee_type, int classId) {
        this.studentNameRoll = studentNameRoll;
        this.studentId = studentId;
        this.feeMonth = feeMonth;
        this.amount = amount;
        this.feeDate = feeDate;
        this.className = className;
        this.fee_type = fee_type;
        this.classId = classId;
    }

    public FeePojo(int feeID, String studentNameRoll, int studentId, String feeMonth, String amount, String feeDate, String className, int classId) {
        this.feeID = feeID;
        this.studentNameRoll = studentNameRoll;
        this.studentId = studentId;
        this.feeMonth = feeMonth;
        this.amount = amount;
        this.feeDate = feeDate;
        this.className = className;
        this.classId = classId;
    }

    public int getFeeID() {
        return feeID;
    }

    public void setFeeID(int feeID) {
        this.feeID = feeID;
    }

    public String getStudentNameRoll() {
        return studentNameRoll;
    }

    public void setStudentNameRoll(String studentNameRoll) {
        this.studentNameRoll = studentNameRoll;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFeeMonth() {
        return feeMonth;
    }

    public void setFeeMonth(String feeMonth) {
        this.feeMonth = feeMonth;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFeeDate() {
        return feeDate;
    }

    public void setFeeDate(String feeDate) {
        this.feeDate = feeDate;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(feeID);
        dest.writeString(studentNameRoll);
        dest.writeInt(studentId);
        dest.writeString(feeMonth);
        dest.writeString(amount);
        dest.writeString(feeDate);
        dest.writeString(className);
        dest.writeString(fee_type);
        dest.writeInt(classId);
    }

    protected FeePojo(Parcel in) {
        this.feeID = in.readInt();
        this.studentNameRoll = in.readString();
        this.studentId = in.readInt();
        this.feeMonth = in.readString();
        this.amount = in.readString();
        this.feeDate = in.readString();
        this.className = in.readString();
        this.fee_type = in.readString();
        this.classId = in.readInt();
    }

    public static final Parcelable.Creator<FeePojo> CREATOR = new Parcelable.Creator<FeePojo>() {
        @Override
        public FeePojo createFromParcel(Parcel source) {
            return new FeePojo(source);
        }

        @Override
        public FeePojo[] newArray(int size) {
            return new FeePojo[size];
        }
    };
}
