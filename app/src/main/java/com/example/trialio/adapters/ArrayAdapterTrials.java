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
import com.example.trialio.models.Trial;

import java.util.ArrayList;

public class ArrayAdapterTrials extends ArrayAdapter {
    private Context context;
    private ArrayList<Trial> trialList;

    public ArrayAdapterTrials(Context context, ArrayList<Trial> trialList) {
        super(context, 0, trialList);

        this.trialList = trialList;
        this.context = context;
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
        TextView textID = view.findViewById(R.id.text_trial_id);
        TextView textOwner = view.findViewById(R.id.text_trial_owner);
        TextView textDate = view.findViewById(R.id.text_trial_date);
        TextView textResult = view.findViewById(R.id.text_trial_result);


        // set the textviews
        textID.setText("TEST ID");
        textOwner.setText("TEST OWNER");
        textDate.setText("TEST DATE");
        textResult.setText("TEST RESULT");


        // TODO: ASK HOW TO GET TRIAL OWNER -JEFF
        // TODO: ASK HOW TO GET TRIAL RESULT -JEFF

        return view;
    }
}
