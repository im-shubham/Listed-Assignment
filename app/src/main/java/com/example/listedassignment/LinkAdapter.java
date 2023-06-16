package com.example.listedassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.listedassignment.Link;
import com.example.listedassignment.R;

import java.util.List;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.LinkViewHolder> {

    private List<Link> linkList;

    public LinkAdapter(List<Link> linkList) {
        this.linkList = linkList;
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_link, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkViewHolder holder, int position) {
        Link link = linkList.get(position);
        holder.bind(link);
    }

    @Override
    public int getItemCount() {
        return linkList.size();
    }

    public class LinkViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailImageView;
        private TextView titleTextView;
        private TextView urlTextView;

        public LinkViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            urlTextView = itemView.findViewById(R.id.urlTextView);
        }

        public void bind(Link link) {
            titleTextView.setText(link.getTitle());
            urlTextView.setText(link.getUrl());

            Glide.with(itemView.getContext())
                    .load(link.getThumbnail())
                    .placeholder(R.drawable.img) // placeholder image while loading
                    .into(thumbnailImageView);
        }
    }
}
