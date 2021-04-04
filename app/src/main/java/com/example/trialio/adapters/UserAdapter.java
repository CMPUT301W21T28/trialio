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
import com.example.trialio.models.User;

import java.util.ArrayList;

/**
 * This class inherits from ArrayAdapter and is responsible for adapting a User object into the GUI
 * ListView item to be displayed on the app screen. This ArrayAdapter is referenced from
 * ExperimentSettingsActivity
 */
public class UserAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> ignoredList;

    public UserAdapter(Context context, ArrayList<String> ignoredList) {
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