package com.example.appchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.R;
import com.example.appchat.objects.MessageSend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_LEFT_TYPE = 0 ;
    public static final int MSG_RIGHT_TYPE = 1 ;
    List<MessageSend> list;
    Context context;
    FirebaseUser firebaseUser;

    public MessageAdapter(List<MessageSend> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_LEFT_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.left_message_item,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.right_message_item,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        MessageSend messageSend = list.get(position);
        holder.tvMessage.setText(messageSend.getContent());
        if (holder.getItemViewType()==MSG_RIGHT_TYPE){
            if (messageSend.getIsSeen().equals("seen")){
                holder.tvSeen.setVisibility(View.VISIBLE);
            }else{
                holder.tvSeen.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        TextView tvMessage,tvSeen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatarMess);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvSeen = itemView.findViewById(R.id.tvStatusSeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).getIdSender().equals(firebaseUser.getUid())){
            return MSG_RIGHT_TYPE;
        }else{
            return MSG_LEFT_TYPE;
        }

    }
}
