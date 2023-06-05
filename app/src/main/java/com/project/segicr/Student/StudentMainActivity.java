package com.project.segicr.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.segicr.Admin.AdminMainActivity;
import com.project.segicr.MainActivity;
import com.project.segicr.R;
import com.project.segicr.Utils;
import com.project.segicr.databinding.StudentMainBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StudentMainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    StudentMainBinding binding;
    DatabaseReference requestRoot;
    boolean isAlreadyRegistered = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StudentMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        updateToken();
        binding.stdToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.logout) {
                    new AlertDialog.Builder(StudentMainActivity.this)
                            .setTitle("Logout")
                            .setMessage("Do you really want to Logout?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    sigOut();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
                if (item.getItemId() == R.id.announcements){
                    startActivity(new Intent(StudentMainActivity.this, AnnouncementsActivity.class));
                }
                return true;
            }
        });
        requestRoot = FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_COURSES_NODE);
        requestRoot.child(Objects.requireNonNull(mAuth.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    isAlreadyRegistered = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        binding.addDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAlreadyRegistered) {
                    Intent intent = new Intent(StudentMainActivity.this, RegisterCourseActivity.class);
                    intent.putExtra("status", "true");
                    startActivity(intent);
                }else{
                    Toast.makeText(StudentMainActivity.this, "You have not registered courses yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAlreadyRegistered) {
                    startActivity(new Intent(StudentMainActivity.this, ProgressionActivity.class));
                }else{
                    Toast.makeText(StudentMainActivity.this, "You have not registered courses yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        if (isAlreadyRegistered){
                            new AlertDialog.Builder(StudentMainActivity.this)
                                    .setTitle("Already Registered")
                                    .setMessage("You already registered courses. Do you want to update them?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Intent intent = new Intent(StudentMainActivity.this, RegisterCourseActivity.class);
                                            intent.putExtra("status", "true");
                                            startActivity(intent);
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();
                        }else{
                            startActivity(new Intent(StudentMainActivity.this, RegisterCourseActivity.class));
                        }
            }
        });
    }
    private void updateToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    final String token = task.getResult();
                    DatabaseReference tokenRoot = FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_NODE);
                    Map<String , Object> map = new HashMap<>();
                    map.put(Utils.TOKEN, token);
                    tokenRoot.child(Objects.requireNonNull(mAuth.getUid())).updateChildren(map);
                }
            }
        });
    }
    private void sigOut(){
        mAuth.signOut();
        startActivity(new Intent(StudentMainActivity.this, MainActivity.class));
        finish();
    }


}