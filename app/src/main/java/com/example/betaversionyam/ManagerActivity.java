package com.example.betaversionyam;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import static com.example.betaversionyam.FBref.refDis;
import static com.example.betaversionyam.FBref.refUsers;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		13/02/2020
 *
 * The home screen of a manager. He can start a new distribution or change the status of opened distribution.
 */

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
    int howManyActive = 0;


    /**
     * the function makes a connection between the variables in the java to the xml components
     * and find all of the distributions that was created. The active distributions are in green and the not active are in red.
     */
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
                    if (distribution.isActive()) howManyActive++;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (refDis != null) refDis.removeEventListener(disListener);
    }

    public void newDis(View view) {
        if (howManyActive > 0 )
            Toast.makeText(this, "only one active distribution at one time", Toast.LENGTH_SHORT).show();
        else {
            t = new Intent(this, newDistribution.class);
            startActivity(t);
            finish();
        }
    }

    /**
     * this function is called when the manager clicks on an item of the listView.
     * the manager can decide if the selected distribution is active or not.

     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage("is this distribution active?");
        final TextView tv = view.findViewById(android.R.id.text1);
        adb.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (howManyActive>0)
                    Toast.makeText(ManagerActivity.this, "only one active distribution at one time", Toast.LENGTH_SHORT).show();
                else {
                    isActive = true;
                    refDis.child(stringsArrayList.get(position)).child("active").setValue(isActive);
                    tv.setTextColor(Color.GREEN);
                }
            }
        });
        adb.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                howManyActive--;
                isActive = false;
                refDis.child(stringsArrayList.get(position)).child("active").setValue(isActive);
                tv.setTextColor(Color.RED);
            }
        });
        if (distributionArrayList.get(position).isActive()) {
            adb.setNeutralButton("connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent t = new Intent(ManagerActivity.this , ManagerMapActivity.class);
                    t.putExtra("name" , stringsArrayList.get(position));
                    startActivityForResult(t,111);
                    finish();
                }
            });
        }
        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String s = item.getTitle().toString();
        if (s.equals("Credits")) {
            startActivity(new Intent(ManagerActivity.this, CreditsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

