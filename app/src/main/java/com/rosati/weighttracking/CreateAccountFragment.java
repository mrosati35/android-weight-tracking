package com.rosati.weighttracking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.weighttracking.R;
import com.example.weighttracking.databinding.FragmentCreateAccountBinding;

public class CreateAccountFragment extends Fragment {

    private FragmentCreateAccountBinding binding;
    private AppDatabase db;
    private boolean hasSmsPermission;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false);
        db = AppDatabase.getDatabase(requireContext());
        
        hasSmsPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) 
                == PackageManager.PERMISSION_GRANTED;
        
        if (!hasSmsPermission) {
            binding.newPhoneLayout.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonRegister.setOnClickListener(v -> {
            String username = binding.newUsernameEditText.getText() != null ? 
                    binding.newUsernameEditText.getText().toString() : "";
            String password = binding.newPasswordEditText.getText() != null ? 
                    binding.newPasswordEditText.getText().toString() : "";
            
            final String phoneNumber;
            if (hasSmsPermission) {
                phoneNumber = binding.newPhoneEditText.getText() != null ?
                        binding.newPhoneEditText.getText().toString() : "";
            } else {
                phoneNumber = "";
            }

            if (username.isEmpty() || password.isEmpty() || (hasSmsPermission && phoneNumber.isEmpty())) {
                Toast.makeText(requireContext(), R.string.error_fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                if (db.userDao().findByUsername(username) != null) {
                   if (getActivity() != null) {
                       getActivity().runOnUiThread(() -> 
                           Toast.makeText(requireContext(), R.string.user_exists, Toast.LENGTH_SHORT).show());
                   }
                   return;
                }
                
                // Initialize with 0 goal weight; user will set it via dialog in WeightTrackingFragment
                db.userDao().insert(new User(username, password, 0, phoneNumber));
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), R.string.account_created, Toast.LENGTH_SHORT).show();
                        Bundle args = new Bundle();
                        args.putString("username", username);
                        NavHostFragment.findNavController(CreateAccountFragment.this)
                                .navigate(R.id.action_CreateAccountFragment_to_WeightTrackingFragment, args);
                    });
                }
            }).start();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
