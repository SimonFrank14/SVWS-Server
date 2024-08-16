package de.svws_nrw.core.data.gost.klausurplanung;

import java.util.ArrayList;
import java.util.List;

import de.svws_nrw.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Diese Klasse wird bei der Kommunikation über die Open-API-Schnittstelle verwendet.
 * Sie liefert die neuen GostKlausurraumstunden und GostSchuelerklausurraumstunden,
 * wenn Schülerklausuren einem Raum zugewiesen werden.
 */
@XmlRootElement
@Schema(description = "die Sammlung von neuen GostKlausurraumstunden und GostSchuelerklausurraumstunden, wenn Schülerklausuren über die API einem Raum zugewiesen werden.")
@TranspilerDTO
public class GostKlausurenCollectionSkrsKrsData {


	/** Die Liste der Schülerklausuren. */
	@Schema(description = "die ID der Klausurraumstunde", example = "")
	public @NotNull GostKlausurenCollectionRaumData raumdata = new GostKlausurenCollectionRaumData();

	/** Die ID der Schülerklausur. */
	@Schema(description = "die ID der Schülerklausur", example = "")
	public @NotNull List<GostKlausurraumstunde> raumstundenGeloescht = new ArrayList<>();

	/** Die ID der Klausurraumstunde. */
	@Schema(description = "die ID der Klausurraumstunde", example = "")
	public long idKlausurraum = -1;

	/** Die ID der Klausurraumstunde. */
	@Schema(description = "die ID der Klausurraumstunde", example = "")
	public @NotNull List<Long> idsSchuelerklausurtermine = new ArrayList<>();

	/** Die ID der Klausurraumstunde. */
	@Schema(description = "die ID der Klausurraumstunde", example = "")
	public GostKursklausur kursKlausurPatched = null;

}