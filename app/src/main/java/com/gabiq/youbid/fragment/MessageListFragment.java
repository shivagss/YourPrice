package com.gabiq.youbid.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.gabiq.youbid.R;
import com.gabiq.youbid.activity.MessageListActivity;
import com.gabiq.youbid.adapter.MessageListAdapter;
import com.gabiq.youbid.model.Bid;
import com.gabiq.youbid.model.Item;
import com.gabiq.youbid.model.Message;
import com.gabiq.youbid.utils.EndlessScrollListener;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MessageListFragment extends Fragment {
    private static final String ARG_ITEM_ID = "itemId";
    private static final String ARG_BID_ID = "bidId";
    private String itemId;
    private String bidId;

    private Button btnBidAccept;
    private Button btnBidReject;
    private EditText etPostMessage;
    private ImageView ivPostMessage;

    private MessageListAdapter messageListAdapter;
    private ListView lvMessageList;
    private SwipeRefreshLayout swipeContainer;

    private OnFragmentInteractionListener mListener;


    public static MessageListFragment newInstance(String bidId, String itemId) {
        MessageListFragment fragment = new MessageListFragment();
        Bundle args = new Bundle();
        if (itemId != null) {
            args.putString(ARG_ITEM_ID, itemId);
        }
        if (bidId != null) {
            args.putString(ARG_BID_ID, bidId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public MessageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemId = getArguments().getString(ARG_ITEM_ID);
            bidId = getArguments().getString(ARG_BID_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        setupViews(view);
        setupSwipeContainer(view);
        setupListView(view);
        return view;
    }

    private void setupViews(View view) {
        lvMessageList = (ListView) view.findViewById(R.id.lvMessageList);

        btnBidAccept = (Button) view.findViewById(R.id.btnBidAccept);
        btnBidReject = (Button) view.findViewById(R.id.btnBidReject);
        etPostMessage = (EditText) view.findViewById(R.id.etPostMessage);
        ivPostMessage = (ImageView) view.findViewById(R.id.ivPostMessage);

        etPostMessage.clearFocus();

        btnBidAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("INFO", "******** Accepted Bid");
                getActivity().finish();
            }
        });

        btnBidReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("INFO", "******** Rejected Bid");
                getActivity().finish();
            }
        });

        ivPostMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etPostMessage.getText().toString();
                if(message !=null  &&  !message.isEmpty()) {
                    postMessage(message);
                    etPostMessage.setText(null);
                }
            }
        });

    }

    private void postMessage(String messageText) {
        Message message = new Message();

        message.setSender(ParseUser.getCurrentUser());
        message.setBody(messageText);
        Bid bid = ParseObject.createWithoutData(Bid.class, bidId);
        message.setBid(bid);
        Item item = ParseObject.createWithoutData(Item.class, itemId);
        message.setItem(item);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                messageListAdapter.loadObjects();
                closeKeyboard();
            }
        });
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etPostMessage.getWindowToken(), 0);
    }

    private void setupSwipeContainer(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.bidListSwipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                messageListAdapter.loadObjects();
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

        messageListAdapter = new MessageListAdapter(getActivity(), getParseQuery());

        lvMessageList.setAdapter(messageListAdapter);
        lvMessageList.setOnScrollListener(new EndlessScrollListener() {
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

    protected ParseQueryAdapter.QueryFactory<Message> getParseQuery() {
        return new ParseQueryAdapter.QueryFactory<Message>() {
            public ParseQuery<Message> create() {
                ParseQuery query = new ParseQuery("Message");
                Bid bid = ParseObject.createWithoutData(Bid.class, bidId);
                query.whereEqualTo("bid", bid);
                query.orderByDescending("createdAt");
                return query;
            }
        };
    }


}
