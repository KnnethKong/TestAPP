package com.kxf.baselibrary.widget;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kxf.baselibrary.R;


public class CustomProgress extends Dialog {
    private static CustomProgress dialog;

    private static Context mContext;

    public CustomProgress(Context context) {
        super(context);
        mContext = context;
    }

    public CustomProgress(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }


//	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//	public static void resetDialog(){
//		if (isValidContext(mContext) && dialog.isShowing()) {
//			dialog.dismiss();
//			dialog =null;
//		}
//	}

    /**
     * 当窗口焦点改变时调用
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        // 获取ImageView上的动画背景
        AnimationDrawable spinner = (AnimationDrawable) imageView
                .getBackground();
        // 开始动画
        spinner.start();
    }

    /**
     * 给Dialog设置提示信息
     *
     * @param message
     */
    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(R.id.message).setVisibility(View.VISIBLE);
            TextView txt = (TextView) findViewById(R.id.message);
            txt.setText(message);
            txt.invalidate();
        }
    }

    /**
     * 弹出自定义ProgressDialog
     *
     * @param context        上下文
     * @param message        提示
     * @param cancelable     是否按返回键取消
     * @param cancelListener 按下返回键监听
     * @return
     */
    public static CustomProgress show(Context context, CharSequence message,
                                      boolean cancelable, OnCancelListener cancelListener) {
        mContext = context;
        if (dialog == null || isValidContext(mContext)) {
            dialog = new CustomProgress(context, R.style.Custom_Progress);
        }

        dialog.setTitle("");
        dialog.setContentView(R.layout.progress_custom);
        if (message == null || message.length() == 0) {
            dialog.findViewById(R.id.message).setVisibility(View.GONE);
        } else {
            TextView txt = (TextView) dialog.findViewById(R.id.message);
            txt.setText(message);
        }
        // 按返回键是否取消
        dialog.setCancelable(cancelable);
        // 监听返回键处理
        dialog.setOnCancelListener(cancelListener);
        // 设置居中
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        // 设置背景层透明度
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
    public static boolean dialogIshowing() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        }
        return false;
    }
    public static void hideProgress() {
        if (isValidContext(mContext)) {
            if (dialogIshowing()) {
                dialog.dismiss();
            }
        } else {
            if (dialogIshowing()) {
                dialog.dismiss();
                dialog = null;
            }
        }

    }

    /**
     * 判断activity是否存在
     *
     * @param c
     * @return
     */
    private static boolean isValidContext(Context c) {
        Activity a = (Activity) c;
        if (a.isDestroyed() || a.isFinishing()) {
            Log.i("YXH", "Activity is invalid." + " isDestoryed-->" + a.isDestroyed() +
                    " isFinishing-->" + a.isFinishing());
            return false;
        } else {
            return true;
        }
    }
}
