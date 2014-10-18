package com.gabiq.youbid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.gabiq.youbid.R;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProfileFragment extends Fragment {
    private static final String ARG_USER_ID = "userId";

    private String userId;
    private UserStoreFragment storeFragment;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId user id.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }else{
            userId = ParseUser.getCurrentUser().getObjectId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        storeFragment = UserStoreFragment.newInstance(userId);
        Bundle args = storeFragment.getArguments();
        args.putInt("headerVisibility", View.GONE);
        storeFragment.setArguments(args);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.flStoreContainer, storeFragment).commit();
        return v;

    }
}
