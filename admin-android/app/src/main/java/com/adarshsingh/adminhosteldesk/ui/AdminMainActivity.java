package com.adarshsingh.adminhosteldesk.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.adarshsingh.adminhosteldesk.R;
import com.adarshsingh.adminhosteldesk.data.local.SessionManager;
import com.adarshsingh.adminhosteldesk.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityAdminMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);

        setupHeader();
        setupNavigation();

        String role = sessionManager.getRole();
        boolean isStaff = "STAFF".equalsIgnoreCase(role) || "MAINTENANCE_STAFF".equalsIgnoreCase(role);

        if (savedInstanceState == null) {
            if (isStaff) {
                binding.bottomNav.setSelectedItemId(R.id.nav_staff_work);
            } else {
                binding.bottomNav.setSelectedItemId(R.id.nav_warden);
            }
        } else {
            if (isStaff) {
                int selected = binding.bottomNav.getSelectedItemId();
                if (selected != R.id.nav_staff_work && selected != R.id.nav_profile) {
                    binding.bottomNav.setSelectedItemId(R.id.nav_staff_work);
                }
            }
        }
    }

    private void setupHeader() {
        String role = sessionManager.getRole();
        binding.tvRoleBadge.setText(role != null ? role.replace('_', ' ') : "STAFF");

        String hostel = sessionManager.getHostelName();
        String institute = sessionManager.getInstituteName();
        String campus = (hostel != null && !hostel.trim().isEmpty()) ? hostel :
                ((institute != null && !institute.trim().isEmpty()) ? institute : "Campus Facilities");
        String dept = sessionManager.getDepartmentName();
        if (dept != null && !dept.trim().isEmpty()) {
            binding.tvAdminSubtitle.setText(campus + " · " + dept);
        } else {
            binding.tvAdminSubtitle.setText(campus + " · Operations");
        }

        boolean canOnboard = "INSTITUTE_ADMIN".equalsIgnoreCase(role) || "SUPER_ADMIN".equalsIgnoreCase(role);
        binding.btnHeaderOnboard.setVisibility(canOnboard ? android.view.View.VISIBLE : android.view.View.GONE);
        binding.btnHeaderOnboard.setOnClickListener(v -> InstituteConsoleHelper.showInstituteManagementDialog(this));
    }

    private void setupNavigation() {
        String role = sessionManager.getRole();
        boolean isInstAdmin = "INSTITUTE_ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role) || "SUPER_ADMIN".equalsIgnoreCase(role);
        boolean isStaff = "STAFF".equalsIgnoreCase(role) || "MAINTENANCE_STAFF".equalsIgnoreCase(role);
        boolean isWarden = "WARDEN".equalsIgnoreCase(role);

        android.view.Menu menu = binding.bottomNav.getMenu();
        if (isStaff) {
            menu.removeItem(R.id.nav_warden);
            menu.removeItem(R.id.nav_tickets);
            menu.removeItem(R.id.nav_insights);
            android.view.MenuItem workItem = menu.findItem(R.id.nav_staff_work);
            if (workItem != null) {
                workItem.setTitle("My Work");
                workItem.setIcon(R.drawable.ic_assignment);
                workItem.setVisible(true);
            }
            android.view.MenuItem profileItem = menu.findItem(R.id.nav_profile);
            if (profileItem != null) {
                profileItem.setVisible(true);
            }
        } else if (isWarden) {
            menu.removeItem(R.id.nav_staff_work);
            android.view.MenuItem wardenItem = menu.findItem(R.id.nav_warden);
            if (wardenItem != null) wardenItem.setVisible(true);
            android.view.MenuItem ticketsItem = menu.findItem(R.id.nav_tickets);
            if (ticketsItem != null) ticketsItem.setVisible(true);
            android.view.MenuItem insightsItem = menu.findItem(R.id.nav_insights);
            if (insightsItem != null) insightsItem.setVisible(true);
            android.view.MenuItem profileItem = menu.findItem(R.id.nav_profile);
            if (profileItem != null) profileItem.setVisible(true);
        } else {
            android.view.MenuItem workItem = menu.findItem(R.id.nav_staff_work);
            if (workItem != null) {
                workItem.setTitle("Directory");
                workItem.setIcon(R.drawable.ic_domain);
                workItem.setVisible(true);
            }
            android.view.MenuItem wardenItem = menu.findItem(R.id.nav_warden);
            if (wardenItem != null) wardenItem.setVisible(true);
            android.view.MenuItem ticketsItem = menu.findItem(R.id.nav_tickets);
            if (ticketsItem != null) ticketsItem.setVisible(true);
            android.view.MenuItem insightsItem = menu.findItem(R.id.nav_insights);
            if (insightsItem != null) insightsItem.setVisible(true);
            android.view.MenuItem profileItem = menu.findItem(R.id.nav_profile);
            if (profileItem != null) profileItem.setVisible(true);
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_warden) {
                selectedFragment = new WardenOverviewFragment();
            } else if (id == R.id.nav_tickets) {
                selectedFragment = new AllTicketsFragment();
            } else if (id == R.id.nav_staff_work) {
                selectedFragment = isInstAdmin ? new InstituteOperationsFragment() : new StaffWorkFragment();
            } else if (id == R.id.nav_insights) {
                selectedFragment = new InsightsFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new AdminProfileFragment();
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
