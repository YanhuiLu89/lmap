package baidumapsdk.demo.map;

import com.baidu.mapapi.animation.AlphaAnimation;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapapi.animation.AnimationSet;
import com.baidu.mapapi.animation.RotateAnimation;
import com.baidu.mapapi.animation.ScaleAnimation;
import com.baidu.mapapi.animation.SingleScaleAnimation;
import com.baidu.mapapi.animation.Transformation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import baidumapsdk.demo.R;

public class MarkerAnimationDemo extends Activity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private Marker mMarkerB;
    private Marker mMarkerC;
    private Marker mMarkerD;
    private Marker mMarkerE;
    private Marker mMarkerF;
    private Marker mMarkerG;
    private LatLng llA;
    private LatLng llB;
    private LatLng llC;
    private LatLng llD;
    private LatLng llE;
    private LatLng llF;
    private LatLng llG;

    private static final String LTAG = MarkerAnimationDemo.class.getSimpleName();

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marka);
    BitmapDescriptor bdB = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markb);
    BitmapDescriptor bdC = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markc);
    BitmapDescriptor bdD = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markd);
    BitmapDescriptor bdE = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marke);
    BitmapDescriptor bdF = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markf);
    BitmapDescriptor bdG = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markg);
    private Point mScreenCenterPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_animation);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initOverlay();
            }
        });

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus status) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus status, int reason) {

            }

            @Override
            public void onMapStatusChange(MapStatus status) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus status) {
                if (null == mMarkerF) {
                    return;
                }
                mMarkerF.setAnimation(getTransformationPoint());
                mMarkerF.startAnimation();
            }
        });

    }

    /**
     * 开启动画
     *
     * @param view
     */
    public void startAnimation(View view) {
        switch (view.getId()) {
            case R.id.btn_rotate:
                //旋转动画
                startRotateAnimation();
                break;

            case R.id.btn_scale:
                //缩放动画
                startScaleAnimation();
                break;

            case R.id.btn_transformation:
                //平移动画
                startTransformation();
                break;

            case R.id.btn_alpha:
                //透明动画
                startAlphaAnimation();
                break;

            case R.id.btn_singleScale:
                //组合动画
                startSingleScaleAnimation();
                break;

            case R.id.btn_animationSet:
                //组合动画
                startAnimationSet();
                break;

            default:
                break;

        }

    }

    /**
     * 初始化Overlay
     */
    public void initOverlay() {
        // add marker overlay
        llA = new LatLng(40.023537, 116.289429);
        llB = new LatLng(40.022211, 116.406137);
        llC = new LatLng(40.022211, 116.499274);
        llD = new LatLng(39.847829, 116.289429);
        llE = new LatLng(39.862009, 116.394064);
        llG = new LatLng(39.856691, 116.503873);

        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA);
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));

        MarkerOptions ooB = new MarkerOptions().position(llB).icon(bdB);
        mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));

        MarkerOptions ooC = new MarkerOptions().position(llC).icon(bdC);
        mMarkerC = (Marker) (mBaiduMap.addOverlay(ooC));

        MarkerOptions ooD = new MarkerOptions().position(llD).icon(bdD);
        mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));

        MarkerOptions ooE = new MarkerOptions().position(llE).icon(bdE);
        mMarkerE = (Marker) (mBaiduMap.addOverlay(ooE));

        if (null != mBaiduMap.getMapStatus()) {
            llF = mBaiduMap.getMapStatus().target;
            mScreenCenterPoint = mBaiduMap.getProjection().toScreenLocation(llF);
            MarkerOptions ooF = new MarkerOptions().position(llF).icon(bdF).perspective(true)
                    .fixedScreenPosition(mScreenCenterPoint);
            mMarkerF = (Marker) (mBaiduMap.addOverlay(ooF));

        }

        MarkerOptions ooG = new MarkerOptions().position(llG).icon(bdG);
        mMarkerG = (Marker) (mBaiduMap.addOverlay(ooG));

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(12.0f);
        mBaiduMap.setMapStatus(msu);

    }

    /**
     * 开启旋转动画
     */
    public void startRotateAnimation() {
        mMarkerA.setAnimation(getRotateAnimation());
        mMarkerA.startAnimation();

    }

    /**
     * 开启缩放动画
     */
    public void startScaleAnimation() {
        mMarkerB.setAnimation(getScaleAnimation());
        mMarkerB.startAnimation();
    }

    /**
     * 开启平移动画
     */
    public void startTransformation() {
        mMarkerC.setAnimation(getTransformation());
        mMarkerC.startAnimation();

    }

    /**
     * 开启单边缩放动画 X或Y方向
     */
    public void startSingleScaleAnimation() {
        mMarkerG.setAnimation(getSingleScaleAnimation());
        mMarkerG.startAnimation();

    }

    /**
     * 添加透明动画
     */
    public void startAlphaAnimation() {
        mMarkerD.setAnimation(getAlphaAnimation());
        mMarkerD.startAnimation();

    }

    /**
     * 得到单独缩放动画类
     */
    public Animation getSingleScaleAnimation() {
        SingleScaleAnimation mSingleScale =
                new SingleScaleAnimation(SingleScaleAnimation.ScaleType.SCALE_X, 1f, 2f, 1f);
        mSingleScale.setDuration(1000);
        mSingleScale.setRepeatCount(1);
        mSingleScale.setRepeatMode(Animation.RepeatMode.RESTART);
        return mSingleScale;
    }

    /**
     * 添加组合动画
     */
    public void startAnimationSet() {
        AnimationSet animationSet = new AnimationSet();
        animationSet.addAnimation(getAlphaAnimation());
        animationSet.addAnimation(getRotateAnimation());
        animationSet.addAnimation(getSingleScaleAnimation());
        animationSet.addAnimation(getScaleAnimation());
        animationSet.setAnimatorSetMode(0);
        animationSet.setInterpolator(new LinearInterpolator());
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {
            }
        });

        if (animationSet == null) {
            return;
        }

        mMarkerE.setAnimation(animationSet);
        mMarkerE.startAnimation();
    }

    /**
     * 创建缩放动画
     */
    private Animation getScaleAnimation() {
        ScaleAnimation mScale = new ScaleAnimation(1f, 2f, 1f);
        mScale.setDuration(2000);
        mScale.setRepeatMode(Animation.RepeatMode.RESTART);//动画重复模式
        mScale.setRepeatCount(1);//动画重复次数
        mScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {
            }
        });

        return mScale;

    }

    /**
     * 创建旋转动画
     */
    private Animation getRotateAnimation() {
        RotateAnimation mRotate = new RotateAnimation(0f, 360f);
        mRotate.setDuration(1000);//设置动画旋转时间
        mRotate.setRepeatMode(Animation.RepeatMode.RESTART);//动画重复模式
        mRotate.setRepeatCount(1);//动画重复次数
        mRotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {
            }
        });

        return mRotate;
    }

    /**
     * 创建平移动画
     */
    private Animation getTransformation() {
        Point point = mBaiduMap.getProjection().toScreenLocation(llC);
        LatLng latLng1 = mBaiduMap.getProjection().fromScreenLocation(new Point(point.x, point.y - 100));
        Transformation mTransforma = new Transformation(llC, latLng1, llC);
        mTransforma.setDuration(500);
        mTransforma.setRepeatMode(Animation.RepeatMode.RESTART);//动画重复模式
        mTransforma.setRepeatCount(1);//动画重复次数
        mTransforma.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {

            }
        });

        return mTransforma;
    }

    /**
     * 创建平移坐标动画
     */
    private Animation getTransformationPoint() {

        if (null != mScreenCenterPoint) {
            Point pointTo = new Point(mScreenCenterPoint.x, mScreenCenterPoint.y - 100);
            Transformation mTransforma = new Transformation(mScreenCenterPoint, pointTo, mScreenCenterPoint);
            mTransforma.setDuration(500);
            mTransforma.setRepeatMode(Animation.RepeatMode.RESTART);//动画重复模式
            mTransforma.setRepeatCount(1);//动画重复次数
            mTransforma.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart() {
                }

                @Override
                public void onAnimationEnd() {
                }

                @Override
                public void onAnimationCancel() {
                }

                @Override
                public void onAnimationRepeat() {

                }
            });
            return mTransforma;
        }

        return null;
    }

    /**
     * 创建透明度动画
     */
    private Animation getAlphaAnimation() {
        AlphaAnimation mAlphaAnimation = new AlphaAnimation(1f, 0f, 1f);
        mAlphaAnimation.setDuration(3000);
        mAlphaAnimation.setRepeatCount(1);
        mAlphaAnimation.setRepeatMode(Animation.RepeatMode.RESTART);
        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationEnd() {
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {
            }
        });

        return mAlphaAnimation;
    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMarkerA.cancelAnimation();
        mMarkerB.cancelAnimation();
        mMarkerC.cancelAnimation();
        mMarkerD.cancelAnimation();
        mMarkerE.cancelAnimation();
        mMarkerF.cancelAnimation();
        mMarkerG.cancelAnimation();

        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        markerRemove();
        super.onDestroy();
        // 回收 bitmap 资源
        bdA.recycle();
        bdB.recycle();
        bdC.recycle();
        bdD.recycle();
        bdE.recycle();
        bdF.recycle();
        bdG.recycle();

    }

    public void markerRemove() {
        mMarkerA.remove();
        mMarkerB.remove();
        mMarkerC.remove();
        mMarkerD.remove();
        mMarkerE.remove();
        mMarkerG.remove();

    }

}
