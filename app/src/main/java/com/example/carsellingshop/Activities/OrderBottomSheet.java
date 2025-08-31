package com.example.carsellingshop.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.carsellingshop.Model.Order;
import com.example.carsellingshop.R;
import com.example.carsellingshop.Repositories.OrderRepository;
import com.example.carsellingshop.Services.OrderService;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_CAR_ID = "car_id";
    private static final String ARG_CAR_MODEL = "car_model";
    private static final String ARG_CAR_IMAGE = "car_image";
    private static final String ARG_CAR_PRICE = "car_price";

    public interface OnOrderPlacedListener {
        void onOrderPlaced(String carId);
    }

    private OnOrderPlacedListener listener;

    public void setOnOrderPlacedListener(OnOrderPlacedListener l) {
        this.listener = l;
    }

    public static OrderBottomSheet newInstance(String carId, @Nullable String model,
                                               @Nullable String image, double price) {
        OrderBottomSheet sheet = new OrderBottomSheet();
        Bundle b = new Bundle();
        b.putString(ARG_CAR_ID, carId);
        b.putString(ARG_CAR_MODEL, model);
        b.putString(ARG_CAR_IMAGE, image);
        b.putDouble(ARG_CAR_PRICE, price);
        sheet.setArguments(b);
        return sheet;
    }

    private OrderService orderService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderService = new OrderService(new OrderRepository());
        // If you prefer a specific bottom sheet theme and your app theme doesn’t set it:
        // setStyle(STYLE_NORMAL, com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_form_sheet, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            View sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (sheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(sheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        Bundle args = getArguments();
        final String carId = (args != null) ? args.getString(ARG_CAR_ID) : null;
        final String carModel = (args != null) ? args.getString(ARG_CAR_MODEL) : null;
        final String carImage = (args != null) ? args.getString(ARG_CAR_IMAGE) : null;
        final double price = (args != null) ? args.getDouble(ARG_CAR_PRICE, 0d) : 0d;

        TextView tvCarModel = v.findViewById(R.id.tvCarModel);
        TextView tvCarPrice = v.findViewById(R.id.tvCarPrice);
        TextView tvEmail = v.findViewById(R.id.tvEmail);
        EditText etName = v.findViewById(R.id.etName);
        EditText etPhone = v.findViewById(R.id.etPhone);
        EditText etAddress = v.findViewById(R.id.etAddress);
        Button btnSubmit = v.findViewById(R.id.btnSubmit);

        tvCarModel.setText(carModel != null ? carModel : "Car");
        String priceText = price > 0 ? NumberFormat.getCurrencyInstance(Locale.US).format(price) : "";
        tvCarPrice.setText(priceText);

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = (u != null ? u.getUid() : "");
        final String email = (u != null ? u.getEmail() : "");
        tvEmail.setText(email != null ? email : "—");

        btnSubmit.setOnClickListener(view -> {
            if (uid.isEmpty() || carId == null) {
                Toast.makeText(requireContext(), "Auth or car invalid", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prevent double-taps during network call
            btnSubmit.setEnabled(false);

            Order order = new Order();
            order.setUserId(uid);
            order.setUserEmail(email);
            order.setCarId(carId);
            order.setCarModel(carModel);
            order.setCarImageUrl(carImage);
            order.setPrice(price);
            order.setName(name);
            order.setPhone(phone);
            order.setAddress(address);
            // either omit (null) or set explicitly; OrderService.placeOrder will enforce "pending"
            order.setStatus("pending");

            orderService.placeOrder(order)
                    .addOnSuccessListener(orderId -> {
                        Toast.makeText(requireContext(), "Order placed!", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onOrderPlaced(carId);

                        // Open user's mail app with a prefilled draft (free)
                        String subject = "Order received: " + (carModel != null ? carModel : "Your car");
                        String body = "Thanks for your order!\n\nOrder ID: " + orderId +
                                "\nCar: " + (carModel != null ? carModel : "") +
                                "\n\nWe’ll follow up shortly.";
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:" + (email != null ? email : "")));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                        if (emailIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                            startActivity(emailIntent);
                        }

                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        btnSubmit.setEnabled(true);
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}