package com.qppd.smartwaterguard.Tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.qppd.smartwaterguard.Classes.User;
import com.qppd.smartwaterguard.Globals.UserGlobal;
import com.qppd.smartwaterguard.Libs.Functionz.UserFunctions;
import com.qppd.smartwaterguard.Libs.IntentManager.IntentManagerClass;
import com.qppd.smartwaterguard.MonitorActivity;

public class LoginTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private UserFunctions function;


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private boolean loginStatus = false;

    private String uid;

    public LoginTask(String uid, Context context, UserFunctions function) {
        this.uid = uid;
        this.context = context;
        this.function = function;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        loginStatus = false;
        try {

            firebaseDatabase.getReference().child("datas/account/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    loginStatus = true;
                    UserGlobal.setUser_id(uid);
                    //function.showMessage("asdjaksd");
                    UserGlobal.setUser(snapshot.getValue(User.class));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loginStatus = false;
                }
            });



            Thread.sleep(2000);
            return loginStatus;
        } catch (InterruptedException e) {
            return loginStatus = false;
        }

    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            function.showMessage("LOGIN SUCCESS!");
            IntentManagerClass.intentsify(((Activity) context), MonitorActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ((Activity) context).finish();

        } else {
            function.showMessage("LOGIN FAILED!");
        }
    }

    @Override
    protected void onCancelled() {
        function.showMessage("LOGIN CANCELLED");
    }
}

