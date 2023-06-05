package com.project.segicr.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.segicr.Adapters.CoursesAdapter;
import com.project.segicr.Models.CourseModel;
import com.project.segicr.R;
import com.project.segicr.Utils;
import com.project.segicr.databinding.ActivityCoursesBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CoursesActivity extends AppCompatActivity {
    DatabaseReference root;
    ActivityCoursesBinding binding;
    ArrayList<CourseModel> listItems;
    CoursesAdapter adapter;
    Dialog dialog;
    private static final String TAG = "ABDULLAH";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoursesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = FirebaseDatabase.getInstance().getReference(Utils.COURSES_NODE);
        adapter = new CoursesAdapter(this, this);
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if (snapshot.getChildrenCount()>0){
                        listItems = new ArrayList<>();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            listItems.add(new CourseModel(
                                    ds.getKey(),
                                    ds.child("courseCode").getValue(String.class),
                                    ds.child("courseName").getValue(String.class),
                                    ds.child("creditHours").getValue(String.class),
                                    Boolean.TRUE.equals(ds.child("checked").getValue(Boolean.class))
                            ));
                        }
                        adapter.setCourses(listItems);
                      binding.list.setLayoutManager(new LinearLayoutManager(CoursesActivity.this));
                      binding.list.setAdapter(adapter);
                      adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(CoursesActivity.this);
            }
        });
    }

    private void showDialog(Activity activity){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_course);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        EditText txtTitle = dialog.findViewById(R.id.title);
        EditText txtCode = dialog.findViewById(R.id.code);
        EditText txtHours = dialog.findViewById(R.id.hours);
        Button btnSubmit = dialog.findViewById(R.id.btnAdd);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = txtTitle.getText().toString();
                String code = txtCode.getText().toString();
                String hours = txtHours.getText().toString();
                if (title.isEmpty()){
                    txtTitle.setError("Please fill filed!");
                    return;
                }
                if (code.isEmpty()){
                    txtCode.setError("Please fill filed!");
                    return;
                }
                if (hours.isEmpty()){
                    txtHours.setError("Please fill filed!");
                    return;
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssa").format(Calendar.getInstance().getTime());
                CourseModel model = new CourseModel(code, title,hours, true);
                root.child(timeStamp).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                            Toast.makeText(activity, Utils.SUCCESS, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity, Utils.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}