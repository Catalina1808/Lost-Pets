package com.example.lostmypet.helpers;

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
import com.example.lostmypet.activities.ViewAnnouncementActivity;
import com.example.lostmypet.activities.WelcomeActivity;
import com.example.lostmypet.models.Announcement;
import com.example.lostmypet.models.AnnouncementItemRV;
import com.example.lostmypet.models.Favorite;
import com.example.lostmypet.models.LocationPoint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_announcements, parent, false);
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
            holder.genderImage.setImageResource(R.drawable.female_icon);
        } else if(announcement.getGender().equals(context.getResources().getString(R.string.male))){
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
        }).addOnFailureListener(exception -> Toast.makeText(context, "The image could not be loaded.",
                Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AnnouncementsViewHolder extends RecyclerView.ViewHolder{

        TextView name, animal, city, type;
        ImageView petImage, genderImage;
        Button moreButton, editButton;
        ImageButton favoriteButton, deleteButton;
        LinearLayout layoutEditable;

        public AnnouncementsViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.tv_name);
            animal=itemView.findViewById(R.id.tv_animal);
            city=itemView.findViewById(R.id.tv_city);
            type=itemView.findViewById(R.id.tv_type);
            petImage=itemView.findViewById(R.id.imv_pet);
            genderImage=itemView.findViewById(R.id.imv_gender);
            moreButton=itemView.findViewById(R.id.btn_more);
            editButton=itemView.findViewById(R.id.btn_edit);
            deleteButton=itemView.findViewById(R.id.imbtn_delete);
            favoriteButton=itemView.findViewById(R.id.imbtn_favorite);
            layoutEditable=itemView.findViewById(R.id.layout_editable);

            moreButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewAnnouncementActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("announcement", list.get(getAdapterPosition()));
                intent.putExtras(bundle);
                context.startActivity(intent);
            });

            //change the heart color when an announcement is added or deleted from the favorites
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(list.get(getAdapterPosition()).getFavoriteID()==null) {
                        favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.red),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                        addToFavorite();
                    } else {
                        favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.dark_orange_alpha_2),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                        deleteFromFavorite();
                    }
                }
            });

        }

        //add the announcement to favorites
        public void addToFavorite(){
            DAOFavorite daoFavorite = new DAOFavorite();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            Favorite favorite = new Favorite(currentUser.getUid(),
                    list.get(getAdapterPosition()).getAnnouncementId());

            daoFavorite.add(favorite).
                    addOnSuccessListener(succes -> Toast.makeText(context,
                            "Announcement added to favorite",
                            Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(err -> Toast.makeText(context,
                            "Insertion failed",
                            Toast.LENGTH_SHORT).show());

            list.get(getAdapterPosition()).setFavoriteID(daoFavorite.getId());
            }

        public void deleteFromFavorite(){
            DAOFavorite daoFavorite = new DAOFavorite();
            daoFavorite.remove(list.get(getAdapterPosition()).getFavoriteID()).
                    addOnSuccessListener(succes -> Toast.makeText(context,
                            "Announcement removed from favorite",
                            Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(err -> Toast.makeText(context,
                            "Removal failed",
                            Toast.LENGTH_SHORT).show());


            list.get(getAdapterPosition()).setFavoriteID(null);
        }
        }
}
