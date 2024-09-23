package de.svws_nrw.module.reporting.proxytypes.jahrgang;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.svws_nrw.core.data.jahrgang.JahrgangsDaten;
import de.svws_nrw.module.reporting.types.schule.ReportingSchuljahresabschnitt;
import de.svws_nrw.module.reporting.repositories.ReportingRepository;
import de.svws_nrw.module.reporting.types.jahrgang.ReportingJahrgang;
import de.svws_nrw.module.reporting.types.klasse.ReportingKlasse;
import de.svws_nrw.module.reporting.types.schueler.ReportingSchueler;

import java.util.Comparator;
import java.util.List;

/**
 *  <p>Proxy-Klasse im Rahmen des Reportings für Daten vom Typ Jahrgang und erweitert die Klasse {@link ReportingJahrgang}.</p>
 *
 *  <p>In diesem Kontext besitzt die Proxy-Klasse ausschließlich die gleichen Methoden wie die zugehörige Reporting-Super-Klasse.
 *  Während die Super-Klasse aber als reiner Datentyp konzipiert ist, d. h. ohne Anbindung an die Datenbank,
 *  greift die Proxy-Klassen an verschiedenen Stellen auf die Datenbank zu.</p>
 *
 *  <ul>
 *      <li>Die Proxy-Klasse stellt in der Regel einen zusätzlichen Constructor zur Verfügung, um Reporting-Objekte
 *  		aus Stammdatenobjekten (aus dem Package core.data) erstellen zu können. Darin werden Felder, die Reporting-Objekte
 *  		zurückgegeben und nicht im Stammdatenobjekt enthalten sind, mit null initialisiert.</li>
 * 		<li>Die Proxy-Klasse überschreibt einzelne Getter der Super-Klasse (beispielsweise bei Felder, die mit null initialisiert wurden)
 *  		und lädt dort dann aus der Datenbank die Daten bei Bedarf nach (lazy-loading), um den Umfang der Datenstrukturen gering zu
 *  		halten.</li>
 * 		<li>Die Proxy-Klasse können zudem auf das Repository {@link ReportingRepository} zugreifen. Dieses
 * 			enthält neben den Stammdaten der Schule einige Maps, in der zur jeweiligen ID bereits ausgelesene Stammdaten anderer Objekte
 * 			wie Kataloge, Jahrgänge, Klassen, Lehrer, Schüler usw. gespeichert werden. So sollen Datenbankzugriffe minimiert werden. Werden in der
 * 			Proxy-Klasse Daten nachgeladen, so werden sie dabei auch in der entsprechenden Map des Repository ergänzt.</li>
 *  </ul>
 */
public class ProxyReportingJahrgang extends ReportingJahrgang {

	/** Repository für die Reporting */
	@JsonIgnore
	private final ReportingRepository reportingRepository;



	/**
	 * Erstellt ein neues Reporting-Objekt auf Basis eines Stammdaten-Objektes.
	 * @param reportingRepository Repository für die Reporting.
	 * @param jahrgangsDaten Stammdaten-Objekt aus der DB.
	 * @param schuljahresabschnitt Der Schuljahresabschnitt zu diesem Jahrgang.
	 */
	public ProxyReportingJahrgang(final ReportingRepository reportingRepository, final JahrgangsDaten jahrgangsDaten,
			final ReportingSchuljahresabschnitt schuljahresabschnitt) {
		super(jahrgangsDaten.bezeichnung,
				jahrgangsDaten.gueltigBis,
				jahrgangsDaten.gueltigVon,
				null,
				jahrgangsDaten.id,
				jahrgangsDaten.idFolgejahrgang,
				null,
				jahrgangsDaten.kuerzel,
				jahrgangsDaten.kuerzelSchulgliederung,
				jahrgangsDaten.kuerzelStatistik,
				jahrgangsDaten.istSichtbar,
				null,
				schuljahresabschnitt,
				jahrgangsDaten.sortierung);
		this.reportingRepository = reportingRepository;
	}



	/**
	 * Gibt das Repository mit den Daten der Schule und den zwischengespeicherten Daten zurück.
	 * @return Repository für die Reporting
	 */
	public ReportingRepository reportingRepository() {
		return reportingRepository;
	}

	/**
	 * Stellt die Daten des Folgejahrgangs zur Verfügung.
	 * @return Daten des Folgejahrgangs
	 */
	@Override
	public ReportingJahrgang folgejahrgang() {
		if ((super.folgejahrgang() == null) && (super.idFolgejahrgang() != null) && (super.idFolgejahrgang() >= 0)) {
			if (!this.reportingRepository.mapJahrgaenge().containsKey(super.idFolgejahrgang())) {
				// TODO: Wenn die Jahrgänge eine Gültigkeit erhalten, dann ist diese hier auch zu implementieren. Aktuell werden in alle Schuljahresabschnitte alle
				//  Jahrgänge übernommen und der Folgejahrgang innerhalb des gleichen Lernabschnitts ermittelt, da keine Regelung zum Folgejahrgang und einem
				//  Folgeabschnitt im System implementiert ist. Daher wird eine direkt Rückgabe erzeugt, die aber nie auftreten dürfte.
				return super.folgejahrgang();
			} else {
				// ID des FolgeJahrgangs ist bekannt und der Jahrgang wurde in einem Lernabschnitt bereits erzeugt, hole ihn aus Lernabschnitt.
				super.folgejahrgang = super.schuljahresabschnitt().jahrgang(super.idFolgejahrgang());
			}
		}
		return super.folgejahrgang();
	}

	/**
	 * Stellt eine Liste mit Klassen des Jahrgangs im übergebenen Schuljahresabschnitt zur Verfügung.
	 * @return	Liste mit Klassen
	 */
	@Override
	public List<ReportingKlasse> klassen() {
		if (super.klassen().isEmpty()) {
			super.klassen =
					super.schuljahresabschnitt.klassen().stream()
							.filter(k -> k.idJahrgang() == super.id())
							.sorted(Comparator
									.comparing(ReportingKlasse::kuerzel)
									.thenComparing(ReportingKlasse::parallelitaet))
							.toList();
		}
		return super.klassen();
	}

	/**
	 * Stellt eine Liste mit Schülern des Jahrgangs zur Verfügung.
	 * @return	Liste mit Schülern
	 */
	@Override
	public List<ReportingSchueler> schueler() {
		if (super.schueler().isEmpty()) {
			super.schueler =
					klassen().stream()
							.flatMap(k -> k.schueler().stream())
							.sorted(Comparator
									.comparing(ReportingSchueler::nachname)
									.thenComparing(ReportingSchueler::vorname)
									.thenComparing(ReportingSchueler::vornamen)
									.thenComparing(ReportingSchueler::geburtsdatum)
									.thenComparing(ReportingSchueler::id))
							.toList();
		}
		return super.schueler();
	}
}
