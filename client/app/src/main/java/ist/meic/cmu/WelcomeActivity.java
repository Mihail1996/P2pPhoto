package ist.meic.cmu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ist.meic.cmu.asyncTasks.Ping;
import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.PicassoClient;
import ist.meic.cmu.utils.RetrofitFactory;

public class WelcomeActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        requestPermissions();
        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ip", getString(R.string.local_ip));
        editor.apply();
        RetrofitFactory.initServerRetrofit(sharedPref);
        RetrofitFactory.initDbRetrofit();
        Button login = findViewById(R.id.welcome_login_button);
        Button register = findViewById(R.id.welcome_register_button);
        login.setOnClickListener(view -> {
            LoggerFactory.log(this.getClass().getSimpleName() + ": Login Clicked");
            new Ping(new Ping.Callback() {
                @Override
                public void onFinish() {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError() {
                    Toast.makeText(getApplicationContext(), "Cannot connect to the server", Toast.LENGTH_LONG).show();
                }
            }).execute();
        });

        register.setOnClickListener(view -> {
            LoggerFactory.log(this.getClass().getSimpleName() + ": Registration Clicked");
            new Ping(new Ping.Callback() {
                @Override
                public void onFinish() {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError() {
                    Toast.makeText(getApplicationContext(), "Cannot connect to the server", Toast.LENGTH_LONG).show();
                }
            }).execute();
        });
    }


    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        } else {
            LoggerFactory.init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LoggerFactory.init();
            PicassoClient.init(getApplicationContext());
            LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        }

    }


    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


}
