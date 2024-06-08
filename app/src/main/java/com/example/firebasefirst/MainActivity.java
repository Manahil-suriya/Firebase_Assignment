package com.example.firebasefirst;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabnotes;
    RecyclerView rvNotes;
    NotesAdapter adapter;
    SearchView searchView;
    Button btnSortTitle, btnSortDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();

        fabnotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.design_add_notes_popup, null);

                TextView tvTimestamp;
                TextInputEditText edTitle, edContent;

                tvTimestamp = view.findViewById(R.id.tvTimestamp);
                edTitle = view.findViewById(R.id.edTitle);
                edContent = view.findViewById(R.id.edContent);

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date date = new Date();
                tvTimestamp.setText(formatter.format(date));

                AlertDialog.Builder addnotes = new AlertDialog.Builder(MainActivity.this)
                        .setView(view)
                        .setTitle("Create New Note")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = edTitle.getText().toString().trim();
                                String content = edContent.getText().toString().trim();

                                HashMap<String, Object> data = new HashMap<>();
                                data.put("title", title);
                                data.put("content", content);
                                data.put("TimeStamp", tvTimestamp.getText().toString().trim());
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("notes")
                                        .push()
                                        .setValue(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                addnotes.create();
                addnotes.show();
            }
        });

        btnSortTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortNotes("title");
            }
        });

        btnSortDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortNotes("TimeStamp");
            }
        });
    }

    private void init() {
        fabnotes = findViewById(R.id.fabnotes);
        rvNotes = findViewById(R.id.rvNotes);
        searchView = findViewById(R.id.searchView);
        btnSortTitle = findViewById(R.id.btnSortTitle);
        btnSortDate = findViewById(R.id.btnSortDate);

        rvNotes.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("notes");

        FirebaseRecyclerOptions<Notes> options =
                new FirebaseRecyclerOptions.Builder<Notes>()
                        .setQuery(query, Notes.class)
                        .build();

        adapter = new NotesAdapter(options, this);
        rvNotes.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query;
                if (newText.isEmpty()) {
                    query = FirebaseDatabase.getInstance().getReference().child("notes");
                } else {
                    query = FirebaseDatabase.getInstance().getReference().child("notes")
                            .orderByChild("title")
                            .startAt(newText)
                            .endAt(newText + "\uf8ff");
                }
                FirebaseRecyclerOptions<Notes> options =
                        new FirebaseRecyclerOptions.Builder<Notes>()
                                .setQuery(query, Notes.class)
                                .build();
                adapter.updateOptions(options);
                return false;
            }
        });
    }

    private void sortNotes(String criteria) {
        Query query = FirebaseDatabase.getInstance().getReference().child("notes").orderByChild(criteria);
        FirebaseRecyclerOptions<Notes> options =
                new FirebaseRecyclerOptions.Builder<Notes>()
                        .setQuery(query, Notes.class)
                        .build();
        adapter.updateOptions(options);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
