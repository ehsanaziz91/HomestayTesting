package com.example.homestaytesting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context Context;
    private List<Upload> Upload;

    public ImageAdapter(Context context, List<Upload> uploads){
        Context = context;
        Upload = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(Context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = Upload.get(position);
        holder.textViewName.setText(uploadCurrent.getImgName());
        holder.textViewDetails.setText(uploadCurrent.getImgDetails());
        holder.textViewLocation.setText(uploadCurrent.getImgLocation());
        holder.textViewDetails.setText(uploadCurrent.getImgDetails());
        holder.textViewDetails.setText(uploadCurrent.getImgDetails());
        Picasso.with(Context)
                .load(uploadCurrent.getImgUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return Upload.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewContact;
        public TextView textViewPrice;
        public TextView textViewLocation;
        public TextView textViewDetails;
        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(View itemView){
            super(itemView);

            textViewContact = itemView.findViewById(R.id.textViewContact);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewDetails = itemView.findViewById(R.id.textViewDetails);
            textViewName = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imgView);
        }
    }
}
