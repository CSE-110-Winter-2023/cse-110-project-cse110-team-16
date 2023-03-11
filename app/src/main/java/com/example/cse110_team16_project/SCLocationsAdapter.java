package com.example.cse110_team16_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class SCLocationsAdapter extends RecyclerView.Adapter<SCLocationsAdapter.ViewHolder> {
    private List<SCLocation> locations = Collections.emptyList();

    private Consumer<SCLocation> onSCLocationDeleteClicked;

    public void setOnSCLocationDeleteClickListener(Consumer<SCLocation> onSCLocationDeleteClicked) {
        this.onSCLocationDeleteClicked = onSCLocationDeleteClicked;
    }

    /**
     * This time around, the ViewHolder is much simpler, just data.
     * This is closer to "modern" Kotlin Android conventions.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View itemView;
        public final TextView nameView;
        public final TextView codeView;
        public final View deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            // Populate the text views...
            this.nameView = itemView.findViewById(R.id.location_item_label);
            this.codeView = itemView.findViewById(R.id.location_item_code);
            this.deleteButton = itemView.findViewById(R.id.location_item_delete);
        }

        public void bind(SCLocation location) {
            nameView.setText(location.getLabel());
            codeView.setText(location.getPublicCode());
            deleteButton.setOnClickListener(v -> onSCLocationDeleteClicked.accept(location));
        }
    }

    public void setSCLocations(List<SCLocation> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        var view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        var location = locations.get(position);
        holder.bind(location);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    @Override
    public long getItemId(int position) {
        // We don't actually have a unique int/long ID on the SCLocation object, so instead
        // we generate a unique ID based on the label. It is possible that two locations
        // could have different labels but the same hash code, but it is beyond unlikely.
        return locations.get(position).getPublicCode().hashCode();
    }
}

