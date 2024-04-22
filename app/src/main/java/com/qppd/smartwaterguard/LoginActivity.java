package com.qppd.smartwaterguard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.qppd.smartwaterguard.Libs.AutoTimez.AutotimeClass;
import com.qppd.smartwaterguard.Libs.Functionz.UserFunctions;
import com.qppd.smartwaterguard.Libs.IntentManager.IntentManagerClass;
import com.qppd.smartwaterguard.Libs.Permissionz.PermissionClass;
import com.qppd.smartwaterguard.Libs.Validatorz.ValidatorClass;
import com.qppd.smartwaterguard.Tasks.LoginTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private UserFunctions function = new UserFunctions(this);
    private AutotimeClass autotime = new AutotimeClass(this);
    private PermissionClass permission = new PermissionClass(this, this);

    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.CAMERA, android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CALL_PHONE, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CHANGE_NETWORK_STATE, android.Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private EditText email;
    private EditText password;

    private Button btn_login;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatase = FirebaseDatabase.getInstance();

    private SignInButton btSignIn;
    GoogleSignInClient googleSignInClient;

    boolean doubleBackToExitPressedOnce = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        function.noActionBar(getSupportActionBar());
        autotime.checkAutotime();

        // Get the current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

// Save the current date to Firebase Realtime Database
        firebaseDatase.getReference().child("datas/time").setValue(currentDate);

        if (!permission.hasPermissions()) {
            permission.requestPermissions(PERMISSION_ALL);
        }

        initializeComponents();
        attemptSignin();

        btSignIn = findViewById(R.id.btnGoogleSignIn);
        btSignIn.setVisibility(View.GONE);
        // Initialize sign in options the client-id is copied form google-services.json file
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("958066941515-dqn4h4vq93clr1id5doc15eviakv119j.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);

        btSignIn.setOnClickListener((View.OnClickListener) view -> {
            // Initialize sign in intent
            Intent intent = googleSignInClient.getSignInIntent();
            // Start activity for result
            startActivityForResult(intent, 100);
        });

        // Initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            //startActivity(new Intent(LoginActivity.this, MonitorActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful initialize string
                String s = "Google sign in successful";
                // Display Toast
                function.showMessage(s);
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Check condition
                                if (task.isSuccessful()) {
                                    // When task is successful redirect to profile activity display Toast
                                    //startActivity(new Intent(LoginActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    function.showMessage("Firebase authentication successful");
                                } else {
                                    // When task is unsuccessful display Toast
                                    function.showMessage("Authentication Failed :" + task.getException().getMessage());
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void attemptSignin() {
        // Reset errors.
        email.setError(null);
        password.setError(null);

        boolean cancel = false;
        View focusView = null;

        String signin_email = email.getText().toString();
        String signin_password = password.getText().toString();


        if (TextUtils.isEmpty(signin_email)) {
            email.setError("Email is empty!");
            focusView = email;
            cancel = true;
        } else if (!ValidatorClass.validateEmailOnly(signin_email)) {
            email.setError("Invalid Email!");
            focusView = email;
            cancel = true;
        }

        if (TextUtils.isEmpty(signin_password)) {
            password.setError("Password is empty!");
            focusView = password;
            cancel = true;
        } else if (!ValidatorClass.validatePasswordOnly(signin_password)) {
            password.setError("Invalid Password!");
            focusView = password;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt signup and focus the
            // field with an error.
            focusView.requestFocus();
        } else {

            firebaseAuth.signInWithEmailAndPassword(signin_email, signin_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    String uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                    //function.showMessage(uid);
                    if (task.isSuccessful()) {
                        LoginTask loginTask = new LoginTask(uid, LoginActivity.this, function);
                        loginTask.execute((Void) null);
                    } else {
                        function.showMessage("Login failed!");
                    }
                }
            });


        }
    }

    void initializeComponents() {
        email = findViewById(R.id.email);
        email.setText("sajedhm@gmail.com");
        password = findViewById(R.id.password);
        password.setText("Jedtala01+");
        btn_login = findViewById(R.id.login);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                attemptSignin();
                //IntentManagerClass.intentsify(this, MonitoringActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;

        }
    }


}

