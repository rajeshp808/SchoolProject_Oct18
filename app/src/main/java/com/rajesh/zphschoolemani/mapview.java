package com.rajesh.zphschoolemani;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

public class mapview extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap mMap;
    protected Bitmap icon;
    private DatabaseReference refDatabase;
    private ProgressDialog pd;
    private  String phone_key;
    private String imageIcon_URL;
    private final int MAX_IMAGE_SIZE=120;
    private Marker m1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        refDatabase = FirebaseDatabase.getInstance().getReference().child("alumn_list");

        Intent intent = getIntent();
        phone_key = intent.getStringExtra("phone_key");


        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //String url=  dataSnapshot.toString();
                //url = url.replaceAll("^\"|\"$", "");
                //Toast.makeText(getApplicationContext(), "url value"+url, Toast.LENGTH_LONG).show();
                //
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    if ((singleSnapshot.getKey()).equals(phone_key)) {
                        // Toast.makeText(getApplicationContext(), "user value 666 found"+singleSnapshot.toString(), Toast.LENGTH_LONG).show();
                        String profile_pic_url = (String) singleSnapshot.child("profpicurl").getValue();
                        profile_pic_url = profile_pic_url.replaceAll("^\"|\"$", "");
                        Log.d("Debug", "Sowmuch_profile_pic_url" + profile_pic_url);
                        imageIcon_URL=profile_pic_url;



                        Log.e("Debug", "Sowmuch_actual url is" + profile_pic_url);
                        //imgview_profpic.setImageBitmap(getBitmapFromURL(profile_pic_url));
                    }

                    //Log.e("Debug", "Sowmuch_alumnhome_onDataChange= " + user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Debug", "Sowmuch_alumnhome_onCancelled", databaseError.toException());
            }
        });

    } catch (Exception ex){
            Log.e("Debug", "Sowmuch_error db"+ ex.getMessage());
    }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{


        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(12.899051, 77.657961);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Rajesh Perakalapudi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 5.0f ) );
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setBuildingsEnabled(true);
        /*m1 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(16.3258994,80.6532196))
                .anchor(0.5f, 0.5f)
                .title("Rajesh P")
                .snippet("lives in Bangalore")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.greendots)));*/
                //.icon(BitmapDescriptorFactory.fromBitmap(icon)));

        } catch (Exception ex){
            Log.e("Debug", "Sowmuch_error onMapReady"+ ex.getMessage());
        }
        Log.d("Debug", "sowmuch_onMapReady: marker code rannn ");
        mMap.setOnMapLoadedCallback(this);
    }


//Bitmap icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.app_logo);
    /**
     * This class used to reduced size of image
     * @param realImage
     * @return
     */
    protected Bitmap adjustImage(Bitmap realImage,float maxImageSize,
                                 boolean filter) {
        Log.d("Debug", "sowmuch-adjustImage: realImage.getWidth()"+realImage.getWidth());
        Log.d("Debug", "sowmuch-adjustImage:  realImage.getHeight()"+ realImage.getHeight());
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,height, filter);
        return newBitmap;
    }

    @Override
    public void onMapLoaded() {
        Log.d("Debug", "sowmuch-onMapLoaded: maploaded , doing async task start ");
       AddMarkers addMarkersTask = new AddMarkers(imageIcon_URL);
        addMarkersTask.execute();
        try {
            Bitmap asyncBitmap=addMarkersTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("Debug", "sowmuch-onMapLoaded: async task completed ");
    }






public class AddMarkers extends AsyncTask<String, Integer, Bitmap> {
    private String imgURL;

    public AddMarkers(String imgUrl) {
        this.imgURL = imgUrl;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mapview.this);
        pd.setTitle("Loading Alumni Icons");
        pd.setMessage("Please wait..");
        pd.show();
        Log.d("Debug", "sowmuch-onPreExecute: progresss starts");
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        URL url;
        try {
            url = new URL(imgURL);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());

        }  catch (Exception e) {
            Log.d("Debug", "sowmuch-doInBackground: error "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        pd.setMessage("Please wait for "+values[0]+ " seconds");

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(pd!=null && pd.isShowing())
            pd.dismiss();
        if(bitmap!=null ){
            super.onPostExecute(bitmap);
            icon=bitmap;
            Log.d("Debug", "sowmuch-onPostExecute: before adjust image  "+icon.getByteCount());
            icon=adjustImage(icon,MAX_IMAGE_SIZE,true);
            Log.d("Debug", "sowmuch-onPostExecute: after adjust image "+icon.getByteCount());
            try {
             m1 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(16.3258994,80.6532196))
                .anchor(0.5f, 1)
                .title("Rajesh P")
                .snippet("lives in Bangalore")
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .flat(true));


            } catch (Exception exp) {
                Log.d("Debug", "sowmuch-onPostExecute: setting marker "+exp.getMessage());
            }
        } else {
            Log.d("Debug", "sowmuch-onPostExecute: bitmap is null ");
        }
    }
}

}

