package com.gilkagan.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mDataSnapshots;

    private ChildEventListener mListener= new ChildEventListener(){
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mDataSnapshots.add(dataSnapshot);
            notifyDataSetChanged();
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {
        mActivity = activity;
        mDatabaseReference = ref.child("messages");
        mDatabaseReference.addChildEventListener(mListener);
        mDisplayName = name;
        mDataSnapshots = new ArrayList<>();
    }

    static class ViewHolder{
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mDataSnapshots.size();
    }

    @Override
    public InstantMessage getItem(int i) {
        DataSnapshot dataSnapshot = mDataSnapshots.get(i);
        return dataSnapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.chat_msg_row, viewGroup, false);
            final ViewHolder holder = new ViewHolder();
            holder.authorName = view.findViewById(R.id.author);
            holder.body = view.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            view.setTag(holder);
        }
        final InstantMessage message = getItem(i);
        final ViewHolder holder = (ViewHolder) view.getTag();

        boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRowAppearance(isMe, holder);

        holder.authorName.setText(message.getAuthor());
        holder.body.setText(message.getMessage());

        return view;
    }

    private void setChatRowAppearance(boolean isMe, ViewHolder view) {
        if (isMe) {
            view.params.gravity = Gravity.END;
            view.authorName.setTextColor(Color.BLUE);
            view.body.setBackgroundResource(R.drawable.bubble2);
        }else {
            view.params.gravity = Gravity.START;
            view.authorName.setTextColor(Color.GREEN);
            view.body.setBackgroundResource(R.drawable.bubble1);
        }
        view.authorName.setLayoutParams(view.params);
        view.body.setLayoutParams(view.params);
    }

    public void cleanup(){
        mDatabaseReference.removeEventListener(mListener);
    }
}
