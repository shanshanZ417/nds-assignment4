package com.example.assignment4;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by misaki on 12/5/17.
 * INTRO:
 * retrieve photo information based on given authentication, store the qualified photo into ArrayList and pass it to adapter
 */


public class ViewActivity extends AppCompatActivity {

    private DatabaseReference pubDatabaseRef;
    private DatabaseReference ownDatabaseRef;
    private List<Photo> imgList;
    private ListView lv;
    private ImageListAdapter adapter;
    private ProgressDialog progressDialog;
    private Bundle bundle;
    private String views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        imgList = new ArrayList<>();
        lv = findViewById(R.id.ListViewImage);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Waiting for the image loading....");
        progressDialog.show();

        pubDatabaseRef = FirebaseDatabase.getInstance().getReference("publicPhoto");
        bundle = getIntent().getExtras();
        views =  bundle.getString("view");
        if(bundle.getBoolean("isAuthen")) {
            ownDatabaseRef = FirebaseDatabase.getInstance().getReference("privateUser");
            ownDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    String userId = bundle.getString("userID");
                    if (userId != null){
                        DataSnapshot dataSnapshots = dataSnapshot.child(userId);
                        for (DataSnapshot snapshot : dataSnapshots.getChildren()) {
                            Photo img = snapshot.getValue(Photo.class);
                            imgList.add(img);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });
        }

        pubDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Photo img = snapshot.getValue(Photo.class);
                    imgList.add(img);
                }
                //Init adapter
                adapter = new ImageListAdapter(ViewActivity.this, R.layout.image_item, imgList, views);
                //Set adapter for listview
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }
}
