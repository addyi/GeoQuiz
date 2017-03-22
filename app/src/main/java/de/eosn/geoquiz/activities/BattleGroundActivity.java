package de.eosn.geoquiz.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.eosn.geoquiz.R;
import de.eosn.geoquiz.database.DatabaseSetup;
import de.eosn.geoquiz.database.realmObjects.GameSettings;
import de.eosn.geoquiz.database.realmObjects.LatLong;
import de.eosn.geoquiz.database.realmObjects.Player;
import de.eosn.geoquiz.fragments.TimerFragment;
import io.realm.Realm;

public class BattleGroundActivity extends AppCompatActivity implements OnMapReadyCallback,
		GoogleMap.OnMapClickListener,
		GoogleMap.OnMapLoadedCallback,
		TimerFragment.TimerCallbacks {

	private static final String TAG = BattleGroundActivity.class.getSimpleName();
	private static final String TAG_TIMER_FRAGMENT = "timer_fragment";
	private static final String MARKER_POSITION_KEY = "It hurts, when IP";
	private static final String BATTLE_STATUS_KEY = "Whatever works";
	private static final int MAP_MIN_ZOOM_PREFERENCE = 4;
	private static final int MAP_MAX_ZOOM_PREFERENCE = 6;
	private GoogleMap googleMap;
	private Realm realm;
	private GameSettings gameSettings;
	private FragmentManager fragmentManager;
	private TimerFragment timerFragment;
	@Nullable
	private LatLng markerPosition = null;
	@Nullable
	private AlertDialog alertDialog = null;
	private ProgressBar progressBar;
	private boolean instanceChange = false;
	private BattleStatus battleStatus = BattleStatus.START;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battle_ground);

		realm = Realm.getDefaultInstance();
		gameSettings = realm.where(GameSettings.class).findFirst();

		if (savedInstanceState != null) {
			markerPosition = savedInstanceState.getParcelable(MARKER_POSITION_KEY);
			battleStatus = (BattleStatus) savedInstanceState.getSerializable(BATTLE_STATUS_KEY);
		}
		initGUI();
		statusHandler();
	}

	private void initGUI() {
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);

		fragmentManager = getSupportFragmentManager();

		// Obtain the SupportMapFragment and get notified when the googleMap is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
				.findFragmentById(R.id.map_battle_ground);
		mapFragment.getMapAsync(this);


		if (battleStatus == BattleStatus.START
				|| battleStatus == BattleStatus.INFO
				|| battleStatus == BattleStatus.IDLE) {

			timerFragment = (TimerFragment) fragmentManager
					.findFragmentByTag(TAG_TIMER_FRAGMENT);

			// If the Fragment is non-null, the timer is running and was initialized
			// before an instanceStateChange occurred
			if (timerFragment == null) {
				timerFragment = new TimerFragment();
				fragmentManager.beginTransaction()
						.add(timerFragment, TAG_TIMER_FRAGMENT)
						.commit();
			}
		}

		addListeners();
	}

	private void statusHandler() {
		Log.d(TAG, "statusHandler: battleStatus" + battleStatus);
		if (!instanceChange) {
			switch (battleStatus) {
				case START:
					realm.beginTransaction();
					gameSettings.nextTerm();
					realm.commitTransaction();
					setBattleStatus(BattleStatus.INFO);
					break;
				case INFO:
					showDialog(gameSettings.getCurrentPlayer().getLongPlayerName(this),
							gameSettings.getCurrentRound().getTask().getQuestion(),
							BattleStatus.IDLE);
					break;
				case IDLE:

					break;
				case DONE:
					deleteTimer();
					showDialog(getString(R.string.point_registered),
							getString(R.string.point_registered_msg),
							BattleStatus.SAVE);
					break;
				case TIME_UP:
					showDialog(getString(R.string.time_is_up),
							getString(R.string.time_is_up_msg),
							BattleStatus.SAVE);
					break;
				case SAVE:
					saveTermInfos();
					break;
				case INTENT:
					intent();
					break;
				default:
					throw new IllegalStateException("WTF BattleState is not defined?");
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		instanceChange = true;
		dialogRemover();
		outState.putParcelable(MARKER_POSITION_KEY, markerPosition);
		outState.putSerializable(BATTLE_STATUS_KEY, battleStatus);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.end_game:
				DatabaseSetup.setup(getAssets());
				Intent intent = new Intent(BattleGroundActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void addListeners() {
		FloatingActionButton questionFab = (FloatingActionButton) findViewById(R.id.question_fab);
		questionFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setBattleStatus(BattleStatus.INFO);
			}
		});

		FloatingActionButton finishedTask = (FloatingActionButton) findViewById(R.id.done_fab);
		finishedTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setBattleStatus(BattleStatus.DONE);
			}
		});
	}

	private void showDialog(String title, String message, final BattleStatus battleStatus) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.ok, null);
		builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				setBattleStatus(battleStatus);
			}
		});
		dialogRemover();
		alertDialog = builder.show();
	}

	private void dialogRemover() {
		if (alertDialog != null) {
			alertDialog.dismiss();
			alertDialog = null;
		}
	}

	private void setBattleStatus(BattleStatus battleStatus) {
		this.battleStatus = battleStatus;
		statusHandler();
	}

	private void saveTermInfos() {
		if (!instanceChange) {
			realm.beginTransaction();
			gameSettings.getCurrentRound()
					.setTargetPlayer(
							gameSettings.getCurrentPlayer().getPlayerID(),
							new LatLong(markerPosition));// change to LatLong aus dem instance state
			realm.commitTransaction();
			setBattleStatus(BattleStatus.INTENT);
		}
	}

	private void intent() {
		if (!instanceChange) {
			Intent intent;
			switch (gameSettings.getCurrentPlayer().getPlayerID()) {
				case Player.FIRST_PLAYER:
					// Not nice but i don't care
					intent = new Intent(BattleGroundActivity.this, BattleGroundActivity.class);
					break;
				case Player.SECOND_PLAYER:
					intent = new Intent(BattleGroundActivity.this, RoundStatisticsActivity.class);
					break;
				case Player.NO_PLAYER:
					throw new IllegalStateException("Player with \"NO_PLAYER\"-ID");
				default:
					throw new IllegalStateException("Undefined PlayerID Code");
			}
			dialogRemover(); // to be sure no dialog is open, should be unnecessary -> battleState
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.googleMap = googleMap;
		final LatLng centerEurope = new LatLng(50.113396, 9.252183);
		//googleMap.addMarker(new MarkerOptions().position(centerEurope).title("EU"));
		googleMap.setOnMapClickListener(this);
		googleMap.setOnMapLoadedCallback(this);
		googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		// disable navigation toolbar
		googleMap.getUiSettings().setMapToolbarEnabled(false);
		googleMap.setMinZoomPreference(MAP_MIN_ZOOM_PREFERENCE);
		googleMap.setMaxZoomPreference(MAP_MAX_ZOOM_PREFERENCE);
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(centerEurope));
	}

	@Override
	public void onMapClick(LatLng latLng) {
		googleMap.clear();
		googleMap.addMarker(new MarkerOptions().position(latLng));
		this.markerPosition = latLng;
	}

	@Override
	public void onMapLoaded() {
		if (markerPosition != null) {
			googleMap.addMarker(new MarkerOptions().position(markerPosition));
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition,
					MAP_MIN_ZOOM_PREFERENCE));
		}
	}

	@Override
	public void onProgressUpdate(int percent) {
		progressBar.setProgress(percent);
	}

	@Override
	public void onPostExecute() {
		setBattleStatus(BattleStatus.TIME_UP);
	}

	private void deleteTimer() {
		timerFragment.commitSuicide();
		fragmentManager.beginTransaction().remove(timerFragment).commit();
	}

	private enum BattleStatus {
		START, // First Start of the Activity
		INFO, // Show InfoDialog
		IDLE, // Idle
		DONE, // Show DoneDialog
		TIME_UP, // Show TimeUpDialog
		SAVE, // ready to save Data
		INTENT // ready to intent
	}
}
