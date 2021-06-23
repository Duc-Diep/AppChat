package com.example.appchat.login;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.appchat.R;
import com.example.appchat.objects.User;
import com.example.appchat.databinding.RegistrationFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment {
    RegistrationFragmentBinding binding;
    FirebaseAuth mAth;
    public static RegisterFragment newInstance() {

        Bundle args = new Bundle();

        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.registration_fragment,container,false);
        mAth =FirebaseAuth.getInstance();
        binding.btnRegister.setOnClickListener(v->registerUser());
        binding.txtLogin.setOnClickListener(v -> {
            Fragment fragment = LoginFragment.newInstance();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right).replace(R.id.layout_login,fragment).addToBackStack(null).commit();
        });
        return binding.getRoot();
    }
    public void registerUser(){
        String email = binding.edtEmail.getText().toString().trim();
        String pass = binding.edtPass.getText().toString().trim();
        String repass = binding.edtRePass.getText().toString().trim();
        String username = binding.edtUsername.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.edtEmail.setError("Incorrect format email");
            binding.edtEmail.requestFocus();
            return;
        }
        if (pass.length()<6){
            binding.edtPass.setError("Password must not be less than 6 characters");
            binding.edtPass.requestFocus();
            return;
        }
        if (!repass.equals(pass)){
            binding.edtRePass.setError("Confirm password must be the same");
            binding.edtRePass.requestFocus();
            return;
        }
        if (username.isEmpty()){
            binding.edtUsername.setError("Username must not empty");
            binding.edtUsername.requestFocus();
            return;
        }
        binding.progessBar.setVisibility(View.VISIBLE);

        mAth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = mAth.getCurrentUser();
                    User user = new User(firebaseUser.getUid(),username,"","Offline");
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(), "User has been registerd successfully", Toast.LENGTH_SHORT).show();
                                binding.progessBar.setVisibility(View.GONE);
                            }else{
                                Toast.makeText(getContext(), "Failed to register! Try again", Toast.LENGTH_SHORT).show();
                                binding.progessBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "Failed to register", Toast.LENGTH_SHORT).show();
                    binding.progessBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
