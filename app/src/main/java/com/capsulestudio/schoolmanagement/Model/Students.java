package com.capsulestudio.schoolmanagement.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shuvo on 12/14/2017.
 */

public class Students implements Parcelable {

    private int id;
    private byte[] poto;
    private String student_name;
    private String student_roll;
    private String student_dob;
    private String student_dob_day_month;
    private String parent_name;
    private String student_mobile;
    private String address;
    private String class_name;
    private String section;
    private String admission_date;
    private int classID;

    public Students(){

    }

    public Students(byte[] poto, String student_name, String student_roll, String student_dob, String student_dob_day_month, String parent_name, String student_mobile, String address, String class_name, String section, String admission_date, int classID) {
        this.poto = poto;
        this.student_name = student_name;
        this.student_roll = student_roll;
        this.student_dob = student_dob;
        this.student_dob_day_month = student_dob_day_month;
        this.parent_name = parent_name;
        this.student_mobile = student_mobile;
        this.address = address;
        this.class_name = class_name;
        this.section = section;
        this.admission_date = admission_date;
        this.classID = classID;
    }

    public Students(int id, byte[] poto, String student_name, String student_roll, String student_dob, String student_dob_day_month, String parent_name, String student_mobile, String address, String class_name, String section, String admission_date, int classID) {
        this.id = id;
        this.poto = poto;
        this.student_name = student_name;
        this.student_roll = student_roll;
        this.student_dob = student_dob;
        this.student_dob_day_month = student_dob_day_month;
        this.parent_name = parent_name;
        this.student_mobile = student_mobile;
        this.address = address;
        this.class_name = class_name;
        this.section = section;
        this.admission_date = admission_date;
        this.classID = classID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getPoto() {
        return poto;
    }

    public void setPoto(byte[] poto) {
        this.poto = poto;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_roll() {
        return student_roll;
    }

    public void setStudent_roll(String student_roll) {
        this.student_roll = student_roll;
    }

    public String getStudent_dob() {
        return student_dob;
    }

    public void setStudent_dob(String student_dob) {
        this.student_dob = student_dob;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public String getStudent_mobile() {
        return student_mobile;
    }

    public void setStudent_mobile(String student_mobile) {
        this.student_mobile = student_mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getAdmission_date() {
        return admission_date;
    }

    public void setAdmission_date(String admission_date) {
        this.admission_date = admission_date;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getStudent_dob_day_month() {
        return student_dob_day_month;
    }

    public void setStudent_dob_day_month(String student_dob_day_month) {
        this.student_dob_day_month = student_dob_day_month;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeByteArray(this.poto);
        dest.writeString(this.student_name);
        dest.writeString(this.student_roll);
        dest.writeString(this.student_dob);
        dest.writeString(this.student_dob_day_month);
        dest.writeString(this.parent_name);
        dest.writeString(this.student_mobile);
        dest.writeString(this.address);
        dest.writeString(this.class_name);
        dest.writeString(this.section);
        dest.writeString(this.admission_date);
        dest.writeInt(this.classID);
    }

    protected Students(Parcel in) {
        this.id = in.readInt();
        this.poto = in.createByteArray();
        this.student_name = in.readString();
        this.student_roll = in.readString();
        this.student_dob = in.readString();
        this.student_dob_day_month = in.readString();
        this.parent_name = in.readString();
        this.student_mobile = in.readString();
        this.address = in.readString();
        this.class_name = in.readString();
        this.section = in.readString();
        this.admission_date = in.readString();
        this.classID = in.readInt();
    }

    public static final Parcelable.Creator<Students> CREATOR = new Parcelable.Creator<Students>() {
        @Override
        public Students createFromParcel(Parcel source) {
            return new Students(source);
        }

        @Override
        public Students[] newArray(int size) {
            return new Students[size];
        }
    };
}
