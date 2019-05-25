package ist.meic.cmu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import ist.meic.cmu.asyncTasks.CreateAlbumTask;
import ist.meic.cmu.asyncTasks.Login;
import ist.meic.cmu.utils.LoggerFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = CreateAlbumTask.class.getName();
    private TextInputEditText username;
    private TextInputEditText password;
    private RadioButton cloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        Button loginButton = findViewById(R.id.loginPageButton);

        loginButton.setOnClickListener(view -> {
            LoggerFactory.log(this.getClass().getSimpleName() + ": Login Button Clicked");
            username = findViewById(R.id.loginInputNameText);
            password = findViewById(R.id.loginInputPasswordText);
            cloud = findViewById(R.id.radioButtonCloud);
            new Login(sharedPref, new Login.Callback() {
                @Override
                public void onFinish() {
                    editor.putString("username", username.getText().toString());
                    editor.putBoolean("cloud", cloud.isChecked());
                    editor.apply();
                    Log.d(TAG, "onFinish: " + sharedPref);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                }

                @Override
                public void onError() {
                    Toast.makeText(getApplicationContext(), "Incorrect Username or password", Toast.LENGTH_LONG).show();
                }
            }).execute(username.getText().toString(), password.getText().toString());

        });
    }


}
