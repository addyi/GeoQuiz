package de.eosn.geoquiz.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import de.eosn.geoquiz.database.realmObjects.GameSettings;
import io.realm.Realm;

public class TimerFragment extends Fragment {


	private static final String TAG = TimerFragment.class.getSimpleName();

	private TimerCallbacks timerCallbacks;
	private TimerTask timerTask;

	// INFO Pattern: http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		// Hold a reference to the parent Activity so we can report the
		// task's current progress and results. The Android framework
		// will pass us a reference to the newly created Activity after
		// each configuration change.
		if (context instanceof TimerCallbacks) {
			timerCallbacks = (TimerCallbacks) context;
			Log.d(TAG, "onAttach: new TimerCallback attached");
		} else {
			Log.d(TAG, "onAttach: needs to be called with implemented TimerCallback Activity");
			timerCallbacks = null;
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// This method will only be called once when the retained Fragment is first created.

		// Retain this fragment across configuration changes.
		setRetainInstance(true);

		Realm realm = Realm.getDefaultInstance();
		GameSettings gameSettings = realm.where(GameSettings.class).findFirst();
		int secondsPerTerm = gameSettings.getSecondsPerTerm();

		// Create and execute the background task.
		timerTask = new TimerTask();
		timerTask.execute(secondsPerTerm);
		Log.d(TAG, "onCreate: TimerTask started with " + secondsPerTerm + " seconds per Term");

	}

	public void commitSuicide() {
		timerCallbacks = null;
		timerTask.cancel(true);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// Set the callback to null so we don't accidentally leak the Activity instance.
		timerCallbacks = null;
	}

	public interface TimerCallbacks {
		void onProgressUpdate(int percent);

		void onPostExecute();
	}

	private class TimerTask extends AsyncTask<Integer, Integer, Void> {

		// Note that we need to check if the callbacks are null in each
		// method in case they are invoked after the Activity's and
		// Fragment's onDestroy() method have been called.

		@Override
		protected void onPostExecute(Void aVoid) {
			if (timerCallbacks != null) {
				timerCallbacks.onPostExecute();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (timerCallbacks != null) {
				timerCallbacks.onProgressUpdate(values[0]);
			}
		}

		@Override
		protected Void doInBackground(Integer... params) {
			// Note that we do NOT call the callback object's methods
			// directly from the background thread, as this could result
			// in a race condition.

			int secondsPer100 = params[0];
			secondsPer100 *= 1000; // sec to milliseconds
			secondsPer100 /= 100; // break the howl waiting period into 100 pieces
			for (int i = 1; !isCancelled() && i <= 100; i++) {
				SystemClock.sleep(secondsPer100);
				publishProgress(i);
			}
			return null;
		}
	}
}
