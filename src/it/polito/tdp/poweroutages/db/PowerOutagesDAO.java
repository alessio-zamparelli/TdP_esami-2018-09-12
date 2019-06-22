package it.polito.tdp.poweroutages.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polito.tdp.poweroutages.model.Correlation;
import it.polito.tdp.poweroutages.model.Nerc;
import it.polito.tdp.poweroutages.model.PowerOutages;

public class PowerOutagesDAO {

	public List<Nerc> loadAllNercs() {

		String sql = "SELECT id, value FROM nerc";
		List<Nerc> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n = new Nerc(res.getInt("id"), res.getString("value"));
				nercList.add(n);
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}

	public Map<Nerc, Set<Nerc>> loadAllNercsRelations(Map<Integer, Nerc> nercIdMap) {
		String sql = "SELECT id, nerc_one, nerc_two FROM nercrelations";
		Map<Nerc, Set<Nerc>> results = new HashMap<Nerc, Set<Nerc>>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n1 = nercIdMap.get(res.getInt("nerc_one"));
				Nerc n2 = nercIdMap.get(res.getInt("nerc_two"));
				if (!results.containsKey(n1))
					// se la mappa non contiene n1 inizializzo il set
					results.put(n1, new HashSet<Nerc>());
				results.get(n1).add(n2);

				if (!results.containsKey(n2))
					// se la mappa non contiene n2
					results.put(n2, new HashSet<Nerc>());
//				results.put(n2, new HashSet<Nerc>(results.get(n2)).add(n1));
				results.get(n2).add(n1);

			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return results;
	}

	public List<Correlation> loadAllCorrelations(Map<Integer, Nerc> nercIdMap) {
		String sql = "SELECT n1, n2, SUM(tot) AS res " + "FROM ( "
				+ "SELECT p1.nerc_id AS n1, p2.nerc_id AS n2, COUNT(*) AS tot "
				+ "FROM poweroutages p1, poweroutages p2 "
				+ "WHERE MONTH(p1.date_event_began)=MONTH(p2.date_event_began) "
				+ "AND YEAR(p1.date_event_began) = YEAR(p2.date_event_began) " + "AND p1.nerc_id != p2.nerc_id "
				+ "GROUP BY p1.nerc_id, p2.nerc_id, MONTH(p1.date_event_began), YEAR(p1.date_event_began) "
				+ "ORDER BY  p1.nerc_id, p2.nerc_id, YEAR(p1.date_event_began), MONTH(p1.date_event_began) " + ") AS t "
				+ "GROUP BY n1, n2";

		List<Correlation> results = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n1 = nercIdMap.get(res.getInt("n1"));
				Nerc n2 = nercIdMap.get(res.getInt("n2"));
				int corr = res.getInt("res");
				Correlation c = new Correlation(n1, n2, corr);
				results.add(c);

			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return results;
	}

	public List<PowerOutages> loadAllPowerOutages(Map<Integer, Nerc> nercIdMap) {
		String sql = "select * from poweroutages";

		List<PowerOutages> results = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n = nercIdMap.get(res.getInt("nerc_id"));
				System.out.println("Aggiungo il nerc " + n);
				PowerOutages po = new PowerOutages(n, res.getDate("date_event_began").toLocalDate(),
						res.getDate("date_event_finished").toLocalDate());
				results.add(po);

			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return results;
	}
}
