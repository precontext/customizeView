package com.program.customizeview.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.program.customizeview.R;
import com.program.customizeview.WanAnroidApi;
import com.program.customizeview.adapter.LooperPagerAdapter;
import com.program.customizeview.domain.WanAndroidBanner;
import com.program.customizeview.uitls.SizeUtils;
import com.program.customizeview.view.AutoLoopViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.program.customizeview.App.getContext;

public class LooperPageActivity extends AppCompatActivity {

    public static final String TAG="LooperPageActivity";

    @BindView(R.id.loop_pager)
    public AutoLoopViewPager mLoopViewPager;
    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;
    private Retrofit mRetrofit;
    private WanAnroidApi mWanAnroidApi;
    private LooperPagerAdapter mLooperPagerAdapter;
    private WanAndroidBanner mData;

    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Log.d(TAG,"handleMessage");
                    onLooperListLoaded(mData);
                    mLoopViewPager.setAdapter(mLooperPagerAdapter);
                    mLoopViewPager.startLoop();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper_page);
        ButterKnife.bind(this);
        initView();
        initRetrofit();
        initListener();
        getImage();
    }

    private void getImage() {
        Call<WanAndroidBanner> task = mWanAnroidApi.getBanner();
        task.enqueue(new Callback<WanAndroidBanner>() {
            @Override
            public void onResponse(Call<WanAndroidBanner> call, Response<WanAndroidBanner> response) {
                mData = response.body();
                Log.d(TAG,"data size"+mData.getData().size());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                }).start();
//                onLooperListLoaded(data);
            }

            @Override
            public void onFailure(Call<WanAndroidBanner> call, Throwable t) {

            }
        });
    }

    //???????????????
    public void onLooperListLoaded(WanAndroidBanner contents) {
        List<WanAndroidBanner.DataBean> dataBeanList = new ArrayList<>();
        for (int i = 0; i < contents.getData().size(); i++) {
            dataBeanList.add(contents.getData().get(i));
        }
        mLooperPagerAdapter.setData(dataBeanList);
        //??????????????????
        //?????????%?????????????????????0????????????????????????????????????
        int dx = (Integer.MAX_VALUE / 2) % contents.getData().size();
        int targetCenterPostion = (Integer.MAX_VALUE / 2) - dx;
        mLoopViewPager.setCurrentItem(targetCenterPostion);
        //?????????
        for (int i = 0; i < contents.getData().size(); i++) {
            View point = new View(getContext());
            //view?????????LinearLayout???
            int size = SizeUtils.dip2px(8);
            //????????????
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            point.setLayoutParams(layoutParams);
            point.setBackgroundColor(getContext().getColor(R.color.white));
            layoutParams.leftMargin = SizeUtils.dip2px(5);
            layoutParams.rightMargin = SizeUtils.dip2px(5);
            if (i == 0) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_noraml);
            }
            looperPointContainer.addView(point);
        }
    }


    private void initListener() {
        mLoopViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //??????
            }

            @Override
            public void onPageSelected(int position) {
                //???????????????
                //??????postion???????????????,???????????????
                if (mLooperPagerAdapter.getDataSize()==0) {
                    return;
                }
                int targetPosition = position % mLooperPagerAdapter.getDataSize();
                updateLooperIndicator(targetPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //????????????
            }
        });
    }


    /**
     * ???????????????
     *
     * @param targetPosition
     */
    private void updateLooperIndicator(int targetPosition) {
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);
            if (i == targetPosition) {
                point.setBackgroundResource(R.drawable.shape_flow_text_bg_press);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_noraml);
            }
        }

    }

    private void initView() {
        mLooperPagerAdapter = new LooperPagerAdapter();
//        mLoopViewPager.setAdapter(mLooperPagerAdapter);       //???onLooperListLoaded???????????????set???????????????
    }

    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder().baseUrl("https://www.wanandroid.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mWanAnroidApi = mRetrofit.create(WanAnroidApi.class);

    }




}