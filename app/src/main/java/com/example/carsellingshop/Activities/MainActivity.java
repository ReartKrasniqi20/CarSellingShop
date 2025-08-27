package com.example.carsellingshop.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.carsellingshop.Services.CarService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView carRecyclerView;
    private CarAdapter carAdapter;
    private final CarService carService = new CarService();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(toolbar);

        // Drawer views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Hamburger opens drawer
        toolbar.setNavigationOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START));

        // Drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                // TODO: show all cars (already default)
            } else if (id == R.id.nav_favorites) {
                Toast.makeText(this, "Favorites (coming soon)", Toast.LENGTH_SHORT).show();
                // TODO: filter adapter to only favorite cars
            } else if (id == R.id.nav_orders) {
                Toast.makeText(this, "Orders (coming soon)", Toast.LENGTH_SHORT).show();
                // TODO: open Orders screen
            } else if (id == R.id.nav_logout) {
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                // TODO: FirebaseAuth.getInstance().signOut();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    finish();

            }
            drawerLayout.closeDrawers();
            return true;
        });

        // RecyclerView
        carRecyclerView = findViewById(R.id.carRecyclerView);
        carRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        carRecyclerView.setHasFixedSize(true);
        carAdapter = new CarAdapter(new ArrayList<>());
        carRecyclerView.setAdapter(carAdapter);

        loadCars();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        // SearchView setup
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) searchItem.getActionView();
        sv.setQueryHint("Search cars...");
        sv.setIconifiedByDefault(true);
        sv.setMaxWidth(Integer.MAX_VALUE);

        // White text/hint inside SearchView
        EditText et = sv.findViewById(androidx.appcompat.R.id.search_src_text);
        if (et != null) {
            et.setTextColor(Color.WHITE);
            et.setHintTextColor(Color.parseColor("#B3FFFFFF"));
        }
        // White icons inside SearchView
        int white = Color.WHITE;
        ImageView mag   = sv.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ImageView close = sv.findViewById(androidx.appcompat.R.id.search_close_btn);
        ImageView go    = sv.findViewById(androidx.appcompat.R.id.search_go_btn);
        ImageView voice = sv.findViewById(androidx.appcompat.R.id.search_voice_btn);
        if (mag   != null) mag.setColorFilter(white);
        if (close != null) close.setColorFilter(white);
        if (go    != null) go.setColorFilter(white);
        if (voice != null) voice.setColorFilter(white);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                carAdapter.getFilter().filter(query, count -> {
                    if (count == 0) Toast.makeText(MainActivity.this, "No match found", Toast.LENGTH_SHORT).show();
                });
                return true;
            }
            @Override public boolean onQueryTextChange(String newText) {
                carAdapter.getFilter().filter(newText, count -> {
                    if (count == 0 && newText.length() > 0)
                        Toast.makeText(MainActivity.this, "No match found", Toast.LENGTH_SHORT).show();
                });
                return true;
            }
        });

        // Reset when collapsed
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override public boolean onMenuItemActionExpand(MenuItem item) { return true; }
            @Override public boolean onMenuItemActionCollapse(MenuItem item) {
                carAdapter.getFilter().filter("");
                return true;
            }
        });

        return true;
    }

    private void loadCars() {
        carService.fetchAllCars(querySnapshot -> {
            List<Car> fresh = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                fresh.add(doc.toObject(Car.class));
            }
            carAdapter.replaceData(fresh);
        }, e -> Toast.makeText(this, "Failed to load cars: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

}
