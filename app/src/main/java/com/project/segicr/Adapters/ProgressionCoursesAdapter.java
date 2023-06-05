package com.project.segicr.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.segicr.Models.CourseModel;
import com.project.segicr.R;

import java.util.ArrayList;

public class ProgressionCoursesAdapter extends RecyclerView.Adapter<ProgressionCoursesAdapter.ViewHolder> {
private static final String TAG = "ABDULLAH";
        Context context;
        ArrayList<CourseModel> courses;
public ProgressionCoursesAdapter(Context context) {
        this.context = context;
        }

public void setCourses(ArrayList<CourseModel> courses) {
        this.courses = courses;
        notifyDataSetChanged();
        }


@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.progression_course_item, parent, false);
        return new ViewHolder(v);
        }

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(courses.get(position).getCourseName());
        holder.code.setText(courses.get(position).getCourseCode());
        holder.hours.setText(courses.get(position).getCreditHours());

        }


public ArrayList<CourseModel> getCourses() {
        return courses;
        }

@Override
public int getItemCount() {
        return courses.size();
        }

protected static class ViewHolder extends RecyclerView.ViewHolder{
    TextView title, code, hours;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.s_c_title);
        code = itemView.findViewById(R.id.s_c_code);
        hours = itemView.findViewById(R.id.s_c_hours);
    }
}
}
