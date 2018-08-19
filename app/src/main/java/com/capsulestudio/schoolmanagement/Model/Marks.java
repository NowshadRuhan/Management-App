package com.capsulestudio.schoolmanagement.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shuvo on 12/24/2017.
 */

public class Marks implements Parcelable {

    private int id;
    private String test_date;
    private String sub_name;
    private String total_marks;
    private String pass_marks;
    private String obtained_marks;
    private String result_status;
    private String description;
    private String exam_type;
    private int id_student;

    // students model
    private int id_std;
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
    private String attendance_status;
    private String atd_date;
    private int classID;

    public Marks() {
    }

    public Marks(String sub_name, String total_marks, String pass_marks, String obtained_marks, String result_status, String description, String exam_type, int id_student) {
        this.sub_name = sub_name;
        this.total_marks = total_marks;
        this.pass_marks = pass_marks;
        this.obtained_marks = obtained_marks;
        this.result_status = result_status;
        this.description = description;
        this.exam_type = exam_type;
        this.id_student = id_student;
    }

    public Marks(int id, String test_date, String sub_name, String total_marks, String pass_marks, String obtained_marks, String result_status, int id_student) {
        this.id = id;
        this.test_date = test_date;
        this.sub_name = sub_name;
        this.total_marks = total_marks;
        this.pass_marks = pass_marks;
        this.obtained_marks = obtained_marks;
        this.result_status = result_status;
        this.id_student = id_student;
    }

    public Marks(String test_date, String sub_name, String total_marks, String pass_marks, String obtained_marks, String result_status, String description, String exam_type, int id_student) {
        this.test_date = test_date;
        this.sub_name = sub_name;
        this.total_marks = total_marks;
        this.pass_marks = pass_marks;
        this.obtained_marks = obtained_marks;
        this.result_status = result_status;
        this.description = description;
        this.exam_type = exam_type;
        this.id_student = id_student;
    }


    public Marks(int id_std, byte[] poto, String student_name, String student_roll, String student_dob, String student_dob_day_month, String parent_name, String student_mobile, String address, String class_name, String section, String atd_date, int classID, int id, String test_date, String sub_name, String total_marks, String pass_marks, String obtained_marks, String result_status, String description, String exam_type, int id_student) {
        this.id_std = id_std;
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
        this.atd_date = atd_date;
        this.classID = classID;
        this.id = id;
        this.test_date = test_date;
        this.sub_name = sub_name;
        this.total_marks = total_marks;
        this.pass_marks = pass_marks;
        this.obtained_marks = obtained_marks;
        this.result_status = result_status;
        this.description = description;
        this.exam_type = exam_type;
        this.id_student = id_student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTest_date() {
        return test_date;
    }

    public void setTest_date(String test_date) {
        this.test_date = test_date;
    }

    public String getSub_name() {
        return sub_name;
    }

    public void setSub_name(String sub_name) {
        this.sub_name = sub_name;
    }

    public String getTotal_marks() {
        return total_marks;
    }

    public void setTotal_marks(String total_marks) {
        this.total_marks = total_marks;
    }

    public String getPass_marks() {
        return pass_marks;
    }

    public void setPass_marks(String pass_marks) {
        this.pass_marks = pass_marks;
    }

    public String getObtained_marks() {
        return obtained_marks;
    }

    public void setObtained_marks(String obtained_marks) {
        this.obtained_marks = obtained_marks;
    }

    public String getResult_status() {
        return result_status;
    }

    public void setResult_status(String result_status) {
        this.result_status = result_status;
    }

    public int getId_student() {
        return id_student;
    }

    public void setId_student(int id_student) {
        this.id_student = id_student;
    }

    public int getId_std() {
        return id_std;
    }

    public void setId_std(int id_std) {
        this.id_std = id_std;
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

    public String getAttendance_status() {
        return attendance_status;
    }

    public void setAttendance_status(String attendance_status) {
        this.attendance_status = attendance_status;
    }

    public String getAtd_date() {
        return atd_date;
    }

    public void setAtd_date(String atd_date) {
        this.atd_date = atd_date;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStudent_dob_day_month() {
        return student_dob_day_month;
    }

    public void setStudent_dob_day_month(String student_dob_day_month) {
        this.student_dob_day_month = student_dob_day_month;
    }

    public String getExam_type() {
        return exam_type;
    }

    public void setExam_type(String exam_type) {
        this.exam_type = exam_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(test_date);
        dest.writeString(sub_name);
        dest.writeString(total_marks);
        dest.writeString(pass_marks);
        dest.writeString(obtained_marks);
        dest.writeString(result_status);
        dest.writeString(description);
        dest.writeString(exam_type);
        dest.writeInt(id_student);
        // Student
        dest.writeInt(this.id_std);
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

    protected Marks(Parcel in) {
        this.id = in.readInt();
        this.test_date = in.readString();
        this.sub_name = in.readString();
        this.total_marks = in.readString();
        this.pass_marks = in.readString();
        this.obtained_marks = in.readString();
        this.result_status = in.readString();
        this.description = in.readString();
        this.exam_type = in.readString();
        this.id_student = in.readInt();
        // Student
        this.id_std = in.readInt();
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

    public static final Parcelable.Creator<Marks> CREATOR = new Parcelable.Creator<Marks>() {
        @Override
        public Marks createFromParcel(Parcel source) {
            return new Marks(source);
        }

        @Override
        public Marks[] newArray(int size) {
            return new Marks[size];
        }
    };
}
