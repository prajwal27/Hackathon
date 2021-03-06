package com.example.hackathon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckingActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private String TAG = CheckingActivity.class.getName();
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking);

        session = new SessionManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(CheckingActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "send to login : Checking");
            sendToLogin();
        } else {
            current_user_id = mAuth.getCurrentUser().getUid();
            Log.d(TAG, current_user_id);

            firebaseFirestore.collection("uid").document(current_user_id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.get("type").equals("club")) {
                                session.setType("club");
                                Log.d(TAG, "club");

                                if (session.isSetup(current_user_id)) {
                                    Log.d("isSetup", "club");
                                    Intent formal = new Intent(CheckingActivity.this, FormalHomeActivity.class);
                                    formal.putExtra("formal", session.getUser(current_user_id));
                                    startActivity(formal);
                                    finish();
                                } else {
                                    firebaseFirestore.collection("formal").document(current_user_id).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        /*CustomerFinal customer = documentSnapshot.toObject(CustomerFinal.class);
                                                        session.createProfileCustomer(customer.getName(),customer.getImage(),customer.getDob(),customer.getCity()
                                                        ,customer.getState(),customer.getPhone(),customer.getAddressLine1(),customer.getAddressLine2()
                                                                ,customer.getAddressLine3());
                                                        session.saveCustomer(customer);*/
                                                        User user = documentSnapshot.toObject(User.class);
                                                        Intent intent = new Intent(CheckingActivity.this, FormalHomeActivity.class);
                                                        intent.putExtra("formal", user);
                                                        startActivity(intent);

                                                    } else {
                                                       /* Intent customer = new Intent(CheckingActivity.this, SetupActivity.class);
                                                        customer.putExtra("type", "customer");
                                                        startActivity(customer);
                                                        finish();*/
                                                       Log.d("club","cscdc");
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                }

                                    /*{
                                    Intent customer = new Intent(CheckingActivity.this, SetupActivity.class);
                                    customer.putExtra("type","customer");
                                    startActivity(customer);
                                    finish();
                                }*/

                            } else {
                                session.setType("informal");
                                Log.d(TAG, "informal");
                                if (session.isSetup(current_user_id)) {
                                    Intent informal = new Intent(CheckingActivity.this, InformalHomeActivity.class);
                                    informal.putExtra("informal", session.getUser(current_user_id));
                                    startActivity(informal);
                                    finish();
                                } else {
                                    firebaseFirestore.collection("informal").document(current_user_id).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                       /* Labourer labourer = documentSnapshot.toObject(Labourer.class);
                                                        session.createProfileLabourer(labourer.getName(), labourer.getImage(), labourer.getDob(), labourer.getCity()
                                                                , labourer.getState(), labourer.getPhone(), labourer.getAddressLine1(), labourer.getAddressLine2()
                                                                , labourer.getAddressLine3(), labourer.getSkill(), 9L);

                                                        Intent intent = new Intent(CheckingActivity.this, InformalHomeActivity.class);
                                                        startActivity(intent);*/

                                                        User user = documentSnapshot.toObject(User.class);
                                                        Intent intent = new Intent(CheckingActivity.this, InformalHomeActivity.class);
                                                        intent.putExtra("informal", user);
                                                        startActivity(intent);
                                                    } else {
                                                        Intent user = new Intent(CheckingActivity.this, SetupActivity.class);
                                                        user.putExtra("type", "informal");
                                                        startActivity(user);
                                                        finish();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "error22 : " + e.toString());
                                                }
                                            });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "error11 : " + e.toString());
                        }
                    });
        }
    }
}
