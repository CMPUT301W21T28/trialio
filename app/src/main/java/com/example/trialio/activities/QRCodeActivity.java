package com.example.trialio.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.ArrayAdapterQR;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.fragments.CountTrialFragment;
import com.example.trialio.fragments.QRFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class QRCodeActivity extends AppCompatActivity {
    private Experiment experiment;
    private ArrayList<Trial> trialList;
    private ArrayAdapterQR QRAdapter;
    private final Context context = this;
    private ExperimentManager experimentManager;


    /**
     * onCreate takes in the experiment passed in as a bundle and send triallist into QRAdaptor
     * @param savedInstanceState
     */
    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_qr");

        trialList = experiment.getTrialManager().getTrials();
        QRAdapter = new ArrayAdapterQR(this, experiment);

        experimentManager = new ExperimentManager();

        // Set up the adapter for the list and experiment manager
        ListView trialListView = findViewById(R.id.list_trials_QR);
        trialListView.setAdapter(QRAdapter);

    }

    /**
     * Sets up on click listeners for the activity.
     */
    private void setUpOnClickListeners() {
        // Called when the user clicks item in experiment list
        ListView QRListView = findViewById(R.id.list_trials_QR);
        QRListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                QRFragment qrCode = new QRFragment();
                Bundle args = new Bundle();
                args.putSerializable("experiment", experiment);
                qrCode.setArguments(args);
                qrCode.show(getSupportFragmentManager(), "showQR");

            }
        });
    }


}
