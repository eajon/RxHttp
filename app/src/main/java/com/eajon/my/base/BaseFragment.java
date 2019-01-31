package com.eajon.my.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.eajon.base.RxBusFragment;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;


/**
 * Created by newbiechen on 17-3-31.
 */

public abstract class BaseFragment extends RxBusFragment {


    protected Activity self;

    @LayoutRes
    protected abstract int setContentId();

    protected abstract void initView();

    protected abstract void initClick();

    protected abstract void initData(Bundle savedInstanceState);

    protected abstract void initLogic();

    /******************************lifecycle area*****************************************/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int resId = setContentId();
        return inflater.inflate(resId, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        self = this.getActivity();
        ButterKnife.bind(this, view);
        initView();
        initClick();
        initData(savedInstanceState);
        initLogic();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    protected int getWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        self.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    protected int getHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        self.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    protected void toActivity(Class activity) {
        Intent intent = new Intent();
        intent.setClass(self, activity);
        startActivity(intent);
    }
}
