package com.example.lostmypet.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lostmypet.DAO.DAOComment;
import com.example.lostmypet.R;
import com.example.lostmypet.models.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import timber.log.Timber;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private final Context context;
    private final ArrayList<Comment> list;

    public CommentsAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.CommentsViewHolder holder, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Comment comment = list.get(position);
        holder.usernameTextView.setText(comment.getUsername());
        holder.dateTextView.setText(comment.getDate());
        holder.messageTextView.setText(comment.getMessage());
        if(Objects.requireNonNull(currentUser).getUid().equals(comment.getUserID())){
            holder.deleteTextView.setVisibility(View.VISIBLE);
        } else {
            holder.deleteTextView.setVisibility(View.GONE);
        }

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("Users/").child(comment.getUserID());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageURL = uri.toString();
            Glide.with(context).load(imageURL).into(holder.userImage);
        }).addOnFailureListener(exception -> holder.userImage.setImageResource(R.drawable.default_profile_pic));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, dateTextView, messageTextView, deleteTextView;
        ImageView userImage;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameTextView = itemView.findViewById(R.id.tv_username);
            dateTextView = itemView.findViewById(R.id.tv_date);
            messageTextView = itemView.findViewById(R.id.tv_message);
            deleteTextView = itemView.findViewById(R.id.tv_delete);
            userImage = itemView.findViewById(R.id.imv_user);

            deleteTextView.setOnClickListener(v -> deleteComment());
        }

        public void deleteComment() {
            DAOComment daoComment = new DAOComment();
            daoComment.remove(list.get(getAdapterPosition()).getCommentID())
                    .addOnSuccessListener(success -> Timber.w("Comment deleted!"))
                    .addOnFailureListener(err -> Timber.w("Removal failed"));

        }

    }
}
