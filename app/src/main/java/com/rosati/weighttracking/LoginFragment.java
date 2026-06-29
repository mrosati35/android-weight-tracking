package com.rosati.weighttracking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.weighttracking.R;
import com.example.weighttracking.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AppDatabase db;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        db = AppDatabase.getDatabase(requireContext());
        
        // Populate default user for testing if needed
        new Thread(() -> {
            if (db.userDao().login("admin", "admin") == null) {
                db.userDao().insert(new User("admin", "admin", 180, "5551234"));
            }
        }).start();

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(v -> {
            String username = binding.usernameEditText.getText() != null ? 
                    binding.usernameEditText.getText().toString() : "";
            String password = binding.passwordEditText.getText() != null ? 
                    binding.passwordEditText.getText().toString() : "";

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), R.string.error_fill_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            User user = db.userDao().login(username, password);
            if (user != null) {
                Bundle args = new Bundle();
                args.putString("username", username);
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_LoginFragment_to_WeightTrackingFragment, args);
            } else {
                Toast.makeText(requireContext(), R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonCreateAccount.setOnClickListener(v ->
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_LoginFragment_to_CreateAccountFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
