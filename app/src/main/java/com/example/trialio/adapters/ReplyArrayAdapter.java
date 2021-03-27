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
import com.example.trialio.models.Reply;
import com.example.trialio.models.User;

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

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.reply_content, parent, false);
        }

        Reply reply = replyList.get(position);

        // set text views
        TextView replyBody = view.findViewById(R.id.replyBody);
        replyBody.setText(reply.getBody());
        UserManager manager = new UserManager();

        View finalView = view;
        manager.getUserById(reply.getUserId(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                TextView replyAuthorID = finalView.findViewById(R.id.replyAuthorID);
                replyAuthorID.setText(user.getUsername());
            }
        });

        return view;
    }
}
