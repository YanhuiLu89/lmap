package baidumapsdk.demo.map;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.IndoorRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorPlanNode;
import com.baidu.mapapi.search.route.IndoorRouteLine;
import com.baidu.mapapi.search.route.IndoorRoutePlanOption;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import baidumapsdk.demo.R;
import baidumapsdk.demo.indoorview.BaseStripAdapter;
import baidumapsdk.demo.indoorview.StripListView;

public class IndoorMapDemo extends Activity implements OnGetRoutePlanResultListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    Button isIndoorBtn;
    Button indoorRoutePlane;
    Boolean isIndoor = true;

    StripListView stripListView;
    BaseStripAdapter mFloorListAdapter;
    MapBaseIndoorMapInfo mMapBaseIndoorMapInfo = null;
    RoutePlanSearch mSearch;
    IndoorRouteLine mIndoorRouteline;
    IndoorRouteOverlay mIndoorRoutelineOverlay = null;
    int nodeIndex = -1;
    private TextView popupText = null; // ??????view
    Button mBtnPre = null; // ???????????????
    Button mBtnNext = null; // ???????????????

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mainview = inflater.inflate(R.layout.activity_indoor, null);
        layout.addView(mainview);

        mMapView = (MapView) mainview.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        /* ??????????????????????????????????????????22.0f????????????????????????????????????????????????????????????????????????????????????????????????
         * ???????????????22.0f????????????
         */
        mBaiduMap.setIndoorEnable(true);

        LatLng centerpos = new LatLng(39.916958, 116.379278); // ???????????????
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(centerpos).zoom(19.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        indoorRoutePlane = (Button) mainview.findViewById(R.id.indoorRoutePlane);
        indoorRoutePlane.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ??????????????????????????????
                IndoorPlanNode startNode = new IndoorPlanNode(new LatLng(39.917380, 116.37978), "F1");
                IndoorPlanNode endNode = new IndoorPlanNode(new LatLng(39.917239, 116.37955), "F6");
                IndoorRoutePlanOption irpo = new IndoorRoutePlanOption().from(startNode).to(endNode);
                mSearch.walkingIndoorSearch(irpo);
            }
        });

        isIndoorBtn = (Button) mainview.findViewById(R.id.isIndoor);
        isIndoorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (!isIndoor) {
                    EnableIndoorMap();
                } else {
                    DisableIndoorMap();
                }
                isIndoor = !isIndoor;
            }
        });

        stripListView = new StripListView(this);
        layout.addView( stripListView );
        setContentView(layout);
        mFloorListAdapter = new BaseStripAdapter(IndoorMapDemo.this);


        mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
            @Override
            public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
                if (b == false || mapBaseIndoorMapInfo == null) {
                    stripListView.setVisibility(View.INVISIBLE);
                    return;
                }
                mFloorListAdapter.setmFloorList( mapBaseIndoorMapInfo.getFloors());
                stripListView.setVisibility(View.VISIBLE);
                stripListView.setStripAdapter(mFloorListAdapter);
                mMapBaseIndoorMapInfo = mapBaseIndoorMapInfo;
            }
        });
        stripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mMapBaseIndoorMapInfo == null) {
                    return;
                }
                String floor = (String) mFloorListAdapter.getItem(position);
                mBaiduMap.switchBaseIndoorMapFloor(floor, mMapBaseIndoorMapInfo.getID());
                mFloorListAdapter.setSelectedPostion(position);
                mFloorListAdapter.notifyDataSetInvalidated();
            }
        });

        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
    }

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
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        if (indoorRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            IndoorRouteOverlay overlay = new IndoorRouteOverlay(mBaiduMap);
            mIndoorRouteline = indoorRouteResult.getRouteLines().get(0);
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            overlay.setData(indoorRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mSearch.destroy();
        super.onDestroy();
    }


    /**
     * ??????????????????
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (mBaiduMap.isBaseIndoorMapMode()) {
            LatLng nodeLocation = null;
            String nodeTitle = null;
            IndoorRouteLine.IndoorRouteStep step = null;


            if (mIndoorRouteline == null || mIndoorRouteline.getAllStep() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            // ??????????????????
            if (v.getId() == R.id.next) {
                if (nodeIndex < mIndoorRouteline.getAllStep().size() - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            // ?????????????????????
            step = mIndoorRouteline.getAllStep().get(nodeIndex);
            nodeLocation = step.getEntrace().getLocation();
            nodeTitle = step.getInstructions();

            if (nodeLocation == null || nodeTitle == null) {
                return;
            }

            // ?????????????????????
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
            // show popup
            popupText = new TextView(IndoorMapDemo.this);
            popupText.setBackgroundResource(R.drawable.popup);
            popupText.setTextColor(0xFF000000);
            popupText.setText(step.getFloorId() + ":" + nodeTitle);
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

            // ?????????????????????
            mBaiduMap.switchBaseIndoorMapFloor(step.getFloorId(), mMapBaseIndoorMapInfo.getID());
//        mFloorListAdapter.setSelectedPostion();
            mFloorListAdapter.notifyDataSetInvalidated();
        }else{
            Toast.makeText(IndoorMapDemo.this,"????????????????????????????????????????????????",Toast.LENGTH_SHORT).show();
        }
    }

    private void EnableIndoorMap() {

        indoorRoutePlane.setEnabled(true);
        mBaiduMap.setIndoorEnable(true);
        isIndoorBtn.setText("???????????????");

        Toast.makeText(IndoorMapDemo.this, "??????????????????", Toast.LENGTH_SHORT).show();
    }

    private void DisableIndoorMap() {
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);

        indoorRoutePlane.setEnabled(false);
        if (null != mIndoorRoutelineOverlay) {
            mIndoorRoutelineOverlay.removeFromMap();
            mIndoorRoutelineOverlay = null;
        }

        mBaiduMap.clear();
        mBaiduMap.setIndoorEnable(false);
        isIndoorBtn.setText("???????????????");

        Toast.makeText(IndoorMapDemo.this, "??????????????????", Toast.LENGTH_SHORT).show();
    }
}
