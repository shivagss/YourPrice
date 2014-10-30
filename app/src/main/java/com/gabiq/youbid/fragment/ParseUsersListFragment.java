package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gabiq.youbid.R;
import com.gabiq.youbid.adapter.UsersListAdapter;
import com.gabiq.youbid.model.Followers;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ParseUsersListFragment extends Fragment {

    private OnSelectUserListener mListener;
    protected ArrayList<ParseUser> mUsers = new ArrayList<ParseUser>();
    private ListView lvUsers;
    private UsersListAdapter mUsersAdapter;
    private ProgressBar progressBar;
    private RelativeLayout emptySection;
    private SwipeRefreshLayout swipeContainer;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        lvUsers = (ListView) view.findViewById(R.id.lvUsers);
        emptySection = (RelativeLayout) view.findViewById(R.id.emptySection);
        lvUsers.setEmptyView(emptySection);
        lvUsers.setDivider(getActivity().getResources().getDrawable(android.R.color.transparent));

        mUsersAdapter = new UsersListAdapter(getActivity(), mUsers);
        lvUsers.setAdapter(mUsersAdapter);

        emptySection.setVisibility(View.GONE);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        getActivity().setTitle(getTitle());

        setupSwipeContainer(view);
        return view;
    }

    public void updateUI(){
        ParseQuery<Followers> query = getFollowUsersParseQuery(mListener.getUser());
        query.include("follower");
        query.include("following");
        if(query != null){
            query.findInBackground(new FindCallback<Followers>() {
                @Override
                public void done(List<Followers> followersList, ParseException e) {
                    mUsers.clear();
                    mUsers.addAll(loadData(followersList));
                    mUsersAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
//                    emptySection.setVisibility(View.VISIBLE);
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

    private void setupSwipeContainer(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.usersListSwipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUI();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorScheme(R.color.refreshColor1,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

}
