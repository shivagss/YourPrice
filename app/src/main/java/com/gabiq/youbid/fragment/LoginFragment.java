package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.utils.ConnectionManager;
import com.gabiq.youbid.utils.Utils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Locale;

public class LoginFragment extends Fragment {
    private Button btnLogin;
    private Button btnSignup;
    private TextView tvForgotPass;
    private EditText etLoginUserName;
    private EditText etLoginPassword;

    private ConnectionManager connectionManger;
    private OnFragmentInteractionListener mListener;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        setupViews(view);

        return view;
    }

    private void setupViews(View view) {
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnSignup = (Button) view.findViewById(R.id.btnSignup);
        tvForgotPass = (TextView) view.findViewById(R.id.tvLoginForgot);
        etLoginUserName = (EditText) view.findViewById(R.id.etLoginUserName);
        etLoginPassword = (EditText) view.findViewById(R.id.etLoginPassword);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionManger.isConnected()) {
                    performLogin();
                } else {
                    Utils.showAlertDialog(getFragmentManager(), getResources().getString(R.string.alert_header_generic),
                            "Please check your internet connection", false);
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onSignupClicked();
                }
            }
        });

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // notify activity
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        connectionManger = new ConnectionManager(activity.getApplicationContext());

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onLoginSuccessful();
        public void onSignupClicked();
    }

    public void performLogin() {

        clearErrors();

        // Store values at the time of the login attempt.
        String username = etLoginUserName.getText().toString();
        String password = etLoginPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            etLoginPassword.setError(getString(R.string.error_field_required));
            focusView = etLoginPassword;
            cancel = true;
        } else if (password.length() < 4) {
            etLoginPassword.setError(getString(R.string.error_invalid_password));
            focusView = etLoginPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            etLoginUserName.setError(getString(R.string.error_field_required));
            focusView = etLoginUserName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // perform the user login attempt.
            login(username.toLowerCase(Locale.getDefault()), password);
        }
    }

    private void login(String lowerCase, String password) {
        ParseUser.logInInBackground(lowerCase, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null)
                    loginSuccessful();
                else
                    loginUnSuccessful();
            }
        });

    }

    protected void loginSuccessful() {
        if (mListener != null) {
            mListener.onLoginSuccessful();
        }
    }

    protected void loginUnSuccessful() {
//        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
        Utils.showAlertDialog(getFragmentManager(),getResources().getString(R.string.alert_header_generic), "Username or Password is invalid.", false);
    }

    private void clearErrors(){
        etLoginUserName.setError(null);
        etLoginPassword.setError(null);
    }



}
