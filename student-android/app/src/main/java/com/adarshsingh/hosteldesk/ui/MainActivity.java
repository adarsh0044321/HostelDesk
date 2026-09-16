package com.adarshsingh.hosteldesk.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.adarshsingh.hosteldesk.R;
import com.adarshsingh.hosteldesk.data.local.SessionManager;
import com.adarshsingh.hosteldesk.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);
        setupHeader();
        setupNavigation();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void setupHeader() {
        String institute = sessionManager.getInstituteName();
        String hostel = sessionManager.getHostelName();
        String room = sessionManager.getRoomNumber();

        if (hostel != null && !hostel.isEmpty()) {
            binding.tvHeaderSubtitle.setText(hostel);
        } else if (institute != null && !institute.isEmpty()) {
            binding.tvHeaderSubtitle.setText(institute);
        } else {
            binding.tvHeaderSubtitle.setText("Campus Residence");
        }

        if (room != null && !room.isEmpty()) {
            binding.tvHeaderRoom.setText(room.startsWith("Room") ? room : "Room " + room);
        } else {
            binding.tvHeaderRoom.setText("Resident");
        }

        String name = sessionManager.getFullName();
        if (name != null && !name.trim().isEmpty()) {
            String clean = name.trim();
            binding.tvHeaderAvatar.setText(clean.substring(0, 1).toUpperCase());
        } else {
            binding.tvHeaderAvatar.setText("R");
        }

        binding.btnHeaderProfile.setOnClickListener(v -> {
            binding.bottomNav.setSelectedItemId(R.id.nav_profile);
        });
    }

    private void setupNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_issues) {
                selectedFragment = new IssuesFragment();
            } else if (id == R.id.nav_report) {
                startActivity(new android.content.Intent(this, ReportIssueActivity.class));
                return false;
            } else if (id == R.id.nav_alerts) {
                selectedFragment = new AlertsFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    public void switchToTab(int navItemId) {
        binding.bottomNav.setSelectedItemId(navItemId);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
