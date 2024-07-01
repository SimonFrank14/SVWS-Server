package de.svws_nrw.core.types;

import java.util.HashMap;

import jakarta.validation.constraints.NotNull;

/**
 * Diese Klasse stellt die Core-Types als Aufzählung für die Personal-Typen
 * in der Lehrkräfteverwaltung zur Verfügung.
 * Core-Types dienen als grundlegende abstrakte Datentypen sowohl für die Core-Algorithmen
 * als auch für die OpenAPI-Schnittstelle.
 */
public enum PersonalTyp {

	/** Lehrkraft fest der Schule zugeordnet hat eine Identnummer */
	LEHRKRAFT(1, "LEHRKRAFT", "Lehrkraft", null, null),

	/** Verwaltungskraft ohne Identnummer */
	SEKRETARIAT(2, "SEKRETARIAT", "Sekretär/Sekretärin", null, null),

	/** angestelltes Personal (z.B. Sozialarbeiter*in ohne Identnummer */
	PERSONAL(3, "PERSONAL", "Angestelltes Personal ohne Identnummer", null, null),

	/** externe Lehrkräfte mit Identnummer von anderen Schulen abgeordnet */
	EXTERN(4, "EXTERN", "Externe Lehrkraft, z.B. abgeordnet oder im Rahmen einer Kooperation", null, null),

	/** Sonstige Personaltypen */
	SONSTIGE(5, "SONSTIGE", "Sonstiges Personal", null, null);


	/** Die Version dieses Core-Types, um beim Datenbank Update-Process die Version des Core-Types feststellen zu können. */
	public static final long VERSION = 1;



	/** Eine HashMap für den schnellen Zugriff auf Personal-Typen anhand des Kürzels des PersonalTyps */
	private static final @NotNull HashMap<String, PersonalTyp> _mapKuerzel = new HashMap<>();

	/** Eine HashMap für den schnellen Zugriff auf Personal-Typen anhand der ID des PersonalTyps */
	private static final @NotNull HashMap<Integer, PersonalTyp> _mapID = new HashMap<>();



	/** Die ID des Personal-Typs als Integer */
	public final int id;

	/** Das Kürzel des Personal-Typs als String */
	public final @NotNull String kuerzel;

	/** Die Bezeichnung des Personal-Typs als String */
	public final @NotNull String bezeichnung;

	/** Gibt an, in welchem Schuljahr der Personaltyp eingeführt wurde. Ist kein Schuljahr bekannt, so ist null gesetzt. */
	public final Integer gueltigVon;

	/** Gibt an, bis zu welchem Schuljahr der Personaltyp gültig ist. Ist kein Schuljahr bekannt, so ist null gesetzt. */
	public final Integer gueltigBis;


	/**
	 * Erzeugt einen neuen PersonalTyp für die Aufzählung.
	 *
	 * @param id           die ID des Personal-Typs
	 * @param kuerzel      das Kürzel des Personal-Typs
	 * @param bezeichnung  die Bezeichnung des Personal-Typs
	 * @param gueltigVon   gibt an, in welchem Schuljahr der Personaltyp eingeführt wurde. Ist kein Schuljahr bekannt, so ist null gesetzt.
	 * @param gueltigBis   gibt an, bis zu welchem Schuljahr der Personaltyp gültig ist. Ist kein Schuljahr bekannt, so ist null gesetzt.
	 */
	PersonalTyp(final int id, final @NotNull String kuerzel, final @NotNull String bezeichnung, final Integer gueltigVon, final Integer gueltigBis) {
		this.id = id;
		this.kuerzel = kuerzel;
		this.bezeichnung = bezeichnung;
		this.gueltigVon = gueltigVon;
		this.gueltigBis = gueltigBis;
	}



	/**
	 * Gibt eine Map von den IDs der Personal-Typen auf die zugehörigen Personal-Typen
	 * zurück. Sollte diese noch nicht initialisiert sein, so wird sie initialisiert.
	 *
	 * @return die Map von den IDs der Personal-Typen auf die zugehörigen Personal-Typen
	 */
	private static @NotNull HashMap<Integer, PersonalTyp> getMapID() {
		if (_mapID.size() == 0)
			for (final PersonalTyp p : PersonalTyp.values())
				_mapID.put(p.id, p);
		return _mapID;
	}


	/**
	 * Gibt eine Map von den Kürzeln der Personal-Typen auf die zugehörigen Personal-Typen
	 * zurück. Sollte diese noch nicht initialisiert sein, so wird sie initialisiert.
	 *
	 * @return die Map von den Kürzeln der Personal-Typen auf die zugehörigen Personal-Typen
	 */
	private static @NotNull HashMap<String, PersonalTyp> getMapKuerzel() {
		if (_mapKuerzel.size() == 0)
			for (final PersonalTyp p : PersonalTyp.values())
				_mapKuerzel.put(p.kuerzel, p);
		return _mapKuerzel;
	}


	/**
	 * Gibt den PersonalTyp anhand des Kürzels  zurück.
	 *
	 * @param kuerzel   das Kürzel des Personal-Typs
	 *
	 * @return der Personal-Typ oder null, falls das Kürzel ungültig ist
	 * */
	public static PersonalTyp fromKuerzel(final String kuerzel) {
		return getMapKuerzel().get(kuerzel);
	}



	/**
	 * Gibt den PersonalTyp anhand der ID zurück.
	 *
	 * @param id   die ID des Personal-Typs
	 *
	 * @return der Personal-Typ oder null, falls die ID ungültig ist
	 * */
	public static PersonalTyp fromID(final Integer id) {
		return getMapID().get(id);
	}



	@Override
	public @NotNull String toString() {
		return kuerzel;
	}


}

