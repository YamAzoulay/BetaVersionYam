package com.example.betaversionyam;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.betaversionyam.FBref.refUsers;


/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		05/03/2020
 *
 * In this activity the manager choose the workers of the distribution.
 */
public class AddUsersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ArrayList<Users> UsersList = new ArrayList<>();
    ArrayList<String> UsersStringList = new ArrayList<>();
    ArrayList<String> SelectedUsersList = new ArrayList<>();
    ListView lv;
    ValueEventListener usersListener;
    ArrayAdapter adp;
    Users user;
    String userString, selectedWorkers="";
    TextView textView;
    Intent gi = new Intent();

    /**
     * the function makes a connection between the variables in the java to the xml components
     * and find all of the registered workers and shows them on a list view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);
        lv = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);
        lv.setOnItemClickListener(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersList.clear();
                UsersStringList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    user = data.getValue(Users.class);
                    UsersList.add(user);
                    userString = data.getKey();
                    UsersStringList.add(userString);
                }
                adp = new ArrayAdapter<>(AddUsersActivity.this, R.layout.support_simple_spinner_dropdown_item, UsersStringList);
                lv.setAdapter(adp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        refUsers.child("Workers").addValueEventListener(usersListener);
    }



    public void select(View view) {
        if (!SelectedUsersList.isEmpty()) {
            gi.putExtra("selectedWorkers", SelectedUsersList);
            setResult(RESULT_OK, gi);
            finish();
        }
    }

    public void getBack(View view) {
        Intent t = new Intent(this, newDistribution.class);
        startActivity(t);
        finish();
    }

    /**
     * this function is called when the manager clicks on an item of the listView.
     * the manager can decide who are the workers that work in this distribution.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!UsersStringList.isEmpty() && selectedWorkers.contains(UsersStringList.get(position))){
                if (SelectedUsersList.size()==1)
                    Toast.makeText(this, "you must select at least one worker", Toast.LENGTH_SHORT).show();
                else {
                    SelectedUsersList.remove(position);
                    selectedWorkers = TextUtils.join(", ", SelectedUsersList);
                    textView.setText(selectedWorkers);
                }
            }
        else {
            SelectedUsersList.add(UsersStringList.get(position));
            selectedWorkers = TextUtils.join( ", " , SelectedUsersList);
            textView.setText(selectedWorkers);
        }
    }
}