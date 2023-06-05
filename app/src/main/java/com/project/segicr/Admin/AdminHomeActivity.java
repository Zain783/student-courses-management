package com.project.segicr.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.project.segicr.MainActivity;
import com.project.segicr.R;

public class AdminHomeActivity extends AppCompatActivity {

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        activity = this;
        Button btnFaculty = findViewById(R.id.btnFaculty);
        TextView btnLogout = findViewById(R.id.btnLogout);
        Button btnStudentRequests = findViewById(R.id.btnStudentsRequests);
        Button btnMajor = findViewById(R.id.btnMajor);
        Button btnSpecialization = findViewById(R.id.btnSpecialization);
        Button btnCourses = findViewById(R.id.btnCourses);
        Button btnAnn = findViewById(R.id.btnAnnouncements);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnStudents = findViewById(R.id.btnStudents);
        btnStudentRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, AdminMainActivity.class));
            }
        });



        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AdminHomeActivity.this)
                        .setTitle("Logout")
                        .setMessage("Do you really want to Logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                sigOut();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });



        btnFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, FacultyActivity.class));
            }
        });
        btnStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, ShowAllStudents.class));
            }
        });
        btnMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, MajorActivity.class));
            }
        });
        btnSpecialization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, SpecializationActivity.class));
            }
        });
        btnCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, CoursesActivity.class));
            }
        });
        btnAnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, ActivityAnnouncements.class));
            }
        });
    }


    private void sigOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(AdminHomeActivity.this, MainActivity.class));
        finish();
    }
}