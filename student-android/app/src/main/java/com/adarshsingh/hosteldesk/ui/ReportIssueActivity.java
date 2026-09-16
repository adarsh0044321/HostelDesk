package com.adarshsingh.hosteldesk.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.adarshsingh.hosteldesk.data.api.ApiClient;
import com.adarshsingh.hosteldesk.data.local.SessionManager;
import com.adarshsingh.hosteldesk.data.model.IssueDetailDto;
import com.adarshsingh.hosteldesk.databinding.ActivityReportIssueBinding;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportIssueActivity extends AppCompatActivity {

    private ActivityReportIssueBinding binding;
    private SessionManager sessionManager;
    private File selectedPhotoFile;
    private String currentPhotoPath;
    private Uri cameraImageUri;

    private final String[] CATEGORIES = new String[]{
            "Auto-Detect / General",
            "Carpentry & Furniture",
            "Plumbing & Sanitation",
            "Electrical & Lighting",
            "Internet & Wi-Fi",
            "Pest Control & Cleanliness",
            "Appliance & Air Conditioning"
    };

    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> pickGalleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportIssueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);

        setupToolbar();
        setupCategories();
        setupLocationInfo();
        setupPhotoPickers();

        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString("saved_photo_path");
            String uriStr = savedInstanceState.getString("saved_camera_uri");
            if (uriStr != null) {
                cameraImageUri = Uri.parse(uriStr);
            }
            if (currentPhotoPath != null) {
                File f = new File(currentPhotoPath);
                if (f.exists()) {
                    selectedPhotoFile = f;
                    showPhotoPreview(Uri.fromFile(f));
                }
            }
        }

        binding.btnSubmitIssue.setOnClickListener(v -> submitIssue());

        if (getIntent().getBooleanExtra("priority_urgent", false)) {
            binding.etTitle.setHint("e.g. Urgent electrical spark / water pipe burst");
        }

        if (getIntent().getBooleanExtra("auto_launch_camera", false) && savedInstanceState == null) {
            launchCamera();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentPhotoPath != null) {
            outState.putString("saved_photo_path", currentPhotoPath);
        }
        if (cameraImageUri != null) {
            outState.putString("saved_camera_uri", cameraImageUri.toString());
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupCategories() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                CATEGORIES
        );
        binding.autoCompleteCategory.setAdapter(adapter);
        binding.autoCompleteCategory.setText(CATEGORIES[0], false);
    }

    private void setupLocationInfo() {
        String hostel = sessionManager.getHostelName();
        String room = sessionManager.getRoomNumber();
        String institute = sessionManager.getInstituteName();

        String loc = "";
        if (hostel != null && !hostel.isEmpty()) {
            loc = hostel;
        } else if (institute != null && !institute.isEmpty()) {
            loc = institute;
        } else {
            loc = "Campus Residence";
        }

        if (room != null && !room.isEmpty()) {
            loc += " · " + (room.startsWith("Room") ? room : "Room " + room);
        } else {
            loc += " · Room unassigned";
        }
        binding.tvLocationInfo.setText(loc);
    }

    private void setupPhotoPickers() {
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        doLaunchCamera();
                    } else {
                        Toast.makeText(this, "Camera permission is required to capture photos", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && selectedPhotoFile != null && selectedPhotoFile.exists() && selectedPhotoFile.length() > 0) {
                        showPhotoPreview(Uri.fromFile(selectedPhotoFile));
                    } else {
                        if (selectedPhotoFile != null && selectedPhotoFile.length() == 0) {
                            selectedPhotoFile.delete();
                            selectedPhotoFile = null;
                            currentPhotoPath = null;
                        }
                    }
                }
        );

        pickGalleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        processGalleryUriInBackground(uri);
                    }
                }
        );

        binding.btnCapturePhoto.setOnClickListener(v -> launchCamera());
        binding.btnPickGallery.setOnClickListener(v -> pickGalleryLauncher.launch("image/*"));
        binding.btnRemovePhoto.setOnClickListener(v -> removePhoto());
    }

    private void launchCamera() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this, "Camera is not available on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            doLaunchCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void doLaunchCamera() {
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (storageDir == null) {
                storageDir = getCacheDir();
            }
            File photoFile = new File(storageDir, "hostel_issue_" + System.currentTimeMillis() + ".jpg");
            selectedPhotoFile = photoFile;
            currentPhotoPath = photoFile.getAbsolutePath();
            cameraImageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );
            takePictureLauncher.launch(cameraImageUri);
        } catch (Exception e) {
            Toast.makeText(this, "Could not launch camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void processGalleryUriInBackground(Uri uri) {
        setLoading(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
                boundsOptions.inJustDecodeBounds = true;
                try (InputStream in = getContentResolver().openInputStream(uri)) {
                    BitmapFactory.decodeStream(in, null, boundsOptions);
                }

                int maxDimension = Math.max(boundsOptions.outWidth, boundsOptions.outHeight);
                int inSampleSize = 1;
                while (maxDimension / inSampleSize > 1920) {
                    inSampleSize *= 2;
                }

                BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
                decodeOptions.inSampleSize = inSampleSize;

                Bitmap bitmap;
                try (InputStream in = getContentResolver().openInputStream(uri)) {
                    bitmap = BitmapFactory.decodeStream(in, null, decodeOptions);
                }

                if (bitmap == null) {
                    runOnUiThread(() -> {
                        setLoading(false);
                        Toast.makeText(ReportIssueActivity.this, "Unable to read selected image", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                File outFile = new File(getCacheDir(), "gallery_" + System.currentTimeMillis() + ".jpg");
                try (OutputStream out = new FileOutputStream(outFile)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
                }
                bitmap.recycle();

                runOnUiThread(() -> {
                    setLoading(false);
                    selectedPhotoFile = outFile;
                    currentPhotoPath = outFile.getAbsolutePath();
                    showPhotoPreview(Uri.fromFile(outFile));
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(ReportIssueActivity.this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showPhotoPreview(Uri uri) {
        binding.cardPhotoPreview.setVisibility(View.VISIBLE);
        Glide.with(this).load(uri).into(binding.ivPhotoPreview);
    }

    private void removePhoto() {
        if (selectedPhotoFile != null && selectedPhotoFile.exists()) {
            selectedPhotoFile.delete();
        }
        selectedPhotoFile = null;
        currentPhotoPath = null;
        cameraImageUri = null;
        binding.cardPhotoPreview.setVisibility(View.GONE);
        binding.ivPhotoPreview.setImageDrawable(null);
    }

    private void submitIssue() {
        String title = binding.etTitle.getText() != null ? binding.etTitle.getText().toString().trim() : "";
        String description = binding.etDescription.getText() != null ? binding.etDescription.getText().toString().trim() : "";
        String category = binding.autoCompleteCategory.getText() != null ? binding.autoCompleteCategory.getText().toString().trim() : "General Maintenance";

        if (title.isEmpty()) {
            binding.tilTitle.setError("Please enter a short summary of the issue");
            binding.etTitle.requestFocus();
            return;
        } else {
            binding.tilTitle.setError(null);
        }

        if (description.isEmpty()) {
            binding.tilDescription.setError("Please describe the issue details");
            binding.etDescription.requestFocus();
            return;
        } else {
            binding.tilDescription.setError(null);
        }

        setLoading(true);

        String catToSend = category;
        if (catToSend != null && (catToSend.contains("Auto-Detect") || catToSend.contains("General"))) {
            catToSend = "GENERAL";
        }
        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descPart = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody catPart = RequestBody.create(MediaType.parse("text/plain"), catToSend);
        RequestBody blockPart = RequestBody.create(MediaType.parse("text/plain"), "Main Block");
        RequestBody roomPart = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getRoomNumber() != null ? sessionManager.getRoomNumber() : "Room 101");

        String priorityVal = getIntent().getBooleanExtra("priority_urgent", false) ? "URGENT" : null;
        RequestBody priorityPart = priorityVal != null ? RequestBody.create(MediaType.parse("text/plain"), priorityVal) : null;

        MultipartBody.Part attachmentPart = null;
        if (selectedPhotoFile != null && selectedPhotoFile.exists()) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), selectedPhotoFile);
            attachmentPart = MultipartBody.Part.createFormData("attachment", selectedPhotoFile.getName(), fileBody);
        }

        ApiClient.getApiService(this).createIssue(
                titlePart,
                descPart,
                catPart,
                blockPart,
                roomPart,
                priorityPart,
                attachmentPart
        ).enqueue(new Callback<IssueDetailDto>() {
            @Override
            public void onResponse(Call<IssueDetailDto> call, Response<IssueDetailDto> response) {
                setLoading(false);
                if (response.code() == 401) {
                    Toast.makeText(ReportIssueActivity.this, "Session expired. Please sign in again.", Toast.LENGTH_LONG).show();
                    sessionManager.clearSession();
                    Intent intent = new Intent(ReportIssueActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    IssueDetailDto issue = response.body();
                    Intent intent = new Intent(ReportIssueActivity.this, AiPreviewActivity.class);
                    intent.putExtra("issue_id", issue.getId());
                    intent.putExtra("ticket_number", issue.getTicketNumber());
                    intent.putExtra("title", issue.getTitle());
                    intent.putExtra("department", issue.getAssignedDepartmentName());
                    intent.putExtra("priority", issue.getPriority());

                    if (issue.getAiAnalysis() != null) {
                        intent.putExtra("ai_summary", issue.getAiAnalysis().getSummary());
                        intent.putExtra("urgency_reason", issue.getAiAnalysis().getUrgencyReason());
                    }

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ReportIssueActivity.this, "Failed to submit issue (" + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<IssueDetailDto> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ReportIssueActivity.this, "Error submitting issue: " + (t.getMessage() != null ? t.getMessage() : "Network failure"), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.btnSubmitIssue.setEnabled(!loading);
        binding.progressBarSubmit.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
