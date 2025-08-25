package com.example.memories;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MemoryAdapter adapter;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(getContext());

        List<Memory> memoryList = new ArrayList<>();

        Cursor cursor = dbHelper.getAllData();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));

                memoryList.add(new Memory(title, description, location, image));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new MemoryAdapter(getContext(), memoryList);
        recyclerView.setAdapter(adapter);

        adapter.setOnMemoryClickListener(memory -> {
            DetailsFragment detailsFragment = new DetailsFragment();

            Bundle bundle = new Bundle();
            bundle.putString("title", memory.getTitle());
            bundle.putString("description", memory.getDescription());
            bundle.putString("location", memory.getLocation());
            bundle.putByteArray("image", memory.getImage());
            detailsFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.body_container, detailsFragment) // use your container id
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);

                if (dy > 0 && bottomNav.isShown()) {
                    bottomNav.animate().translationY(bottomNav.getHeight()).setDuration(200);
                } else if (dy < 0) {
                    bottomNav.animate().translationY(0).setDuration(200);
                }
            }
        });


        return view;
    }
}





