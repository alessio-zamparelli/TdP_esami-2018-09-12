package it.polito.tdp.poweroutages.db;

import java.util.stream.Collectors;

import it.polito.tdp.poweroutages.model.Nerc;

public class TestDAO {

	public static void main(String[] args) {

		PowerOutagesDAO dao = new PowerOutagesDAO();

		System.out.println(dao.loadAllNercs());
		dao.loadAllPowerOutages(dao.loadAllNercs().parallelStream().collect(Collectors.toMap(Nerc::getId, a->a))).forEach(a->System.out.println(a));
		}

}
