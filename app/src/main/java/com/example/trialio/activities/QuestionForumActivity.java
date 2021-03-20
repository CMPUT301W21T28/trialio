package com.example.trialio.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.trialio.R;
import com.example.trialio.adapters.QuestionArrayAdapter;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.fragments.AddQuestionFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Question;
import com.example.trialio.models.Trial;

import java.util.ArrayList;

public class QuestionForumActivity extends AppCompatActivity {
    private final Context context = this;
    private Experiment associatedExperiment;
    private QuestionForumManager questionForumManager;
    private ArrayList<Question> questionList;
    private QuestionArrayAdapter questionAdapter;

    String associatedExperimentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_forum_activity);

        // receive experiment info from main -> the question forum belongs to this experiment
        Bundle bundle = getIntent().getExtras();
        associatedExperiment = (Experiment) bundle.getSerializable("experiment");
        // get id here and pass it into the constructor of the quesitonForumManager
        associatedExperimentID = associatedExperiment.getExperimentID();

        // Initialize attributes for the activity
        questionList = new ArrayList<>();  // TODO: make me a colleciton if we opt to make the questionForum its own seperate sub collection
        questionAdapter = new QuestionArrayAdapter(this, questionList);

        questionForumManager = new QuestionForumManager(associatedExperimentID);

        // Set up the adapter for the ListView
        ListView questionsListView = findViewById(R.id.questionForumListView);
        questionsListView.setAdapter(questionAdapter);

        // Set up onClick listeners
        setUpOnClickListeners();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setQuestionList();
    }

    private void setQuestionList() {
        questionForumManager.setOnAllQuestionsFetchCallback(new QuestionForumManager.OnManyQuestionsFetchListener() {
            @Override
            public void onManyQuestionsFetch(ArrayList<Question> questions) {
                questionList.clear();
                questionList.addAll(questions);
                questionAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Sets up on click listeners for the activity.
     */
    private void setUpOnClickListeners() {

        // Called when the user clicks item in experiment list
        ListView questionForumListView = findViewById(R.id.questionForumListView);
        questionForumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(context, QuestionForumActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                // TODO: how to pass in an argument for a question ???
                args.putSerializable("question_details", questionList.get(position));
                intent.putExtras(args);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });

        /**
         * Adds new question
         */
        Button newQuestion = findViewById(R.id.newQuestion);
        newQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddQuestionFragment.class);
                Bundle args = new Bundle();
                args.putSerializable("owener_experiment_info");
                intent.putExtras(args);
                startActivity(intent)
            }
        });
    }

    /**
     * This is called when the user presses confirm on one of the Trial creation fragments
     *
     * @param question The new question that was created in the fragment
     */
    @Override
    public void onOkPressed(Question question) {
        questionForumManager.setOnQuestionFetchListener();
    }

}
