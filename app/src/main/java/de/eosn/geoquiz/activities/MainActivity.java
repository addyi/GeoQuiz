package de.eosn.geoquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.eosn.geoquiz.R;
import de.eosn.geoquiz.database.realmObjects.GameSettings;
import de.eosn.geoquiz.database.realmObjects.Player;
import io.realm.Realm;


public class MainActivity extends AppCompatActivity {

	private final GameSettings gameSettings = new GameSettings();

	@BindView(R.id.time_text)
	EditText timeEditText;

	@BindView(R.id.rounds_text)
	EditText roundsEditText;

	@BindView(R.id.first_player_name)
	EditText nameFirstPlayerEditText;

	@BindView(R.id.second_player_name)
	EditText nameSecondPlayerEditText;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);
		init();
	}

	private void init() {
		setTimeText(gameSettings.getSecondsPerTerm());
		setRoundsText(gameSettings.getNumOfRounds());
		timeEditText.setEnabled(false);
		roundsEditText.setEnabled(false);
	}


	private void setTimeText(int secondsPerRound) {
		String timeText = secondsPerRound + " " + getString(R.string.abbreviation_seconds);
		timeEditText.setText(timeText);
	}


	private void setRoundsText(int numOfRounds) {
		roundsEditText.setText(String.valueOf(numOfRounds));
	}

	@OnClick(R.id.decrement_time)
	void decrementTime() {
		setTimeText(gameSettings.decrementSecondsPerTerm());
	}

	@OnClick(R.id.increment_time)
	void incrementTime() {
		setTimeText(gameSettings.incrementSecondsPerTerm());
	}

	@OnClick(R.id.decrement_rounds)
	void decrementRounds() {
		setRoundsText(gameSettings.decrementRounds());
	}

	@OnClick(R.id.increment_rounds)
	void incrementRounds() {
		setRoundsText(gameSettings.incrementRounds());
	}

	@OnClick(R.id.start_game_button)
	void startGame() {

		String nameFirstPlayer = getPlayerName(nameFirstPlayerEditText);
		String nameSecondPlayer = getPlayerName(nameSecondPlayerEditText);

		if (nameFirstPlayer != null && nameSecondPlayer != null) {
			if (nameFirstPlayer.equals(nameSecondPlayer)) {
				nameFirstPlayerEditText.setError(getString(R.string.equal_names_error));
				nameSecondPlayerEditText.setError(getString(R.string.equal_names_error));
			} else {
				gameSettings.addPlayer(Player.FIRST_PLAYER, nameFirstPlayer);
				gameSettings.addPlayer(Player.SECOND_PLAYER, nameSecondPlayer);

				Realm realm = Realm.getDefaultInstance();

				realm.beginTransaction();
				realm.copyToRealm(gameSettings);
				realm.commitTransaction();

				Intent intent = new Intent(MainActivity.this, BattleGroundActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}

	@Nullable
	private String getPlayerName(EditText editText) {
		String name = editText.getText().toString().trim();
		if (name.length() < 3 || name.length() > 12) {
			editText.setError(getString(R.string.name_constraints));
			return null;
		}
		return name;
	}


}
