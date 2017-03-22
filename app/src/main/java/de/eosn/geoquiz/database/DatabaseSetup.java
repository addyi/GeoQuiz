package de.eosn.geoquiz.database;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import de.eosn.geoquiz.database.realmObjects.Task;
import io.realm.Realm;

public class DatabaseSetup {

	private static final String TAG = DatabaseSetup.class.getSimpleName();


	public static void setup(AssetManager assetManager) {
		Gson gson = new Gson();
		Task[] tasks;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					assetManager.open("tasks.json"), "utf-8"));

			tasks = gson.fromJson(reader, Task[].class);

			Realm realm = Realm.getDefaultInstance();

			realm.beginTransaction();
			realm.deleteAll();
			realm.commitTransaction();

			realm.beginTransaction();
			for (Task task : tasks) {
				realm.copyToRealm(task);
			}
			realm.commitTransaction();


		} catch (Exception e) {
			Log.e(TAG, "retrieveRandomTask: ", e);
		}
	}
}
