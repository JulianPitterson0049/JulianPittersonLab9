// Julian Pitterson - N01680049
package julian.pitterson.n01680049.jp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

public class PittersonActivity9 extends AppCompatActivity {

    private DrawerLayout julDrawerLayout;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        long startTime = System.currentTimeMillis();
        splashScreen.setKeepOnScreenCondition(() ->
                System.currentTimeMillis() - startTime < 3000
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // restore saved night mode preference
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isNight = prefs.getBoolean(getString(R.string.pref_mode_key), false);
        AppCompatDelegate.setDefaultNightMode(
                isNight ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

    /*    EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.julDrawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        Toolbar toolbar = findViewById(R.id.julToolbar);
        setSupportActionBar(toolbar);

        julDrawerLayout = findViewById(R.id.julDrawerLayout);
        NavigationView julNavView = findViewById(R.id.julNavView);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.julNavHostFragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                R.id.julFragment, R.id.pitFragment)
                .setOpenableLayout(julDrawerLayout)
                .build();

        if (navController != null) {
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
            NavigationUI.setupWithNavController(julNavView, navController);

            julNavView.setNavigationItemSelectedListener(item -> {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                julDrawerLayout.closeDrawers();
                return handled;
            });
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                R.id.julFragment, R.id.pitFragment)
                .setOpenableLayout(julDrawerLayout)
                .build();
        return (navController != null && NavigationUI.navigateUp(navController, appBarConfig))
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isNight = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;

        MenuItem toggleItem = menu.findItem(R.id.julToggleMode);
        MenuItem searchItem = menu.findItem(R.id.julSearch);

        int textColour = isNight ? Color.WHITE : Color.BLACK;

        if (toggleItem != null) {
            SpannableString s = new SpannableString(toggleItem.getTitle());
            s.setSpan(new ForegroundColorSpan(textColour), 0, s.length(), 0);
            toggleItem.setTitle(s);
        }

        if (searchItem != null) {
            SpannableString s = new SpannableString(searchItem.getTitle());
            s.setSpan(new ForegroundColorSpan(textColour), 0, s.length(), 0);
            searchItem.setTitle(s);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.julToggleMode){
            toggleDarkMode();
            return true;
        } else if (id == R.id.julSearch) {
            showSearchDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleDarkMode(){
        SharedPreferences sharedPreferences = getSharedPreferences( "AppPrefs",MODE_PRIVATE);
        boolean isNight = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sharedPreferences.edit().putBoolean(getString(R.string.pref_mode_key), false).apply();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPreferences.edit().putBoolean(getString(R.string.pref_mode_key), true).apply();
        }

        invalidateOptionsMenu();

    }

    private void showSearchDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search, null);
        EditText searchInput = dialogView.findViewById(R.id.julSearchInput);
        Button searchBtn = dialogView.findViewById(R.id.julSearchBtn);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        searchBtn.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                // close keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);

                // launch Google Search
                Uri uri = Uri.parse("https://www.google.com/search?q=" + Uri.encode(query));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}