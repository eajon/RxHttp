package com.eajon.my.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.LayoutRes;

import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;


public abstract class BaseActivity extends RxAppCompatActivity {
    protected Activity self;

    @LayoutRes
    protected abstract int setContentId();

    protected abstract void initView();

    protected abstract void initClick();

    protected abstract void initData(Bundle savedInstanceState);

    protected abstract void initLogic();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(setContentId());
        ButterKnife.bind(this);
        initView();
        initClick();
        initData(savedInstanceState);
        initLogic();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    protected int getWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    protected int getHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    protected void toActivity(Class activity) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        startActivity(intent);
    }


}
