package de.svws_nrw.module.reporting.proxytypes.stundenplanung;

import de.svws_nrw.core.data.stundenplan.StundenplanZeitraster;
import de.svws_nrw.core.types.Wochentag;
import de.svws_nrw.module.reporting.repositories.ReportingRepository;
import de.svws_nrw.module.reporting.types.stundenplanung.ReportingStundenplanungZeitrasterstunde;


/**
 *  <p>Proxy-Klasse im Rahmen des Reportings für Daten vom Typ Raum und erweitert die Klasse {@link ReportingStundenplanungZeitrasterstunde}.</p>
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
public class ProxyReportingStundenplanungZeitrasterstunde extends ReportingStundenplanungZeitrasterstunde {

	/**
	 * Erstellt ein neues Reporting-Objekt auf Basis eines Stammdaten-Objektes.
	 * @param zeitraster		Der Raum aus dem Stundenplan.
	 * @param idStundenplan 	Optional: Die des Stundenplanes, zu dem der Raum mit seiner ID gehört, sonst null.
	 */
	public ProxyReportingStundenplanungZeitrasterstunde(final StundenplanZeitraster zeitraster, final Long idStundenplan) {
		super(zeitraster.id,
				idStundenplan,
				zeitraster.stundenbeginn,
				zeitraster.stundenende,
				zeitraster.unterrichtstunde,
				Wochentag.fromIDorException(zeitraster.wochentag));
	}
}
