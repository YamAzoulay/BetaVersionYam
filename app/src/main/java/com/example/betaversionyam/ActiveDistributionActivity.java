package com.example.betaversionyam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActiveDistributionActivity extends AppCompatActivity {
    TextView tvName, tvDate, tvTime, tvSelectedUsers;
    Intent back;
    ArrayList<LatAndLng> latAndLngArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_distribution);
        tvName = findViewById(R.id.textViewName);
        tvDate = findViewById(R.id.tvDate1);
        tvTime = findViewById(R.id.tvTime1);
        tvSelectedUsers = findViewById(R.id.tvSelectedUsers);
        Initialing();
    }

    private void Initialing() {
        back = getIntent();
        ArrayList<String> stringArrayList;
        String name, dateAndTime, date, time, stringSelectedUsers;
        stringArrayList= back.getStringArrayListExtra("selectedUsers");
        name = back.getStringExtra("name");
        dateAndTime = back.getStringExtra("dateAndTime");
         latAndLngArrayList = (ArrayList<LatAndLng>) back.getExtras().getSerializable("area");
        if (latAndLngArrayList == null) Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        stringSelectedUsers = TextUtils.join(",", stringArrayList);
        time = dateAndTime.substring(10);
        date = dateAndTime.substring(0,10);
        tvName.setText(name);
        tvTime.setText("the time is  " + time);
        tvDate.setText("the date is  " + date);
        tvSelectedUsers.setText("the workers are  " +stringSelectedUsers);
    }

    public void GoToMap(View view) {
        Intent t = new Intent(this, WorkerMapActivity.class);
        t.putExtra("area" , latAndLngArrayList);
        startActivityForResult(t,555);
    }
}
