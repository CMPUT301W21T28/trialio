package com.example.trialio.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trialio.R;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;

import java.util.ArrayList;

/**
 * This class inherits from ArrayAdapter and is responsible for adapting a Trial object into the GUI
 * ListView item to be displayed on the app screen. This ArrayAdapter is referenced from
 * TrialActivity.
 */
public class TrialAdapter extends ArrayAdapter<Trial> {
    private final String TAG = "ArrayAdapterTrials";

    private final Context context;
    private final ArrayList<Trial> trialList;
    private final UserManager userManager;
    private final String experimentType;

    public TrialAdapter(Context context, ArrayList<Trial> trialList, String experimentType) {
        super(context, 0, trialList);
        this.context = context;
        this.trialList = trialList;
        this.experimentType = experimentType;
        this.userManager = new UserManager();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_trials, parent,false);
        }

        Trial trial = trialList.get(position);

        // get the text views
        TextView textOwner = view.findViewById(R.id.text_trial_owner);
        TextView textDate = view.findViewById(R.id.text_trial_date);
        TextView textResult = view.findViewById(R.id.text_trial_result);

        // set the text views
        userManager.getUserById(trial.getExperimenterID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                if (user != null) {
                    String ownerText = "Username: " + user.getUsername();
                    textOwner.setText(ownerText);
                } else {
                    Log.e(TAG, "Failed to fetch user");
                }
            }
        });

        String dateText = "Date: " + trial.getDate().toString();
        textDate.setText(dateText);

        if (ExperimentTypeUtility.isBinomial(experimentType)) {
            textResult.setText("Result: " + ((BinomialTrial) trial).getIsSuccess());
        } else if (ExperimentTypeUtility.isMeasurement(experimentType)) {
            textResult.setText("Result: " + ((MeasurementTrial) trial).getMeasurement() + " " + ((MeasurementTrial) trial).getUnit());
        } else if (ExperimentTypeUtility.isCount(experimentType)) {
            textResult.setText("Result: " + ((CountTrial) trial).getCount());
        } else if (ExperimentTypeUtility.isNonNegative(experimentType)){
            textResult.setText("Result: " + ((NonNegativeTrial) trial).getNonNegCount());
        } else {
            assert(false);
        }

        return view;
    }
}