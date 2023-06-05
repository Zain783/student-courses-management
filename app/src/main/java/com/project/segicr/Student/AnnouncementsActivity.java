package com.project.segicr.Student;

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
import com.project.segicr.Adapters.AnnouncementsAdapter;
import com.project.segicr.Admin.ActivityAnnouncements;
import com.project.segicr.Models.Announcement;
import com.project.segicr.R;
import com.project.segicr.Utils;
import com.project.segicr.databinding.ActivityAnnouncements2Binding;
import com.project.segicr.databinding.ActivityAnnouncementsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AnnouncementsActivity extends AppCompatActivity {
    ActivityAnnouncements2Binding binding;
    ArrayList<Announcement> announcements;
    AnnouncementsAdapter adapter;
    DatabaseReference root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnnouncements2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = FirebaseDatabase.getInstance().getReference(Utils.ANNOUNCEMENTS_NODE);
        adapter = new AnnouncementsAdapter(this, this, "Std");
        FirebaseDatabase.getInstance().getReference().child(Utils.ANNOUNCEMENTS_NODE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            announcements = new ArrayList<>();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                announcements.add(new Announcement(ds.getKey(),ds.getValue(String.class)));
                            }
                            binding.announcementsRecView.setLayoutManager(new LinearLayoutManager(AnnouncementsActivity.this));
                            adapter.setAnnouncements(announcements);
                            binding.announcementsRecView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}