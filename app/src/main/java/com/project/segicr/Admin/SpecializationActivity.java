package com.project.segicr.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.project.segicr.R;
import com.project.segicr.Utils;
import com.project.segicr.databinding.ActivityMajorBinding;
import com.project.segicr.databinding.ActivitySpecializationBinding;

import java.util.ArrayList;

public class SpecializationActivity extends AppCompatActivity {
    ActivitySpecializationBinding binding;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    DatabaseReference root;
    Dialog dialog;
    private static final String TAG = "ABDULLAH";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpecializationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = FirebaseDatabase.getInstance().getReference(Utils.SPECIALIZATION_NODE);
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if (snapshot.getChildrenCount()>0){
                        listItems = new ArrayList<>();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            listItems.add(ds.getValue(String.class));
                            Log.d(TAG, "onDataChange: ITEM FOUNT: " +ds.getValue(String.class) );
                        }
                        adapter=new ArrayAdapter<String>(SpecializationActivity.this,android.R.layout.simple_list_item_1,listItems);
                        binding.list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String txt = binding.list.getItemAtPosition((int)id).toString();
                alertDialog( SpecializationActivity.this, txt);
            }
        });


        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog( SpecializationActivity.this);
            }
        });

    }
    private void showDialog(Activity activity){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_new_major);
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
                root.child(text).setValue(text).addOnCompleteListener(new OnCompleteListener<Void>() {
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
    private void deleteItem(String txt){
        root.child(txt).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SpecializationActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    if (adapter != null){
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    private void alertDialog(Activity activity, String txt){
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure you want to delete this item?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(txt);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}