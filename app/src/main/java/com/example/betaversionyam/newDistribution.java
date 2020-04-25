package com.example.betaversionyam;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.betaversionyam.FBref.refDis;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		26/02/2020
 *
 * In this activity the manager can create a new distribution.
 */
public class newDistribution extends AppCompatActivity implements Serializable {
    static final String TAG = "newDistribution";
    ArrayList<String> SelectedUsersList = new ArrayList<>();
    TextView textViewDate, textViewTime, textViewSumUsers, tvTextTime, tvTextDate, tvMap;
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
    char[] invalidChars = new char[6];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_distribution);

        textViewSumUsers = findViewById(R.id.tvUsers);
        editText = findViewById(R.id.editText);
        textViewDate = findViewById(R.id.tvDate);
        textViewTime = findViewById(R.id.tvTime);
        mapButton = findViewById(R.id.mapButton);
        tvTextDate = findViewById(R.id.tvTextDate);
        tvTextTime = findViewById(R.id.tvTextTime);
        tvMap = findViewById(R.id.textViewMap);

        invalidChars[0] = '.';
        invalidChars[1] = '$';
        invalidChars[2] = '#';
        invalidChars[3] = '[';
        invalidChars[4] = ']';
        invalidChars[5] = '/';


        calendar = Calendar.getInstance();
        setDate();
        setTime();
    }

    /**
     * this function is called when the manager clicks on the text view of the date, in order to select the date of the distribution.
     * the function opens a date picker dialog and the manager selects the date.
     */

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
                tvTextDate.setText(date);
            }
        };
    }

    /**
     * this function is called when the manager clicks on the text view of the time, in order to select the time of the distribution.
     * the function opens a time picker dialog and the manager selects the time.
     */
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
                selectedTime = String.format("%02d:%02d", hour, min);
                tvTextTime.setText(selectedTime);
            }
        };
    }

    /**
     * this function is called when the manager finished to enter all the details and wants to create the distribution.
     * the function checks that the input is well and uploads the details to the database.
     */

    public void applyNewDis(View view) {

        dateAndTime =  dayAndMonth + "/" + year + selectedTime;
        name = editText.getText().toString();

        if (checkInput()) {
            distribution = new Distribution(dateAndTime, name, SelectedUsersList, area, true);
            refDis.child(name).setValue(distribution);
            Intent t = new Intent(this, ManagerActivity.class);
            startActivity(t);
            finish();
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

    /**
     * this function is called when the user return from the activity to choose workers or from the activity to choose an area.
     * the function detects when the manager came back from and saves the information from that activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null && requestCode == 100) {
            SelectedUsersList = data.getStringArrayListExtra("selectedWorkers");
            if (SelectedUsersList!=null) {
                sumUsers = SelectedUsersList.size();
                textViewSumUsers.setText("" + sumUsers);
            }
        }
        if (data != null && requestCode == 200) {
            latAndLngArrayList = (ArrayList<LatAndLng>) data.getExtras().getSerializable("selectedArea");
            area = new Area(latAndLngArrayList);
            if (latAndLngArrayList!=null)
                tvMap.setText("successful");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * this function checks if the manager entered all of the details that required to open a new distribution.
     */
    public boolean checkInput(){
        boolean ok = true;
        for (char invalidChar : invalidChars) {
            if (name.contains(Character.toString(invalidChar))) {
                editText.setError("Invalid Character.");
                ok=false;
                break;
            }
        }
        if (name.isEmpty()) {
            editText.setError("select a name");
            ok=false;
        }
        if (SelectedUsersList.isEmpty()) {
            Toast.makeText(this, "you must select workers", Toast.LENGTH_SHORT).show();
            ok = false;
        }
        if (selectedTime==null) {
            tvTextTime.setError("you must select time");
            ok = false;
        }
        if (dayAndMonth==null) {
            tvTextDate.setError("you must select date");
            ok=false;
        }
        if (latAndLngArrayList == null){
            Toast.makeText(this, "you must select an area", Toast.LENGTH_SHORT).show();
            ok=false;
        }

        return ok;
    }


}