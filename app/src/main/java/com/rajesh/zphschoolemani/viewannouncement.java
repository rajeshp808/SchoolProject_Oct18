package com.rajesh.zphschoolemani;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


public class viewannouncement extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private ProgressDialog loadPD;
    private FirebaseRecyclerAdapter<Announcementdata, AnnouncementdataViewHolder> newfirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewannouncement);

        loadPD=new ProgressDialog(viewannouncement.this);
        loadPD.setTitle("Loading Announcements ");
        loadPD.setMessage("Please wait..");
        loadPD.show();
        //initialize recyclerview and FIrebase objects
        try {
            recyclerView = (RecyclerView) findViewById(R.id.id_recycle_announce);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Announcements");

            //disabling keepsync feature to enable offline save feature
            //mDatabase.keepSynced(true);
            Query newsQuery = mDatabase.orderByKey();
            FirebaseRecyclerOptions newsOptions = new FirebaseRecyclerOptions.Builder<Announcementdata>().setQuery(newsQuery, Announcementdata.class).build();

            newfirebaseAdapter = new FirebaseRecyclerAdapter<Announcementdata, AnnouncementdataViewHolder>(newsOptions) {
                @Override
                protected void onBindViewHolder(AnnouncementdataViewHolder holder, int position, Announcementdata model) {
                    holder.setTitle(model.getTitle());
                    holder.setDescription(model.getDescription());
                    holder.setImage(getApplicationContext(), model.getUrl());
                }
                @Override
                public void onDataChanged() {
                    super.onDataChanged();
                    if(loadPD.isShowing() && loadPD !=null) {

                        loadPD.dismiss();
                        Log.d("sowmuch", " progress dialog is closed");
                    } else {
                        Log.d("sowmuch", " progress dialog is not showing");
                    }
                }
                @Override
                public AnnouncementdataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.activity_announcement_card, parent, false);

                    return new AnnouncementdataViewHolder(view);
                }
            };
            Log.d("sowmore", "starting");
        }catch (Exception ex) {
            Log.d("sowmuch4", "error in onCreate"+ex.getMessage());
            ex.printStackTrace();
        }


    }

    @Override
    protected void onStart(){
        super.onStart();
        newfirebaseAdapter.startListening();
        recyclerView.setAdapter(newfirebaseAdapter);
    }
    @Override
    public void onStop() {
        super.onStop();
        newfirebaseAdapter.stopListening();

    }

    public static class AnnouncementdataViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public AnnouncementdataViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle (String title) {
            try {
                TextView tv_nc_title = (TextView) mView.findViewById(R.id.tv_announce_title);
                tv_nc_title.setText(title);

            }catch (Exception ex) {
                Log.d("sowmuch", "error in setting title"+ex.getMessage());
                ex.printStackTrace();
            }
        }
        public void setDescription (String description){
            try {
                TextView tv_nc_description = (TextView) mView.findViewById(R.id.tv_announce_description);
                tv_nc_description.setText(description);
            }catch (Exception ex) {
                Log.d("sowmuch2", "error in setting desc"+ex.getMessage());
                ex.printStackTrace();
            }
        }
        public void setImage (Context ctx, String url){
            ImageView imageView4 = (ImageView) mView.findViewById(R.id.img_announce);

            Picasso.with(ctx).load(url).into(imageView4);
        }

    }
}
