package com.capsulestudio.schoolmanagement.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capsulestudio.schoolmanagement.Helper.CheckPermission;
import com.capsulestudio.schoolmanagement.Helper.DatabaseHandler;
import com.capsulestudio.schoolmanagement.Model.Classes;
import com.capsulestudio.schoolmanagement.Model.Students;
import com.capsulestudio.schoolmanagement.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.CALL_PHONE;
import static android.content.Intent.ACTION_CALL;

public class UpdateStudentActivity extends AppCompatActivity {

    ImageView  profilePic;
    Spinner spinner_class;
    EditText editTextName, editTextRoll, editTextDob, editTextParentName, editTextMobile, editTextAddress, editTextAdmissionDate;
    TextView textViewAge;
    Button btnUpdate;
    AutoCompleteTextView acTvClass;
    private TextInputLayout inputLayoutClass,inputLayoutName, inputLayoutRoll, inputLayoutDob, inputLayoutParentName,
            inputLayoutMobile, inputLayoutAddress, inputLayoutAdmissionDate;

    private Students student;
    private DatabaseHandler db;
    private int class_id;
    private String class_name;
    private String section_name;

    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_DIRECTORY = "schoolM";
    private static final int PICK_CAMERA_IMAGE = 2;
    private static final int PICK_GALLERY_IMAGE = 1;

    private File file;
    private File sourceFile;
    private SimpleDateFormat dateFormatter;
    private Uri imageCaptureUri;
    private Uri resultUri;
    private  Uri imageUri;
    private  File finalImage;
    private Bitmap bp;
    private byte[] photo;
    private String studentName, studentRoll, parentName, address, mobile, studentDob, admissionDate;

    String class_name_temp, section_name_temp, student_dob_day_month;
    int class_id_temp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("View Student");

        student = getIntent().getParcelableExtra("person"); // Perceable Object


        // make directory for temp image
        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);

        db = new DatabaseHandler(this);  // Database object create

        student_dob_day_month = student.getStudent_dob_day_month();
        photo = student.getPoto();  // if photo byte array is not update then we take the old byte array
        studentDob = student.getStudent_dob(); // if Dob not updated then we use previous Dob
        admissionDate = student.getAdmission_date(); //// if AdmissionD not updated then we use previous AdmissionD

        class_name_temp = student.getClass_name();
        section_name_temp = student.getSection();
        class_id_temp = student.getClassID();

        initView();  // View Initialize


        //loadClasses(); // Spinner Load



        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSupportActionBar().setTitle("Update Student");
                acTvClass.setEnabled(true);
                profilePic.setEnabled(true);
                editTextName.setEnabled(true);
                editTextRoll.setEnabled(true);
                editTextDob.setEnabled(true);
                editTextParentName.setEnabled(true);
                editTextMobile.setEnabled(true);
                editTextAddress.setEnabled(true);
                editTextAdmissionDate.setEnabled(true);
                btnUpdate.setVisibility(View.VISIBLE);

                fab.setVisibility(View.GONE);

                profilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater layoutInflater = LayoutInflater.from(UpdateStudentActivity.this);
                        View view =layoutInflater.inflate(R.layout.select_image_dialog_update, null);
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateStudentActivity.this);
                        alertDialogBuilder.setView(view);

                        TextView tvGallery = (TextView) view.findViewById(R.id.tvgallery);
                        TextView tvCamera = (TextView) view.findViewById(R.id.tvcamera);

                        alertDialogBuilder
                                .setCancelable(false)
                                .setTitle("Update Photo From")
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        tvGallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectImageFromGallery();
                                alertDialog.dismiss();
                            }
                        });

                        tvCamera.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                captureImageFromCamera();
                                alertDialog.dismiss();
                            }
                        });

                    }
                });

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStudentData();
            }
        });
    }

    private void updateStudentData() {

        boolean isValid = true;
        boolean  resultOk = false;
        String classSection = null;


        // Student name
        if (acTvClass.getText().toString().isEmpty()) {
            inputLayoutClass.setError("Name Field Must Not be Empty...");
            isValid = false;
        } else {
            inputLayoutName.setErrorEnabled(false);
            classSection = acTvClass.getText().toString().toUpperCase().trim();
            resultOk = checkClassNameIsValidOrNot(classSection);

        }

        // Student name
        if (editTextName.getText().toString().isEmpty()) {
            inputLayoutName.setError("Name Field Must Not be Empty !");
            isValid = false;
        } else {
            studentName = editTextName.getText().toString().trim();
            inputLayoutName.setErrorEnabled(false);
        }
        // Roll
        if (editTextRoll.getText().toString().isEmpty()) {
            inputLayoutRoll.setError("Field Must Not Be Empty");
            isValid = false;
        } else {
            studentRoll = editTextRoll.getText().toString().trim();
            inputLayoutRoll.setErrorEnabled(false);
        }

        // Parent Name
        if (editTextParentName.getText().toString().isEmpty()) {
            inputLayoutParentName.setError("Field Must Not Be Empty");
            isValid = false;
        } else {
            parentName = editTextParentName.getText().toString().trim();
            inputLayoutParentName.setErrorEnabled(false);
        }

        //Address
        if (editTextAddress.getText().toString().isEmpty()) {
            inputLayoutAddress.setError("Field Must Not Be Empty");
            isValid = false;
        } else {
            address = editTextAddress.getText().toString().trim();
            inputLayoutAddress.setErrorEnabled(false);
        }
        //Mobile
        if (editTextMobile.getText().toString().isEmpty()) {
            inputLayoutMobile.setError("Field Must Not Be Empty");
            isValid = false;
        } else {
            mobile = editTextMobile.getText().toString().trim();
            inputLayoutMobile.setErrorEnabled(false);
        }

        //Dob
        if (editTextDob.getText().toString().isEmpty()) {
            inputLayoutDob.setError("Field Must Not Be Empty");
            isValid = false;
        } else {
            inputLayoutDob.setErrorEnabled(false);
        }

        //Admission
        if (editTextAdmissionDate.getText().toString().isEmpty()) {
            inputLayoutAdmissionDate.setError("Field Must Not Be Empty");
            isValid = false;
        } else {
            inputLayoutAdmissionDate.setErrorEnabled(false);
        }

        //Photo
        if (photo == null) {
            Toast.makeText(getApplicationContext(), "Must Be Add A Picture !", Toast.LENGTH_LONG).show();
            isValid = false;
        }


        if (isValid) {
            if (resultOk) {
                String[] split = classSection.split("\\(");
                String class_split = split[0];// Four
                String secondSubString = split[1]; // A)

                String[] split2 = secondSubString.split("\\)");
                String section_split = split2[0];// A

                Log.e("s1: ", class_split);
                Log.e("s2: ", section_split);



                String selected_table_name = "tbl_students_" + class_split.toLowerCase() + "_" + section_split.toLowerCase();
                String privious_table_name = "tbl_students_" + student.getClass_name().toLowerCase() + "_" + student.getSection().toLowerCase();

                Log.e("selected_table: ", selected_table_name);
                Log.e("Previous_table: ", privious_table_name);


                if (selected_table_name.equals(privious_table_name)) {   // if previous table and current table is same
                    try {
                        String upID = String.valueOf(student.getId());
                        Log.e("student_dob_month_day: ", student_dob_day_month);
                        int upResult = db.updateSingleStudent(new Students(photo, studentName, studentRoll, studentDob, student_dob_day_month, parentName, mobile, address, class_split.toLowerCase(), section_split.toLowerCase(), admissionDate, class_id_temp), upID, selected_table_name);

                        if (upResult > 0) {
                            Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), StudentsActivity.class));
                            finish();

                        } else {
                            Toast.makeText(this, "Something is Problem !", Toast.LENGTH_SHORT).show();
                        }

                    } catch (SQLException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else { // when the table is not same, first we delete the entry from previous table and then we insert the entry to the selected table

                    try {
                        String delId = String.valueOf(student.getId());
                        int d = db.deleteSingleStudent(delId, privious_table_name);
                        db.addStudents(new Students(photo, studentName, studentRoll, studentDob, student_dob_day_month, parentName, mobile, address, class_split, section_split, admissionDate, class_id_temp), selected_table_name);
                        Toast.makeText(getApplicationContext(), "Student Updated Successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), StudentsActivity.class));
                        finish();
                    } catch (SQLException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            } else {
                Toast.makeText(getApplicationContext(), "  Invalid class name,Please select from List when Typing....", Toast.LENGTH_LONG).show();

            }
        }
    }

    private boolean checkClassNameIsValidOrNot(String classSection) {
        List<String> data = db.getAllClassAndSentionInBinding();
        for(int i=0; i<data.size(); i++){
            if (classSection.equals(data.get(i))){
                return true;
            }
        }
        return false;
    }

    // Image take from Gallery
    private void selectImageFromGallery() {
        CheckPermission checkPermission = new CheckPermission(UpdateStudentActivity.this);
        if (checkPermission.checkPermission()){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_GALLERY_IMAGE);
        }else {
            checkPermission.requestPermission();
        }
    }

    // Image take from camera
    private void captureImageFromCamera() {
        CheckPermission checkPermission = new CheckPermission(UpdateStudentActivity.this);
        if (checkPermission.checkPermission()){
            sourceFile = new File(file, "img_"
                    + dateFormatter.format(new Date()).toString() + ".jpeg");
            imageCaptureUri = Uri.fromFile(sourceFile);

            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
            startActivityForResult(intentCamera, PICK_CAMERA_IMAGE);
        }else {
            checkPermission.requestPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Gallery
        if (requestCode == PICK_GALLERY_IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(2,2)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);

        }

        // Camera
        if (requestCode == PICK_CAMERA_IMAGE && resultCode == RESULT_OK){
            //imageUri = data.getData();
            if (imageCaptureUri == null){
                Toast.makeText(getApplicationContext(), "Uri empty", Toast.LENGTH_LONG).show();
            }else{

                CropImage.activity(imageCaptureUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(2,2)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(this);

            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
//                finalImage = new File(file, path);
                if(resultUri !=null){

                    bp=decodeUri(resultUri, 400);
                    photo = profileImage(bp);
                    profilePic.setImageBitmap(bp);

                    if (sourceFile != null){   // delete the capture file from camera
                        sourceFile.delete();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Result Uri Empty", Toast.LENGTH_LONG).show();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    //COnvert and resize our image to 400dp for faster uploading our images to DB
    protected Bitmap decodeUri(Uri selectedImage, int REQUIRED_SIZE) {

        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            // final int REQUIRED_SIZE =  size;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //Convert bitmap to bytes
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private byte[] profileImage(Bitmap b){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 30, bos);
        return bos.toByteArray();

    }


    private void initView() {

        profilePic = (ImageView) findViewById(R.id.show_profile_image);
        spinner_class = (Spinner) findViewById(R.id.spinner_classes);
        editTextName = (EditText) findViewById(R.id.student_name);
        editTextRoll = (EditText) findViewById(R.id.student_roll);
        editTextDob = (EditText) findViewById(R.id.student_dob);
        editTextParentName = (EditText) findViewById(R.id.guardian_name);
        editTextMobile = (EditText) findViewById(R.id.student_mobile);
        editTextAddress = (EditText) findViewById(R.id.student_address);
        editTextAdmissionDate = (EditText) findViewById(R.id.student_admission_date);
        textViewAge = (TextView) findViewById(R.id.age);
        btnUpdate = (Button) findViewById(R.id.btn_update);


        inputLayoutClass = (TextInputLayout) findViewById(R.id.InputLayout_student_class);
        inputLayoutName = (TextInputLayout) findViewById(R.id.InputLayout_student_name);
        inputLayoutRoll = (TextInputLayout) findViewById(R.id.InputLayout_student_roll);
        inputLayoutDob = (TextInputLayout) findViewById(R.id.InputLayout_student_dob);
        inputLayoutParentName = (TextInputLayout) findViewById(R.id.InputLayout_guardian_name);
        inputLayoutMobile = (TextInputLayout) findViewById(R.id.InputLayout_student_mobile);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.InputLayout_student_address);
        inputLayoutAdmissionDate = (TextInputLayout) findViewById(R.id.InputLayout_student_admission_date);


        profilePic.setEnabled(false);
        editTextName.setEnabled(false);
        editTextRoll.setEnabled(false);
        editTextDob.setEnabled(false);
        editTextParentName.setEnabled(false);
        editTextMobile.setEnabled(false);
        editTextAddress.setEnabled(false);
        editTextAdmissionDate.setEnabled(false);

        editTextName.setTextColor(getResources().getColor(R.color.black));
        editTextRoll.setTextColor(getResources().getColor(R.color.black));
        editTextDob.setTextColor(getResources().getColor(R.color.black));
        editTextParentName.setTextColor(getResources().getColor(R.color.black));
        editTextMobile.setTextColor(getResources().getColor(R.color.black));
        editTextAddress.setTextColor(getResources().getColor(R.color.black));
        editTextAdmissionDate.setTextColor(getResources().getColor(R.color.black));


        profilePic.setImageBitmap(convertToBitmap(student.getPoto()));
        editTextName.setText(student.getStudent_name());
        editTextRoll.setText(student.getStudent_roll());
        editTextDob.setText(student.getStudent_dob());
        editTextParentName.setText(student.getParent_name());
        editTextMobile.setText(student.getStudent_mobile());
        editTextAddress.setText(student.getAddress());
        editTextAdmissionDate.setText(student.getAdmission_date());


        // Autocomplete for class
        final List<String> data = db.getAllClassAndSentionInBinding();
        acTvClass = (AutoCompleteTextView) findViewById(R.id.autoCompleteClass);
        acTvClass.setEnabled(false);
        acTvClass.setTextColor(getResources().getColor(R.color.black));

        String class_section = student.getClass_name().toUpperCase()+"("+student.getSection().toUpperCase()+")";
        acTvClass.setText(class_section);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_dropdown_item_1line,data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        acTvClass.setThreshold(1);
        acTvClass.setAdapter(adapter);



        editTextDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(UpdateStudentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        /*String date = String.valueOf(year) + "-" + String.valueOf(monthOfYear+1)
                                + "-" + String.valueOf(dayOfMonth);*/
                        String date, day = null, month = null;

                        if (dayOfMonth < 10) {
                            day = "0" + String.valueOf(dayOfMonth);

                        } else {
                            day = String.valueOf(dayOfMonth);
                        }
                        if (monthOfYear + 1 < 10) {
                            month = "0" + String.valueOf(monthOfYear + 1);
                        } else {
                            month = String.valueOf(monthOfYear + 1);
                        }

                        date = String.valueOf(year) + "-" + month
                                + "-" + day;


                        student_dob_day_month = month + "-" + day;

                        studentDob = date;

                        editTextDob.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });

        editTextAdmissionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(UpdateStudentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date, day = null, month = null;

                        if(dayOfMonth < 10)
                        {
                            day = "0" + String.valueOf(dayOfMonth);

                        }
                        else
                        {
                            day = String.valueOf(dayOfMonth);
                        }
                        if(monthOfYear+1 < 10)
                        {
                            month = "0" + String.valueOf(monthOfYear+1);
                        }
                        else
                        {
                            month = String.valueOf(monthOfYear+1);
                        }

                        date = String.valueOf(year) + "-" + month
                                + "-" + day;

                        admissionDate = date;

                        editTextAdmissionDate.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });
    }

    private void loadClasses() {

        final List<Classes> fileName = db.getAllClasses();
        final List<String> data = db.getAllClassAndSentionInBinding();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, data);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // attaching data adapter to spinner
        spinner_class.setAdapter(dataAdapter);

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Classes classes = fileName.get(position);
                class_id =  classes.getId();
                class_name = classes.getClass_name();
                section_name = classes.getSection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //get bitmap image from byte array
    private Bitmap convertToBitmap(byte[] b){

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.call:

                CheckPermission checkPermission = new CheckPermission(UpdateStudentActivity.this);

                if (checkPermission.checkSinglePermission(CALL_PHONE)){

                    Intent callIntent = new Intent(ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + student.getStudent_mobile()));
                    startActivity(callIntent);
                }else{
                    checkPermission.requestForSinglePermission(CALL_PHONE);
                }
                break;
            case R.id.sms:
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", student.getStudent_mobile());
                startActivity(smsIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
