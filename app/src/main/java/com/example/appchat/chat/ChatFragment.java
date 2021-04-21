package com.example.appchat.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appchat.R;
import com.example.appchat.adapter.MessageAdapter;
import com.example.appchat.databinding.ChatFragmentBinding;
import com.example.appchat.objects.MessageSend;
import com.example.appchat.objects.User;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment {
    ChatFragmentBinding binding;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    List<MessageSend> list;
    MessageAdapter adapter;
    Fragment me = this;
    public static ChatFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putParcelable("user",user);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_fragment,container,false);
        User user = getArguments().getParcelable("user");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        binding.tvUsername.setText(user.getUsername());
        readMessage(firebaseUser.getUid(),user.getId());
        binding.btnSendMessage.setOnClickListener(v->{
            String message = binding.edtMessage.getText().toString().trim();
            if (!message.isEmpty()){
                sendMessage(firebaseUser.getUid(),user.getId(),message);
                binding.edtMessage.setText(null);
                readMessage(firebaseUser.getUid(),user.getId());
            }
        });
        binding.btnBack.setOnClickListener(v->{
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.popBackStackImmediate();
        });

        return binding.getRoot();
    }
    public void sendMessage(String senderId,String receiveId,String message){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("idSender",senderId);
        hm.put("idReceiver",receiveId);
        hm.put("content",message);
        hm.put("isSeen","unseen");
        databaseReference.child("Chats").push().setValue(hm);
    }
    public void readMessage(String myId,String receiveID){
        list = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    MessageSend messageSend = dataSnapshot.getValue(MessageSend.class);
                    if (messageSend.getIdReceiver().equals(myId)&&messageSend.getIdSender().equals(receiveID)||messageSend.getIdReceiver().equals(receiveID)&&messageSend.getIdSender().equals(myId)){
                        list.add(messageSend);
                    }
                }
                adapter = new MessageAdapter(list,getContext());
                binding.rcvChats.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
