package com.project.segicr.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.segicr.Models.CourseModel;
import com.project.segicr.Models.CoursesRequestModel;
import com.project.segicr.R;
import com.project.segicr.Student.ProgressionActivity;
import com.project.segicr.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<CoursesRequestModel> requests;
    DatabaseReference root;
    ProgressionCoursesAdapter adapter;

    public RequestsAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        root = FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_COURSES_NODE);
    }

    public void setRequests(ArrayList<CoursesRequestModel> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.approval_layout, parent, false);
        return new RequestsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(requests.get(position).getUser().getName());
        holder.stdId.setText(requests.get(position).getUser().getStdId());
        holder.faculty.setText(requests.get(position).getFaculty());
        holder.specialization.setText(requests.get(position).getSpecialization());
        holder.major.setText(requests.get(position).getMajor());
        adapter = new ProgressionCoursesAdapter(context);
        adapter.setCourses(requests.get(position).getCourseArrayList());
        holder.recView.setLayoutManager(new LinearLayoutManager(context));
        holder.recView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        holder.recView.setAdapter(adapter);

        if (requests.get(position).getStatus().equals("Approval Pending")){
            if (holder.btnApprove.getVisibility() == View.GONE){
                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnApprove.setClickable(true);
            }
            if (holder.btnDeny.getVisibility() == View.GONE){
                holder.btnDeny.setVisibility(View.VISIBLE);
                holder.btnDeny.setClickable(true);
            }
        }else if(requests.get(position).getStatus().equals("Request Denied")){
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnDeny.setText("Denied");
        }else if(requests.get(position).getStatus().equals("Request Approved")){
            holder.btnDeny.setVisibility(View.GONE);
            holder.btnApprove.setText("Approved");
        }


        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.btnApprove.getText().equals("Approved")){
                    alertDialog(activity,"Request Approved", requests.get(position).getId());
                }
            }
        });
        holder.btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.btnDeny.getText().equals("Denied")){
                    alertDialog(activity,"Request Denied", requests.get(position).getId());
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    alertDialog(activity,Utils.DELETE, requests.get(position).getId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, faculty, major, specialization, stdId;
        RecyclerView recView;
        Button btnApprove, btnDeny, btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtStdName);
            faculty = itemView.findViewById(R.id.faculty);
            major = itemView.findViewById(R.id.major);
            specialization = itemView.findViewById(R.id.specialization);
            stdId = itemView.findViewById(R.id.stdID);
            recView = itemView.findViewById(R.id.recView);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnDeny = itemView.findViewById(R.id.btnDeny);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }






    private void alertDialog(Activity activity, String txt, String id){
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(txt);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(txt.equals(Utils.DELETE)){
                            deleteStudent(id);
                        }else{
                            updateStatus(txt, id);
                        }

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

    private void deleteStudent(String id) {
        FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_COURSES_NODE)
                .child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Utils.makeToast(context,"Request Deleted Successfully");
                            notifyDataSetChanged();
                        }
                    }
                });
    }

    private void updateStatus(String txt, String id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", txt);
        FirebaseDatabase.getInstance().getReference(Utils.STUDENTS_COURSES_NODE)
                .child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Successfully Update", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, Utils.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
