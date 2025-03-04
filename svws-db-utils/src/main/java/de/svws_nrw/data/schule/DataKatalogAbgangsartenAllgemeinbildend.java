package de.svws_nrw.data.schule;

import java.io.InputStream;

import de.svws_nrw.core.data.schule.AbgangsartKatalog;
import de.svws_nrw.data.DataManager;
import de.svws_nrw.json.JsonDaten;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Diese Klasse erweitert den abstrakten {@link DataManager} für den
 * Core-DTO {@link AbgangsartKatalog}.
 */
public final class DataKatalogAbgangsartenAllgemeinbildend extends DataManager<Long> {

	/**
	 * Erstellt einen neuen {@link DataManager} für den Core-DTO {@link AbgangsartKatalog}.
	 */
	public DataKatalogAbgangsartenAllgemeinbildend() {
		super(null);
	}

	@Override
	public Response getAll() {
		final AbgangsartKatalog daten = JsonDaten.abgangsartenManager.getKatalogAllgemeinbildend();
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(daten).build();
	}

	@Override
	public Response getList() {
		return this.getAll();
	}

	@Override
	public Response get(final Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Response patch(final Long id, final InputStream is) {
		throw new UnsupportedOperationException();
	}

}
