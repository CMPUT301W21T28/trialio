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
import com.example.trialio.controllers.ChangeUsernameCommand;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Question;
import com.example.trialio.models.User;

import java.util.ArrayList;

/**
 * This class inherits from ArrayAdapter and is responsible for adapting a Question object into
 * the GUI ListView item to be displayed on the app screen. This ArrayAdapter is referenced from
 * QuestionForumActivity.
 */
public class QuestionArrayAdapter extends ArrayAdapter<Question> {

    private Context context;
    private ArrayList<Question> questionsList;

    public QuestionArrayAdapter(Context context, ArrayList<Question> questionsList) {
        super(context, 0, questionsList);
        this.questionsList = questionsList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_question_forum, parent, false);
        }

        Question question = questionsList.get(position);

        TextView authorID = view.findViewById(R.id.questionAuthorID);
        TextView title = view.findViewById(R.id.questionTitle);
        TextView body = view.findViewById(R.id.questionBody);

        // set text views
        UserManager manager = new UserManager();
        manager.getUserById(question.getUserId(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                authorID.setText(user.getUsername());
            }
        });
        title.setText(question.getTitle());
        body.setText(question.getBody());

        return view;
    }
}
