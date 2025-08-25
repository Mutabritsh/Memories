package com.example.memories;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class AddFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    Button chooseImageButton, getlocationButton, saveButton;
    EditText title, description;
    ImageView memoryImageView;
    TextView locationTextView;
    private LocationViewModel viewModel;
    private DatabaseHelper databaseHelper;


    private void showAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1 // just the first match
            );

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressString = address.getAddressLine(0); // full address
                locationTextView.setText(addressString);
            } else {
                locationTextView.setText("Address not found");
            }

        } catch (IOException e) {
            e.printStackTrace();
            locationTextView.setText("Unable to get address");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // DB is now open


        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        chooseImageButton = view.findViewById(R.id.chooseImageButton);
        getlocationButton = view.findViewById(R.id.getlocationButton);
        saveButton = view.findViewById(R.id.savememoryButton);
        memoryImageView = view.findViewById(R.id.memoryImageView);
        locationTextView = view.findViewById(R.id.location);

        databaseHelper = new DatabaseHelper(getActivity());

        viewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        getlocationButton.setOnClickListener(v -> {
            Location location = viewModel.getLocation().getValue();
            if (location != null) {
                locationTextView.setText("Lat: " + location.getLatitude() + "\nLng: " + location.getLongitude());
            } else {
                locationTextView.setText("Location not available");
            }
        });

        getlocationButton.setOnClickListener(v -> {
            Location location = viewModel.getLocation().getValue();
            if (location != null) {
                showAddressFromLocation(location);
            } else {
                locationTextView.setText("Location not available");
            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                resultLauncher.launch(intent);
            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getTitle = title.getText().toString().trim();
                String getDescription = description.getText().toString().trim();
                String getLocation = locationTextView.getText().toString().trim();

                if (getTitle.isEmpty() || getDescription.isEmpty() || getLocation.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get Bitmap from ImageView
                Bitmap bitmap = null;
                if (memoryImageView.getDrawable() != null) {
                    memoryImageView.setDrawingCacheEnabled(true);
                    memoryImageView.buildDrawingCache();
                    bitmap = Bitmap.createBitmap(memoryImageView.getDrawingCache());
                    memoryImageView.setDrawingCacheEnabled(false);
                }

                // Save into database
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                dbHelper.insertMemory(getTitle, getDescription, getLocation, bitmap);

                Toast.makeText(getActivity(), "Memory Saved!", Toast.LENGTH_SHORT).show();

                // clear inputs
                title.setText("");
                description.setText("");
                locationTextView.setText("");
                memoryImageView.setImageResource(0); // clear image


            }
        });


        return view;
    }
    private Bitmap handleImageOrientation(Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);

        ExifInterface exif = new ExifInterface(requireActivity().getContentResolver().openInputStream(imageUri));
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        int rotation = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
        }

        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }

    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        // Fix orientation before setting to ImageView
                        Bitmap bitmap = handleImageOrientation(imageUri);
                        memoryImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

//    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                    Uri imageUri = result.getData().getData();
//                    try {
//                        // Convert URI to Bitmap
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
//                        // Set bitmap to ImageView
//                        memoryImageView.setImageBitmap(bitmap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//    );


        
    }

