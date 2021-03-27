package com.example.trialio.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.fragments.QRFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.Trial;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class QRBinomialActivity extends AppCompatActivity {
    private Experiment experiment;
    private ArrayList<Trial> trialList;
    private Trial trial;
    private ExperimentManager experimentManager;
    private TextView txtExpInfo;
    private Switch aSwitch;
    private Button createQR;
    private Boolean isSuccess;


    /**
     * onCreate takes in the experiment passed in as a bundle and send triallist into QRAdaptor
     * @param savedInstanceState
     */
    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_binomial);
        txtExpInfo = (TextView) findViewById(R.id.txtQRExpInfo);
        aSwitch = (Switch) findViewById(R.id.swtQR);
        createQR = (Button) findViewById(R.id.btnQRBinomial);
        isSuccess = aSwitch.isChecked();

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_qr");
        txtExpInfo.setText("Experiment: " + experiment.getSettings().getDescription() + "\nType: " + experiment.getTrialManager().getType());
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
}
