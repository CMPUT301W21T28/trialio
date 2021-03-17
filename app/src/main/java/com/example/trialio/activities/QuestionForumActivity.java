package com.example.trialio.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.QuestionArrayAdapter;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Question;

import java.util.ArrayList;

public class QuestionForumActivity extends AppCompatActivity {
    private final String TAG = "QuestionForumActivity";
    private final Context context = this;

    private Experiment associatedExperiment;
    private QuestionForumManager questionForumManager;
    private ArrayList<Question> questionList;
    private QuestionArrayAdapter questionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_forum_activity);

        // receive experiment info from main -> the question forum belongs to this experiment
        Bundle bundle = getIntent().getExtras();
        associatedExperiment = (Experiment) bundle.getSerializable("experiment_info_qa");

        // Initialize attributes for the activity
        questionForumManager = new QuestionForumManager();
        questionList = new ArrayList<>();  // TODO: make me a colleciton if we opt to make the questionForum its own seperate sub collection
        questionAdapter = new QuestionArrayAdapter(this, questionList);

        // Set up the adapter for the ListView
        ListView questionsListView = findViewById(R.id.questionForumListView);
        questionsListView.setAdapter(questionAdapter);

        // Set up onClick listeners
        setUpOnClickListeners();

    }

    // TODO: How can I make this work for the question forum -> use setOna
    @Override
    protected void onStart() {
        super.onStart();

        // Fetch data for the list view
        QuestionForumManager.setOnAllQuestionsFetchCallback(new QuestionManager.OnManyQuestionsFetchListener() {
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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, QuestionForumActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                // TODO: how to pass in an argument for a question ???
                // args.putSerializable("experiment", experimentList.get(i));
                intent.putExtras(args);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });

        // TODO: finish fragment
        Button newQuestion = (Button) findViewById(R.id.newQuestion);
        newQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

//    @Override
//    public void onOkPressed(Question newQuestion) {
//
//        question.getTrialManager().addTrial(newTrial);
//        experimentManager.editExperiment(experiment.getExperimentID(), experiment);
//    }

}
