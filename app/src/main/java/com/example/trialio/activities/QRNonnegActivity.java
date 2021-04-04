package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.fragments.QRFragment;
import com.example.trialio.models.Experiment;

/**
 * This activity provides the interface for creating a NonNegative Trial QR code.
 */
public class QRNonnegActivity extends AppCompatActivity {
    private Button createQR;
    private EditText input;
    private Experiment experiment;
    private TextView experimentDescriptionTextView;
    private ImageView experimentLocationImageView ;
    private TextView experimentTypeTextView;
    private TextView experimentOwnerTextView;
    private TextView experimentStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_nonneg);
        createQR = findViewById(R.id.btnQRNonneg);
        input = findViewById(R.id.txtNonNegQRValue);


        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_qr");

        // get views
        experimentDescriptionTextView = findViewById(R.id.qr_description);
        experimentLocationImageView = findViewById(R.id.qr_location);
        experimentTypeTextView = findViewById(R.id.qr_text_type);
        experimentOwnerTextView = findViewById(R.id.qr_text_owner);
        experimentStatusTextView = findViewById(R.id.qr_text_status);

        // set experiment info

        experimentDescriptionTextView.setText(experiment.getSettings().getDescription());
        experimentTypeTextView.setText(experiment.getTrialManager().getType());
        experimentOwnerTextView.setText(experiment.getSettings().getOwnerID());

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
                bundle.putString("result", input.getText().toString());
                qrFragment.setArguments(bundle);
                qrFragment.show(getSupportFragmentManager(),"QrCode");
            }
        });
    }



}