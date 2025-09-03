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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> implements Filterable {

   
    public interface OnOrderClickListener { void onOrderClick(Car car); }
    private OnOrderClickListener orderClickListener;
    public void setOnOrderClickListener(OnOrderClickListener l) { this.orderClickListener = l; }


    public interface OnDetailsClickListener { void onDetailsClick(Car car); }
    private OnDetailsClickListener detailsClickListener;
    public void setOnDetailsClickListener(OnDetailsClickListener l) { this.detailsClickListener = l; }

    public interface OnDeleteClickListener { void onDeleteClick(Car car); }
    private OnDeleteClickListener deleteClickListener;
    public void setOnDeleteClickListener(OnDeleteClickListener l) { this.deleteClickListener = l; }

    // ---- Fields ----
    private final boolean isAdmin;  // admin/user mode
    // carId -> null|"pending"|"confirmed"
    private final Map<String, String> orderStatusByCarId = new HashMap<>();
    private final List<Car> visibleList;
    private final List<Car> fullList;

    // ---- Constructor ----
    public CarAdapter(List<Car> initial, boolean isAdmin) {
        this.visibleList = new ArrayList<>(initial);
        this.fullList = new ArrayList<>(initial);
        this.isAdmin = isAdmin;
        setHasStableIds(true);
    }

    // ---- Manage Data ----

    public void replaceData(List<Car> newData) {
        fullList.clear();
        fullList.addAll(newData);
        visibleList.clear();
        visibleList.addAll(newData);
        notifyDataSetChanged();
    }


    /** Replace the entire status map (user mode). */
    public void setOrderStatusMap(Map<String, String> map) {
        orderStatusByCarId.clear();
        if (map != null) orderStatusByCarId.putAll(map);
        notifyDataSetChanged();
    }

    /** Update one car’s status (user mode). */
    public void setOrderStatusForCar(String carId, String statusOrNull) {
        if (carId == null) return;
        if (statusOrNull == null) orderStatusByCarId.remove(carId);
        else orderStatusByCarId.put(carId, statusOrNull);
        for (int i = 0; i < visibleList.size(); i++) {
            Car c = visibleList.get(i);
            if (carId.equals(c.getId())) { notifyItemChanged(i); break; }
        }
    }


    @NonNull
    @Override

    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
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
                .centerCrop()
                .placeholder(R.drawable.ic_car_placeholder)
                .error(R.drawable.ic_car_placeholder)
                .into(holder.carImageView);

        if (isAdmin) {
            // ---- ADMIN MODE ----
            holder.btnOrder.setVisibility(View.GONE);
            holder.btnDetails.setVisibility(View.GONE);

            holder.btnAdminDelete.setVisibility(View.VISIBLE);
            holder.btnAdminDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) deleteClickListener.onDeleteClick(car);
            });

        } else {
            // ---- USER MODE ----
            holder.btnAdminDelete.setVisibility(View.GONE);

            String status = orderStatusByCarId.get(car.getId()); // null | "pending" | "confirmed"
            styleOrderButton(holder.btnOrder, status);

            holder.btnOrder.setOnClickListener(v -> {
                if (orderClickListener != null) orderClickListener.onOrderClick(car);
            });

            holder.btnDetails.setOnClickListener(v -> {
                if (detailsClickListener != null) detailsClickListener.onDetailsClick(car);
            });
        }

    }

    @Override
    public int getItemCount() { return visibleList.size(); }

    @Override
    public long getItemId(int position) {
        String id = visibleList.get(position).getId();
        return id != null ? id.hashCode() : RecyclerView.NO_ID;
    }


    // ---- ViewHolder ----
    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImageView;
        TextView carModelTextView, carPriceTag, tvDiscount, tvDescription;
        Button btnDetails, btnOrder, btnAdminDelete;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImageView = itemView.findViewById(R.id.carImageView);
            carModelTextView = itemView.findViewById(R.id.carModelTextView);
            carPriceTag = itemView.findViewById(R.id.tvPriceTag);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnOrder = itemView.findViewById(R.id.btnOrder);
            btnAdminDelete = itemView.findViewById(R.id.btnAdminDelete);
        }
    }

    private void styleOrderButton(Button b, String status) {
        if (status == null) {
            b.setEnabled(true);
            b.setText("ORDER");
            try { b.setBackgroundTintList(null); } catch (Exception ignored) {}
            return;
        }
        if ("pending".equals(status)) {
            b.setEnabled(true);
            b.setText("PENDING");
            try {
                b.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(b.getContext(), android.R.color.darker_gray)));
            } catch (Exception ignored) {}
            return;
        }
        if ("confirmed".equals(status)) {
            b.setEnabled(false);
            b.setText("CONFIRMED");
            try {
                b.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(b.getContext(), android.R.color.holo_green_dark)));
            } catch (Exception ignored) {}
            return;
        }
        // fallback
        b.setEnabled(true);
        b.setText("ORDER");
        try { b.setBackgroundTintList(null); } catch (Exception ignored) {}
    }


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