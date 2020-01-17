package loginflow.app.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import loginflow.app.R;
import loginflow.app.database.DatabaseHelper;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private final static String LOGIN_PATTERN = "[a-z]+";
    private final static String PASSWORD_PATTERN = "(?=.*?\\d)(?=.*?[a-zA-Z])(?=.*?[^\\w]).{6,32}";
    private final static String ELECTRONIC_MAIL_PATTERN = "^^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,15}$";

    private final static Pattern loginPattern = Pattern.compile(LOGIN_PATTERN);
    private final static Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    private final static Pattern electronicMailPattern = Pattern.compile(ELECTRONIC_MAIL_PATTERN);


    private EditText etTypedLogin;
    private EditText etTypedEmail;
    private EditText etTypedPassword;
    private TextView tvValidityInfo;

    private Button btnLoginMainActivity;

    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTypedLogin = view.findViewById(R.id.login_fragment_et_login);
        etTypedEmail = view.findViewById(R.id.login_fragment_et_email);
        etTypedPassword = view.findViewById(R.id.login_fragment_et_password);
        tvValidityInfo = view.findViewById(R.id.login_fragment_tv_validity_status);
        Button btnLogin = view.findViewById(R.id.login_fragment_btn_login);
        btnLoginMainActivity = Objects.requireNonNull(getActivity()).findViewById(R.id.main_activity_btn_login);
        btnLoginMainActivity.setVisibility(View.INVISIBLE);
        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_fragment_btn_login) {
            tvValidityInfo.setText("");
            DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

            final String login = String.valueOf(etTypedLogin.getText());
            final String email = String.valueOf(etTypedEmail.getText());
            final String password = String.valueOf(etTypedPassword.getText());

            /*
             * Email format due to RFC-5322
             */
            final boolean isLoginValid = isDataValid(login, loginPattern, R.string.invalid_login);
            final boolean isEmailValid = isDataValid(email, electronicMailPattern, R.string.invalid_email);
            final boolean isPasswordValid = isDataValid(password, passwordPattern, R.string.invalid_password);

            if (isLoginValid && isEmailValid && isPasswordValid) {

                if (databaseHelper.isEmpty()) {
                    databaseHelper.insertUser(login, email, password, 1);
                } else {
                    databaseHelper.updateUser(login, email, password, 1);
                }
                btnLoginMainActivity.setText(Objects.requireNonNull(getContext()).getString(R.string.logout));
                btnLoginMainActivity.setVisibility(View.VISIBLE);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
        }
    }


    private boolean isDataValid(String data, Pattern pattern, int resource) {
        Matcher matcher = pattern.matcher(data);
        if (matcher.matches()) {
            return true;
        }
        String validityInfo = tvValidityInfo.getText() + Objects.requireNonNull(getContext()).getString(resource);
        tvValidityInfo.setText(validityInfo);

        return false;
    }

}
