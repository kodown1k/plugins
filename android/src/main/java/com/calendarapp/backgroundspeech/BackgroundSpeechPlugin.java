package com.calendarapp.backgroundspeech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.calendarapp.dev.services.WakeWordForegroundService; // Import the existing service
import com.calendarapp.dev.receivers.ServiceEventBroadcaster; // Import the event broadcaster

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@CapacitorPlugin(
    name = "BackgroundSpeech",
    permissions = {
        @Permission(strings = { "android.permission.RECORD_AUDIO", "android.permission.FOREGROUND_SERVICE" })
    }
)
public class BackgroundSpeechPlugin extends Plugin {

    private static final String TAG = "BackgroundSpeechPlugin";
    private SpeechEventReceiver speechEventReceiver;

    @Override
    public void load() {
        super.load();
        // Register BroadcastReceiver to get events from the foreground service
        speechEventReceiver = new SpeechEventReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceEventBroadcaster.ACTION_SERVICE_STATE_CHANGED);
        filter.addAction(ServiceEventBroadcaster.ACTION_WAKE_WORD_DETECTED);
        bridge.getContext().registerReceiver(speechEventReceiver, filter);
        Log.d(TAG, "SpeechEventReceiver registered.");
    }

    @Override
    protected void handleOnDestroy() {
        if (speechEventReceiver != null) {
            bridge.getContext().unregisterReceiver(speechEventReceiver);
            Log.d(TAG, "SpeechEventReceiver unregistered.");
        }
        // Ensure service is stopped when plugin is destroyed (e.g., app closes)
        stopWakeWordDetection();
        super.handleOnDestroy();
    }

    @PluginMethod
    public void start(PluginCall call) {
        if (!hasPermission("RECORD_AUDIO") || !hasPermission("FOREGROUND_SERVICE")) {
            Log.d(TAG, "Requesting permissions for background speech.");
            requestAllPermissions(call);
        } else {
            _startWakeWordDetection(call);
        }
    }

    @PluginMethod
    public void stop(PluginCall call) {
        stopWakeWordDetection();
        call.resolve();
    }

    @PluginMethod
    public void updateWakePhrases(PluginCall call) {
        JSONArray phrasesArray = call.getArray("phrases");
        if (phrasesArray == null) {
            call.reject("Must provide 'phrases' array.");
            return;
        }

        List<String> phrasesList = new ArrayList<>();
        for (int i = 0; i < phrasesArray.length(); i++) {
            try {
                phrasesList.add(phrasesArray.getString(i));
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing phrase from array", e);
            }
        }

        Intent serviceIntent = new Intent(getContext(), WakeWordForegroundService.class);
        serviceIntent.setAction(WakeWordForegroundService.ACTION_UPDATE_PHRASES);
        serviceIntent.putExtra(WakeWordForegroundService.EXTRA_PHRASES, phrasesList.toArray(new String[0]));
        getContext().startService(serviceIntent);
        call.resolve();
    }

    private void _startWakeWordDetection(PluginCall call) {
        Intent serviceIntent = new Intent(getContext(), WakeWordForegroundService.class);
        serviceIntent.setAction(WakeWordForegroundService.ACTION_START);
        getContext().startService(serviceIntent); // Use startService for foreground service
        call.resolve();
    }

    private void stopWakeWordDetection() {
        Intent serviceIntent = new Intent(getContext(), WakeWordForegroundService.class);
        serviceIntent.setAction(WakeWordForegroundService.ACTION_STOP);
        getContext().startService(serviceIntent); // Send intent to stop service
    }

    @PermissionCallback
    private void permissionsCallback(PluginCall call) {
        if (hasPermission("RECORD_AUDIO") && hasPermission("FOREGROUND_SERVICE")) {
            Log.d(TAG, "Permissions granted. Starting background speech.");
            _startWakeWordDetection(call);
        } else {
            Log.d(TAG, "Permissions denied for background speech.");
            call.reject("Permissions denied");
        }
    }

    // Inner class to receive broadcasts from the foreground service
    private class SpeechEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            JSObject data = new JSObject();

            if (ServiceEventBroadcaster.ACTION_SERVICE_STATE_CHANGED.equals(action)) {
                boolean isRunning = intent.getBooleanExtra("isRunning", false);
                data.put("isRunning", isRunning);
                notifyListeners("serviceStateChange", data);
                Log.d(TAG, "Service state changed: " + isRunning);
            } else if (ServiceEventBroadcaster.ACTION_WAKE_WORD_DETECTED.equals(action)) {
                String wakePhrase = intent.getStringExtra("wakePhrase");
                String fullText = intent.getStringExtra("fullText");
                data.put("wakePhrase", wakePhrase);
                data.put("fullText", fullText);
                notifyListeners("wakeWordDetected", data);
                Log.d(TAG, "Wake word detected: " + wakePhrase + ", Full Text: " + fullText);
            }
        }
    }
}