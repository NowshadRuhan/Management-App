package com.capsulestudio.schoolmanagement.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddStudentActivity extends AppCompatActivity {

    Spinner spinner_class;
    EditText editTextName, editTextRoll, editTextDob, editTextParentName, editTextMobile, editTextAddress,
            editTextAdmissionDate;
    private TextInputLayout inputLayoutName, inputLayoutRoll, inputLayoutDob, inputLayoutParentName,
            inputLayoutMobile, inputLayoutAddress, inputLayoutAdmissionDate;

    TextView textViewAge;
    Button button_save;
    ImageView imageView;
    Calendar myCalendar;
    int class_id;
    String firstSubString, secondSubString;
    String studentName, studentRoll, parentName, address, mobile, studentDob, studentDob_day_month, admissionDate;
    SQLiteDatabase dbHelper;
    private static final String DataBaseName = "school_management";
    private static final int DB_Version = 1;

    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_DIRECTORY = "schoolM";
    private static final int PICK_CAMERA_IMAGE = 2;
    private static final int PICK_GALLERY_IMAGE = 1;

    private File file;
    private File sourceFile;
    private SimpleDateFormat dateFormatter;
    private Uri imageCaptureUri;
    private Uri resultUri;
    private Uri imageUri;
    private File finalImage;
    private Bitmap bp;
    private byte[] photo;
    private DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myCalendar = Calendar.getInstance();

        // getting views

        initViews();

        // make directory for temp image
        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);

        dbHelper = openOrCreateDatabase(DataBaseName, MODE_PRIVATE, null);
        db = new DatabaseHandler(this);

        Bitmap tempBMP = BitmapFactory.decodeResource(getResources(),R.drawable.camera);
        photo = profileImage(tempBMP);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(AddStudentActivity.this);
                View view = layoutInflater.inflate(R.layout.select_image_dialog, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddStudentActivity.this);
                alertDialogBuilder.setView(view);

                TextView tvGallery = (TextView) view.findViewById(R.id.tvgallery);
                TextView tvCamera = (TextView) view.findViewById(R.id.tvcamera);
                TextView tvRemove = (TextView) view.findViewById(R.id.tvremove);

                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("Take Photo From")
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
                tvRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeImage();
                        alertDialog.dismiss();
                    }
                });

            }
        });


        // load spinner classes
        loadClasses();

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudents();
            }
        });

        editTextDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(AddStudentActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                        studentDob = date;
                        studentDob_day_month = month + "-" + day;
                        editTextDob.setText(date);

                        String age = getAge(year, monthOfYear + 1, dayOfMonth);
                        textViewAge.setText(age + " Years");

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
                DatePickerDialog datePicker = new DatePickerDialog(AddStudentActivity.this, new DatePickerDialog.OnDateSetListener() {
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

    private void initViews() {

        spinner_class = (Spinner) findViewById(R.id.spinner_classes);
        editTextName = (EditText) findViewById(R.id.student_name);
        editTextRoll = (EditText) findViewById(R.id.student_roll);
        editTextDob = (EditText) findViewById(R.id.student_dob);
        editTextParentName = (EditText) findViewById(R.id.guardian_name);
        editTextMobile = (EditText) findViewById(R.id.student_mobile);
        editTextAddress = (EditText) findViewById(R.id.student_address);
        editTextAdmissionDate = (EditText) findViewById(R.id.student_admission_date);
        textViewAge = (TextView) findViewById(R.id.age);
        button_save = (Button) findViewById(R.id.btn_save);
        imageView = (ImageView) findViewById(R.id.imagebtn);

        inputLayoutName = (TextInputLayout) findViewById(R.id.InputLayout_student_name);
        inputLayoutRoll = (TextInputLayout) findViewById(R.id.InputLayout_student_roll);
        inputLayoutDob = (TextInputLayout) findViewById(R.id.InputLayout_student_dob);
        inputLayoutParentName = (TextInputLayout) findViewById(R.id.InputLayout_guardian_name);
        inputLayoutMobile = (TextInputLayout) findViewById(R.id.InputLayout_student_mobile);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.InputLayout_student_address);
        inputLayoutAdmissionDate = (TextInputLayout) findViewById(R.id.InputLayout_student_admission_date);


    }

    // Image take from Gallery
    private void selectImageFromGallery() {
        CheckPermission checkPermission = new CheckPermission(AddStudentActivity.this);
        if (checkPermission.checkPermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_GALLERY_IMAGE);
        } else {
            checkPermission.requestPermission();
        }
    }

    // Image take from camera
    private void captureImageFromCamera() {
        CheckPermission checkPermission = new CheckPermission(AddStudentActivity.this);
        if (checkPermission.checkPermission()) {
            sourceFile = new File(file, "img_"
                    + dateFormatter.format(new Date()).toString() + ".jpeg");
            imageCaptureUri = Uri.fromFile(sourceFile);

            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
            startActivityForResult(intentCamera, PICK_CAMERA_IMAGE);
        } else {
            checkPermission.requestPermission();
        }
    }

    // remove image
    private void removeImage() {
        imageView.setImageResource(R.mipmap.camera);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Gallery
        if (requestCode == PICK_GALLERY_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();

//            CropImage.activity(imageUri)
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(2,2)
//                    .start(this);
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);

        }

        // Camera
        if (requestCode == PICK_CAMERA_IMAGE && resultCode == RESULT_OK) {
            //imageUri = data.getData();
            if (imageCaptureUri == null) {
                Toast.makeText(getApplicationContext(), "Uri empty", Toast.LENGTH_LONG).show();
            } else {

                CropImage.activity(imageCaptureUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(2, 2)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(this);

            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
//                finalImage = new File(file, path);
                if (resultUri != null) {

                    bp = decodeUri(resultUri, 400);
                    photo = profileImage(bp);
                    imageView.setImageBitmap(bp);

                } else {
                    Toast.makeText(getApplicationContext(), "Result Uri Empty", Toast.LENGTH_LONG).show();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    //Convert and resize our image to 400dp for faster uploading our images to DB
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //Convert bitmap to bytes
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private byte[] profileImage(Bitmap b) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 30, bos);
        return bos.toByteArray();

    }

    private void addStudents() {

        String student_tble_name = "tbl_students_" + firstSubString.toLowerCase() + "_" + secondSubString.toLowerCase();

        boolean isValid = true;

        // Student name
        if (editTextName.getText().toString().isEmpty()) {
            inputLayoutName.setError("Name is Mandatory");
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

        /*//Photo
        if (photo == null) {
            Toast.makeText(getApplicationContext(), "Must Be Add A Picture !", Toast.LENGTH_LONG).show();
            isValid = false;
        }*/

        if (isValid) {

            if (db.CheckStudentExistence(student_tble_name, studentRoll)) {
                Toast.makeText(this, "Please Enter a unique roll number..", Toast.LENGTH_SHORT).show();
                inputLayoutRoll.setError("Please enter a unique roll number.");
                return;
            } else {
                try {
                    db.addStudents(new Students(photo, studentName, studentRoll, studentDob, studentDob_day_month, parentName, mobile, address, firstSubString, secondSubString, admissionDate, class_id), student_tble_name);

                    final SweetAlertDialog sd = new SweetAlertDialog(AddStudentActivity.this, SweetAlertDialog.WARNING_TYPE);
                    sd.setTitleText("SMS!");
                    sd.setContentText("Send confirmation message to: " + studentName + " ?");
                    sd.setConfirmText("Yes!");
                    sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            smsIntent.putExtra("address", mobile);
                            startActivity(smsIntent);
                            Toast.makeText(getApplicationContext(), "Student Added Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    sd.setCancelText("No!");
                    sd.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Toast.makeText(getApplicationContext(), "Student Added Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), StudentsActivity.class));
                            finish();
                        }
                    });
                    sd.show();
                } catch (SQLException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadClasses() {

        final List<Classes> fileName = new ArrayList<>();
        final List<String> data = new ArrayList<>();
        Cursor cursor = dbHelper.rawQuery("SELECT * FROM tbl_classes", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //fileName.add(cursor.getInt(0) + "-" + cursor.getString(1) + "(" + cursor.getString(2) + ")");
                fileName.add(new Classes(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)));
                data.add(cursor.getString(1).toUpperCase() + "(" + cursor.getString(2).toUpperCase() + ")");
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_list, data);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_class.setAdapter(dataAdapter);

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Classes classes = fileName.get(position);
                class_id = classes.getId();
                firstSubString = classes.getClass_name();
                secondSubString = classes.getSection();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        if(ageInt<0)
        {
            ageInt = 0;
        }
        String ageS = ageInt.toString();

        return ageS;
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
