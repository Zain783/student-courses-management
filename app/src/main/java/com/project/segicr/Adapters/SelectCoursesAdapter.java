package com.project.segicr.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.segicr.Models.CourseModel;
import com.project.segicr.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class SelectCoursesAdapter extends RecyclerView.Adapter<SelectCoursesAdapter.ViewHolder> {
    private static final String TAG = "ABDULLAH";
    Context context;
    ArrayList<CourseModel> courses;
    ArrayList<String> selectedCourses;
    public SelectCoursesAdapter(Context context) {
        this.context = context;
    }

    public void setCourses(ArrayList<CourseModel> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    public void setSelectedCourses(ArrayList<String> selectedCourses) {
        this.selectedCourses = selectedCourses;
        if (courses != null){
            ArrayList<CourseModel> newList = new ArrayList<>();
            for (String s: selectedCourses){
                for (int i=0;i<courses.size();i++){
                    if (courses.get(i).getId().equals(s)){
                        newList.add(courses.get(i));
                        break;
                    }
                }
            }
            for (CourseModel c: courses){
                if (!newList.contains(c)){
                    newList.add(c);
                }
            }

            courses = newList;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.select_course_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(courses.get(position).getCourseName());
        holder.code.setText(courses.get(position).getCourseCode());
        holder.hours.setText(courses.get(position).getCreditHours());
        if (selectedCourses != null){
            Log.d(TAG, "onBindViewHolder: SIZE: " + selectedCourses.size());

            if (selectedCourses.contains(courses.get(position).getId())){
                courses.get(position).setChecked(true);
                holder.checkBox.setChecked(true);
            }else{
                holder.checkBox.setChecked(false);
            }
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                courses.get(position).setChecked(isChecked);
            }
        });
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
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.s_c_title);
            code = itemView.findViewById(R.id.s_c_code);
            hours = itemView.findViewById(R.id.s_c_hours);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
