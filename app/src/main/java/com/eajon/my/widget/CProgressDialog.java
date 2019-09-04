package com.eajon.my.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.eajon.my.R;


public class CProgressDialog extends Dialog {

    private Context mContext;


    public CProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CProgressDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    public CProgressDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.common_dialog_progressbar);

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//
//		DisplayMetrics  dm = new DisplayMetrics();
//		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//		wm.getDefaultDisplay().getMetrics(dm);
        lp.width = dpToPx(this.getContext(), 90);
        dialogWindow.setAttributes(lp);
    }

    private int dpToPx(Context context, float dpValue) {//dp转换为px
        float scale = context.getResources().getDisplayMetrics().density;//获得当前屏幕密度
        return ( int ) (dpValue * scale + 0.5f);
    }


}
