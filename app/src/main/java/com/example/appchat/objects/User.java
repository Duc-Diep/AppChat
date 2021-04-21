package com.example.appchat.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    String id,username,imageurl,status;

    public User() {
    }

    public User(String id, String username, String imageurl, String status) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.status = status;
    }

    protected User(Parcel in) {
        id = in.readString();
        username = in.readString();
        imageurl = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(imageurl);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
