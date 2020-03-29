package com.example.betaversionyam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.betaversionyam.FBref.refDis;
import static com.example.betaversionyam.FBref.refUsers;

public class WorkerActivity extends AppCompatActivity {

    ValueEventListener usersListener, disListener;
    ListView listView;
    ArrayList<Distribution> distributionArrayList = new ArrayList<>();
    ArrayList<String> stringsArrayList = new ArrayList<>();
    Distribution distribution;
    ArrayAdapter adp;
    String phoneOfUser, nameOfDistribution;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Users currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
        listView = findViewById(R.id.listView1);

        FirebaseUser user = mAuth.getCurrentUser();
        phoneOfUser = user.getPhoneNumber();

        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (phoneOfUser.equals(data.getValue(Users.class).getPhone())) {
                        currentUser = data.getValue(Users.class);
                    }
                }
                disListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        distributionArrayList.clear();
                        stringsArrayList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            distribution = data.getValue(Distribution.class);
                            nameOfDistribution = data.getKey();
                            if (currentUser == null){
                                Toast.makeText(WorkerActivity.this, "null", Toast.LENGTH_SHORT).show();
                            }
                            else
                            if ((distribution.getSelectedUsersList().contains(currentUser.getName()))
                                    && (!distribution.isActive())) {
                                distributionArrayList.add(distribution);
                                stringsArrayList.add(nameOfDistribution);
                            }
                        }
                        adp = new ArrayAdapter<>(WorkerActivity.this, R.layout.support_simple_spinner_dropdown_item, stringsArrayList);
                        listView.setAdapter(adp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                refDis.addValueEventListener(disListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        refUsers.child("Workers").addValueEventListener(usersListener);
    }


    public void connectDis(View view) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "connecting", "searching for ", true);
        disListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    distribution = data.getValue(Distribution.class);
                    if (currentUser == null){
                        progressDialog.dismiss();
                        Toast.makeText(WorkerActivity.this, "no distributions found", Toast.LENGTH_SHORT).show();
                    }
                    else
                    if ((distribution.getSelectedUsersList().contains(currentUser.getName()))
                            && (distribution.isActive())) {
                        progressDialog.dismiss();
                        ActiveDistribution();
                    }
                }
                adp = new ArrayAdapter<>(WorkerActivity.this, R.layout.support_simple_spinner_dropdown_item, stringsArrayList);
                listView.setAdapter(adp);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        refDis.addValueEventListener(disListener);
    }

    private void ActiveDistribution() {
        ArrayList<String> stringsArrayList = distribution.getSelectedUsersList();
        String name = distribution.getName();
        String dateAndTime = distribution.getDateAndTime();
        ArrayList<Location> locationArrayList = distribution.getArea().getLocationList();
        Intent t = new Intent(this, ActiveDistributionActivity.class);
        t.putExtra("selectedUsers", stringsArrayList);
        t.putExtra("name", name);
        t.putExtra("dateAndTime", dateAndTime);
        t.putExtra("area", locationArrayList);
        startActivityForResult(t, 500);
    }
}
