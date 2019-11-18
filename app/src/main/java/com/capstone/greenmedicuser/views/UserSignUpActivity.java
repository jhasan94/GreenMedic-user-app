package com.capstone.greenmedicuser.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.capstone.greenmedicuser.R;
import com.capstone.greenmedicuser.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

public class UserSignUpActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    String mFullName,mEmail,
            mUserAddress,mUsername,mPassword;


    @BindView(R.id.fullnat_et)
    TextInputEditText fullname;

    @BindView(R.id.email_et)
    TextInputEditText email;

    @BindView(R.id.user_address_et)
    TextInputEditText userAddress;

    @BindView(R.id.username_et)
    TextInputEditText username;

    @BindView(R.id.password_et)
    TextInputEditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);


        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }
    @OnClick(R.id.submit)
    public void signUp(){
        String nFullname =fullname.getText().toString();
        String nEmail =email.getText().toString();
        String nUserAddress =userAddress.getText().toString();
        String nUsername =username.getText().toString();
        String nPassword =password.getText().toString();

        if (nFullname.isEmpty()){
            fullname.setError("Enter code...");
            fullname.requestFocus();
            return;
        }else if (nEmail.isEmpty()){
            email.setError("Enter code...");
            email.requestFocus();
            return;
        }else if (nUserAddress.isEmpty()){
            userAddress.setError("Enter code...");
            userAddress.requestFocus();
            return;
        }else if (nUsername.isEmpty()){
            username.setError("Enter code...");
            username.requestFocus();
            return;
        }else if (nPassword.isEmpty()){
            password.setError("Enter code...");
            password.requestFocus();
            return;
        }
        User user =new User(
                true,
                nFullname,
                nEmail,

                nUserAddress,
                nUsername,
                nPassword,
                "000,000"
        );
        sendDataTotheFirebase(user);
    }

    private void sendDataTotheFirebase(User user) {
        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"").setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                assert databaseError != null;
                Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserSignUpActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });
    }


}
