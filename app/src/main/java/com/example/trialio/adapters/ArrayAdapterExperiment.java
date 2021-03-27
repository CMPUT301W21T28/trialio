package com.example.trialio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.R;
import com.example.trialio.models.User;

import java.util.ArrayList;

/**
 * This is an ArrayAdapter which contains an experiment list. Used in MainActivity.
 */
public class ArrayAdapterExperiment extends ArrayAdapter {
    private Context context;
    private ArrayList<Experiment> experimentList;

    public ArrayAdapterExperiment(Context context, ArrayList<Experiment> experimentList) {
        super(context, 0, experimentList);
        this.context = context;

        this.experimentList = experimentList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_experiment, parent, false);
        }

        Experiment experiment = experimentList.get(position);

        // get the textviews
        TextView textDescription = view.findViewById(R.id.text_description);
        TextView textType = view.findViewById(R.id.text_type);
        TextView textStatus = view.findViewById(R.id.text_status);
        TextView textOwner = view.findViewById(R.id.text_owner);
        ImageView locNeed = view.findViewById(R.id.location);

        // set the textviews
        textDescription.setText(experiment.getSettings().getDescription());
        textType.setText(experiment.getTrialManager().getType());
        textStatus.setText(experiment.getTrialManager().getIsOpen() ? "OPEN" : "CLOSED");
        locNeed.setImageResource(R.drawable.ic_baseline_location_on_24);

        UserManager userManager = new UserManager();
        userManager.getUserById(experiment.getSettings().getOwnerId(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                textOwner.setText(user.getUsername());
            }
        });

        if (!experiment.getSettings().getGeoLocationRequired()) {
            locNeed.setImageResource(R.drawable.ic_baseline_location_off_24);
        }

        return view;
    }
}