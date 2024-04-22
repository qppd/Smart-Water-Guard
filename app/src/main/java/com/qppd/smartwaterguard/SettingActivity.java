package com.qppd.smartwaterguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qppd.smartwaterguard.Classes.Setting;
import com.qppd.smartwaterguard.Classes.Unit;
import com.qppd.smartwaterguard.Libs.AutoTimez.AutotimeClass;
import com.qppd.smartwaterguard.Libs.Functionz.UserFunctions;
import com.qppd.smartwaterguard.Libs.Permissionz.PermissionClass;
import com.qppd.smartwaterguard.Libs.Validatorz.ValidatorClass;
import com.qppd.smartwaterguard.Tasks.SettingTask;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private UserFunctions function = new UserFunctions(this);
    private AutotimeClass autotime = new AutotimeClass(this);
    private PermissionClass permission = new PermissionClass(this, this);

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private RadioGroup rgpProvider;
    private RadioButton radMWSS;
    private RadioButton radMAYNILAD;

    private EditText edtMWSSEnvironmentalCharge, edtMWSSMaintenanceServiceCharge, edtMWSSGovernmentTax;
    private EditText edtMWSSFirst10, edtMWSSNext10;
    private EditText edtMWSSNext20_1, edtMWSSNext20_2, edtMWSSNext20_3, edtMWSSNext20_4;
    private EditText edtMWSSNext50_1, edtMWSSNext50_2;
    private EditText edtMWSSOver200;

    private EditText edtMAYNILADEnvironmentalCharge, edtMAYNILADMaintenanceServiceCharge, edtMAYNILADGovernmentTax;
    private EditText edtMAYNILADFirst10, edtMAYNILADNext10;
    private EditText edtMAYNILADNext20_1, edtMAYNILADNext20_2, edtMAYNILADNext20_3, edtMAYNILADNext20_4;
    private EditText edtMAYNILADNext50_1, edtMAYNILADNext50_2;
    private EditText edtMAYNILADOver200;

    private Spinner spnUnits;

    private Button btnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        function.setActionbar(getSupportActionBar(), 1, "Settings", 0);
        autotime.checkAutotime();


        initComponents();
    }

    private void initComponents() {
        radMWSS = findViewById(R.id.radMWSS);
        radMWSS.setChecked(true);
        loadSetting(0);
        radMAYNILAD = findViewById(R.id.radMAYNILAD);
        rgpProvider = findViewById(R.id.rgpProvider);
        rgpProvider.setOnCheckedChangeListener(this);


        edtMWSSEnvironmentalCharge = findViewById(R.id.edtMWSSEnvironmentalCharge);
        edtMWSSMaintenanceServiceCharge = findViewById(R.id.edtMWSSMaintenanceServiceCharge);
        edtMWSSGovernmentTax = findViewById(R.id.edtMWSSGovernmentTax);
        edtMWSSFirst10 = findViewById(R.id.edtMWSSFirst10);
        edtMWSSNext10 = findViewById(R.id.edtMWSSNext10);
        edtMWSSNext20_1 = findViewById(R.id.edtMWSSNext20_1);
        edtMWSSNext20_2 = findViewById(R.id.edtMWSSNext20_2);
        edtMWSSNext20_3 = findViewById(R.id.edtMWSSNext20_3);
        edtMWSSNext20_4 = findViewById(R.id.edtMWSSNext20_4);
        edtMWSSNext50_1 = findViewById(R.id.edtMWSSNext50_1);
        edtMWSSNext50_2 = findViewById(R.id.edtMWSSNext50_2);
        edtMWSSOver200 = findViewById(R.id.edtMWSSOver200);

        edtMAYNILADEnvironmentalCharge = findViewById(R.id.edtMAYNILADEnvironmentalCharge);
        edtMAYNILADMaintenanceServiceCharge = findViewById(R.id.edtMAYNILADMaintenanceServiceCharge);
        edtMAYNILADGovernmentTax = findViewById(R.id.edtMAYNILADGovernmentTax);
        edtMAYNILADFirst10 = findViewById(R.id.edtMAYNILADFirst10);
        edtMAYNILADNext10 = findViewById(R.id.edtMAYNILADNext10);
        edtMAYNILADNext20_1 = findViewById(R.id.edtMAYNILADNext20_1);
        edtMAYNILADNext20_2 = findViewById(R.id.edtMAYNILADNext20_2);
        edtMAYNILADNext20_3 = findViewById(R.id.edtMAYNILADNext20_3);
        edtMAYNILADNext20_4 = findViewById(R.id.edtMAYNILADNext20_4);
        edtMAYNILADNext50_1 = findViewById(R.id.edtMAYNILADNext50_1);
        edtMAYNILADNext50_2 = findViewById(R.id.edtMAYNILADNext50_2);
        edtMAYNILADOver200 = findViewById(R.id.edtMAYNILADOver200);
        setVisibility(true, false);

        spnUnits = findViewById(R.id.spnUnits);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reading_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUnits.setAdapter(adapter);
        spnUnits.setOnItemSelectedListener(this);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

    }

    private void setVisibility(boolean isMWSSVisible, boolean isMayniladVisible) {
        int mwssVisibility = isMWSSVisible ? View.VISIBLE : View.GONE;
        int mayniladVisibility = isMayniladVisible ? View.VISIBLE : View.GONE;

        // Set visibility for MWSS EditText views
        edtMWSSEnvironmentalCharge.setVisibility(mwssVisibility);
        edtMWSSMaintenanceServiceCharge.setVisibility(mwssVisibility);
        edtMWSSGovernmentTax.setVisibility(mwssVisibility);
        edtMWSSFirst10.setVisibility(mwssVisibility);
        edtMWSSNext10.setVisibility(mwssVisibility);
        edtMWSSNext20_1.setVisibility(mwssVisibility);
        edtMWSSNext20_2.setVisibility(mwssVisibility);
        edtMWSSNext20_3.setVisibility(mwssVisibility);
        edtMWSSNext20_4.setVisibility(mwssVisibility);
        edtMWSSNext50_1.setVisibility(mwssVisibility);
        edtMWSSNext50_2.setVisibility(mwssVisibility);
        edtMWSSOver200.setVisibility(mwssVisibility);

        // Set visibility for Maynilad EditText views
        edtMAYNILADEnvironmentalCharge.setVisibility(mayniladVisibility);
        edtMAYNILADMaintenanceServiceCharge.setVisibility(mayniladVisibility);
        edtMAYNILADGovernmentTax.setVisibility(mayniladVisibility);
        edtMAYNILADFirst10.setVisibility(mayniladVisibility);
        edtMAYNILADNext10.setVisibility(mayniladVisibility);
        edtMAYNILADNext20_1.setVisibility(mayniladVisibility);
        edtMAYNILADNext20_2.setVisibility(mayniladVisibility);
        edtMAYNILADNext20_3.setVisibility(mayniladVisibility);
        edtMAYNILADNext20_4.setVisibility(mayniladVisibility);
        edtMAYNILADNext50_1.setVisibility(mayniladVisibility);
        edtMAYNILADNext50_2.setVisibility(mayniladVisibility);
        edtMAYNILADOver200.setVisibility(mayniladVisibility);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:

                if (radMWSS.isChecked()) {
                    //function.showMessage("MWSS!");
                    attemptSaveMWSS();
                } else {
                    //function.showMessage("MAYNILAD!");
                    attemptSaveMAYNILAD();
                }

                break;
        }
    }

    private void attemptSaveMWSS() {

        boolean cancel = false;
        View focusView = null;

        edtMWSSEnvironmentalCharge.setError(null);
        edtMWSSMaintenanceServiceCharge.setError(null);
        edtMWSSGovernmentTax.setError(null);
        edtMWSSFirst10.setError(null);
        edtMWSSNext10.setError(null);
        edtMWSSNext20_1.setError(null);
        edtMWSSNext20_2.setError(null);
        edtMWSSNext20_3.setError(null);
        edtMWSSNext20_4.setError(null);
        edtMWSSNext50_1.setError(null);
        edtMWSSNext50_2.setError(null);
        edtMWSSOver200.setError(null);

        String environmental_charge = edtMWSSEnvironmentalCharge.getText().toString();
        String maintenance_service_charge = edtMWSSMaintenanceServiceCharge.getText().toString();
        String government_tax = edtMWSSGovernmentTax.getText().toString();

        String first_10 = edtMWSSFirst10.getText().toString();
        String next_10 = edtMWSSNext10.getText().toString();
        String next_20_1 = edtMWSSNext20_1.getText().toString();
        String next_20_2 = edtMWSSNext20_2.getText().toString();
        String next_20_3 = edtMWSSNext20_3.getText().toString();
        String next_20_4 = edtMWSSNext20_4.getText().toString();
        String next_50_1 = edtMWSSNext20_3.getText().toString();
        String next_50_2 = edtMWSSNext20_4.getText().toString();
        String over200 = edtMWSSOver200.getText().toString();

        int reading_unit = spnUnits.getSelectedItemPosition();

        if (TextUtils.isEmpty(first_10)) {
            edtMWSSFirst10.setError("First 10 value is required!");
            focusView = edtMWSSFirst10;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(first_10)) {
            edtMWSSFirst10.setError("First 10 value is invalid!");
            focusView = edtMWSSFirst10;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_10)) {
            edtMWSSNext10.setError("Next 10 value is required!");
            focusView = edtMWSSNext10;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_10)) {
            edtMWSSNext10.setError("Next 10 value is invalid!");
            focusView = edtMWSSNext10;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_20_1)) {
            edtMWSSNext20_1.setError("Next 20(1) value is required!");
            focusView = edtMWSSNext20_1;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_20_1)) {
            edtMWSSNext20_1.setError("Next 20(1) value is invalid!");
            focusView = edtMWSSNext20_1;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_20_2)) {
            edtMWSSNext20_2.setError("Next 20(2) value is required!");
            focusView = edtMWSSNext20_2;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_20_2)) {
            edtMWSSNext20_2.setError("Next 20(2) value is invalid!");
            focusView = edtMWSSNext20_2;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_20_3)) {
            edtMWSSNext20_3.setError("Next 20(3) value is required!");
            focusView = edtMWSSNext20_3;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_20_3)) {
            edtMWSSNext20_3.setError("Next 20(3) value is invalid!");
            focusView = edtMWSSNext20_3;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_20_4)) {
            edtMWSSNext20_4.setError("Next 20(4) value is required!");
            focusView = edtMWSSNext20_4;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_20_4)) {
            edtMWSSNext20_4.setError("Next 20(4) value is invalid!");
            focusView = edtMWSSNext20_4;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_50_1)) {
            edtMWSSNext50_1.setError("Next 50(1) value is required!");
            focusView = edtMWSSNext50_1;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_50_1)) {
            edtMWSSNext50_1.setError("Next 50(1) value is invalid!");
            focusView = edtMWSSNext50_1;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_50_2)) {
            edtMWSSNext50_2.setError("Next 50(2) value is required!");
            focusView = edtMWSSNext50_2;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_50_2)) {
            edtMWSSNext50_2.setError("Next 50(2) value is invalid!");
            focusView = edtMWSSNext50_2;
            cancel = true;
        }

        if (TextUtils.isEmpty(over200)) {
            edtMWSSOver200.setError("Over 200 value is required!");
            focusView = edtMWSSOver200;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(over200)) {
            edtMWSSOver200.setError("Over 200 value is invalid!");
            focusView = edtMWSSOver200;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt signup and focus the
            // field with an error.
            focusView.requestFocus();
        } else {

            Setting setting = new Setting(Float.parseFloat(environmental_charge),
                    Float.parseFloat(maintenance_service_charge),
                    Float.parseFloat(government_tax), Float.parseFloat(first_10),
                    Float.parseFloat(next_10), Float.parseFloat(next_20_1),
                    Float.parseFloat(next_20_2), Float.parseFloat(next_20_3),
                    Float.parseFloat(next_20_4), Float.parseFloat(next_50_1),
                    Float.parseFloat(next_50_2), Float.parseFloat(over200));

            Unit unit = new Unit(reading_unit);

            SettingTask settingTask = new SettingTask(0, setting, unit, this, function);
            settingTask.execute((Void) null);
        }

    }

    private void attemptSaveMAYNILAD() {

        boolean cancel = false;
        View focusView = null;

        edtMAYNILADEnvironmentalCharge.setError(null);
        edtMAYNILADMaintenanceServiceCharge.setError(null);
        edtMAYNILADGovernmentTax.setError(null);
        edtMAYNILADFirst10.setError(null);
        edtMAYNILADNext10.setError(null);
        edtMAYNILADNext20_1.setError(null);
        edtMAYNILADNext20_2.setError(null);
        edtMAYNILADNext20_3.setError(null);
        edtMAYNILADNext20_4.setError(null);
        edtMAYNILADNext50_1.setError(null);
        edtMAYNILADNext50_2.setError(null);
        edtMAYNILADOver200.setError(null);

        String environmental_charge = edtMAYNILADEnvironmentalCharge.getText().toString();
        String maintenance_service_charge = edtMAYNILADMaintenanceServiceCharge.getText().toString();
        String government_tax = edtMAYNILADGovernmentTax.getText().toString();
        String first_10 = edtMAYNILADFirst10.getText().toString();
        String next_10 = edtMAYNILADNext10.getText().toString();
        String next_20_1 = edtMAYNILADNext20_1.getText().toString();
        String next_20_2 = edtMAYNILADNext20_2.getText().toString();
        String next_20_3 = edtMAYNILADNext20_3.getText().toString();
        String next_20_4 = edtMAYNILADNext20_4.getText().toString();
        String next_50_1 = edtMAYNILADNext20_3.getText().toString();
        String next_50_2 = edtMAYNILADNext20_4.getText().toString();
        String over200 = edtMAYNILADOver200.getText().toString();

        int reading_unit = spnUnits.getSelectedItemPosition();

        if (TextUtils.isEmpty(first_10)) {
            edtMAYNILADFirst10.setError("First 10 value is required!");
            focusView = edtMAYNILADFirst10;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(first_10)) {
            edtMAYNILADFirst10.setError("First 10 value is invalid!");
            focusView = edtMAYNILADFirst10;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_10)) {
            edtMAYNILADNext10.setError("Next 10 value is required!");
            focusView = edtMAYNILADNext10;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_10)) {
            edtMAYNILADNext10.setError("Next 10 value is invalid!");
            focusView = edtMAYNILADNext10;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_20_1)) {
            edtMAYNILADNext20_1.setError("Next 20(1) value is required!");
            focusView = edtMAYNILADNext20_1;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_20_1)) {
            edtMAYNILADNext20_1.setError("Next 20(1) value is invalid!");
            focusView = edtMAYNILADNext20_1;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_20_2)) {
            edtMAYNILADNext20_2.setError("Next 20(2) value is required!");
            focusView = edtMAYNILADNext20_2;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_20_2)) {
            edtMAYNILADNext20_2.setError("Next 20(2) value is invalid!");
            focusView = edtMAYNILADNext20_2;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_20_3)) {
            edtMAYNILADNext20_3.setError("Next 20(3) value is required!");
            focusView = edtMAYNILADNext20_3;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_20_3)) {
            edtMAYNILADNext20_3.setError("Next 20(3) value is invalid!");
            focusView = edtMAYNILADNext20_3;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_20_4)) {
            edtMAYNILADNext20_4.setError("Next 20(4) value is required!");
            focusView = edtMAYNILADNext20_4;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_20_4)) {
            edtMAYNILADNext20_4.setError("Next 20(4) value is invalid!");
            focusView = edtMAYNILADNext20_4;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_50_1)) {
            edtMAYNILADNext50_1.setError("Next 50(1) value is required!");
            focusView = edtMAYNILADNext50_1;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_50_1)) {
            edtMAYNILADNext50_1.setError("Next 50(1) value is invalid!");
            focusView = edtMAYNILADNext50_1;
            cancel = true;
        }

        if (TextUtils.isEmpty(next_50_2)) {
            edtMAYNILADNext50_2.setError("Next 50(2) value is required!");
            focusView = edtMAYNILADNext50_2;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(next_50_2)) {
            edtMAYNILADNext50_2.setError("Next 50(2) value is invalid!");
            focusView = edtMAYNILADNext50_2;
            cancel = true;
        }

        if (TextUtils.isEmpty(over200)) {
            edtMAYNILADOver200.setError("Over 200 value is required!");
            focusView = edtMAYNILADOver200;
            cancel = true;
        } else if (!ValidatorClass.validateDecimalNumberOnly(over200)) {
            edtMAYNILADOver200.setError("Over 200 value is invalid!");
            focusView = edtMAYNILADOver200;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt signup and focus the
            // field with an error.
            focusView.requestFocus();
        } else {

            Setting setting = new Setting(Integer.parseInt(environmental_charge),
                    Float.parseFloat(maintenance_service_charge),
                    Integer.parseInt(government_tax), Float.parseFloat(first_10),
                    Float.parseFloat(next_10), Float.parseFloat(next_20_1),
                    Float.parseFloat(next_20_2), Float.parseFloat(next_20_3),
                    Float.parseFloat(next_20_4), Float.parseFloat(next_50_1),
                    Float.parseFloat(next_50_2), Float.parseFloat(over200));

            Unit unit = new Unit(reading_unit);

            if (radMWSS.isChecked()) {
                SettingTask settingTask = new SettingTask(0, setting, unit, this, function);
                settingTask.execute((Void) null);
            } else if (radMAYNILAD.isChecked()) {
                SettingTask settingTask = new SettingTask(1, setting, unit, this, function);
                settingTask.execute((Void) null);
            }

        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radMWSS.isChecked()) {
            setVisibility(true, false);
            loadSetting(0);
        } else if (radMAYNILAD.isChecked()) {
            setVisibility(false, true);
            loadSetting(1);
        }
    }

    private void loadSetting(int provider) {
        if (provider == 0) {
            firebaseDatabase.getReference().child("datas/app/0")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Setting setting = snapshot.getValue(Setting.class);

                            edtMWSSEnvironmentalCharge.setText(String.valueOf(setting.getEnvironmental_charge()));
                            edtMWSSMaintenanceServiceCharge.setText(String.valueOf(setting.getMaintenance_service_charge()));
                            edtMWSSGovernmentTax.setText(String.valueOf(setting.getGovernment_tax()));
                            edtMWSSFirst10.setText(String.valueOf(setting.getFirst10()));
                            edtMWSSNext10.setText(String.valueOf(setting.getNext10()));
                            edtMWSSNext20_1.setText(String.valueOf(setting.getNext20_1()));
                            edtMWSSNext20_2.setText(String.valueOf(setting.getNext20_2()));
                            edtMWSSNext20_3.setText(String.valueOf(setting.getNext20_3()));
                            edtMWSSNext20_4.setText(String.valueOf(setting.getNext20_4()));
                            edtMWSSNext50_1.setText(String.valueOf(setting.getNext50_1()));
                            edtMWSSNext50_2.setText(String.valueOf(setting.getNext50_2()));
                            edtMWSSOver200.setText(String.valueOf(setting.getOver200()));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else if (provider == 1) {
            firebaseDatabase.getReference().child("datas/app/1")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Setting setting = snapshot.getValue(Setting.class);

                            edtMAYNILADEnvironmentalCharge.setText(String.valueOf(setting.getEnvironmental_charge()));
                            edtMAYNILADMaintenanceServiceCharge.setText(String.valueOf(setting.getMaintenance_service_charge()));
                            edtMAYNILADGovernmentTax.setText(String.valueOf(setting.getGovernment_tax()));
                            edtMAYNILADFirst10.setText(String.valueOf(setting.getFirst10()));
                            edtMAYNILADNext10.setText(String.valueOf(setting.getNext10()));
                            edtMAYNILADNext20_1.setText(String.valueOf(setting.getNext20_1()));
                            edtMAYNILADNext20_2.setText(String.valueOf(setting.getNext20_2()));
                            edtMAYNILADNext20_3.setText(String.valueOf(setting.getNext20_3()));
                            edtMAYNILADNext20_4.setText(String.valueOf(setting.getNext20_4()));
                            edtMAYNILADNext50_1.setText(String.valueOf(setting.getNext50_1()));
                            edtMAYNILADNext50_2.setText(String.valueOf(setting.getNext50_2()));
                            edtMAYNILADOver200.setText(String.valueOf(setting.getOver200()));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        firebaseDatabase.getReference().child("datas/unit")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Unit unit = snapshot.getValue(Unit.class);

                        spnUnits.setSelection(unit.getUnit());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}