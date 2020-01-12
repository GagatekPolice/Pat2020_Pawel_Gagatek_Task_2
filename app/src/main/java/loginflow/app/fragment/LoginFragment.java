package loginflow.app.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import loginflow.app.R;
import loginflow.app.database.DatabaseHelper;


public class LoginFragment extends Fragment implements View.OnClickListener {

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
            DatabaseHelper databaseHelper=new DatabaseHelper(getContext());

            String login = String.valueOf(etTypedLogin.getText());
            String email = String.valueOf(etTypedEmail.getText());
            String password = String.valueOf(etTypedPassword.getText());

            /*
            * Email format due to RFC-5322
            */
            boolean isLoginValid =isDataValid(login,"[a-z]{5,}",R.string.invalid_login);
            boolean isEmailValid =isDataValid(email,"^^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,12}$",R.string.invalid_email);
            boolean isPasswordValid  =isDataValid(password,"(?=.*?\\d)(?=.*?[a-zA-Z])(?=.*?[^\\w]).{6,32}",R.string.invalid_password);

            if(isLoginValid && isEmailValid && isPasswordValid){
     
            if(databaseHelper.isEmpty()){
                databaseHelper.insertUser(login,email,password,1);
            }
            else{
                databaseHelper.updateUser(login,email,password,1);
            }
                btnLoginMainActivity.setText(Objects.requireNonNull(getContext()).getString(R.string.logout));
                btnLoginMainActivity.setVisibility(View.VISIBLE);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
        }
    }



    private boolean isDataValid(String data, String pattern, int resource){
        if(data.matches(String.valueOf(pattern))){
            return true;
        }
        String validityInfo=tvValidityInfo.getText()+ Objects.requireNonNull(getContext()).getString(resource);
        tvValidityInfo.setText(validityInfo);

    return false;
    }

}
