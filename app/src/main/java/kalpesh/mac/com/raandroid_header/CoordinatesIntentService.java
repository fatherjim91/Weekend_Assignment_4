package kalpesh.mac.com.raandroid_header;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kalpesh.mac.com.raandroid_header.model.Marker;

/**
 * Created by fatherjim on 15/04/2016.
 */
public class CoordinatesIntentService extends IntentService {

    public static final String SUCCESSFUL_RESULT = "INTENT_RESULTS";
    private static final String TAG = "reverse-geocoding-intent-service";

    public CoordinatesIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<Marker> map_markers = intent.getParcelableArrayListExtra("markers");

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if (geocoder.isPresent()) {
            List<Address> list = null;

            for (int i=0; i<map_markers.size(); i++) {
                try {
                    list = geocoder.getFromLocationName(map_markers.get(i).getPostcode(), 1);
                    while (list.size() == 0) {
                        list = geocoder.getFromLocationName(map_markers.get(i).getPostcode(), 1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = list.get(0);
                if (address != null) {
                    map_markers.get(i).setLatitude(address.getLatitude());
                    map_markers.get(i).setLongitude(address.getLongitude());
                } else {
                    map_markers.get(i).setInfoIncorrect();
                }
            }

        }
        Intent send_results = new Intent();
        send_results.setAction(CoordinatesIntentService.SUCCESSFUL_RESULT);
        send_results.putExtra("map_markers", map_markers);
        sendBroadcast(send_results);
    }

}
