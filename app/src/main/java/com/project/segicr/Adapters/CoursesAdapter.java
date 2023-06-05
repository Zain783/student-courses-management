package com.project.segicr.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.segicr.Models.CourseModel;
import com.project.segicr.R;
import com.project.segicr.Utils;

import java.util.ArrayList;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {
    Context context;
    ArrayList<CourseModel> courses;
    Dialog dialog;
    Activity activity;
    DatabaseReference root;

    public CoursesAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        root = FirebaseDatabase.getInstance().getReference(Utils.COURSES_NODE);
    }

    public void setCourses(ArrayList<CourseModel> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(courses.get(position).getCourseName());
        holder.code.setText(courses.get(position).getCourseCode());
        holder.hours.setText(courses.get(position).getCreditHours());
        holder.checkBox.setChecked(courses.get(position).isChecked());


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCheckedState(courses.get(position).getId(), !courses.get(position).isChecked());
            }
        });



        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog(activity, courses.get(position).getId());
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(activity, courses.get(position).getId(),
                        courses.get(position).getCourseCode(),
                        courses.get(position).getCourseName(),
                        courses.get(position).getCreditHours()
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, code, hours, edit, delete;
        CheckBox checkBox;
         public ViewHolder(@NonNull View itemView) {
             super(itemView);
             title = itemView.findViewById(R.id.title);
             code = itemView.findViewById(R.id.code);
             hours = itemView.findViewById(R.id.hours);
             edit = itemView.findViewById(R.id.edit);
             delete = itemView.findViewById(R.id.delete);
             checkBox = itemView.findViewById(R.id.checkBox);
         }
     }

     private void changeCheckedState(String courseId, boolean checkValue){
        FirebaseDatabase.getInstance().getReference("courses").child(courseId)
                .child("checked").setValue(checkValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (checkValue){
                            Toast.makeText(context, "Course activated", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Course deactivated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
     }

    private void deleteItem(String txt){
        root.child(txt).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDialog(Activity activity, String updatingId, String c, String t, String h){
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
        txtHours.setText(h);
        txtCode.setText(c);
        txtTitle.setText(t);
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
                CourseModel model = new CourseModel(code, title,hours, true);
                root.child(updatingId).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
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
