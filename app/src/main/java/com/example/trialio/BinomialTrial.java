package com.example.trialio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.w3c.dom.Text;

public class BinomialTrial extends AppCompatDialogFragment {

    private Switch Switch;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_binomial_trial_fragment, null);
        Switch = view.findViewById(R.id.switchSuccessIndicator);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Add Binomial Trial:")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        ((ExperimentActivity) getActivity()).fetchResult(String.valueOf((Switch.isChecked())));
                    }
                })
                .create();


    }

}
