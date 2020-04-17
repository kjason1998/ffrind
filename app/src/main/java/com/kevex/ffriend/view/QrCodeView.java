package com.kevex.ffriend.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.kevex.ffriend.Activities.ChatActivity;
import com.kevex.ffriend.R;

public class QrCodeView extends Dialog implements View.OnClickListener {

    private View customView;
    private Bitmap bitmap;

    public QrCodeView(ChatActivity context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
        setContentView(R.layout.view_dialog_qr);
        //Dialog disappears after setting click layout
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.view_dialog_qr, null);
        setContentView(view);

        ImageView qrCode = (ImageView) view.findViewById(R.id.iv_qr);
        qrCode.setImageBitmap(bitmap);
//        qrCode = qrCodeImage;
        Window window = getWindow();
        //Set up pop-up animation
        window.setWindowAnimations(R.style.style_dialog);
        WindowManager.LayoutParams wl = window.getAttributes();
        //Set popup position
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);

    }


    public void onClick(View v) {
        dismiss();
    }
}
