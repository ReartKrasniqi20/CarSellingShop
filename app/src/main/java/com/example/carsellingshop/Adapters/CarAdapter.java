package com.example.carsellingshop.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<Car> carList;

    public CarAdapter(List<Car> carList) {
        this.carList = carList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);

        holder.carModelTextView.setText(car.getModel() == null ? "" : car.getModel());

        // Price: 55,000.00 style
        String priceText = NumberFormat.getCurrencyInstance(Locale.US)
                .format(car.getPrice());
        holder.carPriceTag.setText(priceText);


        int disc = (int) car.getDiscount();
        holder.tvDiscount.setText(disc > 0 ? "Discount - " + disc + "%" : "");


        holder.tvDescription.setText(
                car.getDescription() != null ? car.getDescription() : ""
        );

        Glide.with(holder.itemView.getContext())
                .load(car.getImageUrl())
                .fitCenter() // show full image
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.carImageView);

        holder.btnDetails.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                // TODO open details for carList.get(pos)
            }
        });

        holder.btnOrder.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                // TODO start order flow for carList.get(pos)
            }
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

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
}
