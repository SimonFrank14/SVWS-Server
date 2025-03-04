package de.svws_nrw.data.schueler;

import de.svws_nrw.core.data.schule.FoerderschwerpunktEintrag;
import de.svws_nrw.asd.types.schule.Foerderschwerpunkt;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.dto.current.schild.schueler.DTOFoerderschwerpunkt;
import de.svws_nrw.db.utils.ApiOperationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.io.InputStream;
import java.util.List;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den
 * Core-DTO {@link FoerderschwerpunktEintrag}.
 */
public final class DataKatalogSchuelerFoerderschwerpunkte extends DataManager<Long> {

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO {@link FoerderschwerpunktEintrag}.
	 *
	 * @param conn   die Datenbank-Verbindung für den Datenbankzugriff
	 */
	public DataKatalogSchuelerFoerderschwerpunkte(final DBEntityManager conn) {
		super(conn);
	}

	private FoerderschwerpunktEintrag map(final DTOFoerderschwerpunkt k) {
		final int schuljahr = conn.getUser().schuleGetSchuljahr();
		final FoerderschwerpunktEintrag eintrag = new FoerderschwerpunktEintrag();
		eintrag.id = k.ID;
		eintrag.kuerzel = (k.Bezeichnung == null) ? "" : k.Bezeichnung;
		final Foerderschwerpunkt fs = Foerderschwerpunkt.data().getWertByKuerzel(eintrag.kuerzel);
		eintrag.text = ((fs == null) || (fs.daten(schuljahr) == null)) ? "---" : fs.daten(schuljahr).text;
		eintrag.kuerzelStatistik = k.StatistikKrz;
		eintrag.istSichtbar = k.Sichtbar;
		return eintrag;
	}

	@Override
	public Response getAll() throws ApiOperationException {
		final List<FoerderschwerpunktEintrag> daten = getAllFromDB();
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	/**
	 * Ermittelt alle Förderschwerpunkte aus der Datenbank.
	 *
	 * @return Liste der Förderschwerpunkte.
	 *
	 * @throws ApiOperationException im Fehlerfall
	 */
	public List<FoerderschwerpunktEintrag> getAllFromDB() throws ApiOperationException {
		final List<DTOFoerderschwerpunkt> katalog = conn.queryAll(DTOFoerderschwerpunkt.class);
		if (katalog == null)
			throw new ApiOperationException(Status.NOT_FOUND);
		return katalog.stream().map(fs -> map(fs)).toList();
	}


	@Override
	public Response getList() throws ApiOperationException {
		return this.getAll();
	}

	@Override
	public Response get(final Long id) throws ApiOperationException {
		final DTOFoerderschwerpunkt fs = conn.queryByKey(DTOFoerderschwerpunkt.class, id);
		if (fs == null)
			throw new ApiOperationException(Status.NOT_FOUND);
		final FoerderschwerpunktEintrag daten = map(fs);
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	@Override
	public Response patch(final Long id, final InputStream is) {
		// TODO
		throw new UnsupportedOperationException();
	}

}
