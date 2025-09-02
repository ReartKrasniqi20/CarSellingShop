package com.example.carsellingshop.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsellingshop.Adapters.CarAdapter;
import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.R;
import com.example.carsellingshop.Repositories.OrderRepository;
import com.example.carsellingshop.Services.CarService;
import com.example.carsellingshop.Services.OrderService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private RecyclerView carRecyclerView;
    private CarAdapter carAdapter;
    private final CarService carService = new CarService();

    // carId -> "pending" | "confirmed"
    private final Map<String, String> orderStatusMap = new HashMap<>();
    private final OrderService orderService = new OrderService(new OrderRepository());
    private ListenerRegistration ordersListener; // detach later


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                if (drawerLayout != null) drawerLayout.openDrawer(GravityCompat.START);
            });
        }
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                item.setChecked(true);
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    drawerLayout.closeDrawers();
                    return true;
                } else if (id == R.id.nav_favorites) {
                    Toast.makeText(this, "Favorites (coming soon)", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else if (id == R.id.nav_aboutus) {
                    startActivity(new Intent(this, AboutActivity.class));
                } else if (id == R.id.nav_logout) {

                    // stop real-time listener before signing out
                    if (ordersListener != null) {
                        ordersListener.remove();
                        ordersListener = null;
                    }

                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(this, LogInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                drawerLayout.closeDrawers();
                return true;
            });
        }

        // RecyclerView
        carRecyclerView = findViewById(R.id.carRecyclerView);
        carRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        carRecyclerView.setHasFixedSize(true);

        carAdapter = new CarAdapter(new ArrayList<>(), false);
        carRecyclerView.setAdapter(carAdapter);

        // Order button logic
        carAdapter.setOnOrderClickListener(car -> {
            String carId = car.getId();
            String status = orderStatusMap.get(carId); // null | "pending" | "confirmed"

            if (status == null) {
                // open the order form
                OrderBottomSheet sheet = OrderBottomSheet.newInstance(
                        car.getId(),
                        car.getModel(),
                        car.getImageUrl(),
                        car.getPrice()
                );
                sheet.setOnOrderPlacedListener(orderedCarId -> {
                    // optimistic UI; Firestore listener will confirm
                    orderStatusMap.put(orderedCarId, "pending");
                    carAdapter.setOrderStatusMap(orderStatusMap);
                });
                sheet.show(getSupportFragmentManager(), "orderSheet");
                return;
            }

            if ("pending".equals(status)) {
                // allow cancel while pending
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Cancel order?")
                        .setMessage("You can cancel while the order is still pending.")
                        .setPositiveButton("Yes", (d, w) -> {
                            FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
                            if (u == null) return;
                            new OrderService(new OrderRepository())
                                    .cancelPendingOrder(u.getUid(), carId)
                                    .addOnSuccessListener(v -> {
                                        orderStatusMap.remove(carId);
                                        carAdapter.setOrderStatusMap(orderStatusMap);
                                        Toast.makeText(this, "Order cancelled", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        })
                        .setNegativeButton("No", null)
                        .show();
                return;
            }

            if ("confirmed".equals(status)) {
                Toast.makeText(this, "Order approved. Thank you!", Toast.LENGTH_SHORT).show();
            }
        });

        // Details
        carAdapter.setOnDetailsClickListener(car -> {
            String specs = buildSpecs(car);
            CarDetailsBottomSheet sheet = CarDetailsBottomSheet.newInstance(
                    car.getModel(),
                    car.getPrice(),
                    car.getImageUrl(),
                    car.getDescription(),
                    car.getDiscount(),
                    specs
            );
            sheet.show(getSupportFragmentManager(), "carDetails");
        });

        loadCars();
        listenToUserOrders(); // realtime
    }

    // Real-time listen to ACTIVE orders and map to carId -> status
    private void listenToUserOrders() {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u == null) return;

        ordersListener = orderService.listenToActiveOrdersByUser(u.getUid(), (snap, e) -> {
            if (e != null) {
                Toast.makeText(this, "Failed to listen orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            orderStatusMap.clear();
            if (snap != null) {
                for (DocumentSnapshot d : snap.getDocuments()) {
                    String carId = d.getString("carId");
                    String st = d.getString("status");
                    if (carId != null && st != null) orderStatusMap.put(carId, st);
                }
            }
            carAdapter.setOrderStatusMap(orderStatusMap);
        });

    }

    // Toolbar menu (Search + Profile avatar actionView)
    @Override
    protected void onStop() {
        super.onStop();
        if (ordersListener != null) {
            ordersListener.remove();
            ordersListener = null;
        }
    }

    // 🔍 Toolbar menu (Search + Profile avatar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        // Search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) searchItem.getActionView();
        if (sv != null) {
            sv.setQueryHint("Search cars…");
            sv.setIconifiedByDefault(true);
            sv.setMaxWidth(Integer.MAX_VALUE);

            // Make search text/icons white
            EditText et = sv.findViewById(androidx.appcompat.R.id.search_src_text);
            if (et != null) {
                et.setTextColor(Color.WHITE);

                et.setHintTextColor(0xB3FFFFFF);

            }
            ImageView mag   = sv.findViewById(androidx.appcompat.R.id.search_mag_icon);
            ImageView close = sv.findViewById(androidx.appcompat.R.id.search_close_btn);
            ImageView go    = sv.findViewById(androidx.appcompat.R.id.search_go_btn);
            ImageView voice = sv.findViewById(androidx.appcompat.R.id.search_voice_btn);
            if (mag   != null) mag.setColorFilter(Color.WHITE);
            if (close != null) close.setColorFilter(Color.WHITE);
            if (go    != null) go.setColorFilter(Color.WHITE);
            if (voice != null) voice.setColorFilter(Color.WHITE);

            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override public boolean onQueryTextSubmit(String query) {
                    carAdapter.getFilter().filter(query, count -> {
                        if (count == 0) {
                            Toast.makeText(MainActivity.this, "No match found", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }
                @Override public boolean onQueryTextChange(String newText) {
                    carAdapter.getFilter().filter(newText, count -> {
                        if (count == 0 && newText.length() > 0) {
                            Toast.makeText(MainActivity.this, "No match found", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }
            });

            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override public boolean onMenuItemActionExpand(MenuItem item) { return true; }
                @Override public boolean onMenuItemActionCollapse(MenuItem item) {
                    carAdapter.getFilter().filter(""); // reset list
                    return true;
                }
            });
        }

        // Profile avatar
        MenuItem profileItem = menu.findItem(R.id.action_profile);
        if (profileItem != null) {
            View avatarView = profileItem.getActionView();

            if (avatarView != null) {
                TextView tv = avatarView.findViewById(R.id.tvAvatarInitialSmall);
                View container = avatarView.findViewById(R.id.avatarContainerSmall);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String display = (user != null && user.getEmail() != null && !user.getEmail().isEmpty())
                        ? user.getEmail()
                        : "User";
                char initial = Character.toUpperCase(display.trim().isEmpty() ? 'U' : display.trim().charAt(0));
                if (tv != null) tv.setText(String.valueOf(initial));

                if (container != null && container.getBackground() instanceof GradientDrawable) {
                    GradientDrawable bg = (GradientDrawable) container.getBackground().mutate();
                    String key = (user != null && user.getUid() != null) ? user.getUid() : display;
                    bg.setColor(pickStableColor(key));
                }

                avatarView.setOnClickListener(v ->
                        startActivity(new Intent(this, ProfileActivity.class)));
            }
        }
        return true;
    }


    // Fallback if no custom actionView

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int pickStableColor(String key) {
        int hash = (key == null ? 0 : key.hashCode());
        float hue = (hash & 0xFFFFFF) % 360;
        return Color.HSVToColor(new float[]{hue, 0.55f, 0.85f});
    }

    private void loadCars() {
        carService.fetchAllCars(querySnapshot -> {
            List<Car> fresh = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Car c = doc.toObject(Car.class);
                try { c.setId(doc.getId()); } catch (Exception ignored) {}
                fresh.add(c);
            }
            carAdapter.replaceData(fresh);
            carAdapter.setOrderStatusMap(orderStatusMap);
        }, e -> Toast.makeText(this, "Failed to load cars: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private String buildSpecs(Car c) {
        List<String> lines = new ArrayList<>();
        if (c.getYear() > 0) lines.add("Year: " + c.getYear());
        if (c.getMileageKm() != null) lines.add("Kilometers: " + c.getMileageKm());
        if (c.getFuelType() != null) lines.add("Fuel: " + c.getFuelType());
        if (c.getTransmission() != null) lines.add("Transmission: " + c.getTransmission());
        return android.text.TextUtils.join("\n", lines);

    }

    private String buildSpecs(Car c) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        if (c.getYear() != null)        lines.add("Year: " + c.getYear());
        if (c.getMileageKm() != null)   lines.add("Kilometers: " + c.getMileageKm());
        if (c.getFuelType() != null)    lines.add("Fuel: " + c.getFuelType());
        if (c.getTransmission() != null)lines.add("Transmission: " + c.getTransmission());
        return android.text.TextUtils.join("\n", lines);
    }

}