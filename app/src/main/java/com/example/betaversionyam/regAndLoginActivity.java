package com.example.betaversionyam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static com.example.betaversionyam.FBref.refAuth;
import static com.example.betaversionyam.FBref.refUsers;

public class regAndLoginActivity extends AppCompatActivity {

    private static final String TAG = "Phone";

    TextView tVtitle, tVregister, tVworker, tVmanager;
    EditText eTname, eTphone, eTemail, eTcode;
    CheckBox cBstayconnect;
    Button btn, btnVerify;
    Switch Switch;

    private String mVerificationId;
    String name, phone, email, uid;
    Users userdb;
    Boolean stayConnect, registered, firstrun, status;
    Boolean mVerificationInProgress = false;

    PhoneAuthCredential c;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_and_login);


        tVtitle = findViewById(R.id.tVtitle);
        eTname = findViewById(R.id.eTname);
        eTemail = findViewById(R.id.eTemail);
        eTphone = findViewById(R.id.eTphone);
        eTcode = findViewById(R.id.eTcode);
        cBstayconnect = findViewById(R.id.cBstayconnect);
        tVregister = findViewById(R.id.tVregister);
        tVworker = findViewById(R.id.textView2);
        Switch = findViewById(R.id.switch1);
        tVmanager = findViewById(R.id.textView3);
        btn = findViewById(R.id.btn);
        btnVerify = findViewById(R.id.button2);

        stayConnect = false;
        registered = true;

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        firstrun = settings.getBoolean("firstRun", false);
        Toast.makeText(this, "" + firstrun, Toast.LENGTH_SHORT).show();
        if (firstrun) {
            tVtitle.setText("Register");
            eTname.setVisibility(View.VISIBLE);
            eTemail.setVisibility(View.VISIBLE);
            tVworker.setVisibility(View.VISIBLE);
            Switch.setVisibility(View.VISIBLE);
            tVmanager.setVisibility(View.VISIBLE);
            eTcode.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.VISIBLE);
            btn.setText("Register");
            registered = false;
            logoption();
        } else regoption();

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                c=credential;
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    //etCode.setError("Invalid phone number.");
                }
                else if (e instanceof FirebaseTooManyRequestsException) {
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        Intent si = new Intent(regAndLoginActivity.this, CreditsActivity.class);
        if (refAuth.getCurrentUser() != null && isChecked) {
            stayConnect = true;
            startActivity(si);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                eTcode.setError("Invalid code.");
                            }

                        }
                    }
                });
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


    private boolean validatePhoneNumber() {
        String phoneNumber = eTphone.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            eTphone.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    private void regoption() {
        SpannableString ss = new SpannableString("Don't have an account?  Register here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                tVtitle.setText("Register");
                eTname.setVisibility(View.VISIBLE);
                eTemail.setVisibility(View.VISIBLE);
                tVworker.setVisibility(View.VISIBLE);
                Switch.setVisibility(View.VISIBLE);
                eTcode.setVisibility(View.VISIBLE);
                btnVerify.setVisibility(View.VISIBLE);
                tVmanager.setVisibility(View.VISIBLE);
                btn.setText("Register");
                registered = false;
                logoption();
            }
        };
        ss.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVregister.setText(ss);
        tVregister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void logoption() {
        SpannableString ss = new SpannableString("Already have an account?  Login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                tVtitle.setText("Login");
                eTname.setVisibility(View.INVISIBLE);
                eTphone.setVisibility(View.INVISIBLE);
                btn.setText("Login");
                registered = true;
                regoption();
            }
        };
        ss.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVregister.setText(ss);
        tVregister.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void Verify(View view) {
        if (!validatePhoneNumber()) {
            return;
        }
        startPhoneNumberVerification(eTphone.getText().toString());
    }

    public void logorreg(View view) {
        phone = eTphone.getText().toString();
        if (registered) {
            final ProgressDialog pd = ProgressDialog.show(this, "Login", "Connecting...", true);
            refAuth.signInWithCredential(c)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("stayConnect", cBstayconnect.isChecked());
                                editor.commit();
                                Log.d("MainActivity", "signinUserWithEmail:success");
                                Toast.makeText(regAndLoginActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                                Intent si = new Intent(regAndLoginActivity.this, CreditsActivity.class);
                                startActivity(si);
                            } else {
                                Log.d("MainActivity", "signinUserWithEmail:fail");
                                Toast.makeText(regAndLoginActivity.this, "e-mail or password are wrong!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            name = eTname.getText().toString();
            email = eTemail.getText().toString();
            phone = eTphone.getText().toString();
            final ProgressDialog pd = ProgressDialog.show(this, "Register", "Registering...", true);
            startPhoneNumberVerification(phone);
            String code = eTcode.getText().toString();
            if (TextUtils.isEmpty(code)) {
                eTcode.setError("Cannot be empty.");
                return;
            }
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            refAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("stayConnect", cBstayconnect.isChecked());
                                editor.putBoolean("firstRun", false);
                                editor.commit();
                                Log.d("MainActivity", "createUserWithEmail:success");
                                FirebaseUser user = refAuth.getCurrentUser();
                                uid = user.getUid();
                                if (Switch.isChecked()) {
                                    status = true;
                                }
                                else status = false;
                                userdb = new Users(name, email, phone, uid);
                                if (status) refUsers.child("Workers").child(name).setValue(userdb);
                                else refUsers.child("Managers").child(name).setValue(userdb);
                                Toast.makeText(regAndLoginActivity.this, "Successful registration", Toast.LENGTH_LONG).show();
                                Intent si = new Intent(regAndLoginActivity.this, CreditsActivity.class);
                                startActivity(si);
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    eTcode.setError("Invalid code.");
                                }
                                else {
                                    Log.w("MainActivity", "createUserWithCredential:failure", task.getException());
                                    Toast.makeText(regAndLoginActivity.this, "User create failed.", Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });
        }
    }

}

