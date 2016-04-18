package kalpesh.mac.com.raandroid_header;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kalpesh.mac.com.raandroid_header.adapter.Adapter;
import kalpesh.mac.com.raandroid_header.model.Example;
import kalpesh.mac.com.raandroid_header.model.Marker;
import kalpesh.mac.com.raandroid_header.model.Restaurant;
import kalpesh.mac.com.raandroid_header.observables.IRestaurant;
import kalpesh.mac.com.raandroid_header.services.Services;
import kalpesh.mac.com.raandroid_header.utilities.RxUtils;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private IRestaurant _api;
    private List<Restaurant> mRestaurantList;
    private ProgressDialog pDialog;
    private ListView list;
    private ArrayList<Marker> markers = new ArrayList<>();
    // Subscription that represents a group of Subscriptions that are unsubscribed together.
    private CompositeSubscription _subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar configuration
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.main_title);

        list = (ListView)findViewById(R.id.list);

        _api= Services._createRestruentshubApi();

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        pattern();

    }
    @Override
    public void onResume() {
        super.onResume();
        _subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(_subscriptions);
    }

    @Override
    public void onPause() {
        super.onPause();
        RxUtils.unsubscribeIfNotNull(_subscriptions);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    public void pattern(){
        _subscriptions.add(_api.getRestraurent()
                .timeout(5000, TimeUnit.MILLISECONDS)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Example>>() {
                    @Override
                    public Observable<? extends Example> call(Throwable throwable) {
                        Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_SHORT).show();
                        Log.i("ERROR RX","NO MSG" );
                        return Observable.error(throwable);
                    }
                })
                .retry()
                .distinct()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Example>() {
                    @Override
                    public void onCompleted() {
                        hidePDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hidePDialog();
                    }

                    @Override
                    public void onNext(Example example) {
                        mRestaurantList = example.getRestaurants();
                        // Adds all restaurant marker info to be passed into the next activity
                        for (int i=0; i< mRestaurantList.size(); i++) {
                            markers.add(new Marker(mRestaurantList.get(i).getName(),mRestaurantList.get(i).getAddress(),mRestaurantList.get(i).getPostcode(), mRestaurantList.get(i).getCity()));
                        }
                        System.out.println("Got: " + " (" + Thread.currentThread().getName() + ")");
                        Adapter adapt = new Adapter(getApplicationContext(), R.layout.row, mRestaurantList);
                        list.setAdapter(adapt);
                    }
                }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map) {
            Intent map = new Intent(this, ViewMap.class);
            map.putExtra("markers", markers);
            startActivity(map);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
