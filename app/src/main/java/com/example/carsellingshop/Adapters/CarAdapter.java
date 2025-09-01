package com.example.carsellingshop.Adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> implements Filterable {

    // Click to order/unorder
    public interface OnOrderClickListener { void onOrderClick(Car car); }
    private OnOrderClickListener orderClickListener;
    public void setOnOrderClickListener(OnOrderClickListener l) { this.orderClickListener = l; }

    public interface OnDetailsClickListener { void onDetailsClick(Car car); }
    private OnDetailsClickListener detailsClickListener;
    public void setOnDetailsClickListener(OnDetailsClickListener l) { this.detailsClickListener = l; }

    // Track ordered cars
    private final Set<String> orderedIds = new HashSet<>();
    public void setOrderedCarIds(Set<String> ids) {
        orderedIds.clear();
        if (ids != null) orderedIds.addAll(ids);
        notifyDataSetChanged();
    }
    public void markCarOrdered(String carId, boolean ordered) {
        if (carId == null) return;
        if (ordered) orderedIds.add(carId); else orderedIds.remove(carId);
        // update only the affected row
        for (int i = 0; i < visibleList.size(); i++) {
            Car c = visibleList.get(i);
            if (carId.equals(c.getId())) { notifyItemChanged(i); break; }
        }
    }

    private final List<Car> visibleList; // displayed
    private final List<Car> fullList;    // source for filter

    public CarAdapter(List<Car> initial) {
        this.visibleList = new ArrayList<>(initial);
        this.fullList = new ArrayList<>(initial);
        setHasStableIds(true); // smoother updates if IDs are stable
    }

    /** Replace entire data set (e.g., after Firestore fetch) */
    public void replaceData(List<Car> newData) {
        fullList.clear();
        fullList.addAll(newData);
        visibleList.clear();
        visibleList.addAll(newData);
        notifyDataSetChanged();
    }

    @Override public long getItemId(int position) {
        String id = visibleList.get(position).getId();
        return id != null ? id.hashCode() : RecyclerView.NO_ID;
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


        Glide.with(holder.itemView.getContext())
                .load(car.getImageUrl())
                .centerCrop() // fill nicely
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.carImageView);

        boolean isOrdered = car.getId() != null && orderedIds.contains(car.getId());
        styleOrderButton(holder.btnOrder, isOrdered);

        holder.btnOrder.setOnClickListener(v -> {
            if (orderClickListener != null) orderClickListener.onOrderClick(car);
        });

        holder.btnDetails.setOnClickListener(v -> {
                if (detailsClickListener != null) detailsClickListener.onDetailsClick(car);
        });
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

    private void styleOrderButton(Button b, boolean isOrdered) {
        if (isOrdered) {
            b.setText("ORDERED");
            b.setEnabled(true); // keep clickable if you allow "cancel"
            // If you use theme tint:
            try {
                b.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(b.getContext(), android.R.color.darker_gray)));
            } catch (Exception ignored) {}
            // If you use custom drawable pills instead, prefer:
            // b.setBackgroundResource(R.drawable.btn_gray_pill);
        } else {
            b.setText("ORDER");
            b.setEnabled(true);
            // Reset tint (if using theme tints)
            try { b.setBackgroundTintList(null); } catch (Exception ignored) {}
            // Or custom drawable:
            // b.setBackgroundResource(R.drawable.btn_green_pill);
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