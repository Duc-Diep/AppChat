package com.example.appchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.objects.User;
import com.example.appchat.event.IOnClickUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    List<User> list;
    Context context;
    IOnClickUser iOnClickUser;

    public void setiOnClickUser(IOnClickUser iOnClickUser) {
        this.iOnClickUser = iOnClickUser;
    }

    public UsersAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        User user = list.get(position);
        holder.tvUsername.setText(user.getUsername());
        if (!user.getImageurl().equals("")){
            Glide.with(context).load(user.getImageurl()).into(holder.imgAvatar);
        }
        if (user.getStatus().equals("Online")){
            holder.imgStatus.setBackgroundResource(R.drawable.is_online);
        }else{
            holder.imgStatus.setBackgroundResource(R.drawable.is_offline);
        }
        holder.itemView.setOnClickListener(v-> iOnClickUser.itemClick(user));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        ImageView imgStatus;
        TextView tvUsername;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatarUserItem);
            imgStatus = itemView.findViewById(R.id.imgStatusItem);
            tvUsername = itemView.findViewById(R.id.tvUsernameItem);
        }
    }
}
