package com.qppd.smartwaterguard.Tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.qppd.smartwaterguard.Classes.Control;
import com.qppd.smartwaterguard.Classes.Device;
import com.qppd.smartwaterguard.Classes.Process;
import com.qppd.smartwaterguard.Libs.Functionz.UserFunctions;
import com.qppd.smartwaterguard.Libs.Imagez.ImageBase64;
import com.qppd.smartwaterguard.R;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StartTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private UserFunctions function;
    private boolean startOperationStatus = false;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private ImageBase64 imageBase64 = new ImageBase64();

    public StartTask(Context context, UserFunctions function) {

        this.context = context;
        this.function = function;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

//        startOperationStatus = false;
//        try {
//            Bitmap bitmap = imageBase64.loadDrawableToBitmap(context, R.drawable.applogo);
//            Device device = new Device(imageBase64.enCode(bitmap), 0, 0, 0, 0);
//            FirebaseDatabase.getInstance().getReference().child("incubator/device/readings").setValue(device, (error, ref) -> {
//                if (error != null) {
//                    Log.d("Resetting Incubator failed", error.getMessage(), null);
//                    //startOperationStatus = false;
//                } else {
//                    //startOperationStatus = true;
//
//                }
//            });
//
//            Control control = new Control(true, true);
//
//            FirebaseDatabase.getInstance().getReference().child("incubator/device/controls").setValue(control, (error, ref) -> {
//                if (error != null) {
//                    Log.d("Resetting Incubator failed", error.getMessage(), null);
//                    startOperationStatus = false;
//                } else {
//                    startOperationStatus = true;
//
//                }
//            });
//
//            Date currentDate = new Date();
//            // Format the date as "MM/dd/yyyy"
//            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//            String formattedDate = dateFormat.format(currentDate);
//
//            Process process = new Process(formattedDate, 1);
//
//            FirebaseDatabase.getInstance().getReference().child("incubator/device/process").setValue(process, (error, ref) -> {
//                if (error != null) {
//                    Log.d("Starting Incubator failed", error.getMessage(), null);
//                    startOperationStatus = false;
//                } else {
//                    startOperationStatus = true;
//
//                }
//            });
//
//
//
//
//            Thread.sleep(2000);
//            return startOperationStatus;
//        } catch (InterruptedException e) {
//            return startOperationStatus = false;
//        }
        return null;

    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            function.showMessage("INCUBATOR STARTING!");


        } else {
            function.showMessage("STARTING INCUBATOR FAILED!");
        }

    }

    @Override
    protected void onCancelled() {
        function.showMessage("STARTING INCUBATOR CANCELLED");
    }


}

