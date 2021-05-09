package com.example.appchat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.appchat.R;
import com.example.appchat.chat.ListUsersFragment;
import com.example.appchat.event.EventCloseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getFragment(ListUsersFragment.newInstance());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
    }

    public void getFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_chat, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("status", "Online");
        databaseReference.updateChildren(hm);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("status", "Offline");
        databaseReference.updateChildren(hm);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("status", "Offline");
        databaseReference.updateChildren(hm);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseEvent(EventCloseActivity event) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}