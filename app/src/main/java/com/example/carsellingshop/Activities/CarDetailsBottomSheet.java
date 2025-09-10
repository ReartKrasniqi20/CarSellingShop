package com.example.carsellingshop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.carsellingshop.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.NumberFormat;
import java.util.Locale;

public class CarDetailsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_MODEL     = "model";
    private static final String ARG_PRICE     = "price";
    private static final String ARG_IMAGE_URL = "imageUrl";
    private static final String ARG_DESC      = "description";
    private static final String ARG_DISCOUNT  = "discount";
    private static final String ARG_SPECS     = "specs";



    public static CarDetailsBottomSheet newInstance(
            String model, double price, String imageUrl,
            String description, double discount, @Nullable String specsText) {

        CarDetailsBottomSheet s = new CarDetailsBottomSheet();
        Bundle b = new Bundle();
        b.putString(ARG_MODEL, model);
        b.putDouble(ARG_PRICE, price);
        b.putString(ARG_IMAGE_URL, imageUrl);
        b.putString(ARG_DESC, description);
        b.putDouble(ARG_DISCOUNT, discount);
        b.putString(ARG_SPECS, specsText);
        s.setArguments(b);
        return s;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_bottom_sheet, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        BottomSheetDialog d = (BottomSheetDialog) getDialog();
        if (d != null) {
            View sheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (sheet != null) {
                BottomSheetBehavior.from(sheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        ImageView iv = v.findViewById(R.id.ivCar);
        TextView tvModel = v.findViewById(R.id.tvModel);
        TextView tvPrice = v.findViewById(R.id.tvPrice);
        TextView tvDiscount = v.findViewById(R.id.tvDiscount);
        TextView tvDesc = v.findViewById(R.id.tvDescription);
        TextView tvSpecs = v.findViewById(R.id.tvSpecs);
        Button btnClose = v.findViewById(R.id.btnClose);

        Bundle b = getArguments();
        String model = b != null ? b.getString(ARG_MODEL) : "";
        double price = b != null ? b.getDouble(ARG_PRICE, 0d) : 0d;
        String image = b != null ? b.getString(ARG_IMAGE_URL) : "";
        String desc  = b != null ? b.getString(ARG_DESC) : "";
        double disc  = b != null ? b.getDouble(ARG_DISCOUNT, 0d) : 0d;
        String specs = b != null ? b.getString(ARG_SPECS) : null;

        tvModel.setText(model == null ? "" : model);
        tvPrice.setText(NumberFormat.getCurrencyInstance(Locale.US).format(price));
        if (disc > 0) {
            tvDiscount.setText("Discount: " + (int)disc + "%");
            tvDiscount.setVisibility(View.VISIBLE);
        } else {
            tvDiscount.setVisibility(View.GONE);
        }
        tvDesc.setText(desc == null ? "" : desc);
        tvSpecs.setText(specs == null || specs.trim().isEmpty() ? "" : specs);

        Glide.with(v.getContext())
                .load(image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(iv);

        btnClose.setOnClickListener(view -> dismiss());
    }
}
