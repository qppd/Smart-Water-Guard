package com.qppd.smartwaterguard.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.qppd.smartwaterguard.Classes.Setting;
import com.qppd.smartwaterguard.Classes.Unit;
import com.qppd.smartwaterguard.Libs.Functionz.UserFunctions;

public class SettingTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private UserFunctions function;
    private boolean updateSettingStatus = false;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private int type;
    private Setting setting;
    private Unit unit;


    public SettingTask(int type, Setting setting, Unit unit, Context context, UserFunctions function) {
        this.type = type;
        this.setting = setting;
        this.unit = unit;
        this.context = context;
        this.function = function;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        updateSettingStatus = false;
        try {
            FirebaseDatabase.getInstance().getReference().child("datas/app/" + type).setValue(setting, (error, ref) -> {
                if (error != null) {
                    Log.d("Edit Setting Firebase failed", error.getMessage(), null);
                    updateSettingStatus = false;
                } else {
                    updateSettingStatus = true;

                }
            });

            FirebaseDatabase.getInstance().getReference().child("datas/unit").setValue(unit, (error, ref) -> {
                if (error != null) {
                    Log.d("Edit Setting Firebase failed", error.getMessage(), null);
                    updateSettingStatus = false;
                } else {
                    updateSettingStatus = true;

                }
            });


            Thread.sleep(2000);
            return updateSettingStatus;
        } catch (InterruptedException e) {
            return updateSettingStatus = false;
        }

    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            function.showMessage("Updating setting successful!");


        } else {
            function.showMessage("Updating setting failed!");
        }

    }

    @Override
    protected void onCancelled() {
        function.showMessage("Setting update failed!");
    }


}

