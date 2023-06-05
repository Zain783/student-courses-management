package com.project.segicr.Models;

import java.util.ArrayList;

public class CoursesRequestModel {
    private String id, faculty, specialization, major, status;
    private ArrayList<String> courses;
    private ArrayList<CourseModel> courseArrayList;
    private User user;

    public CoursesRequestModel(String id,String status, String faculty, String specialization, String major, ArrayList<CourseModel> courseArrayList, User user) {
        this.id = id;
        this.faculty = faculty;
        this.specialization = specialization;
        this.major = major;
        this.status = status;
        this.courseArrayList = courseArrayList;
        this.user = user;
    }

    public CoursesRequestModel(String id, String faculty, String specialization, String major, String status, ArrayList<String> courses) {
        this.id = id;
        this.faculty = faculty;
        this.specialization = specialization;
        this.major = major;
        this.status = status;
        this.courses = courses;
    }

    public CoursesRequestModel(String faculty, String specialization, String major, String status, ArrayList<String> courses) {
        this.faculty = faculty;
        this.specialization = specialization;
        this.major = major;
        this.status = status;
        this.courses = courses;
    }

    public CoursesRequestModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<String> courses) {
        this.courses = courses;
    }

    public ArrayList<CourseModel> getCourseArrayList() {
        return courseArrayList;
    }

    public void setCourseArrayList(ArrayList<CourseModel> courseArrayList) {
        this.courseArrayList = courseArrayList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
