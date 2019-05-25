package ist.meic.cmu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dropbox.core.android.Auth;
import com.google.android.material.textfield.TextInputEditText;

import ist.meic.cmu.asyncTasks.Register;
import ist.meic.cmu.utils.LoggerFactory;


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button registerButton = findViewById(R.id.buttonRegister);
        registerButton.setEnabled(false);
        Button linkButton = findViewById(R.id.linkDropboxAccountButton);
        linkButton.setOnClickListener(v -> Auth.startOAuth2Authentication(RegisterActivity.this, getString(R.string.app_key)));

        registerButton.setOnClickListener(view -> {
            LoggerFactory.log(this.getClass().getSimpleName()+": Register Button Clicked");
            TextInputEditText username = findViewById(R.id.registerInputNameText);
            TextInputEditText password = findViewById(R.id.registerInputPasswordText);
            new Register(new Register.Callback() {
                @Override
                public void onUserRegistered() {
                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError() {
                    Toast.makeText(getApplicationContext(), "User already registered or bad fields", Toast.LENGTH_LONG).show();
                }
            }).execute(username.getText().toString(), password.getText().toString(), Auth.getOAuth2Token());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Auth.getOAuth2Token() != null) {
            Button registerButton = findViewById(R.id.buttonRegister);
            registerButton.setEnabled(true);
            ((TextView) findViewById(R.id.link_dropbox_info)).setText(Auth.getOAuth2Token());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
