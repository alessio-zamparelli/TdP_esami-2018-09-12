package it.polito.tdp.poweroutages.model;

public class Correlation {
	private Nerc n1;
	private Nerc n2;
	private int c;

	public Correlation(Nerc n1, Nerc n2, int c) {
		super();
		this.n1 = n1;
		this.n2 = n2;
		this.c = c;
	}

	public Nerc getN1() {
		return n1;
	}

	public Nerc getN2() {
		return n2;
	}

	public int getC() {
		return c;
	}

}
