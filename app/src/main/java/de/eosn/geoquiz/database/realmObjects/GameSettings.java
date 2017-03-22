package de.eosn.geoquiz.database.realmObjects;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashSet;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class GameSettings extends RealmObject {


	private static final String TAG = GameSettings.class.getSimpleName();

	private static final int ROUNDS_STEP = 1;
	private static final int MIN_NUM_OF_ROUNDS = ROUNDS_STEP;
	// MAX_NUM_OF_ROUNDS needs to be equal or smaller than available task
	private static final int MAX_NUM_OF_ROUNDS = ROUNDS_STEP * 11;
	private static final int SECONDS_STEP = 5;
	private static final int MIN_SECONDS_PER_TERM = SECONDS_STEP;
	private static final int MAX_SECONDS_PER_TERM = SECONDS_STEP * 24;


	private int numOfRounds = 5;
	private int secondsPerTerm = 30;

	@Nullable
	private Round currentRound = null;

	@Nullable
	private Player currentPlayer = null;

	private RealmList<Round> rounds = new RealmList<>();
	private RealmList<Player> players = new RealmList<>();

	public GameSettings() {

	}

	private void initializeGame() {
		if (players.size() != 2) {
			throw new IllegalStateException("Players missing");
		}

		Task[] randomTasks = getRandTasks(numOfRounds);

		for (int i = 0; i < randomTasks.length; i++) {
			Round round = new Round(i, randomTasks[i]);
			if (round.getRoundNr() != i) {
				throw new IllegalStateException("RoundNr not equal to position in ArrayList: " +
						"roundNr=" + round.getRoundNr() + ", i=" + i);
			}
			rounds.add(i, round);
		}

		currentRound = rounds.first();
		currentPlayer = players.first();
	}


	private Task[] getRandTasks(int numOfTasks) {
		RealmResults<Task> tasks = Realm.getDefaultInstance().where(Task.class).findAll();
		if (tasks.size() < MAX_NUM_OF_ROUNDS
				|| tasks.size() < numOfTasks
				|| numOfTasks > MAX_NUM_OF_ROUNDS) {
			throw new IllegalStateException("tasks.size=" + tasks.size()
					+ " numOfTasks=" + numOfTasks);
		}

		HashSet<Task> randomTasks = new HashSet<>();

		Random random = new Random();
		int newRand;
		while (randomTasks.size() < numOfTasks) {
			newRand = random.nextInt(tasks.size());
			randomTasks.add(tasks.get(newRand));
		}
		if (randomTasks.size() != numOfTasks) throw new AssertionError();
		return randomTasks.toArray(new Task[0]);
	}

	public void addPlayer(@Player.PlayerID int playerID, @NonNull String playerName) {
		players.add(playerID, new Player(playerID, playerName));
	}

	private void nextPlayer() {
		if (currentPlayer == null) {
			throw new IllegalStateException("Current Player undefined");
		} else {
			switch (currentPlayer.getPlayerID()) {
				case Player.FIRST_PLAYER:
					currentPlayer = players.get(Player.SECOND_PLAYER);
					break;
				case Player.SECOND_PLAYER:
					currentPlayer = players.get(Player.FIRST_PLAYER);
					break;
				case Player.NO_PLAYER:
					throw new IllegalStateException("Player with \"NO_PLAYER\"-ID");
				default:
					throw new IllegalStateException("Undefined PlayerID Code");
			}
		}
	}

	private void nextRound() {
		if (currentRound == null) {
			throw new IllegalStateException("Current round undefined");
		} else {
			Log.d(TAG, "nextRound: winner=" + currentRound.getWinnerID());
			int nextRoundNr = currentRound.getRoundNr() + 1;
			if (nextRoundNr >= rounds.size()) {
				currentRound = null;
			} else {
				currentRound = rounds.get(nextRoundNr);
			}
		}
	}

	public void nextTerm() {
		if (currentPlayer == null && currentRound == null) {
			initializeGame();
		} else {
			switch (currentPlayer.getPlayerID()) {
				case Player.FIRST_PLAYER:
					nextPlayer();
					break;
				case Player.SECOND_PLAYER:
					nextPlayer();
					nextRound();
					break;
				case Player.NO_PLAYER:
					throw new IllegalStateException("Player with \"NO_PLAYER\"-ID");
				default:
					throw new IllegalStateException("Undefined PlayerID Code");
			}
		}
	}

	public int decrementSecondsPerTerm() {
		if (secondsPerTerm > MIN_SECONDS_PER_TERM) {
			secondsPerTerm = secondsPerTerm - SECONDS_STEP;
		}
		return getSecondsPerTerm();
	}

	public int incrementSecondsPerTerm() {
		if (secondsPerTerm < MAX_SECONDS_PER_TERM) {
			secondsPerTerm = secondsPerTerm + SECONDS_STEP;
		}
		return getSecondsPerTerm();
	}

	public int decrementRounds() {
		if (numOfRounds > MIN_NUM_OF_ROUNDS) {
			numOfRounds = numOfRounds - ROUNDS_STEP;
		}
		return getNumOfRounds();
	}


	public int incrementRounds() {
		if (numOfRounds < MAX_NUM_OF_ROUNDS) {
			numOfRounds = numOfRounds + ROUNDS_STEP;
		}
		return getNumOfRounds();
	}

	@Nullable
	public Round getCurrentRound() {
		return currentRound;
	}

	@Nullable
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public int getNumOfRounds() {
		return numOfRounds;
	}

	public int getSecondsPerTerm() {
		return secondsPerTerm;
	}


}
