package it.polito.tdp.poweroutages.model;

import java.time.LocalDate;

public class PowerOutages {
	private Nerc nerc;
	private LocalDate date_event_began;
	private LocalDate date_event_finished;

	public PowerOutages(Nerc nerc, LocalDate date_event_began, LocalDate date_event_finished) {
		super();
		this.nerc = nerc;
		this.date_event_began = date_event_began;
		this.date_event_finished = date_event_finished;
	}

	public Nerc getNerc() {
		return nerc;
	}

	public LocalDate getDate_event_began() {
		return date_event_began;
	}

	public LocalDate getDate_event_finished() {
		return date_event_finished;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PowerOutages [nerc_id=");
		builder.append(nerc);
		builder.append(", date_event_began=");
		builder.append(date_event_began);
		builder.append(", date_event_finished=");
		builder.append(date_event_finished);
		builder.append("]");
		return builder.toString();
	}

}
