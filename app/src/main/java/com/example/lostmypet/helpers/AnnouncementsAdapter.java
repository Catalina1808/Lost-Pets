package com.example.lostmypet.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lostmypet.DAO.DAOAnnouncement;
import com.example.lostmypet.DAO.DAOFavorite;
import com.example.lostmypet.DAO.DAOLocationPoint;
import com.example.lostmypet.R;
import com.example.lostmypet.activities.EditAnnouncementActivity;
import com.example.lostmypet.activities.ViewAnnouncementActivity;
import com.example.lostmypet.models.Announcement;
import com.example.lostmypet.models.AnnouncementItemRV;
import com.example.lostmypet.models.Favorite;
import com.example.lostmypet.models.LocationPoint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import timber.log.Timber;

public class AnnouncementsAdapter  extends RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementsViewHolder> {

    private final Context context;
    private final ArrayList<AnnouncementItemRV> list;
    private boolean isEditable = false;

    public AnnouncementsAdapter(Context context, ArrayList<AnnouncementItemRV> list) {
        this.context = context;
        this.list = list;
    }

    public AnnouncementsAdapter(Context context, ArrayList<AnnouncementItemRV> list, boolean isEditable) {
        this.context = context;
        this.list = list;
        this.isEditable = isEditable;
    }

    @NonNull
    @Override
    public AnnouncementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_announcement, parent, false);
        return  new AnnouncementsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementsViewHolder holder, int position) {
        AnnouncementItemRV announcement = list.get(position);
        holder.name.setText(announcement.getName());
        holder.animal.setText(announcement.getAnimal());
        if(announcement.getCity()==null){
            holder.city.setVisibility(View.GONE);
        } else {
            holder.city.setText(announcement.getCity());
        }
        holder.type.setText(announcement.getType());
        if(announcement.getFavoriteID()!=null) {
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.red),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context,
                    R.color.dark_orange_alpha_2), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if(announcement.getGender().equals(context.getResources().getString(R.string.female))){
            holder.genderImage.setVisibility(View.VISIBLE);
            holder.genderImage.setImageResource(R.drawable.female_icon);
        } else if(announcement.getGender().equals(context.getResources().getString(R.string.male))){
            holder.genderImage.setVisibility(View.VISIBLE);
            holder.genderImage.setImageResource(R.drawable.male_icon);
        } else {
            holder.genderImage.setVisibility(View.GONE);
        }
        if(isEditable){
            holder.layoutEditable.setVisibility(View.VISIBLE);
        }

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("Announcements/").child(announcement.getAnnouncementId());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageURL = uri.toString();
            Glide.with(context).load(imageURL).into(holder.petImage);
        }).addOnFailureListener(exception -> holder.petImage.setImageResource(R.drawable.icon_no_image_pet));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AnnouncementsViewHolder extends RecyclerView.ViewHolder {

        TextView name, animal, city, type;
        ImageView petImage, genderImage;
        Button moreButton, editButton;
        ImageButton favoriteButton, deleteButton;
        LinearLayout layoutEditable;
        private final FirebaseDatabase database;

        public AnnouncementsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_name);
            animal = itemView.findViewById(R.id.tv_animal);
            city = itemView.findViewById(R.id.tv_city);
            type = itemView.findViewById(R.id.tv_type);
            petImage = itemView.findViewById(R.id.imv_pet);
            genderImage = itemView.findViewById(R.id.imv_gender);
            moreButton = itemView.findViewById(R.id.btn_more);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.imbtn_delete);
            favoriteButton = itemView.findViewById(R.id.imbtn_favorite);
            layoutEditable = itemView.findViewById(R.id.layout_editable);

            database = FirebaseDatabase.getInstance(
                    "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");

            moreButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewAnnouncementActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("announcement", list.get(getAdapterPosition()));
                intent.putExtras(bundle);
                context.startActivity(intent);
            });

            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditAnnouncementActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("announcement", list.get(getAdapterPosition()));
                intent.putExtras(bundle);
                context.startActivity(intent);
            });

            deleteButton.setOnClickListener(v -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Delete");
                alert.setIcon(R.drawable.ic_delete);
                alert.setMessage("Are you sure you want to delete this announcement?");
                alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    //if user is sure then delete
                    deleteAllLocationPoints();
                    deleteFromAllUsersFavorites();
                    deleteAnnouncement();
                    deletePicture();
                });
                alert.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel());
                alert.show();
            });

            //change the heart color when an announcement is added or deleted from the favorites
            favoriteButton.setOnClickListener(v -> {
                if (list.get(getAdapterPosition()).getFavoriteID() == null) {
                    favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.red),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    addToFavorite();
                } else {
                    favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.dark_orange_alpha_2),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    deleteFromCurrentUserFavorites();
                }
            });

        }

        //add the announcement to favorites
        public void addToFavorite() {
            DAOFavorite daoFavorite = new DAOFavorite();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            Favorite favorite = new Favorite(Objects.requireNonNull(currentUser).getUid(),
                    list.get(getAdapterPosition()).getAnnouncementId());

            daoFavorite.add(favorite)
                    .addOnSuccessListener(success -> Toast.makeText(context,
                            "Announcement added to favorite",
                            Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(err -> Toast.makeText(context,
                            "Insertion failed",
                            Toast.LENGTH_SHORT).show());

            list.get(getAdapterPosition()).setFavoriteID(daoFavorite.getId());
        }

        public void deleteFromCurrentUserFavorites() {
            DAOFavorite daoFavorite = new DAOFavorite();
            daoFavorite.remove(list.get(getAdapterPosition()).getFavoriteID())
                    .addOnSuccessListener(success -> Toast.makeText(context,
                            "Announcement removed from favorite",
                            Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(err -> Toast.makeText(context,
                            "Removal failed",
                            Toast.LENGTH_SHORT).show());


            list.get(getAdapterPosition()).setFavoriteID(null);
        }

        public void deleteFromAllUsersFavorites() {
            DAOFavorite daoFavorite = new DAOFavorite();

            DatabaseReference databaseReferenceFavorites = database.getReference(Favorite.class.getSimpleName());
            databaseReferenceFavorites.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Objects.equals(Objects.requireNonNull(dataSnapshot.getValue(Favorite.class))
                                .getAnnouncementID(), list.get(getAdapterPosition()).getAnnouncementId())) {
                            Favorite favorite = dataSnapshot.getValue(Favorite.class);
                            daoFavorite.remove(Objects.requireNonNull(favorite).getFavoriteID())
                                    .addOnSuccessListener(success -> Timber.w("Favorites removed"))
                                    .addOnFailureListener(err -> Timber.w("Favorites removal failed"));
                        }}}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        public void deleteAllLocationPoints() {
            DAOLocationPoint daoLocationPoint = new DAOLocationPoint();

            DatabaseReference databaseReferenceFavorites = database.getReference(LocationPoint.class.getSimpleName());
            databaseReferenceFavorites.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Objects.equals(Objects.requireNonNull(dataSnapshot.getValue(LocationPoint.class))
                                .getAnnouncementID(), list.get(getAdapterPosition()).getAnnouncementId())) {
                            LocationPoint locationPoint = dataSnapshot.getValue(LocationPoint.class);
                            daoLocationPoint.remove(Objects.requireNonNull(locationPoint).getLocationPointID())
                                    .addOnSuccessListener(success -> Timber.w("Locations removed"))
                                    .addOnFailureListener(err -> Timber.w("Locations removal failed"));
                        }}}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        public void deleteAnnouncement() {
            DAOAnnouncement daoAnnouncement = new DAOAnnouncement();

            DatabaseReference databaseReferenceFavorites = database
                    .getReference(Announcement.class.getSimpleName());
            databaseReferenceFavorites.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Objects.equals(Objects.requireNonNull(dataSnapshot
                                        .getValue(Announcement.class)).getAnnouncementID(),
                                list.get(getAdapterPosition()).getAnnouncementId())) {

                            Announcement announcement = dataSnapshot.getValue(Announcement.class);
                            daoAnnouncement.remove(Objects.requireNonNull(announcement).getAnnouncementID())
                                    .addOnSuccessListener(success -> Timber.w("Announcement removed"))
                                    .addOnFailureListener(err -> Timber.w("Announcement removal failed"));
                        }}}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        private void deletePicture(){
            FirebaseStorage.getInstance()
                    .getReference("Announcements/")
                    .child(list.get(getAdapterPosition()).getAnnouncementId())
                    .delete()
                    .addOnSuccessListener(success -> Timber.w("Picture removed"))
                    .addOnFailureListener(err -> Timber.w("Picture removal failed"));
        }
    }
}
