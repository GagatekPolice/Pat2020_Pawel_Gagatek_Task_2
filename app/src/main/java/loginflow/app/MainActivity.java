package loginflow.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import loginflow.app.database.DatabaseHelper;
import loginflow.app.fragment.LoginFragment;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHelper databaseHelper;

    private LoginFragment loginFragment;

    private Button btnLogin;

    private FragmentTransaction fragmentTransaction;

    private static final String LOGIN_FRAGMENT = "login_fragment";

    private final static String TYPED_LOGIN_KEY = "typedLogin";
    private final static String TYPED_EMAIL_KEY = "typedEmail";
    private final static String TYPED_PASSWORD_KEY = "typedPassword";
    private final static String TYPED_VALIDITY_INFO_KEY = "validityInfo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.app_name));

        databaseHelper = new DatabaseHelper(this);
        loginFragment = new LoginFragment();
        btnLogin = findViewById(R.id.main_activity_btn_login);
        btnLogin.setOnClickListener(this);


        if (databaseHelper.isUserIsLoggedIn()) {
            btnLogin.setText(this.getResources().getString(R.string.logout));
        } else {
            btnLogin.setVisibility(View.INVISIBLE);
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.bottomFragment, loginFragment, LOGIN_FRAGMENT);
            fragmentTransaction.commit();
        }

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.main_activity_btn_login) {
            Cursor userData = databaseHelper.getUserData();
            userData.moveToFirst();

            if (databaseHelper.isUserIsLoggedIn()) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.bottomFragment, loginFragment, LOGIN_FRAGMENT);
                fragmentTransaction.commit();
                databaseHelper.changeUserSession(0);
                Toast.makeText(this, getResources().getString(R.string.toast_logout), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT);
        if (fragment != null) {
            btnLogin.setVisibility(View.VISIBLE);
            if (databaseHelper.isUserIsLoggedIn()) {
                btnLogin.setText(this.getResources().getString(R.string.logout));
            } else {
                btnLogin.setText(this.getResources().getString(R.string.login));
            }
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT);
        if (fragment != null) {
            final EditText etTypedLogin = findViewById(R.id.login_fragment_et_login);
            final EditText etTypedEmail = findViewById(R.id.login_fragment_et_email);
            final EditText etTypedPassword = findViewById(R.id.login_fragment_et_password);
            final TextView tvValidityInfo = findViewById(R.id.login_fragment_tv_validity_status);


            outState.putString(TYPED_LOGIN_KEY, String.valueOf(etTypedLogin.getText()));
            outState.putString(TYPED_EMAIL_KEY, String.valueOf(etTypedEmail.getText()));
            outState.putString(TYPED_PASSWORD_KEY, String.valueOf(etTypedPassword.getText()));
            outState.putString(TYPED_VALIDITY_INFO_KEY, String.valueOf(tvValidityInfo.getText()));
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT);
        if (savedInstanceState != null && fragment != null) {
            EditText etTypedLogin = findViewById(R.id.login_fragment_et_login);
            EditText etTypedEmail = findViewById(R.id.login_fragment_et_email);
            EditText etTypedPassword = findViewById(R.id.login_fragment_et_password);
            TextView tvValidityInfo = findViewById(R.id.login_fragment_tv_validity_status);

            etTypedLogin.setText(savedInstanceState.getString(TYPED_LOGIN_KEY));
            etTypedEmail.setText(savedInstanceState.getString(TYPED_EMAIL_KEY));
            etTypedPassword.setText(savedInstanceState.getString(TYPED_PASSWORD_KEY));
            tvValidityInfo.setText(savedInstanceState.getString(TYPED_VALIDITY_INFO_KEY));


        }
    }
}
