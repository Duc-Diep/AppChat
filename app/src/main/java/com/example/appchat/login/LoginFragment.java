package com.example.appchat.login;

import android.content.Intent;
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
import com.example.appchat.activity.ChatActivity;
import com.example.appchat.databinding.LoginFragmentBinding;
import com.example.appchat.event.EventCloseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

public class LoginFragment extends Fragment {
    LoginFragmentBinding binding;
    FirebaseAuth mAuth;

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment,container,false);
        binding.btnRegis.setOnClickListener(v -> {
            Fragment fragment = RegisterFragment.newInstance();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right).replace(R.id.layout_login,fragment).addToBackStack(null).commit();
        });
        mAuth = FirebaseAuth.getInstance();
        binding.btnLogin.setOnClickListener(v->signIn());
        binding.txtForgot.setOnClickListener(v->{
            Fragment fragment = ForgotPassFragment.newInstance();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right).replace(R.id.layout_login,fragment).addToBackStack(null).commit();
        });
        return binding.getRoot();
    }
    public void signIn(){
        String email = binding.edtEmail.getText().toString().trim();
        String password = binding.edtPass.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.edtEmail.setError("Incorrect format email");
            binding.edtEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            binding.edtPass.setError("Password must not empty");
            binding.edtPass.requestFocus();
            return;
        }
        binding.progessBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()){
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        startActivity(intent);
                        EventBus.getDefault().post(new EventCloseActivity());
                        binding.progessBar.setVisibility(View.GONE);
                    }else{
                        user.sendEmailVerification();
                        binding.progessBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Please check your email to verify your account", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getContext(), "Failed to login! Please check your credentials", Toast.LENGTH_SHORT).show();
                    binding.progessBar.setVisibility(View.GONE);
                }
            }
        });
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
