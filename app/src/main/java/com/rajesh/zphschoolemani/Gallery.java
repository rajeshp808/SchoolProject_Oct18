package com.rajesh.zphschoolemani;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class Gallery extends AppCompatActivity {
    private ImageView image1;
    private int[] imageArray;
    private int currentIndex;
    private int startIndex;
    private int endIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

            image1= (ImageView)findViewById(R.id.gallery_image);
         imageArray = new int[8];

            imageArray[0] = R.drawable.maingate2;
            imageArray[1] = R.drawable.hd_maingate_entrance;
            imageArray[2] = R.drawable.mainbuild;
            imageArray[3] = R.drawable.hd_assembly;
            imageArray[4] = R.drawable.students_drill;
            imageArray[5] = R.drawable.play_ground;
            imageArray[6] = R.drawable.play_ground2;
            imageArray[7] = R.drawable.hd_maingate;
            startIndex = 0;
            endIndex = 7;
            currentIndex=0;
            nextImage();


        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), "swipe on your screen for the next Image", Toast.LENGTH_LONG).show();


    }

    public void nextImage(){
        image1.setImageResource(imageArray[currentIndex]);
        Animation rotateimage = AnimationUtils.loadAnimation(this, R.anim.custom_anim);
        image1.startAnimation(rotateimage);
        currentIndex++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentIndex>endIndex){
                    currentIndex--;
                    previousImage();
                }else{
                    nextImage();
                }

            }
        },1000); // here 1000(1 second) interval to change from current  to next image

    }
    public void previousImage(){
        image1.setImageResource(imageArray[currentIndex]);
        Animation rotateimage = AnimationUtils.loadAnimation(this, R.anim.custom_anim);
        image1.startAnimation(rotateimage);
        currentIndex--;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentIndex<startIndex){
                    currentIndex++;
                    nextImage();
                }else{
                    previousImage(); // here 1000(1 second) interval to change from current  to previous image
                }
            }
        },1000);

    }
}
