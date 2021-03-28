package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.fragments.QRFragment;
import com.example.trialio.models.Experiment;

public class QRCountActivity extends AppCompatActivity {
    private Button createQR;
    private TextView txtExpInfo;
    private Experiment experiment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_count);
        txtExpInfo = findViewById(R.id.txtQRExpInfo);
        createQR = findViewById(R.id.btnQRCount);
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
                bundle.putString("result", String.valueOf(1));
                qrFragment.setArguments(bundle);
                qrFragment.show(getSupportFragmentManager(),"QrCode");
            }
        });
    }

}