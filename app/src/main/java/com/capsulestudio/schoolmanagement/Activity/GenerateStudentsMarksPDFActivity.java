package com.capsulestudio.schoolmanagement.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Adapter.ViewAllTestAdapter;
import com.capsulestudio.schoolmanagement.Helper.CheckPermission;
import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.Marks;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.Model.StudentsAttendance;
import com.capsulestudio.schoolmanagement.R;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class GenerateStudentsMarksPDFActivity extends AppCompatActivity {
    private static DatabaseHandler db;
    SQLiteDatabase dbHelper;
    private static final String DataBaseName = "school_management";

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 24,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    private static Font columnText = new Font(Font.FontFamily.TIMES_ROMAN, 8,
            Font.BOLD);

    private static Font verysmall = new Font(Font.FontFamily.TIMES_ROMAN, 6,
            Font.NORMAL);

    private String class_id;
    private String class_name;
    private String section;

    private TextView textViewCheckFee;
    private Spinner spinnerclasses;
    private Spinner spinner_student;
    private Button btngeneratePDF;
    private List<Students> studentsList;
    private String student_table;
    private static List<Marks> marksList;
    private static String attendence_table;

    private File pdfFolder;
    private File myFile;
    public static final String IMAGE_DIRECTORY = "schoolM";
    public static final String Pdf_DIRECTORY = "PDF";
    private static String selectedMonth;
    private static List<StudentsAttendance> columnday;
    List<StudentsAttendance> studentName;
    private static List<Students> howManyStudent;
    private int id_student;
    private String Table_Name, student_Name, studentRoll;

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_students_marks_pdf);

        sharedPreferences = getApplicationContext().getSharedPreferences("school_management", 0); // O for Private Mode
        // get editor to edit in file
        editor = sharedPreferences.edit();

        // make directory for temp image
        pdfFolder = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY+ "/" + Pdf_DIRECTORY);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Generate Marks PDF");

        spinner_student = (Spinner) findViewById(R.id.spinner_students);
        spinnerclasses = (Spinner) findViewById(R.id.spinner_classes);
        textViewCheckFee = (TextView) findViewById(R.id.textViewCheckFee);
        btngeneratePDF = (Button) findViewById(R.id.btnGenerate);

        db = new DatabaseHandler(this);
        dbHelper = openOrCreateDatabase(DataBaseName, MODE_PRIVATE, null);

        loadSpinnerClasses();

        btngeneratePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    CheckPermission checkPermission = new CheckPermission(GenerateStudentsMarksPDFActivity.this);
                    if (checkPermission.checkSinglePermission(WRITE_EXTERNAL_STORAGE)){
                        generatePDF();
                    }else{
                        checkPermission.requestForSinglePermission(WRITE_EXTERNAL_STORAGE);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private void loadStudent(final String class_name, final String section_name) {

        studentName = db.getAllStudents(class_name, section_name);
        final List<String> data = db.getAllStudentsBinding(class_name, section_name);

        if (data.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No students found for this class", Toast.LENGTH_SHORT).show();
        }
        if(studentName.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "No attendance data found", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, data);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // attaching data adapter to spinner
        spinner_student.setAdapter(dataAdapter);

        spinner_student.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                StudentsAttendance students = studentName.get(position);

                id_student = students.getId_student();
                Table_Name = "tbl_result_" + class_name.toLowerCase() + "_" + section_name.toLowerCase();
                showTestHistory();
                //Toast.makeText(getApplicationContext(), students.getStudent_name().toString().trim(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void loadSpinnerClasses() {
        final List<Classes> data = db.getAllClasses();
        final List<String> fileName = db.getAllClassAndSentionInBinding();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, fileName);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerclasses.setAdapter(dataAdapter);

        spinnerclasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Classes classes = data.get(position);

                class_id = String.valueOf((classes.getId()));
                class_name = classes.getClass_name();
                section = classes.getSection();

                // load student spinner
                loadStudent(class_name, section);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showTestHistory() {

        marksList = db.getAllStudentsTestHistory(class_name, section, String.valueOf(id_student));
        if(marksList.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "No Test History Found", Toast.LENGTH_SHORT).show();
            return;
        }

        student_Name = marksList.get(0).getStudent_name();
        studentRoll = marksList.get(0).getStudent_roll();

    }



    private void generatePDF() throws FileNotFoundException, DocumentException {
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String pdfName = "Marks"+class_name.toUpperCase()+"_"+section.toUpperCase()+"_"+timeStamp+".pdf";

        myFile = new File(pdfFolder, pdfName);

        OutputStream output = new FileOutputStream(myFile);
        //Step 1
        Document document = new Document();
        //Step 2
        PdfWriter.getInstance(document, output);
        //Step 3
        document.open();

        //Step 4 Add content

        addTitlePage(document);
        createTable(document);

        document.close();

        promptForNextAction();

    }

    public void addTitlePage(Document document) throws DocumentException {
        Paragraph paragraph;

        // Header
        paragraph = new Paragraph(sharedPreferences.getString("School", ""),catFont);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        addEmptyLine(paragraph, 1);

        // Class - Section
        paragraph = new Paragraph("Class: "+class_name+"     "+"Section: "+section, subFont);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        addEmptyLine(paragraph, 1);

        // Attendance Month
        paragraph = new Paragraph("Student Name: " + student_Name + ", Roll: " + studentRoll, subFont);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        addEmptyLine(paragraph, 1);

        // Generate Date
        paragraph = new Paragraph("Marks Sheet Generate Date:  "+ new Date(), smallBold);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(paragraph, 2);
        document.add(paragraph);

    }


    private static void createTable(Document subCatPart)
            throws DocumentException {

        int NumofColumn = 14; // take 2 column for Roll and 3 column for Name

        PdfPTable table = new PdfPTable(NumofColumn);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(100);


        PdfPCell c1 = new PdfPCell(new Phrase("Id", columnText));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor (BaseColor.GRAY);
        c1.setColspan (2);
        //c1.setPadding (10.0f);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Subject", columnText));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor (BaseColor.GRAY);
        c1.setColspan (3);
        //c1.setPadding (10.0f);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Total Mark", columnText));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor (BaseColor.GRAY);
        c1.setColspan (3);
        //c1.setPadding (10.0f);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Obtained Mark", columnText));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor (BaseColor.GRAY);
        c1.setColspan (3);
        //c1.setPadding (10.0f);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Date", columnText));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor (BaseColor.GRAY);
        c1.setColspan (3);
        //c1.setPadding (10.0f);
        table.addCell(c1);


        table.setHeaderRows(1);

        for (int i=0; i<marksList.size(); i++){

            Marks marks = marksList.get(i);

            c1 = new PdfPCell(new Phrase(String.valueOf(marks.getId()), verysmall));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setColspan (2);
            //c1.setPadding (10.0f);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(marks.getSub_name(), verysmall));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setColspan (3);
            //c1.setPadding (10.0f);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(marks.getTotal_marks(), verysmall));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setColspan (3);
            //c1.setPadding (10.0f);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(marks.getObtained_marks(), verysmall));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setColspan (3);
            //c1.setPadding (10.0f);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(marks.getTest_date(), verysmall));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setColspan (3);
            //c1.setPadding (10.0f);
            table.addCell(c1);

        }

        subCatPart.add(table);

    }

    private void promptForNextAction()
    {
        final String[] options = { getString(R.string.label_preview),
                getString(R.string.label_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(GenerateStudentsMarksPDFActivity.this);
        builder.setTitle("Note Saved, What Next?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getString(R.string.label_preview))){
                    viewPdf();
                }else if (options[which].equals(getString(R.string.label_cancel))){
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }

    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}