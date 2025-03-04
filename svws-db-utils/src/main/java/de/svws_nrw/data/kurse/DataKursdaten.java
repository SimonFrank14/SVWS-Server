package de.svws_nrw.data.kurse;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.function.ObjLongConsumer;
import java.util.stream.Collectors;

import de.svws_nrw.core.data.kurse.KursDaten;
import de.svws_nrw.core.types.KursFortschreibungsart;
import de.svws_nrw.data.DTOMapper;
import de.svws_nrw.data.DataBasicMapper;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.data.schueler.DataSchuelerliste;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.faecher.DTOFach;
import de.svws_nrw.db.dto.current.schild.kurse.DTOKurs;
import de.svws_nrw.db.dto.current.schild.kurse.DTOKursSchueler;
import de.svws_nrw.db.dto.current.schild.lehrer.DTOLehrer;
import de.svws_nrw.db.dto.current.schild.schueler.DTOSchueler;
import de.svws_nrw.db.dto.current.schild.schule.DTOJahrgang;
import de.svws_nrw.db.dto.current.schild.schule.DTOSchuljahresabschnitte;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den
 * Core-DTO {@link KursDaten}.
 */
public final class DataKursdaten extends DataManager<Long> {

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO {@link KursDaten}.
	 *
	 * @param conn   die Datenbank-Verbindung für den Datenbankzugriff
	 */
	public DataKursdaten(final DBEntityManager conn) {
		super(conn);
	}


	private static List<Integer> convertSchienenStrToList(final String strSchienen) {
		final List<Integer> result = new ArrayList<>();
		if ((strSchienen != null) && (!strSchienen.isBlank())) {
			for (final String strSchiene : strSchienen.split(",")) {
				if ("".equals(strSchiene.trim()))
					continue;
				try {
					result.add(Integer.parseInt(strSchiene.trim()));
				} catch (@SuppressWarnings("unused") final NumberFormatException nfe) {
					// ignore exception
				}
			}
		}
		return result;
	}


	private static List<Long> convertJahrgaenge(final DTOKurs kurs) {
		final List<Long> result = new ArrayList<>();
		if (kurs.Jahrgang_ID != null)
			result.add(kurs.Jahrgang_ID);
		if (kurs.Jahrgaenge != null)
			for (final String jahrgang : kurs.Jahrgaenge.split(","))
				if (jahrgang.matches("^\\d+$"))
					result.add(Long.parseLong(jahrgang));
		return result;
	}


	/**
	 * Lambda-Ausdruck zum Umwandeln eines Datenbank-DTOs {@link DTOKurs} in einen Core-DTO {@link KursDaten}.
	 */
	public static final DTOMapper<DTOKurs, KursDaten> dtoMapper = (final DTOKurs kurs) -> {
		final KursDaten daten = new KursDaten();
		daten.id = kurs.ID;
		daten.idSchuljahresabschnitt = kurs.Schuljahresabschnitts_ID;
		daten.kuerzel = kurs.KurzBez;
		daten.idJahrgaenge.addAll(convertJahrgaenge(kurs));
		daten.idFach = kurs.Fach_ID;
		daten.lehrer = kurs.Lehrer_ID;
		daten.kursartAllg = (kurs.KursartAllg == null) ? "" : kurs.KursartAllg;
		daten.sortierung = (kurs.Sortierung == null) ? 32000 : kurs.Sortierung;
		daten.istSichtbar = kurs.Sichtbar;
		daten.schienen.addAll(convertSchienenStrToList(kurs.Schienen));
		daten.wochenstunden = (kurs.WochenStd == null) ? 0 : kurs.WochenStd;
		daten.wochenstundenLehrer = (kurs.WochenstdKL == null) ? daten.wochenstunden : kurs.WochenstdKL;
		daten.idKursFortschreibungsart = kurs.Fortschreibungsart.id;
		daten.schulnummer = kurs.SchulNr;
		daten.istEpochalunterricht = kurs.EpochU;
		daten.bezeichnungZeugnis = kurs.ZeugnisBez;
		if ((daten.bezeichnungZeugnis != null) && daten.bezeichnungZeugnis.isBlank())
			daten.bezeichnungZeugnis = null;
		return daten;
	};

	@Override
	public Response getAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Response getList() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Ermittelt die Daten zu dem Kurs mit der angegebenen ID.
	 *
	 * @param conn   die Datenbankverbindung
	 * @param id     die ID des Kurses
	 *
	 * @return die Daten des Kurses
	 *
	 * @throws ApiOperationException im Fehlerfall
	 */
	public static KursDaten getKursdaten(final DBEntityManager conn, final Long id) throws ApiOperationException {
		if (id == null)
			throw new ApiOperationException(Status.NOT_FOUND);
		final DTOKurs kurs = conn.queryByKey(DTOKurs.class, id);
		if (kurs == null)
			throw new ApiOperationException(Status.NOT_FOUND);
		final KursDaten daten = dtoMapper.apply(kurs);
		// Bestimme die Schüler des Kurses
		final List<DTOKursSchueler> listKursSchueler =
				conn.queryList("SELECT e FROM DTOKursSchueler e WHERE e.Kurs_ID = ?1 AND e.LernabschnittWechselNr = 0", DTOKursSchueler.class, daten.id);
		final List<Long> schuelerIDs = listKursSchueler.stream().map(ks -> ks.Schueler_ID).toList();
		final List<DTOSchueler> listSchueler = ((schuelerIDs == null) || (schuelerIDs.isEmpty())) ? new ArrayList<>()
				: conn.queryByKeyList(DTOSchueler.class, schuelerIDs);
		for (final DTOSchueler dto : listSchueler)
			daten.schueler.add(DataSchuelerliste.mapToSchueler(dto, null));  // TODO Abschlussjahrgang bestimmen
		return daten;
	}


	@Override
	public Response get(final Long id) throws ApiOperationException {
		final KursDaten daten = getKursdaten(conn, id);
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}


	private static final Map<String, DataBasicMapper<DTOKurs>> patchMappings = Map.ofEntries(
			Map.entry("id", (conn, dto, value, map) -> {
				final Long patch_id = JSONMapper.convertToLong(value, true);
				if ((patch_id == null) || (patch_id.longValue() != dto.ID))
					throw new ApiOperationException(Status.BAD_REQUEST, "Die ID im Patch stimmt nicht mit der ID des Datenbank-Objektes überein");
			}),
			Map.entry("idSchuljahresabschnitt", (conn, dto, value, map) -> {
				final Long idAbschnitt = JSONMapper.convertToLong(value, true);
				if (idAbschnitt == null)
					throw new ApiOperationException(Status.BAD_REQUEST, "Die ID des Shuljahresabschnittes darf nicht null sein.");
				final DTOSchuljahresabschnitte abschnitt = conn.queryByKey(DTOSchuljahresabschnitte.class, idAbschnitt);
				if (abschnitt == null)
					throw new ApiOperationException(Status.NOT_FOUND, "Es konnte kein Schuljahresabschnitt mit der angegebenen ID gefunden werden.");
				dto.Schuljahresabschnitts_ID = idAbschnitt;
			}),
			Map.entry("idFach", (conn, dto, value, map) -> {
				final Long idFach = JSONMapper.convertToLong(value, true);
				if (idFach == null)
					throw new ApiOperationException(Status.BAD_REQUEST, "Die ID des Faches darf nicht null sein.");
				final DTOFach fach = conn.queryByKey(DTOFach.class, idFach);
				if (fach == null)
					throw new ApiOperationException(Status.NOT_FOUND, "Es konnte kein Fach mit der angegebenen ID gefunden werden.");
				dto.Fach_ID = idFach;
			}),
			Map.entry("lehrer", (conn, dto, value, map) -> {
				dto.Lehrer_ID = JSONMapper.convertToLong(value, true);
				if (dto.Lehrer_ID != null) {
					final DTOLehrer lehrer = conn.queryByKey(DTOLehrer.class, dto.Lehrer_ID);
					if (lehrer == null)
						throw new ApiOperationException(Status.NOT_FOUND, "Es konnte kein Lehrer mit der angegebenen ID gefunden werden.");
				}
			}),
			Map.entry("kuerzel", (conn, dto, value, map) -> dto.KurzBez = JSONMapper.convertToString(value, false, false, 21)),
			Map.entry("kursartAllg", (conn, dto, value, map) -> {
				dto.KursartAllg = JSONMapper.convertToString(value, false, true, 11);
				// TODO Prüfe Kursart
			}),
			Map.entry("sortierung", (conn, dto, value, map) -> dto.Sortierung = JSONMapper.convertToIntegerInRange(value, false, 0, Integer.MAX_VALUE)),
			Map.entry("istSichtbar", (conn, dto, value, map) -> dto.Sichtbar = JSONMapper.convertToBoolean(value, false)),
			Map.entry("wochenstunden", (conn, dto, value, map) -> dto.WochenStd = JSONMapper.convertToIntegerInRange(value, false, 0, 40)),
			Map.entry("wochenstundenLehrer", (conn, dto, value, map) -> {
				dto.WochenstdKL = JSONMapper.convertToDouble(value, true);
				if (dto.WochenstdKL == null)
					dto.WochenstdKL = 0.0;
			}),
			Map.entry("idKursFortschreibungsart",
					(conn, dto, value, map) -> dto.Fortschreibungsart = KursFortschreibungsart.fromID(JSONMapper.convertToIntegerInRange(value, false, 0, 4))),
			Map.entry("schulnummer", (conn, dto, value, map) -> {
				dto.SchulNr = JSONMapper.convertToIntegerInRange(value, true, 100000, 999999);
				// TODO Prüfe die Schulnummer anhand des Katalogs
			}),
			Map.entry("istEpochalunterricht", (conn, dto, value, map) -> dto.EpochU = JSONMapper.convertToBoolean(value, false)),
			Map.entry("bezeichnungZeugnis", (conn, dto, value, map) -> dto.ZeugnisBez = JSONMapper.convertToString(value, true, true, 131)),
			Map.entry("schienen", (conn, dto, value, map) -> {
				final List<Integer> neu = JSONMapper.convertToListOfInteger(value, false);
				final List<Integer> vorher = convertSchienenStrToList(dto.Schienen);
				boolean changed = (neu.size() != vorher.size());
				for (final int n : neu) {
					if (n < 0)
						throw new ApiOperationException(Status.BAD_REQUEST, "Eine Schienen-Nummer kleiner als 0 ist nicht zulässig.");
					if (!vorher.contains(n))
						changed = true;
				}
				if (changed) {
					dto.Schienen = neu.stream().map(Object::toString).collect(Collectors.joining(","));
				}
			}),
			Map.entry("idJahrgaenge", (conn, dto, value, map) -> {
				final List<Long> neu = JSONMapper.convertToListOfLong(value, false);
				final List<Long> vorher = convertJahrgaenge(dto);
				boolean changed = (neu.size() != vorher.size());
				for (final long n : neu) {
					if (!vorher.contains(n)) {
						changed = true;
						break;
					}
				}
				if (!changed)
					return;
				if (neu.isEmpty()) {
					dto.ASDJahrgang = null;
					dto.Jahrgang_ID = null;
					dto.Jahrgaenge = null;
				} else {
					final List<DTOJahrgang> dtoJahrgaenge = conn.queryByKeyList(DTOJahrgang.class, neu);
					if (dtoJahrgaenge.size() != neu.size())
						throw new ApiOperationException(Status.BAD_REQUEST,
								"Mindestens einer der angegebenen Jahrgang-IDs existiert nicht in der SVWS-Datenbank");
					if (neu.size() > 1) {
						dto.ASDJahrgang = null;
						dto.Jahrgang_ID = null;
						dto.Jahrgaenge = neu.stream().map(Object::toString).collect(Collectors.joining(","));
					} else {
						dto.Jahrgang_ID = neu.get(0);
						dto.ASDJahrgang = dtoJahrgaenge.get(0).ASDJahrgang;
						dto.Jahrgaenge = null;
					}
				}
			}));


	@Override
	public Response patch(final Long id, final InputStream is) throws ApiOperationException {
		return super.patchBasic(id, is, DTOKurs.class, patchMappings);
	}


	private static final Set<String> requiredCreateAttributes = Set.of("id", "idSchuljahresabschnitt", "kuerzel", "idFach", "kursartAllg");

	private static final ObjLongConsumer<DTOKurs> initDTO = (dto, id) -> {
		dto.ID = id;
		dto.Sichtbar = true;
		dto.Sortierung = 32000;
		dto.WochenStd = 3;
		dto.Fortschreibungsart = KursFortschreibungsart.KEINE;
		dto.EpochU = false;
	};


	/**
	 * Fügt einen Kurs mit den übergebenen JSON-Daten der Datenbank hinzu und gibt das zugehörige CoreDTO
	 * zurück. Falls ein Fehler auftritt wird ein entsprechender Response-Code zurückgegeben.
	 *
	 * @param is   der InputStream mit den JSON-Daten
	 *
	 * @return die Response mit den Daten
	 *
	 * @throws ApiOperationException im Fehlerfall
	 */
	public Response add(final InputStream is) throws ApiOperationException {
		return super.addBasic(is, DTOKurs.class, initDTO, dtoMapper, requiredCreateAttributes, patchMappings);
	}


	/**
	 * Löscht einen Kurs
	 *
	 * @param id   die ID des Kurses
	 *
	 * @return die HTTP-Response, welchen den Erfolg der Lösch-Operation angibt.
	 *
	 * @throws ApiOperationException im Fehlerfall
	 */
	public Response delete(final Long id) throws ApiOperationException {
		return super.deleteBasic(id, DTOKurs.class, dtoMapper);
	}

}
