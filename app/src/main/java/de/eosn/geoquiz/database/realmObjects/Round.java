package de.eosn.geoquiz.database.realmObjects;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Round extends RealmObject {

	public final static String ROUND_FIELD_ROUND_WINNER_ID = "winnerID";
	private static final String TAG = Round.class.getSimpleName();
	@PrimaryKey
	private int roundNr;
	@NonNull
	private Task task;
	private RealmList<LatLong> targetsPlayer = new RealmList<>();
	@Player.PlayerID
	private int winnerID = Player.NO_PLAYER;

	public Round() {
		task = new Task(); // Default Constructor because of Realm
	}

	Round(int roundNr, @NonNull Task task) {
		this.roundNr = roundNr;
		this.task = task;
	}


	private void determineWinner() {
		switch (targetsPlayer.size()) {
			case 2:
				float distanceToTargetFirstPlayer = determineDistance(targetsPlayer
						.get(Player.FIRST_PLAYER));
				float distanceToTargetSecondPlayer = determineDistance(targetsPlayer
						.get(Player.SECOND_PLAYER));

				if (distanceToTargetFirstPlayer == distanceToTargetSecondPlayer) {
					winnerID = Player.NO_PLAYER;
				} else {
					if (distanceToTargetFirstPlayer > distanceToTargetSecondPlayer) {
						winnerID = Player.SECOND_PLAYER;
					} else {
						winnerID = Player.FIRST_PLAYER;
					}
				}
				break;
			case 1:
				winnerID = Player.NO_PLAYER;
				break;

			default:
				throw new IllegalStateException("Undefined State of Player Targets: "
						+ targetsPlayer.size());
		}
	}

	private float determineDistance(@NonNull LatLong latLong) {

		if (latLong.getLatitude() == null || latLong.getLongitude() == null) {
			return Float.MAX_VALUE;
		}

		float[] distance = new float[1];

		Location.distanceBetween(
				latLong.getLatitude(),
				latLong.getLongitude(),
				task.getLat(),
				task.getLon(),
				distance
		);
		return distance[0];
	}

	public int getRoundNr() {
		return roundNr;
	}

	@NonNull
	public Task getTask() {
		return task;
	}


	public void setTargetPlayer(@Player.PlayerID int playerID, @NonNull LatLong latLong) {
		Log.d(TAG, "setTargetPlayer: player=" + playerID + " latLong=" + latLong);
		targetsPlayer.add(playerID, latLong);
		determineWinner();
	}

	@Nullable
	public LatLng getTargetPlayer(@Player.PlayerID int playerID) {
		LatLong target = targetsPlayer.get(playerID);
		if (target.getLatitude() == null || target.getLongitude() == null) {
			return null;
		}
		return new LatLng(target.getLatitude(), target.getLongitude());
	}

	@Player.PlayerID
	public int getWinnerID() {
		return winnerID;
	}

	public float getDistanceToTargetInMeter(@Player.PlayerID int playerID) {
		return determineDistance(targetsPlayer.get(playerID));
	}

	@Override
	public String toString() {
		return "Round{" +
				"roundNr=" + roundNr +
				", task=" + task +
				", targetsPlayer=" + targetsPlayer +
				", winnerID=" + winnerID +
				'}';
	}
}
