package com.capsulestudio.schoolmanagement.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.ExamType;
import com.capsulestudio.schoolmanagement.Model.FeePojo;
import com.capsulestudio.schoolmanagement.Model.FeeType;
import com.capsulestudio.schoolmanagement.Model.Marks;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.Model.StudentsAttendance;
import com.capsulestudio.schoolmanagement.Model.Subjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shuvo on 12/13/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "school_management";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Package Name
    private static final String PACKAGE_NAME = "com.capsulestudio.schoolmanagement";

    // Table 'Class' Documentation
    private static final String Table_Class_Name = "tbl_classes";
    private static final String KEY_Class_name = "class_name";
    private static final String KEY_Section = "section";
    private static final String KEY_ID = "id";
    private static final String KEY_Test_Date = "test_date";
    private static final String KEY_Sub_name = "sub_name";
    private static final String KEY_Total_Mark = "total_marks";
    private static final String KEY_Passing_Mark = "pass_marks";
    private static final String KEY_Obtained_Mark = "obtained_marks";
    private static final String KEY_Result_Status = "result_status";
    private static final String KEY_Description = "description";
    private static final String KEY_ExamType = "exam_type";
    private static final String KEY_ID_Student = "id_student";

    private static final String Create_table_Classes_Sql = "CREATE TABLE IF NOT EXISTS tbl_classes (\n" +
            "    id INTEGER NOT NULL CONSTRAINT class_pk PRIMARY KEY AUTOINCREMENT,\n" +
            "    class_name varchar(200) NOT NULL,\n" +
            "    section varchar(200) NOT NULL\n" +
            ");";

    // Table 'Attendance'Documentation
    private static String Create_table_Attendence_Sql;

    // Doc of student
    private static final String KEY_photo = "photo";
    private static final String KEY_student_name = "student_name";
    private static final String KEY_student_roll = "student_roll";
    private static final String KEY_student_dob = "student_dob";
    private static final String KEY_student_dob_day_month = "student_dob_day_month";
    private static final String KEY_parent_name = "parent_name";
    private static final String KEY_student_mobile = "student_mobile";
    private static final String KEY_address = "address";
    private static final String KEY_class_name = "class_name";
    private static final String KEY_section = "section";
    private static final String KEY_admission_date = "admission_date";
    private static final String KEY_classID = "classID";

    // Doc of student fee table
    private static final String KEY_Fee_ID = "feeID";
    private static final String KEY_Std_Name_Roll = "studentNameRoll";
    private static final String KEY_Std_ID = "studentId";
    private static final String KEY_Fee_Month = "feeMonth";
    private static final String KEY_Fee_Amount = "amount";
    private static final String KEY_Fee_Date = "feeDate";
    private static final String KEY_Fee_Class_Name = "className";
    private static final String KEY_Fee_Type = "fee_type";
    private static final String KEY_Fee_Class_ID = "classId";

    // Doc of subjects table
    private static final String Table_Subjects = "tbl_subjects";
    private static final String KEY_Sub_ID = "subID";
    private static final String KEY_Sub = "sub_name";

    // Doc of fee table
    private static final String Table_Fees = "tbl_fee_type";
    private static final String KEY_fee_ID = "feeID";
    private static final String KEY_fee_type = "fee_type";

    // Doc of exam table
    private static final String Table_Exams = "tbl_exam_type";
    private static final String KEY_exam_ID = "examID";
    private static final String KEY_exam_type = "exam_type";

    Context mtcx;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mtcx = context;
    }

    //Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_table_Classes_Sql); // Create Table Class
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Table_Class_Name);

        // Create tables again
        onCreate(db);
    }

    public boolean CheckClassExistence(String class_name, String section) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM tbl_classes WHERE class_name = ? AND section = ?";
        Cursor cursor = db.rawQuery(Query, new String[]{class_name, section});
        if (cursor.moveToFirst()) {
            // record exists
            return true;
        } else {
            // record not found
            return false;
        }
    }

    public void addClasses(Classes classe) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Class_name, classe.getClass_name());
        values.put(KEY_Section, classe.getSection());
        db.insert(Table_Class_Name, null, values);
        db.close();
    }

    public boolean CheckSubjectExistence(String sub_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM tbl_subjects WHERE sub_name = ?";
        Cursor cursor = db.rawQuery(Query, new String[]{sub_name});
        if (cursor.moveToFirst()) {
            // record exists
            return true;
        } else {
            // record not found
            return false;
        }
    }

    public void addSubjects(String sub_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Sub, sub_name);
        db.insert(Table_Subjects, null, values);
        db.close();
    }

    public List<Subjects> getAllSubjects() {

        List<Subjects> sublist = new ArrayList<Subjects>();

        String selecAlltQuery = "SELECT  * FROM " + Table_Subjects;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selecAlltQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Subjects subs = new Subjects();

                subs.setId(Integer.parseInt(cursor.getString(0)));
                subs.setSub_name(cursor.getString(1));

                sublist.add(subs);

            } while (cursor.moveToNext());
        }
        return sublist;
    }

    public List<String> getAllSubjectsInBinding() {
        List<String> subList = new ArrayList<String>();

        String selecAlltQuery = "SELECT  * FROM " + Table_Subjects;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selecAlltQuery, null);

        if (cursor.moveToFirst()) {
            do {

                String data = cursor.getString(1);
                subList.add(data);

            } while (cursor.moveToNext());
        }

        return subList;
    }

    public boolean CheckFeeTypeExistence(String fee_type) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM tbl_fee_type WHERE fee_type = ?";
        Cursor cursor = db.rawQuery(Query, new String[]{fee_type});
        if (cursor.moveToFirst()) {
            // record exists
            return true;
        } else {
            // record not found
            return false;
        }
    }

    public List<FeeType> getAllFeeTypes() {

        List<FeeType> feelist = new ArrayList<FeeType>();

        String selecAlltQuery = "SELECT  * FROM " + Table_Fees;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selecAlltQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FeeType feeType = new FeeType();

                feeType.setFee_id(Integer.parseInt(cursor.getString(0)));
                feeType.setFee_type(cursor.getString(1));

                feelist.add(feeType);

            } while (cursor.moveToNext());
        }
        return feelist;
    }

    public List<String> getAllFeeTypeInBinding() {
        List<String> feeList = new ArrayList<String>();

        String selecAlltQuery = "SELECT  * FROM " + Table_Fees;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selecAlltQuery, null);

        if (cursor.moveToFirst()) {
            do {

                String data = cursor.getString(1);
                feeList.add(data);

            } while (cursor.moveToNext());
        }

        return feeList;
    }

    public void addFeeTypes(String fee_type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_fee_type, fee_type);
        db.insert(Table_Fees, null, values);
        db.close();
    }

    public boolean CheckExamTypeExistence(String exam_type) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM tbl_exam_type WHERE exam_type = ?";
        Cursor cursor = db.rawQuery(Query, new String[]{exam_type});
        if (cursor.moveToFirst()) {
            // record exists
            return true;
        } else {
            // record not found
            return false;
        }
    }

    public List<ExamType> getAllExamTypes() {

        List<ExamType> examlist = new ArrayList<ExamType>();

        String selecAlltQuery = "SELECT  * FROM " + Table_Exams;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selecAlltQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ExamType examType = new ExamType();

                examType.setExam_id(Integer.parseInt(cursor.getString(0)));
                examType.setExam_type(cursor.getString(1));

                examlist.add(examType);

            } while (cursor.moveToNext());
        }
        return examlist;
    }

    public List<String> getAllExamTypeInBinding() {
        List<String> examList = new ArrayList<String>();

        String selecAlltQuery = "SELECT  * FROM " + Table_Exams;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selecAlltQuery, null);

        if (cursor.moveToFirst()) {
            do {

                String data = cursor.getString(1);
                examList.add(data);

            } while (cursor.moveToNext());
        }

        return examList;
    }

    public void addExamTypes(String exam_type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_exam_type, exam_type);
        db.insert(Table_Exams, null, values);
        db.close();
    }

    public boolean CheckMarkExistence(String TableName, String test_date, String sub_name, String id_student) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM " + TableName + " WHERE test_date = ? AND sub_name = ? AND id_student = ?";
        Cursor cursor = db.rawQuery(Query, new String[]{test_date, sub_name, id_student});
        if (cursor.moveToFirst()) {
            // record exists
            return true;
        } else {
            // record not found
            return false;
        }
    }

    public long addMarks(Marks marks, String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_Test_Date, marks.getTest_date());
        values.put(KEY_Sub_name, marks.getSub_name());
        values.put(KEY_Total_Mark, marks.getTotal_marks());
        values.put(KEY_Passing_Mark, marks.getPass_marks());
        values.put(KEY_Obtained_Mark, marks.getObtained_marks());
        values.put(KEY_Result_Status, marks.getResult_status());
        values.put(KEY_Description, marks.getDescription());
        values.put(KEY_ExamType, marks.getExam_type());
        values.put(KEY_ID_Student, marks.getId_student());

        long inserted = db.insert(table_name, null, values);
        db.close();

        return inserted;
    }

    public List<Classes> getAllClasses() {

        List<Classes> classList = new ArrayList<Classes>();

        String selecAlltQuery = "SELECT  * FROM " + Table_Class_Name;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selecAlltQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Classes classes = new Classes();

                classes.setId(Integer.parseInt(cursor.getString(0)));
                classes.setClass_name(cursor.getString(1));
                classes.setSection(cursor.getString(2));

                classList.add(classes);

            } while (cursor.moveToNext());
        }

        return classList;
    }

    public List<String> getAllClassAndSentionInBinding() {
        List<String> classList = new ArrayList<String>();

        String selecAlltQuery = "SELECT  * FROM " + Table_Class_Name;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selecAlltQuery, null);

        if (cursor.moveToFirst()) {
            do {

                String data = cursor.getString(1).toUpperCase() + "(" + cursor.getString(2).toUpperCase() + ")";
                classList.add(data);

            } while (cursor.moveToNext());
        }

        return classList;
    }

    public List<StudentsAttendance> getAllStudents(String class_name, String section_name) {

        String class_section = class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String student_table = "tbl_students_" + class_section;
        String student_attendance_table = "tbl_attendance_" + class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String query = "SELECT * FROM " + student_table + " a INNER JOIN " + student_attendance_table + " b ON a.id=b.id_student";

        List<StudentsAttendance> studentList = new ArrayList<StudentsAttendance>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorStudents = db.rawQuery(query, null);

        if (cursorStudents.moveToFirst()) {
            do {
                //pushing each record in the class list
                studentList.add(new StudentsAttendance(
                        cursorStudents.getInt(0),
                        cursorStudents.getBlob(1),
                        cursorStudents.getString(2),
                        cursorStudents.getString(3),
                        cursorStudents.getString(4),
                        cursorStudents.getString(5),
                        cursorStudents.getString(6),
                        cursorStudents.getString(7),
                        cursorStudents.getString(8),
                        cursorStudents.getString(9),
                        cursorStudents.getString(10),
                        cursorStudents.getString(11),
                        cursorStudents.getInt(12),
                        cursorStudents.getInt(13),
                        cursorStudents.getString(14),
                        cursorStudents.getString(15),
                        cursorStudents.getString(16),
                        cursorStudents.getInt(17),
                        cursorStudents.getInt(18)
                ));

            } while (cursorStudents.moveToNext());
        }

        return studentList;
    }

    public List<String> getAllStudentsBinding(String class_name, String section_name) {
        List<String> studentList = new ArrayList<String>();

        String student_table = "tbl_students_" + class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String query = "SELECT * FROM " + student_table + " ORDER BY student_roll ASC";
        SQLiteDatabase dbHelper = this.getReadableDatabase();
        Cursor cursorStudents = dbHelper.rawQuery(query, null);

        if (cursorStudents.moveToFirst()) {
            do {

                String data = cursorStudents.getString(3) + " - (" + cursorStudents.getString(2).toUpperCase() + ")";
                studentList.add(data);

            } while (cursorStudents.moveToNext());
        }

        return studentList;
    }

    public List<StudentsAttendance> getAllStudentsAttendance(String class_name, String section_name, String date) {

        String class_section = class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String student_table = "tbl_students_" + class_section;
        String student_attendance_table = "tbl_attendance_" + class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String query = "SELECT * FROM " + student_table + " a INNER JOIN " + student_attendance_table + " b ON a.id=b.id_student WHERE b.atd_date = ?";

        List<StudentsAttendance> studentList = new ArrayList<StudentsAttendance>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorStudents = db.rawQuery(query, new String[]{date});

        if (cursorStudents.moveToFirst()) {
            do {
                //pushing each record in the class list
                studentList.add(new StudentsAttendance(
                        cursorStudents.getInt(0),
                        cursorStudents.getBlob(1),
                        cursorStudents.getString(2),
                        cursorStudents.getString(3),
                        cursorStudents.getString(4),
                        cursorStudents.getString(5),
                        cursorStudents.getString(6),
                        cursorStudents.getString(7),
                        cursorStudents.getString(8),
                        cursorStudents.getString(9),
                        cursorStudents.getString(10),
                        cursorStudents.getString(11),
                        cursorStudents.getInt(12),
                        cursorStudents.getInt(13),
                        cursorStudents.getString(14),
                        cursorStudents.getString(15),
                        cursorStudents.getString(16),
                        cursorStudents.getInt(17),
                        cursorStudents.getInt(18)
                ));

            } while (cursorStudents.moveToNext());
        }

        return studentList;
    }

    // get Student All Information
    public List<Students> getAllStudentInfo(String student_table_name) {

        List<Students> studentsData = new ArrayList<Students>();
        String selecAlltQuery = "SELECT  * FROM " + student_table_name;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorStudents = db.rawQuery(selecAlltQuery, null);

        if (cursorStudents.moveToFirst()) {
            do {

                Students students = new Students();

                students.setId(cursorStudents.getInt(0));
                students.setPoto(cursorStudents.getBlob(1));
                students.setStudent_name(cursorStudents.getString(2));
                students.setStudent_roll(cursorStudents.getString(3));
                students.setStudent_dob(cursorStudents.getString(4));
                students.setParent_name(cursorStudents.getString(5));
                students.setParent_name(cursorStudents.getString(6));
                students.setStudent_mobile(cursorStudents.getString(7));
                students.setAddress(cursorStudents.getString(8));
                students.setClass_name(cursorStudents.getString(9));
                students.setSection(cursorStudents.getString(10));
                students.setAdmission_date(cursorStudents.getString(11));
                students.setClassID(cursorStudents.getInt(12));

                studentsData.add(students);

            } while (cursorStudents.moveToNext());
        }
        return studentsData;
    }

    public List<Students> getSingleStudentData(String keyword, String student_table) {
        List<Students> stdsingleList = new ArrayList<>();

        //String[] columns ={KEY_photo};
        String selection = KEY_ID + " LIKE ?";
        String[] selection_args = {keyword};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(student_table, null, selection, selection_args, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Students students = new Students();

                students.setPoto(cursor.getBlob(1));

                stdsingleList.add(students);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return stdsingleList;
    }

    // get student Roll with name
    public List<String> getStudentWithRollNumberBinding(String student_table_name) {

        List<String> nameRoll = new ArrayList<String>();

        String selecAlltQuery = "SELECT  * FROM " + student_table_name;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selecAlltQuery, null);

        if (cursor.moveToFirst()) {
            do {

                String data = cursor.getString(3) + " - " + cursor.getString(2);
                nameRoll.add(data);

            } while (cursor.moveToNext());
        }
        return nameRoll;
    }

    public List<Marks> getAllStudentsTest(String class_name, String section_name, String date) {

        String class_section = class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String student_table = "tbl_students_" + class_section;
        String student_result_table = "tbl_result_" + class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String query = "SELECT * FROM " + student_table + " a INNER JOIN " + student_result_table + " b ON a.id=b.id_student WHERE b.test_date=?";

        List<Marks> studentList = new ArrayList<Marks>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorStudents = db.rawQuery(query, new String[]{date});

        if (cursorStudents.moveToFirst()) {
            do {
                //pushing each record in the class list
                studentList.add(new Marks(
                        cursorStudents.getInt(0),
                        cursorStudents.getBlob(1),
                        cursorStudents.getString(2),
                        cursorStudents.getString(3),
                        cursorStudents.getString(4),
                        cursorStudents.getString(5),
                        cursorStudents.getString(6),
                        cursorStudents.getString(7),
                        cursorStudents.getString(8),
                        cursorStudents.getString(9),
                        cursorStudents.getString(10),
                        cursorStudents.getString(11),
                        cursorStudents.getInt(12),
                        cursorStudents.getInt(13),
                        cursorStudents.getString(14),
                        cursorStudents.getString(15),
                        cursorStudents.getString(16),
                        cursorStudents.getString(17),
                        cursorStudents.getString(18),
                        cursorStudents.getString(19),
                        cursorStudents.getString(20),
                        cursorStudents.getString(21),
                        cursorStudents.getInt(22)
                ));

            } while (cursorStudents.moveToNext());
        }

        return studentList;
    }


    public List<Marks> getAllStudentsTestHistory(String class_name, String section_name, String id_student) {

        String class_section = class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String student_table = "tbl_students_" + class_section;
        String student_result_table = "tbl_result_" + class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String query = "SELECT * FROM " + student_table + " a INNER JOIN " + student_result_table + " b ON a.id=b.id_student WHERE b.id_student = ? ORDER BY b.test_date DESC";

        List<Marks> markList = new ArrayList<Marks>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorStudents = db.rawQuery(query, new String[]{id_student});

        if (cursorStudents.moveToFirst()) {
            do {
                //pushing each record in the class list
                markList.add(new Marks(
                        cursorStudents.getInt(0),
                        cursorStudents.getBlob(1),
                        cursorStudents.getString(2),
                        cursorStudents.getString(3),
                        cursorStudents.getString(4),
                        cursorStudents.getString(5),
                        cursorStudents.getString(6),
                        cursorStudents.getString(7),
                        cursorStudents.getString(8),
                        cursorStudents.getString(9),
                        cursorStudents.getString(10),
                        cursorStudents.getString(11),
                        cursorStudents.getInt(12),
                        cursorStudents.getInt(13),
                        cursorStudents.getString(14),
                        cursorStudents.getString(15),
                        cursorStudents.getString(16),
                        cursorStudents.getString(17),
                        cursorStudents.getString(18),
                        cursorStudents.getString(19),
                        cursorStudents.getString(20),
                        cursorStudents.getString(21),
                        cursorStudents.getInt(22)
                ));

            } while (cursorStudents.moveToNext());
        }

        return markList;
    }


    public void creteAttendenceTableDynamically(String class_section) {

        Create_table_Attendence_Sql = "CREATE TABLE IF NOT EXISTS tbl_attendance_" + class_section + "(\n" +
                "    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "    atd_status varchar(200) NOT NULL,\n" +
                "    atd_date datetime  NOT NULL\n" +
                ");";

    }

    public boolean CheckStudentExistence(String TableName, String student_roll) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM " + TableName + " WHERE student_roll = ?";
        Cursor cursor = db.rawQuery(Query, new String[]{student_roll});
        if (cursor.moveToFirst()) {
            // record exists
            return true;
        } else {
            // record not found
            return false;
        }
    }

    public void addStudents(Students students, String table_name) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_photo, students.getPoto());
        values.put(KEY_student_name, students.getStudent_name());
        values.put(KEY_student_roll, students.getStudent_roll());
        values.put(KEY_student_dob, students.getStudent_dob());
        values.put(KEY_student_dob_day_month, students.getStudent_dob_day_month());
        values.put(KEY_parent_name, students.getParent_name());
        values.put(KEY_student_mobile, students.getStudent_mobile());
        values.put(KEY_address, students.getAddress());
        values.put(KEY_class_name, students.getClass_name());
        values.put(KEY_section, students.getSection());
        values.put(KEY_admission_date, students.getAdmission_date());
        values.put(KEY_classID, students.getClassID());

        db.insert(table_name, null, values);
        db.close();

    }

    //  Delete a student from Any Student Table
    public int deleteSingleStudent(String id, String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = KEY_ID + " LIKE ?"; // whereCluse
        String[] selection_args = {id};   // Where_args
        int deleted = db.delete(table_name, deleteQuery, selection_args);

        return deleted;
    }

    public int updateSingleStudent(Students students, String updateId, String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String updateQuery = KEY_ID + " LIKE ?";   //
        String[] selection_args = {updateId};

        values.put(KEY_photo, students.getPoto());
        values.put(KEY_student_name, students.getStudent_name());
        values.put(KEY_student_roll, students.getStudent_roll());
        values.put(KEY_student_dob, students.getStudent_dob());
        values.put(KEY_student_dob_day_month, students.getStudent_dob_day_month());
        values.put(KEY_parent_name, students.getParent_name());
        values.put(KEY_student_mobile, students.getStudent_mobile());
        values.put(KEY_address, students.getAddress());
        values.put(KEY_class_name, students.getClass_name());
        values.put(KEY_section, students.getSection());
        values.put(KEY_admission_date, students.getAdmission_date());
        values.put(KEY_classID, students.getClassID());

        int updated = db.update(table_name, values, updateQuery, selection_args);

        return updated;
    }

    public int updateSingleStudentMarks(Marks marks, String id, String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String updateQuery = KEY_ID + " LIKE ?";   //
        String[] selection_args = {id};

        values.put(KEY_Sub_name, marks.getSub_name());
        values.put(KEY_Total_Mark, marks.getTotal_marks());
        values.put(KEY_Passing_Mark, marks.getPass_marks());
        values.put(KEY_Obtained_Mark, marks.getObtained_marks());
        values.put(KEY_Result_Status, marks.getResult_status());
        values.put(KEY_Description, marks.getDescription());
        values.put(KEY_ExamType, marks.getExam_type());
        values.put(KEY_ID_Student, String.valueOf(marks.getId_student()));

        int updated = db.update(table_name, values, updateQuery, selection_args);

        return updated;
    }


    // Add student fee to fee table
    public long addStudenFee(FeePojo feePojo, String table_name) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_Std_Name_Roll, feePojo.getStudentNameRoll());
        values.put(KEY_Std_ID, feePojo.getStudentId());
        values.put(KEY_Fee_Month, feePojo.getFeeMonth());
        values.put(KEY_Fee_Amount, feePojo.getAmount());
        values.put(KEY_Fee_Date, feePojo.getFeeDate());
        values.put(KEY_Fee_Class_Name, feePojo.getClassName());
        values.put(KEY_Fee_Type, feePojo.getFee_type());
        values.put(KEY_Fee_Class_ID, feePojo.getClassId());

        long r = db.insert(table_name, null, values);
        db.close();

        return r;
    }

    public boolean CheckStudentFeeExistence(String TableName, String student_ID, String feeMonth, String fee_type) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "SELECT * FROM " + TableName + " WHERE studentId = ? AND feeMonth = ? AND fee_type = ?";
        Cursor cursor = db.rawQuery(Query, new String[]{student_ID, feeMonth, fee_type});
        if (cursor.moveToFirst()) {
            // record exists
            return true;
        } else {
            // record not found
            return false;
        }
    }

    public List<FeePojo> getsingleStudentFeeHistory(String keyword, String fee_table_name) {
        List<FeePojo> feePojoList = new ArrayList<>();
        //String[] projections ={UserName,UserRoll, UserAddress,UserPhone, UserEmail};
        String selection = KEY_Std_ID + " LIKE ?";
        String[] selection_args = {keyword};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(fee_table_name, null, selection, selection_args, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                FeePojo feePojo = new FeePojo();

                feePojo.setFeeID(cursor.getInt(0));
                feePojo.setStudentNameRoll(cursor.getString(1));
                feePojo.setStudentId(cursor.getInt(2));
                feePojo.setFeeMonth(cursor.getString(3));
                feePojo.setAmount(cursor.getString(4));
                feePojo.setFeeDate(cursor.getString(5));
                feePojo.setClassName(cursor.getString(6));
                feePojo.setFee_type(cursor.getString(7));
                feePojo.setClassId(cursor.getInt(8));


                feePojoList.add(feePojo);

            } while (cursor.moveToNext());
        }

        return feePojoList;
    }


    public List<Students> getAllStudentsMonthFeeHistory(String class_name, String section_name, String fee_month) {

        String class_section = class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String student_table = "tbl_students_" + class_section;
        String student_fee_table = "tbl_fee_" + class_section;

        String query = "SELECT * FROM " + student_table + " a INNER JOIN " + student_fee_table + " b ON a.id=b.studentId WHERE b.feeMonth = ? ";

        List<Students> paidMonthFeeList = new ArrayList<Students>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorStudents = db.rawQuery(query, new String[]{fee_month});

        if (cursorStudents.moveToFirst()) {
            do {
                Students students = new Students();

                students.setId(cursorStudents.getInt(0));
                students.setPoto(cursorStudents.getBlob(1));
                students.setStudent_name(cursorStudents.getString(2));
                students.setStudent_roll(cursorStudents.getString(3));
                students.setStudent_dob(cursorStudents.getString(4));
                students.setStudent_dob_day_month(cursorStudents.getString(5));
                students.setParent_name(cursorStudents.getString(6));
                students.setStudent_mobile(cursorStudents.getString(7));
                students.setAddress(cursorStudents.getString(8));
                students.setClass_name(cursorStudents.getString(9));
                students.setSection(cursorStudents.getString(10));
                students.setAdmission_date(cursorStudents.getString(11));
                students.setClassID(cursorStudents.getInt(12));

                paidMonthFeeList.add(students);

            } while (cursorStudents.moveToNext());
        }

        return paidMonthFeeList;
    }

    public List<Students> getAllStudentsMonthNoFeeHistory(String class_name, String section_name, String fee_month) {

        String class_section = class_name.toLowerCase() + "_" + section_name.toLowerCase();

        String student_table = "tbl_students_" + class_section;
        String student_fee_table = "tbl_fee_" + class_section;

        //String query = "SELECT * FROM " + student_table + " a INNER JOIN " + student_fee_table + " b ON a.id=b.studentId WHERE NOT b.feeMonth = ? ";

        String query = "SELECT * FROM " + student_table + " WHERE NOT EXISTS ( SELECT * FROM " + student_fee_table + " WHERE " + student_table + ".id = studentId AND feeMonth = ? )";

        List<Students> paidMonthFeeList = new ArrayList<Students>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorStudents = db.rawQuery(query, new String[]{fee_month});

        if (cursorStudents.moveToFirst()) {
            do {
                Students students = new Students();

                students.setId(cursorStudents.getInt(0));
                students.setPoto(cursorStudents.getBlob(1));
                students.setStudent_name(cursorStudents.getString(2));
                students.setStudent_roll(cursorStudents.getString(3));
                students.setStudent_dob(cursorStudents.getString(4));
                students.setStudent_dob_day_month(cursorStudents.getString(5));
                students.setParent_name(cursorStudents.getString(6));
                students.setStudent_mobile(cursorStudents.getString(7));
                students.setAddress(cursorStudents.getString(8));
                students.setClass_name(cursorStudents.getString(9));
                students.setSection(cursorStudents.getString(10));
                students.setAdmission_date(cursorStudents.getString(11));
                students.setClassID(cursorStudents.getInt(12));

                paidMonthFeeList.add(students);

            } while (cursorStudents.moveToNext());
        }

        return paidMonthFeeList;
    }

    //  Delete a student Fee entry from Any fee Table
    public int deleteSingleStudentFeeEntry(String id, String fee_table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = KEY_Fee_ID + " LIKE ?";
        String[] selection_args = {id};   // Where_args
        int deleted = db.delete(fee_table_name, deleteQuery, selection_args);

        return deleted;
    }

    /**
     *  Attendance PDF Section
     */

    public List<StudentsAttendance> getColumnSizeforMakeAtttTablePDF(String attendence_table_name, String monthw){
        List<StudentsAttendance> onlyDateList = new ArrayList<>();

        String query = "SELECT DISTINCT day FROM "+ attendence_table_name +" WHERE month = ? ORDER BY day ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor totalColumn = db.rawQuery(query, new String[]{monthw});

        if (totalColumn.moveToFirst()) {
            do {
                StudentsAttendance studentsAttendance = new StudentsAttendance();
                studentsAttendance.setDay(totalColumn.getInt(0));
                onlyDateList.add(studentsAttendance);

            } while (totalColumn.moveToNext());
        }

        return onlyDateList;
    }


    public List<Students> getHowManyStudentAttNeedToMakePDF(String student_table_name){
        List<Students> studentsData = new ArrayList<Students>();
        String selecAlltQuery = "SELECT  * FROM " + student_table_name+" order by student_roll ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorStudents = db.rawQuery(selecAlltQuery, null);

        if (cursorStudents.moveToFirst()) {
            do {

                Students students = new Students();

                students.setId(cursorStudents.getInt(0));
                students.setStudent_name(cursorStudents.getString(2));
                students.setStudent_roll(cursorStudents.getString(3));
                studentsData.add(students);

            } while (cursorStudents.moveToNext());
        }
        return studentsData;

    }

    public List<StudentsAttendance>  getSingleStudentAllAttStatus(String att_table_name, String month, String std_id){
        List<StudentsAttendance>  attendanceList = new ArrayList<StudentsAttendance>();

        String query = "SELECT atd_status , day FROM "+ att_table_name +" WHERE month = ? AND id_student = ? ORDER BY day ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{month, std_id});

        if (cursor.moveToFirst()) {
            do {
                StudentsAttendance attStatusAndDay = new StudentsAttendance();

                attStatusAndDay.setAttendance_status(cursor.getString(0));
                attStatusAndDay.setDay(cursor.getInt(1));

                attendanceList.add(attStatusAndDay);

            } while (cursor.moveToNext());
        }

        return attendanceList;
    }

    public int getAllStdAttendenceForPDF(String student_table, String attendence_table) {


        //String query = "SELECT * FROM " + student_table + " a INNER JOIN " + attendence_table + " b ON a.id=b.id_student ";
        String query = "SELECT  b.*, a.student_name FROM "+student_table+" a INNER JOIN "+attendence_table+" b ON a.id=b.id_student ";

        //List<Students>  paidMonthFeeList = new ArrayList<Students>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorStudents = db.rawQuery(query, null);

        return cursorStudents.getCount();

//        if (cursorStudents.moveToFirst()) {
//            do {
//                Students students = new Students();
//
//                students.setId(cursorStudents.getInt(0));
//                students.setPoto( cursorStudents.getBlob(1));
//                students.setStudent_name(cursorStudents.getString(2));
//                students.setStudent_roll(cursorStudents.getString(3));
//                students.setStudent_dob(cursorStudents.getString(4));
//                students.setParent_name(cursorStudents.getString(5));
//                students.setStudent_mobile(cursorStudents.getString(6));
//                students.setAddress(cursorStudents.getString(7));
//                students.setClass_name(cursorStudents.getString(8));
//                students.setSection(cursorStudents.getString(9));
//                students.setAdmission_date(cursorStudents.getString(10));
//                students.setClassID(cursorStudents.getInt(11));
//
//                paidMonthFeeList.add(students);
//
//            } while (cursorStudents.moveToNext());
//        }

        // return paidMonthFeeList;
    }


    public ArrayList<String> getArrayList(String class_section) {

        ArrayList<String> namesList = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT student_mobile FROM tbl_students_" + class_section;//your query here
            cursor = db.rawQuery(query,null);
            if (cursor != null && cursor.moveToFirst()) {
                namesList = new ArrayList<String>();
                do {
                    namesList.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            namesList = null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.deactivate();
                cursor.close();
                cursor = null;
            }
            close();
        }
        return namesList;
    }





    /**
     * Closes the database
     */
    public void close() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dbBackUp() throws IOException {
        final String inFileName = mtcx.getDatabasePath(DATABASE_NAME).getPath();
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        String outFileName = Environment.getExternalStorageDirectory()+"/database_copy.db";

        // Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer))>0){
            output.write(buffer, 0, length);
        }

        // Close the streams
        output.flush();
        output.close();
        fis.close();
    }

    //importing database
    public void importDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + PACKAGE_NAME
                        + "//databases//" + DATABASE_NAME;
                String backupDBPath  = "/School_Management_DB_BackUp/" + DATABASE_NAME;
                File  backupDB= new File(data, currentDBPath);
                File currentDB  = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(mtcx, "Successfully Imported", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(mtcx, e.toString(), Toast.LENGTH_LONG).show();

        }
    }

    //exporting database
    public void exportDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + PACKAGE_NAME
                        + "//databases//" + DATABASE_NAME;
                String backupDBPath  = "/School_Management_DB_BackUp/" + DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(mtcx, "Successfully Exported", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(mtcx, e.toString(), Toast.LENGTH_LONG).show();

        }
    }

}
