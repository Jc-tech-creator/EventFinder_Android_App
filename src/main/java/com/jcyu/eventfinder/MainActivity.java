package com.jcyu.eventfinder;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jcyu.eventfinder.ui.search.EventDetails.EventDetailsFragment;


public class MainActivity extends AppCompatActivity implements EventDetailsFragment.OnBackArrowClickListener{

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public LinearLayout rootContainer;

    public FrameLayout newContainer;

    public static double Lat;

    public static double Lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_EventFinder);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        rootContainer = findViewById(R.id.root_container);
        newContainer = findViewById(R.id.new_container);

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle()));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("SEARCH");
                    break;
                case 1:
                    tab.setText("FAVORITES");
                    break;
            }
        }
        );
        tabLayoutMediator.attach();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // the user location permission logic is copied from given online tutorial
        // Check for location permission and request it if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchLocation();
        }
        //reset visibility when go back from eventDetail page
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.d("MainActivity", "BackStack changed");
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    Log.d("MainActivity", "getBackStackEntryCount() == 0");
                    rootContainer.setVisibility(View.VISIBLE);
                    newContainer.setVisibility(View.GONE);

                    View cardView = findViewById(R.id.card_search);
                    if (cardView != null) {
                        Log.d("MainActivity", "resume card view");
                        cardView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
//        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                Log.d("MainActivity", "BackStack changed");
//                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//                    tabsContainer.setVisibility(View.VISIBLE);
//                    fragmentContainer.setVisibility(View.GONE);
//
//                    // Set the card_search visibility
//                    View cardView = findViewById(R.id.card_search);
//                    if (cardView != null) {
//                        cardView.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        });
    }

    public void displayEventDetailsFragment(String eventId, String venueName) {
        rootContainer.setVisibility(View.GONE);
        newContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.new_container, EventDetailsFragment.newInstance(eventId, venueName))
                .addToBackStack(null)
                .commit();
    }
    // Add this method to save the location in SharedPreferences
    // Add this method to save the location in SharedPreferences
    public void locationSaving(double latitude, double longitude) {
        SharedPreferences sharedPreferences = getSharedPreferences("location_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("latitude", (float) latitude);
        editor.putFloat("longitude", (float) longitude);
        this.Lat = latitude;
        this.Lon = longitude;
        editor.apply();
    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                locationSaving(latitude, longitude);
                                Log.d("LocationUpdate", "Latitude: " + latitude + ", Longitude: " + longitude);
                            } else {
                                Toast.makeText(MainActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                // Permission denied, show a message or handle the situation as needed
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onBackArrowClick() {
        getSupportFragmentManager().popBackStack();
        rootContainer.setVisibility(View.VISIBLE);
        newContainer.setVisibility(View.GONE);
    }

}