package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.MenuItem;

import com.gabiq.youbid.adapter.UsersListAdapter;
import com.gabiq.youbid.model.Followers;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ParseUsersListFragment extends ListFragment {

    private OnSelectUserListener mListener;
    protected ArrayList<ParseUser> mUsers = new ArrayList<ParseUser>();

    public static ParseUsersListFragment newInstance() {
        ParseUsersListFragment fragment = new ParseUsersListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ParseUsersListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        getActivity().getActionBar().setTitle(getTitle());
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void updateUI(){
        ParseQuery<Followers> query = getFollowUsersParseQuery(mListener.getUser());
        query.include("follower");
        query.include("following");
        if(query != null){
            query.findInBackground(new FindCallback<Followers>() {
                @Override
                public void done(List<Followers> followersList, ParseException e) {
                    mUsers = loadData(followersList);
                    setListAdapter(new UsersListAdapter(getActivity(), mUsers));
                }
            });
        }
    }

    protected String getTitle(){
        return "Users";
    }

    protected ArrayList<ParseUser> loadData(List<Followers> followersList) {
        return mUsers;
    }

    protected ParseQuery<Followers> getFollowUsersParseQuery(ParseUser user){
        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectUserListener) activity;
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


//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//        if (null != mListener) {
//        }
//    }

    public interface OnSelectUserListener {
        public ParseUser getUser();
    }

}
