package com.project.uoa.carpooling.fragments.main;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.uoa.carpooling.R;
import com.project.uoa.carpooling.entities.firebase.SimpleMessageEntity;

/**
 * Class used to implement the messaging feature within the
 * application.
 *
 * Created by Chester Booker and Angel Castro.
 */
public class SimpleMessenger extends DialogFragment {

    /*
     * Class for defining the ViewHolder of the RecyclerView.
     * Specifies the contents of an item in the RecyclerView.
    */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView userTextView;

        public ItemViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            userTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        }
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //UI Components
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;

    // Firebase Instance Variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<SimpleMessageEntity, ItemViewHolder>
            mFirebaseAdapter;

    public SimpleMessenger() {
    }

    public static SimpleMessenger newInstance(String param1, String param2) {
        SimpleMessenger fragment = new SimpleMessenger();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_simple_messenger, container, false);

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) rootView.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Get Reference to Firebase Database
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Set up Adapter for RecyclerView
        mFirebaseAdapter = new FirebaseRecyclerAdapter<SimpleMessageEntity,
                ItemViewHolder>(
                SimpleMessageEntity.class,
                R.layout.item__simple_message,
                ItemViewHolder.class,
                mFirebaseDatabaseReference.child("messages")) {

            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder,
                                              SimpleMessageEntity SimpleMessageEntity, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(SimpleMessageEntity.getText());
                viewHolder.userTextView.setText(SimpleMessageEntity.getName());
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        // Set up RecyclerView with LayoutManager and Adapter
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        // Add some listeners to the edit text field.
        mMessageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //Add on click listener for send button.
        mSendButton = (Button) rootView.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleMessageEntity SimpleMessageEntity = new
                        SimpleMessageEntity(mMessageEditText.getText().toString(),
                        "Anonymous");
                mFirebaseDatabaseReference.child("messages")
                        .push().setValue(SimpleMessageEntity);
                mMessageEditText.setText("");
            }
        });

        return rootView;
    }
}
