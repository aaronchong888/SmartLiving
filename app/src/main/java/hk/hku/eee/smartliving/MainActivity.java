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
import android.os.Handler;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import static java.text.DateFormat.getTimeInstance;

public class MainActivity extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LineChartOnValueSelectListener
{
    private static final int TAG_CODE_PERMISSION_LOCATION = 1340;
    private static final String TAG_Check = "Check GooglePlay Ver";
    private static final double CONSTANT_PM25 = 0.0002180567;
    private static final double CONSTANT_PM10 = 0.0002821751;
    private static final double CONSTANT_SO2 = 0.0001393235;
    private static final double CONSTANT_O3 = 0.0005116328;
    private static final double CONSTANT_NO2 = 0.0004462559;
    private static final int MaxRow = 44 + 1;
    private static final int MaxCol = 60 + 1;
    private boolean completed = false;
    private boolean has_location = false;

    private LatLng current_latlng;
    private LocationManager locationManager;
    private QuickTask quickTask;

    private RelativeLayout Relative_Profile;
    private RelativeLayout Relative_Home;
    private RelativeLayout Relative_Health;
    private RelativeLayout Relative_Report;

    private BottomBar mBottomBar;
    private @IdRes int prev_tabId = R.id.tab_home;

    private RelativeLayout Device1ViewClick;
    private LinearLayout Device1ViewDetail;
    private DiscreteSeekBar discreteSeekBar1;
    private FloatingActionButton addBtn;
    private ToggleButton quiet_button;
    private ToggleButton swing_button;
    private RadioGroup radio_group;
    private TextView device1_degree;
    private TextView device1_mode;
    private TextView temp_text;
    private Switch device1_switch;

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
    private TextView exercise_text;
    private ImageView aqhi_icon;

    private CheckBox ill_edit;
    private CheckBox ill2_edit;
    private TextView dob_text;
    private Button dob_edit;
    private TextView user_name_text;
    private Button user_name_edit;
    private TextView email_text;
    private Button clear_profile;

    private RelativeLayout ReportViewClick;
    private LinearLayout ReportViewDetail;
    private Handler handler = new Handler();
    private TextView device1_usage_hr_text;
    private TextView device1_usage_min_text;
    private TextView report_usage_hr_text;
    private TextView report_usage_min_text;
    private TextView report_power_text;
    private TextView device1_power_text;
    private ColumnChartView PowerChart;
    private TextView power_data_title;
    private TextView power_data_txt;

    private GoogleApiClient mClient = null;
    private static final String TAG = "BasicHistoryApi";
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
    private Boolean history_device1_state;
    private String history_device1_mode;
    private Integer history_device1_temp;
    private Boolean history_device1_swing;
    private Boolean history_device1_quiet;
    private Integer history_usage_hr;
    private Integer history_usage_min;
    private Float history_power;

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
                //device status
                history_device1_state = preferences.getBoolean("device1_state", false);
                history_device1_mode = preferences.getString("device1_mode", "");
                history_device1_temp = preferences.getInt("device1_temp", 25);
                history_device1_swing = preferences.getBoolean("device1_swing", false);
                history_device1_quiet = preferences.getBoolean("device1_quiet", false);
                //report
                history_usage_hr = preferences.getInt("usage_hr", 0);
                history_usage_min = preferences.getInt("usage_min", 0);
                history_power = preferences.getFloat("power", 0.0f);
                // InitializeUI
                initializeUI();
            }else {
                //Registration Dialog
                showRegistrationDialog(MainActivity.this);
                history_device1_state = false;
                history_device1_mode = "[ Auto ]";
                history_device1_temp = 25;
                history_device1_swing = false;
                history_device1_quiet = false;
                history_usage_hr = 0;
                history_usage_min = 0;
                history_power = 0.0f;
            }
        }
    }

    private void initializeUI() {

        Log.i("Initialize UI", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        Relative_Home = (RelativeLayout) findViewById(R.id.home_view);
        Relative_Profile = (RelativeLayout) findViewById(R.id.profile_view);
        Relative_Health = (RelativeLayout) findViewById(R.id.health_view);
        Relative_Report = (RelativeLayout) findViewById(R.id.report_view);

        Device1ViewClick = (RelativeLayout) findViewById(R.id.device1_view_click);
        Device1ViewDetail = (LinearLayout) findViewById(R.id.device1_detail_view);
        discreteSeekBar1 = (DiscreteSeekBar) findViewById(R.id.discrete1);
        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        swing_button = (ToggleButton) findViewById(R.id.swing_button);
        quiet_button = (ToggleButton) findViewById(R.id.quiet_button);
        device1_mode = (TextView) findViewById(R.id.device1_mode);
        temp_text = (TextView) findViewById(R.id.temp_text);
        device1_degree = (TextView) findViewById(R.id.device1_subTitle);
        device1_switch = (Switch) findViewById(R.id.device1_switch);
        addBtn = (FloatingActionButton) findViewById(R.id.add_button);

        //restore History states
        if (history_device1_mode.equals("[ Cool ]")) {
            radio_group.check(radio_group.getChildAt(1).getId());
        } else if (history_device1_mode.equals("[ Dry ]")) {
            radio_group.check(radio_group.getChildAt(2).getId());
        } else if (history_device1_mode.equals("[ Fan ]")) {
            radio_group.check(radio_group.getChildAt(3).getId());
        } else if (history_device1_mode.equals("[ Heat ]")) {
            radio_group.check(radio_group.getChildAt(4).getId());
        } else {
            radio_group.check(radio_group.getChildAt(0).getId());
        }
        if (history_device1_state){
            Device1ViewDetail.setVisibility(View.VISIBLE);
            device1_switch.setChecked(true);
            device1_degree.setText(String.valueOf(history_device1_temp));
            device1_mode.setText(history_device1_mode);
            handler.removeCallbacks(updateTimer);
            handler.postDelayed(updateTimer, 1000);
        } else {
            Device1ViewDetail.setVisibility(View.GONE);
            device1_switch.setChecked(false);
            device1_degree.setText("--");
            device1_mode.setText("[ -- ]");
        }
        if (history_device1_temp < 15) {
            discreteSeekBar1.setTrackColor(0xFF55DEFF);
        } else if (history_device1_temp < 22){
            discreteSeekBar1.setTrackColor(0xFF54FE94);
        } else if (history_device1_temp < 26){
            discreteSeekBar1.setTrackColor(0xFF9EFE53);
        } else if (history_device1_temp < 28){
            discreteSeekBar1.setTrackColor(0xFFFDD252);
        } else {
            discreteSeekBar1.setTrackColor(0xFFFD525D);
        }
        discreteSeekBar1.setProgress(history_device1_temp);
        temp_text.setText(String.valueOf(history_device1_temp));
        quiet_button.setChecked(history_device1_quiet);
        swing_button.setChecked(history_device1_swing);

        //set Event Listeners for all components
        Device1ViewClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Device1ViewDetail.getVisibility() == View.GONE && device1_switch.isChecked()){
                    Device1ViewDetail.setVisibility(View.VISIBLE);
                } else {
                    Device1ViewDetail.setVisibility(View.GONE);
                }
            }
        });
        discreteSeekBar1.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                temp_text.setText(String.valueOf(value));
                if (value < 15) {
                    seekBar.setTrackColor(0xFF55DEFF);
                } else if (value < 22){
                    seekBar.setTrackColor(0xFF54FE94);
                } else if (value < 26){
                    seekBar.setTrackColor(0xFF9EFE53);
                } else if (value < 28){
                    seekBar.setTrackColor(0xFFFDD252);
                } else {
                    seekBar.setTrackColor(0xFFFD525D);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                device1_degree.setText(String.valueOf(seekBar.getProgress()));
                history_device1_temp = seekBar.getProgress();
                SingleToast.show(MainActivity.this, "Temperature set to "+ String.valueOf(seekBar.getProgress()) + " °C", Toast.LENGTH_SHORT);
            }
        });
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                if (index == 0){
                    device1_mode.setText("[ Auto ]");
                    history_device1_mode = "[ Auto ]";
                    SingleToast.show(MainActivity.this, "Changed to Auto Mode.", Toast.LENGTH_SHORT);
                } else if (index == 1){
                    device1_mode.setText("[ Cool ]");
                    history_device1_mode = "[ Cool ]";
                    SingleToast.show(MainActivity.this, "Changed to Cool Mode.", Toast.LENGTH_SHORT);
                } else if (index == 2){
                    device1_mode.setText("[ Dry ]");
                    history_device1_mode = "[ Dry ]";
                    SingleToast.show(MainActivity.this, "Changed to Dry Mode.", Toast.LENGTH_SHORT);
                } else if (index == 3){
                    device1_mode.setText("[ Fan ]");
                    history_device1_mode = "[ Fan ]";
                    SingleToast.show(MainActivity.this, "Changed to Fan Mode.", Toast.LENGTH_SHORT);
                } else {
                    device1_mode.setText("[ Heat ]");
                    history_device1_mode = "[ Heat ]";
                    SingleToast.show(MainActivity.this, "Changed to Heat Mode.", Toast.LENGTH_SHORT);
                }
            }
        });
        swing_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    history_device1_swing = true;
                    SingleToast.show(MainActivity.this, "Swing is ON.", Toast.LENGTH_SHORT);
                }
                else
                {
                    history_device1_swing = false;
                    SingleToast.show(MainActivity.this, "Swing is OFF.", Toast.LENGTH_SHORT);
                }
            }
        });
        quiet_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    history_device1_quiet = true;
                    SingleToast.show(MainActivity.this, "Changed to Quiet operation.", Toast.LENGTH_SHORT);
                }
                else
                {
                    history_device1_quiet = false;
                    SingleToast.show(MainActivity.this, "Changed to Normal operation.", Toast.LENGTH_SHORT);
                }
            }
        });
        device1_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);
                    history_device1_state = true;
                    Device1ViewDetail.setVisibility(View.VISIBLE);
                    device1_degree.setText(String.valueOf(history_device1_temp));
                    device1_mode.setText(history_device1_mode);
                    SingleToast.show(MainActivity.this, "Bedroom Air Conditioner turned ON.", Toast.LENGTH_SHORT);
                }else{
                    handler.removeCallbacks(updateTimer);
                    history_device1_state = false;
                    Device1ViewDetail.setVisibility(View.GONE);
                    device1_degree.setText("--");
                    device1_mode.setText("[ -- ]");
                    SingleToast.show(MainActivity.this, "Bedroom Air Conditioner turned OFF.", Toast.LENGTH_SHORT);
                }
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleToast.show(MainActivity.this, "New device successfully added.", Toast.LENGTH_SHORT);
            }
        });

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

        ReportViewClick = (RelativeLayout) findViewById(R.id.report_view_click);
        ReportViewDetail = (LinearLayout) findViewById(R.id.report_detail_view);
        ReportViewClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ReportViewDetail.getVisibility() == View.GONE){
                    ReportViewDetail.setVisibility(View.VISIBLE);
                } else {
                    ReportViewDetail.setVisibility(View.GONE);
                }
            }
        });
        device1_usage_hr_text = (TextView) findViewById(R.id.device1_usage_hr_text);
        device1_usage_min_text = (TextView) findViewById(R.id.device1_usage_min_text);
        report_usage_hr_text = (TextView) findViewById(R.id.report_usage_hr_text);
        report_usage_min_text = (TextView) findViewById(R.id.report_usage_min_text);
        report_power_text = (TextView) findViewById(R.id.report_power_text);
        device1_power_text = (TextView) findViewById(R.id.device1_power_text);
        PowerChart = (ColumnChartView) findViewById(R.id.report_chart);
        PowerChart.setOnValueTouchListener(new ValueTouchListener());
        power_data_title = (TextView) findViewById(R.id.power_data_title);
        power_data_txt = (TextView) findViewById(R.id.power_data_txt);

        //restore History value
        report_usage_hr_text.setText(String.valueOf(history_usage_hr));
        device1_usage_hr_text.setText(String.valueOf(history_usage_hr));
        report_usage_min_text.setText(String.format("%02d",history_usage_min%60));
        device1_usage_min_text.setText(String.format("%02d",history_usage_min%60));
        report_power_text.setText(String.format("%.2f", history_power));
        device1_power_text.setText(String.format("%.2f", history_power));

        //generate random chart value for simulation
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < 11; ++i) {
            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < 1; ++j) {
                values.add(new SubcolumnValue((float) Math.random() * 50f + 15, Color.parseColor("#FF4081")));
            }
            Column column = new Column(values);
            column.setHasLabels(false);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }
        values = new ArrayList<SubcolumnValue>();
        values.add(new SubcolumnValue(history_power, Color.parseColor("#FFAE19")));
        Column column = new Column(values);
        column.setHasLabels(false);
        column.setHasLabelsOnlyForSelected(false);
        columns.add(column);

        ColumnChartData data = new ColumnChartData(columns);
        PowerChart.setColumnChartData(data);
        PowerChart.setZoomEnabled(false);
        Calendar c2 = Calendar.getInstance();
        Integer yyyy2 = c2.get(Calendar.YEAR);
        Integer mm2 = c2.get(Calendar.MONTH);
        String date = String.format("%02d", mm2+1) + "/" + String.format("%04d", yyyy2);
        power_data_title.setText(date);
        power_data_txt.setText(String.format("%.2f", history_power) + " kWh");

        aqhi_text = (TextView) findViewById(R.id.aqhi_text);
        er_text = (TextView) findViewById(R.id.er_text);
        advice_text = (TextView) findViewById(R.id.advice_text);
        exercise_text = (TextView) findViewById(R.id.exercise_text);
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

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_home) {
                    //action on Home pressed
                    if (tabId == prev_tabId){
                        return;
                    }
                    prev_tabId = R.id.tab_home;
                    if (Relative_Profile.getVisibility() == View.VISIBLE) {
                        Relative_Profile.setVisibility(View.GONE);
                    }
                    if (Relative_Health.getVisibility() == View.VISIBLE) {
                        Relative_Health.setVisibility(View.GONE);
                    }
                    if (Relative_Report.getVisibility() == View.VISIBLE) {
                        Relative_Report.setVisibility(View.GONE);
                    }
                    if (Relative_Home.getVisibility()!=View.VISIBLE) {
                        Relative_Home.setVisibility(View.VISIBLE);
                    }

                } else if (tabId == R.id.tab_report) {
                    // action on Report pressed
                    if (tabId == prev_tabId){
                        return;
                    }
                    prev_tabId = R.id.tab_report;
                    if (Relative_Profile.getVisibility() == View.VISIBLE) {
                        Relative_Profile.setVisibility(View.GONE);
                    }
                    if (Relative_Health.getVisibility() == View.VISIBLE) {
                        Relative_Health.setVisibility(View.GONE);
                    }
                    if (Relative_Home.getVisibility() == View.VISIBLE) {
                        Relative_Home.setVisibility(View.GONE);
                    }
                    if (Relative_Report.getVisibility()!=View.VISIBLE) {
                        Relative_Report.setVisibility(View.VISIBLE);
                    }

                } else if (tabId == R.id.tab_health) {
                    // Action on Health pressed
                    if (tabId == prev_tabId){
                        return;
                    }
                    prev_tabId = R.id.tab_health;
                    new LocationControl().execute(MainActivity.this);
                    Calendar c = Calendar.getInstance();
                    Integer yyyy = c.get(Calendar.YEAR);
                    user_age = yyyy - Integer.parseInt(dob_text.getText().toString().substring(dob_text.length()-4, dob_text.length()));
                    if (Relative_Home.getVisibility()==View.VISIBLE) {
                        Relative_Home.setVisibility(View.GONE);
                    }
                    if (Relative_Report.getVisibility() == View.VISIBLE) {
                        Relative_Report.setVisibility(View.GONE);
                    }
                    if (Relative_Profile.getVisibility() == View.VISIBLE) {
                        Relative_Profile.setVisibility(View.GONE);
                    }
                    if (Relative_Health.getVisibility() != View.VISIBLE) {
                        Relative_Health.setVisibility(View.VISIBLE);
                    }

                } else if (tabId == R.id.tab_profile) {
                    // action on Profile pressed
                    if (tabId == prev_tabId){
                        return;
                    }
                    prev_tabId = R.id.tab_profile;
                    if (Relative_Home.getVisibility()==View.VISIBLE) {
                        Relative_Home.setVisibility(View.GONE);
                    }
                    if (Relative_Report.getVisibility() == View.VISIBLE) {
                        Relative_Report.setVisibility(View.GONE);
                    }
                    if (Relative_Health.getVisibility() == View.VISIBLE) {
                        Relative_Health.setVisibility(View.GONE);
                    }
                    if (Relative_Profile.getVisibility() != View.VISIBLE) {
                        Relative_Profile.setVisibility(View.VISIBLE);
                    }

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
        editor.putBoolean("device1_state", history_device1_state);
        editor.putString("device1_mode", history_device1_mode);
        editor.putBoolean("device1_swing", history_device1_swing);
        editor.putBoolean("device1_quiet", history_device1_quiet);
        editor.putInt("device1_temp", history_device1_temp);
        editor.putInt("usage_hr", history_usage_hr);
        editor.putInt("usage_min", history_usage_min);
        editor.putFloat("power", history_power);
        editor.commit();
        if (quickTask != null && quickTask.getStatus() != AsyncTask.Status.FINISHED)
            quickTask.cancel(true);
    }

    private void updateLocation(LatLng centerLatLng) {
        if (quickTask != null && quickTask.getStatus() != AsyncTask.Status.FINISHED)
            quickTask.cancel(true);
        Integer row = (int) (((22.57 - centerLatLng.latitude) / 0.01) + 1);
        Integer col = (int) (((centerLatLng.longitude - 113.81) / 0.01) + 1);
        if (row > 0 && row < MaxRow && col > 0 && col < MaxCol) {
            if (!completed) {
                quickTask = new QuickTask();
                quickTask.execute(row.toString(), col.toString());
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
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.HOUR_OF_DAY, -1);        //suppose no need this line
                        Integer test_minute = c.get(Calendar.MINUTE);
                        if (test_minute < 30){                       //suppose this is 2 mins only
                            c.add(Calendar.HOUR_OF_DAY, -1);
                        }
                        er_text.setText(dateFormat.format(c.getTime()));
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
                        final_result += "\n\nAQHI : \t" + Integer.toString(AQHI_value);
                        if (AQHI_value < 4) {
                            final_result += "  ( Low )";
                            exercise_text.setVisibility(View.VISIBLE);
                            advice_text.setVisibility(View.GONE);
                            aqhi_icon.setImageResource(R.drawable.aqhi_low);
                        } else if (AQHI_value < 7) {
                            final_result += "  ( Moderate )";
                            if (illness || user_age < 6 || user_age > 64) {
                                exercise_text.setVisibility(View.GONE);
                                advice_text.setVisibility(View.VISIBLE);
                            } else {
                                exercise_text.setVisibility(View.VISIBLE);
                                advice_text.setVisibility(View.GONE);
                            }
                            aqhi_icon.setImageResource(R.drawable.aqhi_moderate);
                        } else if (AQHI_value < 8) {
                            final_result += "  ( High )";
                            exercise_text.setVisibility(View.GONE);
                            advice_text.setVisibility(View.VISIBLE);
                            aqhi_icon.setImageResource(R.drawable.aqhi_high);
                        } else if (AQHI_value < 11) {
                            final_result += "  ( Very High )";
                            exercise_text.setVisibility(View.GONE);
                            advice_text.setVisibility(View.VISIBLE);
                            aqhi_icon.setImageResource(R.drawable.aqhi_very_high);
                        } else if (AQHI_value < 12) {
                            final_result += "  ( Serious )";
                            exercise_text.setVisibility(View.GONE);
                            advice_text.setVisibility(View.VISIBLE);
                            aqhi_icon.setImageResource(R.drawable.aqhi_serious);
                        }
                        aqhi_text.setText(Integer.toString(AQHI_value));
                    }
                } catch (Throwable t) {
                    Log.e("PostExecute Error : ", "cannot parse malformed JSON");
                }
            }
            Log.e("Final Result : ", final_result);
        }

        @Override
        protected void onPreExecute() {
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
            c.add(Calendar.MONTH, -1);                //this line is added in case the server do not have the latest data
            c.add(Calendar.HOUR_OF_DAY, -1);        //this line is added in case the server do not have the latest data
            Integer test_minute = c.get(Calendar.MINUTE);
            if (test_minute < 30){                       //this line is added in case the server do not have the latest data
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

    public class LocationControl extends AsyncTask<Context, Void, Void>
    {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute()
        {
            this.dialog.setMessage("Determining your location...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        protected Void doInBackground(Context... params)
        {
            while (!has_location) {
                try {
                    Thread.currentThread();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(final Void unused)
        {
            if (current_latlng!=null) {
                Log.i("Location Found", current_latlng.toString());
                updateLocation(current_latlng);
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
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
    }

    private void prepareDataAnimation2(LineChartData data) {
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
                    SingleToast.show(MainActivity.this, "Please enter a user name.", Toast.LENGTH_SHORT);
                    return;
                }
                if (emailText.getText().equals("") || emailText.getText()==null || (android.util.Patterns.EMAIL_ADDRESS.matcher(emailText.getText().toString()).matches() == false)){
                    SingleToast.show(MainActivity.this, "Please enter a valid email.", Toast.LENGTH_SHORT);
                    return;
                }
                if (agreeEdit.isChecked() == false){
                    SingleToast.show(MainActivity.this, "To continue, you must agree to the Privacy Policy.", Toast.LENGTH_SHORT);
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

    private Runnable updateTimer = new Runnable() {
        public void run() {
            history_usage_min++;
            history_usage_hr = (history_usage_min)/60;
            history_power = 0.02f * history_usage_min;
            report_usage_hr_text.setText(String.valueOf(history_usage_hr));
            device1_usage_hr_text.setText(String.valueOf(history_usage_hr));
            device1_usage_min_text.setText(String.format("%02d",history_usage_min%60));
            report_usage_min_text.setText(String.format("%02d",history_usage_min%60));
            report_power_text.setText(String.format("%.2f", history_power));
            device1_power_text.setText(String.format("%.2f", history_power));

            List<SubcolumnValue> values;
            values = new ArrayList<SubcolumnValue>();
            values.add(new SubcolumnValue(history_power, Color.parseColor("#FFAE19")));
            PowerChart.getColumnChartData().getColumns().get(11).setValues(values);

            handler.postDelayed(this, 1000);
        }
    };

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, columnIndex-11);
            Integer yyyy = c.get(Calendar.YEAR);
            Integer mm = c.get(Calendar.MONTH);
            String date = String.format("%02d", mm+1) + "/" + String.format("%04d", yyyy);
            power_data_title.setText(date);
            power_data_txt.setText(String.format("%.2f", value.getValue()) + " kWh");
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    }

}
