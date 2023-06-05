package com.project.segicr.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.segicr.Adapters.AllStudentsAdapter;
import com.project.segicr.Models.CourseModel;
import com.project.segicr.Models.User;
import com.project.segicr.R;
import com.project.segicr.Utils;
import com.project.segicr.databinding.ActivityShowAllStudentsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ShowAllStudents extends AppCompatActivity {
    AllStudentsAdapter adapter;
    Dialog dialog;
    ActivityShowAllStudentsBinding binding;
    ArrayList<User> students;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowAllStudentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = new AllStudentsAdapter(this);
        FirebaseDatabase.getInstance().getReference("Students")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            students = new ArrayList<>();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                students.add(ds.getValue(User.class));
                            }
                            binding.recView.setLayoutManager(new LinearLayoutManager(ShowAllStudents.this));
                            adapter.setStudents(students);
                            binding.recView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(ShowAllStudents.this);
            }
        });

        binding.searchStd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterStudents(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void filterStudents(String text){
        if (text.isEmpty()){
            adapter.setStudents(students);
        }else{
            ArrayList<User> newStdList = new ArrayList<>();
            for (User u: students){
                if (u.getName().toLowerCase().contains(text.toLowerCase())){
                    newStdList.add(u);
                }
            }
            adapter.setStudents(newStdList);
            adapter.notifyDataSetChanged();
        }
    }


    private void showDialog(Activity activity){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_student);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        EditText name = dialog.findViewById(R.id.name);
        EditText stdID = dialog.findViewById(R.id.stdID);
        EditText email = dialog.findViewById(R.id.email);
        EditText password = dialog.findViewById(R.id.password);
        Button btnSubmit = dialog.findViewById(R.id.btnAdd);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = name.getText().toString();
                String i = stdID.getText().toString();
                String e = email.getText().toString();
                String p = password.getText().toString();
                if (n.isEmpty()){
                    name.setError("Please fill filed!");
                    return;
                }
                if (i.isEmpty()){
                    stdID.setError("Please fill filed!");
                    return;
                }
                if (e.isEmpty()){
                    email.setError("Please fill filed!");
                    return;
                }
                if (p.isEmpty()){
                    password.setError("Please fill filed!");
                    return;
                }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(e,p)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                                    User user = new User(
                                                      n, i, e, p, FirebaseAuth.getInstance().getUid()
                                                    );
                                                    FirebaseDatabase.getInstance().getReference("Students")
                                                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                            .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    FirebaseAuth.getInstance().signInWithEmailAndPassword("admin@gmail.com", "12345678")
                                                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                                                @Override
                                                                                public void onSuccess(AuthResult authResult) {
                                                                                    Toast.makeText(ShowAllStudents.this, "Students added successfully", Toast.LENGTH_SHORT).show();
                                                                                    dialog.dismiss();
                                                                                }
                                                                            });
                                                                }
                                                            });

                                }else{
                                    Toast.makeText(activity, Utils.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }




}