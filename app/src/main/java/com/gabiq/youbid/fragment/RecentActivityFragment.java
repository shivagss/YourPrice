package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.adapter.NotificationAdapter;
import com.gabiq.youbid.model.Notification;

import java.util.ArrayList;

public class RecentActivityFragment extends Fragment {
    ArrayList<Notification> notificationList;
    NotificationAdapter notificationAdapter;
    ListView lvNotifications;


    public static RecentActivityFragment newInstance() {
        RecentActivityFragment fragment = new RecentActivityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public RecentActivityFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_recent_activity, container, false);

        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        lvNotifications = (ListView) view.findViewById(R.id.lvNotifications);
        lvNotifications.setAdapter(notificationAdapter);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        notificationList = new ArrayList<Notification>();
        notificationAdapter = new NotificationAdapter(getActivity(), notificationList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotifications();
    }

    void loadNotifications() {
        notificationAdapter.clear();
        notificationAdapter.addAll(Notification.getAll());

        if (notificationAdapter.getCount() == 0) {
            // display empty message

        }
    }
}
