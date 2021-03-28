package com.example.trialio.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.fragments.QRFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.Trial;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class QRBinomialActivity extends AppCompatActivity {
    private Experiment experiment;
    private ArrayList<Trial> trialList;
    private Trial trial;
    private ExperimentManager experimentManager;
    private Switch aSwitch;
    private Button createQR;
    private Boolean isSuccess;
    private TextView experimentDescriptionTextView;
    private ImageView experimentLocationImageView ;
    private TextView experimentTypeTextView;
    private TextView experimentOwnerTextView;
    private TextView experimentStatusTextView;


    /**
     * onCreate takes in the experiment passed in as a bundle and send triallist into QRAdaptor
     * @param savedInstanceState
     */
    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_binomial);
        aSwitch = findViewById(R.id.swtQR);
        createQR = findViewById(R.id.btnQRBinomial);
        isSuccess = aSwitch.isChecked();


        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_qr");
        setOnClickListeners();


        trialList = experiment.getTrialManager().getTrials();
        experimentManager = new ExperimentManager();

        // get views
        experimentDescriptionTextView = findViewById(R.id.qr_description);
        experimentLocationImageView = findViewById(R.id.qr_location);
        experimentTypeTextView = findViewById(R.id.qr_text_type);
        experimentOwnerTextView = findViewById(R.id.qr_text_owner);
        experimentStatusTextView = findViewById(R.id.qr_text_status);

        // set experiment info

        experimentDescriptionTextView.setText(experiment.getSettings().getDescription());
        experimentTypeTextView.setText(experiment.getTrialManager().getType());
        experimentOwnerTextView.setText(experiment.getSettings().getOwnerId());

        if ( experiment.getTrialManager().getIsOpen() ) {
            experimentStatusTextView.setText("Open");
        } else {
            experimentStatusTextView.setText("Closed");
        }

        if (!experiment.getSettings().getGeoLocationRequired()) {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_off_24);
        } else {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_on_24);
        }

        setOnClickListeners();

    }



    public void setOnClickListeners() {
        createQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRFragment qrFragment = new QRFragment();
                Bundle bundle = new Bundle();

                bundle.putSerializable("experiment",experiment);
                bundle.putString("result", String.valueOf(isSuccess));
                qrFragment.setArguments(bundle);
                qrFragment.show(getSupportFragmentManager(),"QrCode");
            }
        });
    }
    private void setActivitytoBarcode() {

    }


}
