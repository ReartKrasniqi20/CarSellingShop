package com.example.carsellingshop.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.carsellingshop.Fragments.UsersFragment;
import com.example.carsellingshop.Fragments.CarsFragment;
import com.example.carsellingshop.Fragments.OrdersFragment;

public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new UsersFragment();
            case 1: return new CarsFragment();
            case 2: return new OrdersFragment();
            default: return new UsersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}