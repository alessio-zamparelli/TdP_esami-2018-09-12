package it.polito.tdp.poweroutages.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import it.polito.tdp.poweroutages.model.Evento.TipoEvento;

public class Simulazione {
	// var simulazione
	private int K;
	private PriorityQueue<Evento> queue;
	private int catastrofi;
	// var interne
	private Map<Nerc, Integer> nercBonus;
//	private Map<Integer, Nerc> nercIdMap;
	private Map<Nerc, Map<LocalDate, Nerc>> donazioni;
	private List<Nerc> nercDisponibili;
	private Model model;

	public void init(int K, List<PowerOutages> powerOutages, Map<Integer, Nerc> nercIdMap, Model model) {
		this.K = K;
		this.model = model;
		this.nercBonus = new HashMap<>();
//		this.nercIdMap = nercIdMap;
		this.queue = new PriorityQueue<>();
		this.nercDisponibili = new ArrayList<>(nercIdMap.values());
		this.catastrofi = 0;
		this.donazioni = new HashMap<>();

		for (Nerc n : nercIdMap.values())
			donazioni.put(n, new HashMap<>());

		for (PowerOutages po : powerOutages) {
			Evento e = new Evento(po.getNerc(), po.getDate_event_began(), TipoEvento.INIZIO_INTERRUZIONE, po, null);
			queue.add(e);
			System.out.println(e);

		}
	}

	public void run() {
		Evento e;
		while ((e = queue.poll()) != null) {
			switch (e.getTipoEvento()) {

				case INIZIO_INTERRUZIONE:
					System.out.println(e.getData() + " INIZIO INTERRUZIONE PER " + e.getNerc());
					Nerc donatore = null;

					Map<Nerc, Double> vicini = model.getViciniPesati(e.getNerc());

					System.out.println("\n\nVicini a " + e.getNerc());
					System.out.println(vicini);
					System.out.println("Donazioni fatte da " + e.getNerc());
					System.out.println(this.donazioni.get(e.getNerc()));

					if (donazioni.get(e.getNerc()).size() > 0) {
						// Il nerc ha fatto donazioni, quindi puo riscuotere
						Map<LocalDate, Nerc> possibiliDonatori = this.donazioni.get(e.getNerc());
						LocalDate dataDonazione = null;
						for (Entry<LocalDate, Nerc> entry : possibiliDonatori.entrySet()) {
							if (vicini.containsKey(entry.getValue())) {
								// se non è passato troppo tempo
								if (!entry.getValue().equals(e.getNerc())
										&& Period.between(entry.getKey(), e.getData()).getMonths() < this.K) {
									if (donatore == null) {
										// primo caso
										donatore = entry.getValue();
										dataDonazione = entry.getKey();

									} else if (vicini.get(entry.getValue()) < vicini.get(donatore)) {
										// Ho trovato un miglior nuovo donatore
										donatore = entry.getValue();
										dataDonazione = entry.getKey();

									}
								} else {
									// la donazione non è piu utile, si perde
									this.donazioni.get(e.getNerc()).remove(entry.getKey());
								}
							}
						}
						// il donatore trovato ha pagato il debito
						this.donazioni.get(e.getNerc()).remove(dataDonazione);
					}
					if (donatore == null) {
						// Il nerc non ha ancora fatto donazioni o le donazioni non erano valide
						double peso = Integer.MAX_VALUE;
						for (Entry<Nerc, Double> entry : vicini.entrySet()) {
							if (!entry.getKey().equals(e.getNerc()) && nercDisponibili.contains(entry.getKey())) {
								if (entry.getValue() < peso) {
									donatore = entry.getKey();
									peso = entry.getValue();
								}
							}

						}

					} 
					if (donatore == null) {
						// Non ho trovato il donatore
						System.out.println(e.getData() + " CATASTROFE PER " + e);
						catastrofi++;
					} else {
						// Ho trovato un donatore
						// devo aggiungere il donatore alle donazioni
						if (!this.donazioni.containsKey(donatore))
							this.donazioni.put(donatore, new HashMap<>());
						this.donazioni.get(donatore).put(e.getData(), e.getNerc());
						if (!vicini.containsKey(donatore)) {
							System.err.println("Come cazzo è stato possibile ?");
							return;
						}
						// il donatore non è piu disponibile
						this.nercDisponibili.remove(donatore);

						// Aggiungo l'evento fine_interruzione
						Evento newEvento = new Evento(e.getNerc(), e.getPowerOutages().getDate_event_finished(),
								TipoEvento.FINE_INTERRUZIONE, e.getPowerOutages(), donatore);

						queue.add(newEvento);

						System.out.println(donatore + " dona a " + e.getNerc());
						System.out.println(donatore + " ha donato a " + this.donazioni.get(donatore));
//						try {
//							Thread.sleep(2000);
//						} catch (InterruptedException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
					}
					break;

				case FINE_INTERRUZIONE:
					System.out.println(e.getData() + " FINE DELL'INTERRUZIONE PER " + e.getNerc());
					// il donatore è nuovamente disponibile
					this.nercDisponibili.add(e.getDonatore());
					// do il bonus al donatore
					int durata = Period.between(e.getPowerOutages().getDate_event_began(),
							e.getPowerOutages().getDate_event_finished()).getDays();
					if (!nercBonus.containsKey(e.getDonatore()))
						nercBonus.put(e.getDonatore(), durata);
					else
						nercBonus.put(e.getDonatore(), nercBonus.get(e.getDonatore()) + durata);

					break;

			}
		}
	}

	public Map<Nerc, Integer> getNercBonus() {
		return this.nercBonus;
	}

	public int getCatastrofi() {
		return this.catastrofi;
	}
}
