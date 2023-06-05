package com.project.segicr.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.segicr.Admin.FacultyActivity;
import com.project.segicr.Models.Announcement;
import com.project.segicr.R;
import com.project.segicr.Utils;

import java.util.ArrayList;

public class AnnouncementsAdapter extends RecyclerView.Adapter<AnnouncementsAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<Announcement> announcements;
    DatabaseReference root;
    String from;

    public AnnouncementsAdapter(Context context, Activity activity, String from) {
        this.context = context;
        this.activity = activity;
        this.from = from;
        root = FirebaseDatabase.getInstance().getReference().child(Utils.ANNOUNCEMENTS_NODE);
    }

    public void setAnnouncements(ArrayList<Announcement> announcements) {
        this.announcements = announcements;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public AnnouncementsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_announcements, parent, false);
        return new AnnouncementsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(announcements.get(position).getText());
        if (!from.equals("Admin")){
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equals("Admin")){
                    alertDialog(activity, announcements.get(position).getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        MaterialCardView cardView;
        ImageView btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtAnnouncements);
            cardView = itemView.findViewById(R.id.container);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
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
