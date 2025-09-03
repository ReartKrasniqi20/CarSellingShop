package com.example.carsellingshop.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsellingshop.Model.User;
import com.example.carsellingshop.R;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<User> users;

    public UsersAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User u = users.get(position);
        holder.tvName.setText(u.getUsername());
        holder.tvEmail.setText(u.getEmail());
        holder.tvType.setText("Type: " + u.getUserType());

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                holder.itemView.getContext(),
                R.array.users_role,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerRole.setAdapter(adapter);

        // set initial selection
        if (u.getUserType() != null) {
            int pos = adapter.getPosition(u.getUserType());
            if (pos >= 0) holder.spinnerRole.setSelection(pos);
        }

        holder.spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean first = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (first) { first = false; return; }

                String newRole = parent.getItemAtPosition(pos).toString();
                if (!newRole.equals(u.getUserType())) {
                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(u.getUid())
                            .update("userType", newRole)
                            .addOnSuccessListener(a -> u.setUserType(newRole));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ---- Delete user ----
        holder.btnDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(u.getUid())
                    .delete()
                    .addOnSuccessListener(a -> {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            users.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    });
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvType;
        Spinner spinnerRole;
        Button btnDelete;


        UserViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvType = itemView.findViewById(R.id.tvType);

            spinnerRole = itemView.findViewById(R.id.spinnerRole);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser); 

        }
    }
}