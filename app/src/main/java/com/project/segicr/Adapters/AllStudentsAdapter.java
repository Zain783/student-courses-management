package com.project.segicr.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.project.segicr.MainActivity;
import com.project.segicr.Models.User;
import com.project.segicr.R;

import java.util.ArrayList;
import java.util.Objects;

public class AllStudentsAdapter extends RecyclerView.Adapter<AllStudentsAdapter.ViewHolder> {
    ArrayList<User> students;
    Context context;

    public AllStudentsAdapter(Context context) {
        this.context = context;
    }

    public void setStudents(ArrayList<User> students) {
        this.students = students;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(students.get(position).getName());
        holder.id.setText("ID: "+students.get(position).getStdId());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Student")
                        .setMessage("Do you really want to delete " + students.get(position).getName() + "'s account?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteUserAccount(students.get(position).getEmail(), students.get(position).getPassword(), students.get(position).getUid());
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void deleteUserAccount(String email, String password, String uid) {
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FirebaseAuth.getInstance().signInWithEmailAndPassword("admin@gmail.com", "12345678")
                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    FirebaseDatabase.getInstance().getReference("Students")
                                                            .child(uid).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(context, "Student deleted successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, id;
        ImageView btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            id = itemView.findViewById(R.id.txtStdId);
            btnDelete = itemView.findViewById(R.id.btnDeleteStd);
        }
    }
}
