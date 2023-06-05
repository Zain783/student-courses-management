package com.project.segicr.Models;

public class CourseModel {
    private String id, courseCode, courseName, creditHours;
    private boolean isChecked = false;

    public CourseModel(String id, String courseCode, String courseName, String creditHours, boolean isChecked) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditHours = creditHours;
        this.isChecked = isChecked;
    }

    public CourseModel(String courseCode, String courseName, String creditHours, boolean isChecked) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditHours = creditHours;
        this.isChecked = isChecked;
    }

    public CourseModel(String courseCode, String courseName, String creditHours) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditHours = creditHours;
    }

    public CourseModel(String id, String courseCode, String courseName, String creditHours) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditHours = creditHours;
    }

    public CourseModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(String creditHours) {
        this.creditHours = creditHours;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
