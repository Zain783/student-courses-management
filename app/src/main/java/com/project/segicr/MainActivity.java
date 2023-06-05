package com.project.segicr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.segicr.Admin.AdminHomeActivity;
import com.project.segicr.Admin.AdminMainActivity;
import com.project.segicr.Models.User;
import com.project.segicr.Student.StudentMainActivity;
import com.project.segicr.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Context  context;
    FirebaseAuth mAuth;
    DatabaseReference root;
    private boolean isAdmin = false;
    private final String do_not_have_account = "Don't have any account? Sign up";
    private final String have_account = "Already have account? Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnSubmit.setText("LOGIN");
        root = FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_NODE);
        context = this;
        mAuth = FirebaseAuth.getInstance();
        binding.splashIcon.startAnimation(Utils.slideUp(this));
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null){
                    if (Objects.equals(mAuth.getCurrentUser().getEmail(), Utils.ADMIN_EMAIL)){
                        isAdmin = true;
                    }
                    navigateUser();
                }else{
                    binding.loginLayout.setVisibility(View.VISIBLE);
                    binding.loginLayout.setAnimation(Utils.fadeIn());
                }
            }
        }, 2500);
        binding.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.btnChoose.getText().equals(do_not_have_account)){
                    binding.btnChoose.setText(have_account);
                    binding.txtTitle.setText("SIGNUP");
                    binding.btnSubmit.setText("SIGNUP");
                    binding.llSignUp.setVisibility(View.VISIBLE);
                }else{
                    binding.btnChoose.setText(do_not_have_account);
                    binding.llSignUp.setVisibility(View.GONE);
                    binding.txtTitle.setText("LOGIN");
                    binding.btnSubmit.setText("LOGIN");
                }
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.email.getText().toString().length()<1){
                    binding.email.setError("Enter Email");
                    return;
                }
                if (binding.password.getText().toString().length()<1){
                    binding.password.setError("Enter Password");
                    return;
                }

                if (binding.btnSubmit.getText().toString().equals("LOGIN")){
                    login();
                }else{
                    if (binding.email.getText().equals(Utils.ADMIN_EMAIL)){
                        Utils.makeToast(context, "This email is Already exists");
                        return;
                    }
                    if (binding.name.getText().toString().length()<1){
                        binding.name.setError("Enter Name");
                        return;
                    }
                    if (binding.stdID.getText().toString().length()<1){
                        binding.stdID.setError("Enter Student ID");
                        return;
                    }
                    signUp();
                }
            }
        });
    }

    private void navigateUser() {
        if (isAdmin){
            startActivity(new Intent(MainActivity.this, AdminHomeActivity.class));
        }else{
            startActivity(new Intent(MainActivity.this, StudentMainActivity.class));
        }
        finish();
    }

    private void signUp() {
        mAuth.createUserWithEmailAndPassword(binding.email.getText().toString().trim(),binding.password.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            mAuth.signInWithEmailAndPassword(binding.email.getText().toString().trim(), binding.password.getText().toString().trim())
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            User user = new User(binding.name.getText().toString(), binding.stdID.getText().toString(),
                                                    binding.email.getText().toString().trim(), binding.password.getText().toString().trim()
                                                    , mAuth.getUid());
                                            root.child(Objects.requireNonNull(mAuth.getUid())).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        navigateUser();
                                                    }else{
                                                        Objects.requireNonNull(mAuth.getCurrentUser()).delete();
                                                        Toast.makeText(MainActivity.this, Utils.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                            
                        }else{
                            Utils.makeToast(context, "SIGNUP: "+Utils.SOMETHING_WENT_WRONG);
                        }
                    }
                });
    }

    private void login() {
        mAuth.signInWithEmailAndPassword(binding.email.getText().toString().trim(),binding.password.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            if (Objects.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), Utils.ADMIN_EMAIL)){
                                isAdmin = true;
                            }
                            navigateUser();
                        }else{
                            Utils.makeToast(context, "LOGIN: "+Utils.SOMETHING_WENT_WRONG + task.getException());
                        }
                    }
                });
    }
}