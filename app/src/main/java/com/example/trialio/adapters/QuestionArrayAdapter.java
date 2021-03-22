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
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Question;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class QuestionArrayAdapter extends ArrayAdapter {

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

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.question_forum_content, parent,false);
        }

        Question question = questionsList.get(position);

        TextView authorID = view.findViewById(R.id.questionAuthorID);
        TextView title = view.findViewById(R.id.questionTitle);
        TextView body = view.findViewById(R.id.questionBody);

        // set text views
        authorID.setText(question.getUser().getId());
        title.setText(question.getTitle());
        body.setText(question.getBody());

        return view;
    }
}
