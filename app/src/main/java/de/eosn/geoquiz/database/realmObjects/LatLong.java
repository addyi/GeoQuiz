package de.eosn.geoquiz.database.realmObjects;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import io.realm.RealmObject;

public class LatLong extends RealmObject {

	@Nullable
	private Double latitude = null;
	@Nullable
	private Double longitude = null;

	public LatLong() {

	}

	public LatLong(@Nullable LatLng latLng) {
		if (latLng != null) {
			this.latitude = latLng.latitude;
			this.longitude = latLng.longitude;
		}
	}

	@Nullable
	Double getLatitude() {
		return latitude;
	}

	@Nullable
	Double getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return "LatLong{" +
				"lat=" + latitude +
				", long=" + longitude +
				'}';
	}
}
