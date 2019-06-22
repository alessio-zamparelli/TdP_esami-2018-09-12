package it.polito.tdp.poweroutages.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.poweroutages.db.PowerOutagesDAO;

public class Model {
	// var esterne
	private PowerOutagesDAO dao;
	private Simulazione sim;
	// var interne
	private Graph<Nerc, DefaultWeightedEdge> grafo;
	private Map<Integer, Nerc> nercIdMap;

	public Model() {
		this.dao = new PowerOutagesDAO();
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
	}

	public void creaGrafo() {
		nercIdMap = dao.loadAllNercs().parallelStream().collect(Collectors.toMap(Nerc::getId, a -> a));
		Graphs.addAllVertices(this.grafo, nercIdMap.values());
		Map<Nerc, Set<Nerc>> nercConf = dao.loadAllNercsRelations(this.nercIdMap);
		for (Entry<Nerc, Set<Nerc>> entry : nercConf.entrySet())
			for (Nerc n : entry.getValue())
				this.grafo.addEdge(entry.getKey(), n);
		System.out.println("Lista dei vertici");
		this.nercIdMap.values().forEach(a -> System.out.println(a));
		System.out.println("\n\n\nLista di archi");
		this.grafo.edgeSet()
				.forEach(a -> System.out.println(this.grafo.getEdgeSource(a) + " - " + this.grafo.getEdgeTarget(a)));
		List<Correlation> corr = dao.loadAllCorrelations(nercIdMap);
		for (Correlation c : corr) {
			System.out.println("aggiungo tra " + c.getN1() + " e " + c.getN2());
			if (this.grafo.getEdge(c.getN1(), c.getN2()) != null)
				this.grafo.setEdgeWeight(this.grafo.getEdge(c.getN1(), c.getN2()), c.getC());
		}
		for (DefaultWeightedEdge e : this.grafo.edgeSet())
			System.out.format("Da %s a %s peso %.0f\n", this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e),
					this.grafo.getEdgeWeight(e));
	}

	public List<Nerc> getAllNercs() {
		if (nercIdMap != null)
			return new ArrayList<>(this.nercIdMap.values());
		return dao.loadAllNercs();
	}

	public Map<Nerc, Double> getViciniPesati(Nerc n) {
		Map<Nerc, Double> res = new HashMap<>();
		for (DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(n)) {
			res.put(this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e));
		}
		return res;
	}

	public void simula(int k) {
		sim = new Simulazione();
		if(grafo==null)
			this.creaGrafo();
		sim.init(k, dao.loadAllPowerOutages(this.nercIdMap), this.nercIdMap, this);
		sim.run();
	}
	
	public int getCatastrofi() {
		return this.sim.getCatastrofi();
	}
	
	public Map<Nerc, Integer> getNercBonus(){
		return this.sim.getNercBonus();
	}

}
