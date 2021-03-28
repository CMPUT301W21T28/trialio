package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.fragments.QRFragment;
import com.example.trialio.models.Experiment;

public class QRNonnegActivity extends AppCompatActivity {
    private TextView txtExpInfo;
    private Button createQR;
    private EditText input;
    private Experiment experiment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_nonneg);
        txtExpInfo = findViewById(R.id.txtQRExpInfo);
        createQR = findViewById(R.id.btnQRNonneg);
        input = findViewById(R.id.txtNonNegQRValue);

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
                bundle.putString("result", input.getText().toString());
                qrFragment.setArguments(bundle);
                qrFragment.show(getSupportFragmentManager(),"QrCode");
            }
        });
    }



}