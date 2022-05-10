package com.example.lostmypet.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lostmypet.R;
import com.example.lostmypet.models.AnnouncementItemRV;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AnnouncementsAdapter  extends RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementsViewHolder> {

    private Context context;
    private ArrayList<AnnouncementItemRV> list;

    public AnnouncementsAdapter(Context context, ArrayList<AnnouncementItemRV> list) {
        this.context = context;
        this.list = list;
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
        if(announcement.getGender().equals(context.getResources().getString(R.string.female))){
            holder.genderImage.setImageResource(R.drawable.female_icon);
        } else if(announcement.getGender().equals(context.getResources().getString(R.string.male))){
            holder.genderImage.setImageResource(R.drawable.male_icon);
        } else {
            holder.genderImage.setVisibility(View.GONE);
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
        public AnnouncementsViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.tv_name);
            animal=itemView.findViewById(R.id.tv_animal);
            city=itemView.findViewById(R.id.tv_city);
            type=itemView.findViewById(R.id.tv_type);
            petImage=itemView.findViewById(R.id.imv_pet);
            genderImage=itemView.findViewById(R.id.imv_gender);
        }
    }
}
