package de.eosn.geoquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.eosn.geoquiz.R;
import de.eosn.geoquiz.database.DatabaseSetup;
import de.eosn.geoquiz.database.realmObjects.GameSettings;
import de.eosn.geoquiz.database.realmObjects.Player;
import de.eosn.geoquiz.database.realmObjects.Round;
import io.realm.Realm;


public class RoundStatisticsActivity extends AppCompatActivity implements OnMapReadyCallback,
		GoogleMap.OnMapLoadedCallback,
		View.OnClickListener {

	private static final String TAG = RoundStatisticsActivity.class.getSimpleName();

	@BindView(R.id.winner_label)
	TextView winnerTextView;
	@BindView(R.id.first_player_label_round_statistics)
	TextView firstPlayerNameTextView;
	@BindView(R.id.second_player_label_round_statistics)
	TextView secondPlayerNameTextView;
	@BindView(R.id.distance_target_label_first_player)
	TextView distanceTargetFirstPlayerTextView;
	@BindView(R.id.distance_target_label_second_player)
	TextView distanceTargetSecondPlayerTextView;
	@BindView(R.id.won_rounds_label_first_player)
	TextView wonRoundsFirstPlayerTextView;
	@BindView(R.id.won_rounds_label_second_player)
	TextView wonRoundsSecondPlayerTextView;
	@BindView(R.id.answer_task)
	TextView answerTextView;
	private Realm realm;
	private GameSettings gameSettings;
	private GoogleMap googleMap;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_round_statistics);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_round_statistics);

		ButterKnife.bind(this);

		findViewById(R.id.next_round_fab).setOnClickListener(this);

		realm = Realm.getDefaultInstance();

		gameSettings = realm.where(GameSettings.class).findAll().first();

		firstPlayerNameTextView.setText(getLongPlayerName(Player.FIRST_PLAYER));
		secondPlayerNameTextView.setText(getLongPlayerName(Player.SECOND_PLAYER));

		distanceTargetFirstPlayerTextView.setText(getDistanceToTargetText(Player.FIRST_PLAYER));
		distanceTargetSecondPlayerTextView.setText(getDistanceToTargetText(Player.SECOND_PLAYER));

		wonRoundsFirstPlayerTextView.setText(getWonRounds(Player.FIRST_PLAYER));
		wonRoundsSecondPlayerTextView.setText(getWonRounds(Player.SECOND_PLAYER));

		answerTextView.setText(gameSettings.getCurrentRound().getTask().getAnswer());

		Log.i(TAG, "onCreate: Task Creator = " +
				gameSettings.getCurrentRound().getTask().getCreator());

		String winner;
		if (gameSettings.getCurrentRound().getWinnerID() == Player.NO_PLAYER) {
			winner = getString(R.string.all_winner);
		} else {
			winner = getString(R.string.round_winner) + " ";
			winner += realm.where(Player.class)
					.equalTo(Player.PLAYER_FIELD_PLAYER_ID,
							gameSettings.getCurrentRound().getWinnerID())
					.findAll().first()
					.getPlayerName();
		}
		winnerTextView.setText(winner);


		mapFragment.getMapAsync(this);
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
				Intent intent = new Intent(RoundStatisticsActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private String getLongPlayerName(@Player.PlayerID int playerID) {
		return realm.where(Player.class)
				.equalTo(Player.PLAYER_FIELD_PLAYER_ID, playerID)
				.findAll().first()
				.getLongPlayerName(this);
	}

	private String getDistanceToTargetText(@Player.PlayerID int playerID) {
		String distanceToTargetText = getString(R.string.distance_to_target);
		float distanceToTarget = gameSettings.getCurrentRound()
				.getDistanceToTargetInMeter(playerID);
		final Formatter formatter = new Formatter(Locale.GERMAN);
		final String format = " %.2f";
		if (distanceToTarget == Float.MAX_VALUE) {
			distanceToTargetText += " " + getString(R.string.infinity);
		} else {
			distanceToTarget /= 1000; // meter -> kilometer
			distanceToTargetText += formatter.format(format, distanceToTarget);
		}
		distanceToTargetText += " km";
		return distanceToTargetText;
	}

	private String getWonRounds(@Player.PlayerID int playerID) {
		int rounds = realm.where(Round.class)
				.equalTo(Round.ROUND_FIELD_ROUND_WINNER_ID, playerID)
				.findAll()
				.size();
		return getString(R.string.won_rounds_total) + " " + rounds;
	}

	@Nullable
	private MarkerOptions getPlayerTargetMarkerOptions(@Player.PlayerID int playerID) {
		LatLng target = gameSettings.getCurrentRound().getTargetPlayer(playerID);
		if (target == null) {
			return null;
		}
		String playerName = realm.where(Player.class)
				.equalTo(Player.PLAYER_FIELD_PLAYER_ID, playerID)
				.findAll().first()
				.getPlayerName();
		return new MarkerOptions()
				.position(target)
				.title(playerName)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.googleMap = googleMap;
		googleMap.setOnMapLoadedCallback(this);

		final LatLng centerEurope = new LatLng(50.113396, 9.252183);
		// disable navigation toolbar
		googleMap.getUiSettings().setMapToolbarEnabled(false);
		googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		googleMap.setMaxZoomPreference(12);
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerEurope, 4));

	}

	@Override
	public void onMapLoaded() {
		ArrayList<MarkerOptions> markerOptions = new ArrayList<>();

		final double lat = gameSettings.getCurrentRound().getTask().getLat();
		final double lon = gameSettings.getCurrentRound().getTask().getLon();

		LatLng target = new LatLng(lat, lon);

		markerOptions.add(new MarkerOptions()
				.position(target)
				.title(getString(R.string.searched_target))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		markerOptions.add(getPlayerTargetMarkerOptions(Player.FIRST_PLAYER));
		markerOptions.add(getPlayerTargetMarkerOptions(Player.SECOND_PLAYER));

		Marker marker;
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (MarkerOptions options : markerOptions) {
			if (options != null) {
				marker = googleMap.addMarker(options);
				builder.include(marker.getPosition());
			}
		}
		LatLngBounds bounds = builder.build();

		int padding = 200; // offset from edges of the map in pixels
		googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		if (gameSettings.getCurrentRound().getRoundNr() == (gameSettings.getNumOfRounds() - 1)) {
			intent = new Intent(RoundStatisticsActivity.this, WinnerActivity.class);
		} else {
			intent = new Intent(RoundStatisticsActivity.this, BattleGroundActivity.class);
		}
		startActivity(intent);
		finish();
	}
}
