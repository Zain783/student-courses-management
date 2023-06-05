package com.project.segicr;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {
    public static String ADMIN_EMAIL = "admin@gmail.com";
    public static String SOMETHING_WENT_WRONG = "Something went wrong, Please try again!";
    public static String SUCCESS = "Successful!";
    public static String STUDENTS_NODE = "Students";
    public static String ADMIN_TOKEN = "admin_token";
    public static String TOKEN = "token";
    public static String COURSES_NODE = "courses";
    public static String FACULTY_NODE = "faculties";
    public static String SPECIALIZATION_NODE = "specializations";
    public static String MAJOR_NODE = "majors";
    public static String ANNOUNCEMENTS_NODE = "announcements";
    public static String STUDENTS_COURSES_NODE = "students_courses";
    public static String DELETE = "Are you sure you want to delete Student Registration Request?";


        public static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 22,
                Font.BOLD, BaseColor.BLUE);
        public static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                Font.NORMAL, BaseColor.RED);
        public static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
                Font.BOLD, BaseColor.RED);
        public static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 14,
                Font.BOLD, BaseColor.BLACK);

    public static Animation scaleDown(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.scale_down);
    }
    public static Animation slideUp(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.slide_up);
    }
    public static Animation fadeIn(){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);
        return fadeIn;
    }
    public static void makeToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
