package com.capsulestudio.schoolmanagement.Model;

/**
 * Created by Shuvo on 12/21/2017.
 */

public class StudentsAttendance {

    private int id;
    private byte[] poto;
    private int id_student;
    private int id_atd;
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
    private String month;
    private int day;
    private int classID;
    private boolean isSelected;


    public StudentsAttendance() {
    }

    public StudentsAttendance(int id, String student_name, String student_roll, String student_dob, String parent_name, String student_mobile, String address, String class_name, String section, String admission_date, int classID) {
        this.id = id;
        this.student_name = student_name;
        this.student_roll = student_roll;
        this.student_dob = student_dob;
        this.parent_name = parent_name;
        this.student_mobile = student_mobile;
        this.address = address;
        this.class_name = class_name;
        this.section = section;
        this.admission_date = admission_date;
        this.classID = classID;
        isSelected = false;
    }

    public StudentsAttendance(int id, byte[] poto, String student_name, String student_roll, String student_dob, String student_dob_day_month, String parent_name, String student_mobile, String address, String class_name, String section, String admission_date, int classID, int id_atd, String attendance_status, String atd_date,String month, int day, int id_student) {
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
        this.id_atd = id_atd;
        this.attendance_status = attendance_status;
        this.atd_date = atd_date;
        this.month = month;
        this.day = day;
        this.id_student = id_student;
    }

    public StudentsAttendance(int id_atd, String attendance_status, String atd_date, String month, int day, int id_student) {
        this.id_atd = id_atd;
        this.attendance_status = attendance_status;
        this.atd_date = atd_date;
        this.month = month;
        this.day = day;
        this.id_student = id_student;
    }

    public StudentsAttendance(int id, byte[] poto, String student_name, String student_roll, String student_dob, String student_dob_day_month, String parent_name, String student_mobile, String address, String class_name, String section, String admission_date, int classID) {
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAttendance_status() {
        return attendance_status;
    }

    public void setAttendance_status(String attendance_status) {
        this.attendance_status = attendance_status;
    }

    public int getId_student() {
        return id_student;
    }

    public void setId_student(int id_student) {
        this.id_student = id_student;
    }

    public String getAtd_date() {
        return atd_date;
    }

    public void setAtd_date(String atd_date) {
        this.atd_date = atd_date;
    }

    public int getId_atd() {
        return id_atd;
    }

    public void setId_atd(int id_atd) {
        this.id_atd = id_atd;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public byte[] getPoto() {
        return poto;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStudent_dob_day_month() {
        return student_dob_day_month;
    }

    public void setStudent_dob_day_month(String student_dob_day_month) {
        this.student_dob_day_month = student_dob_day_month;
    }
}
