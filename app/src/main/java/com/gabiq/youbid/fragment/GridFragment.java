package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capricorn.ArcMenu;
import com.etsy.android.grid.StaggeredGridView;
import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.DetailsActivity;
import com.gabiq.youbid.adapter.ItemAdapter;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.utils.EndlessScrollListener;
import com.gabiq.youbid.utils.GridScrollingHelper;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

public class GridFragment extends Fragment {
    public static final String INTENT_EXTRA_ITEM = "item";

    private StaggeredGridView mGvItemGrid;
    private ItemAdapter mItemAdapter;
//    private ImageView mEmptyImage;
//    private TextView mEmptyLabel;
    private ProgressBar progressBar;
    private RelativeLayout emptySection;
    private SwipeRefreshLayout swipeContainer;
    private GridScrollingHelper mGridHelper;

    private OnFragmentInteractionListener mListener;

    public static GridFragment newInstance() {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public GridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    protected ParseQueryAdapter.QueryFactory<Item> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Item>() {
            public ParseQuery<Item> create() {
                ParseQuery query = new ParseQuery("Item");
                query.orderByDescending("createdAt");
                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
                return query;
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);

        setupViews(view);
        setupSwipeContainer(view);
        setupGrid(view);
        return view;
    }

    private void setupViews(View view) {


        mGvItemGrid = (StaggeredGridView) view.findViewById(R.id.gvItemGrid);
        emptySection = (RelativeLayout) view.findViewById(R.id.emptySection);
        mGvItemGrid.setEmptyView(emptySection);
//        mEmptyLabel = (TextView) view.findViewById(R.id.empty_label);
//        mEmptyImage = (ImageView) view.findViewById(R.id.empty_image);


//        mGvItemGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position >= mItemAdapter.getCount()) return;
//                Item item = mItemAdapter.getItem(position);
//
//                Intent intent = new Intent(getActivity(), DetailsActivity.class);
//                intent.putExtra("item_id",item.getObjectId());
//                startActivity(intent);
//            }
//        });


        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

    }

    private void setupSwipeContainer(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mItemAdapter.loadObjects();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorScheme(R.color.refreshColor1,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    private void setupGrid(View view) {
        mGvItemGrid.setAdapter(mItemAdapter);


        mGridHelper = new GridScrollingHelper(getActivity(), null);
        mGridHelper.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mItemAdapter.loadNextPage();
            }
        });

        mGvItemGrid.setOnScrollListener(mGridHelper);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mItemAdapter = new ItemAdapter(activity, getParseQuery());
        mItemAdapter.setAutoload(false);
//        mItemAdapter.setPaginationEnabled(false);
//        mItemAdapter.setObjectsPerPage(10);

        mItemAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Item>() {
            @Override
            public void onLoading() {
//                progressBar.setVisibility(View.VISIBLE);
                emptySection.setVisibility(View.GONE);
            }

            @Override
            public void onLoaded(List<Item> items, Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });

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
        public void onFragmentMessage();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mItemAdapter != null){
            swipeContainer.setRefreshing(true);
            reloadItems();
            swipeContainer.setRefreshing(false);
        }
    }

    public void reloadItems() {
        mItemAdapter.loadObjects();
    }
}
