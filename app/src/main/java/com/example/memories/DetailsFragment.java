package com.example.memories;

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
import android.widget.ImageView;
import android.widget.TextView;



import android.net.Uri;
import android.widget.Toast;


public class DetailsFragment extends Fragment {

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
        if (bundle != null) {
            title.setText(bundle.getString("title"));
            description.setText(bundle.getString("description"));
            location.setText(bundle.getString("location"));

            byte[] imageBytes = bundle.getByteArray("image");
            if (imageBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                image.setImageBitmap(bitmap);
            }
        }

        // ---- Add this part for clickable location ----
        // ---- Click listener for location text AND image ----
        String loc = bundle.getString("location");
        View.OnClickListener openMapListener = v -> {
            if (loc != null && !loc.isEmpty()) {
                Uri geoUri;
                if (loc.matches("-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?")) {
                    // lat,long
                    geoUri = Uri.parse("geo:" + loc + "?q=" + loc);
                } else {
                    // Address string
                    geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(loc));
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "No Maps app found", Toast.LENGTH_SHORT).show();
                }
            }
        };

        location.setOnClickListener(openMapListener);
        image.setOnClickListener(openMapListener); // same action for image
        // -----------------------------------------------


        return view;
    }
}

