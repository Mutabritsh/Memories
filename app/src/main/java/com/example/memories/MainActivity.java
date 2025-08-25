package com.example.memories;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    public static byte[] imageToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.body_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, new HomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);


        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    fragment = new HomeFragment();
                } else if (id == R.id.nav_add) {
                    fragment = new AddFragment();
                } else if (id == R.id.nav_location) {
                    fragment = new LocationFragment();
                } else if (id == R.id.nav_list) {
                    fragment = new ListFragment();
                } else if (id == R.id.nav_details) {
                    fragment = new DetailsFragment();
                }
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();
                    return true;
                }
                return false;
            }
        });
//initialize location fragment
        Fragment locationFragment = new LocationFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.body_container, locationFragment)
                .commit();

    }
}