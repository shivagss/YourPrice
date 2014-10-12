package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gabiq.youbid.R;
import com.gabiq.youbid.model.User;
import com.gabiq.youbid.utils.ConnectionManager;
import com.gabiq.youbid.utils.Utils;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import java.util.Locale;

public class SignupFragment extends Fragment {
    private Button btnSignup;
    private EditText etSignupEmail;
    private EditText etSignupUsername;
    private EditText etSignupPassword;
    private EditText etSignupRetypePassword;

    private ConnectionManager connectionManger;
    private OnFragmentInteractionListener mListener;

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public SignupFragment() {
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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        btnSignup = (Button) view.findViewById(R.id.btnSignup);
        etSignupEmail = (EditText) view.findViewById(R.id.etSignupEmail);
        etSignupUsername = (EditText) view.findViewById(R.id.etSignupUsername);
        etSignupPassword = (EditText) view.findViewById(R.id.etSignupPassword);
        etSignupRetypePassword = (EditText) view.findViewById(R.id.etSignupRetypePassword);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionManger.isConnected()) {
                    validateAndSignup();
                } else {
                    Utils.showAlertDialog(getActivity(), "No Internet Connection",
                            "You don't have internet connection.", false);
                }
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
        public void onSignupSuccessful();
    }

    private void validateAndSignup(){
        clearErrors();

        boolean cancel = false;
        View focusView = null;

        // Store values at the time of the login attempt.
        String username = etSignupUsername.getText().toString();
        String email = etSignupEmail.getText().toString();
        String password = etSignupPassword.getText().toString();
        String retpePassword = etSignupRetypePassword.getText().toString();

        // Check for a valid confirm password.
        if (TextUtils.isEmpty(retpePassword)) {
            etSignupRetypePassword.setError(getString(R.string.error_field_required));
            focusView = etSignupRetypePassword;
            cancel = true;
        } else if (password != null && !retpePassword.equals(password)) {
            etSignupPassword.setError(getString(R.string.error_invalid_confirm_password));
            focusView = etSignupPassword;
            cancel = true;
        }
        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            etSignupPassword.setError(getString(R.string.error_field_required));
            focusView = etSignupPassword;
            cancel = true;
        } else if (password.length() < 6) {
            etSignupPassword.setError(getString(R.string.error_invalid_password));
            focusView = etSignupPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            etSignupEmail.setError(getString(R.string.error_field_required));
            focusView = etSignupEmail;
            cancel = true;
        } else if (!email.contains("@")) {
            etSignupEmail.setError(getString(R.string.error_invalid_email));
            focusView = etSignupEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Toast.makeText(getActivity().getApplicationContext(), "signUp", Toast.LENGTH_SHORT).show();
            signUp(username.toLowerCase(Locale.getDefault()),  email, password);

        }

    }

    private void signUp(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    signUpMsg("Account Created Successfully");
                    if (mListener != null) {
                        mListener.onSignupSuccessful();
                    }
                } else {
                    signUpMsg("Account already exists.");
                }
            }
        });
    }

    protected void signUpMsg(String msg) {
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void clearErrors(){
        etSignupEmail.setError(null);
        etSignupPassword.setError(null);
        etSignupRetypePassword.setError(null);
    }


}
