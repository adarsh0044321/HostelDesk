package com.adarshsingh.adminhosteldesk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.adarshsingh.adminhosteldesk.R;
import com.adarshsingh.adminhosteldesk.data.api.ApiClient;
import com.adarshsingh.adminhosteldesk.data.local.SessionManager;
import com.adarshsingh.adminhosteldesk.data.model.CrewWorkloadDto;
import com.adarshsingh.adminhosteldesk.data.model.HostelDto;
import com.adarshsingh.adminhosteldesk.data.model.UserDto;
import com.adarshsingh.adminhosteldesk.databinding.FragmentInstituteOperationsBinding;
import com.adarshsingh.adminhosteldesk.ui.adapter.DirectoryAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstituteOperationsFragment extends Fragment {

    private FragmentInstituteOperationsBinding binding;
    private DirectoryAdapter adapter;
    private DirectoryAdapter.Mode currentMode = DirectoryAdapter.Mode.HOSTELS;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInstituteOperationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = SessionManager.getInstance(requireContext());

        String inst = sessionManager.getInstituteName();
        if (inst != null && !inst.isEmpty()) {
            binding.tvOperationsSubtitle.setText(inst + " · Campus Rolls & Crews");
        }

        setupRecyclerView();
        setupChips();

        binding.swipeRefreshDirectory.setOnRefreshListener(this::fetchCurrentTab);

        binding.btnContextAction.setOnClickListener(v -> handleContextAction());

        fetchCurrentTab();
    }

    private void setupRecyclerView() {
        adapter = new DirectoryAdapter();
        adapter.setOnHostelActionListener(new DirectoryAdapter.OnHostelActionListener() {
            @Override
            public void onAssignWarden(HostelDto hostel) {
                InstituteConsoleHelper.showAssignWardenDialog(requireContext(), hostel.getId(), hostel.getName(), InstituteOperationsFragment.this::fetchCurrentTab);
            }

            @Override
            public void onEditHostel(HostelDto hostel) {
                InstituteConsoleHelper.showEditHostelDialog(requireContext(), hostel, InstituteOperationsFragment.this::fetchCurrentTab);
            }

            @Override
            public void onDeleteHostel(HostelDto hostel) {
                InstituteConsoleHelper.confirmDeleteHostel(requireContext(), hostel, InstituteOperationsFragment.this::fetchCurrentTab);
            }
        });
        binding.rvDirectory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDirectory.setAdapter(adapter);
    }

    private void setupChips() {
        binding.chipGroupDirectory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);

            if (id == R.id.chipHostels) {
                currentMode = DirectoryAdapter.Mode.HOSTELS;
                binding.btnContextAction.setText("+ Add Hostel");
            } else if (id == R.id.chipCrews) {
                currentMode = DirectoryAdapter.Mode.CREWS;
                binding.btnContextAction.setText("+ Staff/Crew");
            } else if (id == R.id.chipStudents) {
                currentMode = DirectoryAdapter.Mode.STUDENTS;
                binding.btnContextAction.setText("+ Add Student");
            } else if (id == R.id.chipStaff) {
                currentMode = DirectoryAdapter.Mode.STAFF;
                binding.btnContextAction.setText("+ Add Warden");
            }

            fetchCurrentTab();
        });

        binding.btnContextAction.setText("+ Add Hostel");
    }

    private void handleContextAction() {
        if (currentMode == DirectoryAdapter.Mode.HOSTELS) {
            InstituteConsoleHelper.showCreateHostelDialog(requireContext(), this::fetchCurrentTab);
        } else if (currentMode == DirectoryAdapter.Mode.CREWS) {
            InstituteConsoleHelper.showOnboardStaffDialog(requireContext(), this::fetchCurrentTab);
        } else if (currentMode == DirectoryAdapter.Mode.STUDENTS) {
            InstituteConsoleHelper.showOnboardStudentDialog(requireContext(), this::fetchCurrentTab);
        } else if (currentMode == DirectoryAdapter.Mode.STAFF) {
            InstituteConsoleHelper.showOnboardWardenDialog(requireContext(), this::fetchCurrentTab);
        }
    }

    private void fetchCurrentTab() {
        binding.swipeRefreshDirectory.setRefreshing(true);

        if (currentMode == DirectoryAdapter.Mode.HOSTELS) {
            fetchHostels();
        } else if (currentMode == DirectoryAdapter.Mode.CREWS) {
            fetchCrews();
        } else if (currentMode == DirectoryAdapter.Mode.STUDENTS) {
            fetchStudents();
        } else if (currentMode == DirectoryAdapter.Mode.STAFF) {
            fetchStaff();
        }
    }

    private void fetchHostels() {
        ApiClient.getApiService(requireContext()).getHostels().enqueue(new Callback<List<HostelDto>>() {
            @Override
            public void onResponse(Call<List<HostelDto>> call, Response<List<HostelDto>> response) {
                if (!isAdded()) return;
                binding.swipeRefreshDirectory.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<HostelDto> list = response.body();
                    adapter.setHostels(list);
                    updateEmptyState(list.isEmpty(), "🏢", "No Hostels Registered",
                            "No residence halls or hostels found. Tap '+ Add Hostel' to configure student buildings.");
                } else {
                    Toast.makeText(requireContext(), "Failed to load hostels (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<HostelDto>> call, Throwable t) {
                if (!isAdded()) return;
                binding.swipeRefreshDirectory.setRefreshing(false);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCrews() {
        ApiClient.getApiService(requireContext()).getCrewWorkloads().enqueue(new Callback<List<CrewWorkloadDto>>() {
            @Override
            public void onResponse(Call<List<CrewWorkloadDto>> call, Response<List<CrewWorkloadDto>> response) {
                if (!isAdded()) return;
                binding.swipeRefreshDirectory.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<CrewWorkloadDto> list = response.body();
                    adapter.setCrews(list);
                    updateEmptyState(list.isEmpty(), "🔧", "No Maintenance Crews",
                            "No facilities crews configured yet. Onboard technicians to track workload distribution.");
                } else {
                    Toast.makeText(requireContext(), "Failed to load crew breakdown (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CrewWorkloadDto>> call, Throwable t) {
                if (!isAdded()) return;
                binding.swipeRefreshDirectory.setRefreshing(false);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStudents() {
        ApiClient.getApiService(requireContext()).getInstituteStudents().enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                if (!isAdded()) return;
                binding.swipeRefreshDirectory.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<UserDto> list = response.body();
                    adapter.setStudents(list);
                    updateEmptyState(list.isEmpty(), "🎓", "No Students Enrolled",
                            "No student resident records found. Tap '+ Add Student' to onboard residents.");
                } else {
                    Toast.makeText(requireContext(), "Failed to load student rolls (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                if (!isAdded()) return;
                binding.swipeRefreshDirectory.setRefreshing(false);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStaff() {
        ApiClient.getApiService(requireContext()).getInstituteWardens().enqueue(new Callback<List<UserDto>>() {
            @Override
            public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                if (!isAdded()) return;

                List<UserDto> allStaff = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    allStaff.addAll(response.body());
                }

                ApiClient.getApiService(requireContext()).getInstituteStaff().enqueue(new Callback<List<UserDto>>() {
                    @Override
                    public void onResponse(Call<List<UserDto>> call2, Response<List<UserDto>> response2) {
                        if (!isAdded()) return;
                        binding.swipeRefreshDirectory.setRefreshing(false);

                        if (response2.isSuccessful() && response2.body() != null) {
                            allStaff.addAll(response2.body());
                        }

                        adapter.setStaff(allStaff);
                        updateEmptyState(allStaff.isEmpty(), "🛡", "No Wardens or Staff",
                                "No campus officers or maintenance staff on record. Use the Add button to onboard staff.");
                    }

                    @Override
                    public void onFailure(Call<List<UserDto>> call2, Throwable t) {
                        if (!isAdded()) return;
                        binding.swipeRefreshDirectory.setRefreshing(false);
                        adapter.setStaff(allStaff);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<UserDto>> call, Throwable t) {
                if (!isAdded()) return;
                binding.swipeRefreshDirectory.setRefreshing(false);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState(boolean isEmpty, String icon, String title, String msg) {
        if (isEmpty) {
            binding.rvDirectory.setVisibility(View.GONE);
            binding.layoutEmptyDirectory.setVisibility(View.VISIBLE);
            binding.tvEmptyIcon.setText(icon);
            binding.tvEmptyTitle.setText(title);
            binding.tvEmptyMessage.setText(msg);
        } else {
            binding.rvDirectory.setVisibility(View.VISIBLE);
            binding.layoutEmptyDirectory.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCurrentTab();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
