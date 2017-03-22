package de.eosn.geoquiz.database.realmObjects;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import de.eosn.geoquiz.R;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Player extends RealmObject {


	// TODO: 15.03.17 Find better solution for player ids. Variable num of players would be nice
	public static final int NO_PLAYER = -1;
	public static final int FIRST_PLAYER = 0;
	public static final int SECOND_PLAYER = 1;
	public static final String PLAYER_FIELD_PLAYER_ID = "playerID";
	@PrimaryKey
	@PlayerID
	private int playerID;
	@NonNull
	private String playerName;

	Player(@PlayerID int playerID, @NonNull String playerName) {
		this.playerID = playerID;
		this.playerName = playerName;
	}

	public Player() {
		this(NO_PLAYER, "");
	}

	@PlayerID
	public int getPlayerID() {
		return playerID;
	}


	@NonNull
	public String getPlayerName() {
		return playerName;
	}

	@NonNull
	public String getLongPlayerName(Context context) {
		return context.getString(R.string.player) + " "
				+ (getPlayerID() + 1) + ": " + getPlayerName();
	}

	@IntDef({NO_PLAYER, FIRST_PLAYER, SECOND_PLAYER})
	public @interface PlayerID {
	}
}
