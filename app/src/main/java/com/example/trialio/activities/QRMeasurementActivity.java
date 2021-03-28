package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.fragments.QRFragment;
import com.example.trialio.models.Experiment;

public class QRMeasurementActivity extends AppCompatActivity {
    private TextView txtExpInfo;
    private Button createQR;
    private Experiment experiment;
    private TextView Warning;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_measurement);
        txtExpInfo = findViewById(R.id.txtQRExpInfo);
        createQR = findViewById(R.id.btnQRMeasurement);
        input = findViewById(R.id.txtMeasurementQRValue);
        Warning = findViewById(R.id.txtWarning);
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
                if (input.getText().toString().matches("")){
                    Warning.setText("Measurement cannot be blank.");
                }else{
                    bundle.putString("result", input.getText().toString());
                    qrFragment.setArguments(bundle);
                    qrFragment.show(getSupportFragmentManager(),"QrCode");
                }
            }
        });
    }
}