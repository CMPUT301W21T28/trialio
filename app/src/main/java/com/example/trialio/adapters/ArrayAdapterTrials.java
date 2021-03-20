package com.example.trialio.adapters;

import android.content.Context;
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
import com.example.trialio.models.Experiment;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;

import java.util.ArrayList;

/**
 * This is an ArrayAdapter which contains a trial list. Used in TrialActivity.
 */
public class ArrayAdapterTrials extends ArrayAdapter {
    private Context context;
    private ArrayList<Trial> trialList;
    private Experiment experiment;
    private UserManager userManager;

    public ArrayAdapterTrials(Context context, Experiment experiment) {
        super(context, 0, experiment.getTrialManager().getTrials());

        this.trialList = experiment.getTrialManager().getTrials();
        this.context = context;
        this.experiment = experiment;

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

        // get the textviews
        TextView textOwner = view.findViewById(R.id.text_trial_owner);
        TextView textDate = view.findViewById(R.id.text_trial_date);
        TextView textResult = view.findViewById(R.id.text_trial_result);

        // set the textviews
        // get the owner's username
        userManager.getUser(trial.getExperimenterID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                textOwner.setText("Experimenter: " + user.getUsername());
            }
        });

        textDate.setText("Date: "+trial.getDate().toString());

        if (ExperimentTypeUtility.isBinomial(experiment.getTrialManager().getType())) {
            textResult.setText("Result: " + ((BinomialTrial) trial).getIsSuccess());
        }else if (ExperimentTypeUtility.isMeasurement(experiment.getTrialManager().getType())) {
            textResult.setText("Result: " + ((MeasurementTrial) trial).getMeasurement() + " " + ((MeasurementTrial) trial).getUnit());
        }else if (ExperimentTypeUtility.isCount(experiment.getTrialManager().getType())) {
            textResult.setText("Result: " + ((CountTrial) trial).getCount());
        }else if (ExperimentTypeUtility.isNonNegative(experiment.getTrialManager().getType())){
            textResult.setText("Result: " + ((NonNegativeTrial) trial).getNonNegCount());
        }

        return view;
    }
}