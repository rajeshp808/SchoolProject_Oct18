package com.rajesh.zphschoolemani;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class viewnews extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnews);
        //initialize recyclerview and FIrebase objects
        recyclerView = (RecyclerView)findViewById(R.id.id_recycle_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("News");
        mDatabase.keepSynced(true);



    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<NewsData,NewsDataViewHolder> adapter = new FirebaseRecyclerAdapter<NewsData, NewsDataViewHolder>
                (NewsData.class,R.layout.activity_newscard,NewsDataViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(NewsDataViewHolder viewHolder, NewsData model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImageUrl());
            }
        };
        recyclerView.setAdapter(adapter);
    }

   public static class NewsDataViewHolder extends RecyclerView.ViewHolder {
        View mView;

       public NewsDataViewHolder(@NonNull View itemView) {
           super(itemView);
           mView=itemView;
       }
       public void setTitle(String News_Title) {
           TextView tv_nc_title=(TextView)mView.findViewById(R.id.tv_nc_title);
           tv_nc_title.setText(News_Title);
       }
       public void setDescription(String News_Description) {
           TextView tv_nc_description=(TextView)mView.findViewById(R.id.tv_nc_description);
           tv_nc_description.setText(News_Description);
       }
       public void setImage(Context ctx, String File_URL) {
           ImageView imageView4=(ImageView)mView.findViewById(R.id.imageView4);
           Picasso.with(ctx).load(File_URL).into(imageView4);
       }
   }
}
