package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.DetailsActivity;
import com.gabiq.youbid.adapter.ItemAdapter;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.utils.EndlessScrollListener;

import java.util.ArrayList;

public class GridFragment extends Fragment {
    public static final String INTENT_EXTRA_ITEM = "item";

    private StaggeredGridView mGvItemGrid;
    private ItemAdapter mItemAdapter;
    private ImageView mEmptyImage;
    private TextView mEmptyLabel;
    private SwipeRefreshLayout swipeContainer;


    private OnFragmentInteractionListener mListener;

    public static GridFragment newInstance(String param1, String param2) {
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
        mGvItemGrid.setEmptyView(view.findViewById(android.R.id.empty));
        mEmptyLabel = (TextView) view.findViewById(R.id.empty_label);
        mEmptyImage = (ImageView) view.findViewById(R.id.empty_image);

        mGvItemGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= mItemAdapter.getCount()) return;
                Item item = mItemAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(INTENT_EXTRA_ITEM, item);

                startActivity(intent);
            }
        });

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
        swipeContainer.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    private void setupGrid(View view) {
        mGvItemGrid.setAdapter(mItemAdapter);
        mGvItemGrid.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // this may not be needed
            }
        });

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mItemAdapter = new ItemAdapter(activity);

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

}
