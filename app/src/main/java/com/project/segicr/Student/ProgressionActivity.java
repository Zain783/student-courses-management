package com.project.segicr.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.segicr.Adapters.ProgressionCoursesAdapter;
import com.project.segicr.Models.CourseModel;
import com.project.segicr.Models.CoursesRequestModel;
import com.project.segicr.R;
import com.project.segicr.Utils;
import com.project.segicr.databinding.ActivityProgressionBinding;

import java.util.ArrayList;

public class ProgressionActivity extends AppCompatActivity {
ActivityProgressionBinding binding;
DatabaseReference requestRoot;
DatabaseReference coursesRoot;
String uid;
ProgressionCoursesAdapter adapter;
ArrayList<CourseModel> courses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProgressionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestRoot = FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_COURSES_NODE);
        coursesRoot = FirebaseDatabase.getInstance().getReference(Utils.COURSES_NODE);
        uid = FirebaseAuth.getInstance().getUid();
        adapter = new ProgressionCoursesAdapter(this);
        setViewsData();

    }




    private void setViewsData() {

        FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_NODE).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.txtStdName.setText(snapshot.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        requestRoot.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ArrayList<String> coursesList = new ArrayList<>();
                    String f,s,m, status;
                    for (DataSnapshot ds: snapshot.child("courses").getChildren()){
                        coursesList.add(ds.getValue(String.class));
                    }
                    f = snapshot.child("faculty").getValue(String.class);
                    s = snapshot.child("specialization").getValue(String.class);
                    m = snapshot.child("major").getValue(String.class);
                    status = snapshot.child("status").getValue(String.class);
                    binding.specialization.setText(s);
                    binding.major.setText(m);
                    binding.status.setText(status);
                    binding.faculty.setText(f);
                    coursesRoot.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                courses = new ArrayList<>();
                                for (DataSnapshot ds: snapshot.getChildren()){
                                    if (coursesList.contains(ds.getKey())){
                                        courses.add(new CourseModel(
                                                ds.child("courseCode").getValue(String.class),
                                                ds.child("courseName").getValue(String.class),
                                                ds.child("creditHours").getValue(String.class)
                                        ));
                                    }
                                }
                                adapter.setCourses(courses);
                                binding.recView.setLayoutManager(new LinearLayoutManager(ProgressionActivity.this));
                                binding.recView.addItemDecoration(new DividerItemDecoration(ProgressionActivity.this, DividerItemDecoration.VERTICAL));
                                binding.recView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}