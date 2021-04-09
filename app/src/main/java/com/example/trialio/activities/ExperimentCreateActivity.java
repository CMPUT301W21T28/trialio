package com.example.trialio.activities;

//map reference from https://developers.google.com/maps/documentation/android-sdk/start

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.trialio.controllers.CurrentUserHandler;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.R;
import com.example.trialio.models.ExperimentSettings;
import com.example.trialio.models.Location;
import com.example.trialio.models.Region;
import com.example.trialio.models.User;
import com.example.trialio.utils.HomeButtonUtility;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity allows a user to create an experiment, with full settings for the user to make it
 *
 * This activity navigates to:
 * <ul>
 *  <li>ExperimentActivity</li>
 *  <li>MainActivity</li>
 * </ul>
 */

public class ExperimentCreateActivity extends AppCompatActivity implements OnMapReadyCallback{
    private final String TAG = "ExpCreateActivity";
    private Experiment experiment;
    private ExperimentManager experimentManager;
    private UserManager userManager;
    private final Context context = this;
    private String selectedType = "";
    private GoogleMap regionMap;
    private Location regionLocation;
    private String regionName;
    private UiSettings uiSettings;

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    /**
     * the On create the takes in the saved instance from the main activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_experiment);

        experimentManager = new ExperimentManager();
        userManager = new UserManager();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.regionMap);
        mapFragment.getMapAsync(this);

        Spinner selectType = (Spinner) findViewById(R.id.typeDropdown);

        // Adapted from class/division code.
        // DATE:	2021-03-18
        // LICENSE:	CC BY 4.0 [https://creativecommons.org/licenses/by/4.0/]
        // SOURCE:  Working with Spinners in Android [https://www.studytonight.com/android/spinner-example-in-android#]
        // AUTHOR: 	Studytonight tutorial developers
        selectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button addNewExperiment = (Button) findViewById(R.id.btnAddNewExperiment);
        addNewExperiment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: testing that this actually works
                Intent intent = new Intent(context, ExperimentActivity.class);

                EditText editDescription = (EditText) findViewById(R.id.descriptionEditText);
                EditText editRegion = (EditText) findViewById(R.id.regionEditText);
                EditText editNumTrials = (EditText) findViewById(R.id.numTrialsEditText);

                Switch geoSwitch = (Switch) findViewById(R.id.geo_switch);
                Switch openSwitch = (Switch) findViewById(R.id.open_switch);
                Switch publishedSwitch = (Switch) findViewById(R.id.published_switch);


                //----------------------------------
                // prepare ExperimentSettings object
                //----------------------------------

                // prepare description
                String description = editDescription.getText().toString();

                // prepare region
                Region region = new Region();
                region.setRegionText(editRegion.getText().toString());
                region.setGeoLocation(regionLocation);

                // prepare geo
                boolean geo = geoSwitch.isChecked();

                //--------------------------
                // prepare Experiment object
                //--------------------------

                // generate ID
                String newID = experimentManager.getNewExperimentID();

                // prepare type
                String type = selectedType;

                // prepare open
                boolean open = openSwitch.isChecked();

                // prepare published
                boolean published = publishedSwitch.isChecked();

                // prepare minimum number of trials
                String int_popup = "Please enter a positive integer for minimum number of trials";
                try {
                    int numTrials = Integer.parseInt(editNumTrials.getText().toString());

                    if (numTrials < 0) {
                        Toast.makeText(context, int_popup, Toast.LENGTH_LONG).show();
                    } else {
                        // get owner id
                        CurrentUserHandler.getInstance().getCurrentUser(new CurrentUserHandler.OnUserFetchCallback() {
                            @Override
                            public void onUserFetch(User user) {

                                // prepare experiment settings
                                ExperimentSettings settings = new ExperimentSettings(description, region, user.getId(), geo);

                                // create Experiment object
                                experiment = new Experiment(newID, settings, type, open, numTrials, published);
                                experimentManager.publishExperiment(experiment);

                                Bundle args = new Bundle();
                                args.putSerializable("experiment", experiment);
                                intent.putExtras(args);

                                startActivity(intent);

                                // finish the activity so back button does not return here
                                finish();
                            }
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(context, int_popup, Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cancelNewExperiment = (Button) findViewById(R.id.btnCancelNewExperiment);
        cancelNewExperiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set the home button
        HomeButtonUtility.setHomeButtonListener(findViewById(R.id.button_home));
    }

    private BitmapDescriptor iconFromDrawable(Context context, int imgID) {
        Drawable icon = ContextCompat.getDrawable(context, imgID);
        icon.setBounds(0, 0, 100, 100);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        regionMap = googleMap;

        EditText editRegion = (EditText) findViewById(R.id.regionEditText);

        uiSettings = regionMap.getUiSettings();

        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(false);

        regionLocation = new Location();

        regionMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                regionMap.clear();
                regionMap.addMarker(new MarkerOptions().position(latLng).icon(iconFromDrawable(context, R.drawable.conical_flask_empty)));
                regionLocation.setLatitude(latLng.latitude);
                regionLocation.setLongitude(latLng.longitude);

                final Handler handler = new Handler();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Getting Location Name in thread: " + Thread.currentThread().getName());
                        Geocoder geocoder = new Geocoder(ExperimentCreateActivity.this);
                        String regionName = null;
                        List<Address> addresses = new ArrayList<>();
                        try {
                            addresses = geocoder.getFromLocation(regionLocation.getLatitude(), regionLocation.getLongitude(), 3);
                            regionName = addresses.get(0).getLocality();
                            if (regionName == null || regionName.length() > 20) {
                                regionName = addresses.get(0).getAdminArea();
                                if (regionName == null || regionName.length() > 20) {
                                    regionName = addresses.get(0).getCountryName();
                                }
                            }
                            setRegionName(regionName);
                            String finalRegionName = regionName;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalRegionName != null) {
                                        editRegion.setText(finalRegionName, TextView.BufferType.EDITABLE);
                                    }
                                }
                            });
                            Log.d(TAG, "Successfully found name: " + regionName);
                        } catch (IOException e) {
                            Log.e(TAG, "Could not get Location Name: " +e.getMessage());
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });
    }

}