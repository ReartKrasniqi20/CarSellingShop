package com.example.carsellingshop.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.R;

import java.util.List;

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

        holder.carModelTextView.setText(car.getModel());
        holder.carPriceTextView.setText("Price: $" + car.getPrice());

        // Load image with Glide
        Glide.with(holder.itemView.getContext())
                .load(car.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground) // Optional: default image
                .into(holder.carImageView);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImageView;
        TextView carModelTextView, carPriceTextView;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImageView = itemView.findViewById(R.id.carImageView);
            carModelTextView = itemView.findViewById(R.id.carModelTextView);
            carPriceTextView = itemView.findViewById(R.id.carPriceTextView);
        }
    }
}
