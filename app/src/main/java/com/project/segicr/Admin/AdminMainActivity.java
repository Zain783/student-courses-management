package com.project.segicr.Admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.segicr.Adapters.RequestsAdapter;
import com.project.segicr.MainActivity;
import com.project.segicr.Models.CourseModel;
import com.project.segicr.Models.CoursesRequestModel;
import com.project.segicr.Models.User;
import com.project.segicr.R;
import com.project.segicr.Student.RegisterCourseActivity;
import com.project.segicr.Utils;
import com.project.segicr.databinding.AdminMainBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminMainActivity extends AppCompatActivity {
    AdminMainBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference root;
    ArrayList<String> majors;
    ArrayAdapter<String> majorAdapter;
    ArrayList<CoursesRequestModel> requests;
    RequestsAdapter adapter;
    private static final String TAG = "ABDULLAH";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance().getReference();
        updateToken();
        adapter = new RequestsAdapter(this, this);

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AdminMainActivity.this)
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


        binding.btnDeleteAllReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AdminMainActivity.this)
                        .setTitle("Delete Requests Forever")
                        .setMessage("Do you really want to Delete All Students Requests Forever?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_COURSES_NODE).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(AdminMainActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        });
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });



        loadMajors(FirebaseDatabase.getInstance().getReference().child(Utils.MAJOR_NODE));
        Query query = root.child(Utils.STUDENTS_COURSES_NODE);
        loadRequests(query);


        binding.major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String major = majorAdapter.getItem(position);
                if (!major.equals("Select Major")){
                    Query q = FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_COURSES_NODE)
                            .orderByChild("major").equalTo(major);
                    loadRequests(q);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadMajors(DatabaseReference root) {
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    majors = new ArrayList<>();
                    majors.add("Select Major");
                    for (DataSnapshot ds: snapshot.getChildren()){
                        majors.add(ds.getValue(String.class));
                    }
                    majorAdapter = new ArrayAdapter<String>(AdminMainActivity.this,   android.R.layout.simple_spinner_item, majors);
                    majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    majorAdapter.notifyDataSetChanged();

                    binding.major.setAdapter(majorAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void loadRequests(Query query) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (snap.exists()){
                    if (binding.requestsRecView.getVisibility() == View.GONE){
                        binding.requestsRecView.setVisibility(View.VISIBLE);
                    }
                    requests =  new ArrayList<>();
                    for (DataSnapshot ds: snap.getChildren()){

                        final User[] user = new User[1];
                        root.child(Utils.STUDENTS_NODE).child(Objects.requireNonNull(ds.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    user[0] = new User(
                                            snapshot.child("name").getValue(String.class),
                                            snapshot.child("stdId").getValue(String.class),
                                            snapshot.child("token").getValue(String.class)
                                    );
                                    ArrayList<String> cNamesList = new ArrayList<>();

                                    for (DataSnapshot d: ds.child("courses").getChildren()){
                                        cNamesList.add(d.getValue(String.class));
                                    }
                                    FirebaseDatabase.getInstance().getReference().child(Utils.COURSES_NODE).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                ArrayList<CourseModel> courseModelArrayList = new ArrayList<>();
                                                for (DataSnapshot s: snapshot.getChildren()){
                                                    if (cNamesList.contains(s.getKey())){
                                                        courseModelArrayList.add(new CourseModel(
                                                                s.child("courseCode").getValue(String.class),
                                                                s.child("courseName").getValue(String.class),
                                                                s.child("creditHours").getValue(String.class)
                                                        ));
                                                    }
                                                }
                                                String f,s,m,status;

                                                f = ds.child("faculty").getValue(String.class);
                                                s = ds.child("specialization").getValue(String.class);
                                                m = ds.child("major").getValue(String.class);
                                                status = ds.child("status").getValue(String.class);
                                                requests.add(new CoursesRequestModel(
                                                        ds.getKey(),status,
                                                        f,s,m,courseModelArrayList,user[0]
                                                ));
                                                Log.d(TAG, "onDataChange: " + courseModelArrayList.size());
                                                adapter.setRequests(requests);
                                                binding.requestsRecView.setLayoutManager(new LinearLayoutManager(AdminMainActivity.this));
                                                binding.requestsRecView.setAdapter(adapter);
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
                }else{
                    if (binding.requestsRecView.getVisibility() == View.VISIBLE){
                        binding.requestsRecView.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    final String token = task.getResult();
                    DatabaseReference tokenRoot = FirebaseDatabase.getInstance().getReference();
                    Map<String , Object> map = new HashMap<>();
                    map.put(Utils.ADMIN_TOKEN, token);
                    tokenRoot.updateChildren(map);
                }
            }
        });
    }

    private void sigOut(){
        mAuth.signOut();
        startActivity(new Intent(AdminMainActivity.this, MainActivity.class));
        finish();
    }


}