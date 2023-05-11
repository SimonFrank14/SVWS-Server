package de.svws_nrw.data.stundenplan;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.svws_nrw.core.data.stundenplan.StundenplanPausenzeit;
import de.svws_nrw.data.DataBasicMapper;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanPausenzeit;
import de.svws_nrw.db.utils.OperationError;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den
 * Core-DTO {@link StundenplanPausenzeit}.
 */
public final class DataStundenplanPausenzeiten extends DataManager<Long> {

	private final Long stundenplanID;

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO {@link StundenplanPausenzeit}.
	 *
	 * @param conn            die Datenbank-Verbindung für den Datenbankzugriff
	 * @param stundenplanID   die ID des Stundenplans, dessen Pausenzeiten abgefragt werden
	 */
	public DataStundenplanPausenzeiten(final DBEntityManager conn, final Long stundenplanID) {
		super(conn);
		this.stundenplanID = stundenplanID;
	}


	/**
	 * Lambda-Ausdruck zum Umwandeln eines Datenbank-DTOs {@link DTOStundenplanPausenzeit} in einen Core-DTO {@link StundenplanPausenzeit}.
	 */
	private static final Function<DTOStundenplanPausenzeit, StundenplanPausenzeit> dtoMapper = (final DTOStundenplanPausenzeit p) -> {
		final StundenplanPausenzeit daten = new StundenplanPausenzeit();
		daten.id = p.ID;
		daten.wochentag = p.Tag;
		daten.beginn = p.Beginn;
		daten.ende = p.Ende;
		return daten;
	};


	@Override
	public Response getAll() {
		return this.getList();
	}

	/**
	 * Gibt die Pausenzeiten des Stundenplans zurück.
	 *
	 * @param conn            die Datenbankverbindung
	 * @param idStundenplan   die ID des Stundenplans
	 *
	 * @return die Liste der Pausenzeiten
	 */
	public static List<StundenplanPausenzeit> getPausenzeiten(final @NotNull DBEntityManager conn, final long idStundenplan) {
		final List<DTOStundenplanPausenzeit> pausenzeiten = conn.queryNamed("DTOStundenplanPausenzeit.stundenplan_id", idStundenplan, DTOStundenplanPausenzeit.class);
		final ArrayList<StundenplanPausenzeit> daten = new ArrayList<>();
		for (final DTOStundenplanPausenzeit p : pausenzeiten)
			daten.add(dtoMapper.apply(p));
		return daten;
	}

	@Override
	public Response getList() {
		final List<StundenplanPausenzeit> daten = getPausenzeiten(conn, this.stundenplanID);
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	@Override
	public Response get(final Long id) {
		if (id == null)
			return OperationError.BAD_REQUEST.getResponse("Eine Anfrage zu einer Pausenzeit eines Stundenplans mit der ID null ist unzulässig.");
		final DTOStundenplanPausenzeit pausenzeit = conn.queryByKey(DTOStundenplanPausenzeit.class, id);
		if (pausenzeit == null)
			return OperationError.NOT_FOUND.getResponse("Es wurde keine Pausenzeit eines Stundenplans mit der ID %d gefunden.".formatted(id));
		final StundenplanPausenzeit daten = dtoMapper.apply(pausenzeit);
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}


	private static final Map<String, DataBasicMapper<DTOStundenplanPausenzeit>> patchMappings = Map.ofEntries(
		Map.entry("id", (dto, value, map) -> {
			final Long patch_id = JSONMapper.convertToLong(value, true);
			if ((patch_id == null) || (patch_id.longValue() != dto.ID))
				throw OperationError.BAD_REQUEST.exception();
		}),
		Map.entry("wochentag", (dto, value, map) -> dto.Tag = JSONMapper.convertToInteger(value, false)),
		Map.entry("beginn", (dto, value, map) -> dto.Beginn = JSONMapper.convertToString(value, false, false, null)),
		Map.entry("end", (dto, value, map) -> dto.Ende = JSONMapper.convertToString(value, false, false, null))
	);

	@Override
	public Response patch(final Long id, final InputStream is) {
		return super.patchBasic(id, is, DTOStundenplanPausenzeit.class, patchMappings);
	}


}
