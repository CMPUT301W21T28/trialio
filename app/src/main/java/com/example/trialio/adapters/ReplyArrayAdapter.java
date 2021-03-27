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
import com.example.trialio.models.Reply;

import java.util.ArrayList;

public class ReplyArrayAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Reply> replyList;

    public ReplyArrayAdapter(Context context, ArrayList<Reply> replyList) {
        super(context, 0, replyList);
        this.replyList = replyList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_reply, parent,false);
        }

        Reply reply = replyList.get(position);

        TextView replyAuthorID = view.findViewById(R.id.replyAuthorID);
        TextView replyBody = view.findViewById(R.id.replyBody);

        // set text views
        replyAuthorID.setText(reply.getUser().getUsername());
        replyBody.setText(reply.getBody());

        return view;
    }
}
