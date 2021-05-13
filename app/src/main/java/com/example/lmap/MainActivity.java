
package com.example.lmap;

import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import static com.baidu.mapapi.map.BaiduMap.MAP_TYPE_NONE;
import static com.baidu.mapapi.map.BaiduMap.MAP_TYPE_NORMAL;
import static com.baidu.mapapi.map.BaiduMap.MAP_TYPE_SATELLITE;

public class MainActivity extends AppCompatActivity{
    private MapView mMapView = null;
    private  LocationClient  mLocationClient=null;
    private PoiSearch mPoiSearch=null;
    private EditText mInputText=null;
    private BDLocation mCurLocation=null;
    private RoutePlanSearch mRoutePlanSrch=null;
    class MyLocationListener extends BDAbstractLocationListener {
        private boolean isFirstLoc=true;
        private boolean autoLocation=false;
        public void setAutoLocation(boolean b){
            autoLocation=b;
        }
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //mapView 销毁后不在处理新接收的位置
            if (bdLocation == null || mMapView == null){
                return;
            }
            mCurLocation=bdLocation;//保存当前定位，后面检索算路要用
            int type=bdLocation.getLocType();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            BaiduMap bmap=mMapView.getMap();
            bmap.setMyLocationData(locData);
            /**
             *当首次定位或手动发起定位时，记得要放大地图，便于观察具体的位置
             * LatLng是缩放的中心点，这里注意一定要和上面设置给地图的经纬度一致；
             * MapStatus.Builder 地图状态构造器
             */
            if (isFirstLoc||autoLocation) {
                isFirstLoc = false;
                autoLocation=false;
                LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                //设置缩放中心点；缩放比例；
                builder.target(ll).zoom(18.0f);
                //给地图设置状态
                bmap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }
    //创建poi检索监听器
    OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            //显示搜索结果
            List<PoiInfo> poiList=poiResult.getAllPoi();
            PoiAdapter adapter=new PoiAdapter(MainActivity.this,R.layout.poi_item,poiList);
            ListView listView=findViewById(R.id.searchResult);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);

            //当滑动到底部时加载更多搜索结果
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        // 判断是否滚动到底部
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            //加载更多
                            int curPage=poiResult.getCurrentPageNum();
                            int totalPage=poiResult.getTotalPageNum();
                            if(curPage< totalPage)
                            {
                                poiResult.setCurrentPageNum(curPage+1);
                                String city=mCurLocation.getCity();
                                TextView textV=findViewById(R.id.inputText);
                                String keyWord=textV.getText().toString();
                                mPoiSearch.searchInCity(new PoiCitySearchOption()
                                        .city(city)
                                        .keyword(keyWord)
                                        .pageNum(curPage+1));
                            }
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        }
        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        //开启定位
        mMapView.getMap().setMyLocationEnabled(true);
        //定位初始化
        mLocationClient = new LocationClient(this);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mLocationClient.start();

        //获得检索输入框控件
        mInputText=findViewById(R.id.inputText);
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean ret=false;
                if(actionId== EditorInfo.IME_ACTION_SEARCH)
                {
                    String city=mCurLocation.getCity();
                    String keyWord=v.getText().toString();
                    ret=mPoiSearch.searchInCity(new PoiCitySearchOption()
                            .city(city)
                            .keyword(keyWord)
                            .pageNum(0));
                    //搜索后隐藏键盘
                    InputMethodManager imum=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    View view=getWindow().peekDecorView();
                    if(view!=null)
                    {
                        imum.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }
                }
                return ret;
            }
        });
        //创建poi检索实例
        mPoiSearch = PoiSearch.newInstance();
        //设置监听器
        mPoiSearch.setOnGetPoiSearchResultListener(poiSearchListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ImageButton matype = this.findViewById(R.id.mapTypeBtn);
        matype.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                BaiduMap map= mMapView.getMap();
                int type=map.getMapType();
                switch(type)
                {
                    case MAP_TYPE_NORMAL:
                        map.setMapType( MAP_TYPE_SATELLITE);//从普通地图切换到卫星地图
                        break;
                    case MAP_TYPE_SATELLITE:
                        map.setMapType(MAP_TYPE_NONE);//从卫星地图切换到空白地图
                        break;
                    case MAP_TYPE_NONE:
                        map.setMapType(MAP_TYPE_NORMAL);//从空白地图切换到普通地图111
                        break;
                }
            }
        });

        ImageButton locationBtn=this.findViewById(R.id.mylocation);
        locationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //设置手动定位
                myLocationListener.setAutoLocation(true);
                //开启地图定位图层
                mLocationClient.start();
            }
        });

        //创建路线规划检索实例
        mRoutePlanSrch = RoutePlanSearch.newInstance();
        //创建路线规划检索结果监听器
        OnGetRoutePlanResultListener routePlanSrchlistener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                //创建DrivingRouteOverlay实例
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mMapView.getMap());
                List<DrivingRouteLine> routelines=drivingRouteResult.getRouteLines();
                if(routelines!=null&&routelines.size()>0)
                {
                    for(DrivingRouteLine routeLine:routelines
                    ) {
                        //为DrivingRouteOverlay实例设置数据
                        overlay.setData(routeLine);
                        //在地图上绘制DrivingRouteOverlay
                        overlay.addToMap();

                    }

                    //a关闭搜索结果
                    ListView listView = findViewById(R.id.searchResult);
                    listView.setVisibility(View.GONE);
                }
            }
        };
        //设置路线规划检索监听器
        mRoutePlanSrch.setOnGetRoutePlanResultListener(routePlanSrchlistener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected  void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stop();
        mMapView.getMap().setMyLocationEnabled(false);
        mMapView.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

     @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
     if (ev.getAction() == MotionEvent.ACTION_DOWN) {
         ListView listView = findViewById(R.id.searchResult);
         int left = listView.getLeft(), top = listView.getTop(), right = left + listView.getWidth(), bottom = top + listView.getHeight();
         if (ev.getX() < left || ev.getX() > right || ev.getY() < top || ev.getY() > bottom)//点击搜索结果列表之外区域，隐藏搜索结果列表
             listView.setVisibility(View.GONE);
     }
     return super.dispatchTouchEvent(ev);
    }

    public BDLocation curLocation() {
        return mCurLocation;
    }
    public RoutePlanSearch routePlanSearch() {
        return mRoutePlanSrch;
    }
}