package com.example.memories;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;



import android.net.Uri;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class DetailsFragment extends Fragment {

    private Bitmap currentBitmap; // store bitmap for popup
    public static byte[] imageToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        TextView title = view.findViewById(R.id.detailTitle);
        TextView description = view.findViewById(R.id.detailDescription);
        TextView location = view.findViewById(R.id.detailLocation);
        ImageView image = view.findViewById(R.id.detailImage);

        Bundle bundle = getArguments();
        String loc = null;

        if (bundle != null) {
            title.setText(bundle.getString("title"));
            description.setText(bundle.getString("description"));
            loc = bundle.getString("location");
            location.setText(loc);

            byte[] imageBytes = bundle.getByteArray("image");
            if (imageBytes != null) {
                currentBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                image.setImageBitmap(currentBitmap);
            }
        }

        // Click listener for location text
        String finalLoc = loc;
        location.setOnClickListener(v -> {
            if (finalLoc != null && !finalLoc.isEmpty()) {
                Uri geoUri;
                if (finalLoc.matches("-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?")) {
                    // latitude,longitude format
                    geoUri = Uri.parse("geo:" + finalLoc + "?q=" + finalLoc);
                } else {
                    // Address string
                    geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(finalLoc));
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // fallback to any available maps app
                    startActivity(new Intent(Intent.ACTION_VIEW, geoUri));
                }
            } else {
                Toast.makeText(getContext(), "No location available", Toast.LENGTH_SHORT).show();
            }
        });

        // Click listener for image popup
        image.setOnClickListener(v -> {
            if (currentBitmap != null) {
                showImagePopup(currentBitmap);
            }
        });

        return view;
    }

    // Show image in popup box
    private void showImagePopup(Bitmap bitmap) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ImageView imageView = new ImageView(requireContext());
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        dialog.setContentView(imageView);

        // close when i tap the image
        imageView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        LocationFragment locationFragment = new LocationFragment();
        Bundle bundle = new Bundle();
        bundle.putByteArray("image", currentBitmap != null ? imageToBytes(currentBitmap) : null);
        locationFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, locationFragment)
                .addToBackStack(null)
                .commit();
    }
}



