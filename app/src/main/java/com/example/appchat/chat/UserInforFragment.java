package com.example.appchat.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.activity.MainActivity;
import com.example.appchat.databinding.UserinforFragmentBinding;
import com.example.appchat.event.EventCloseActivity;
import com.example.appchat.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class UserInforFragment extends Fragment {
    UserinforFragmentBinding binding;
    User user;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference data;
    Bitmap bitmap;
    Uri filepath;
    UploadTask uploadTask;
    public static UserInforFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putParcelable("user",user);
        UserInforFragment fragment = new UserInforFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.userinfor_fragment,container,false);
        user = getArguments().getParcelable("user");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(getContext()).load(user.getImageurl()).into(binding.imgAvatarUser);
        binding.edtUsernameInfor.setText(user.getUsername());
        binding.txtEmail.setText(firebaseUser.getEmail());
        binding.btnEdit.setOnClickListener(v->{
            binding.edtUsernameInfor.setEnabled(true);
            binding.btnEdit.setColorFilter(Color.YELLOW);
        });
        binding.layoutUserInfo.setOnClickListener(v->{
            binding.edtUsernameInfor.setEnabled(false);
            binding.btnEdit.setColorFilter(Color.BLUE);
            changeName();
        });
        binding.btnBack.setColorFilter(Color.BLUE);
        binding.btnBack.setOnClickListener(v->{
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.popBackStackImmediate();
        });
        binding.btnSignOut.setOnClickListener(v->signOut());
        binding.imgChooseImg.setOnClickListener(v->{chooseImage();});
        return binding.getRoot();
    }

    private void changeName() {
        data  = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("username",binding.edtUsernameInfor.getText().toString());
        data.updateChildren(hm);
    }

    private void chooseImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    if (data==null){
                        Toast.makeText(getContext(), "Choose image failed", Toast.LENGTH_SHORT).show();
                            return;
                    }else  {
                        try {
                            filepath = data.getData();
                            InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                            inputStream = getContext().getContentResolver().openInputStream(filepath);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            inputStream.close();
                            uploadFile(bitmap);
                        }catch (Exception e){
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        }
    }

    public void signOut(){
        setStatus("Offline");
        mAuth.signOut();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        EventBus.getDefault().post(new EventCloseActivity());
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
    //up ảnh bằng bitmap
    private void uploadFile(Bitmap bitmap) {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Uploading Image");
        dialog.show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageref = storage.getReference().child("images/" + firebaseUser.getUid() + ".jpg");
        uploadTask =imageref.putFile(filepath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        setImageurl(uri);
                        Log.d("TAG", "onSuccess: "+uri);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float percent = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                dialog.setMessage("Uploaded: " + (int)percent + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Fail to upload", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void setImageurl(Uri uri){
        data  = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("imageurl",uri.toString());
        data.updateChildren(hm);
        Glide.with(getContext()).load(uri.toString()).into(binding.imgAvatarUser);
    }
}
