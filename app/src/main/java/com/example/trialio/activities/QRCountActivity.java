package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.adapters.ArrayAdapterBarcode;
import com.example.trialio.controllers.BarcodeManager;
import com.example.trialio.fragments.QRFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.utils.HomeButtonUtility;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity provides the interface for creating a Count Trial QR code.
 */
public class QRCountActivity extends AppCompatActivity {
    private Button createQR;
    private Experiment experiment;
    private Button showQR;
    private Button showBarcode;
    private Context context = this;
    private TextView experimentDescriptionTextView;
    private ImageView experimentLocationImageView ;
    private TextView experimentTypeTextView;
    private TextView experimentOwnerTextView;
    private TextView experimentStatusTextView;
    private TextView txtMode;
    private ArrayList<String> barcodeList;
    private ArrayAdapterBarcode barcodeAdapter;
    private BarcodeManager barcodeManager;
    private ListView listviewBarcode;
    private Boolean onBarcodeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_count);
        createQR = findViewById(R.id.btnQRCount);
        showQR = findViewById(R.id.btnshowQR);
        showBarcode = findViewById(R.id.btnshowBarcode);
        listviewBarcode = findViewById(R.id.listBarcode);
        txtMode = findViewById(R.id.txtMode);


        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_qr");


        barcodeManager = new BarcodeManager(experiment.getExperimentID());
        barcodeList = new ArrayList<>();
        barcodeAdapter = new ArrayAdapterBarcode(this, barcodeList, experiment);

        listviewBarcode.setAdapter(barcodeAdapter);


        setExperimentInfo();
        setQRView();
        setOnClickListeners();

        // set the home button
        HomeButtonUtility.setHomeButtonListener(findViewById(R.id.button_home));
    }


    @Override
    protected void onResume() {
        super.onResume();
        setBarcodeList();
        setExperimentInfo();
    }


    /**
     * Sets the questionList and updates the ArrayAdapter of the activity
     */
    private void setBarcodeList() {
        barcodeManager.setOnAllBarcodesFetchCallback(new BarcodeManager.OnManyBarcodesFetchListener() {
            @Override
            public void onManyBarcodesFetch(List<String> barcodes) {  // TODO: why not ArrayList ***
                Log.w("", "Successfully fetched barcodes");
                barcodeList.clear();
                barcodeList.addAll(barcodes);   // TODO: check for errors
                barcodeAdapter.notifyDataSetChanged();
            }
        });
    }


    public void setOnClickListeners() {
        createQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBarcodeView){
                    Intent intent = new Intent(context, ScanningActivity.class);
                    Bundle bundle = new Bundle();
                    Boolean isBarcode = true;
                    bundle.putSerializable("experiment", experiment);
                    bundle.putSerializable("result", String.valueOf(1));
                    bundle.putBoolean("isBarcode", isBarcode);
                    intent.putExtra("Parent", "QRActivity");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    QRFragment qrFragment = new QRFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("experiment",experiment);
                    bundle.putString("result", String.valueOf(1));
                    qrFragment.setArguments(bundle);
                    qrFragment.show(getSupportFragmentManager(),"QrCode");
                }
            }
        });

        showQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQRView();
            }
        });

        showBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBarcodeView();
            }
        });

        listviewBarcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QRFragment qrFragment = new QRFragment();
                Bundle bundle = new Bundle();
                Boolean isBarcode = true;
                bundle.putString("barcode",barcodeList.get(i));
                bundle.putBoolean("isBarcode", isBarcode);
                qrFragment.setArguments(bundle);
                qrFragment.show(getSupportFragmentManager(),"barcode");
            }
        });

    }

    private void setExperimentInfo(){
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
    }

    private void setBarcodeView (){
        toggleListButton(R.id.btnshowBarcode);
        listviewBarcode.setVisibility(View.VISIBLE);
        createQR.setText("Register Barcode: ");
        txtMode.setText("Barcode");
        onBarcodeView = true;
    }

    private void setQRView(){
        listviewBarcode.setVisibility(View.INVISIBLE);
        toggleListButton(R.id.btnshowQR);
        createQR.setText("Create QR: ");
        txtMode.setText("QR Code");
        onBarcodeView = false;
    }

    private void toggleListButton(int btn) {
        /* Shayne3000, https://stackoverflow.com/users/8801181/shayne3000,
         * "How to add button tint programmatically", 2018-02-13, CC BY-SA 3.0
         * https://stackoverflow.com/questions/29801031/how-to-add-button-tint-programmatically/49259711#49259711
         */

        // Set old button to grey
        Drawable buttonDrawable = showQR.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.button_dark_grey));
        showQR.setBackground(buttonDrawable);

        // Set new button to special yellow
        Button selectedBtn = (Button) findViewById(btn);
        buttonDrawable = selectedBtn.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.special_yellow));
        selectedBtn.setBackground(buttonDrawable);

        showQR = selectedBtn;

    }

}