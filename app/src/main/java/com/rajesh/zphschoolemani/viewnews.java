package com.rajesh.zphschoolemani;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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
        try {
            recyclerView = (RecyclerView) findViewById(R.id.id_recycle_news);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("News");
            mDatabase.keepSynced(true);
            Log.d("sowmore", "starting");
        }catch (Exception ex) {
            Log.d("sowmuch4", "error in onCreate"+ex.getMessage());
            ex.printStackTrace();
        }


    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Newsdata,NewsdataViewHolder> news_adapter = new FirebaseRecyclerAdapter<Newsdata, NewsdataViewHolder>
                (Newsdata.class,R.layout.activity_newscard,NewsdataViewHolder.class,mDatabase) {

            @Override
            protected void populateViewHolder(NewsdataViewHolder viewHolder, Newsdata model, int position) {
                try {
                    viewHolder.setTitle(model.getNews_Title());
                    viewHolder.setDescription(model.getNews_Description());

                    viewHolder.setImage(getApplicationContext(), model.getFile_URL());
                } catch (Exception ex) {
                    Log.d("sowmuch3", "error in populateViewHolder"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };
        recyclerView.setAdapter(news_adapter);
    }

   public static class NewsdataViewHolder extends RecyclerView.ViewHolder {
       View mView;

       public NewsdataViewHolder( View itemView) {
           super(itemView);
           mView = itemView;
     }

           public void setTitle (String News_Title) {
               try {
                   TextView tv_nc_title = (TextView) mView.findViewById(R.id.tv_nc_title);
                   tv_nc_title.setText(News_Title);

               }catch (Exception ex) {
                   Log.d("sowmuch", "error in setting title"+ex.getMessage());
                   ex.printStackTrace();
               }
           }
           public void setDescription (String News_Description){
           try {
               TextView tv_nc_description = (TextView) mView.findViewById(R.id.tv_nc_description);
               tv_nc_description.setText(News_Description);
           }catch (Exception ex) {
               Log.d("sowmuch2", "error in setting desc"+ex.getMessage());
               ex.printStackTrace();
           }
       }
           public void setImage (Context ctx, String File_URL){
           ImageView imageView4 = (ImageView) mView.findViewById(R.id.imageView4);

           Picasso.with(ctx).load(File_URL).into(imageView4);
       }

   }
}
