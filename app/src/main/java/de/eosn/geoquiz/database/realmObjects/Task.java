package de.eosn.geoquiz.database.realmObjects;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Task extends RealmObject {


	@SerializedName("question")
	@Expose
	private String question = "";
	@SerializedName("lat")
	@Expose
	private Double lat = 0.0;
	@SerializedName("long")
	@Expose
	private Double lon = 0.0;
	@SerializedName("answer")
	@Expose
	private String answer = "";
	@SerializedName("creator")
	@Expose
	private String creator = "";

	public String getQuestion() {
		return question;
	}

	public Double getLat() {
		return lat;
	}

	public Double getLon() {
		return lon;
	}

	public String getAnswer() {
		return answer;
	}

	public String getCreator() {
		return creator;
	}

	@Override
	public String toString() {
		return "Task{" +
				"question='" + question + '\'' +
				", lat=" + lat +
				", lon=" + lon +
				", answer='" + answer + '\'' +
				", creator='" + creator + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Task task = (Task) o;

		return question.equals(task.question)
				&& lat.equals(task.lat)
				&& lon.equals(task.lon)
				&& answer.equals(task.answer)
				&& creator.equals(task.creator);

	}

	@Override
	public int hashCode() {
		int result = question.hashCode();
		result = 31 * result + lat.hashCode();
		result = 31 * result + lon.hashCode();
		result = 31 * result + answer.hashCode();
		result = 31 * result + creator.hashCode();
		return result;
	}
}