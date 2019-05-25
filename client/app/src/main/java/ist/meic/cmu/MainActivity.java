package ist.meic.cmu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.File;

import ist.meic.cmu.asyncTasks.GetLogTask;
import ist.meic.cmu.asyncTasks.Logout;
import ist.meic.cmu.mainActivityFragments.AlbumsFragment;
import ist.meic.cmu.mainActivityFragments.InfoFragment;
import ist.meic.cmu.mainActivityFragments.SettingsFragment;
import ist.meic.cmu.mainActivityFragments.UserFragment;
import ist.meic.cmu.mainActivityFragments.WifiAlbumsFragment;
import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.WifiService;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = this.getClass().getSimpleName();


    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.mainUsername);
        navUsername.setText(sharedPreferences.getString("username", null));

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new InfoFragment()).commit();


        /*    WIFI DIRECT */
        if (!sharedPreferences.getBoolean("cloud", false)) {
            WifiService.init(getApplicationContext());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (sharedPreferences.getBoolean("cloud", false)) {
            WifiService.pause();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            new Logout(sharedPreferences).execute();
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
            WifiService.wifiOff();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_albums) {
            if (sharedPreferences.getBoolean("cloud", true)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AlbumsFragment()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new WifiAlbumsFragment()).commit();
            }

        } else if (id == R.id.account_info) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new InfoFragment()).commit();
        } else if (id == R.id.find_user) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new UserFragment()).commit();
        } else if (id == R.id.log_app) {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, "p2pPhotosLog.txt");
            Intent i = new Intent();
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            i.setAction(android.content.Intent.ACTION_VIEW);
            i.setDataAndType(uri, "text/plain");
            startActivity(i);


        } else if (id == R.id.log_server) {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, "p2pPhotosServerLog.txt");
            new GetLogTask(sharedPreferences, new GetLogTask.Callback() {
                @Override
                public void onSuccess(Void log) {
                    Intent i = new Intent();
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                    i.setAction(android.content.Intent.ACTION_VIEW);
                    i.setDataAndType(uri, "text/plain");
                    startActivity(i);
                }

                @Override
                public void onError() {

                }
            }).execute();

        } else if (id == R.id.settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SettingsFragment()).commit();
        } else if (id == R.id.logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            WifiService.wifiOff();
            new Logout(sharedPreferences).execute();
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

