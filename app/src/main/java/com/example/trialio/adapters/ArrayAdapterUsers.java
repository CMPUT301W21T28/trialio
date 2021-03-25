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

import java.util.ArrayList;

/**
 * This is an ArrayAdapter which contains a list of users. Used in ExperimentSettingsActivity.
 */
public class ArrayAdapterUsers extends ArrayAdapter {
    private Context context;
    private ArrayList<String> ignoredList;

    public ArrayAdapterUsers(Context context, ArrayList<String> ignoredList) {
        super(context, 0, ignoredList);
        this.context = context;

        this.ignoredList = ignoredList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_user, parent,false);
        }

        // get the userID
        String userID = ignoredList.get(position);

        // get the textview
        TextView textUsername = view.findViewById(R.id.text_ignored_user);

        // set the textview
        textUsername.setText(userID);

        return view;
    }
}