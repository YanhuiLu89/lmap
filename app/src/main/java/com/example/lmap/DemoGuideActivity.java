package com.example.lmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.baidu.navisdk.adapter.BNaviCommonParams;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNaviListener;
import com.baidu.navisdk.adapter.IBNaviViewListener;
import com.baidu.navisdk.adapter.struct.BNGuideConfig;
import com.baidu.navisdk.adapter.struct.BNHighwayInfo;
import com.baidu.navisdk.adapter.struct.BNRoadCondition;
import com.baidu.navisdk.adapter.struct.BNaviInfo;
import com.baidu.navisdk.adapter.struct.BNaviLocation;
import com.baidu.navisdk.ui.routeguide.model.RGLineItem;

import java.util.List;

import static com.baidu.navisdk.adapter.IBNaviViewListener.Action.ContinueNavi;
import static com.baidu.navisdk.adapter.IBNaviViewListener.Action.OpenSetting;

public class DemoGuideActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //管理专业导航生命周期
        Bundle bundle = new Bundle();
        // IS_REALNAVI代表导航类型，true表示真实导航，false表示模拟导航，默认是true
        boolean isRealNavi=getIntent().getBooleanExtra("isRealNavi",true);
        bundle.putBoolean(BNaviCommonParams.ProGuideKey.IS_REALNAVI,isRealNavi);
        // IS_SUPPORT_FULL_SCREEN代表是否沉浸式，默认是true
        bundle.putBoolean(BNaviCommonParams.ProGuideKey.IS_SUPPORT_FULL_SCREEN, true);
        BNGuideConfig config = new BNGuideConfig.Builder().params(bundle).build();
        View view=BaiduNaviManagerFactory.getRouteGuideManager().onCreate(this, config);
        if (view != null) {
            setContentView(view);
        }

        //设置导航事件监听
        BaiduNaviManagerFactory.getRouteGuideManager().setNaviListener(new IBNaviListener() {
            @Override
            public void onRoadNameUpdate(String s) {

            }

            @Override
            public void onRemainInfoUpdate(int i, int i1) {

            }

            @Override
            public void onViaListRemainInfoUpdate(Message message) {

            }

            @Override
            public void onGuideInfoUpdate(BNaviInfo bNaviInfo) {

            }

            @Override
            public void onHighWayInfoUpdate(Action action, BNHighwayInfo bnHighwayInfo) {

            }

            @Override
            public void onFastExitWayInfoUpdate(Action action, String s, int i, String s1) {

            }

            @Override
            public void onEnlargeMapUpdate(Action action, View view, String s, int i, String s1, Bitmap bitmap) {

            }

            @Override
            public void onDayNightChanged(DayNightMode dayNightMode) {

            }

            @Override
            public void onRoadConditionInfoUpdate(double v, List<BNRoadCondition> list) {

            }

            @Override
            public void onMainSideBridgeUpdate(int i) {

            }

            @Override
            public void onLaneInfoUpdate(Action action, List<RGLineItem> list) {

            }

            @Override
            public void onSpeedUpdate(int i, int i1) {

            }

            @Override
            public void onArriveDestination() {

            }

            @Override
            public void onArrivedWayPoint(int i) {

            }

            @Override
            public void onLocationChange(BNaviLocation bNaviLocation) {

            }

            @Override
            public void onMapStateChange(MapStateMode mapStateMode) {

            }

            @Override
            public void onStartYawing(String s) {

            }

            @Override
            public void onYawingSuccess() {

            }

            @Override
            public void onYawingArriveViaPoint(int i) {

            }

            @Override
            public void onNotificationShow(String s) {

            }

            @Override
            public void onHeavyTraffic() {

            }

            @Override
            public void onNaviGuideEnd() {
                finish();

            }
        });

        //设置导航视图监听
        BaiduNaviManagerFactory.getRouteGuideManager().setNaviViewListener(new IBNaviViewListener() {
            @Override
            public void onMainInfoPanCLick() {

            }

            @Override
            public void onNaviTurnClick() {

            }

            @Override
            public void onFullViewButtonClick(boolean b) {

            }

            @Override
            public void onFullViewWindowClick(boolean b) {

            }

            @Override
            public void onNaviBackClick() {
                BaiduNaviManagerFactory.getRouteGuideManager().stopNavi();
            }

            @Override
            public void onBottomBarClick(Action action) {

            }

            @Override
            public void onNaviSettingClick() {

            }

            @Override
            public void onRefreshBtnClick() {

            }

            @Override
            public void onZoomLevelChange(int i) {

            }

            @Override
            public void onMapClicked(double v, double v1) {

            }

            @Override
            public void onMapMoved() {

            }

            @Override
            public void onFloatViewClicked() {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        BaiduNaviManagerFactory.getRouteGuideManager().onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        BaiduNaviManagerFactory.getRouteGuideManager().onResume();
    }

    @Override
    protected  void onPause() {
        super.onPause();
        BaiduNaviManagerFactory.getRouteGuideManager().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
       BaiduNaviManagerFactory.getRouteGuideManager().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaiduNaviManagerFactory.getRouteGuideManager().onDestroy(false);
    }
}