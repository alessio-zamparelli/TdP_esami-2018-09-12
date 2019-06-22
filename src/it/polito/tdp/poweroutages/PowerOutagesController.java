/**
 * Sample Skeleton for 'PowerOutages.fxml' Controller Class
 */

package it.polito.tdp.poweroutages;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import it.polito.tdp.poweroutages.model.Model;
import it.polito.tdp.poweroutages.model.Nerc;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PowerOutagesController {

	private Model model;

	private boolean grafoInizializzato = false;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="txtResult"
	private TextArea txtResult; // Value injected by FXMLLoader

	@FXML // fx:id="btnCreaGrafo"
	private Button btnCreaGrafo; // Value injected by FXMLLoader

	@FXML // fx:id="cmbBoxNerc"
	private ComboBox<Nerc> cmbBoxNerc; // Value injected by FXMLLoader

	@FXML // fx:id="btnVisualizzaVicini"
	private Button btnVisualizzaVicini; // Value injected by FXMLLoader

	@FXML // fx:id="txtK"
	private TextField txtK; // Value injected by FXMLLoader

	@FXML // fx:id="btnSimula"
	private Button btnSimula; // Value injected by FXMLLoader

	@FXML
	void doCreaGrafo(ActionEvent event) {
		model.creaGrafo();
		txtResult.appendText("Grafo creato correttamente\n");
		grafoInizializzato = true;
	}

	@FXML
	void doSimula(ActionEvent event) {
		int k;
		try {
			k=Integer.parseInt(this.txtK.getText());
		} catch (NumberFormatException e) {
			txtResult.appendText("Valore di K non valido, deve essere un numero intero positivo\n");
			return;
		}
		model.simula(k);
		Map<Nerc,Integer> bonus = model.getNercBonus();
		int catastrofi = model.getCatastrofi();
		txtResult.appendText(String.format("Ci sono state %d catastrofi\n", catastrofi));
		bonus.entrySet().forEach(a->txtResult.appendText(a.getKey() + " ha guadagnato " + a.getValue() + " punti\n"));
	}

	@FXML
	void doVisualizzaVicini(ActionEvent event) {
		if (!grafoInizializzato) {
			txtResult.appendText("Devi prima inizializzare il grafo!\n");
			return;
		}
		Nerc n = cmbBoxNerc.getSelectionModel().getSelectedItem();
		if (n == null) {
			txtResult.appendText("Selezionare un nerc dal menu a tendina\n");
			return;
		}
		Map<Nerc, Double> vicini = model.getViciniPesati(n);
		txtResult.appendText("\nVicini di " + n + "\n");
		vicini.entrySet().stream().sorted(new Comparator<Entry<Nerc, Double>>() {

			@Override
			public int compare(Entry<Nerc, Double> o1, Entry<Nerc, Double> o2) {
				return (int) (o2.getValue()-o1.getValue());
			}
		})
				.forEach(a -> txtResult.appendText(String.format("%s - %.0f\n", a.getKey(), a.getValue())));
		;

	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert cmbBoxNerc != null : "fx:id=\"cmbBoxNerc\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert btnVisualizzaVicini != null : "fx:id=\"btnVisualizzaVicini\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert txtK != null : "fx:id=\"txtK\" was not injected: check your FXML file 'PowerOutages.fxml'.";
		assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'PowerOutages.fxml'.";

	}

	public void setModel(Model model) {
		this.model = model;
		List<Nerc> nercList = model.getAllNercs();
		cmbBoxNerc.getItems().setAll(nercList);

	}
}
