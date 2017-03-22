package de.eosn.geoquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.eosn.geoquiz.R;
import de.eosn.geoquiz.database.DatabaseSetup;
import de.eosn.geoquiz.database.realmObjects.Player;
import de.eosn.geoquiz.database.realmObjects.Round;
import io.realm.Realm;

public class WinnerActivity extends AppCompatActivity {

	@BindView(R.id.first_winner)
	TextView firstWinnerTextView;
	@BindView(R.id.second_winner)
	TextView secondWinnerTextView;
	@BindView(R.id.first_winner_rounds)
	TextView firstWinnerRoundsTextView;
	@BindView(R.id.second_winner_rounds)
	TextView secondWinnerRoundsTextView;
	private String wonRoundsText;
	private Realm realm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_winner);

		ButterKnife.bind(this);

		realm = Realm.getDefaultInstance();

		int wonRoundsTotalFirstPlayer = getWonRoundsTotal(Player.FIRST_PLAYER);
		int wonRoundsTotalSecondPlayer = getWonRoundsTotal(Player.SECOND_PLAYER);

		wonRoundsText = getString(R.string.winner_total) + " ";


		if (wonRoundsTotalFirstPlayer == wonRoundsTotalSecondPlayer) {
			setEqualWinner();
		} else {
			if (wonRoundsTotalFirstPlayer > wonRoundsTotalSecondPlayer) {
				setFirstWinner(Player.FIRST_PLAYER);
				setSecondWinner(Player.SECOND_PLAYER);
			} else {
				setFirstWinner(Player.SECOND_PLAYER);
				setSecondWinner(Player.FIRST_PLAYER);
			}
		}
	}

	private void setFirstWinner(@Player.PlayerID int playerID) {
		String firstWinnerText = getString(R.string.first_winner) + " ";
		firstWinnerText += getPlayerName(playerID);

		String wonRounds = wonRoundsText;
		wonRounds += getWonRoundsTotal(playerID);

		firstWinnerTextView.setText(firstWinnerText);
		firstWinnerRoundsTextView.setText(wonRounds);
	}

	private void setSecondWinner(@Player.PlayerID int playerID) {
		String secondWinnerText = getString(R.string.second_winner) + " ";
		secondWinnerText += getPlayerName(playerID);

		String wonRounds = wonRoundsText;
		wonRounds += getWonRoundsTotal(playerID);

		secondWinnerTextView.setText(secondWinnerText);
		secondWinnerRoundsTextView.setText(wonRounds);
	}

	private void setEqualWinner() {
		final String equalWinnerText = getString(R.string.winner) + " ";
		String firstPlayer = equalWinnerText + getPlayerName(Player.FIRST_PLAYER);
		String secondPlayer = equalWinnerText + getPlayerName(Player.SECOND_PLAYER);
		String wonRounds = wonRoundsText;
		wonRounds += getWonRoundsTotal(Player.FIRST_PLAYER);

		firstWinnerTextView.setText(firstPlayer);
		firstWinnerRoundsTextView.setText(wonRounds);
		secondWinnerTextView.setText(secondPlayer);
		secondWinnerRoundsTextView.setText(wonRounds);
	}

	private int getWonRoundsTotal(@Player.PlayerID int playerID) {
		return realm.where(Round.class)
				.equalTo(Round.ROUND_FIELD_ROUND_WINNER_ID, playerID)
				.findAll()
				.size();
	}

	private String getPlayerName(@Player.PlayerID int playerID) {
		return realm.where(Player.class)
				.equalTo(Player.PLAYER_FIELD_PLAYER_ID, playerID)
				.findFirst()
				.getPlayerName();
	}

	@OnClick(R.id.next_game)
	void nextGame() {
		DatabaseSetup.setup(getAssets());
		Intent intent = new Intent(WinnerActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
