package de.svws_nrw.module.reporting.types.schule;

import java.util.ArrayList;
import java.util.List;

import de.svws_nrw.module.reporting.types.fach.ReportingFach;
import de.svws_nrw.module.reporting.types.jahrgang.ReportingJahrgang;
import de.svws_nrw.module.reporting.types.klasse.ReportingKlasse;

/**
 * <p>Basis-Klasse im Rahmen des Reportings für Daten vom Typ Schuljahresabschnitt.</p>
 * <p>Sie enthälten Daten zu einem Schuljahresabschnitt, also zum Schuljahr und Halbjahr.</p>
 * <p>Diese Klasse ist als reiner Datentyp konzipiert, d. h. sie hat keine Anbindung an die Datenbank. Sie dient als Super-Klasse
 * einer Proxy-Klasse, welche die Getter in Teilen überschreibt und dort die Daten aus der Datenbank nachlädt.</p>
 */
public class ReportingSchuljahresabschnitt {

	/** Die ID des Schuljahresabschnittes */
	protected long id;

	/** Das Schuljahr, in welchem der Schuljahresabschnitt liegt */
	protected int schuljahr;

	/** Die Nummer des Abschnitts im Schuljahr */
	protected int abschnitt;

	/** Die ID des Schuljahresabschnittes, der diesem Abschnitt folgt. */
	protected Long idFolgenderAbschnitt;

	/** Die ID des Schuljahresabschnittes, der diesem Abschnitt vorhergeht. */
	protected Long idVorherigerAbschnitt;

	/** Der Schuljahresabschnitt, der diesem Abschnitt folgt. */
	protected ReportingSchuljahresabschnitt folgenderAbschnitt;

	/** Der Schuljahresabschnitt, der diesem Abschnitt vorhergeht. */
	protected ReportingSchuljahresabschnitt vorherigerAbschnitt;

	/** Die Fächer des Schuljahresabschnitts */
	protected List<ReportingFach> faecher = new ArrayList<>();

	/** Die Jahrgänge des Schuljahresabschnitts */
	protected List<ReportingJahrgang> jahrgaenge = new ArrayList<>();

	/** Die Klassen des Schuljahresabschnitts */
	protected List<ReportingKlasse> klassen = new ArrayList<>();


	/**
	 * Erstellt ein neues Reporting-Objekt auf Basis dieser Klasse.
	 * @param id 					Die ID des Schuljahresabschnittes
	 * @param schuljahr 			Das Schuljahr, in welchem der Schuljahresabschnitt liegt
	 * @param abschnitt 			Die Nummer des Abschnitts im Schuljahr
	 * @param idFolgenderAbschnitt 	Die ID des Schuljahresabschnittes, der diesem Abschnitt folgt.
	 * @param idVorherigerAbschnitt Die ID des Schuljahresabschnittes, der diesem Abschnitt vorhergeht.
	 * @param folgenderAbschnitt 	Der Schuljahresabschnitt, der diesem Abschnitt folgt.
	 * @param vorherigerAbschnitt 	Der Schuljahresabschnitt, der diesem Abschnitt vorhergeht.
	 * @param faecher				Die Fächer des Schuljahresabschnitts
	 * @param jahrgaenge			Die Jahrgänge des Schuljahresabschnitts
	 * @param klassen				Die Klassen des Schuljahresabschnitts
	 */
	public ReportingSchuljahresabschnitt(final long id, final int schuljahr, final int abschnitt, final Long idFolgenderAbschnitt,
			final Long idVorherigerAbschnitt, final ReportingSchuljahresabschnitt folgenderAbschnitt, final ReportingSchuljahresabschnitt vorherigerAbschnitt,
			final List<ReportingFach> faecher, final List<ReportingJahrgang> jahrgaenge, final List<ReportingKlasse> klassen) {
		this.id = id;
		this.schuljahr = schuljahr;
		this.abschnitt = abschnitt;
		this.idFolgenderAbschnitt = idFolgenderAbschnitt;
		this.idVorherigerAbschnitt = idVorherigerAbschnitt;
		this.folgenderAbschnitt = folgenderAbschnitt;
		this.vorherigerAbschnitt = vorherigerAbschnitt;
		this.faecher = faecher;
		this.jahrgaenge = jahrgaenge;
		this.klassen = klassen;
	}


	// ##### Berechnete Methoden #####
	/**
	 * Kurzer Text zum Schuljahresabschnitt im Format 20XX/YY.A
	 * @return Kurzer Text zum Schuljahresabschnitt
	 */
	public String textSchuljahresabschnittKurz() {
		return "%s/%s.%s".formatted(schuljahr, (schuljahr % 100) + 1, abschnitt);
	}

	/**
	 * Kurzer Text zum Schuljahresabschnitt im Format 20XX/YY.A
	 * @return Kurzer Text zum Schuljahresabschnitt
	 */
	public String textSchuljahresabschnittLang() {
		return "%s/%s %s. Halbjahr".formatted(schuljahr, (schuljahr % 100) + 1, abschnitt);
	}

	/**
	 * Gibt das Fach zur ID aus der Liste der Fächer des Schuljahresabschnitts zurück
	 * @param id	Die ID des Faches
	 * @return 		Das Fach zur ID oder null, wenn das Fach nicht vorhanden ist.
	 */
	public ReportingFach fach(final long id) {
		return faecher().stream().filter(f -> f.id() == id).findFirst().orElse(null);
	}

	/**
	 * Gibt den Jahrgang zur ID aus der Liste der Jahrgänge des Schuljahresabschnitts zurück
	 * @param id	Die ID des Jahrgangs
	 * @return 		Der Jahrgang zur ID oder null, wenn der Jahrgang nicht vorhanden ist.
	 */
	public ReportingJahrgang jahrgang(final long id) {
		return jahrgaenge().stream().filter(j -> j.id() == id).findFirst().orElse(null);
	}

	/**
	 * Gibt die Klasse zur ID aus der Liste der Klassen des Schuljahresabschnitts zurück
	 * @param id	Die ID der Klasse
	 * @return 		Die Klasse zur ID oder null, wenn die Klasse nicht vorhanden ist.
	 */
	public ReportingKlasse klasse(final long id) {
		return klassen().stream().filter(k -> k.id() == id).findFirst().orElse(null);
	}


	// ##### Getter #####

	/**
	 * Die ID des Schuljahresabschnittes
	 * @return Inhalt des Feldes id
	 */
	public long id() {
		return id;
	}

	/**
	 * Das Schuljahr, in welchem der Schuljahresabschnitt liegt
	 * @return Inhalt des Feldes schuljahr
	 */
	public int schuljahr() {
		return schuljahr;
	}

	/**
	 * Die Nummer des Abschnitts im Schuljahr
	 * @return Inhalt des Feldes abschnitt
	 */
	public int abschnitt() {
		return abschnitt;
	}

	/**
	 * Die ID des Schuljahresabschnittes, der diesem Abschnitt folgt.
	 * @return Inhalt des Feldes idFolgenderAbschnitt
	 */
	public Long idFolgenderAbschnitt() {
		return idFolgenderAbschnitt;
	}

	/**
	 * Die ID des Schuljahresabschnittes, der diesem Abschnitt vorhergeht.
	 * @return Inhalt des Feldes idVorherigerAbschnitt
	 */
	public Long idVorherigerAbschnitt() {
		return idVorherigerAbschnitt;
	}

	/**
	 * Der Schuljahresabschnitt, der diesem Abschnitt folgt.
	 * @return Inhalt des Feldes folgenderAbschnitt
	 */
	public ReportingSchuljahresabschnitt folgenderAbschnitt() {
		return folgenderAbschnitt;
	}

	/**
	 * Der Schuljahresabschnitt, der diesem Abschnitt vorhergeht.
	 * @return Inhalt des Feldes vorherigerAbschnitt
	 */
	public ReportingSchuljahresabschnitt vorherigerAbschnitt() {
		return vorherigerAbschnitt;
	}

	/**
	 * Die Fächer des Schuljahresabschnitts
	 * @return Inhalt des Feldes faecher
	 */
	public List<ReportingFach> faecher() {
		return faecher;
	}

	/**
	 * Die Jahrgänge des Schuljahresabschnitts
	 * @return Inhalt des Feldes jahrgaenge
	 */
	public List<ReportingJahrgang> jahrgaenge() {
		return jahrgaenge;
	}

	/**
	 * Die Klassen des Schuljahresabschnitts
	 * @return Inhalt des Feldes klassen
	 */
	public List<ReportingKlasse> klassen() {
		return klassen;
	}
}
