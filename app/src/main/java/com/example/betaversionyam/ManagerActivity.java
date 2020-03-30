package com.example.betaversionyam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.betaversionyam.FBref.refDis;
import static com.example.betaversionyam.FBref.refUsers;

public class ManagerActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener {

    ListView listView;
    ArrayList<Distribution> distributionArrayList = new ArrayList<>();
    ArrayList<String> stringsArrayList = new ArrayList<>();
    ArrayAdapter adp;
    Intent t;
    ValueEventListener disListener;
    Distribution distribution;
    String nameOfDistribution;
    boolean isActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        disListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                distributionArrayList.clear();
                stringsArrayList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    distribution = data.getValue(Distribution.class);
                    distributionArrayList.add(distribution);
                    nameOfDistribution = data.getKey();
                    stringsArrayList.add(nameOfDistribution);
                }
                adp = new ArrayAdapter<String>(ManagerActivity.this, android.R.layout.simple_list_item_1, stringsArrayList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        for (int i = 0; i < stringsArrayList.size(); i++) {
                            TextView tv = view.findViewById(android.R.id.text1);
                            if (distributionArrayList.get(position).isActive())
                                tv.setTextColor(Color.GREEN);
                            else
                                tv.setTextColor(Color.RED);
                        }
                        return view;
                    }
                };
                listView.setAdapter(adp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        refDis.addValueEventListener(disListener);
    }

    public void newDis(View view) {
        t = new Intent(this, newDistribution.class);
        startActivity(t);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage("is this distribution active?");
        final TextView tv = view.findViewById(android.R.id.text1);
        adb.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isActive = true;
                refDis.child(stringsArrayList.get(position)).child("active").setValue(isActive);
                tv.setTextColor(Color.GREEN);
            }
        });
        adb.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isActive = false;
                refDis.child(stringsArrayList.get(position)).child("active").setValue(isActive);
                tv.setTextColor(Color.RED);
            }
        });
        adb.setNeutralButton("back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }
}

