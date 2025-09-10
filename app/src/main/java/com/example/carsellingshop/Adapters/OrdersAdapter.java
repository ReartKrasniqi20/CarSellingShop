package com.example.carsellingshop.Adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsellingshop.Model.Order;
import com.example.carsellingshop.R;
import com.example.carsellingshop.Services.OrderService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private final List<Order> orders;

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order o = orders.get(position);

        holder.tvOrderId.setText("Order ID: " + o.getId());
        holder.tvUser.setText("User: " + (o.getUserEmail() != null ? o.getUserEmail() : o.getUserId()));
        holder.tvCar.setText("Car: " + (o.getCarModel() != null ? o.getCarModel() : o.getCarId()));
        holder.tvPrice.setText("Price: $" + o.getPrice());
        holder.tvPhone.setText("Phone: " + (o.getPhone() != null ? o.getPhone() : "N/A"));
        holder.tvAddress.setText("Address: " + (o.getAddress() != null ? o.getAddress() : "N/A"));


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                holder.itemView.getContext(),
                R.array.order_status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerStatus.setAdapter(adapter);

        if (o.getStatus() != null) {
            int pos = adapter.getPosition(o.getStatus());
            if (pos >= 0) holder.spinnerStatus.setSelection(pos);
        }

        holder.spinnerStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            boolean first = true;
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                if (first) { first = false; return; }
                String label = parent.getItemAtPosition(pos).toString();
                String newStatus = OrderService.normalizeStatus(label);
                if (newStatus.equals(o.getStatus())) return;

                FirebaseFirestore.getInstance()
                        .collection("orders")
                        .document(o.getId())
                        .update("status", newStatus)
                        .addOnSuccessListener(a -> o.setStatus(newStatus))
                        .addOnFailureListener(e ->
                                Toast.makeText(holder.itemView.getContext(),
                                        "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });


        holder.btnDeleteOrder.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Delete order?")
                    .setMessage("This will permanently remove the order.")
                    .setPositiveButton("Delete", (d, w) -> {
                        FirebaseFirestore.getInstance()
                                .collection("orders")
                                .document(o.getId())
                                .delete()
                                .addOnSuccessListener(a ->
                                        android.widget.Toast.makeText(
                                                v.getContext(), "Order deleted", android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                )
                                .addOnFailureListener(e ->
                                        android.widget.Toast.makeText(
                                                v.getContext(), "Delete failed: " + e.getMessage(),
                                                android.widget.Toast.LENGTH_LONG
                                        ).show()
                                );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() { return orders.size(); }

    @Override
    public long getItemId(int position) {
        String id = orders.get(position).getId();
        return id != null ? id.hashCode() : super.getItemId(position);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvUser, tvCar, tvPrice, tvPhone, tvAddress;
        Spinner spinnerStatus;
        Button btnDeleteOrder;

        OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvCar = itemView.findViewById(R.id.tvCar);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
            btnDeleteOrder = itemView.findViewById(R.id.btnDeleteOrder);
        }
    }
}