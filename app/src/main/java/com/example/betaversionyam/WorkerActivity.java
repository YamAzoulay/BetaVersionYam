package com.example.betaversionyam;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
 * The home screen of a worker. He can connect to a new distribution or see the last distributions.
 */
public class WorkerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener  {

    ValueEventListener usersListener, disListener;
    ListView listView;
    ArrayList<Distribution> distributionArrayList = new ArrayList<>();
    ArrayList<String> stringsArrayList = new ArrayList<>();
    Distribution distribution;
    ArrayAdapter adp;
    String uidOfUser, nameOfDistribution;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Users currentUser;
    String workerName;
    boolean isDisFound = false;

    /**
     * the function makes a connection between the variables in the java to the xml components
     * and find all of the distributions that the current worker took part in.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
        listView = findViewById(R.id.listView1);
        listView.setOnItemClickListener(this);

        FirebaseUser user = mAuth.getCurrentUser();
        uidOfUser = user.getUid();

        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (uidOfUser.equals(data.getValue(Users.class).getUid())) {
                        currentUser = data.getValue(Users.class);
                        workerName = currentUser.getName();
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
                            if (currentUser != null)
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (refUsers != null) refUsers.removeEventListener(usersListener);
        if (refDis != null) refDis.removeEventListener(disListener);
    }


    /**
     * this function is called when the worker clicks on an item of the listView.
     * the worker can see the details of the distribution.
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Distribution distribution = distributionArrayList.get(position);
        String name = distribution.getName();
        ArrayList<String> stringsArrayList = distribution.getSelectedUsersList();
        String dateAndTime = distribution.getDateAndTime();
        String time = dateAndTime.substring(10);
        String date = dateAndTime.substring(0, 10);
        String stringSelectedUsers = TextUtils.join(",", stringsArrayList);

        StringBuilder sb = new StringBuilder();
        sb.append("date: " + date);
        sb.append("\n");
        sb.append("time: " + time);
        sb.append("\n");
        sb.append("workers: " + stringSelectedUsers);

        AlertDialog.Builder adb= new AlertDialog.Builder(WorkerActivity.this);
        adb.setMessage(sb.toString());
        adb.setTitle(name);
        AlertDialog ad = adb.create();
        ad.show();
    }

    /**
     * this function is called when the worker wants to connect to a new distribution and clicks on the button "connect".
     * the function searches an active distribution that concludes the current user and sent him to an active distribution activity.
     */
    public void connectDis(View view) {
        disListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    distribution = data.getValue(Distribution.class);
                    if (currentUser != null)
                    if ((distribution != null && distribution.getSelectedUsersList().contains(currentUser.getName()))
                            && (distribution.isActive())) {
                        isDisFound = true;
                        ActiveDistribution(distribution);
                    }
                }
                if (!isDisFound) Toast.makeText(WorkerActivity.this, "no distributions was found", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        refDis.addValueEventListener(disListener);
    }



    /**
     * the function takes all the information from the distribution and pass it to an active distribution activity.
     *
     * @param distribution an active distribution that includes the current worker.
     */
    public void ActiveDistribution(Distribution distribution) {
        ArrayList<String> stringsArrayList = distribution.getSelectedUsersList();
        String name = distribution.getName();
        String dateAndTime = distribution.getDateAndTime();
        Area area = distribution.getArea();
        ArrayList<LatAndLng> latAndLngs = area.getLatAndLngArrayList();
        boolean isActive = distribution.isActive();
        Intent t = new Intent(this, ActiveDistributionActivity.class);
        t.putExtra("selectedUsers", stringsArrayList);
        t.putExtra("name", name);
        t.putExtra("dateAndTime", dateAndTime);
        t.putExtra("area", latAndLngs);
        t.putExtra("isActive" , isActive);
        t.putExtra("workerName" , workerName);
        startActivityForResult(t, 500);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String s = item.getTitle().toString();
        if (s.equals("Credits")) {
            startActivity(new Intent(WorkerActivity.this, CreditsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


}
