package com.interview.practicall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.interview.practicall.R;
import com.interview.practicall.model.UserModel;
import com.interview.practicall.utils.App;
import com.interview.practicall.utils.MySharedPreferences;
import com.interview.practicall.utils.Utils;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private LinearLayoutCompat llLogin;
    private TextView tvTitle;
    private AppCompatEditText edtFName;
    private AppCompatEditText edtLName;
    private AppCompatEditText edtEmail;
    private AppCompatEditText edtPass;
    private MaterialButton btnAuthRegister;
    private AppCompatTextView tvAuthOrRegister;
    boolean registration = true;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        checkForLogin();
        initView();
        bindClick();

    }

    private void checkForLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String isLogin = "";
                if (App.sharedPreferences.chk(MySharedPreferences.isLogin)) {
                    isLogin = App.sharedPreferences.getKey(MySharedPreferences.isLogin);
                }
                if (isLogin.equals(MySharedPreferences.YES)) {
                    MainActivity.this.startActivity(new Intent(MainActivity.this,
                            DashboardActivity.class));
                    MainActivity.this.finish();
                }
            }
        }, 1500);
    }

    private void bindClick() {


        tvAuthOrRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registration){
                    registration = false;
                    tvTitle.setText("Login");
                    btnAuthRegister.setText("Login");
                    edtFName.setVisibility(View.GONE);
                    edtLName.setVisibility(View.GONE);
                    tvAuthOrRegister.setText("Don't have Account ? Register");
                }else {
                    registration = true;
                    tvTitle.setText("Registration");
                    btnAuthRegister.setText("Registration");
                    edtFName.setVisibility(View.VISIBLE);
                    edtLName.setVisibility(View.VISIBLE);
                    tvAuthOrRegister.setText("Already have Account ? Login");
                }
            }
        });

        btnAuthRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
    }

    private void validation() {

        String email, password,fname,lname;
        fname = edtFName.getText().toString();
        lname = edtLName.getText().toString();
        email = edtEmail.getText().toString();
        password = edtPass.getText().toString();

        // Validations for input email and password
        if (!Utils.isValidEmail(email)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter valid email!!",
                    Toast.LENGTH_LONG)
                    .show();
        }else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter password!!",
                    Toast.LENGTH_LONG)
                    .show();
        }else {
            if (registration){
                if (TextUtils.isEmpty(fname)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter first name!!",
                            Toast.LENGTH_LONG)
                            .show();
                }else if (TextUtils.isEmpty(lname)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter last name!!",
                            Toast.LENGTH_LONG)
                            .show();
                }else{
                    Utils.showProgressDialog(MainActivity.this,false,"Loading...");
                    checkForFirstLastName(fname,lname,email,password);
                }
            }else {
                Utils.showProgressDialog(MainActivity.this,false,"Loading...");
                callForAuth(email,password);
            }
        }
    }

    private void checkForFirstLastName(String fname, String lname, String email, String password) {

        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isAvail = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> userData = document.getData();
                                String userid = document.getId();
                                Log.d("ID======>",userid);
                                if (fname.equals(userData.get("fName"))) {
                                    isAvail = true;
                                    Utils.dismisProgressDialog();
                                    Toast.makeText(MainActivity.this, "First Name is Taken! Please choose another one.", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                            if (!isAvail){
                                db.collection("Users")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    boolean check = false;
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Map<String, Object> userData = document.getData();
                                                        String userid = document.getId();
                                                        Log.d("ID======>",userid);
                                                        if (lname.equals(userData.get("lName"))) {
                                                            check = true;
                                                            Utils.dismisProgressDialog();
                                                            Toast.makeText(MainActivity.this, "Last Name is Taken! Please choose another one.", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        }
                                                    }

                                                    if (!check){
                                                        callForRegistration(email,password,fname,lname);
                                                    }
                                                } else {
                                                    Utils.dismisProgressDialog();
                                                    Toast.makeText(MainActivity.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                                                    Log.w("login", "Error getting documents.", task.getException());
                                                }
                                            }

                                        });
                            }
                        } else {
                            Utils.dismisProgressDialog();
                            Toast.makeText(MainActivity.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("login", "Error getting documents.", task.getException());
                        }
                    }

                });

    }

    private void callForAuth(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utils.dismisProgressDialog();
                        Toast.makeText(getApplicationContext(),
                                "Login failed!!"+e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                })
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
                                    Utils.dismisProgressDialog();
                                    Toast.makeText(getApplicationContext(),
                                            "Login successful!!",
                                            Toast.LENGTH_LONG)
                                            .show();
                                    App.sharedPreferences.setKey(MySharedPreferences.isLogin, MySharedPreferences.YES);
                                    startActivity(new Intent(MainActivity.this,DashboardActivity.class));
                                    MainActivity.this.finish();
                                }

                                else {
                                    // sign-in failed
                                    Utils.dismisProgressDialog();


                                }
                            }
                        });
    }

    private void callForRegistration(String email, String password, String fname, String lname) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utils.dismisProgressDialog();
                        Toast.makeText(getApplicationContext(),
                                "Registration failed!!"+e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Registration successful!",
                                    Toast.LENGTH_LONG)
                                    .show();
                            goForStoreInDB(email,password,fname,lname);
                        }
                        else {
                            Utils.dismisProgressDialog();
                            // Registration failed
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Registration failed!!"
                                            + " Please try again later",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    private void goForStoreInDB(String email, String password, String fname, String lname) {

        CollectionReference dbCourses = db.collection("Users");

        // adding our data to our courses object class.
        UserModel user = new UserModel(fname, lname, email,password);

        // below method is use to add data to Firebase Firestore.
        dbCourses.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Utils.dismisProgressDialog();
                Toast.makeText(MainActivity.this, "User has been added to Firebase Firestore", Toast.LENGTH_SHORT).show();
                registration = false;
                edtFName.setText("");
                edtLName.setText("");
                edtEmail.setText("");
                edtPass.setText("");
                tvTitle.setText("Login");
                btnAuthRegister.setText("Login");
                edtFName.setVisibility(View.GONE);
                edtLName.setVisibility(View.GONE);
                tvAuthOrRegister.setText("Don't have Account ? Register");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Utils.dismisProgressDialog();

                Toast.makeText(MainActivity.this, "Fail to add user \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        llLogin = (LinearLayoutCompat) findViewById(R.id.llLogin);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        edtFName = (AppCompatEditText) findViewById(R.id.edtFName);
        edtLName = (AppCompatEditText) findViewById(R.id.edtLName);
        edtEmail = (AppCompatEditText) findViewById(R.id.edtEmail);
        edtPass = (AppCompatEditText) findViewById(R.id.edtPass);
        btnAuthRegister = (MaterialButton) findViewById(R.id.btnAuthRegister);
        tvAuthOrRegister = (AppCompatTextView) findViewById(R.id.tvAuthOrRegister);
    }
}