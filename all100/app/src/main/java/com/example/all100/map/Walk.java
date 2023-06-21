package com.example.all100.map;
// https://ebbnflow.tistory.com/177 참고

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.SphericalUtil;
import com.moo.fighting.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// 산책 기능 동작하는 메인 액티비티
public class Walk extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback{

    // 스톱워치
    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    private long startTime, curTime, endTime; // 시작 시간과 현재 시간.
    private Date strun;

    // 맵
    private GoogleMap mMap;
    private Marker currentMarker = null;
    private boolean walkState = false; // 걸음 상태 파악해 경로 그리기 시작 및 멈춤

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // 앱 실행하기 위해 필요한 퍼미션
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    Location mCurrentLocation;
    LatLng currentPosition;

    // 거리 구하려고 선언한 거
    private LatLng previousPosition = null;
    private Marker addedMarker = null;
    private int tracking = 0;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;  // Snackbar 사용하기위함.

    private double sum_dist; // 총 이동 거리

    // 시작, 중지 버튼 눌렀을때 형성되는 마커들
    private MarkerOptions optFirst = new MarkerOptions();
    private MarkerOptions optSecond = new MarkerOptions();

    // 사진 들어갈 마커
    private MarkerOptions optImage = new MarkerOptions();

    // 카메라 부분
    final private static String TAG1 = "camera";
    final static int TAKE_PICTURE = 1;

    // 경로 변수, 사진촬영 요청 변수 생성
    String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 11;

    // 파이어베이스 유저
    private FirebaseUser user;
    private String my_contents;

    private static final int START_POPUP = 1234;

    // 이미지 경로 저장할 리스트
    private ArrayList<String> pathList = new ArrayList<>();
    private Date endRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        user = FirebaseAuth.getInstance().getCurrentUser();

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");

        Button startBtn = findViewById(R.id.start_btn);
        Button stopBtn = findViewById(R.id.stop_btn);
        Button resetBtn = findViewById(R.id.reset_btn);

        // 촬영 버튼과 이미지 띄울 이미지뷰
        Button btn_photo = findViewById(R.id.btn_photo);
        // iv_photo = findViewById(R.id.iv_photo);

        Button btn_trace = findViewById(R.id.btn_trace);

        // 버튼 눌렀을 때 거리나 평균 속도 출력할 지도 밑의 텍스트뷰
        final TextView text = (TextView) findViewById(R.id.tv_stats) ;

        // 맵 부분
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mLayout = findViewById(R.id.layout_main);
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //시작 버튼 눌렀을 때
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(location==null)
                {
                    Toast.makeText(getApplicationContext(), "현재 위치를 불러오는 중입니다.\n잠시만 기다려주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                walkState = true;
                sum_dist = 0;
                strun = new Date();

                if(!running && view.getId()==R.id.start_btn) {
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chronometer.start();
                    running = true;

                    startTime = System.currentTimeMillis();

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);

                    optFirst.anchor(0.5f, 0.5f);
                    optFirst.position(startLatLng);
                    optFirst.title("산책 시작 지점");

                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.dog);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
                    optFirst.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    mMap.addMarker(optFirst).showInfoWindow();

                }
            }
        });

        //중지 버튼 눌렀을 때
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walkState = false;
                int time = 0;
                double pace = 0;

                if(running){
                    chronometer.stop();
                    pauseOffset = SystemClock.elapsedRealtime()-chronometer.getBase();
                    running = false;

                    optSecond.anchor(0.5f, 0.5f);
                    optSecond.position(endLatLng);
                    optSecond.title("산책 종료 지점");

                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.dog);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
                    optSecond.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    mMap.addMarker(optSecond).showInfoWindow();


                    double distance = SphericalUtil.computeDistanceBetween(startLatLng, optSecond.getPosition());
                    sum_dist += distance;
                    String total_dist = String.format("%.2fm",sum_dist);

                    // 종료 시간 저장
                    endTime = System.currentTimeMillis();
                    time = (int)(endTime-startTime)/1000;
                    pace = sum_dist/time;
                    String total_pace = String.format("%.2fm/s",pace);

//                    if(time>=3)
//                        text.setText("오늘 산책한 거리는 : "+total_dist+"\n평균 산책 속도는 : "+total_pace);
//                    else
//                        text.setText("산책한 시간이 3초 미만입니다.\n산책 거리와 속도를 측정할 수 없습니다.");

                    endRun = new Date();
                }

                if(time>=3&&pace>0&&pace<150) {
                    Intent intent = new Intent(getApplicationContext(), Popup.class);
                    startActivityForResult(intent, START_POPUP);
                } else if(time<3){
                    startToast("산책 시간이 너무 짧습니다. 다시 시도해주세요");
                }else if(pace>150){
                    startToast("비정상적인 산책입니다. 다시 시도해주세요");
                }
            }
        });

        //초기화 버튼 눌렀을 때
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pathList.clear();
                walkState = false;
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;
                chronometer.stop();
                running = false;

                // 맵의 마커들 전부 지우기
                mMap.clear();

                text.setText(" ");
            }
        });

        // 카메라 권한 설정
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG1, "권한 설정 완료");
            }
            else {
                Log.d(TAG1, "권한 설정 요청");
                ActivityCompat.requestPermissions(Walk.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        // 촬영 버튼 눌렀을 때
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_photo:
                        // 썸네일 띄우는 부분
                        //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        //startActivityForResult(cameraIntent, TAKE_PICTURE);

                        // 저장한 사진 띄우는 부분. 달리고 있을때만!
                        if(running){
                            dispatchTakePictureIntent();
                        }
                        break;
                }
            }
        });

        // 나의 산책기록 버튼 눌렀을때
        btn_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyWalkRecord.class);
                Log.e("로그", "산책기록 실행");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //지도 초기위치를 서울로 지정
        setDefaultLocation();

        //위치 퍼미션 갖고 있는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            startLocationUpdates(); //위치 업데이트 시작

        }else {
            //퍼미션 거부하면
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                //퍼미션 이유 알려주기
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        //퍼미션 요청하기
                        ActivityCompat.requestPermissions( Walk.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });
    }

    // 경로 선긋기 위함
    private LatLng startLatLng = new LatLng(0, 0);
    private LatLng endLatLng = new LatLng(0, 0);
    List<Polyline>polylines =new ArrayList<>();

    //polyline을 그려주는 메소드
    private void drawPath(){
        PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(10).color(0xFFCD853F).geodesic(true);
        polylines.add(mMap.addPolyline(options));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 20));
    }

    // 현재 위치가 변경되는 경우에 호출됨.
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                // 경로 긋기 위해 위도 경도 추가
                double latitude =location.getLatitude();
                double longitude=location.getLongitude();

                if(startLatLng.latitude==0&&startLatLng.longitude==0){
                    startLatLng=new LatLng(latitude,longitude);
                }

                // 받아온 위도 경로를 대입
                currentPosition = new LatLng(latitude, longitude);


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(latitude)
                        + " 경도:" + String.valueOf(longitude);

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocation = location;

                curTime = System.currentTimeMillis();
                int time = (int)(curTime - startTime)/1000;

                String total_dist = String.format("%.2f",sum_dist);

                // 경로 그리기 -> walkState가 true일 때만! 즉, 시작 버튼 클릭됐을 때만! 3초마다 경로 업데이트
                if(walkState && time%3==0) {
                    mCurrentLocation = location;
                    endLatLng = new LatLng(latitude, longitude);
                    drawPath();
                    double distance = SphericalUtil.computeDistanceBetween(startLatLng, endLatLng);
                    sum_dist += distance;
                    startLatLng = new LatLng(latitude, longitude);
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            if (checkPermission())
                mMap.setMyLocationEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (mFusedLocationClient != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    public String getCurrentAddress(LatLng latlng) {

        //지오코더 -> gps를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.alpha(0f);
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);

        //if(walkState){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);
        //}
    }

    public void setDefaultLocation() {
        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 20);
        mMap.moveCamera(cameraUpdate);
    }

    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                startLocationUpdates();
            } else{

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    //gps 활성화
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Walk.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;
                        return;
                    }
                }
                break;

            case START_POPUP:
                if(resultCode == RESULT_OK){
                    my_contents = intent.getStringExtra("contents");
                    // 파이어베이스에 데이터 전달 (내용, 시간, 거리, 속도)
                    //Toast.makeText(Walk.this, my_contents, Toast.LENGTH_SHORT).show(); -> 확인용
                    storageUploader();
                }else{
                    my_contents = "";
                }
        }

        // 카메라로 촬영한 것 가져오는 부분
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);

                        Bitmap bitmap;
                            if (Build.VERSION.SDK_INT >= 29) {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                                try {
                                    bitmap = ImageDecoder.decodeBitmap(source);
                                    //if (bitmap != null) { iv_photo.setImageBitmap(bitmap); }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                    //if (bitmap != null) { iv_photo.setImageBitmap(bitmap); }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                    }
                    break;
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }

        if(running) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng1 = new LatLng(latitude, longitude); // 현재 위치

            optImage.anchor(0.5f, 0.5f);
            optImage.position(latLng1);

            File file = new File(mCurrentPhotoPath);
            //별별
            pathList.add(mCurrentPhotoPath);
            Bitmap b = null;
            ExifInterface exif = null;
            try {
                b = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                exif = new ExifInterface(mCurrentPhotoPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 이미지를 회전각도를 구한다
                int exifOrientation;
                int exifDegree;

                if (exif != null) {
                    exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    exifDegree = exifOrientationToDegrees(exifOrientation);
                } else {
                    exifDegree = 0;
                }

            Bitmap smallMarker = rotate(b, exifDegree);
            smallMarker = smallMarker.createScaledBitmap(smallMarker,150,150,false);
            optImage.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            mMap.addMarker(optImage).showInfoWindow();
        }
    }

    // 사진 촬영 후 썸네일만 띄워줌. 이미지를 파일로 저장해야 함
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // 카메라 인텐트 실행하는 부분
    private void dispatchTakePictureIntent() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 카메라 기능 intent
        if(i.resolveActivity(getPackageManager()) != null) {
            File photoFile = null; // 사진파일 변수

            try { photoFile = createImageFile(); }
            catch (IOException ex) { }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.fighting.fileprovider", photoFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(i, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // 사진의 돌아간 각도를 계산하는 메서드 선언
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    // 이미지를 회전시키는 메서드 선언
    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    int successCount = 0;
    // 파이어베이스 업로드 함수
    private void storageUploader() {
        final String contents = my_contents;
        final int walktime = (int)(endTime-startTime)/1000;
        final double distance = sum_dist;
        final double pace = sum_dist/walktime;
        final Date startRun = strun;

        ArrayList<String> contentList = new ArrayList<>();

        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
        String date= sdf.format(endRun);

        if (walktime>=3&&distance>0&&pace>0&&pace<150) {
            for(int i=0;i<pathList.size();i++){
                contentList.add(pathList.get(i));
                StorageReference imgRef= firebaseStorage.getReference("uploads/"+user.getUid()+"/"+date+"/"+i+".png");
                StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", ""+(contentList.size()-1)).build();
                // Uri 객체를 통해 이미지 파일 업로드
                UploadTask uploadTask =imgRef.putFile(Uri.fromFile(new File(pathList.get(i))), metadata);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(Walk.this, "업로드 성공", Toast.LENGTH_SHORT).show();
//                        Log.d("로그", "업로드 성공");
                        final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                contentList.set(index, uri.toString());
                                successCount++;

                                if (pathList.size()==successCount) { // 이렇게 넣어야 저장소 위치로 미리 업로드 방지 가능.
                                    TraceInfo traceInfo = new TraceInfo(contents, walktime, distance, pace, contentList, startRun, endRun);
                                    Log.e("로그", "trace 성공");
                                    storeUploader(traceInfo);
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("로그", "업로드 실패");
                    }
                });
            }

            // 이미지 없을때도 db 업로드 되도록
            if(pathList.size()==0){
                TraceInfo traceInfo = new TraceInfo(contents, walktime, distance, pace, contentList, startRun, endRun);
                Log.e("로그", "trace 성공");
                storeUploader(traceInfo);
            }

            // 똑같은 기능인데 다른 코드일뿐
//            for(int i=0;i<pathList.size();i++){
//                final StorageReference imageRef = firebaseStorage.getReference("uploads/" + user.getUid() + "/"+date+"/"+i+".jpg");
//                try {
//                    InputStream stream = new FileInputStream(new File(pathList.get(i)));
//                    StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", ""+(pathList.size()-1)).build();
//                    UploadTask uploadTask = imageRef.putStream(stream, metadata);
//                    uploadTask.addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            }}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
//                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(Uri uri) {
//                                        Log.e("로그", "uri"+uri);
//                                        pathList.set(index, uri.toString());
//                                        }
//                                    });
//                                }
//                            });
//                    } catch (FileNotFoundException e) {
//                        Log.e("로그", "에러: " + e.toString());
//                    }
//                }

            //TraceInfo traceInfo = new TraceInfo(contents, walktime, distance, pace);
        }


    }

    private void storeUploader(TraceInfo traceInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
        String datedetail= sdf.format(endRun);
        db.collection("traces").document(user.getUid()).collection("date").document(datedetail).set(traceInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("산책정보 등록 성공");
//                        // 수정한 부분 -> 오류나서 종료됨
//                        WalkRecordView tf = (WalkRecordView) getSupportFragmentManager().findFragmentById(R.id.frame_record);
//                        assert tf != null;
//                        tf.traceUpdate(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("산책정보 등록 실패");
                        Log.e("로그", "산책정보 최종 등록 실패", e);
                    }
                });
    }

    private void startToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}
}