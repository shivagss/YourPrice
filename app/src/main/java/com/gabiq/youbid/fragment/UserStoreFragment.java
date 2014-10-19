package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gabiq.youbid.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserStoreFragment extends Fragment {
    private static final String ARG_USER_ID = "userId";
    private static final String ARG_HEADER_VISIBILITY = "headerVisibility";

    private String userId;

    private OnUserStoreFragmentInteractionListener mListener;
    private RelativeLayout rlHeader;
    private int mHeaderVisibility;

    public static UserStoreFragment newInstance(String userId) {
        UserStoreFragment fragment = new UserStoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public UserStoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            mHeaderVisibility = getArguments().getInt(ARG_HEADER_VISIBILITY);
        } else {
            userId = ParseUser.getCurrentUser().getObjectId();
        }

        Fragment userItemsFragment = UserItemsFragment.newInstance(userId);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.flUserItems, userItemsFragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_store, container, false);

        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        rlHeader = (RelativeLayout) view.findViewById(R.id.rlHeader);
        rlHeader.setVisibility(mHeaderVisibility);
        final TextView tvUserStoreName = (TextView) view.findViewById(R.id.tvUserStoreName);
        final ParseImageView pivUserStoreProfile = (ParseImageView) view.findViewById(R.id.pivUserStoreProfile);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    ParseUser user = (ParseUser) objects.get(0);
                    tvUserStoreName.setText(user.getString("name"));
                    ParseFile photoFile = user.getParseFile("photo");
                    if (photoFile != null) {
                        pivUserStoreProfile.setParseFile(photoFile);
                        pivUserStoreProfile.loadInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                // nothing to do
                            }
                        });
                    }
                } else {
                    // Something went wrong.
                }
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.OnUserStoreFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnUserStoreFragmentInteractionListener) activity;
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

    public interface OnUserStoreFragmentInteractionListener {
        public void OnUserStoreFragmentInteraction();
    }

    public void setHeaderVisiblity(int visibility){
        if(rlHeader != null){
            rlHeader.setVisibility(visibility);
        }
    }

}
