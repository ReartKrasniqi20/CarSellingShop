package com.example.carsellingshop.Activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsellingshop.Adapters.CarAdapter;
import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.R;
import com.example.carsellingshop.Services.CarService;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView carRecyclerView;
    private CarAdapter carAdapter;
    private final CarService carService = new CarService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(toolbar);

        // Tint ONLY the navigation icon here (it exists immediately)
        Drawable nav = toolbar.getNavigationIcon();
        if (nav != null) nav.setTint(Color.WHITE);

        // Left "menu" icon -> show popup menu
        toolbar.setNavigationOnClickListener(this::showLeftPopupMenu);

        // Right actions
        toolbar.setOnMenuItemClickListener(this::onToolbarMenuClick);

        // RecyclerView (single column list)
        carRecyclerView = findViewById(R.id.carRecyclerView);
        carRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        carRecyclerView.setHasFixedSize(true);
        carAdapter = new CarAdapter(new ArrayList<>()); // adapter keeps its own full/visible lists
        carRecyclerView.setAdapter(carAdapter);

        loadCars();
    }

    // Inflate toolbar menu and wire SearchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        // SearchView setup (collapsing action view)
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) searchItem.getActionView();
        sv.setQueryHint("Search cars...");
        sv.setIconifiedByDefault(true);
        sv.setMaxWidth(Integer.MAX_VALUE);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                carAdapter.getFilter().filter(query, count ->
                { if (count == 0) Toast.makeText(MainActivity.this, "No match found", Toast.LENGTH_SHORT).show(); });
                return true;
            }
            @Override public boolean onQueryTextChange(String newText) {
                carAdapter.getFilter().filter(newText, count ->
                { if (count == 0 && newText.length() > 0) Toast.makeText(MainActivity.this, "No match found", Toast.LENGTH_SHORT).show(); });
                return true;
            }
        });

        // When the search view collapses, reset list
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override public boolean onMenuItemActionExpand(MenuItem item) { return true; }
            @Override public boolean onMenuItemActionCollapse(MenuItem item) {
                carAdapter.getFilter().filter(""); // show all again
                return true;
            }
        });

        return true;
    }

    private void showLeftPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.popup_home)         { Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show(); return true; }
            else if (id == R.id.popup_categories){ Toast.makeText(this, "Categories", Toast.LENGTH_SHORT).show(); return true; }
            else if (id == R.id.popup_settings){ Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show(); return true; }
            else if (id == R.id.popup_logout)  { Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show(); return true; }
            return false;
        });
        popup.show();
    }

    private boolean onToolbarMenuClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search)  { return true; }
        else if (id == R.id.action_profile) { Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show(); return true; }
        return false;
    }

    private void loadCars() {
        carService.fetchAllCars(querySnapshot -> {
            List<Car> fresh = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                fresh.add(doc.toObject(Car.class));
            }
            // IMPORTANT: feed the adapter so it can filter client-side
            carAdapter.replaceData(fresh);
        }, e -> Toast.makeText(this, "Failed to load cars: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
