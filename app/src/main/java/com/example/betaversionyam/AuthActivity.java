package com.example.betaversionyam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Result;

import static com.example.betaversionyam.FBref.refUsers;

public class AuthActivity extends AppCompatActivity {
    TextView tvTitle, tvRegister, tvManager, tvWorker;
    EditText etName, etPhone, etEmail, etCode;
    CheckBox cbStayConnect;
    Button btn;
    Switch aSwitch;
    public static FirebaseAuth refAuth=FirebaseAuth.getInstance();
    ValueEventListener usersListener;
    AlertDialog ad;

    private static final String TAG = "Phone";
    String name, phone, email, code, mVerificationId, uid = "";
    Users userdb, currentUser;
    Boolean stayConnect, registered, status, isUID = false, isWorker;
    Boolean mVerificationInProgress = false;
    FirebaseUser user;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        tvManager = findViewById(R.id.tvManager);
        tvRegister = findViewById(R.id.tvRegister);
        tvTitle = findViewById(R.id.tvTitle);
        tvWorker = findViewById(R.id.tvWorker);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        cbStayConnect = findViewById(R.id.cbStayConnect);
        aSwitch = findViewById(R.id.switch1);
        btn = findViewById(R.id.buttonPolygon);
        stayConnect = false;
        registered = false;

        onVerificationStateChanged();
        regOption();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        Boolean isChecked=settings.getBoolean("stayConnect",false);
        if (refAuth.getCurrentUser()!=null && isChecked) {
            stayConnect=true;
            setUsersListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }

    private void logOption() {
        tvTitle.setText("Login");
        etName.setVisibility(View.INVISIBLE);
        etEmail.setVisibility(View.INVISIBLE);
        aSwitch.setVisibility(View.INVISIBLE);
        tvWorker.setVisibility(View.INVISIBLE);
        tvManager.setVisibility(View.INVISIBLE);
        btn.setText("Login");
        registered=true;


        SpannableString ss = new SpannableString("Don't have an account?  Register here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                registered=false;
                isUID=false;
                regOption();
            }
        };
        ss.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(ss);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void regOption() {
        tvTitle.setText("Register");
        etName.setVisibility(View.VISIBLE);
        etPhone.setVisibility(View.VISIBLE);
        aSwitch.setVisibility(View.VISIBLE);
        tvWorker.setVisibility(View.VISIBLE);
        tvManager.setVisibility(View.VISIBLE);
        etEmail.setVisibility(View.VISIBLE);
        btn.setText("Register");

        SpannableString ss = new SpannableString("Already have an account?  Login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                isUID=true;
                registered=true;
                logOption();
            }
        };
        ss.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(ss);
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void logOrReg(View view) {
        if (registered){
            phone = etPhone.getText().toString();
            startPhoneNumberVerification(phone);
            onVerificationStateChanged();
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            final EditText edittext = new EditText(this);
            adb.setMessage("enter the code");
            adb.setTitle("Authentication");
            adb.setView(edittext);
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    code = edittext.getText().toString();
                    verifyPhoneNumberWithCode(mVerificationId ,code);
                }
            });
            adb.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            ad = adb.create();
            ad.show();
        }
        else{
            name = etName.getText().toString();
            email = etEmail.getText().toString();
            phone = etPhone.getText().toString();
            if (aSwitch.isChecked()) status = true;
            else status = false;
            isWorker = status;

            if (name.isEmpty()) etName.setError("you must enter a name");
            if (email.isEmpty()) etEmail.setError("you must enter an email ");
            if (phone.isEmpty()) etPhone.setError("you must enter a phone number");
            if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty()) {
                userdb = new Users(name, email, phone, uid, status);
                if (status) refUsers.child("Workers").child(name).setValue(userdb);
                else refUsers.child("Managers").child(name).setValue(userdb);
                logOption();
            }
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        refAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(AuthActivity.this, "Successful login", Toast.LENGTH_SHORT).show();
                            SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("stayConnect", cbStayConnect.isChecked());
                            editor.putBoolean("firstRun", false);
                            editor.commit();

                            FirebaseUser user = refAuth.getCurrentUser();
                            uid = user.getUid();
                            if (!isUID) {
                                if (isWorker)
                                    refUsers.child("Workers").child(name).child("uid").setValue(uid);
                                else
                                    refUsers.child("Managers").child(name).child("uid").setValue(uid);
                            }

                            setUsersListener();

                        }

                        else {
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(AuthActivity.this, "wrong!", Toast.LENGTH_LONG).show();
                            }
                        }
                });
    }

    private void onVerificationStateChanged() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    etCode.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) { }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
            }
        };
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
                            Intent si = new Intent(AuthActivity.this, WorkerActivity.class);
                            startActivity(si);
                        }
                        else {
                            Intent si = new Intent(AuthActivity.this, ManagerActivity.class);
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
