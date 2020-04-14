package com.kevex.ffriend.ViewHolder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kevex.ffriend.R;

public class MessageHolder extends RecyclerView.ViewHolder {

    // each data item is just a string in this case
    public RelativeLayout background;
    public LinearLayout receivingLayout;
    public LinearLayout sendingLayout;
    public TextView messageTextViewReceiving;
    public TextView messageTextViewSending;

    public MessageHolder(View v) {
        super(v);
        background = v.findViewById(R.id.message_background);
        receivingLayout = v.findViewById(R.id.receiverLinearLayout);
        sendingLayout = v.findViewById(R.id.senderLinearLayout);
        messageTextViewReceiving = v.findViewById(R.id.recyclerViewChatMessageTextReceive);
        messageTextViewSending = v.findViewById(R.id.recyclerViewChatMessageTextSending);
    }
}