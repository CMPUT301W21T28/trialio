package com.example.trialio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private final Context context = this;

    private ExperimentManager experimentManager;
    private ArrayAdapterExperiment experimentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an experiment manager for the activity
        experimentManager = new ExperimentManager();
        experimentAdapter = new ArrayAdapterExperiment(this, experimentManager.getExperimentList());

        // Set up the adapter for the list and experiment manager
        ListView experimentListView = findViewById(R.id.list_experiment);
        experimentListView.setAdapter(experimentAdapter);
        experimentManager.setAdapter(experimentAdapter);

        experimentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, ExperimentActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                args.putSerializable("experiment", experimentManager.getExperimentList().get(i));
                intent.putExtras(args);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });


        CollectionReference aCollection = FirebaseFirestore.getInstance().collection("testing");

        aCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (A a : (ArrayList<A>) value.toObjects(A.class)) {
                    Log.d(TAG, " aList: " + a.toString());
                    //Log.d(TAG, " casted: " + ((B)a).toString());
                }
                for (B b : (ArrayList<B>) value.toObjects(B.class)) {
                    Log.d(TAG, " bList: " + b.toString());
                }
            }
        });

        A thing = new B(999, "i am number 999");
        Log.d(TAG, " athing: " + thing.toString());
        Log.d(TAG, " bthing: " + ((B)thing).toString());

        aCollection
                .document("1")
                .set(new B(1, "i am number 1"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data was added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data was not added!" + e.toString());
                    }
                });

        aCollection
                .document("2")
                .set(new B(22, "i am number 2"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data was added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data was not added!" + e.toString());
                    }
                });

        aCollection
                .document("3")
                .set(new B(333, "i am number 3"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data was added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data was not added!" + e.toString());
                    }
                });



        /////////////////////////////////

        CollectionReference alistCollection = FirebaseFirestore.getInstance().collection("testing2");

        alistCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (listofAs alist : (ArrayList<listofAs>) value.toObjects(listofAs.class.)) {
                    for (A a : alist.getAlist()) {
                        Log.d(TAG, " aList: " + a.toString());
//                        Log.d(TAG, " casted: " + ((B)a).toString());
//                        Log.d(TAG, " casted: " + ((C)a).toString());
                    }
                }
                for (DocumentSnapshot dc : value.getDocuments()) {
                    if ((int) dc.get("type") == 0) {
                        for (dc.) dc.toObject()
                        for (A a : alist.getAlist()) {
                            Log.d(TAG, " aList: " + a.toString());
//                        Log.d(TAG, " casted: " + ((B)a).toString());
//                        Log.d(TAG, " casted: " + ((C)a).toString());
                        }
                    }
                }
            }
        });

        listofAs alist1 = new listofAs(0);
        alist1.addA(new B(777, "i am number 777"));
        alistCollection
                .document("1")
                .set(alist1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data was added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data was not added!" + e.toString());
                    }
                });

        listofAs alist2 = new listofAs(0);
        alist2.addA(new B(666, "i am number 666"));
        alist2.addA(new B(555, "i am number 555"));
        alist2.addA(new B(11, "i am number 11"));
        alistCollection
                .document("2")
                .set(alist2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data was added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data was not added!" + e.toString());
                    }
                });

        listofAs alist3 = new listofAs(1);
        alist3.addA(new C(111111, 345));
        alist3.addA(new C(22222, 678));
        alist3.addA(new C(121, 910));
        alistCollection
                .document("3")
                .set(alist3)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data was added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data was not added!" + e.toString());
                    }
                });

//        // Some code to test publishing an experiment
//        User u = new User("uid5");
//        Region r = new Region();
//        ExperimentSettings eset = new ExperimentSettings("experimentDescription5", r, u, true);
//        Experiment e = new Experiment(ExperimentManager.getNewExperimentID(), eset, "experimentType5", 5);
//        experimentManager.publishExperiment(e);
    }
}

