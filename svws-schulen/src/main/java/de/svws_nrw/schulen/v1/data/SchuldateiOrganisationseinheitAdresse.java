package de.svws_nrw.schulen.v1.data;

import de.svws_nrw.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Die Klasse beschreibt eine Adresse einer Organisationseinheit
 * der Schuldatei.
 */
@XmlRootElement
@Schema(description = "eine Adresse einer Organisationseinheit der Schuldatei.")
@TranspilerDTO
public class SchuldateiOrganisationseinheitAdresse extends SchuldateiEintrag {

	/** Die Schulnummer. */
	@Schema(description = "die Schulnummer", example = "100001")
	public @NotNull String schulnummer = "";

	/** Die ID des Adress-Eintrags. */
	@Schema(description = "die ID des Adress-Eintrags", example = "4711")
	public int id = 0;

	/** Die Nummer der Liegenschaft der Organisationseinheit */
	@Schema(description = "Die Nummer der Liegenschaft der Organisationseinheit", example = "1")
	public int liegenschaft = 0;

	/** Straße der Adresse der Organisationseinheit */
	@Schema(description = "Straße der Adresse der Organisationseinheit")
	public @NotNull String strasse = "";

	/** Postleitzahl der Schule */
	@Schema(description = "Postleitzahl der Adresse der Organisationseinheit")
	public @NotNull String postleitzahl = "";

	/** Ort der Schule */
	@Schema(description = "Ort der Adresse der Organisationseinheit")
	public @NotNull String ort = "";

	/** Regionalschlüssel der Schule */
	@Schema(description = "Regionalschlüssel der Adresse der Organisationseinheit")
	public @NotNull String regionalschluessel = "";

	/** Qualität der Verortung */
	@Schema(description = "Qualität der Verortung")
	public int qualitaetverortung;

	/** Koordinatenrechtswert der Adresse */
	@Schema(description = "Koordinatenrechtswert der Adresse")
	public long koordinaterechtswert;

	/** Koordinatenhochwert der Adresse */
	@Schema(description = "Koordinatenhochwert der Adresse")
	public long koordinatehochwert;

	/** Der Adresstyp */
	@Schema(description = "Adresstypid der Adresse", example = "1")
	public @NotNull String adresstypeid = "";

	/** Das Standortkennzeichen */
	@Schema(description = "Standortkennzeichen des Teilstandorts", example = "01")
	public @NotNull String standortkennzeichen = "";

	/** Das Adresskennzeichnen des Teilstandorts (ein Buchstabe) */
	@Schema(description = "Adresskennzeichen des Teilstandors", example = "A")
	public @NotNull String adresskennzeichen = "";

	/** Hauptstandortadresse */
	@Schema(description = "Hauptstandortadresse", example = "")
	public @NotNull String hauptstandortadresse = "";


	/**
	 * Erstellt einen neuen Eintrag zu einer Adresse einer Organisationseinheit der Schuldatei
	 */
	public SchuldateiOrganisationseinheitAdresse() {
		// Die Initialisierung mit Defaults erfolgt direkt über die Attribute
	}

}
