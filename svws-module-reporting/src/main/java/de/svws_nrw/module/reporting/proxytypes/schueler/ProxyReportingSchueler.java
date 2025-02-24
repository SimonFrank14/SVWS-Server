package de.svws_nrw.module.reporting.proxytypes.schueler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.svws_nrw.core.data.gost.Abiturdaten;
import de.svws_nrw.core.data.schueler.SchuelerLernabschnittsdaten;
import de.svws_nrw.asd.data.schueler.SchuelerStammdaten;
import de.svws_nrw.core.data.schueler.Sprachbelegung;
import de.svws_nrw.core.logger.LogLevel;
import de.svws_nrw.asd.types.Geschlecht;
import de.svws_nrw.asd.types.schueler.SchuelerStatus;
import de.svws_nrw.core.types.schule.Nationalitaeten;
import de.svws_nrw.data.gost.DBUtilsGostAbitur;
import de.svws_nrw.data.schueler.DataSchuelerLernabschnittsdaten;
import de.svws_nrw.data.schueler.DataSchuelerSprachbelegung;
import de.svws_nrw.db.utils.ApiOperationException;
import de.svws_nrw.module.reporting.utils.ReportingExceptionUtils;
import de.svws_nrw.module.reporting.proxytypes.schueler.gost.abitur.ProxyReportingSchuelerGostAbitur;
import de.svws_nrw.module.reporting.proxytypes.schueler.gost.laufbahnplanung.ProxyReportingSchuelerGostLaufbahnplanung;
import de.svws_nrw.module.reporting.proxytypes.schueler.lernabschnitte.ProxyReportingSchuelerLernabschnitt;
import de.svws_nrw.module.reporting.proxytypes.schueler.sprachen.ProxyReportingSchuelerSprachbelegung;
import de.svws_nrw.module.reporting.repositories.ReportingRepository;
import de.svws_nrw.module.reporting.types.schueler.ReportingSchueler;
import de.svws_nrw.module.reporting.types.schueler.gost.abitur.ReportingSchuelerGostAbitur;
import de.svws_nrw.module.reporting.types.schueler.gost.laufbahnplanung.ReportingSchuelerGostLaufbahnplanung;
import de.svws_nrw.module.reporting.types.schueler.lernabschnitte.ReportingSchuelerLernabschnitt;
import de.svws_nrw.module.reporting.types.schueler.sprachen.ReportingSchuelerSprachbelegung;

/**
 * Proxy-Klasse im Rahmen des Reportings für Daten vom Typ Schüler und erweitert die Klasse {@link ReportingSchueler}.
 */
public class ProxyReportingSchueler extends ReportingSchueler {

	/** Repository mit Parametern, Logger und Daten-Cache zur Report-Generierung. */
	@JsonIgnore
	private final ReportingRepository reportingRepository;


	/**
	 * Erstellt ein neues Proxy-Reporting-Objekt für {@link ReportingSchueler}.
	 * @param reportingRepository Repository für die Reporting.
	 * @param schuelerStammdaten Stammdaten-Objekt aus der DB.
	 */
	public ProxyReportingSchueler(final ReportingRepository reportingRepository, final SchuelerStammdaten schuelerStammdaten) {
		super(null,
				schuelerStammdaten.anmeldedatum,
				schuelerStammdaten.aufnahmedatum,
				null,
				schuelerStammdaten.druckeKonfessionAufZeugnisse,
				schuelerStammdaten.emailPrivat,
				schuelerStammdaten.emailSchule,
				schuelerStammdaten.erhaeltMeisterBAFOEG,
				schuelerStammdaten.erhaeltSchuelerBAFOEG,
				schuelerStammdaten.externeSchulNr,
				schuelerStammdaten.fahrschuelerArtID,
				schuelerStammdaten.foto,
				schuelerStammdaten.geburtsdatum,
				schuelerStammdaten.geburtsland,
				schuelerStammdaten.geburtslandMutter,
				schuelerStammdaten.geburtslandVater,
				schuelerStammdaten.geburtsname,
				schuelerStammdaten.geburtsort,
				Geschlecht.fromValue(schuelerStammdaten.geschlecht),
				null,
				new ArrayList<>(),
				new ArrayList<>(),
				null,
				schuelerStammdaten.haltestelleID,
				schuelerStammdaten.hatMasernimpfnachweis,
				schuelerStammdaten.hatMigrationshintergrund,
				schuelerStammdaten.hausnummer,
				schuelerStammdaten.hausnummerZusatz,
				schuelerStammdaten.id,
				schuelerStammdaten.istBerufsschulpflichtErfuellt,
				schuelerStammdaten.istDuplikat,
				schuelerStammdaten.istSchulpflichtErfuellt,
				schuelerStammdaten.istVolljaehrig,
				schuelerStammdaten.keineAuskunftAnDritte,
				new ArrayList<>(),
				schuelerStammdaten.nachname,
				schuelerStammdaten.religionabmeldung,
				schuelerStammdaten.religionanmeldung,
				null,
				null,
				Nationalitaeten.getByDESTATIS(schuelerStammdaten.staatsangehoerigkeitID),
				Nationalitaeten.getByDESTATIS(schuelerStammdaten.staatsangehoerigkeit2ID),
				SchuelerStatus.data().getWertByKuerzel("" + schuelerStammdaten.status),
				schuelerStammdaten.strassenname,
				schuelerStammdaten.telefon,
				schuelerStammdaten.telefonMobil,
				schuelerStammdaten.verkehrspracheFamilie,
				schuelerStammdaten.vorname,
				schuelerStammdaten.alleVornamen,
				(schuelerStammdaten.wohnortID != null) ? reportingRepository.katalogOrte().get(schuelerStammdaten.wohnortID) : null,
				"",
				(schuelerStammdaten.ortsteilID != null) ? reportingRepository.katalogOrtsteile().get(schuelerStammdaten.ortsteilID) : null,
				"",
				schuelerStammdaten.zuzugsjahr);

		this.reportingRepository = reportingRepository;

		super.religion = this.reportingRepository.katalogReligionen().get(schuelerStammdaten.religionID);
		super.wohnortname = (super.wohnort() != null) ? super.wohnort().ortsname : "";
		super.wohnortsteilname = (super.wohnortsteil() != null) ? super.wohnortsteil().ortsteil : "";

		final List<Sprachbelegung> sprachbelegungen = new DataSchuelerSprachbelegung(reportingRepository.conn(), super.id()).getListSprachbelegungen();
		super.sprachbelegungen = sprachbelegungen.stream()
				.map(sb -> ((ReportingSchuelerSprachbelegung) new ProxyReportingSchuelerSprachbelegung(reportingRepository, sb))).toList();

		// Füge Stammdaten des Schülers für weitere Verwendung in der Map im Repository hinzu.
		this.reportingRepository.mapSchuelerStammdaten().putIfAbsent(super.id(), schuelerStammdaten);
	}


	/**
	 * Gibt das Repository mit den Daten der Schule und den zwischengespeicherten Daten zurück.
	 * @return Repository für die Reporting
	 */
	public ReportingRepository reportingRepository() {
		return reportingRepository;
	}


	/**
	 * Stellt die Daten des aktuellen Lernabschnitts des Schülers zur Verfügung.
	 * @return Daten des aktuellen Lernabschnitts, wenn dieser vorhanden ist.
	 */
	@Override
	public ReportingSchuelerLernabschnitt aktuellerLernabschnitt() {
		if (super.aktuellerLernabschnitt() == null) {
			if (this.reportingRepository.mapAktuelleLernabschnittsdaten().containsKey(this.id())) {
				super.aktuellerLernabschnitt = new ProxyReportingSchuelerLernabschnitt(this.reportingRepository,
						this.reportingRepository.mapAktuelleLernabschnittsdaten().get(this.id()));
			} else {
				if (!lernabschnitte().isEmpty()) {
					final List<ReportingSchuelerLernabschnitt> tmpListAbschnitte =
							lernabschnitte().stream().filter(a -> a.wechselNr() == 0)
									.filter(a -> a.schuljahresabschnitt().id() == this.reportingRepository.aktuellerSchuljahresabschnitt().id()).toList();
					if (!tmpListAbschnitte.isEmpty())
						super.aktuellerLernabschnitt = tmpListAbschnitte.getFirst();
				}
			}
		}
		return super.aktuellerLernabschnitt();
	}

	/**
	 * Stellt die Daten des ausgewählten Lernabschnitts des Schülers zur Verfügung.
	 * @return Daten des ausgewählten Lernabschnitts, wenn dieser vorhanden ist.
	 */
	@Override
	public ReportingSchuelerLernabschnitt auswahlLernabschnitt() {
		if (super.auswahlLernabschnitt() == null) {
			if (this.reportingRepository.mapAuswahlLernabschnittsdaten().containsKey(this.id())) {
				super.auswahlLernabschnitt = new ProxyReportingSchuelerLernabschnitt(this.reportingRepository,
						this.reportingRepository.mapAuswahlLernabschnittsdaten().get(this.id()));
			} else {
				if (!lernabschnitte().isEmpty()) {
					final List<ReportingSchuelerLernabschnitt> tmpListAbschnitte =
							lernabschnitte().stream().filter(a -> a.wechselNr() == 0)
									.filter(a -> a.schuljahresabschnitt().id() == this.reportingRepository.auswahlSchuljahresabschnitt().id()).toList();
					if (!tmpListAbschnitte.isEmpty())
						super.auswahlLernabschnitt = tmpListAbschnitte.getFirst();
				}
			}
		}
		return super.auswahlLernabschnitt();
	}

	/**
	 * Stellt die Daten zum Abitur in der GOSt des Schülers zur Verfügung.
	 *
	 * @return Daten zum Abitur in der GOSt
	 */
	@Override
	public ReportingSchuelerGostAbitur gostAbitur() {
		if (super.gostAbitur() == null) {
			if (this.reportingRepository.mapGostSchuelerAbiturdaten().containsKey(this.id())) {
				super.gostAbitur =
						new ProxyReportingSchuelerGostAbitur(this.reportingRepository, this.reportingRepository.mapGostSchuelerAbiturdaten().get(this.id()));
			} else {
				try {
					final Abiturdaten abiturdaten = DBUtilsGostAbitur.getAbiturdaten(this.reportingRepository.conn(), this.id());
					super.gostAbitur = new ProxyReportingSchuelerGostAbitur(this.reportingRepository, abiturdaten);
				} catch (final ApiOperationException e) {
					ReportingExceptionUtils.putStacktraceInLog(
							"INFO: Fehler mit definiertem Rückgabewert abgefangen bei der Bestimmung der GOSt-Abiturdaten eines Schülers.", e,
							reportingRepository.logger(), LogLevel.INFO, 0);
					return null;
				}
			}
		}
		return super.gostAbitur();
	}

	/**
	 * Stellt die Daten der GOSt-Laufbahnplanung des Schülers zur Verfügung.
	 * @return Daten der GOSt-Laufbahnplanung
	 */
	@Override
	public ReportingSchuelerGostLaufbahnplanung gostLaufbahnplanung() {
		if (super.gostLaufbahnplanung() == null) {
			super.gostLaufbahnplanung = new ProxyReportingSchuelerGostLaufbahnplanung(this.reportingRepository, this);
		}
		return super.gostLaufbahnplanung();
	}

	/**
	 * Stellt die Daten aller Lernabschnitte des Schülers in einer Liste zur Verfügung.
	 * @return Liste der Daten aller Lernabschnitte
	 */
	@Override
	public List<ReportingSchuelerLernabschnitt> lernabschnitte() {
		if (super.lernabschnitte().isEmpty()) {
			final List<SchuelerLernabschnittsdaten> schuelerLernabschnittsdaten = new ArrayList<>();
			try {
				schuelerLernabschnittsdaten.addAll(new DataSchuelerLernabschnittsdaten(this.reportingRepository().conn())
						.getListFromSchuelerIDs(new ArrayList<>(List.of(this.id())), true));
			} catch (final ApiOperationException e) {
				ReportingExceptionUtils.putStacktraceInLog(
						"INFO: Fehler mit definiertem Rückgabewert abgefangen bei der Bestimmung der Lernabschnitte eines Schülers.", e,
						reportingRepository.logger(), LogLevel.INFO, 0);
			}
			// Wenn, wie bei einer Neuaufnahme, keine Lernabschnitte vorhanden sind, gebe die leere Liste zurück.
			if (schuelerLernabschnittsdaten.isEmpty()) {
				super.aktuellerLernabschnitt = null;
				super.auswahlLernabschnitt = null;
				return super.lernabschnitte();
			}
			super.lernabschnitte = schuelerLernabschnittsdaten.stream()
					.map(a -> (ReportingSchuelerLernabschnitt) new ProxyReportingSchuelerLernabschnitt(this.reportingRepository, a))
					.sorted(Comparator
							.comparing((final ReportingSchuelerLernabschnitt a) -> a.schuljahresabschnitt().schuljahr())
							.thenComparing((final ReportingSchuelerLernabschnitt a) -> a.schuljahresabschnitt().abschnitt())
							.thenComparing(ReportingSchuelerLernabschnitt::wechselNr))
					.toList();
			// Aktuellen Lernabschnitt in der Map im Repository speichern. Gibt es nur einen Abschnitt, dann wird dieser gespeichert, andernfalls der mit WechselNr. 0 im aktuellen Schuljahresabschnitt.
			if (schuelerLernabschnittsdaten.size() == 1) {
				this.reportingRepository.mapAktuelleLernabschnittsdaten()
						.computeIfAbsent(super.id(), l -> schuelerLernabschnittsdaten.getFirst());
				if (reportingRepository.reportingParameter().idSchuljahresabschnitt == reportingRepository.mapAktuelleLernabschnittsdaten()
						.get(super.id()).schuljahresabschnitt)
					this.reportingRepository.mapAuswahlLernabschnittsdaten()
							.computeIfAbsent(super.id(), l -> schuelerLernabschnittsdaten.getFirst());
			} else {
				final List<SchuelerLernabschnittsdaten> tmpListAktuell =
						schuelerLernabschnittsdaten.stream().filter(a -> a.wechselNr == 0)
								.filter(a -> a.schuljahresabschnitt == this.reportingRepository.aktuellerSchuljahresabschnitt().id()).toList();
				final List<SchuelerLernabschnittsdaten> tmpListAuswahl =
						schuelerLernabschnittsdaten.stream().filter(a -> a.wechselNr == 0)
								.filter(a -> a.schuljahresabschnitt == this.reportingRepository.auswahlSchuljahresabschnitt().id()).toList();
				if (!tmpListAktuell.isEmpty())
					this.reportingRepository.mapAktuelleLernabschnittsdaten().computeIfAbsent(super.id(), l -> tmpListAktuell.getFirst());
				if (!tmpListAuswahl.isEmpty())
					this.reportingRepository.mapAuswahlLernabschnittsdaten().computeIfAbsent(super.id(), l -> tmpListAuswahl.getFirst());
			}
		}
		return super.lernabschnitte();
	}
}
