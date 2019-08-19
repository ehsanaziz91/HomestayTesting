package com.example.homestaytesting.HomestayPost.MVC;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.homestaytesting.Modal.Upload;
import com.example.homestaytesting.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context Context;
    private List<com.example.homestaytesting.Modal.Upload> Upload;

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
        holder.textViewName.setText(uploadCurrent.getHmName());
        holder.textViewDetails.setText(uploadCurrent.getHmDetails());
        holder.textViewLocation.setText(uploadCurrent.getHmLocation());
        holder.textViewPrice.setText(uploadCurrent.getHmPrice());
        holder.textViewContact.setText(uploadCurrent.getHmContact());
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
        public RatingBar ratingBar;
        public TextView textViewContact;
        public TextView textViewPrice;
        public TextView textViewLocation;
        public TextView textViewDetails;
        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(View itemView){
            super(itemView);

            ratingBar = itemView.findViewById(R.id.rating);
            textViewContact = itemView.findViewById(R.id.tvContact);
            textViewPrice = itemView.findViewById(R.id.tvPrice);
            textViewLocation = itemView.findViewById(R.id.tvLocation);
            textViewDetails = itemView.findViewById(R.id.tvDetails);
            textViewName = itemView.findViewById(R.id.tvName);
            imageView = itemView.findViewById(R.id.imgView);
        }
    }
}
