package hk.hku.eee.smartliving;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import static java.text.DateFormat.getTimeInstance;

public class MainActivity extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LineChartOnValueSelectListener
{
    private static final int TAG_CODE_PERMISSION_LOCATION = 1340;
    public static final String TAG_Check = "Check GooglePlay Ver";
    private static final String advice_reduce = "The general public is advised to reduce outdoor physical exertion, and to reduce the time of their stay outdoors, especially in areas with heavy traffic.";
    private static final String advice_restrict = "The general public is advised to restrict outdoor physical exertion, and to restrict the time of their stay outdoors, especially in areas with heavy traffic.";
    private static final String advice_avoid = "The general public is advised to avoid outdoor physical exertion, and to avoid the time of their stay outdoors, especially in areas with heavy traffic.";
    private static final int MaxRow = 44 + 1;
    private static final int MaxCol = 60 + 1;
    public static final int CONSTANT_MTR = 40;
    public static final int CONSTANT_BUS = 50;
    public static final int CONSTANT_OTHERS = 50;
    public static final double CONSTANT_ADDED_RISK = 1.144;
    public static final double CONSTANT_PM25 = 0.0002180567;
    public static final double CONSTANT_PM10 = 0.0002821751;
    public static final double CONSTANT_SO2 = 0.0001393235;
    public static final double CONSTANT_O3 = 0.0005116328;
    public static final double CONSTANT_NO2 = 0.0004462559;

    public Double[][] array1 = new Double[MaxRow][MaxCol];
    public Double[][] array2 = new Double[MaxRow][MaxCol];
    public Double[][] array3 = new Double[MaxRow][MaxCol];
    public Double[][] array4 = new Double[MaxRow][MaxCol];
    public Double[][] array5 = new Double[MaxRow][MaxCol];
    public boolean completed = false;
    public boolean map_ready = false;
    public boolean has_location = false;
    public List<PolygonOptions> polygonOpts = new ArrayList<>();
    public Polygon[] polygons = new Polygon[(MaxRow-1)*(MaxCol-1)];
    public Polyline[] routes = new Polyline[MaxRow];
    public List<PolylineOptions> routesOpts = new ArrayList<>();
    public int[] routes_color = new int[MaxRow];
    public int[] routes_AQI = new int[MaxRow];
    public int[] routes_duration = new int[MaxRow];
    public String[] routes_text = new String[MaxRow];
    public String[] routes_URL = new String[MaxRow];
    public String current_route_URL = "";

    // Google Map
    //private GoogleMap googleMap;
    //private CustomMapFragment mCustomMapFragment;
    private LatLng current_latlng;
    private LatLng previousLatLng = new LatLng(0, 0);
    private Integer prev_row = 0;
    private Integer prev_col = 0;
    private LocationManager locationManager;
    private Marker srcMarker;
    private Marker destMarker;

    private View mMarkerParentView;
    private ImageView mMarkerImageView;

    private int imageParentWidth = -1;
    private int imageParentHeight = -1;
    private int imageHeight = -1;
    private int centerX = -1;
    private int centerY = -1;

    //private EditText searchView1;
    private Button search_button;
    private Button clear_search_button;
    private Button drawer_button;
    private FloatingActionButton direction_button;
    private FloatingActionButton navigation_button;
    private String current_destText_value = "";
    private TextView target_name;
    private TextView target_LngLat;
    private TextView result_text;
    private ProgressBar spinner;

    private QuickTask quickTask;
    //private BackgroundTask bgTask;
    //private AddPolygonToMap drawTask;
    //private RouteTask routeTask;
    private boolean first_QuickTask = false;
    private boolean backgroundTask_failed = false;

    private Integer THRESHOLD = 2;
    //private DelayAutoCompleteTextView geo_autocomplete;

    private RelativeLayout Relative_Profile;
    private RelativeLayout Relative_MapView;
    private RelativeLayout Relative_Health;
    private RelativeLayout routeBox;
    //private DelayAutoCompleteTextView destText;
    private Button btnDestClear;
    private Button btnReverse;
    private LinearLayout resultbox_incl;

    private BottomBar mBottomBar;
    private @IdRes int prev_tabId = R.id.tab_map;
    private boolean in_Map_mode = true;
    private boolean in_Profile_mode = false;

    private FloatingActionButton refreshBtn;
    private boolean illness = false;
    private int user_age = 20;
    private int current_heartrate = 90;
    private int avg_heartrate = 90;
    private int AQHI_value = 0;
    private double ER_value = 0.0;
    private TextView aqhi_text;
    private TextView er_text;
    private TextView advice_text;
    private ImageView aqhi_icon;

    private CheckBox ill_edit;
    private CheckBox ill2_edit;
    private TextView dob_text;
    private Button dob_edit;
    private TextView user_name_text;
    private Button user_name_edit;
    private TextView email_text;
    private Button clear_profile;

    private GoogleApiClient mClient = null;
    public static final String TAG = "BasicHistoryApi";
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private static boolean authInProgress = false;
    private boolean connected_GoogleFit = false;

    private static final String PREF_LOGIN = "LOGIN_PREF";
    private static final String KEY_CREDENTIALS = "LOGIN_CREDENTIALS";
    private String history_name;
    private String history_email;
    private String history_dob;
    private Boolean history_illness1;
    private Boolean history_illness2;
    private String history_success_date;
    private String current_using_date;

    private FetchStepsAsync stepsTask;
    private LineChartView StepsChart;
    private TextView StepsDataDate;
    private TextView StepsDataValue;
    private TextView StepsAvgDate;
    private TextView StepsAvgValue;
    private RelativeLayout StepsViewClick;
    private LinearLayout StepsViewDetail;
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH);
    ArrayList<Integer> stepsArr = new ArrayList<>();
    long[] stepsDateArr = new long[7];
    long avg_steps = 0;

    private FetchHeartRateAsync HeartrateTask;
    private LineChartView HeartChart;
    private TextView HeartDataDate;
    private TextView HeartDataValue;
    private TextView HeartAvgDate;
    private TextView HeartAvgValue;
    private RelativeLayout HeartViewClick;
    private LinearLayout HeartViewDetail;
    ArrayList<Double> HeartrateArr = new ArrayList<>();
    long[] HeartrateDateArr = new long[7];

    private LinearLayout SurveyView;
    private ImageView HappyFace;
    private ImageView NeutralFace;
    private ImageView UnhappyFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG_Check, GooglePlayServicesUtil.getErrorString(status));
            // ask user to update google play services.
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            dialog.show();
        } else {
            Log.i(TAG_Check, GooglePlayServicesUtil.getErrorString(status));
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) ==
                    PackageManager.PERMISSION_GRANTED) {
                //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BODY_SENSORS}, TAG_CODE_PERMISSION_LOCATION);
            }
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.SENSORS_API)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.RECORDING_API)
                    .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            //new LocationControl().execute(this);

            registerReceiver(new NetworkChangeReceiver(),
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            SharedPreferences preferences = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE);
            if(preferences.contains(KEY_CREDENTIALS)){
                //set Name, Email, DoB (& age), illnesses
                history_name = preferences.getString("name", "");
                history_email = preferences.getString("email", "");
                history_dob = preferences.getString("dob", "");
                history_illness1 = preferences.getBoolean("illness1", false);
                history_illness2 = preferences.getBoolean("illness2", false);
                history_success_date = preferences.getString("success_date", "");
                // InitializeUI
                initializeUI();
            }else {
                //Registration Dialog
                showRegistrationDialog(MainActivity.this);
                //history_success_date = CONSTANT_SUCCESS_DATE;
            }
        }
    }

    private void initializeUI() {

        Log.i("Initialize UI", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        mMarkerParentView = findViewById(R.id.marker_view_incl);
        mMarkerImageView = (ImageView) findViewById(R.id.marker_icon_view);
        resultbox_incl = (LinearLayout) findViewById(R.id.resultbox_incl);

        Relative_MapView = (RelativeLayout) findViewById(R.id.map_view);
        Relative_Profile = (RelativeLayout) findViewById(R.id.profile_view);
        Relative_Health = (RelativeLayout) findViewById(R.id.health_view);
        routeBox = (RelativeLayout) findViewById(R.id.routeBox);
        //destText = (DelayAutoCompleteTextView) findViewById(R.id.routeDest);
        btnDestClear = (Button) findViewById(R.id.clearDest_btn);
        btnReverse = (Button) findViewById(R.id.reverse_btn);

        target_name = (TextView) findViewById(R.id.target_name);
        target_LngLat = (TextView) findViewById(R.id.target_LngLat);
        result_text = (TextView) findViewById(R.id.result_text);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        clear_search_button = (Button) findViewById(R.id.clearSrc_btn);
        search_button = (Button) findViewById(R.id.search_button);

        refreshBtn = (FloatingActionButton) findViewById(R.id.fab);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected_GoogleFit) {
                    if (stepsTask != null && stepsTask.getStatus() != AsyncTask.Status.FINISHED)
                        stepsTask.cancel(true);
                    stepsTask = new FetchStepsAsync();
                    stepsTask.execute();
                    if (HeartrateTask != null && HeartrateTask.getStatus() != AsyncTask.Status.FINISHED)
                        HeartrateTask.cancel(true);
                    HeartrateTask = new FetchHeartRateAsync();
                    HeartrateTask.execute();
                }
            }
        });

        aqhi_text = (TextView) findViewById(R.id.aqhi_text);
        er_text = (TextView) findViewById(R.id.er_text);
        advice_text = (TextView) findViewById(R.id.advice_text);
        aqhi_icon = (ImageView) findViewById(R.id.aqhi_icon);
        ill_edit = (CheckBox) findViewById(R.id.ill_edit);
        ill_edit.setChecked(history_illness1);
        ill2_edit = (CheckBox) findViewById(R.id.ill2_edit);
        ill2_edit.setChecked(history_illness2);
        dob_text = (TextView) findViewById(R.id.dob_text);
        dob_text.setText(history_dob);
        dob_edit = (Button) findViewById(R.id.dob_edit);
        user_name_text = (TextView) findViewById(R.id.user_name_text);
        user_name_text.setText(history_name);
        user_name_edit = (Button) findViewById(R.id.user_name_edit);
        email_text = (TextView) findViewById(R.id.email_text);
        email_text.setText(history_email);
        clear_profile = (Button) findViewById(R.id.clear_profile);
        clear_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to clear your Profile?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences preferences = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                                user_name_text.setText("User");
                                email_text.setText("");
                                dob_text.setText("01 / 01 / 2017");
                                ill_edit.setChecked(false);
                                ill_edit.setChecked(false);
                                finish();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        ill_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                illness = isChecked | ill2_edit.isChecked();
            }
        });
        ill2_edit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                illness = isChecked | ill_edit.isChecked();
            }
        });
        dob_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogfragment = new DatePickerDialogTheme();
                dialogfragment.show(getFragmentManager(), "Choose your Date of Birth");
            }
        });
        user_name_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Set Profile Name");
                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                user_name_text.setText(input.getText().toString());
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
        illness = history_illness1 | history_illness2;
        Calendar c = Calendar.getInstance();
        Integer yyyy = c.get(Calendar.YEAR);
        user_age = yyyy - Integer.parseInt(dob_text.getText().toString().substring(dob_text.length()-4, dob_text.length()));

        StepsChart = (LineChartView) findViewById(R.id.steps_chart);
        StepsDataDate = (TextView) findViewById(R.id.steps_data_title);
        StepsDataValue = (TextView) findViewById(R.id.steps_data_txt);
        StepsAvgDate = (TextView) findViewById(R.id.steps_subTitle);
        StepsAvgValue = (TextView) findViewById(R.id.steps_avg_txt);
        StepsViewClick = (RelativeLayout) findViewById(R.id.steps_view_click);
        StepsViewDetail = (LinearLayout) findViewById(R.id.steps_detail_view);
        HeartChart = (LineChartView) findViewById(R.id.hr_chart);
        HeartDataDate  = (TextView) findViewById(R.id.hr_data_title);
        HeartDataValue = (TextView) findViewById(R.id.hr_data_txt);
        HeartAvgDate = (TextView) findViewById(R.id.hr_subTitle);
        HeartAvgValue = (TextView) findViewById(R.id.hr_avg_txt);
        HeartViewClick = (RelativeLayout) findViewById(R.id.hr_view_click);
        HeartViewDetail = (LinearLayout) findViewById(R.id.hr_detail_view);

        StepsViewClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StepsViewDetail.getVisibility() == View.VISIBLE){
                    StepsViewDetail.setVisibility(View.GONE);
                } else {
                    StepsViewDetail.setVisibility(View.VISIBLE);
                }
            }
        });
        HeartViewClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HeartViewDetail.getVisibility() == View.VISIBLE){
                    HeartViewDetail.setVisibility(View.GONE);
                } else {
                    HeartViewDetail.setVisibility(View.VISIBLE);
                }
            }
        });

        SurveyView = (LinearLayout) findViewById(R.id.survey_view);
        HappyFace = (ImageView) findViewById(R.id.happy_face);
        NeutralFace = (ImageView) findViewById(R.id.normal_face);
        UnhappyFace = (ImageView) findViewById(R.id.unhappy_face);

        HappyFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // POST result
                String user_email = email_text.getText().toString();
                String user_year = dob_text.getText().toString().substring(dob_text.length()-4, dob_text.length());
                String user_steps = null;
                if (connected_GoogleFit) {
                    user_steps = String.format("%d", stepsArr.get(6));
                }
                String user_perception = "1";
                String user_hrate = null;
                if (connected_GoogleFit) {
                    user_hrate = String.format("%.1f", HeartrateArr.get(6));
                }
                String user_disease = (illness?"1":"0");
                String user_lat =  String.format("%.5f", previousLatLng.latitude);
                String user_lon = String.format("%.5f", previousLatLng.longitude);
                SurveyView.setVisibility(View.GONE);
            }
        });
        NeutralFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // POST result
                String user_email = email_text.getText().toString();
                String user_year = dob_text.getText().toString().substring(dob_text.length()-4, dob_text.length());
                String user_steps = null;
                if (connected_GoogleFit) {
                    user_steps = String.format("%d", stepsArr.get(6));
                }
                String user_perception = "2";
                String user_hrate = null;
                if (connected_GoogleFit) {
                    user_hrate = String.format("%.1f", HeartrateArr.get(6));
                }
                String user_disease = (illness?"1":"0");
                String user_lat =  String.format("%.5f", previousLatLng.latitude);
                String user_lon = String.format("%.5f", previousLatLng.longitude);
                SurveyView.setVisibility(View.GONE);
            }
        });
        UnhappyFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // POST result
                String user_email = email_text.getText().toString();
                String user_year = dob_text.getText().toString().substring(dob_text.length()-4, dob_text.length());
                String user_steps = null;
                if (connected_GoogleFit) {
                    user_steps = String.format("%d", stepsArr.get(6));
                }
                String user_perception = "3";
                String user_hrate = null;
                if (connected_GoogleFit) {
                    user_hrate = String.format("%.1f", HeartrateArr.get(6));
                }
                String user_disease = (illness?"1":"0");
                String user_lat =  String.format("%.5f", previousLatLng.latitude);
                String user_lon = String.format("%.5f", previousLatLng.longitude);
                SurveyView.setVisibility(View.GONE);
            }
        });

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        navigation_button = (FloatingActionButton) findViewById(R.id.navigation_button);
        direction_button = (FloatingActionButton) findViewById(R.id.direction_button);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_map) {
                    //action on Map pressed
                    if (tabId == prev_tabId){
                        return;
                    }
                    Calendar c = Calendar.getInstance();
                    Integer yyyy = c.get(Calendar.YEAR);
                    user_age = yyyy - Integer.parseInt(dob_text.getText().toString().substring(dob_text.length()-4, dob_text.length()));
                    prev_tabId = R.id.tab_map;
                    if (Relative_Profile.getVisibility() == View.VISIBLE) {
                        Relative_Profile.setVisibility(View.GONE);
                    }
                    if (Relative_Health.getVisibility() == View.VISIBLE) {
                        Relative_Health.setVisibility(View.GONE);
                    }
                    if (Relative_MapView.getVisibility()!=View.VISIBLE) {
                        Relative_MapView.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < routes.length; i++) {
                        if (routes[i] != null) {
                            routes[i].remove();
                        }
                    }
                    if (srcMarker != null) {
                        srcMarker.remove();
                    }
                    if (destMarker != null) {
                        destMarker.remove();
                    }
                    //mCustomMapFragment.setOnDragListener(MainActivity.this);
                    mMarkerImageView.setVisibility(View.VISIBLE);
                    target_name.setVisibility(View.VISIBLE);
                    routeBox.setVisibility(View.INVISIBLE);
                    resultbox_incl.setVisibility(View.VISIBLE);
                    if (!in_Map_mode | in_Profile_mode) {
                        //geo_autocomplete.setText("");
                        //geo_autocomplete.setHint("Search Location");
                        result_text.setText("");
                        //destText.setText("");
                        previousLatLng = new LatLng(0, 0);
                        prev_col = -1;
                        prev_row = -1;
                        if (has_location) {
                            updateLocation(current_latlng);
                            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(current_latlng));
                            //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current_latlng, 14));
                        }
                    }
                    in_Map_mode = true;
                    in_Profile_mode = false;

                } else if (tabId == R.id.tab_route) {
                    // action on Direction pressed
                    if (tabId == prev_tabId){
                        return;
                    }
                    if (prev_tabId != R.id.tab_map){
                        //if (geo_autocomplete.getText().toString().equals("") || geo_autocomplete.getText() == null){
                        //    geo_autocomplete.setText("Your Location");
                        //}
                        Calendar c = Calendar.getInstance();
                        Integer yyyy = c.get(Calendar.YEAR);
                        user_age = yyyy - Integer.parseInt(dob_text.getText().toString().substring(dob_text.length()-4, dob_text.length()));
                    }
                    prev_tabId = R.id.tab_route;
                    if (Relative_Profile.getVisibility() == View.VISIBLE) {
                        Relative_Profile.setVisibility(View.GONE);
                    }
                    if (Relative_Health.getVisibility() == View.VISIBLE) {
                        Relative_Health.setVisibility(View.GONE);
                    }
                    if (Relative_MapView.getVisibility()!=View.VISIBLE) {
                        Relative_MapView.setVisibility(View.VISIBLE);
                    }
                    //mCustomMapFragment.setOnDragListener(null); //temp disable onDragListener
                    mMarkerImageView.setVisibility(View.INVISIBLE);
                    target_name.setVisibility(View.GONE);
                    if (in_Map_mode) {
                        result_text.setText("");
                        //destText.setText("");
                        //geo_autocomplete.setText("Your Location");
                        navigation_button.setVisibility(View.INVISIBLE);
                    }
                    in_Map_mode = false;
                    in_Profile_mode = false;
                    //geo_autocomplete.setHint("From..");
                    //resultbox_incl.setVisibility(View.INVISIBLE);
                    routeBox.setVisibility(View.VISIBLE);
                    direction_button.setVisibility(View.INVISIBLE);
                    //navigation_button.setVisibility(View.VISIBLE);

                } else if (tabId == R.id.tab_health) {
                    // Action on Health pressed
                    if (tabId == prev_tabId){
                        return;
                    }
                    prev_tabId = R.id.tab_health;
                    Calendar c = Calendar.getInstance();
                    Integer yyyy = c.get(Calendar.YEAR);
                    user_age = yyyy - Integer.parseInt(dob_text.getText().toString().substring(dob_text.length()-4, dob_text.length()));
                    if (Relative_MapView.getVisibility()==View.VISIBLE) {
                        Relative_MapView.setVisibility(View.GONE);
                    }
                    if (Relative_Profile.getVisibility() == View.VISIBLE) {
                        Relative_Profile.setVisibility(View.GONE);
                    }
                    if (Relative_Health.getVisibility() != View.VISIBLE) {
                        Relative_Health.setVisibility(View.VISIBLE);
                    }
                    in_Profile_mode = false;
                    //Toast.makeText(getApplicationContext(), "selected Item is Health", Toast.LENGTH_SHORT).show();

                } else if (tabId == R.id.tab_profile) {
                    // action on Profile pressed
                    if (tabId == prev_tabId){
                        return;
                    }
                    prev_tabId = R.id.tab_profile;
                    if (Relative_MapView.getVisibility()==View.VISIBLE) {
                        Relative_MapView.setVisibility(View.GONE);
                    }
                    if (Relative_Health.getVisibility() == View.VISIBLE) {
                        Relative_Health.setVisibility(View.GONE);
                    }
                    if (Relative_Profile.getVisibility() != View.VISIBLE) {
                        Relative_Profile.setVisibility(View.VISIBLE);
                    }
                    in_Profile_mode = true;
                    //Toast.makeText(getApplicationContext(), "selected Item is Profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setCurrentLocation(Location location) {
        current_latlng = new LatLng(location.getLatitude(), location.getLongitude()); // This methods gets the users current longitude and latitude
    }

    public String getBestProvider() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        return bestProvider;
    }

    @Override
    protected void onStart(){
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Google API connected");
        connected_GoogleFit = true;
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    }
                });
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_HEART_RATE_BPM)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    }
                });
        if (stepsTask != null && stepsTask.getStatus() != AsyncTask.Status.FINISHED)
            stepsTask.cancel(true);
        stepsTask = new FetchStepsAsync();
        stepsTask.execute();
        if (HeartrateTask != null && HeartrateTask.getStatus() != AsyncTask.Status.FINISHED)
            HeartrateTask.cancel(true);
        HeartrateTask = new FetchHeartRateAsync();
        HeartrateTask.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i(TAG, "Google Fit Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i(TAG, "Google Fit Connection lost.  Reason: Service Disconnected");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if( !authInProgress ) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( MainActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {
                Log.e( "GoogleFit", "auth ERROR!" );
            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        has_location = true;
        Log.i("Location Found", "haslocation = true");
        setCurrentLocation(newLocation);
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (user_name_text != null) {
            editor.putString("name", user_name_text.getText().toString());
        }
        if (dob_text != null) {
            editor.putString("dob", dob_text.getText().toString());
        }
        if (ill_edit != null) {
            editor.putBoolean("illness1", ill_edit.isChecked());
        }
        if (ill2_edit != null) {
            editor.putBoolean("illness2", ill2_edit.isChecked());
        }
        editor.putString("success_date", history_success_date);
        editor.commit();
        if (quickTask != null && quickTask.getStatus() != AsyncTask.Status.FINISHED)
            quickTask.cancel(true);
    }

    private void updateLocation(LatLng centerLatLng) {
        if (first_QuickTask && quickTask != null && quickTask.getStatus() != AsyncTask.Status.FINISHED)
            quickTask.cancel(true);
        Integer row = (int) (((22.57 - centerLatLng.latitude) / 0.01) + 1);
        Integer col = (int) (((centerLatLng.longitude - 113.81) / 0.01) + 1);
        if (row > 0 && row < MaxRow && col > 0 && col < MaxCol) {
            if (prev_row != row || prev_col != col) {
                prev_row = row;
                prev_col = col;
                if (!completed) {
                        quickTask = new QuickTask();
                        quickTask.execute(row.toString(), col.toString());
                } else {
                    //fetch data from local
                    if (spinner.getVisibility() == View.VISIBLE){
                        spinner.setVisibility(View.GONE);
                    }
                    String final_result = "";
                    if (array1[row][col] != null)
                        final_result += "PM2.5 : \t" + String.format("%.4f", array1[row][col]) + "\n";
                    if (array2[row][col] != null)
                        final_result += "PM10 : \t" + String.format("%.4f", array2[row][col]) + "\n";
                    if (array3[row][col] != null)
                        final_result += "NO2 : \t\t\t" + String.format("%.4f", array3[row][col]) + "\n";
                    if (array4[row][col] != null)
                        final_result += "O3 : \t\t\t\t" + String.format("%.4f", array4[row][col]) + "\n";
                    if (array5[row][col] != null)
                        final_result += "SO2 : \t\t\t" + String.format("%.4f", array5[row][col]);
                    if (array1[row][col] != null && array2[row][col] != null && array3[row][col] != null && array4[row][col] != null && array5[row][col] != null ) {
                        ER_value = (Math.max(Math.expm1(array1[row][col] * CONSTANT_PM25), Math.expm1(array2[row][col] * CONSTANT_PM10)) + Math.expm1(array3[row][col] * CONSTANT_NO2) + Math.expm1(array4[row][col] * CONSTANT_O3) + Math.expm1(array5[row][col] * CONSTANT_SO2)) * 100;
                        Log.i("ER_value : ", Double.toString(ER_value));
                        if (illness) {
                            ER_value = ER_value * CONSTANT_ADDED_RISK;
                            Log.i("Illness const: ", Double.toString(CONSTANT_ADDED_RISK));
                        }
                        //Log.i("ER_value aft illness: ", Double.toString(ER_value));
                        Log.i("User Age: ", Integer.toString(user_age));
                        if (user_age < 5 || user_age > 65) {
                            ER_value = ER_value * CONSTANT_ADDED_RISK;
                            Log.i("Age const: ", Double.toString(CONSTANT_ADDED_RISK));
                        } else if (user_age < 21) {
                            ER_value = ER_value * (CONSTANT_ADDED_RISK - ((user_age - 5) * 0.144 / 15.0));
                            Log.i("Age const: ", Double.toString((CONSTANT_ADDED_RISK - ((user_age - 5) * 0.144 / 15.0))));
                        } else if (user_age > 39) {
                            ER_value = ER_value * (CONSTANT_ADDED_RISK - ((65 - user_age) * 0.144 / 25.0));
                            Log.i("Age const: ", Double.toString((CONSTANT_ADDED_RISK - ((65 - user_age) * 0.144 / 25.0))));
                        }
                        //Log.i("ER_value aft age: ", Double.toString(ER_value));
                        if (current_heartrate != 0) {
                            ER_value = ER_value * (double) current_heartrate / (double) avg_heartrate;
                            Log.i("HR const: ", Double.toString((double) current_heartrate / (double) avg_heartrate));
                        }
                        //Log.i("ER_value aft HR: ", Double.toString(ER_value));
                        Log.i("Personal ER_value : ", Double.toString(ER_value));
                        er_text.setText(String.format("%.2f", ER_value));
                        if (ER_value < 1.88) {
                            AQHI_value = 1;
                        } else if (ER_value < 3.76) {
                            AQHI_value = 2;
                        } else if (ER_value < 5.64) {
                            AQHI_value = 3;
                        } else if (ER_value < 7.52) {
                            AQHI_value = 4;
                        } else if (ER_value < 9.41) {
                            AQHI_value = 5;
                        } else if (ER_value < 11.29) {
                            AQHI_value = 6;
                        } else if (ER_value < 12.91) {
                            AQHI_value = 7;
                        } else if (ER_value < 15.07) {
                            AQHI_value = 8;
                        } else if (ER_value < 17.22) {
                            AQHI_value = 9;
                        } else if (ER_value < 19.37) {
                            AQHI_value = 10;
                        } else if (ER_value > 19.37) {
                            AQHI_value = 11;
                        } else {
                            AQHI_value = 0;
                        }
                        final_result += "\n\nPersonal AQHI : \t" + Integer.toString(AQHI_value);
                        if (AQHI_value < 4) {
                            final_result += "  ( Low )";
                            advice_text.setText("");
                            advice_text.setVisibility(View.GONE);
                            aqhi_icon.setImageResource(R.drawable.aqhi_low);
                        } else if (AQHI_value < 7) {
                            final_result += "  ( Moderate )";
                            advice_text.setText("");
                            advice_text.setVisibility(View.GONE);
                            aqhi_icon.setImageResource(R.drawable.aqhi_moderate);
                        } else if (AQHI_value < 8) {
                            final_result += "  ( High )";
                            advice_text.setText(advice_reduce);
                            advice_text.setVisibility(View.VISIBLE);
                            aqhi_icon.setImageResource(R.drawable.aqhi_high);
                        } else if (AQHI_value < 11) {
                            final_result += "  ( Very High )";
                            advice_text.setText(advice_restrict);
                            advice_text.setVisibility(View.VISIBLE);
                            aqhi_icon.setImageResource(R.drawable.aqhi_very_high);
                        } else if (AQHI_value < 12) {
                            final_result += "  ( Serious )";
                            advice_text.setText(advice_avoid);
                            advice_text.setVisibility(View.VISIBLE);
                            aqhi_icon.setImageResource(R.drawable.aqhi_serious);
                        }
                        aqhi_text.setText(Integer.toString(AQHI_value));
                    }
                    Log.i("Local Data Row : ", row.toString());
                    Log.i("Local Data Col : ", col.toString());
                    Log.i("Local Data : ", final_result);
                    if (routeBox.getVisibility() == View.INVISIBLE) {
                        result_text.setText(final_result);
                    }
                    if (result_text.getVisibility() == View.GONE){
                        result_text.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        if (centerLatLng != null) {
            Locale locale = new Locale("en", "HK");
            Geocoder geocoder = new Geocoder(MainActivity.this, locale);

            List<Address> addresses = new ArrayList<Address>();
            try {
                addresses = geocoder.getFromLocation(centerLatLng.latitude, centerLatLng.longitude, 1);
                target_LngLat.setText("Latitude : " + centerLatLng.latitude + "\nLongitude : " + centerLatLng.longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                String addressIndex0 = (addresses.get(0).getAddressLine(0) != null) ? addresses
                        .get(0).getAddressLine(0) : null;
                String addressIndex1 = (addresses.get(0).getAddressLine(1) != null) ? addresses
                        .get(0).getAddressLine(1) : null;
                String addressIndex2 = (addresses.get(0).getAddressLine(2) != null) ? addresses
                        .get(0).getAddressLine(2) : null;
                String addressIndex3 = (addresses.get(0).getAddressLine(3) != null) ? addresses
                        .get(0).getAddressLine(3) : null;
                String completeAddress = addressIndex0;

                if (addressIndex1 != null) {
                    completeAddress += ", " + addressIndex1;
                }
                if (addressIndex2 != null) {
                    completeAddress += ", " + addressIndex2;
                }
                if (addressIndex3 != null) {
                    completeAddress += ", " + addressIndex3;
                }
                if (completeAddress != null) {
                    target_name.setText(completeAddress);
                    current_destText_value = completeAddress;
                    if (routeBox.getVisibility() == View.INVISIBLE) {
                        direction_button.setVisibility(View.VISIBLE);
                        navigation_button.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    public class QuickTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            // TODO Auto-generated method stub
            String final_result = "";
            if (s != null) {
                Log.i("Quick Result : ", s);
                try {
                    double temp1 = -1, temp2 = -1, temp3 = -1, temp4 = -1, temp5 = -1;
                    boolean valid = false;
                    JSONObject obj = new JSONObject(s);
                    Iterator iterator = obj.keys();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        JSONObject issue = obj.getJSONObject(key);
                        if (issue.has("PM2.5")) {
                            temp1 = Double.parseDouble(issue.optString("PM2.5"));
                            valid = true;
                        } else if (valid){
                            temp1 = 0.0;
                        }
                        final_result += "PM2.5 : \t" + String.format("%.4f", temp1) + "\n";
                        if (issue.has("PM10")) {
                            temp2 = Double.parseDouble(issue.optString("PM10"));
                            valid = true;
                        } else if (valid){
                            temp2 = 0.0;
                        }
                        final_result += "PM10 : \t" + String.format("%.4f", temp2) + "\n";
                        if (issue.has("NO2")) {
                            temp3 = Double.parseDouble(issue.optString("NO2"));
                            valid = true;
                        } else if (valid){
                            temp3 = 0.0;
                        }
                        final_result += "NO2 : \t\t\t" + String.format("%.4f", temp3) + "\n";
                        if (issue.has("O3")) {
                            temp4 = Double.parseDouble(issue.optString("O3"));
                            valid = true;
                        } else if (valid){
                            temp4 = 0.0;
                        }
                        final_result += "O3 : \t\t\t\t" + String.format("%.4f", temp4) + "\n";
                        if (issue.has("SO2")) {
                            temp5 = Double.parseDouble(issue.optString("SO2"));
                            valid = true;
                        } else if (valid){
                            temp5 = 0.0;
                        }
                        final_result += "SO2 : \t\t\t" + String.format("%.4f", temp5);
                    }
                    if (temp1 != -1 && temp2 != -1 && temp3 != -1 && temp4 != -1 && temp5 != -1 ) {
                        ER_value = (Math.max(Math.expm1(temp1 * CONSTANT_PM25), Math.expm1(temp2 * CONSTANT_PM10)) + Math.expm1(temp3 * CONSTANT_NO2) + Math.expm1(temp4 * CONSTANT_O3) + Math.expm1(temp5 * CONSTANT_SO2)) * 100;
                        Log.i("ER_value : ", Double.toString(ER_value));
                        if (illness) {
                            ER_value = ER_value * CONSTANT_ADDED_RISK;
                        }
                        Log.i("ER_value aft illness: ", Double.toString(ER_value));
                        if (user_age < 5 || user_age > 65) {
                            ER_value = ER_value * CONSTANT_ADDED_RISK;
                        } else if (user_age < 21) {
                            ER_value = ER_value * (CONSTANT_ADDED_RISK - ((user_age - 5) * (1 - CONSTANT_ADDED_RISK) / 15));
                        } else if (user_age > 39) {
                            ER_value = ER_value * (CONSTANT_ADDED_RISK - ((65 - user_age) * (1 - CONSTANT_ADDED_RISK) / 25));
                        }
                        Log.i("ER_value aft age: ", Double.toString(ER_value));
                        if (current_heartrate != 0) {
                            ER_value = ER_value * (double) current_heartrate / (double) avg_heartrate;
                        }
                        Log.i("ER_value aft HR: ", Double.toString(ER_value));
                        Log.i("Personal ER_value : ", Double.toString(ER_value));
                        er_text.setText(String.format("%.2f", ER_value));
                        if (ER_value < 1.88) {
                            AQHI_value = 1;
                        } else if (ER_value < 3.76) {
                            AQHI_value = 2;
                        } else if (ER_value < 5.64) {
                            AQHI_value = 3;
                        } else if (ER_value < 7.52) {
                            AQHI_value = 4;
                        } else if (ER_value < 9.41) {
                            AQHI_value = 5;
                        } else if (ER_value < 11.29) {
                            AQHI_value = 6;
                        } else if (ER_value < 12.91) {
                            AQHI_value = 7;
                        } else if (ER_value < 15.07) {
                            AQHI_value = 8;
                        } else if (ER_value < 17.22) {
                            AQHI_value = 9;
                        } else if (ER_value < 19.37) {
                            AQHI_value = 10;
                        } else if (ER_value > 19.37) {
                            AQHI_value = 11;
                        } else {
                            AQHI_value = 0;
                        }
                        final_result += "\n\nPersonal AQHI : \t" + Integer.toString(AQHI_value);
                        if (AQHI_value < 4) {
                            final_result += "  ( Low )";
                            advice_text.setText("");
                            aqhi_icon.setImageResource(R.drawable.aqhi_low);
                        } else if (AQHI_value < 7) {
                            final_result += "  ( Moderate )";
                            advice_text.setText("");
                            aqhi_icon.setImageResource(R.drawable.aqhi_moderate);
                        } else if (AQHI_value < 8) {
                            final_result += "  ( High )";
                            advice_text.setText(advice_reduce);
                            aqhi_icon.setImageResource(R.drawable.aqhi_high);
                        } else if (AQHI_value < 11) {
                            final_result += "  ( Very High )";
                            advice_text.setText(advice_restrict);
                            aqhi_icon.setImageResource(R.drawable.aqhi_very_high);
                        } else if (AQHI_value < 12) {
                            final_result += "  ( Serious )";
                            advice_text.setText(advice_avoid);
                            aqhi_icon.setImageResource(R.drawable.aqhi_serious);
                        }
                        aqhi_text.setText(Integer.toString(AQHI_value));
                    }
                } catch (Throwable t) {
                    Log.e("PostExecute Error : ", "cannot parse malformed JSON");
                }
            }
            result_text.setText(final_result);
            spinner.setVisibility(View.GONE);
            result_text.setVisibility(View.VISIBLE);
            first_QuickTask = true;
        }

        @Override
        protected void onPreExecute() {
            result_text.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            while (!isNetworkAvailable() ) {
                if (isCancelled()) return null;
                try {
                    Thread.currentThread();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;
            if (isCancelled()) return null;

            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR_OF_DAY, -1);        //suppose no need this line
            Integer test_minute = c.get(Calendar.MINUTE);
            if (test_minute < 30){                       //suppose this is 2 mins only
                c.add(Calendar.HOUR_OF_DAY, -1);
            }
            Integer yyyy = c.get(Calendar.YEAR);
            Integer mm = c.get(Calendar.MONTH);
            Integer dd = c.get(Calendar.DAY_OF_MONTH);
            Integer hh = c.get(Calendar.HOUR_OF_DAY);

            String date = String.format("%04d", yyyy) + "-" + String.format("%02d", mm+1) + "-" + String.format("%02d", dd) + "-" + String.format("%02d", hh);

            String target_URL = "http://www.hkair.eee.hku.hk/csv-to-api/?source=http://www.hkair.eee.hku.hk/data/" + date + ".csv&Row(44)=" + params[0] + "&Column(60)=" + params[1];

            Log.i("target_URL : ", target_URL);

            try {
                URL url = new URL(target_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null || isCancelled()) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null && isCancelled() == false) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                jsonStr = buffer.toString();
                return jsonStr;

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if(!isNetworkAvailable())
            {
                this.dialog.setMessage("No network connection...");
                this.dialog.setCancelable(false);
                this.dialog.show();
            } else {
                if(this.dialog.isShowing())
                {
                    this.dialog.dismiss();
                }
            }
        }
    }

    public static class DatePickerDialogTheme extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT,this,year,month,day);
            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            TextView textview = (TextView)getActivity().findViewById(R.id.dob_text);
            textview.setText(String.format("%02d", day) + " / " + String.format("%02d", (month+1)) + " / " + String.format("%04d", year));
        }
    }

    private class FetchStepsAsync extends AsyncTask<Object, Object, DataReadResult> {
        protected DataReadResult doInBackground(Object... params) {
            Calendar c = Calendar.getInstance();
            Calendar cal = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            long endTime = cal.getTimeInMillis();
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            long startTime = cal.getTimeInMillis();
            Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
            Log.i(TAG, "Range End: " + dateFormat.format(endTime));
            for (int x = 0; x<stepsDateArr.length; x++){
                cal.add(Calendar.DAY_OF_MONTH, 1);
                stepsDateArr[x] = cal.getTimeInMillis();
            }
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();
            DataReadResult dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            return dataReadResult;
        }

        @Override
        protected void onPostExecute(DataReadResult dataReadResult) {
            super.onPostExecute(dataReadResult);
            stepsArr.clear();
            ArrayList<BarEntry> entries = new ArrayList<>();
            int temp_date_index = 0;
            if (dataReadResult.getBuckets().size() > 0) {       //Used for aggregated data
                Log.e(TAG, "Number of buckets: " + dataReadResult.getBuckets().size());
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet stepData : dataSets) {
                        Log.i(TAG, "Data returned for Data type: " + stepData.getDataType().getName());
                        Log.i(TAG, "Data Size: " + stepData.getDataPoints().size());
                        if (stepData.getDataPoints().size() == 0){
                            stepsArr.add(0);
                            temp_date_index++;
                        } else {
                            for (DataPoint dp : stepData.getDataPoints()) {
                                Log.i(TAG, "Data point:");
                                Log.i(TAG, "\tType: " + dp.getDataType().getName());
                                Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                                Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                                for (Field field : dp.getDataType().getFields()) {
                                    Log.i(TAG, "\tField: " + field.getName() +
                                            " Value: " + dp.getValue(field));
                                    stepsArr.add(Integer.parseInt(dp.getValue(field).toString()));
                                    stepsDateArr[temp_date_index] = dp.getEndTime(TimeUnit.MILLISECONDS);
                                    temp_date_index++;
                                }
                            }
                        }
                    }
                }
            }
            else if (dataReadResult.getDataSets().size() > 0) {     //Used for non-aggregated data
                Log.e(TAG, "Number of returned DataSets: " + dataReadResult.getDataSets().size());
                for (DataSet stepData : dataReadResult.getDataSets()) {
                    Log.i(TAG, "Data returned for Data type: " + stepData.getDataType().getName());
                    DateFormat dateFormat = getTimeInstance();
                    Log.i(TAG, "Data Size: " + stepData.getDataPoints().size());
                    if (stepData.getDataPoints().size() == 0){
                        stepsArr.add(0);
                        temp_date_index++;
                    } else {
                        for (DataPoint dp : stepData.getDataPoints()) {
                            Log.i(TAG, "Data point:");
                            Log.i(TAG, "\tType: " + dp.getDataType().getName());
                            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                            for (Field field : dp.getDataType().getFields()) {
                                Log.i(TAG, "\tField: " + field.getName() +
                                        " Value: " + dp.getValue(field));
                                stepsArr.add(Integer.parseInt(dp.getValue(field).toString()));
                                stepsDateArr[temp_date_index] = dp.getEndTime(TimeUnit.MILLISECONDS);
                                temp_date_index++;
                            }
                        }
                    }
                }
            }

            List<PointValue> values = new ArrayList<PointValue>();
            List<PointValue> avg_values = new ArrayList<PointValue>();
            int index = 0;
            long total_steps = 0;
            int count = 0;
            for (Integer test : stepsArr){
                Log.i(TAG, "Date: " + dateFormat.format(stepsDateArr[index]));
                Log.i(TAG, test.toString());
                values.add(new PointValue(index, test));
                total_steps += test;
                if (test != 0){
                    count++;
                }
                index++;
            }
            if (count == 0){
                count = 1;
            }
            avg_steps = total_steps / count;
            StepsAvgValue.setText(String.format("%d",avg_steps));
            StepsAvgDate.setText(dateFormat.format(stepsDateArr[6]));
            StepsDataDate.setText(dateFormat.format(stepsDateArr[6]));
            StepsDataValue.setText(String.format("%d", stepsArr.get(6)));

            //In most cased you can call data model methods in builder-pattern-like manner.
            Line line = new Line(values).setColor(0xFFFF4081).setCubic(false).setPointRadius(5).setStrokeWidth(3);
            List<Line> lines = new ArrayList<Line>();
            lines.add(line);
            //add avg Line
            avg_values.add(new PointValue(0, 0));
            avg_values.add(new PointValue(1, 0));
            avg_values.add(new PointValue(2, 0));
            avg_values.add(new PointValue(3, 0));
            avg_values.add(new PointValue(4, 0));
            avg_values.add(new PointValue(5, 0));
            avg_values.add(new PointValue(6, 0));
            Line avg_line = new Line(avg_values).setColor(0xFFFFAE19).setCubic(false).setPointRadius(0).setStrokeWidth(1);
            lines.add(avg_line);

            LineChartData data = new LineChartData();
            data.setLines(lines);

            StepsChart.setLineChartData(data);
            StepsChart.setOnValueTouchListener(MainActivity.this);
            prepareDataAnimation(data);
            StepsChart.startDataAnimation();
            StepsChart.setZoomEnabled(false);
        }
    }

    private class FetchHeartRateAsync extends AsyncTask<Object, Object, DataReadResult> {
        protected DataReadResult doInBackground(Object... params) {
            Calendar c = Calendar.getInstance();
            Calendar cal = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
            long endTime = cal.getTimeInMillis();
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            long startTime = cal.getTimeInMillis();
            Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
            Log.i(TAG, "Range End: " + dateFormat.format(endTime));
            for (int x = 0; x<HeartrateDateArr.length; x++){
                cal.add(Calendar.DAY_OF_MONTH, 1);
                HeartrateDateArr[x] = cal.getTimeInMillis();
            }
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();
            DataReadResult dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            return dataReadResult;
        }

        @Override
        protected void onPostExecute(DataReadResult dataReadResult) {
            super.onPostExecute(dataReadResult);
            HeartrateArr.clear();
            ArrayList<BarEntry> entries = new ArrayList<>();
            int temp_date_index = 0;
            if (dataReadResult.getBuckets().size() > 0) {       //Used for aggregated data
                Log.e(TAG, "Number of buckets: " + dataReadResult.getBuckets().size());
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet stepData : dataSets) {
                        Log.i(TAG, "Data returned for Data type: " + stepData.getDataType().getName());
                        Log.i(TAG, "Data Size: " + stepData.getDataPoints().size());
                        if (stepData.getDataPoints().size() == 0){
                            HeartrateArr.add(0.0);
                            temp_date_index++;
                        } else {
                            for (DataPoint dp : stepData.getDataPoints()) {
                                Log.i(TAG, "Data point:");
                                Log.i(TAG, "\tType: " + dp.getDataType().getName());
                                Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                                Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                                for (Field field : dp.getDataType().getFields()) {
                                    Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                                    if (field.getName().equals("average")) {
                                        HeartrateArr.add(Double.parseDouble(dp.getValue(field).toString()));
                                        HeartrateDateArr[temp_date_index] = dp.getEndTime(TimeUnit.MILLISECONDS);
                                        temp_date_index++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (dataReadResult.getDataSets().size() > 0) {     //Used for non-aggregated data
                Log.e(TAG, "Number of returned DataSets: " + dataReadResult.getDataSets().size());
                for (DataSet stepData : dataReadResult.getDataSets()) {
                    Log.i(TAG, "Data returned for Data type: " + stepData.getDataType().getName());
                    DateFormat dateFormat = getTimeInstance();
                    Log.i(TAG, "Data Size: " + stepData.getDataPoints().size());
                    if (stepData.getDataPoints().size() == 0){
                        HeartrateArr.add(0.0);
                        temp_date_index++;
                    } else {
                        for (DataPoint dp : stepData.getDataPoints()) {
                            Log.i(TAG, "Data point:");
                            Log.i(TAG, "\tType: " + dp.getDataType().getName());
                            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                            for (Field field : dp.getDataType().getFields()) {
                                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                                if (field.getName().equals("average")) {
                                    HeartrateArr.add(Double.parseDouble(dp.getValue(field).toString()));
                                    HeartrateDateArr[temp_date_index] = dp.getEndTime(TimeUnit.MILLISECONDS);
                                    temp_date_index++;
                                }
                            }
                        }
                    }
                }
            }

            List<PointValue> dummy_values = new ArrayList<PointValue>();
            List<PointValue> values = new ArrayList<PointValue>();
            List<PointValue> avg_values = new ArrayList<PointValue>();
            int index = 0;
            int count = 0;
            double total_heartrate = 0.0;
            for (Double test : HeartrateArr){
                Log.i(TAG, "Date: " + dateFormat.format(HeartrateDateArr[index]));
                Log.i(TAG, test.toString());
                values.add(new PointValue(index, Float.parseFloat(test.toString())));
                total_heartrate += test;
                if (test != 0.0){
                    count++;
                }
                index++;
            }
            if (count == 0){
                count = 1;
            }

            avg_heartrate = (int) total_heartrate / count;
            HeartAvgValue.setText(String.format("%d",avg_heartrate));
            HeartAvgDate.setText(dateFormat.format(HeartrateDateArr[6]));
            HeartDataDate.setText(dateFormat.format(HeartrateDateArr[6]));
            HeartDataValue.setText(String.format("%.0f", HeartrateArr.get(6)));
            current_heartrate = HeartrateArr.get(6).intValue();

            List<Line> lines = new ArrayList<Line>();
            //add 2 dummy lines
            Line dummy_line = new Line(dummy_values);
            lines.add(dummy_line);
            lines.add(dummy_line);
            //In most cased you can call data model methods in builder-pattern-like manner.
            Line line = new Line(values).setColor(0xFFFF4081).setCubic(false).setPointRadius(5).setStrokeWidth(3);
            lines.add(line);
            //add avg Line
            avg_values.add(new PointValue(0, 0));
            avg_values.add(new PointValue(1, 0));
            avg_values.add(new PointValue(2, 0));
            avg_values.add(new PointValue(3, 0));
            avg_values.add(new PointValue(4, 0));
            avg_values.add(new PointValue(5, 0));
            avg_values.add(new PointValue(6, 0));
            Line avg_line = new Line(avg_values).setColor(0xFFFFAE19).setCubic(false).setPointRadius(0).setStrokeWidth(1);
            lines.add(avg_line);

            LineChartData data = new LineChartData();
            data.setLines(lines);

            HeartChart.setLineChartData(data);
            HeartChart.setOnValueTouchListener(MainActivity.this);
            prepareDataAnimation2(data);
            HeartChart.startDataAnimation();
            HeartChart.setZoomEnabled(false);
        }
    }

    private class VerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            long total = 0;
            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                total = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }
            Log.i(TAG, "Total steps: " + total);
            return null;
        }
    }
    @Override
    public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
        if (lineIndex == 0) {
            StepsDataDate.setText(dateFormat.format(stepsDateArr[pointIndex]));
            StepsDataValue.setText(String.format("%.0f", value.getY()));
        } else if (lineIndex == 2){
            HeartDataDate.setText(dateFormat.format(HeartrateDateArr[pointIndex]));
            HeartDataValue.setText(String.format("%.0f", value.getY()));
        }
    }

    @Override
    public void onValueDeselected() { }

    private void prepareDataAnimation(LineChartData data) {
        for (Line line : data.getLines()) {
            if (line.getColor() == 0xFFFFAE19) {
                for (PointValue value : line.getValues()) {
                    value.setTarget(value.getX(), (float) avg_steps);
                }
            }
        }
    }

    private void prepareDataAnimation2(LineChartData data) {
        for (Line line : data.getLines()) {
            if (line.getColor() == 0xFFFFAE19) {
                for (PointValue value : line.getValues()) {
                    value.setTarget(value.getX(), (float) avg_heartrate);
                }
            }
        }
    }

    private void showRegistrationDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.registration);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        final EditText emailText = (EditText) dialog.findViewById(R.id.reg_email_text);
        final EditText nameText = (EditText) dialog.findViewById(R.id.reg_name_text);
        final TextView dobText = (TextView) dialog.findViewById(R.id.reg_dob_text);
        final Button dobEdit = (Button) dialog.findViewById(R.id.reg_dob_edit);
        final CheckBox illEdit = (CheckBox) dialog.findViewById(R.id.reg_ill_edit);
        final CheckBox ill2Edit = (CheckBox) dialog.findViewById(R.id.reg_ill2_edit);
        final CheckBox agreeEdit = (CheckBox) dialog.findViewById(R.id.reg_agree_edit);
        final Button submit = (Button) dialog.findViewById(R.id.reg_confirm);

        dobEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day)
                    {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        dobText.setText(String.format("%02d", day) + " / " + String.format("%02d", (month+1)) + " / " + String.format("%04d", year));
                    }
                };
                DatePickerDialog dialog =
                        new DatePickerDialog(MainActivity.this,
                                datepicker,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (nameText.getText().toString().equals("") || nameText.getText()==null){
                    Toast.makeText(MainActivity.this, "Please enter a user name.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (emailText.getText().equals("") || emailText.getText()==null || (android.util.Patterns.EMAIL_ADDRESS.matcher(emailText.getText().toString()).matches() == false)){
                    Toast.makeText(MainActivity.this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (agreeEdit.isChecked() == false){
                    Toast.makeText(MainActivity.this, "To continue, you must agree to the Privacy Policy.", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences.Editor editor = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE).edit();
                editor.putString(KEY_CREDENTIALS, "DUMMY CREDENTIALS");
                editor.putString("name", nameText.getText().toString());
                editor.putString("email", emailText.getText().toString());
                editor.putString("dob", dobText.getText().toString());
                editor.putBoolean("illness1", illEdit.isChecked());
                editor.putBoolean("illness2", ill2Edit.isChecked());
                editor.commit();
                history_name = nameText.getText().toString();
                history_email = emailText.getText().toString();
                history_dob = dobText.getText().toString();
                history_illness1 = illEdit.isChecked();
                history_illness2 = ill2Edit.isChecked();
                dialog.dismiss();
                initializeUI();
            }
        });

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.8);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);
        dialog.show();
    }


}