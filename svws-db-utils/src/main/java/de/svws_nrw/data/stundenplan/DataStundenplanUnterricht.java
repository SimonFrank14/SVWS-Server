package de.svws_nrw.data.stundenplan;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ObjLongConsumer;
import java.util.stream.Collectors;

import de.svws_nrw.core.data.stundenplan.StundenplanUnterricht;
import de.svws_nrw.data.DataBasicMapper;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.faecher.DTOFach;
import de.svws_nrw.db.dto.current.schild.kurse.DTOKurs;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplan;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanUnterricht;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanUnterrichtKlasse;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanUnterrichtLehrer;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanUnterrichtRaum;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanUnterrichtSchiene;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanZeitraster;
import de.svws_nrw.db.utils.OperationError;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den Core-DTO
 * {@link StundenplanUnterricht}.
 */
public final class DataStundenplanUnterricht extends DataManager<Long> {

	private final Long idStundenplan;

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO
	 * {@link StundenplanUnterricht}.
	 *
	 * @param conn          die Datenbank-Verbindung für den Datenbankzugriff
	 * @param idStundenplan die ID des Stundenplans, dessen Unterricht abgefragt wird
	 */
	public DataStundenplanUnterricht(final DBEntityManager conn, final Long idStundenplan) {
		super(conn);
		this.idStundenplan = idStundenplan;
	}

	@Override
	public Response getAll() {
		return this.getList();
	}

	/**
	 * Bestimmt zu dem Stundenplan mit der angegebenen ID die Liste der Unterrichte.
	 *
	 * @param conn            die Datenbank-Verbindung
	 * @param idStundenplan   die ID des Stundenplans
	 *
	 * @return die Liste der Unterrichte
	 */
	public static List<StundenplanUnterricht> getUnterrichte(final DBEntityManager conn, final long idStundenplan) {
		final List<StundenplanUnterricht> daten = new ArrayList<>();
		// Bestimme die Zeitraster-IDs des Stundenplans
		final List<Long> zeitrasterIDs = conn.queryNamed("DTOStundenplanZeitraster.stundenplan_id", idStundenplan, DTOStundenplanZeitraster.class)
				.stream().map(p -> p.ID).toList();
		if (zeitrasterIDs.isEmpty())
			return daten;
		// Bestimme die Unterrichte der Zeitraster-Einträge
		final List<DTOStundenplanUnterricht> dtoUnterrichte = conn.queryNamed("DTOStundenplanUnterricht.zeitraster_id.multiple", zeitrasterIDs, DTOStundenplanUnterricht.class);
		if (dtoUnterrichte.isEmpty())
			return daten;
		final List<Long> unterrichtIDs = dtoUnterrichte.stream().map(a -> a.ID).toList();
		// Bestimme die Zuordnung der Räume zu den Unterrichts-Einträgen
		final Map<Long, List<DTOStundenplanUnterrichtRaum>> mapRaeume = conn.queryNamed("DTOStundenplanUnterrichtRaum.unterricht_id.multiple", unterrichtIDs, DTOStundenplanUnterrichtRaum.class)
				.stream().collect(Collectors.groupingBy(u -> u.Unterricht_ID));
		// Bestimme die Zuordnung der Schienen zu den Unterrichts-Einträgen
		final Map<Long, List<DTOStundenplanUnterrichtSchiene>> mapSchienen = conn.queryNamed("DTOStundenplanUnterrichtSchiene.unterricht_id.multiple", unterrichtIDs, DTOStundenplanUnterrichtSchiene.class)
				.stream().collect(Collectors.groupingBy(u -> u.Unterricht_ID));
		// Bestimme die Zuordnung der Klassen zu den Unterrichts-Einträgen
		final Map<Long, List<DTOStundenplanUnterrichtKlasse>> mapKlassen = conn.queryNamed("DTOStundenplanUnterrichtKlasse.unterricht_id.multiple", unterrichtIDs, DTOStundenplanUnterrichtKlasse.class)
				.stream().collect(Collectors.groupingBy(u -> u.Unterricht_ID));
		// Bestimme die Zuordnung der Lehrer zu den Unterrichts-Einträgen
		final Map<Long, List<DTOStundenplanUnterrichtLehrer>> mapLehrer = conn.queryNamed("DTOStundenplanUnterrichtLehrer.unterricht_id.multiple", unterrichtIDs, DTOStundenplanUnterrichtLehrer.class)
				.stream().collect(Collectors.groupingBy(u -> u.Unterricht_ID));
		for (final DTOStundenplanUnterricht dtoUnterricht : dtoUnterrichte) {
			final StundenplanUnterricht unterricht = new StundenplanUnterricht();
			unterricht.id = dtoUnterricht.ID;
			unterricht.idZeitraster = dtoUnterricht.Zeitraster_ID;
			unterricht.wochentyp = dtoUnterricht.Wochentyp;
			unterricht.idKurs = dtoUnterricht.Kurs_ID;
			unterricht.idFach = dtoUnterricht.Fach_ID;
			if (mapRaeume.containsKey(unterricht.id))
				unterricht.raeume.addAll(mapRaeume.get(unterricht.id).stream().map(b -> b.Raum_ID).toList());
			if (mapKlassen.containsKey(unterricht.id))
				unterricht.klassen.addAll(mapKlassen.get(unterricht.id).stream().map(b -> b.Klasse_ID).toList());
			if (mapLehrer.containsKey(unterricht.id))
				unterricht.lehrer.addAll(mapLehrer.get(unterricht.id).stream().map(b -> b.Lehrer_ID).toList());
			if (mapSchienen.containsKey(unterricht.id))
				unterricht.schienen.addAll(mapSchienen.get(unterricht.id).stream().map(b -> b.Schiene_ID).toList());
			daten.add(unterricht);
		}
		return daten;
	}

	@Override
	public Response getList() {
		if (idStundenplan == null)
			return OperationError.BAD_REQUEST.getResponse("Eine Anfrage zu einem Stundenplan mit der ID null ist unzulässig.");
		final DTOStundenplan stundenplan = conn.queryByKey(DTOStundenplan.class, idStundenplan);
		if (stundenplan == null)
			return OperationError.NOT_FOUND.getResponse("Es wurde kein Stundenplan mit der ID %d gefunden.".formatted(idStundenplan));
		final List<StundenplanUnterricht> daten = getUnterrichte(conn, idStundenplan);
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}


	private StundenplanUnterricht getUnterricht(final Long id) {
		if (id == null)
			throw OperationError.BAD_REQUEST.exception("Eine Anfrage zu einem Unterricht mit der ID null ist unzulässig.");
		final DTOStundenplanUnterricht dtoUnterricht = conn.queryByKey(DTOStundenplanUnterricht.class, id);
		if (dtoUnterricht == null)
			throw OperationError.NOT_FOUND.exception("Es wurde kein Unterricht mit der ID %d gefunden.".formatted(id));
		final List<Long> raeume = conn.queryNamed("DTOStundenplanUnterrichtRaum.unterricht_id", dtoUnterricht.ID, DTOStundenplanUnterrichtRaum.class)
				.stream().map(b -> b.Raum_ID).toList();
		final List<Long> schienen = conn.queryNamed("DTOStundenplanUnterrichtSchiene.unterricht_id", dtoUnterricht.ID, DTOStundenplanUnterrichtSchiene.class)
				.stream().map(b -> b.Schiene_ID).toList();
		final List<Long> klassen = conn.queryNamed("DTOStundenplanUnterrichtKlasse.unterricht_id", dtoUnterricht.ID, DTOStundenplanUnterrichtKlasse.class)
				.stream().map(b -> b.Klasse_ID).toList();
		final List<Long> lehrer = conn.queryNamed("DTOStundenplanUnterrichtLehrer.unterricht_id", dtoUnterricht.ID, DTOStundenplanUnterrichtLehrer.class)
				.stream().map(b -> b.Lehrer_ID).toList();
		final StundenplanUnterricht daten = new StundenplanUnterricht();
		daten.id = dtoUnterricht.ID;
		daten.idZeitraster = dtoUnterricht.Zeitraster_ID;
		daten.wochentyp = dtoUnterricht.Wochentyp;
		daten.idKurs = dtoUnterricht.Kurs_ID;
		daten.idFach = dtoUnterricht.Fach_ID;
		daten.raeume.addAll(raeume);
		daten.schienen.addAll(schienen);
		daten.klassen.addAll(klassen);
		daten.lehrer.addAll(lehrer);
		return daten;
	}

	@Override
	public Response get(final Long id) {
		final StundenplanUnterricht daten = getUnterricht(id);
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}


	private static final Map<String, DataBasicMapper<DTOStundenplanUnterricht>> patchMappings = Map.ofEntries(
		Map.entry("id", (conn, dto, value, map) -> {
			final Long patch_id = JSONMapper.convertToLong(value, true);
			if ((patch_id == null) || (patch_id.longValue() != dto.ID))
				throw OperationError.BAD_REQUEST.exception();
		}),
		Map.entry("idZeitraster", (conn, dto, value, map) -> {
			final DTOStundenplanZeitraster zeitraster = conn.queryByKey(DTOStundenplanZeitraster.class, value);
			if (zeitraster == null)
				throw OperationError.NOT_FOUND.exception("Zeitraster mit der ID %d nicht gefunden.".formatted((Long) value));
			dto.Zeitraster_ID = zeitraster.ID;
		}),
		Map.entry("wochentyp", (conn, dto, value, map) -> dto.Wochentyp = JSONMapper.convertToInteger(value, false)),
		Map.entry("idKurs", (conn, dto, value, map) -> {
			final DTOKurs kurs = conn.queryByKey(DTOKurs.class, value);
			if (kurs == null)
				throw OperationError.NOT_FOUND.exception("Kurs mit der ID %d nicht gefunden.".formatted((Long) value));
			dto.Kurs_ID = kurs.ID;
		}),
		Map.entry("idFach", (conn, dto, value, map) -> {
			final DTOFach fach = conn.queryByKey(DTOFach.class, value);
			if (fach == null)
				throw OperationError.NOT_FOUND.exception("Fach mit der ID %d nicht gefunden.".formatted((Long) value));
			dto.Fach_ID = fach.ID;
		})
	);


	@Override
	public Response patch(final Long id, final InputStream is) {
		return super.patchBasic(id, is, DTOStundenplanUnterricht.class, patchMappings);
	}


	private static final Set<String> requiredCreateAttributes = Set.of("idZeitraster", "wochentyp", "idKurs", "idFach");


	private final Function<DTOStundenplanUnterricht, StundenplanUnterricht> dtoMapper = (final DTOStundenplanUnterricht u) -> getUnterricht(u.ID);

	/**
	 * Fügt einen Unterricht mit den übergebenen JSON-Daten der Datenbank hinzu und gibt das zugehörige Core-DTO
	 * zurück. Falls ein Fehler auftritt wird ein entsprechender Response-Code zurückgegeben.
	 *
	 * @param is   der InputStream mit den JSON-Daten
	 *
	 * @return die Response mit den Daten
	 */
	public Response add(final InputStream is) {
		// Füge den Unterricht in der Datenbank hinzu und gebe das zugehörige CoreDTO zurück.
		final ObjLongConsumer<DTOStundenplanUnterricht> initDTO = (dto, id) -> dto.ID = id;
		return super.addBasic(is, DTOStundenplanUnterricht.class, initDTO, dtoMapper, requiredCreateAttributes, patchMappings);
	}


	/**
	 * Löscht einen Unterricht
	 *
	 * @param id   die ID des Unterrichts
	 *
	 * @return die HTTP-Response, welchen den Erfolg der Lösch-Operation angibt.
	 */
	public Response delete(final Long id) {
		return super.deleteBasic(id, DTOStundenplanUnterricht.class, dtoMapper);
	}

}
