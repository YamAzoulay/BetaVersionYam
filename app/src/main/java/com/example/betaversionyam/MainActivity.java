package com.example.betaversionyam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.example.betaversionyam.AuthActivity.refAuth;
import static com.example.betaversionyam.FBref.refUsers;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		13/02/2020
 *
 * Welcome screen
 */


public class MainActivity extends AppCompatActivity {
    Intent t;
    public static int SPLASH_TIME_OUT = 2000;
    ValueEventListener usersListener;
    FirebaseUser user;
    Users currentUser;
    Boolean isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        isChecked=settings.getBoolean("stayConnect",false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refAuth.getCurrentUser()!=null && isChecked) {
                    setUsersListener();
                }
                else {
                    Intent si = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(si);
                }
            }
        } , SPLASH_TIME_OUT);

    }

    public void setUsersListener() {
        user = refAuth.getCurrentUser();
        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (user.getUid().equals(data.getValue(Users.class).getUid())){
                        currentUser=data.getValue(Users.class);
                        if (currentUser.getIsWorker()){
                            Intent si = new Intent(MainActivity.this, WorkerActivity.class);
                            startActivity(si);
                        }
                        else {
                            Intent si = new Intent(MainActivity.this, ManagerActivity.class);
                            startActivity(si);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        refUsers.child("Managers").addValueEventListener(usersListener);
        refUsers.child("Workers").addValueEventListener(usersListener);
    }
}