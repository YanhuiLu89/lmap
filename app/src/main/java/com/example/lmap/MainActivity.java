
package com.example.lmap;

import android.content.Intent;
import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
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
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBNTTSManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.baidu.navisdk.adapter.struct.BNTTsInitConfig;
import com.baidu.navisdk.adapter.struct.BNaviInitConfig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
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
    private File mSDCardPath=null;
    private static final String APP_FOLDER_NAME = "lmap";
    private PoiInfo mDestation=null;

    class MyLocationListener extends BDAbstractLocationListener {
        private boolean isFirstLoc=true;
        private boolean autoLocation=false;
        public void setAutoLocation(boolean b){
            autoLocation=b;
        }
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //mapView ???????????????????????????????????????
            if (bdLocation == null || mMapView == null){
                return;
            }
            mCurLocation=bdLocation;//?????????????????????????????????????????????
            int type=bdLocation.getLocType();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    // ?????????????????????????????????????????????????????????0-360
                    .direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            BaiduMap bmap=mMapView.getMap();
            bmap.setMyLocationData(locData);
            /**
             *?????????????????????????????????????????????????????????????????????????????????????????????
             * LatLng??????????????????????????????????????????????????????????????????????????????????????????
             * MapStatus.Builder ?????????????????????
             */
            if (isFirstLoc||autoLocation) {
                isFirstLoc = false;
                autoLocation=false;
                LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                //???????????????????????????????????????
                builder.target(ll).zoom(18.0f);
                //?????????????????????
                bmap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }
    //??????poi???????????????
    OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            //??????????????????
            List<PoiInfo> poiList=poiResult.getAllPoi();
            PoiAdapter adapter=new PoiAdapter(MainActivity.this,R.layout.poi_item,poiList);
            ListView listView=findViewById(R.id.searchResult);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);

            //?????????????????????????????????????????????
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        // ???????????????????????????
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            //????????????
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
        //??????
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //????????????????????????
        mMapView = (MapView) findViewById(R.id.bmapView);

        //????????????
        mMapView.getMap().setMyLocationEnabled(true);
        //???????????????
        mLocationClient = new LocationClient(this);
        //??????LocationClientOption??????LocationClient????????????
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // ??????gps
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll"); // ??????????????????
        option.setScanSpan(1000);
        //??????locationClientOption
        mLocationClient.setLocOption(option);
        //??????LocationListener?????????
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //????????????????????????
        mLocationClient.start();

        //???????????????????????????
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
                    //?????????????????????
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
        //??????poi????????????
        mPoiSearch = PoiSearch.newInstance();
        //???????????????
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
                        map.setMapType( MAP_TYPE_SATELLITE);//????????????????????????????????????
                        break;
                    case MAP_TYPE_SATELLITE:
                        map.setMapType(MAP_TYPE_NONE);//????????????????????????????????????
                        break;
                    case MAP_TYPE_NONE:
                        map.setMapType(MAP_TYPE_NORMAL);//????????????????????????????????????111
                        break;
                }
            }
        });

        ImageButton locationBtn=this.findViewById(R.id.mylocation);
        locationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //??????????????????
                myLocationListener.setAutoLocation(true);
                //????????????????????????
                mLocationClient.start();
            }
        });

        //??????????????????????????????
        mRoutePlanSrch = RoutePlanSearch.newInstance();
        //???????????????????????????????????????
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
                //??????DrivingRouteOverlay??????
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mMapView.getMap());
                List<DrivingRouteLine> routelines=drivingRouteResult.getRouteLines();
                if(routelines!=null&&routelines.size()>0)
                {
                    for(DrivingRouteLine routeLine:routelines
                    ) {
                        //???DrivingRouteOverlay??????????????????
                        overlay.setData(routeLine);
                        //??????????????????DrivingRouteOverlay
                        overlay.addToMap();
                        overlay.zoomToSpan();
                        routeLine.getStarting();
                    }

                    //a??????????????????
                    ListView listView = findViewById(R.id.searchResult);
                    listView.setVisibility(View.GONE);
                    //????????????????????????
                    Button startNav=findViewById(R.id.startnavi);
                    startNav.setVisibility(View.VISIBLE);
                    Button simNav=findViewById(R.id.simnavi);
                    simNav.setVisibility(View.VISIBLE);

                }
                else
                {
                    mDestation=null;
                }
            }
        };
        //?????????????????????????????????
        mRoutePlanSrch.setOnGetRoutePlanResultListener(routePlanSrchlistener);

        //???????????????
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//??????sd???????????????
        if(sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//???????????????
        }
        
        if(!initDirs())
        {
            return;
        }

        BNaviInitConfig navInitCfg= new BNaviInitConfig.Builder().sdcardRootPath(mSDCardPath.toString())
                .appFolderName(APP_FOLDER_NAME)
                .naviInitListener(new IBaiduNaviManager.INaviInitListener() {
                    @Override
                    public void onAuthResult(int i, String s) {
                        if(i==0)
                        {
                           Toast.makeText(MainActivity.this, "key????????????!", Toast.LENGTH_SHORT).show();
                        }
                        else if(i==1)
                        {
                          Toast.makeText(MainActivity.this, "key????????????, " + s, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void initStart() {

                    }

                    @Override
                    public void initSuccess() {
                        Toast.makeText(MainActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                        // ?????????tts
                        initTTs();

                    }

                    @Override
                    public void initFailed(int i) {
                        Toast.makeText(MainActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();

                    }
                }).build();
        BaiduNaviManagerFactory.getBaiduNaviManager().init(getApplicationContext(),navInitCfg);

        //?????????????????????????????????
        Button startNav=findViewById(R.id.startnavi);
        startNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavi(true);
            }
        });
        Button simNav=findViewById(R.id.simnavi);
        simNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavi(false);
            }
        });
    }

    private void startNavi(boolean isRealNavi) {
        if(mCurLocation==null||mDestation==null)
            return;
        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                .latitude(mCurLocation.getLatitude())
                .longitude(mCurLocation.getLongitude())
                .name("????????????")
                .description("????????????")
                .build();
        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                .latitude(mDestation.getLocation().latitude)
                .longitude(mDestation.getLocation().longitude)
                .name(mDestation.name)
                .description(mDestation.name)
                .build();
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);
        BaiduNaviManagerFactory.getRoutePlanManager().routePlanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "????????????", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "????????????", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "????????????", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "??????????????????????????????", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this,
                                        DemoGuideActivity.class);
                                intent.putExtra("isRealNavi",isRealNavi);
                                startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }

                });
    }

    private boolean initDirs() {
        mSDCardPath = Environment.getStorageDirectory();;
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initTTs() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//??????sd???????????????
        if(sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//???????????????
        }
        BNTTsInitConfig.Builder builder=new BNTTsInitConfig.Builder();
        builder.context(getApplicationContext()).sdcardRootPath(mSDCardPath.toString()).appFolderName(APP_FOLDER_NAME).appId(("24274222"));
        BaiduNaviManagerFactory.getTTSManager().initTTS( builder.build());

        // ??????????????????tts????????????
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedListener(
                new IBNTTSManager.IOnTTSPlayStateChangedListener() {
                    @Override
                    public void onPlayStart() {
                        Log.e("lmap", "ttsCallback.onPlayStart");
                    }

                    @Override
                    public void onPlayEnd(String speechId) {
                        Log.e("lmap", "ttsCallback.onPlayEnd");
                    }

                    @Override
                    public void onPlayError(int code, String message) {
                        Log.e("lmap", "ttsCallback.onPlayError");
                    }
                }
        );

    }

    @Override
    protected void onResume(){
        super.onResume();
        //???activity??????onResume?????????mMapView. onResume ()?????????????????????????????????
        mMapView.onResume();
    }

    @Override
    protected  void onPause() {
        super.onPause();
        //???activity??????onPause?????????mMapView. onPause ()?????????????????????????????????
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //???activity??????onDestroy?????????mMapView.onDestroy()?????????????????????????????????
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
         if (ev.getX() < left || ev.getX() > right || ev.getY() < top || ev.getY() > bottom)//???????????????????????????????????????????????????????????????
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

    public void setDestation(PoiInfo poi){mDestation=poi;}
}