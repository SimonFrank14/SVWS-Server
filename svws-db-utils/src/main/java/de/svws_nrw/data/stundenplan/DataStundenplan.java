package de.svws_nrw.data.stundenplan;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import de.svws_nrw.core.adt.map.HashMap2D;
import de.svws_nrw.core.data.stundenplan.Stundenplan;
import de.svws_nrw.core.data.stundenplan.StundenplanAufsichtsbereich;
import de.svws_nrw.core.data.stundenplan.StundenplanJahrgang;
import de.svws_nrw.core.data.stundenplan.StundenplanKalenderwochenzuordnung;
import de.svws_nrw.core.data.stundenplan.StundenplanPausenzeit;
import de.svws_nrw.core.data.stundenplan.StundenplanRaum;
import de.svws_nrw.core.data.stundenplan.StundenplanSchiene;
import de.svws_nrw.core.data.stundenplan.StundenplanZeitraster;
import de.svws_nrw.data.DataBasicMapper;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.data.JSONMapper;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.schule.DTOSchuljahresabschnitte;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplan;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanAufsichtsbereich;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanPausenaufsichtenBereiche;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanUnterricht;
import de.svws_nrw.db.dto.current.schild.stundenplan.DTOStundenplanZeitraster;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den Core-DTO
 * {@link Stundenplan}.
 */
public final class DataStundenplan extends DataManager<Long> {

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO {@link Stundenplan}.
	 *
	 * @param conn   die Datenbank-Verbindung
	 */
	public DataStundenplan(final DBEntityManager conn) {
		super(conn);
	}

	@Override
	public Response getAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Response getList() {
		throw new UnsupportedOperationException();
	}


	/**
	 * Prüft, ob ein Stundenplan mit der angegegeben ID vorhanden ist und gibt das Datenbank-DTO
	 * ggf. zurück. Ist der Stundenplan nicht vorhanden, so wird eine {@link ApiOperationException}
	 * geworfen.
	 *
	 * @param conn   die zu verwendende Datenbank-Verbindung
	 * @param id     die ID des Stundenplans
	 *
	 * @return das Datenbank-DTO
	 *
	 * @throws ApiOperationException falls kein Stundenplan mit der ID gefunden wurde
	 */
	public static DTOStundenplan getDTOStundenplan(final DBEntityManager conn, final Long id) throws ApiOperationException {
		// Prüfe, ob ein Stundenplan mit der stundenplanID existiert und lade diesen
		if (id == null)
			throw new ApiOperationException(Status.NOT_FOUND, "Die StundenplanID darf nicht null sein.");
		final DTOStundenplan stundenplan = conn.queryByKey(DTOStundenplan.class, id);
		if (stundenplan == null)
			throw new ApiOperationException(Status.NOT_FOUND, "Ein Stundenplan mit der ID %d ist nicht vorhanden.".formatted(id));
		return stundenplan;
	}


	/**
	 * Gibt den Stundenplan zur angegebenen ID zurück.
	 *
	 * @param conn            die Datenbankverbindung
	 * @param id   die ID des Stundenplans
	 *
	 * @return das Stundenplan-Objekt
	 *
	 * @throws ApiOperationException im Fehlerfall
	 */
	public static Stundenplan getStundenplan(final DBEntityManager conn, final long id) throws ApiOperationException {
		final DTOStundenplan stundenplan = conn.queryByKey(DTOStundenplan.class, id);
		if (stundenplan == null)
			return null;
		final DTOSchuljahresabschnitte schuljahresabschnitt = conn.queryByKey(DTOSchuljahresabschnitte.class, stundenplan.Schuljahresabschnitts_ID);
		if (schuljahresabschnitt == null)
			return null;
		final List<StundenplanZeitraster> zeitraster = DataStundenplanZeitraster.getZeitraster(conn, id);
		final List<StundenplanRaum> raeume = DataStundenplanRaeume.getRaeume(conn, id);
		final List<StundenplanSchiene> schienen = DataStundenplanSchienen.getSchienen(conn, id);
		final List<StundenplanJahrgang> jahrgaenge = DataStundenplanJahrgaenge.getJahrgaenge(conn, id);
		final List<StundenplanPausenzeit> pausenzeiten = new DataStundenplanPausenzeiten(conn, id).getList();
		final List<StundenplanAufsichtsbereich> aufsichtsbereiche = DataStundenplanAufsichtsbereiche.getAufsichtsbereiche(conn, id);
		final List<StundenplanKalenderwochenzuordnung> kalenderwochenzuordnung = new DataStundenplanKalenderwochenzuordnung(conn, id).getList();
		// Erstelle das Core-DTO-Objekt für die Response
		final Stundenplan daten = new Stundenplan();
		daten.id = stundenplan.ID;
		daten.idSchuljahresabschnitt = stundenplan.Schuljahresabschnitts_ID;
		daten.schuljahr = schuljahresabschnitt.Jahr;
		daten.abschnitt = schuljahresabschnitt.Abschnitt;
		daten.gueltigAb = stundenplan.Beginn;
		if (stundenplan.Ende == null) {
			daten.gueltigBis = "%04d-%02d-%02d".formatted(schuljahresabschnitt.Jahr + 1, 7, 31);
		} else {
			daten.gueltigBis = stundenplan.Ende;
		}
		daten.bezeichnungStundenplan = stundenplan.Beschreibung;
		daten.wochenTypModell = stundenplan.WochentypModell;
		daten.zeitraster.addAll(zeitraster);
		daten.raeume.addAll(raeume);
		daten.schienen.addAll(schienen);
		daten.jahrgaenge.addAll(jahrgaenge);
		daten.pausenzeiten.addAll(pausenzeiten);
		daten.aufsichtsbereiche.addAll(aufsichtsbereiche);
		daten.kalenderwochenZuordnung.addAll(kalenderwochenzuordnung);
		return daten;
	}

	@Override
	public Response get(final Long id) throws ApiOperationException {
		if (id == null)
			throw new ApiOperationException(Status.BAD_REQUEST, "Eine Anfrage zu einem Stundenplan mit der ID null ist unzulässig.");
		final Stundenplan stundenplan = DataStundenplan.getStundenplan(conn, id);
		if (stundenplan == null)
			throw new ApiOperationException(Status.NOT_FOUND, "Es wurde kein Stundenplan mit der ID %d gefunden.".formatted(id));
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(stundenplan).build();
	}


	private static final Map<String, DataBasicMapper<DTOStundenplan>> patchMappings = Map.ofEntries(
			Map.entry("id", (conn, dto, value, map) -> {
				final Long patch_id = JSONMapper.convertToLong(value, true);
				if ((patch_id == null) || (patch_id.longValue() != dto.ID))
					throw new ApiOperationException(Status.BAD_REQUEST);
			}),
			Map.entry("idSchuljahresabschnitt", (conn, dto, value, map) -> {
				throw new ApiOperationException(Status.BAD_REQUEST);
			}),
			Map.entry("schuljahr", (conn, dto, value, map) -> {
				throw new ApiOperationException(Status.BAD_REQUEST);
			}),
			Map.entry("abschnitt", (conn, dto, value, map) -> {
				throw new ApiOperationException(Status.BAD_REQUEST);
			}),
			Map.entry("gueltigAb", (conn, dto, value, map) -> dto.Beginn = JSONMapper.convertToString(value, false, false, null)),
			Map.entry("gueltigBis", (conn, dto, value, map) -> dto.Ende = JSONMapper.convertToString(value, false, false, null)),
			Map.entry("bezeichnungStundenplan", (conn, dto, value, map) -> dto.Beschreibung = JSONMapper.convertToString(value, false, false, 1000)),
			Map.entry("wochenTypModell", (conn, dto, value, map) -> {
				final long idStundenplan = dto.ID;
				int wochentypmodell = JSONMapper.convertToIntegerInRange(value, false, 0, 100);
				if (wochentypmodell == 1)
					wochentypmodell = 0;
				if (dto.WochentypModell == wochentypmodell)
					return;
				// Bestimme den kompletten Unterricht, der einem Wochentyp > als dem Wert für das Wochentyp-Modell zugeordnet ist und passe diesen ggf. an.
				final List<Long> idsZeitraster = conn.queryList(DTOStundenplanZeitraster.QUERY_BY_STUNDENPLAN_ID, DTOStundenplanZeitraster.class, idStundenplan)
						.stream().map(z -> z.ID).toList();
				if (!idsZeitraster.isEmpty()) {
					final List<DTOStundenplanUnterricht> unterrichte =
							conn.queryList("SELECT e FROM DTOStundenplanUnterricht e WHERE e.Zeitraster_ID IN ?1 AND e.Wochentyp > ?2",
									DTOStundenplanUnterricht.class, idsZeitraster, wochentypmodell);
					if (!unterrichte.isEmpty()) {
						for (final DTOStundenplanUnterricht unterricht : unterrichte)
							unterricht.Wochentyp = 0;
						conn.transactionPersistAll(unterrichte);
						conn.transactionFlush();
					}
				}
				// Bestimme alle Aufsichtsbereich des Stundenplans, wo zugeordnete Pausenaufsichten einen Wochentyp > als dem Wert für das Wochentyp-Modell zugeordnet haben und passe diesen ggf. an.
				final List<Long> idsAufsichtsbereiche =
						conn.queryList(DTOStundenplanAufsichtsbereich.QUERY_BY_STUNDENPLAN_ID, DTOStundenplanAufsichtsbereich.class, idStundenplan)
								.stream().map(z -> z.ID).toList();
				if (!idsAufsichtsbereiche.isEmpty()) {
					final List<DTOStundenplanPausenaufsichtenBereiche> bereiche =
							conn.queryList("SELECT e FROM DTOStundenplanPausenaufsichtenBereiche e WHERE e.Aufsichtsbereich_ID IN ?1 AND e.Wochentyp > ?2",
									DTOStundenplanPausenaufsichtenBereiche.class, idsAufsichtsbereiche, wochentypmodell);
					if (!bereiche.isEmpty()) {
						final HashMap2D<Long, Long, Boolean> updatedPausenaufsichtsbereich = new HashMap2D<>();
						for (final DTOStundenplanPausenaufsichtenBereiche bereich : bereiche) {
							final List<DTOStundenplanPausenaufsichtenBereiche> andereBereiche = conn.queryList(
									"SELECT e FROM DTOStundenplanPausenaufsichtenBereiche e WHERE e.Aufsichtsbereich_ID = ?1 AND e.Pausenaufsicht_ID = ?2 AND e.Wochentyp <= ?3",
									DTOStundenplanPausenaufsichtenBereiche.class, bereich.Aufsichtsbereich_ID, bereich.Pausenaufsicht_ID, wochentypmodell);
							if (andereBereiche.isEmpty() && !updatedPausenaufsichtsbereich.contains(bereich.Aufsichtsbereich_ID, bereich.Pausenaufsicht_ID)) {
								bereich.Wochentyp = 0;
								conn.transactionPersist(bereich);
								updatedPausenaufsichtsbereich.put(bereich.Aufsichtsbereich_ID, bereich.Pausenaufsicht_ID, true);
							} else {
								conn.transactionRemove(bereich);
							}
						}
						conn.transactionFlush();
					}
				}
				// Lösche die Kalenderwochenzuordnungen
				conn.transactionExecuteDelete("DELETE FROM DTOStundenplanKalenderwochenZuordnung e WHERE e.Stundenplan_ID = %d".formatted(dto.ID));
				// Setze das Wochentyp-Modell
				dto.WochentypModell = wochentypmodell;
			}));

	@Override
	public Response patch(final Long id, final InputStream is) throws ApiOperationException {
		return super.patchBasic(id, is, DTOStundenplan.class, patchMappings);
	}

}
