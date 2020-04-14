package com.kevex.ffriend.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kevex.ffriend.Model.Message;
import com.kevex.ffriend.R;
import com.kevex.ffriend.ViewHolder.MessageHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {
    private final String TAG = "Message Adapter";

    private List<Map> messageList;
    private Context context = null;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Map> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_text_view, parent, false);

        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        String messageText = (String) messageList.get(position).get(context.getResources().getString(R.string.dbMessage));
        if(mAuth.getCurrentUser().getUid().equals(messageList.get(position).get(context.getResources().getString(R.string.dbSenderUid)))){
            holder.sendingLayout.setVisibility(View.VISIBLE);
            holder.receivingLayout.setVisibility(View.GONE);
            holder.messageTextViewSending.setText(messageText);
        }else {
            holder.messageTextViewReceiving.setText(messageText);
        }
    }

    @Override
    public int getItemCount() {
        return (messageList.isEmpty())? 0:messageList.size();
    }
}
