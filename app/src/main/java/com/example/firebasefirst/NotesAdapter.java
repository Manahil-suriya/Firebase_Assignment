package com.example.firebasefirst;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class NotesAdapter extends FirebaseRecyclerAdapter<Notes, NotesAdapter.NotesViewHolder> {
    Context parent;

    public NotesAdapter(@NonNull FirebaseRecyclerOptions<Notes> options, Context context) {
        super(options);
        this.parent = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NotesViewHolder holder, int position, @NonNull Notes model) {
        holder.tvTitle.setText(model.getTitle());
        holder.tvTimeStamp.setText(model.getTimeStamp());
        holder.tvContent.setText(model.getContent());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override

            public boolean onLongClick(View v) {
                View view = LayoutInflater.from(parent).inflate(R.layout.design_add_notes_popup, null);

                TextInputEditText edTitle = view.findViewById(R.id.edTitle);
                TextInputEditText edContent = view.findViewById(R.id.edContent);
                TextView tvTimestamp = view.findViewById(R.id.tvTimestamp);

                edTitle.setText(model.getTitle());
                edContent.setText(model.getContent());

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date date = new Date();
                tvTimestamp.setText(formatter.format(date));

                AlertDialog.Builder updateDialog = new AlertDialog.Builder(parent)
                        .setTitle("Update Note")
                        .setView(view)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = edTitle.getText().toString().trim();
                                String content = edContent.getText().toString().trim();
                                String timestamp = tvTimestamp.getText().toString().trim();

                                HashMap<String, Object> data = new HashMap<>();
                                data.put("title", title);
                                data.put("content", content);
                                data.put("TimeStamp", timestamp);

                                getRef(position)
                                        .updateChildren(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(parent, "Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(parent, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getRef(position)
                                        .removeValue()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(parent, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(parent, "Note deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                updateDialog.show();
                updateDialog.create();
                return false;
            }
        });
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_note_item_design, parent, false);
        return new NotesViewHolder(v);
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeStamp, tvTitle, tvContent;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimeStamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }

    public void updateOptions(FirebaseRecyclerOptions<Notes> options) {
        super.updateOptions(options);
    }
}
