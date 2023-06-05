package com.project.segicr.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.segicr.Adapters.SelectCoursesAdapter;
import com.project.segicr.BuildConfig;
import com.project.segicr.FCM.FcmNotificationsSender;
import com.project.segicr.Models.CourseModel;
import com.project.segicr.Models.CoursesRequestModel;
import com.project.segicr.Models.User;
import com.project.segicr.R;
import com.project.segicr.Utils;
import com.project.segicr.databinding.ActivityRegisterCourseBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RegisterCourseActivity extends AppCompatActivity {
    private static final String TAG = "ABDULLAH";
    ActivityRegisterCourseBinding binding;
    DatabaseReference root;
    DatabaseReference requestRoot;
    ArrayAdapter<String> facultyAdapter;

    ArrayAdapter<String> specializationAdapter;
    ArrayList<String> faculties;

    ArrayList<String> majors;
    ArrayAdapter<String> majorAdapter;
    PdfPTable table;
    PdfPTable UserTable;
    ArrayList<String> specializations;
    ArrayList<CourseModel> courses;
    SelectCoursesAdapter coursesAdapter;
    boolean shouldUpdate = false;
    CoursesRequestModel requestModel;
    String uid;
    String adminToken = "";
    String pdfTopText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        uid = FirebaseAuth.getInstance().getUid();
        root = FirebaseDatabase.getInstance().getReference();
        requestRoot = FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_COURSES_NODE);
         coursesAdapter = new SelectCoursesAdapter(this);

         table = new PdfPTable(3);
         UserTable = new PdfPTable(2);
         table.addCell(new PdfPCell(new Phrase("Course Title")));
         table.addCell(new PdfPCell(new Phrase("Course Code")));
         table.addCell(new PdfPCell(new Phrase("Credit Hours")));

        if (!isStoragePermissionGranted()){
            finish();
        }

         DatabaseReference adminTokenRoot = FirebaseDatabase.getInstance().getReference(Utils.ADMIN_TOKEN);
         adminTokenRoot.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists()){
                     adminToken = snapshot.getValue(String.class);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

        Intent intent = getIntent();
        if (intent != null) {
            String requestStatus = intent.getStringExtra("status");
            if (requestStatus != null && requestStatus.length()>0){
                shouldUpdate = true;
            }
        }


        setViewsData();
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RegisterCourseActivity.this)
                        .setTitle("Register")
                        .setMessage("Do you double checked your academic details?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                submit();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void submit() {
        int totalCreditHours = 0;
        if (coursesAdapter != null){
            if (coursesAdapter.getCourses() != null && coursesAdapter.getCourses().size()>0){
                ArrayList<String> toSubmitCourses = new ArrayList<>();
                ArrayList<CourseModel> pdfCourses = new ArrayList<>();
                for (CourseModel c: coursesAdapter.getCourses()) {
                    if (c.isChecked()){
                        totalCreditHours = totalCreditHours + Integer.parseInt(c.getCreditHours());
                        toSubmitCourses.add(c.getId());
                        pdfCourses.add(c);
                    }
                }
                if (totalCreditHours > 20){
                    Toast.makeText(this, "Your credit hours exceed to 20, Credit hours must less or equal to 20", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (toSubmitCourses.size()==0){
                    Toast.makeText(this, "Kindly Choose at least one course", Toast.LENGTH_SHORT).show();
                }else{
                    uploadRequest(toSubmitCourses, pdfCourses);
                }
            }
        }
    }

    private void uploadRequest(ArrayList<String> toSubmitCourses, ArrayList<CourseModel> pdfCourses) {
        String f, m, s;
        f = binding.faculty.getSelectedItem().toString();
        m = binding.major.getSelectedItem().toString();
        s = binding.specialization.getSelectedItem().toString();
        CoursesRequestModel model = new CoursesRequestModel(f,s,m,"Approval Pending",toSubmitCourses);
        requestRoot.child(Objects.requireNonNull(uid)).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_NODE).child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                                            if (snapshot.exists()){
                                                User user = new User(
                                                        snapshot.child("name").getValue(String.class),
                                                        snapshot.child("stdId").getValue(String.class)
                                                        );


                                                UserTable.addCell(new PdfPCell(new Phrase("Name")));
                                                UserTable.addCell(new PdfPCell(new Phrase(user.getName())));

                                                UserTable.addCell(new PdfPCell(new Phrase("Student ID")));
                                                UserTable.addCell(new PdfPCell(new Phrase(user.getStdId())));

                                                UserTable.addCell(new PdfPCell(new Phrase("Major")));
                                                UserTable.addCell(new PdfPCell(new Phrase(m)));

                                                UserTable.addCell(new PdfPCell(new Phrase("Faculty")));
                                                UserTable.addCell(new PdfPCell(new Phrase(f)));

                                                UserTable.addCell(new PdfPCell(new Phrase("Specialization")));
                                                UserTable.addCell(new PdfPCell(new Phrase(s)));


                                                String date = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault()).format(new Date());
                                                UserTable.addCell(new PdfPCell(new Phrase("Submission Date")));
                                                UserTable.addCell(new PdfPCell(new Phrase(date)));

                                                for (CourseModel c: pdfCourses){
                                                    table.addCell(new PdfPCell(new Phrase(c.getCourseName())));
                                                    table.addCell(new PdfPCell(new Phrase(c.getCourseCode())));
                                                    table.addCell(new PdfPCell(new Phrase(c.getCreditHours())));
                                                }
                                                try {
                                                    createPDF("/_"+user.getStdId());
                                                } catch (DocumentException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            Toast.makeText(RegisterCourseActivity.this, Utils.SUCCESS, Toast.LENGTH_SHORT).show();
                                            if (!shouldUpdate){
                                                FcmNotificationsSender notification = new FcmNotificationsSender(
                                                        adminToken,"New Registration", m + ", " + f + ", " + s + "\nRequested for " + toSubmitCourses.size() + " courses",RegisterCourseActivity.this, RegisterCourseActivity.this
                                                );
                                                notification.SendNotifications();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });



                }else{
                    Toast.makeText(RegisterCourseActivity.this, Utils.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setViewsData() {

        root.child(Utils.FACULTY_NODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    faculties = new ArrayList<>();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        faculties.add(ds.getValue(String.class));
                    }
                    facultyAdapter = new ArrayAdapter<String>(RegisterCourseActivity.this,   android.R.layout.simple_spinner_item, faculties);
                    facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    facultyAdapter.notifyDataSetChanged();
                    binding.faculty.setAdapter(facultyAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
        root.child(Utils.MAJOR_NODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    majors = new ArrayList<>();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        majors.add(ds.getValue(String.class));
                    }
                    majorAdapter = new ArrayAdapter<String>(RegisterCourseActivity.this,   android.R.layout.simple_spinner_item, majors);
                    majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    majorAdapter.notifyDataSetChanged();

                    binding.major.setAdapter(majorAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
        root.child(Utils.SPECIALIZATION_NODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    specializations = new ArrayList<>();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        specializations.add(ds.getValue(String.class));
                    }
                    specializationAdapter = new ArrayAdapter<String>(RegisterCourseActivity.this,   android.R.layout.simple_spinner_item, specializations);
                    specializationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    specializationAdapter.notifyDataSetChanged();

                    if (shouldUpdate){
                        binding.btnSubmit.setText("Update");
                        requestRoot.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    ArrayList<String> coursesList = new ArrayList<>();
                                    String f,s,m, status;
                                    for (DataSnapshot ds: snapshot.child("courses").getChildren()){
                                        coursesList.add(ds.getValue(String.class));
                                    }
                                    coursesAdapter.setSelectedCourses(coursesList);
                                    f = snapshot.child("faculty").getValue(String.class);
                                    s = snapshot.child("specialization").getValue(String.class);
                                    m = snapshot.child("major").getValue(String.class);
                                    status = snapshot.child("status").getValue(String.class);
                                    binding.stdToolbar.setTitle(status);
                                    requestModel = new CoursesRequestModel(f,s,m,status,coursesList);
                                    int index = faculties.indexOf(requestModel.getFaculty());
                                    binding.faculty.setSelection(index);
                                    int index1 = majors.indexOf(requestModel.getMajor());
                                    binding.major.setSelection(index1);
                                    int index2 = specializations.indexOf(requestModel.getSpecialization());
                                    binding.specialization.setSelection(index2);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    binding.specialization.setAdapter(specializationAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});

        root.child(Utils.COURSES_NODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    courses = new ArrayList<>();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if (Boolean.TRUE.equals(ds.child("checked").getValue(Boolean.class))){
                            courses.add(new CourseModel(
                                    ds.getKey(),
                                    ds.child("courseCode").getValue(String.class),
                                    ds.child("courseName").getValue(String.class),
                                    ds.child("creditHours").getValue(String.class)
                            ));
                        }
                    }

                    if (shouldUpdate){
                        requestRoot.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    ArrayList<String> coursesList = new ArrayList<>();
                                    for (DataSnapshot ds: snapshot.child("courses").getChildren()){
                                        coursesList.add(ds.getValue(String.class));
                                        Log.d(TAG, "onDataChange: " + ds.getValue(String.class));
                                    }
                                    Log.d(TAG, "onDataChange: SIZE OF ARRAY LIST: " + coursesList.size());
                                    coursesAdapter.setCourses(courses);
                                    coursesAdapter.setSelectedCourses(coursesList);
                                    binding.recView.setLayoutManager(new LinearLayoutManager(RegisterCourseActivity.this));
                                    binding.recView.setAdapter(coursesAdapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }else{
                        coursesAdapter.setCourses(courses);
                        binding.recView.setLayoutManager(new LinearLayoutManager(RegisterCourseActivity.this));
                        binding.recView.setAdapter(coursesAdapter);
                    }



                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }


    private void createPDF(String filename) throws IOException, DocumentException {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File file = new File(path + filename + ".pdf");
        FileOutputStream outputStream = new FileOutputStream(file);
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        Uri pdfUri = FileProvider.getUriForFile(RegisterCourseActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", file);

        document.open();


        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image img = null;
        byte[] byteArray = stream.toByteArray();
        try {
            img = Image.getInstance(byteArray);

            float scalar = 15;

            img.scalePercent(scalar);
            img.setAlignment(Element.ALIGN_CENTER);

        } catch (BadElementException | IOException e) {
            e.printStackTrace();
        }

        document.add(img);

        Paragraph p1 = new Paragraph("\nREGISTRATION SUBMISSION\n\n", Utils.catFont);
        p1.setAlignment(Element.ALIGN_CENTER);

        Paragraph p = new Paragraph("\n", Utils.catFont);



        Paragraph p3 = new Paragraph("REGISTERED COURSES\n\n", Utils.subFont);
        p3.setAlignment(Element.ALIGN_CENTER);


        document.add(p1);
        document.add(UserTable);
        document.add(p);
        document.add(p3);
        document.add(table);


        document.close();

        new AlertDialog.Builder(RegisterCourseActivity.this)
                .setTitle("Open PDF")
                .setMessage("Your file saved to 'Emulated/Documents"+filename+".pdf' Directory.\nDo you wanna preview your Registration Submission PDF?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(RegisterCourseActivity.this, "File saved to your documents directory.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(pdfUri, "application/pdf");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);

                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();


    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
}