package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.adapter.BidListAdapter;
import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.utils.EndlessScrollListener;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class BidListFragment extends Fragment {
    private static final String ARG_ITEM_ID = "itemId";
    private String itemId;
    private BidListAdapter bidListAdapter;
    private ListView lvBidList;
    private SwipeRefreshLayout swipeContainer;

    private OnFragmentInteractionListener mListener;


    public static BidListFragment newInstance(String itemId) {
        BidListFragment fragment = new BidListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, itemId);
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

    private void setupViews(View view) {
        lvBidList = (ListView) view.findViewById(R.id.lvBidList);

        lvBidList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= bidListAdapter.getCount()) return;
                Bid bid = bidListAdapter.getItem(position);

                if (bid.getState().equals("pending")) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    BidActionDialogFragment bidActionDialogFragment = BidActionDialogFragment.newInstance(bid.getObjectId(), "Accept bid for "+ String.valueOf(bid.getPrice()) + "?");
                    bidActionDialogFragment.show(fm, "fragment_alert");
                }
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
        swipeContainer.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    private void setupListView(View view) {

        bidListAdapter = new BidListAdapter(getActivity(), getParseQuery());

        lvBidList.setAdapter(bidListAdapter);
        lvBidList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
//                bidListAdapter.loadNextPage();
            }
        });

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        public void onFragmentInteraction();
    }

    protected ParseQueryAdapter.QueryFactory<Bid> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Bid>() {
            public ParseQuery<Bid> create() {
                ParseQuery query = new ParseQuery("Bid");
                query.whereEqualTo("itemId", itemId);
                query.orderByDescending("createdAt");
                return query;
            }
        };
    }


}
