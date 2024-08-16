package de.svws_nrw.module.reporting.types.gost.klausurplanung;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.svws_nrw.module.reporting.types.kurs.ReportingKurs;
import de.svws_nrw.module.reporting.types.schueler.ReportingSchueler;


/**
 * <p>Basis-Klasse im Rahmen des Reportings für Daten vom Typ GostKlausurplanungKlausurplan.</p>
 * <p>Sie enthält die Daten zu einem Klausurplan, d. h. beispielsweise zu den Terminen.</p>
 * <p>Diese Klasse ist als reiner Datentyp konzipiert, d. h. sie hat keine Anbindung an die Datenbank. Sie dient als Super-Klasse
 * einer Proxy-Klasse, welche die Getter in Teilen überschreibt und dort die Daten aus der Datenbank nachlädt.</p>
 */
public class ReportingGostKlausurplanungKlausurplan {

	/** Eine Liste, die alle Termine des Klausurplanes beinhaltet. */
	protected List<ReportingGostKlausurplanungKlausurtermin> klausurtermine;

	/** Eine Liste, die alle Kurse des Klausurplanes beinhaltet. */
	protected List<ReportingKurs> kurse;

	/** Eine Liste, die alle Kursklausuren des Klausurplanes beinhaltet. */
	protected List<ReportingGostKlausurplanungKursklausur> kursklausuren;

	/** Eine Liste, die alle Schüler des Klausurplanes beinhaltet. */
	protected List<ReportingSchueler> schueler;

	/** Eine Liste, die alle Schülerklausuren des Klausurplanes beinhaltet. */
	protected List<ReportingGostKlausurplanungSchuelerklausur> schuelerklausuren;


	/**
	 * Erstellt ein neues Reporting-Objekt auf Basis dieser Klasse.
	 * @param klausurtermine	Eine Liste, die alle Termine des Klausurplanes beinhaltet.
	 * @param kurse 			Eine Liste, die alle Kurse des Klausurplanes beinhaltet.
	 * @param kursklausuren 	Eine Liste, die alle Kursklausuren des Klausurplanes beinhaltet.
	 * @param schueler 			Eine Liste, die alle Schüler des Klausurplanes beinhaltet.
	 * @param schuelerklausuren Eine Liste, die alle Schülerklausuren des Klausurplanes beinhaltet.
	 */
	public ReportingGostKlausurplanungKlausurplan(final List<ReportingGostKlausurplanungKlausurtermin> klausurtermine, final List<ReportingKurs> kurse,
			final List<ReportingGostKlausurplanungKursklausur> kursklausuren, final List<ReportingSchueler> schueler,
			final List<ReportingGostKlausurplanungSchuelerklausur> schuelerklausuren) {

		// Fülle die Basislisten mit den übergebenen Daten.
		this.schueler = (schueler != null) ? schueler : new ArrayList<>();
		this.kurse = (kurse != null) ? kurse : new ArrayList<>();
		this.klausurtermine = (klausurtermine != null) ? klausurtermine : new ArrayList<>();
		this.kursklausuren = (kursklausuren != null) ? kursklausuren : new ArrayList<>();
		this.schuelerklausuren = (schuelerklausuren != null) ? schuelerklausuren : new ArrayList<>();
	}


	// ##### Berechnete Methoden #####

	/**
	 * Eine Liste vom Typ String, die alle vorhandenen Datumsangaben der Termine des Klausurplanes beinhaltet (distinct).
	 * @return Liste der Datumsangaben der Klausurtermine
	 */
	public List<String> datumsangabenKlausurtermine() {
		return this.klausurtermine.stream().filter(t -> Objects.nonNull(t.datum())).map(t -> t.datum).sorted().distinct().toList();
	}

	/**
	 * Eine Liste vom Typ GostKlausurplanungKlausurtermin, die alle Termine des Klausurplanes beinhaltet, denen bereits ein Datum zugewiesen wurde.
	 * @return Liste der Klausurtermine mit Datumsangabe
	 */
	public List<ReportingGostKlausurplanungKlausurtermin> klausurtermineMitDatum() {
		return this.klausurtermine.stream().filter(t -> Objects.nonNull(t.datum())).toList();
	}

	/**
	 * Eine Liste vom Typ GostKlausurplanungKlausurtermin, die alle Termine des Klausurplanes beinhaltet, denen noch kein Datum zugewiesen wurde.
	 * @return Liste der Klausurtermine ohne Datumsangabe
	 */
	public List<ReportingGostKlausurplanungKlausurtermin> klausurtermineOhneDatum() {
		return this.klausurtermine.stream().filter(t -> Objects.isNull(t.datum())).toList();
	}

	/**
	 * Eine Liste vom Typ GostKlausurplanungKlausurtermin, die alle Termine des Klausurplanes zum angegebenen Datum beinhaltet.
	 * @param  datum 	Datum, zu dem die Liste der Klausurtermine zurückgegeben werden soll.
	 * @return 			Liste der Klausurtermine mit dem gewünschten Datum
	 */
	public List<ReportingGostKlausurplanungKlausurtermin> klausurtermineZumDatum(final String datum) {
		if ((datum == null) || datum.isEmpty())
			return new ArrayList<>();
		return this.klausurtermine.stream().filter(t -> datum.equals(t.datum()))
				.sorted(Comparator
						.comparing(ReportingGostKlausurplanungKlausurtermin::gostHalbjahr)
						.thenComparing(ReportingGostKlausurplanungKlausurtermin::startuhrzeit))
				.toList();
	}

	/**
	 * Gibt den Klausurtermin zur übergebenen ID zurück
	 * @param  id 	Die ID des Klausurtermins
	 * @return 		Der Klausurtermin zur ID oder null, wenn nicht vorhanden.
	 */
	public ReportingGostKlausurplanungKlausurtermin klausurtermin(final long id) {
		if (id < 0)
			return null;
		else
			return this.klausurtermine.stream().filter(t -> id == t.id).findFirst().orElse(null);
	}

	/**
	 * Gibt den Kurs zur übergebenen ID zurück
	 * @param  id 	Die ID des Kurses
	 * @return 		Der Kurs zur ID oder null, wenn nicht vorhanden.
	 */
	public ReportingKurs kurs(final long id) {
		if (id < 0)
			return null;
		else
			return this.kurse.stream().filter(k -> id == k.id()).findFirst().orElse(null);
	}

	/**
	 * Gibt die Kursklausur zur übergebenen ID zurück
	 * @param  id 	Die ID der Kursklausur
	 * @return 		Die Kursklausur zur ID oder null, wenn nicht vorhanden.
	 */
	public ReportingGostKlausurplanungKursklausur kursklausur(final long id) {
		if (id < 0)
			return null;
		else
			return this.kursklausuren.stream().filter(k -> id == k.id()).findFirst().orElse(null);
	}

	/**
	 * Gibt den Schüler zur übergebenen ID zurück
	 * @param  id 	Die ID des Schülers
	 * @return 		Der Schüler zur ID oder null, wenn nicht vorhanden.
	 */
	public ReportingSchueler schueler(final long id) {
		if (id < 0)
			return null;
		else
			return this.schueler.stream().filter(s -> id == s.id()).findFirst().orElse(null);
	}

	/**
	 * Gibt die Schülerklausur zur übergebenen ID zurück
	 * @param  id 	Die ID der Schülerklausur
	 * @return 		Die Schülerklausur zur ID oder null, wenn nicht vorhanden.
	 */
	public ReportingGostKlausurplanungSchuelerklausur schuelerklausur(final long id) {
		if (id < 0)
			return null;
		else
			return this.schuelerklausuren.stream().filter(s -> id == s.id()).findFirst().orElse(null);
	}


	// ##### Getter #####

	/**
	 * Eine Liste, die alle Termine des Klausurplanes beinhaltet.
	 * @return Liste der Klausurtermine
	 */
	public List<ReportingGostKlausurplanungKlausurtermin> klausurtermine() {
		return this.klausurtermine;
	}

	/**
	 * Eine Liste, die alle Kurse des Klausurplanes beinhaltet.
	 * @return Liste der Kurse
	 */
	public List<ReportingKurs> kurse() {
		return this.kurse;
	}

	/**
	 * Eine Liste, die alle Kursklausuren des Klausurplanes beinhaltet.
	 * @return Liste der Kursklausuren
	 */
	public List<ReportingGostKlausurplanungKursklausur> kursklausuren() {
		return this.kursklausuren;
	}

	/**
	 * Eine Liste, die alle Schüler des Klausurplanes beinhaltet.
	 * @return Liste der Schüler
	 */
	public List<ReportingSchueler> schueler() {
		return this.schueler;
	}

	/**
	 * Eine Liste, die alle Schülerklausuren des Klausurplanes beinhaltet.
	 * @return Liste der Schülerklausuren
	 */
	public List<ReportingGostKlausurplanungSchuelerklausur> schuelerklausuren() {
		return this.schuelerklausuren;
	}
}
