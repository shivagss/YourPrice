package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.BidListActivity;
import com.gabiq.youbid.activity.MessageListActivity;
import com.gabiq.youbid.adapter.BidListAdapter;
import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.Comment;
import com.gabiq.youbid.utils.EndlessScrollListener;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class BidListFragment extends Fragment {
    private static final String ARG_ISSELLER_ID = "isSeller";
    private static final String ARG_ITEM_ID = "itemId";
    private String itemId;
    private boolean isSeller;
    private BidListAdapter bidListAdapter;
    private ListView lvBidList;
    private SwipeRefreshLayout swipeContainer;

    private OnFragmentInteractionListener mListener;


    public static BidListFragment newInstance(String itemId, boolean isSeller) {
        BidListFragment fragment = new BidListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, itemId);
        args.putBoolean(ARG_ISSELLER_ID, isSeller);
        fragment.setArguments(args);
        return fragment;
    }

    public BidListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemId = getArguments().getString(ARG_ITEM_ID);
            isSeller = getArguments().getBoolean(ARG_ISSELLER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bid_list, container, false);

        setupViews(view);
        setupSwipeContainer(view);
        setupListView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bidListAdapter != null) {
            bidListAdapter.loadObjects();
        }
    }

    private void setupViews(View view) {
        lvBidList = (ListView) view.findViewById(R.id.lvBidList);

        lvBidList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= bidListAdapter.getCount()) return;
                final Bid bid = bidListAdapter.getItem(position);

                Intent i = new Intent(getActivity(), MessageListActivity.class);
                i.putExtra("bidId", bid.getObjectId());
                i.putExtra("itemId", bid.getItemId());
                i.putExtra("isSeller", isSeller);
                startActivity(i);

//                if (bid.getState().equals("pending")) {
//
//                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//                    alertDialogBuilder.setTitle("BID ACTION");
//                    alertDialogBuilder.setMessage("Accept bid for " + String.valueOf(bid.getPrice()) + "?");
//                    alertDialogBuilder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    bid.setState("accepted");
//                                    bid.saveInBackground(new SaveCallback() {
//                                        @Override
//                                        public void done(ParseException e) {
//                                            bidListAdapter.loadObjects();
//                                        }
//                                    });
//                                }
//                            }
//                    );
//                    alertDialogBuilder.setNegativeButton("REJECT", new DialogInterface.OnClickListener()
//
//                            {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    bid.setState("rejected");
//                                    bid.saveInBackground(new SaveCallback() {
//                                        @Override
//                                        public void done(ParseException e) {
//                                            bidListAdapter.loadObjects();
//                                        }
//                                    });
//                                }
//                            }
//
//                    );
//
//                    AlertDialog alertDialog = alertDialogBuilder.create();
//                    alertDialog.show();
//                }
            }
        });

    }


    private void setupSwipeContainer(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.bidListSwipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bidListAdapter.loadObjects();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorScheme(R.color.refreshColor1,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    private void setupListView(View view) {

        bidListAdapter = new BidListAdapter(getActivity(), getParseQuery());
        bidListAdapter.setAutoload(false);

        lvBidList.setAdapter(bidListAdapter);
        lvBidList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
//                bidListAdapter.loadNextPage();
            }
        });

        final RelativeLayout emptySection = (RelativeLayout) view.findViewById(R.id.emptySection);
        lvBidList.setEmptyView(emptySection);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        bidListAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Bid>() {
            @Override
            public void onLoading() {
//                progressBar.setVisibility(View.VISIBLE);
                emptySection.setVisibility(View.GONE);
            }

            @Override
            public void onLoaded(List<Bid> bid, Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction();
    }

    protected ParseQueryAdapter.QueryFactory<Bid> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Bid>() {
            public ParseQuery<Bid> create() {
                if (isSeller) {
                    ParseQuery query = new ParseQuery("Bid");
                    query.whereEqualTo("itemId", itemId);
                    query.orderByDescending("createdAt");
                    query.include("createdBy");
                    return query;
                } else {
                    ParseQuery query = new ParseQuery("Bid");
                    query.whereEqualTo("itemId", itemId);
                    query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                    query.orderByDescending("createdAt");
                    query.include("createdBy");
                    return query;
                }

            }
        };
    }


}
