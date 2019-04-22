package cs591e1_sp19.eatogether;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText email,password,name;
    private Button registerButton;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;

    //Temporary hard coding, will change it into buildConfig
    final private String img = "https://firebasestorage.googleapis.com/v0/b/eatogether-cs591.appspot.com/o/profile_img-01.png?alt=media&token=ef833632-1a12-47cf-aecf-a4504abe9c02";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.userEmail);
        name = findViewById(R.id.userName);
        password = findViewById(R.id.userPassword);
        registerButton = findViewById(R.id.userRegisterButton);
        loginButton = findViewById(R.id.userLoginButton);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailS = email.getText().toString();
                final String nameS = name.getText().toString();
                final String passwordS = password.getText().toString();

                if(TextUtils.isEmpty(emailS)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(nameS)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passwordS)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                }

                if(passwordS.length()<6){
                    Toast.makeText(getApplicationContext(),"Password must be at least 6 characters",Toast.LENGTH_SHORT).show();
                }

                firebaseAuth.createUserWithEmailAndPassword(emailS,passwordS)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    Toast.makeText(getApplicationContext(), "register successful!", Toast.LENGTH_SHORT).show();
                                    addUser(emailS, nameS, passwordS);
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"E-mail or password is wrong",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }


    private void addUser(String emailS, String nameS, String passwordS) {
        FirebaseDatabase mref = FirebaseDatabase.getInstance();

        DatabaseReference db = mref
                .getReference()
                .child("Users");

        String newKey = db.push().getKey();
        db.child(newKey).child("email").setValue(emailS);
        db.child(newKey).child("name").setValue(nameS);
        db.child(newKey).child("password").setValue(passwordS);
        db.child(newKey).child("user_rating").setValue("5.0");
        db.child(newKey).child("rating_amount").setValue("0");
        db.child(newKey).child("avatar").setValue(img);
    }
}
