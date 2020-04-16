package com.example.betaversionyam;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		28/03/2020
 *
 * this screen displays all the details of the current distribution.
 */

public class ActiveDistributionActivity extends AppCompatActivity {
    TextView tvName, tvDate, tvTime, tvSelectedUsers, tvActive;
    Intent back;
    ArrayList<LatAndLng> latAndLngArrayList;
    ArrayList<String> stringArrayList;
    String name, dateAndTime, date, time, stringSelectedUsers, workerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_distribution);
        tvName = findViewById(R.id.textViewName);
        tvDate = findViewById(R.id.tvDate1);
        tvTime = findViewById(R.id.tvTime1);
        tvSelectedUsers = findViewById(R.id.tvSelectedUsers);
        tvActive = findViewById(R.id.textViewActive);
        Initialing();
    }

    /**
     * the function reads all the info that was sent from the worker activity and displays it in the right text view.
     */
    private void Initialing() {
        back = getIntent();
        if (back == null) Toast.makeText(this, "no distribution was found", Toast.LENGTH_SHORT).show();
        else {
            workerName = back.getStringExtra("workerName");
            stringArrayList = back.getStringArrayListExtra("selectedUsers");
            name = back.getStringExtra("name");
            dateAndTime = back.getStringExtra("dateAndTime");
            boolean isActive = back.getBooleanExtra("isActive", false);
            latAndLngArrayList = (ArrayList<LatAndLng>) back.getExtras().getSerializable("area");
            stringSelectedUsers = TextUtils.join(",", stringArrayList);
            if (dateAndTime != null) {
                time = dateAndTime.substring(10);
                date = dateAndTime.substring(0, 10);
                tvTime.setText("the time is  " + time);
                tvDate.setText("the date is  " + date);
            }
            tvName.setText(name);
            if (isActive)             tvActive.setText("an active distribution");
            else                      tvActive.setText("a not active distribution");
            tvSelectedUsers.setText("the workers are  " + stringSelectedUsers);
        }
    }

    /**
     * the function is called when the worker wants to see the area of the distribution and clicked on the button "go to map"
     */
    public void GoToMap(View view) {
        Intent t = new Intent(this, WorkerMapActivity.class);
        t.putExtra("area" , latAndLngArrayList);
        t.putExtra("name" , name );
        t.putExtra("workerName", workerName);
        startActivityForResult(t,555);
    }
}
