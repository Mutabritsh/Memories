package com.example.memories;

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

        return view;
    }
}

