package com.example.betaversionyam;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import static com.example.betaversionyam.FBref.refDis;

public class newDistribution extends AppCompatActivity implements Serializable {
    static final String TAG = "newDistribution";
    ArrayList<String> SelectedUsersList = new ArrayList<>();
    TextView textViewDate, textViewTime, textViewSumUsers;
    DatePickerDialog.OnDateSetListener datePickerDialog;
    TimePickerDialog.OnTimeSetListener timePickerDialog;
    ArrayList<LatAndLng> latAndLngArrayList;
    Calendar calendar;
    int year, month, day, hour, min, sumUsers=0;
    EditText editText;
    String name, dateAndTime, selectedTime, dayAndMonth;
    Area area;
    Distribution distribution;
    Button mapButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_distribution);

        textViewSumUsers = findViewById(R.id.tvUsers);
        editText = findViewById(R.id.editText);
        textViewDate = findViewById(R.id.tvDate);
        textViewTime = findViewById(R.id.tvTime);
        mapButton = findViewById(R.id.mapButton);

        calendar = Calendar.getInstance();
        setDate();
        setTime();
    }

    private void setDate(){
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        newDistribution.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        datePickerDialog,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        datePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year1, int month1, int day1) {
                year=year1;
                month=month1;
                day=day1;
                month++;
                dayAndMonth = String.format("%02d/%02d", day, month);
                Log.d(TAG, "onDateSet: dd/mm/yyyy: " + dayAndMonth + "/" + year);
                String date = day + "/" + month + "/" + year;
                textViewDate.setText(date);
            }
        };
    }

    private void setTime() {
        textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                min = calendar.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(newDistribution.this, timePickerDialog, hour, min,true);{
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            }
        });

        timePickerDialog = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                min = minute;
                Log.d(TAG, "onTimeSet: hh/mm: " + hour + "/" + min);
                selectedTime = String.format("%02d:%02d", hour, min);
                textViewTime.setText(selectedTime);
            }
        };
    }

    public void applyNewDis(View view) {

        dateAndTime = "" + year + "/" + dayAndMonth + selectedTime;
        Toast.makeText(this, dateAndTime, Toast.LENGTH_LONG).show();
        name = editText.getText().toString();
        if (name == null) editText.setError("select a name");
        else {
            distribution = new Distribution(dateAndTime, name, SelectedUsersList, area, true);
            refDis.child(name).setValue(distribution);
            Intent t = new Intent(this, ManagerActivity.class);
            startActivity(t);
        }
}

    public void moveToMap(View view) {
        Intent si = new Intent(this, MapActivity.class);
        startActivityForResult(si,200);
    }

    public void addUsers(View view) {
        Intent si = new Intent(this, AddUsersActivity.class);
        startActivityForResult(si,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null && requestCode == 100) {
            SelectedUsersList = data.getStringArrayListExtra("selectedWorkers");
            sumUsers = SelectedUsersList.size();
            textViewSumUsers.setText("" + sumUsers);
        }
        if (data != null && requestCode == 200) {
            latAndLngArrayList = (ArrayList<LatAndLng>) data.getExtras().getSerializable("selectedArea");
            area = new Area(latAndLngArrayList);
            mapButton.setTextColor(Color.rgb(0,255,0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}