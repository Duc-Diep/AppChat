package com.example.appchat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.appchat.R;
import com.example.appchat.event.EventCloseActivity;
import com.example.appchat.login.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase rootNode;
    DatabaseReference myDatabase;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootNode = FirebaseDatabase.getInstance();
        myDatabase = rootNode.getReference("Message");
        getFragment(LoginFragment.newInstance());
    }
    public void getFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_login,fragment).commit();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseEvent(EventCloseActivity event) {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            Intent intent = new Intent(MainActivity.this,ChatActivity.class);
            startActivity(intent);
        }
    }

}