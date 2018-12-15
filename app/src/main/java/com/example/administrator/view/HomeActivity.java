package com.example.administrator.view;

import android.Manifest;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;


import com.example.administrator.R;
import com.example.administrator.util.ToastUtil;
import com.example.administrator.util.Constants;

import com.example.administrator.model.*;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.example.administrator.util.Constants.RC_CAMERA__CALENDAR_STORAGE_PHONE_LOCATION;


public class HomeActivity extends Fragment implements View.OnClickListener ,
        OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnMarkerClickListener,
        OnPoiSearchListener,EasyPermissions.PermissionCallbacks{


    private static final String TAG = "HomeActivity";

    //显示地图属性
    private View view;
    private MapView mapView;
    private AMap aMap;

    //搜索结果属性
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private Marker locationMarker; // 选择的点
    private Marker detailMarker;
    private Marker mlastMarker;
    private LatLonPoint search_point;
    private PoiSearch poiSearch;
    private myPoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据
    private RelativeLayout mPoiDetail;
    private TextView mPoiName, mPoiAddress;
    private String keyWord = "";
    private EditText mSearchText;


    //页面按钮
    private Button start_trace; //开始记录按钮
    private int button_status = 0;
    private Button review; //写评论按钮
    private Button share; //分享按钮



    //定位功能属性
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation privLocation;
    private AMapLocation curLocation;
    private double distance;
    private boolean start_draw = false;
    private CircleOptions circle;


    //数据部分属性
    private Strategy strategy;
    private Route route;
    private DotStrategy dotStrategy;
    private List<Point> points;
    private List<DotStrategy> dotStrategies;
    private static Double total_Latitude = 0.0;
    private static Double total_Longitude = 0.0;
    private static int count = 0;


    //从写评论界面返回信息
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case 1 :
                if(resultCode == RESULT_OK){
                    DotStrategy dotStrategy = (DotStrategy)data.getSerializableExtra("strategy_data");
                    dotStrategies.add(dotStrategy);
//                    Log.e(TAG,"信息："+dotStrategy.getComment());


                    //按回退按钮
//                    String dataBack = data.getStringExtra("dataBack");
//                    if(dataBack != null){
//                        Log.d("dataBack", dataBack);
//                    }
                }
                break;
            default : break;
        }

    }


    //重写请求权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions, grantResults, this);
    }

    /**
     * 去申请权限
     */
    private void requestPermissions() {

        String[] perms = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_CALL_LOG
        };

        //判断有没有权限
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            //

        } else {

            // 如果没有权限, 就去申请权限
            // this: 上下文
            // Dialog显示的正文
            // RC_CAMERA_AND_RECORD_AUDIO 请求码, 用于回调的时候判断是哪次申请
            // perms 就是申请的权限
            EasyPermissions.requestPermissions(this, "需要申请您的相机、手机访问、存储、定位权限", RC_CAMERA__CALENDAR_STORAGE_PHONE_LOCATION, perms);

        }
    }


    /**
     * 权限申请成功的回调
     *
     * @param requestCode 申请权限时的请求码
     * @param perms 申请成功的权限集合
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsGranted: ");
        if (requestCode != RC_CAMERA__CALENDAR_STORAGE_PHONE_LOCATION) {
            return;
        }
        for (int i = 0; i < perms.size(); i++) {
            if (perms.get(i).equals(Manifest.permission.CAMERA)) {
                Log.i(TAG, "onPermissionsGranted: " + "相机权限成功");

            } else if (perms.get(i).equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.i(TAG, "onPermissionsGranted: " + "存储权限成功");
            }else if (perms.get(i).equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.i(TAG, "onPermissionsGranted: " + "定位权限成功");
            }else if (perms.get(i).equals(Manifest.permission.READ_CALL_LOG)) {
                Log.i(TAG, "onPermissionsGranted: " + "手机访问权限成功");
            }

        }

    }

    /**
     * 权限申请拒绝的回调
     *
     * @param requestCode 申请权限时的请求码
     * @param perms 申请拒绝的权限集合
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsDenied: ");

        if (requestCode != RC_CAMERA__CALENDAR_STORAGE_PHONE_LOCATION) {
            return;
        }

        for (int i = 0; i < perms.size(); i++) {
            if (perms.get(i).equals(Manifest.permission.CAMERA)) {
                Log.i(TAG, "onPermissionsDenied: " + "相机权限失败");

            } else if (perms.get(i).equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.i(TAG, "onPermissionsDenied: " + "存储权限失败");
            }else if (perms.get(i).equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.i(TAG, "onPermissionsDenied: " + "定位权限失败");
            }else if (perms.get(i).equals(Manifest.permission.READ_CALL_LOG)) {
                Log.i(TAG, "onPermissionsDenied: " + "手机访问权限失败");
            }
        }

    }


    //底栏
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_find:
                    Intent intent = new Intent(getActivity(),FragmentItemSetsActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home,container,false);
        requestPermissions();


//        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        if (aMap == null) {
            aMap = mapView.getMap();

        }

        init();
        setup();

        return view;


    }




    /**
     * 初始化AMap对象
     */
    private void init() {


        TextView searchButton = (TextView) view.findViewById(R.id.btn_search);
        searchButton.setOnClickListener(this);
        start_trace =(Button)view.findViewById(R.id.start_trace);
        review  =(Button)view.findViewById(R.id.review);
        share = (Button)view.findViewById(R.id.share);

        circle = new CircleOptions().
                center(new LatLng(30,116)).
                radius(10000000).
                fillColor(Color.argb(55, 1, 1, 1));




        start_trace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (button_status){
                    //点击开始记录
                    case 0:

                        strategy = new Strategy();
                        points = new ArrayList<Point>();
                        dotStrategies = new ArrayList<DotStrategy>();

                        start_trace.setBackgroundResource(R.drawable.end_trace);
                        start_trace.setText(getResources().getString(R.string.end_trace));
                        start_trace.setPadding(0,0,0,0);
                        button_status = 1;
                        review.setVisibility(View.VISIBLE);
                        review.setPadding(0,0,0,0);
                        start_draw = true;
                        aMap.addCircle(circle);


                        break;

                    //点击结束记录
                    case 1:


                        button_status = 2;
                        review.setVisibility(View.INVISIBLE);
                        start_trace.setVisibility(View.INVISIBLE);
                        share.setVisibility(View.VISIBLE);
                        share.setPadding(0,0,0,0);


                        start_draw = false;



                        break;




                }


            }
        });

        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                aMap.clear(true);

                Date date = new Date();
                route = new Route();
                route.setPoints(points);
                route.setTotal_distance(distance);
                strategy.setFeat_LatLng(new Point(total_Latitude/count,total_Longitude/count));
                strategy.setRoute(route);
                strategy.setPublish_time(date);
                strategy.setDotStrategy(dotStrategies);
                Intent intent = new Intent(getActivity(),ShareSubmitActivity.class);
                intent.putExtra("strategy_data", strategy);
                startActivity(intent);

            }
        });

        review.setOnClickListener(new View.OnClickListener() {

            @Override
            //点击写评论按钮
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),CommentActivity.class);
                startActivityForResult(intent,1);

            }


        });




    }

    private void setup() {

        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);// 设置缩放按钮是否显示
        uiSettings.setScaleControlsEnabled(true);// 设置比例尺是否显示
        uiSettings.setRotateGesturesEnabled(true);// 设置地图旋转是否可用
        uiSettings.setTiltGesturesEnabled(true);// 设置地图倾斜是否可用
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(mLocationSource);// 设置定位监听
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.setMapTextZIndex(2);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        mPoiDetail = (RelativeLayout) view.findViewById(R.id.poi_detail);
        mPoiDetail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				Intent intent = new Intent(PoiSearchActivity.this,
//						SearchDetailActivity.class);
//				intent.putExtra("poiitem", mPoi);
//				startActivity(intent);

            }
        });
        mPoiName = (TextView) view.findViewById(R.id.poi_name);
        mPoiAddress = (TextView) view.findViewById(R.id.poi_address);
        mSearchText = (EditText)view.findViewById(R.id.input_edittext);
    }





    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        keyWord = mSearchText.getText().toString().trim();
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        search_point = new LatLonPoint(curLocation.getLatitude(),curLocation.getLongitude());


        poiSearch = new PoiSearch(getActivity(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new SearchBound(search_point, 5000, true));//
        // 设置搜索区域为以当前点为圆心，其周围5000米范围
        poiSearch.searchPOIAsyn();// 异步搜索
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search:
                doSearchQuery();
                break;

            default:
                break;
        }
    }




    //地点搜索
    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
                        if (mlastMarker != null) {
                            resetlastmarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay !=null) {
                            poiOverlay.removeFromMap();
                        }
                        aMap.clear();
                        poiOverlay = new myPoiOverlay(aMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.mipmap.point4)))
                                .position(new LatLng(curLocation.getLatitude(), curLocation.getLongitude())));

                        aMap.addCircle(new CircleOptions()
                                .center(new LatLng(curLocation.getLatitude(),
                                        curLocation.getLongitude())).radius(5000)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 1, 1, 1))
                                .strokeWidth(2));

                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(getActivity(),
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil
                        .show(getActivity(), R.string.no_result);
            }
        } else  {
            ToastUtil.showerror(this.getActivity(), rcode);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);

        } else {
            mPoiDetail.setVisibility(View.GONE);

        }
    }

    private int[] markers = {
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_2,
            R.mipmap.poi_marker_3,
            R.mipmap.poi_marker_4,
            R.mipmap.poi_marker_5,
            R.mipmap.poi_marker_6,
            R.mipmap.poi_marker_7,
            R.mipmap.poi_marker_8,
            R.mipmap.poi_marker_9,
            R.mipmap.poi_marker_10,


    };

    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        }else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.mipmap.marker_other_highlight)));
        }
        mlastMarker = null;

    }

    @Override
    public void onMapClick(LatLng arg0) {
        whetherToShowDetailInfo(false);
        if (mlastMarker != null) {
            resetlastmarker();
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(getActivity(), infomation);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);
            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.mipmap.poi_marker_pressed)));

                setPoiItemDisplayContent(mCurrentPoi);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }


        return true;
    }

    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet()+mCurrentPoi.getDistance());
    }





    /**
     * 绘制运动路线
     *
     * @param curLocation
     */
    public void drawLines(AMapLocation curLocation) {

        if (null == privLocation) {
            return;
        }
        PolylineOptions options = new PolylineOptions();
        //上一个点的经纬度
        options.add(new LatLng(privLocation.getLatitude(), privLocation.getLongitude()));
        //当前的经纬度
        options.add(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));
        options.width(35);
        options.setCustomTexture(BitmapDescriptorFactory.fromResource(R.mipmap.map_alr3));
        options.aboveMaskLayer(true);
        aMap.addPolyline(options);
        //距离的计算
        distance = AMapUtils.calculateLineDistance(new LatLng(privLocation.getLatitude(),
                privLocation.getLongitude()), new LatLng(curLocation.getLatitude(),
                curLocation.getLongitude()));

    }
    public LocationSource mLocationSource = new LocationSource() {
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            mListener = onLocationChangedListener;
            initAmapLocation();
        }
        @Override
        public void deactivate() {
            mListener = null;
            if (mLocationClient != null) {
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
            }
            mLocationClient = null;
        }




    };

    /**
     * 初始化定位
     */
    private void initAmapLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity());
        //设置定位回调监听
        mLocationClient.setLocationListener(mAMapLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        // 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，设备定位模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        mLocationOption.setMockEnable(true);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.startLocation();
        }
    }

    /**
     * 定位回调每1秒调用一次
     */
    public AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点,不写这一句无法显示到当前位置
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表




                    Log.e(TAG, "获取经纬度集合" + privLocation);//打Log记录点是否正确

                    amapLocation.getAccuracy();//获取精度信息
                    amapLocation.getBearing();//获取方向角信息
                    amapLocation.getSpeed();//获取速度信息  单位：米/秒
                    amapLocation.getLocationType();//查看是什么类型的点
                    Log.e(TAG, "获取点的类型" + amapLocation.getLocationType());
                    if (amapLocation.getLocationType() == 1&&start_draw) {


                        drawLines(amapLocation);//一边定位一边连线
                        points.add(new Point(amapLocation.getLatitude(),amapLocation.getLongitude()));
                        total_Latitude+= amapLocation.getLatitude();
                        total_Longitude+= amapLocation.getLongitude();
                        count++;
                        distance += distance;
//                        Toast.makeText(getActivity(), "经纬度"+distance+"KM",Toast.LENGTH_SHORT).show();


                    }
                    //获取定位时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    sdf.format(date);
                    privLocation = amapLocation;
                    curLocation = amapLocation;

//
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
                }
            }
        }
    };








    private class myPoiOverlay {
        private AMap mamap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
        public myPoiOverlay(AMap amap ,List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         * @since V2.1.0
         */
        public void addToMap() {
            if(mPois != null) {
                int size = mPois.size();
                for (int i = 0; i < size; i++) {
                    Marker marker = mamap.addMarker(getMarkerOptions(i));
                    PoiItem item = mPois.get(i);
                    marker.setObject(item);
                    mPoiMarks.add(marker);
                }
            }
        }





        private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
            mPoiName.setText(mCurrentPoi.getTitle());
            mPoiAddress.setText(mCurrentPoi.getSnippet()+mCurrentPoi.getDistance());
        }






        private void whetherToShowDetailInfo(boolean isToShow) {
            if (isToShow) {
                mPoiDetail.setVisibility(View.VISIBLE);

            } else {
                mPoiDetail.setVisibility(View.GONE);

            }
        }


        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            if(mPois != null) {
                int size = mPois.size();
                for (int i = 0; i < size; i++) {
                    b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                            mPois.get(i).getLatLonPoint().getLongitude()));
                }
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            }else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.marker_other_highlight));
                return icon;
            }
        }
    }








}