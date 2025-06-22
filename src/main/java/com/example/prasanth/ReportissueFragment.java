package com.example.prasanth;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.Collections;

public class ReportissueFragment extends Fragment {

    private EditText issueTitleEditText;
    private EditText issueDescriptionEditText;
    private Button submitButton;

    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> issuesCollection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_reportissue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        issueTitleEditText = view.findViewById(R.id.issue_title);
        issueDescriptionEditText = view.findViewById(R.id.issue_description);
        submitButton = view.findViewById(R.id.submit_button);

        // Initialize MongoDB
        initializeMongoDB();

        // Set click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIssueInfo();
            }
        });
    }

    private void initializeMongoDB() {
        Toast.makeText(requireContext(), "Connecting to MongoDB...", Toast.LENGTH_SHORT).show();
        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress("10.0.2.2", 27017))))
                .build());
        mongoDatabase = mongoClient.getDatabase("issues_db");
        issuesCollection = mongoDatabase.getCollection("issues");
        Toast.makeText(requireContext(), "MongoDB Connection Initialized", Toast.LENGTH_SHORT).show();
    }

    private void saveIssueInfo() {
        String issueTitle = issueTitleEditText.getText().toString();
        String issueDescription = issueDescriptionEditText.getText().toString();

        if (issueTitle.isEmpty() || issueDescription.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Document to insert
        Document issueDoc = new Document("title", issueTitle)
                .append("description", issueDescription);

        // Use AsyncTask to save the document in the background
        new InsertIssueTask().execute(issueDoc);
    }

    private class InsertIssueTask extends AsyncTask<Document, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Document... documents) {
            try {
                // Insert the document into the collection
                issuesCollection.insertOne(documents[0]);
                return true; // Insert successful
            } catch (MongoException e) {
                e.printStackTrace();
                return false; // Insert failed
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(requireContext(), "Issue reported successfully", Toast.LENGTH_SHORT).show();
                issueTitleEditText.setText(""); // Clear the input fields
                issueDescriptionEditText.setText("");
            } else {
                Toast.makeText(requireContext(), "Error reporting issue", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
