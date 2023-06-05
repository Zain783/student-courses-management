package com.project.segicr.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.UniversalTimeScale;
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
import com.project.segicr.Adapters.AnnouncementsAdapter;
import com.project.segicr.Models.Announcement;
import com.project.segicr.R;
import com.project.segicr.Utils;
import com.project.segicr.databinding.ActivityAnnouncementsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ActivityAnnouncements extends AppCompatActivity {
    ActivityAnnouncementsBinding binding;
    ArrayList<Announcement> announcements;
    AnnouncementsAdapter adapter;
    Dialog dialog;
    DatabaseReference root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnnouncementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = FirebaseDatabase.getInstance().getReference(Utils.ANNOUNCEMENTS_NODE);
        adapter = new AnnouncementsAdapter(this, this, "Admin");
        FirebaseDatabase.getInstance().getReference().child(Utils.ANNOUNCEMENTS_NODE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            announcements = new ArrayList<>();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                announcements.add(new Announcement(ds.getKey(),ds.getValue(String.class)));
                            }
                            binding.announcementsRecView.setLayoutManager(new LinearLayoutManager(ActivityAnnouncements.this));
                            adapter.setAnnouncements(announcements);
                            binding.announcementsRecView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(ActivityAnnouncements.this);
            }
        });
    }


    private void showDialog(Activity activity){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_announcements);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        EditText txtText = dialog.findViewById(R.id.text);
        Button btnSubmit = dialog.findViewById(R.id.btnAdd);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = txtText.getText().toString();
                if (text.isEmpty()){
                    txtText.setError("Please fill filed!");
                    return;
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssa").format(Calendar.getInstance().getTime());
                root.child(timeStamp).setValue(text).addOnCompleteListener(new OnCompleteListener<Void>() {
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