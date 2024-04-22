package com.qppd.smartwaterguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qppd.smartwaterguard.Classes.Device;
import com.qppd.smartwaterguard.Classes.History;
import com.qppd.smartwaterguard.Classes.Setting;
import com.qppd.smartwaterguard.Classes.Unit;
import com.qppd.smartwaterguard.Globals.UserGlobal;
import com.qppd.smartwaterguard.Libs.AutoTimez.AutotimeClass;
import com.qppd.smartwaterguard.Libs.DateTimez.DateTimeClass;
import com.qppd.smartwaterguard.Libs.Functionz.UserFunctions;
import com.qppd.smartwaterguard.Libs.IntentManager.IntentManagerClass;
import com.qppd.smartwaterguard.Libs.Permissionz.PermissionClass;
import com.qppd.smartwaterguard.Tasks.LogoutTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class MonitorActivity extends AppCompatActivity implements OnChartValueSelectedListener, RadioGroup.OnCheckedChangeListener {

    private UserFunctions function = new UserFunctions(this);
    private AutotimeClass autotime = new AutotimeClass(this);
    private PermissionClass permission = new PermissionClass(this, this);
    private DateTimeClass datetimeMonth = new DateTimeClass("MMMM");
    private DateTimeClass datetime = new DateTimeClass("MM/dd/YYYY");

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private int PERMISSION_ALL = 1;

    private String[] PERMISSIONS = {android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.CAMERA, android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CALL_PHONE, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CHANGE_NETWORK_STATE, android.Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private AlertDialog.Builder logout_dialog_builder;
    private AlertDialog logout_dialog;

    private TextView txtDatetime;

    private RadioGroup rgpProvider;
    private RadioButton radMWSS;
    private RadioButton radMAYNILAD;

    private TextView txtPeso;
    private TextView txtReading;
    private TextView txtRate;
    private TextView txtPreviousReading;

    private BarChart chartMonthly;

    private LineChart chartFlowRate;
    private ArrayList<Entry> flowRateEntries = new ArrayList<>();

    private Handler handler = new Handler(Looper.getMainLooper());

    float previous_reading;
    float present_reading;

    float environmental_charge;
    float maintenance_service_charge;
    float government_tax;
    float first_10;
    float next_10;
    float next_20_1;
    float next_20_2;
    float next_20_3;
    float next_20_4;
    float next_50_1;
    float next_50_2;
    float over200;

    int reading_unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        //function.noActionBar(getSupportActionBar());
        function.setActionbar(getSupportActionBar(), 3, "Water Guard", 0);
        autotime.checkAutotime();

        if (!permission.hasPermissions()) {
            permission.requestPermissions(PERMISSION_ALL);
        }

        initComponents();
        handler.post(updateClockRunnable);


        loadPreviousReading();
        showDeviceReading();
        loadHistory();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.monitor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                IntentManagerClass.intentsify(this, SettingActivity.class);

                break;
//            case R.id.action_exit:
//                finish();
//                break;
            case R.id.action_reset:
                resetPassword();

                break;
            case R.id.action_logout:
                logout_dialog_builder.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initComponents() {

        radMWSS = findViewById(R.id.radMWSS);
        radMWSS.setChecked(true);
        loadSetting(0);
        radMAYNILAD = findViewById(R.id.radMAYNILAD);
        rgpProvider = findViewById(R.id.rgpProvider);
        rgpProvider.setOnCheckedChangeListener(this);

        chartMonthly = findViewById(R.id.chartMonthly);

        chartMonthly.setOnChartValueSelectedListener(this);
        chartMonthly.setDrawBarShadow(false);
        chartMonthly.setDrawValueAboveBar(true);
        chartMonthly.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chartMonthly.setMaxVisibleValueCount(31);
        // scaling can now only be done on x- and y-axis separately
        chartMonthly.setPinchZoom(false);
        chartMonthly.setDrawGridBackground(false);


        //YAxis leftAxis = chartMonthly.getAxisLeft();
        //leftAxis.setTypeface(tfLight);
        //leftAxis.setLabelCount(8, false);
        //leftAxis.setValueFormatter(custom);
        //leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        //leftAxis.setSpaceTop(15f);
        //leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        //YAxis rightAxis = chartMonthly.getAxisRight();
        //rightAxis.setDrawGridLines(false);
        //rightAxis.setTypeface(tfLight);
        //rightAxis.setLabelCount(8, false);
        //rightAxis.setValueFormatter(custom);
        //rightAxis.setSpaceTop(15f);
        //rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

//        Legend l = chartMonthly.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setForm(Legend.LegendForm.SQUARE);
//        l.setFormSize(9f);
//        l.setTextSize(11f);
//        l.setXEntrySpace(4f);

        chartFlowRate = findViewById(R.id.chartFlowRate);
        chartFlowRate.setDragEnabled(true);
        chartFlowRate.setScaleEnabled(true);


        Description description = new Description();
        description.setText("L/min");
        chartFlowRate.setDescription(description);

        txtDatetime = findViewById(R.id.txtDatetime);

        txtPeso = findViewById(R.id.txtPeso);
        txtReading = findViewById(R.id.txtReading);
        txtRate = findViewById(R.id.txtRate);
        txtPreviousReading = findViewById(R.id.txtPreviousReading);

        logout_dialog_builder = new AlertDialog.Builder(this);
        logout_dialog_builder.setCancelable(true);
        logout_dialog_builder.setTitle("Confirmation");
        logout_dialog_builder.setMessage("Are you sure you want to log out?");
        logout_dialog_builder.setPositiveButton(android.R.string.ok, (dialog, which) -> attemptLogout());
        logout_dialog_builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> logout_dialog.dismiss());
        logout_dialog = logout_dialog_builder.create();

    }

    private void attemptLogout() {
        LogoutTask logoutTask = new LogoutTask(this, function);
        logoutTask.execute((Void) null);

    }

    public void resetPassword() {
        firebaseAuth.sendPasswordResetEmail(UserGlobal.getUser().getEmail()).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Reset link sent to email address.", Toast.LENGTH_SHORT).show();
            attemptLogout();
        }).addOnFailureListener(e -> {

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private int columnCounter = 0;

    private void showDeviceReading() {

        firebaseDatabase.getReference().child("datas/device/readings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Device device = snapshot.getValue(Device.class);
                columnCounter++;
                int rate = device.getRate();
                present_reading = device.getCm3();

                computeInPeso();

                showToScreen(present_reading, device.getRate());


                flowRateEntries.add(new Entry(columnCounter, rate));

                LineDataSet dataSet = new LineDataSet(flowRateEntries, "Flow Rate");
                LineData lineData = new LineData(dataSet);
                chartFlowRate.setData(lineData);
                chartFlowRate.invalidate(); // Refresh the chart
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showToScreen(float present_reading, int flow_rate) {
        String unit = null;
        float convertedReading = present_reading; // Initialize converted reading with the same value

        switch (reading_unit) {
            case 0:
                unit = "gal.";
                convertedReading *= 264.172; // Convert to gallons
                flow_rate *= 0.264172; // Convert to gallons
                break;
            case 1:
                unit = "cu.ft.";
                convertedReading *= 35.3147; // Convert to cubic feet
                flow_rate *= 0.0353147; // Convert to cubic feet
                break;
            case 2:
                unit = "mL";
                convertedReading *= 1000000; // Convert to milliliters
                flow_rate *= 1000; // Convert to milliliters
                break;
            case 3:
                unit = "L";
                convertedReading *= 1000; // Convert to liters
                //flow_rate *= 1000; // Convert to liters
                break;
            case 4:
                unit = "cu.m.";
                flow_rate *= 0.001; // Convert to cubic meters
                // No conversion needed, already in cubic meters
                break;
        }

        //txtPreviousReading.setText(convertedReading + " " + unit);
        txtReading.setText(convertedReading + " " + unit);
        txtRate.setText(flow_rate + " " + unit + "/min");
    }

    private void computeInPeso() {
        float reading = present_reading - previous_reading;
        double total = 0;
        //function.showMessage("READING: " + reading);
        double multiplier = 0;
        if (reading <= 10) {
            if (radMWSS.isChecked()) {
                total = Double.parseDouble(String.valueOf(first_10));
            } else if (radMAYNILAD.isChecked()) {
                total = Double.parseDouble(String.valueOf(first_10));
            }
        } else if (reading > 10) {
            total = Double.parseDouble(String.valueOf(first_10));
        }

        if (reading > 10 && reading <= 20) {
            multiplier = next_10;
            total += (reading - 10) * multiplier;
        } else if (reading > 20 && reading <= 40) {
            multiplier = next_20_1;
            total += (reading - 10) * multiplier;
        } else if (reading > 40 && reading <= 60) {
            multiplier = next_20_2;
            total += (reading - 10) * multiplier;
        } else if (reading > 60 && reading <= 80) {
            multiplier = next_20_3;
            total += (reading - 10) * multiplier;
        } else if (reading > 80 && reading <= 100) {
            multiplier = next_20_4;
            total += (reading - 10) * multiplier;
        } else if (reading > 100 && reading <= 150) {
            multiplier = next_50_1;
            total += (reading - 10) * multiplier;
        } else if (reading > 150 && reading <= 200) {
            multiplier = next_50_1;
            total += (reading - 10) * multiplier;
        } else if (reading > 200) {
            multiplier = over200;
            total += (reading - 10) * multiplier;
        }

        total += (environmental_charge / 100) * total;
        total += total * government_tax / 100;
        total += Double.parseDouble(Float.toString(maintenance_service_charge));


        txtPeso.setText("₱ " + String.format("%.2f", Double.parseDouble(String.valueOf(total))));
    }

    private double computeHistoryInPeso(float reading) {
        double total = 0;
        //function.showMessage("READING: " + reading);
        double multiplier = 0;
        if (reading <= 10) {
            if (radMWSS.isChecked()) {
                total = Double.parseDouble(String.valueOf(first_10));
            } else if (radMAYNILAD.isChecked()) {
                total = Double.parseDouble(String.valueOf(first_10));
            }
        } else if (reading > 10) {
            total = Double.parseDouble(String.valueOf(first_10));
        }

        if (reading > 10 && reading <= 20) {
            multiplier = next_10;
            total += (reading - 10) * multiplier;
        } else if (reading > 20 && reading <= 40) {
            multiplier = next_20_1;
            total += (reading - 10) * multiplier;
        } else if (reading > 40 && reading <= 60) {
            multiplier = next_20_2;
            total += (reading - 10) * multiplier;
        } else if (reading > 60 && reading <= 80) {
            multiplier = next_20_3;
            total += (reading - 10) * multiplier;
        } else if (reading > 80 && reading <= 100) {
            multiplier = next_20_4;
            total += (reading - 10) * multiplier;
        } else if (reading > 100 && reading <= 150) {
            multiplier = next_50_1;
            total += (reading - 10) * multiplier;
        } else if (reading > 150 && reading <= 200) {
            multiplier = next_50_1;
            total += (reading - 10) * multiplier;
        } else if (reading > 200) {
            multiplier = over200;
            total += (reading - 10) * multiplier;
        }

        total += (environmental_charge / 100) * total;
        total += total * government_tax / 100;
        total += Double.parseDouble(Float.toString(maintenance_service_charge));


        return total;
    }


    private void loadPreviousReading() {
        firebaseDatabase.getReference().child("datas/device/previous_readings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Device device = snapshot.getValue(Device.class);
                previous_reading = device.getCm3();

                String unit = null;
                float convertedReading = previous_reading; // Initialize converted reading with the same value

                switch (reading_unit) {
                    case 0:
                        unit = "gal.";
                        convertedReading *= 264.172; // Convert to gallons
                        break;
                    case 1:
                        unit = "cu.ft.";
                        convertedReading *= 35.3147; // Convert to cubic feet
                        break;
                    case 2:
                        unit = "mL";
                        convertedReading *= 1000000; // Convert to milliliters
                        break;
                    case 3:
                        unit = "L";
                        convertedReading *= 1000; // Convert to liters
                        break;
                    case 4:
                        unit = "cu.m.";
                        // No conversion needed, already in cubic meters
                        break;
                }

                txtPreviousReading.setText(convertedReading + " " + unit);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadSetting(int provider) {
        if (provider == 0) {
            firebaseDatabase.getReference().child("datas/app/0").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Setting setting = snapshot.getValue(Setting.class);

                    environmental_charge = setting.getEnvironmental_charge();
                    maintenance_service_charge = setting.getMaintenance_service_charge();
                    government_tax = setting.getGovernment_tax();
                    first_10 = setting.getFirst10();
                    next_10 = setting.getNext10();
                    next_20_1 = setting.getNext20_1();
                    next_20_2 = setting.getNext20_2();
                    next_20_3 = setting.getNext20_3();
                    next_20_4 = setting.getNext20_4();
                    next_50_1 = setting.getNext50_1();
                    next_50_2 = setting.getNext50_2();
                    over200 = setting.getOver200();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (provider == 1) {
            firebaseDatabase.getReference().child("datas/app/1").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Setting setting = snapshot.getValue(Setting.class);

                    environmental_charge = setting.getEnvironmental_charge();
                    maintenance_service_charge = setting.getMaintenance_service_charge();
                    government_tax = setting.getGovernment_tax();
                    first_10 = setting.getFirst10();
                    next_10 = setting.getNext10();
                    next_20_1 = setting.getNext20_1();
                    next_20_2 = setting.getNext20_2();
                    next_20_3 = setting.getNext20_3();
                    next_20_4 = setting.getNext20_4();
                    next_50_1 = setting.getNext50_1();
                    next_50_2 = setting.getNext50_2();
                    over200 = setting.getOver200();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        firebaseDatabase.getReference().child("datas/unit").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Unit unit = snapshot.getValue(Unit.class);

                reading_unit = unit.getUnit();
                //function.showMessage("Unit:" + reading_unit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadHistory() {
        firebaseDatabase.getReference().child("datas/history").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> values = new ArrayList<>();
                ArrayList<IBarDataSet> dataSets = new ArrayList<>(); // Store BarDataSet for each entry

                // Collect data into a TreeMap to automatically sort by keys (dates)
                TreeMap<Date, History> sortedData = new TreeMap<>(new Comparator<Date>() {
                    @Override
                    public int compare(Date d1, Date d2) {
                        return d1.compareTo(d2);
                    }
                });

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    History history = dataSnapshot.getValue(History.class);
                    String key = dataSnapshot.getKey();
                    Date date = parseDate(key); // Parse date string into Date object
                    sortedData.put(date, history);
                }

                int index = 0;
                // Fixed colors array
                int[] colors = new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.LTGRAY, Color.WHITE, Color.BLACK};

                // Iterate over sorted entries
                for (Map.Entry<Date, History> entry : sortedData.entrySet()) {
                    Date date = entry.getKey();
                    History history = entry.getValue();
                    String key = formatDate(date); // Format Date object back into string (if needed)

                    float reading = history.getCm3() + 10;
                    double peso = computeHistoryInPeso(reading);
                    double total = peso - 225.15;

                    values.add(new BarEntry(index, (float) total));

                    BarDataSet dataSet = new BarDataSet(Collections.singletonList(new BarEntry(index, (float) total)), "" +key);
                    // Assigning fixed color from the array
                    dataSet.setColor(colors[index % colors.length]); // Use modulo operator to cycle through colors
                    dataSets.add(dataSet);

                    index++;
                }

                BarData data = new BarData(dataSets);
                data.setValueTextSize(10f);
                chartMonthly.setData(data);
                chartMonthly.invalidate(); // Refresh chart
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    // Method to parse date string into Date object
    private Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yy");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to format Date object into string
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d");
        return dateFormat.format(date);
    }



    // Method to generate a random color
    private int generateRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


    private Runnable updateClockRunnable = new Runnable() {
        @Override
        public void run() {
            // Get the current time
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a", Locale.getDefault());
            String currentTime = dateFormat.format(calendar.getTime());

            // Update the TextView with the current time
            txtDatetime.setText(currentTime);

            // Schedule the next update after 1000 milliseconds (1 second)
            handler.postDelayed(this, 1000);
        }
    };


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    protected void onDestroy() {
        // Remove the callback to prevent memory leaks
        handler.removeCallbacks(updateClockRunnable);
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radMWSS.isChecked()) {
            //setVisibility(true, false);
            loadSetting(0);
        } else if (radMAYNILAD.isChecked()) {
            //setVisibility(false, true);
            loadSetting(1);
        }
    }

}