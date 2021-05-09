package com.example.appchat.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.activity.MainActivity;
import com.example.appchat.adapter.UsersAdapter;
import com.example.appchat.databinding.ListuserFragmentBinding;
import com.example.appchat.event.EventCloseActivity;
import com.example.appchat.event.IOnClickUser;
import com.example.appchat.objects.MessageSend;
import com.example.appchat.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListUsersFragment extends Fragment {
    ListuserFragmentBinding binding;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference data;
    String userId;
    List<User> listUser;
    User user;
    String userImagelink;

    public static ListUsersFragment newInstance() {

        Bundle args = new Bundle();

        ListUsersFragment fragment = new ListUsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.listuser_fragment, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        data = FirebaseDatabase.getInstance().getReference("Users");

        userId = firebaseUser.getUid();

        data.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    if (user != null) {
                        userImagelink = user.getImageurl();
                        String username = user.getUsername();
                        if (getContext()!=null) {
                            Glide.with(getContext()).load(userImagelink).into(binding.imgAvatar);
                        }
                        binding.tvUsername.setText(username);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("status", "Online");
        data.child(userId).updateChildren(hm);
        getAllUser();
        binding.imgAvatar.setOnClickListener(v->{
            Fragment fragment = UserInforFragment.newInstance(user);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right).replace(R.id.layout_chat,fragment).addToBackStack(null).commit();
        });
        return binding.getRoot();
    }

    public void getAllUser() {
        listUser = new ArrayList<>();
        data = FirebaseDatabase.getInstance().getReference("Users");
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                        User user = data.getValue(User.class);
                        if(!user.getId().equals(userId)){
                            listUser.add(user);
                        }
                }
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2,RecyclerView.VERTICAL,false);
                UsersAdapter adapter = new UsersAdapter(listUser,getContext());
                adapter.setiOnClickUser(new IOnClickUser() {
                    @Override
                    public void itemClick(User user) {
                        Fragment fragment = ChatFragment.newInstance(user,userImagelink);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right).replace(R.id.layout_chat,fragment).addToBackStack(null).commit();
                    }
                });
                binding.rcvUsers.setLayoutManager(layoutManager);
                binding.rcvUsers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setStatus(String status){
        try {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mAuth = FirebaseAuth.getInstance();
            data = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            HashMap<String,Object> hm = new HashMap<>();
            hm.put("status",status);
            data.updateChildren(hm);
        }catch (Exception e){

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setStatus("Offline");
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(getContext());
    }
    
}
