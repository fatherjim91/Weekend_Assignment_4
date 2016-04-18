package kalpesh.mac.com.raandroid_header;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import kalpesh.mac.com.raandroid_header.model.Marker;

/**
 * Created by fatherjim on 15/04/2016.
 */
public class ViewMap extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap my_map;

    private GoogleApiClient google_api_client;

    private ArrayList<Marker> map_markers;
    private IntentFilter intent_filter;
    private BroadcastReceiver bcast_receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Toolbar configuration
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.map_title);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager fragment_manager = getSupportFragmentManager();
        final SupportMapFragment map_fragment = (SupportMapFragment) fragment_manager.findFragmentById(R.id.map);
        map_fragment.getMapAsync(this);

        intent_filter = new IntentFilter();
        intent_filter.addAction(CoordinatesIntentService.SUCCESSFUL_RESULT);

        bcast_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(CoordinatesIntentService.SUCCESSFUL_RESULT)) {
                    map_markers = intent.getParcelableArrayListExtra("map_markers");
                    for (int i=0; i<map_markers.size(); i++) {
                        if (map_markers.get(i).isInfoCorrect() == 1)
                            my_map.addMarker(new MarkerOptions().position(new LatLng(map_markers.get(i).getLatitude(),map_markers.get(i).getLongitude())).title(map_markers.get(i).getName()).snippet(map_markers.get(i).getAddress()));
                    }
                }
            }
        };

        registerReceiver(bcast_receiver, intent_filter);

        // Build GoogleAPI
        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        my_map = googleMap;
        my_map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.50, -0.12), 10));

        Intent start_service = new Intent(this, CoordinatesIntentService.class);
        start_service.putParcelableArrayListExtra("markers", getIntent().getParcelableArrayListExtra("markers"));
        startService(start_service);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bcast_receiver);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("GoogleAPIClient", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("GoogleAPIClient", "Connection established");
    }

    @Override
    public void onConnectionSuspended(int i) {
        google_api_client.connect();
    }
}
