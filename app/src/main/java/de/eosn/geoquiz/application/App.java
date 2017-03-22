package de.eosn.geoquiz.application;

import android.app.Application;

import de.eosn.geoquiz.database.DatabaseSetup;
import io.realm.Realm;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();


		// Initialize Realm
		Realm.init(getApplicationContext());

		DatabaseSetup.setup(getAssets());


	}


}
