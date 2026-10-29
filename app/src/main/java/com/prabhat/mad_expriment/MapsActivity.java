package com.prabhat.mad_expriment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    // Default landmark coordinates
    private static final LatLng BENGALURU = new LatLng(12.9716, 77.5946);
    private static final LatLng NEW_DELHI = new LatLng(28.6139, 77.2090);
    private static final LatLng TAJ_MAHAL = new LatLng(27.1751, 78.0421);
    private static final LatLng SILICON_INSTITUTE = new LatLng(20.3524, 85.8202);
    private static final LatLng MUMBAI = new LatLng(19.0760, 72.8777);

    private GoogleMap mMap;

    private ScrollView scrollViewMaps;
    private FrameLayout mapFrameLayout;
    private View mapFragmentView;
    private WebView webViewMap;
    private TextView tvMapEngineStatus;

    private TextInputEditText etSearchLocation;
    private TextView tvLocationTitle;
    private TextView tvLocationCoords;

    private MaterialButton btnTypeNormal;
    private MaterialButton btnTypeSatellite;
    private MaterialButton btnTypeTerrain;
    private MaterialButton btnTypeHybrid;

    private LatLng currentSelectedLocation = BENGALURU;
    private String currentSelectedTitle = "Bengaluru, Karnataka";
    private float currentZoom = 14f;

    private boolean isWebEngineActive = false;

    // Location Permission Launcher
    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineGranted || coarseGranted) {
                    enableMyLocationOnMap();
                } else {
                    Toast.makeText(this, "Location permission required to display current position.", Toast.LENGTH_SHORT).show();
                }
            });

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Bind XML views
        scrollViewMaps = findViewById(R.id.scrollViewMaps);
        mapFrameLayout = findViewById(R.id.mapFrameLayout);
        mapFragmentView = findViewById(R.id.mapFragment);
        webViewMap = findViewById(R.id.webViewMap);
        tvMapEngineStatus = findViewById(R.id.tvMapEngineStatus);

        etSearchLocation = findViewById(R.id.etSearchLocation);
        tvLocationTitle = findViewById(R.id.tvLocationTitle);
        tvLocationCoords = findViewById(R.id.tvLocationCoords);

        MaterialButton btnToggleEngine = findViewById(R.id.btnToggleEngine);
        MaterialButton btnSearchMap = findViewById(R.id.btnSearchMap);
        MaterialButton btnZoomIn = findViewById(R.id.btnZoomIn);
        MaterialButton btnZoomOut = findViewById(R.id.btnZoomOut);
        MaterialButton btnMyLocation = findViewById(R.id.btnMyLocation);
        MaterialButton btnClearMarkers = findViewById(R.id.btnClearMarkers);
        MaterialButton btnOpenExternalMap = findViewById(R.id.btnOpenExternalMap);

        btnTypeNormal = findViewById(R.id.btnTypeNormal);
        btnTypeSatellite = findViewById(R.id.btnTypeSatellite);
        btnTypeTerrain = findViewById(R.id.btnTypeTerrain);
        btnTypeHybrid = findViewById(R.id.btnTypeHybrid);

        Chip chipBengaluru = findViewById(R.id.chipBengaluru);
        Chip chipDelhi = findViewById(R.id.chipDelhi);
        Chip chipTajMahal = findViewById(R.id.chipTajMahal);
        Chip chipSiliconInstitute = findViewById(R.id.chipSiliconInstitute);
        Chip chipMumbai = findViewById(R.id.chipMumbai);

        // Prevent ScrollView from stealing touch/pan gestures inside map view
        mapFrameLayout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    scrollViewMaps.requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    scrollViewMaps.requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        });

        // Configure WebView Map Settings
        WebSettings webSettings = webViewMap.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webViewMap.setWebViewClient(new WebViewClient());

        // Check if API key is configured or default placeholder
        String apiKey = getString(R.string.google_maps_key);
        if ("YOUR_API_KEY_HERE".equals(apiKey) || apiKey.isEmpty()) {
            // Default to Web Map Engine for instant guaranteed tile rendering without API Key
            isWebEngineActive = true;
        }

        updateEngineVisibility();

        // Initialize Native SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Toggle Engine Listener
        btnToggleEngine.setOnClickListener(v -> {
            isWebEngineActive = !isWebEngineActive;
            updateEngineVisibility();
            updateMapDisplay(currentSelectedLocation, currentSelectedTitle, "Switched Map View");
            Toast.makeText(this, isWebEngineActive ? "Switched to Web Map View" : "Switched to Native Google Maps SDK", Toast.LENGTH_SHORT).show();
        });

        // Search Action Click Listener
        btnSearchMap.setOnClickListener(v -> searchAndNavigateLocation());

        etSearchLocation.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchAndNavigateLocation();
                return true;
            }
            return false;
        });

        // Preset Landmark Chips
        chipBengaluru.setOnClickListener(v -> moveCameraToLocation(BENGALURU, "Bengaluru, Karnataka", "IT Capital of India"));
        chipDelhi.setOnClickListener(v -> moveCameraToLocation(NEW_DELHI, "New Delhi", "Capital City of India"));
        chipTajMahal.setOnClickListener(v -> moveCameraToLocation(TAJ_MAHAL, "Taj Mahal, Agra", "Wonder of the World"));
        chipSiliconInstitute.setOnClickListener(v -> moveCameraToLocation(SILICON_INSTITUTE, "Silicon Institute of Technology", "Bhubaneswar, Odisha"));
        chipMumbai.setOnClickListener(v -> moveCameraToLocation(MUMBAI, "Mumbai, Maharashtra", "Financial Capital of India"));

        // Map Type Selectors
        btnTypeNormal.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_NORMAL, btnTypeNormal, "m"));
        btnTypeSatellite.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_SATELLITE, btnTypeSatellite, "k"));
        btnTypeTerrain.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_TERRAIN, btnTypeTerrain, "p"));
        btnTypeHybrid.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_HYBRID, btnTypeHybrid, "y"));

        // Map Zoom Controls
        btnZoomIn.setOnClickListener(v -> {
            currentZoom = Math.min(currentZoom + 1f, 21f);
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
            refreshWebMapTile();
        });

        btnZoomOut.setOnClickListener(v -> {
            currentZoom = Math.max(currentZoom - 1f, 2f);
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
            refreshWebMapTile();
        });

        // My Location Button
        btnMyLocation.setOnClickListener(v -> requestLocationPermissionAndShow());

        // Clear Markers Button
        btnClearMarkers.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.clear();
            }
            moveCameraToLocation(currentSelectedLocation, currentSelectedTitle, "Map Cleared");
            Toast.makeText(this, "Map markers reset", Toast.LENGTH_SHORT).show();
        });

        // Open in external Google Maps App Button
        btnOpenExternalMap.setOnClickListener(v -> {
            Uri locationUri = Uri.parse("geo:" + currentSelectedLocation.latitude + "," +
                    currentSelectedLocation.longitude + "?q=" + Uri.encode(currentSelectedTitle));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            try {
                startActivity(mapIntent);
            } catch (Exception e) {
                Intent fallbackIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=" +
                                currentSelectedLocation.latitude + "," + currentSelectedLocation.longitude));
                startActivity(fallbackIntent);
            }
        });

        // Initial render
        updateMapDisplay(BENGALURU, "Bengaluru, Karnataka", "Default Map Location");
    }

    private void updateEngineVisibility() {
        if (isWebEngineActive) {
            webViewMap.setVisibility(View.VISIBLE);
            mapFragmentView.setVisibility(View.GONE);
            tvMapEngineStatus.setText("Map Engine: Web Interactive Map (Guaranteed Tile Visibility)");
        } else {
            webViewMap.setVisibility(View.GONE);
            mapFragmentView.setVisibility(View.VISIBLE);
            tvMapEngineStatus.setText("Map Engine: Native Google Maps SDK");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMapClickListener(this);

        moveCameraToLocation(currentSelectedLocation, currentSelectedTitle, "Default Map Location");

        if (hasLocationPermission()) {
            enableMyLocationOnMap();
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        currentSelectedLocation = latLng;
        String snippet = String.format(Locale.getDefault(), "Lat: %.4f, Lng: %.4f", latLng.latitude, latLng.longitude);
        currentSelectedTitle = "Custom Pin Location";

        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(currentSelectedTitle)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        updateLocationDetailsDisplay(currentSelectedTitle, latLng);
        refreshWebMapTile();
        Toast.makeText(this, "Pinned location: " + snippet, Toast.LENGTH_SHORT).show();
    }

    private void moveCameraToLocation(LatLng location, String title, String snippet) {
        currentSelectedLocation = location;
        currentSelectedTitle = title;

        updateMapDisplay(location, title, snippet);
    }

    private void updateMapDisplay(LatLng location, String title, String snippet) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(title)
                    .snippet(snippet));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, currentZoom));
        }

        refreshWebMapTile();
        updateLocationDetailsDisplay(title, location);
    }

    private void refreshWebMapTile() {
        String query = Uri.encode(currentSelectedTitle + " @ " + currentSelectedLocation.latitude + "," + currentSelectedLocation.longitude);
        String embedUrl = "https://maps.google.com/maps?q=" + currentSelectedLocation.latitude + "," + currentSelectedLocation.longitude +
                "&z=" + ((int) currentZoom) + "&output=embed";
        webViewMap.loadUrl(embedUrl);
    }

    private void updateLocationDetailsDisplay(String title, LatLng location) {
        tvLocationTitle.setText(title);
        tvLocationCoords.setText(String.format(Locale.getDefault(), "Latitude: %.4f, Longitude: %.4f", location.latitude, location.longitude));
    }

    private void searchAndNavigateLocation() {
        String searchQuery = etSearchLocation.getText() != null ? etSearchLocation.getText().toString().trim() : "";
        if (searchQuery.isEmpty()) {
            Toast.makeText(this, "Please enter a place or city name to search", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(searchQuery, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng searchLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                String addressName = address.getFeatureName() != null ? address.getFeatureName() : searchQuery;

                moveCameraToLocation(searchLatLng, addressName, address.getAddressLine(0));
                Toast.makeText(this, "Found: " + addressName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location not found. Try another place name.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Geocoder error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setMapType(int mapType, MaterialButton selectedBtn, String webMapType) {
        if (mMap != null) {
            mMap.setMapType(mapType);
        }
        String embedUrl = "https://maps.google.com/maps?q=" + currentSelectedLocation.latitude + "," + currentSelectedLocation.longitude +
                "&z=" + ((int) currentZoom) + "&t=" + webMapType + "&output=embed";
        webViewMap.loadUrl(embedUrl);
        Toast.makeText(this, "Map mode updated", Toast.LENGTH_SHORT).show();
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissionAndShow() {
        if (hasLocationPermission()) {
            enableMyLocationOnMap();
            Toast.makeText(this, "My Location enabled", Toast.LENGTH_SHORT).show();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocationOnMap() {
        if (mMap != null && hasLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }
}