package de.svws_nrw.data.gost;

import de.svws_nrw.core.data.gost.AbiturFachbelegung;
import de.svws_nrw.core.data.gost.AbiturFachbelegungHalbjahr;
import de.svws_nrw.core.data.gost.Abiturdaten;
import de.svws_nrw.core.data.gost.GostFach;
import de.svws_nrw.core.data.gost.GostLeistungen;
import de.svws_nrw.core.data.gost.GostLeistungenFachbelegung;
import de.svws_nrw.core.data.gost.GostLeistungenFachwahl;
import de.svws_nrw.asd.types.Note;
import de.svws_nrw.asd.types.fach.Fach;
import de.svws_nrw.core.types.gost.AbiturBelegungsart;
import de.svws_nrw.core.types.gost.GostAbiturFach;
import de.svws_nrw.core.types.gost.GostHalbjahr;
import de.svws_nrw.core.types.gost.GostKursart;
import de.svws_nrw.core.utils.gost.GostFaecherManager;
import de.svws_nrw.data.faecher.DBUtilsFaecherGost;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.faecher.DTOFach;
import de.svws_nrw.db.dto.current.schild.schueler.DTOSchueler;
import de.svws_nrw.db.dto.current.schild.schueler.DTOSchuelerSprachenfolge;
import de.svws_nrw.db.dto.current.schild.schueler.abitur.DTOSchuelerAbitur;
import de.svws_nrw.db.dto.current.schild.schueler.abitur.DTOSchuelerAbiturFach;
import de.svws_nrw.db.dto.current.schild.schule.DTOSchuljahresabschnitte;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.Response.Status;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * Diese Klasse stellt Hilfsmethoden für den Zugriff auf Informationen
 * zu dem Abiturbereich von Schülern der gymnasialen Oberstufe zur Verfügung.
 */
public final class DBUtilsGostAbitur {

	private DBUtilsGostAbitur() {
		throw new IllegalStateException("Instantiation of " + DBUtilsGostAbitur.class.getName() + " not allowed");
	}

	/**
	 * Ermittelt die für das Abitur relevanten Daten für den Schüler mit der angegebenen
	 * ID aus den Leistungsdaten in der Datenbank.
	 *
	 * @param conn   die Datenbankverbindung
	 * @param id     die ID des Schülers
	 *
	 * @return die für das Abitur relevanten Daten für den Schüler mit der angegebenen ID
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static Abiturdaten getAbiturdatenAusLeistungsdaten(final DBEntityManager conn, final long id) throws ApiOperationException {
		final GostLeistungen leistungen = DBUtilsGost.getLeistungsdaten(conn, id);
		if (leistungen == null)
			throw new ApiOperationException(Status.NOT_FOUND);

		// TODO bestimme ggf. einen Teil der Daten aus den LuPO-Wahlen des Schülers

		if (!"Q2".equals(leistungen.aktuellerJahrgang))
			return null;
		final Abiturdaten abidaten = new Abiturdaten();
		abidaten.schuelerID = leistungen.id;
		abidaten.schuljahrAbitur = leistungen.aktuellesSchuljahr;
		abidaten.abiturjahr = abidaten.schuljahrAbitur + 1;
		abidaten.sprachendaten = leistungen.sprachendaten;
		abidaten.bilingualeSprache = leistungen.bilingualeSprache;
		abidaten.projektKursThema = leistungen.projektkursThema;
		abidaten.projektkursLeitfach1Kuerzel = leistungen.projektkursLeitfach1Kuerzel;
		abidaten.projektkursLeitfach2Kuerzel = leistungen.projektkursLeitfach2Kuerzel;
		for (final GostHalbjahr hj : GostHalbjahr.values())
			abidaten.bewertetesHalbjahr[hj.id] = leistungen.bewertetesHalbjahr[hj.id];
		for (final GostLeistungenFachwahl leistungenFach : leistungen.faecher) {
			GostHalbjahr letzteBelegungHalbjahr = null;   // das Halbjahr der letzten Belegung
			final AbiturFachbelegung fach = new AbiturFachbelegung();
			fach.fachID = leistungenFach.fach.id;
			fach.istFSNeu = leistungenFach.istFSNeu;
			fach.abiturFach = (GostAbiturFach.fromID(leistungenFach.abiturfach) == null) ? null : leistungenFach.abiturfach;
			for (final GostLeistungenFachbelegung leistungenBelegung : leistungenFach.belegungen) {
				if (!leistungenBelegung.abschnittGewertet)
					continue;

				// Nehme jeweils die Kursart, welche beim letzten gewerteten Abschnitt eingetragen ist
				if (((letzteBelegungHalbjahr == null) || (GostHalbjahr.fromKuerzel(leistungenBelegung.halbjahrKuerzel).compareTo(letzteBelegungHalbjahr) > 0))
						&& (GostHalbjahr.fromKuerzel(leistungenBelegung.halbjahrKuerzel) != null) && leistungenBelegung.abschnittGewertet) {
					letzteBelegungHalbjahr = GostHalbjahr.fromKuerzel(leistungenBelegung.halbjahrKuerzel);
					fach.letzteKursart = (GostKursart.fromKuerzel(leistungenBelegung.kursartKuerzel) == null) ? null
							: GostKursart.fromKuerzel(leistungenBelegung.kursartKuerzel).kuerzel;
				}

				// Erzeuge die zugehörige Belegung
				final AbiturFachbelegungHalbjahr belegung = new AbiturFachbelegungHalbjahr();
				belegung.halbjahrKuerzel = (GostHalbjahr.fromKuerzel(leistungenBelegung.halbjahrKuerzel) == null) ? null
						: GostHalbjahr.fromKuerzel(leistungenBelegung.halbjahrKuerzel).kuerzel;
				belegung.kursartKuerzel = (GostKursart.fromKuerzel(leistungenBelegung.kursartKuerzel) == null) ? null
						: GostKursart.fromKuerzel(leistungenBelegung.kursartKuerzel).kuerzel;
				belegung.schriftlich = leistungenBelegung.istSchriftlich;
				belegung.biliSprache = leistungenBelegung.bilingualeSprache;
				belegung.lehrer = leistungenBelegung.lehrer;
				belegung.wochenstunden = leistungenBelegung.wochenstunden;
				belegung.fehlstundenGesamt = leistungenBelegung.fehlstundenGesamt;
				belegung.fehlstundenUnentschuldigt = leistungenBelegung.fehlstundenUnentschuldigt;
				belegung.notenkuerzel = Note.fromKuerzel(leistungenBelegung.notenKuerzel).daten(abidaten.schuljahrAbitur).kuerzel;
				fach.belegungen[GostHalbjahr.fromKuerzel(belegung.halbjahrKuerzel).id] = belegung;
			}
			abidaten.fachbelegungen.add(fach);
		}

		// Bestimmt die Fehlstunden-Summe für den Block I (Qualifikationsphase) anhand der Fehlstunden bei den einzelnen Kurs-Belegungen.
		int block1FehlstundenGesamt = 0;
		int block1FehlstundenUnentschuldigt = 0;
		for (final AbiturFachbelegung fach : abidaten.fachbelegungen) {
			for (final AbiturFachbelegungHalbjahr belegung : fach.belegungen) {
				if ((belegung == null) || !GostHalbjahr.fromKuerzel(belegung.halbjahrKuerzel).istQualifikationsphase())
					continue;
				block1FehlstundenGesamt += belegung.fehlstundenGesamt;
				block1FehlstundenUnentschuldigt += belegung.fehlstundenUnentschuldigt;
			}
		}
		abidaten.block1FehlstundenGesamt = block1FehlstundenGesamt;
		abidaten.block1FehlstundenUnentschuldigt = block1FehlstundenUnentschuldigt;

		// und gib die Abiturdaten zurück...
		return abidaten;
	}



	/**
	 * Ermittelt die für das Abitur relevanten Daten für den Schüler mit der angegebenen
	 * ID aus den in der Datenbank gespeicherten Abiturtabellen.
	 *
	 * @param conn   die Datenbank-Verbindung
	 * @param id     die ID des Schülers
	 *
	 * @return die für das Abitur relevanten Daten für den Schüler mit der angegebenen ID
	 *
	 * @throws ApiOperationException   im Fehlerfall
	 */
	public static Abiturdaten getAbiturdaten(final DBEntityManager conn, final long id) throws ApiOperationException {
		// Ermittle für einen Vergleich die Abiturdaten für Block I aus den Leistungsdaten, nutze dafür den entsprechenden Service
		final Abiturdaten abidatenVergleich = getAbiturdatenAusLeistungsdaten(conn, id);

		// Ermittle nun zunächst die Abiturdaten aus den entsprechenden Tabellen
		final DTOSchueler dtoSchueler = conn.queryByKey(DTOSchueler.class, id);
		if (dtoSchueler == null)
			throw new ApiOperationException(Status.NOT_FOUND);

		final Map<Long, DTOSchuljahresabschnitte> schuljahresabschnitte =
				conn.queryAll(DTOSchuljahresabschnitte.class).stream().collect(Collectors.toMap(a -> a.ID, a -> a));
		final DTOSchuljahresabschnitte dtoAbschnitt = schuljahresabschnitte.get(dtoSchueler.Schuljahresabschnitts_ID);
		if (dtoAbschnitt == null)
			throw new ApiOperationException(Status.NOT_FOUND);

		// Lese die Abiturdaten anhand der ID aus der Datenbank
		final List<DTOSchuelerAbitur> dtosSchuelerAbitur = conn.queryList(DTOSchuelerAbitur.QUERY_BY_SCHUELER_ID, DTOSchuelerAbitur.class, id);
		if ((dtosSchuelerAbitur == null) || (dtosSchuelerAbitur.isEmpty()))
			throw new ApiOperationException(Status.NOT_FOUND);
		// TODO if (dtosSchuelerAbitur.size() > 1) - Es existieren mehrere Abiturdatensätze für den Schüler mit der ID - TODO neueren Jahrgang auswählen
		final DTOSchuelerAbitur dtoSchuelerAbitur = dtosSchuelerAbitur.get(0);
		final List<DTOSchuelerAbiturFach> faecher = conn.queryList(DTOSchuelerAbiturFach.QUERY_BY_SCHUELER_ID, DTOSchuelerAbiturFach.class, id);
		if (faecher == null)
			throw new ApiOperationException(Status.NOT_FOUND);

		// Lese beide Tabellen mit den Informationen zu den belegten oder geprüften Sprachen aus.
		final List<DTOSchuelerSprachenfolge> sprachenfolge = conn.queryList(DTOSchuelerSprachenfolge.QUERY_BY_SCHUELER_ID, DTOSchuelerSprachenfolge.class, id);

		final DTOSchuljahresabschnitte dtoAbschnittPruefung = schuljahresabschnitte.get(dtoSchuelerAbitur.Schuljahresabschnitts_ID);

		// Bestimme zunächst das Abiturjahr
		Integer abiturjahr = null;
		if (dtoSchuelerAbitur.Schuljahresabschnitts_ID != null) {
			final DTOSchuljahresabschnitte abschnitt = conn.queryByKey(DTOSchuljahresabschnitte.class, dtoSchuelerAbitur.Schuljahresabschnitts_ID);
			if (abschnitt != null)
				abiturjahr = abschnitt.Jahr + 1;
		}
		if (abiturjahr == null)
			abiturjahr = abidatenVergleich.abiturjahr;

		// Lese die Oberstufenfächer aus der DB ein, um schnell Daten zu einzelnen Fächern nachschlagen zu können
		final GostFaecherManager gostFaecher = DBUtilsFaecherGost.getFaecherManager(abidatenVergleich.schuljahrAbitur, conn, abiturjahr);

		// Kopiere die DTOs in die Abiturdaten-Klasse
		final Abiturdaten abidaten = new Abiturdaten();
		abidaten.schuelerID = dtoSchuelerAbitur.Schueler_ID;
		abidaten.schuljahrAbitur = (dtoAbschnittPruefung == null) ? null : dtoAbschnittPruefung.Jahr;
		abidaten.abiturjahr = abiturjahr;
		abidaten.projektKursThema = dtoSchuelerAbitur.ProjektkursThema;
		abidaten.block1FehlstundenGesamt = (dtoSchuelerAbitur.FehlstundenSumme == null) ? -1 : dtoSchuelerAbitur.FehlstundenSumme;
		abidaten.block1FehlstundenUnentschuldigt = (dtoSchuelerAbitur.FehlstundenSummeUnentschuldigt == null) ? -1
				: dtoSchuelerAbitur.FehlstundenSummeUnentschuldigt;
		abidaten.latinum = false;
		for (final DTOSchuelerSprachenfolge folge : sprachenfolge)
			if ((Fach.L == Fach.data().getWertByKuerzel(folge.Sprache)) && (folge.LatinumErreicht != null) && (folge.LatinumErreicht)) {
				abidaten.latinum = true;
				break;
			}
		abidaten.kleinesLatinum = false;
		for (final DTOSchuelerSprachenfolge folge : sprachenfolge)
			if ((Fach.L == Fach.data().getWertByKuerzel(folge.Sprache)) && (folge.KleinesLatinumErreicht != null) && (folge.KleinesLatinumErreicht)) {
				abidaten.kleinesLatinum = true;
				break;
			}
		abidaten.graecum = false;
		for (final DTOSchuelerSprachenfolge folge : sprachenfolge)
			if ((Fach.G == Fach.data().getWertByKuerzel(folge.Sprache)) && (folge.GraecumErreicht != null) && (folge.GraecumErreicht)) {
				abidaten.graecum = true;
				break;
			}
		abidaten.hebraicum = false;
		for (final DTOSchuelerSprachenfolge folge : sprachenfolge)
			if ((Fach.H == Fach.data().getWertByKuerzel(folge.Sprache)) && (folge.HebraicumErreicht != null) && (folge.HebraicumErreicht)) {
				abidaten.hebraicum = true;
				break;
			}
		abidaten.besondereLernleistung = dtoSchuelerAbitur.BesondereLernleistungArt.kuerzel;
		abidaten.besondereLernleistungNotenKuerzel =
				Note.fromNotenpunkte(dtoSchuelerAbitur.BesondereLernleistungNotenpunkte).daten(abidaten.abiturjahr - 1).kuerzel;
		abidaten.besondereLernleistungThema = dtoSchuelerAbitur.BesondereLernleistungThema;

		abidaten.block1AnzahlKurse = dtoSchuelerAbitur.BlockI_AnzahlKurseEingebracht;
		abidaten.block1DefiziteGesamt = dtoSchuelerAbitur.BlockI_AnzahlDefiziteEingebracht;
		abidaten.block1DefiziteLK = dtoSchuelerAbitur.BlockI_AnzahlDefiziteLK;
		abidaten.block1PunktSummeGK = dtoSchuelerAbitur.BlockI_SummeNotenpunkteGK;
		abidaten.block1PunktSummeLK = dtoSchuelerAbitur.BlockI_SummeNotenpunkteLK;
		abidaten.block1PunktSummeNormiert = dtoSchuelerAbitur.BlockI_PunktsummeNormiert;
		abidaten.block1NotenpunkteDurchschnitt = dtoSchuelerAbitur.BlockI_NotenpunktdurchschnittEingebrachterKurse;
		abidaten.block1Zulassung = !"-".equals(dtoSchuelerAbitur.BlockI_HatZulassung);
		abidaten.freiwilligerRuecktritt = "R".equals(dtoSchuelerAbitur.BlockI_HatZulassung);

		abidaten.block2DefiziteGesamt = dtoSchuelerAbitur.Pruefung_AnzahlDefizite;
		abidaten.block2DefiziteLK = dtoSchuelerAbitur.Pruefung_AnzahlDefiziteLK;
		abidaten.block2PunktSumme = dtoSchuelerAbitur.Pruefung_Punktsumme;

		abidaten.gesamtPunkte = dtoSchuelerAbitur.AbiturGesamtPunktzahl;
		abidaten.gesamtPunkteVerbesserung = dtoSchuelerAbitur.VerbesserungAbPunktzahl;
		abidaten.pruefungBestanden = dtoSchuelerAbitur.Pruefung_hatBestanden;
		abidaten.note = dtoSchuelerAbitur.AbiturNote;

		// Füge die Fächerdaten hinzu...
		for (final DTOSchuelerAbiturFach dto : faecher) {
			final AbiturFachbelegung fach = new AbiturFachbelegung();
			final GostFach gostFach = gostFaecher.get(dto.Fach_ID);
			fach.fachID = dto.Fach_ID;
			fach.letzteKursart = (dto.KursartAllgemein == null) ? null : dto.KursartAllgemein.kuerzel;
			fach.abiturFach = (dto.AbiturFach == null) ? null : dto.AbiturFach.id;
			if (dto.KursartAllgemein == GostKursart.PJK) {
				if (gostFach == null) {
					final DTOFach dtoFach = conn.queryByKey(DTOFach.class, dto.Fach_ID);
					final DTOFach dtoLeitfach1 =
							(dtoFach.ProjektKursLeitfach1_ID == null) ? null : conn.queryByKey(DTOFach.class, dtoFach.ProjektKursLeitfach1_ID);
					final DTOFach dtoLeitfach2 =
							(dtoFach.ProjektKursLeitfach2_ID == null) ? null : conn.queryByKey(DTOFach.class, dtoFach.ProjektKursLeitfach2_ID);
					abidaten.projektkursLeitfach1Kuerzel = (dtoLeitfach1 == null) ? null : dtoLeitfach1.Kuerzel;
					abidaten.projektkursLeitfach2Kuerzel = (dtoLeitfach2 == null) ? null : dtoLeitfach2.Kuerzel;
				} else {
					abidaten.projektkursLeitfach1Kuerzel = gostFach.projektKursLeitfach1Kuerzel;
					abidaten.projektkursLeitfach2Kuerzel = gostFach.projektKursLeitfach2Kuerzel;
				}
			}
			fach.block1PunktSumme = dto.ZulassungPunktsumme;
			fach.block1NotenpunkteDurchschnitt = dto.ZulassungNotenpunktdurchschnitt;

			fach.block2NotenKuerzelPruefung = Note.fromNotenpunkte(dto.PruefungNotenpunkte).daten(abidaten.abiturjahr - 1).kuerzel;
			fach.block2PunkteZwischenstand = dto.PruefungPunktsummeZwischenstand;
			fach.block2MuendlichePruefungAbweichung = dto.PruefungMuendlichAbweichung;
			fach.block2MuendlichePruefungBestehen = dto.PruefungMuendlichBestehen;
			fach.block2MuendlichePruefungFreiwillig = dto.PruefungMuendlichFreiwillig;
			fach.block2MuendlichePruefungReihenfolge = dto.PruefungMuendlichReihenfolge;
			fach.block2MuendlichePruefungNotenKuerzel = Note.fromNotenpunkte(dto.PruefungMuendlichNotenpunkte).daten(abidaten.abiturjahr - 1).kuerzel;
			fach.block2Punkte = dto.PruefungPunktsummeGesamt;
			fach.block2Pruefer = dto.Fachlehrer_ID;
			if (dto.EF_HJ1_BelegungArt != AbiturBelegungsart.NICHT_BELEGT) {
				final AbiturFachbelegungHalbjahr ef1 = new AbiturFachbelegungHalbjahr();
				ef1.halbjahrKuerzel = GostHalbjahr.EF1.kuerzel;
				ef1.notenkuerzel = Note.fromNotenpunkteString(dto.EF_HJ1_Notenpunkte).daten(abidaten.abiturjahr - 1).kuerzel;
				ef1.schriftlich = (dto.EF_HJ1_BelegungArt == AbiturBelegungsart.SCHRIFTLICH);
				ef1.block1gewertet = false;
				ef1.block1kursAufZeugnis = false;
				fach.belegungen[GostHalbjahr.EF1.id] = ef1;
			}
			if (dto.EF_HJ2_BelegungArt != AbiturBelegungsart.NICHT_BELEGT) {
				final AbiturFachbelegungHalbjahr ef2 = new AbiturFachbelegungHalbjahr();
				ef2.halbjahrKuerzel = GostHalbjahr.EF2.kuerzel;
				ef2.notenkuerzel = Note.fromNotenpunkteString(dto.EF_HJ2_Notenpunkte).daten(abidaten.abiturjahr - 1).kuerzel;
				ef2.schriftlich = (dto.EF_HJ2_BelegungArt == AbiturBelegungsart.SCHRIFTLICH);
				ef2.block1gewertet = false;
				ef2.block1kursAufZeugnis = false;
				fach.belegungen[GostHalbjahr.EF2.id] = ef2;
			}
			if (dto.Q1_HJ1_BelegungArt != AbiturBelegungsart.NICHT_BELEGT) {
				final AbiturFachbelegungHalbjahr q11 = new AbiturFachbelegungHalbjahr();
				q11.halbjahrKuerzel = GostHalbjahr.Q11.kuerzel;
				q11.notenkuerzel = Note.fromNotenpunkteString(dto.Q1_HJ1_Notenpunkte).daten(abidaten.abiturjahr - 1).kuerzel;
				q11.schriftlich = (dto.Q1_HJ1_BelegungArt == AbiturBelegungsart.SCHRIFTLICH);
				q11.wochenstunden = dto.Q1_HJ1_Wochenstunden;
				q11.block1gewertet = dto.Q1_HJ1_MarkiertFuerAbiturBerechnung.fuerBerechnung;
				q11.block1kursAufZeugnis = dto.Q1_HJ1_MarkiertFuerAbiturBerechnung.aufAbiturZeugnis;
				fach.belegungen[GostHalbjahr.Q11.id] = q11;
			}
			if (dto.Q1_HJ2_BelegungArt != AbiturBelegungsart.NICHT_BELEGT) {
				final AbiturFachbelegungHalbjahr q12 = new AbiturFachbelegungHalbjahr();
				q12.halbjahrKuerzel = GostHalbjahr.Q12.kuerzel;
				q12.notenkuerzel = Note.fromNotenpunkteString(dto.Q1_HJ2_Notenpunkte).daten(abidaten.abiturjahr - 1).kuerzel;
				q12.schriftlich = (dto.Q1_HJ2_BelegungArt == AbiturBelegungsart.SCHRIFTLICH);
				q12.wochenstunden = dto.Q1_HJ2_Wochenstunden;
				q12.block1gewertet = dto.Q1_HJ2_MarkiertFuerAbiturBerechnung.fuerBerechnung;
				q12.block1kursAufZeugnis = dto.Q1_HJ2_MarkiertFuerAbiturBerechnung.aufAbiturZeugnis;
				fach.belegungen[GostHalbjahr.Q12.id] = q12;
			}
			if (dto.Q2_HJ1_BelegungArt != AbiturBelegungsart.NICHT_BELEGT) {
				final AbiturFachbelegungHalbjahr q21 = new AbiturFachbelegungHalbjahr();
				q21.halbjahrKuerzel = GostHalbjahr.Q21.kuerzel;
				q21.notenkuerzel = Note.fromNotenpunkteString(dto.Q2_HJ1_Notenpunkte).daten(abidaten.abiturjahr - 1).kuerzel;
				q21.schriftlich = (dto.Q2_HJ1_BelegungArt == AbiturBelegungsart.SCHRIFTLICH);
				q21.wochenstunden = dto.Q2_HJ1_Wochenstunden;
				q21.block1gewertet = dto.Q2_HJ1_MarkiertFuerAbiturBerechnung.fuerBerechnung;
				q21.block1kursAufZeugnis = dto.Q2_HJ1_MarkiertFuerAbiturBerechnung.aufAbiturZeugnis;
				fach.belegungen[GostHalbjahr.Q21.id] = q21;
			}
			if (dto.Q2_HJ2_BelegungArt != AbiturBelegungsart.NICHT_BELEGT) {
				final AbiturFachbelegungHalbjahr q22 = new AbiturFachbelegungHalbjahr();
				q22.halbjahrKuerzel = GostHalbjahr.Q22.kuerzel;
				q22.notenkuerzel = Note.fromNotenpunkteString(dto.Q2_HJ2_Notenpunkte).daten(abidaten.abiturjahr - 1).kuerzel;
				q22.schriftlich = (dto.Q2_HJ2_BelegungArt == AbiturBelegungsart.SCHRIFTLICH);
				q22.wochenstunden = dto.Q2_HJ2_Wochenstunden;
				q22.block1gewertet = dto.Q2_HJ2_MarkiertFuerAbiturBerechnung.fuerBerechnung;
				q22.block1kursAufZeugnis = dto.Q2_HJ2_MarkiertFuerAbiturBerechnung.aufAbiturZeugnis;
				fach.belegungen[GostHalbjahr.Q22.id] = q22;
			}
			abidaten.fachbelegungen.add(fach);
		}

		// Markiere alles Gost-Halbjahre als gewertet
		for (final GostHalbjahr hj : GostHalbjahr.values())
			abidaten.bewertetesHalbjahr[hj.id] = true;

		// Kopiere Abiturdaten, welche nicht in den Abitur-DB-Tabellen direkt vorhanden sind
		abidaten.sprachendaten = abidatenVergleich.sprachendaten;
		abidaten.bilingualeSprache = abidatenVergleich.bilingualeSprache;
		for (final AbiturFachbelegung fach : abidaten.fachbelegungen) {
			AbiturFachbelegung fachVergleich = null;
			for (final AbiturFachbelegung f : abidatenVergleich.fachbelegungen) {
				if (f.fachID == fach.fachID) {
					fachVergleich = f;
					break;
				}
			}
			if (fachVergleich == null)
				continue;
			fach.istFSNeu = fachVergleich.istFSNeu;
			for (final AbiturFachbelegungHalbjahr belegung : fach.belegungen) {
				if (belegung == null)
					continue;
				final AbiturFachbelegungHalbjahr belegungVergleich = fachVergleich.belegungen[GostHalbjahr.fromKuerzel(belegung.halbjahrKuerzel).id];
				if (belegungVergleich == null)
					continue;
				if (GostHalbjahr.fromKuerzel(belegung.halbjahrKuerzel).istEinfuehrungsphase()) {
					belegung.wochenstunden = belegungVergleich.wochenstunden;
				}
				final GostKursart tmpKursart = GostKursart.fromKuerzel(belegungVergleich.kursartKuerzel);
				belegung.kursartKuerzel = (tmpKursart == null) ? null : tmpKursart.kuerzel;
				fach.letzteKursart = belegung.kursartKuerzel;
				belegung.biliSprache = belegungVergleich.biliSprache;
				belegung.lehrer = belegungVergleich.lehrer;
				belegung.fehlstundenGesamt = belegungVergleich.fehlstundenGesamt;
				belegung.fehlstundenUnentschuldigt = belegungVergleich.fehlstundenUnentschuldigt;
			}
		}

		// gib die Abiturdaten zurück.
		return abidaten;
	}

}
