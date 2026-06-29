package com.rosati.weighttracking;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighttracking.R;
import com.example.weighttracking.databinding.FragmentWeightTrackingBinding;

import java.util.ArrayList;
import java.util.List;

public class WeightTrackingFragment extends Fragment {

    private FragmentWeightTrackingBinding binding;
    private AppDatabase db;
    private String username;
    private WeightEntryAdapter adapter;
    private final List<WeightEntry> weightEntries = new ArrayList<>();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentWeightTrackingBinding.inflate(inflater, container, false);
        db = AppDatabase.getDatabase(requireContext());
        
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader();
        setupRecyclerView();

        binding.buttonLogout.setOnClickListener(v ->
                NavHostFragment.findNavController(WeightTrackingFragment.this)
                        .navigate(R.id.action_WeightTrackingFragment_to_LoginFragment)
        );

        binding.fabAddWeight.setOnClickListener(v -> showAddWeightDialog());
        binding.buttonEditGoal.setOnClickListener(v -> showEditGoalDialog());

        // Check if goal weight is not set (is 0) and prompt user
        new Thread(() -> {
            User user = db.userDao().findByUsername(username);
            if (user != null && user.goalWeight <= 0) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::showSetGoalDialog);
                }
            }
        }).start();
    }

    private void showSetGoalDialog() {
        showGoalDialog(getString(R.string.set_goal_weight_title), getString(R.string.set_goal_weight_message));
    }

    private void showEditGoalDialog() {
        showGoalDialog(getString(R.string.change_goal_weight_title), getString(R.string.change_goal_weight_message));
    }

    private void showGoalDialog(String title, String message) {
        EditText input = new EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        android.widget.FrameLayout container = new android.widget.FrameLayout(requireContext());
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_dialog_padding_material);
        params.rightMargin = getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_dialog_padding_material);
        input.setLayoutParams(params);
        container.addView(input);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setView(container)
                .setPositiveButton(R.string.update, (dialog, which) -> {
                    String goalStr = input.getText().toString();
                    if (!goalStr.isEmpty()) {
                        try {
                            int newGoal = Integer.parseInt(goalStr);
                            updateGoalWeight(newGoal);
                        } catch (NumberFormatException e) {
                            Toast.makeText(requireContext(), R.string.invalid_weight, Toast.LENGTH_SHORT).show();
                            if (title.equals(getString(R.string.set_goal_weight_title))) showSetGoalDialog();
                        }
                    } else if (title.equals(getString(R.string.set_goal_weight_title))) {
                        Toast.makeText(requireContext(), R.string.goal_weight_required, Toast.LENGTH_SHORT).show();
                        showSetGoalDialog();
                    }
                });

        if (title.equals(getString(R.string.set_goal_weight_title))) {
            builder.setCancelable(false);
        } else {
            builder.setNegativeButton(R.string.cancel, null);
        }

        builder.show();
    }

    private void setupHeader() {
        new Thread(() -> {
            User user = db.userDao().findByUsername(username);
            if (user != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    String header = getString(R.string.goal_header_format, user.username, user.goalWeight);
                    binding.headerText.setText(header);
                });
            }
        }).start();
    }

    private void setupRecyclerView() {
        binding.weightRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new WeightEntryAdapter(weightEntries, this::deleteWeightEntry);
        binding.weightRecyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                WeightEntry entry = weightEntries.get(position);
                deleteWeightEntry(entry);
            }
        }).attachToRecyclerView(binding.weightRecyclerView);

        refreshWeightEntries();
    }

    private void deleteWeightEntry(WeightEntry entry) {
        new Thread(() -> {
            db.weightEntryDao().delete(entry);
            refreshWeightEntries();
        }).start();
    }

    private void refreshWeightEntries() {
        new Thread(() -> {
            List<WeightEntry> entries = db.weightEntryDao().getWeightEntriesForUser(username);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    weightEntries.clear();
                    weightEntries.addAll(entries);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private void showAddWeightDialog() {
        EditText input = new EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Add padding to the EditText to prevent overlap issues
        android.widget.FrameLayout container = new android.widget.FrameLayout(requireContext());
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_dialog_padding_material);
        params.rightMargin = getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_dialog_padding_material);
        input.setLayoutParams(params);
        container.addView(input);
        
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_weight_title)
                .setMessage(R.string.add_weight_message)
                .setView(container)
                .setPositiveButton(R.string.add, (dialog, which) -> {
                    String weightStr = input.getText().toString();
                    if (!weightStr.isEmpty()) {
                        try {
                            float weight = Float.parseFloat(weightStr);
                            saveWeightEntry(weight);
                        } catch (NumberFormatException e) {
                            Toast.makeText(requireContext(), R.string.invalid_weight, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void saveWeightEntry(float weight) {
        new Thread(() -> {
            WeightEntry entry = new WeightEntry(username, weight, System.currentTimeMillis());
            db.weightEntryDao().insert(entry);

            User user = db.userDao().findByUsername(username);
            if (user != null && weight <= user.goalWeight) {
                checkSmsPermissionAndSend(user.phoneNumber);
            }

            refreshWeightEntries();
        }).start();
    }

    private void checkSmsPermissionAndSend(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            sendSms(phoneNumber);
        } else {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), R.string.congratulations_goal, Toast.LENGTH_LONG).show());
            }
        }
    }

    private void sendSms(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return;
        try {
            SmsManager smsManager = requireContext().getSystemService(SmsManager.class);
            smsManager.sendTextMessage(phoneNumber, null,
                    getString(R.string.congratulations_goal), null, null);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), R.string.sms_sent, Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), R.string.sms_failed, Toast.LENGTH_SHORT).show());
            }
            e.printStackTrace();
        }
    }

    private void updateGoalWeight(int newGoal) {
        new Thread(() -> {
            db.userDao().updateGoalWeight(username, newGoal);
            setupHeader();
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
