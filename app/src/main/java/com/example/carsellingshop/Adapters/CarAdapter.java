package com.example.carsellingshop.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> implements Filterable {

    private final List<Car> visibleList; // what RecyclerView shows
    private final List<Car> fullList;    // full data to filter

    public CarAdapter(List<Car> initial) {
        this.visibleList = new ArrayList<>(initial);
        this.fullList = new ArrayList<>(initial);
    }

    /** Call this after loading from Firestore */
    public void replaceData(List<Car> newData) {
        fullList.clear();
        fullList.addAll(newData);
        visibleList.clear();
        visibleList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = visibleList.get(position);

        holder.carModelTextView.setText(car.getModel() == null ? "" : car.getModel());
        String priceText = NumberFormat.getCurrencyInstance(Locale.US).format(car.getPrice());
        holder.carPriceTag.setText(priceText);
        int disc = (int) car.getDiscount();
        holder.tvDiscount.setText(disc > 0 ? "Discount - " + disc + "%" : "");
        holder.tvDescription.setText(car.getDescription() == null ? "" : car.getDescription());

        Glide.with(holder.itemView.getContext())
                .load(car.getImageUrl())
                .fitCenter()
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.carImageView);

        // Buttons (hook up later if needed)
        holder.btnDetails.setOnClickListener(v -> {});
        holder.btnOrder.setOnClickListener(v -> {});
    }

    @Override public int getItemCount() { return visibleList.size(); }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImageView;
        TextView carModelTextView, carPriceTag, tvDiscount, tvDescription;
        Button btnDetails, btnOrder;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImageView = itemView.findViewById(R.id.carImageView);
            carModelTextView = itemView.findViewById(R.id.carModelTextView);
            carPriceTag = itemView.findViewById(R.id.tvPriceTag);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnOrder = itemView.findViewById(R.id.btnOrder);
        }
    }

    // ----- Search filter -----
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence constraint) {
                String q = constraint == null ? "" : constraint.toString().trim().toLowerCase(Locale.US);
                List<Car> filtered = new ArrayList<>();
                if (q.isEmpty()) {
                    filtered.addAll(fullList);
                } else {
                    for (Car c : fullList) {
                        String m = c.getModel() == null ? "" : c.getModel().toLowerCase(Locale.US);
                        String d = c.getDescription() == null ? "" : c.getDescription().toLowerCase(Locale.US);
                        if (m.contains(q) || d.contains(q)) filtered.add(c);
                    }
                }
                FilterResults fr = new FilterResults();
                fr.values = filtered;
                fr.count = filtered.size();
                return fr;
            }

            @SuppressWarnings("unchecked")
            @Override protected void publishResults(CharSequence constraint, FilterResults results) {
                visibleList.clear();
                visibleList.addAll((List<Car>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
