package com.example.betaversionyam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.concurrent.TimeUnit;
import static com.example.betaversionyam.FBref.refUsers;

/**
 * @author		Yam Azoulay
 * @version	    1.0
 * @since		13/02/2020
 *
 * Authentication activity, any user needs to register or log in to move on.
 */

public class AuthActivity extends AppCompatActivity {
    TextView tvTitle, tvRegister, tvManager, tvWorker;
    EditText etName, etPhone, etEmail, etCode;
    CheckBox cbStayConnect;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (refUsers!=null) refUsers.removeEventListener(usersListener);
        if (ad!=null) ad.dismiss();
    }

    /**
     * this function is called when the user is in the register option but he needs to log in.
     * the function "changes" the screen for the login option
     */


    private void logOption() {
        tvTitle.setText("Login");
        etName.setVisibility(View.INVISIBLE);
        etEmail.setVisibility(View.INVISIBLE);
        aSwitch.setVisibility(View.INVISIBLE);
        tvWorker.setVisibility(View.INVISIBLE);
        tvManager.setVisibility(View.INVISIBLE);
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

    /**
     * this function is called when the user is in the log option but he needs to register.
     * the function "change" the screen for the register option.
     */

    private void regOption() {
        tvTitle.setText("Register");
        etName.setVisibility(View.VISIBLE);
        etPhone.setVisibility(View.VISIBLE);
        aSwitch.setVisibility(View.VISIBLE);
        tvWorker.setVisibility(View.VISIBLE);
        tvManager.setVisibility(View.VISIBLE);
        etEmail.setVisibility(View.VISIBLE);

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

    /**
     * this function is called when the user clicks on the button (register / login ).
     * the function checks if the user is registered, if yes, he logs in using phone number auth
     * if not, he registers and his info uploads to firebase database.

     */


    public void logOrReg(View view) {
        if (registered){
            phone = etPhone.getText().toString();
            if (phone.isEmpty()) etPhone.setError("you must enter a phone number");
            else {
                if (!phone.startsWith("+972")) phone = "+972" + phone;
                startPhoneNumberVerification(phone);
                onVerificationStateChanged();
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                final EditText edittext = new EditText(this);
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                adb.setMessage("enter the code");
                adb.setTitle("Authentication");
                adb.setView(edittext);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        code = edittext.getText().toString();
                        if (!code.isEmpty())
                            verifyPhoneNumberWithCode(mVerificationId, code);
                    }
                });
                ad = adb.create();
                ad.show();
                Toast.makeText(this, "the code is on his way to your phone", Toast.LENGTH_SHORT).show();
            }
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
                if (!phone.startsWith("+972")) phone = "+972" + phone;
                userdb = new Users(name, email, phone, uid, status);
                if (status) refUsers.child("Workers").child(name).setValue(userdb);
                else refUsers.child("Managers").child(name).setValue(userdb);
                logOption();
            }
        }
    }

    /**
     * this function is called when the user wants to login.
     * the function sends sms to his phone number with a verification code.
     *
     * @param	phoneNumber the phone number of the user. The SMS is sent to this phone number.
     */

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        mVerificationInProgress = true;
    }

    /**
     * this function is called to check if the code the user wrote is the code he received and create a credential.
     * if he wrote a right code, "signInWithPhoneAuthCredential" function is called.
     * @param	code the code that the
     * @param verificationId a verification identity to connect with firebase servers.
     */
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    /**
     * this function is called to sign in the user.
     * if the credential is proper the user is signs in and he sent to the next activity, depends on his status (worker or manager)
     * @param	credential a credential that everything was right and he can sign in.
     */
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        refAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
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

    /**
     * this function checks the status of the verification, if it's completed, failed or inProgress.
     */
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

    /**
     * this function connect the current user with his information in the database by checking his uid,
     * in purpose to check his status and sent him to the right activity.
     */

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
                            finish();
                        }
                        else {
                            Intent si = new Intent(AuthActivity.this, ManagerActivity.class);
                            startActivity(si);
                            finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String s = item.getTitle().toString();
        if (s.equals("Credits")) {
            startActivity(new Intent(AuthActivity.this, CreditsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
