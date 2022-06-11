package com.cominatyou.silverpoint.activityresources.mainactivity;

import android.content.Context;
import android.util.Log;

import com.cominatyou.silverpoint.MainActivity;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityMainBinding;
import com.cominatyou.silverpoint.remoteendpoint.DiscordQueryResult;
import com.cominatyou.silverpoint.remoteendpoint.NonWorkerDiscordStatusQuerier;
import com.google.android.material.snackbar.Snackbar;

public class SwipeRefreshLayout {
    public static void onRefresh(Context context, MainActivity activity, ActivityMainBinding binding) {
        // Run in a new thread because running network code on the UI thread is an abhorrent idea
        new Thread(() -> {
            Log.d("SwipeToRefreshThread", "Running swipe to refresh callback");
            DiscordQueryResult result = NonWorkerDiscordStatusQuerier.run(context);

            if (result != DiscordQueryResult.SUCCESS) activity.runOnUiThread(() -> {
                final String message = result == DiscordQueryResult.FAILURE ? "Something went wrong. Give it a try later." : "You'll need to update the app before you can do this.";
                final Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
                snackbar.show();
            });
            activity.runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
        }).start();
    }
}
