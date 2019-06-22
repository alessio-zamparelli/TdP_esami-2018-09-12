package it.polito.tdp.poweroutages.model;

public class TestModel {
	public static void main(String[] args) {
		Model m = new Model();
		m.creaGrafo();
		m.simula(10);
	}
}
