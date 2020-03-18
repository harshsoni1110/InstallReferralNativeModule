package com.installreferralnativemodule;

import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.util.Map;

public class InstallReferralModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    public InstallReferralModule(@NonNull ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "InstallReferralModule";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        return super.getConstants();
    }

    @ReactMethod
    void getReferral (Promise promise) {
        InstallReferrerClient referrerClient;
        WritableMap result = Arguments.createMap();
        referrerClient = InstallReferrerClient.newBuilder(this.getReactApplicationContext()).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        ReferrerDetails response = null;
                        try {
                            response = referrerClient.getInstallReferrer();
                            result.putString("installTimestamp", String.valueOf(response.getInstallBeginTimestampSeconds()));
                            result.putString("installReferrer", response.getInstallReferrer());
                            result.putString("clickTimestamp", String.valueOf(response.getReferrerClickTimestampSeconds()));
                            referrerClient.endConnection();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        result.putString("Key", "Install referral calling not supported");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        result.putString("Key", "Install referral calling service unavailable");
                        break;
                }
                promise.resolve(result);
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                result.putString("Key", "Install referral calling service disconnected");
                promise.resolve(result);
            }
        });

    }
}