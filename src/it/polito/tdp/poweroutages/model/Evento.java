package it.polito.tdp.poweroutages.model;

import java.time.LocalDate;
import java.time.LocalDate;

public class Evento implements Comparable<Evento> {

	private Nerc nerc;
	private LocalDate data;
	private TipoEvento tipoEvento;
	private PowerOutages po;
	private Nerc donatore;

	public enum TipoEvento {
		INIZIO_INTERRUZIONE, FINE_INTERRUZIONE
	}

	public Evento(Nerc nerc, LocalDate data, TipoEvento tipoEvento, PowerOutages po, Nerc donatore) {
		this.nerc = nerc;
		this.data = data;
		this.tipoEvento = tipoEvento;
		this.po = po;
		this.donatore = donatore;
	}

	public Nerc getNerc() {
		return nerc;
	}
	
	public Nerc getDonatore() {
		return this.donatore;
	}

	public PowerOutages getPowerOutages() {
		return this.po;
	}
	public LocalDate getData() {
		return data;
	}

	public TipoEvento getTipoEvento() {
		return tipoEvento;
	}

	@Override
	public int compareTo(Evento o) {
		return this.data.compareTo(o.data);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Evento [nerc=");
		builder.append(nerc);
		builder.append(", data=");
		builder.append(data);
		builder.append(", tipoEvento=");
		builder.append(tipoEvento);
		builder.append(", po=");
		builder.append(po);
		builder.append(", donatore=");
		builder.append(donatore);
		builder.append("]");
		return builder.toString();
	}
}
