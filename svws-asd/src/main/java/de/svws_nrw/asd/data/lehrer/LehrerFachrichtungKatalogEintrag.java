package de.svws_nrw.asd.data.lehrer;

import de.svws_nrw.asd.data.CoreTypeData;
import de.svws_nrw.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Diese Klasse wird bei der Kommunikation über die Open-API-Schnittstelle verwendet.
 * Sie beschreibt eine Fachrichtung eines Lehrers mit der übergebenen ID.
 */

@XmlRootElement
@Schema(description = "Eine Fachrichtung eines Lehrers.")
@TranspilerDTO
public class LehrerFachrichtungKatalogEintrag extends CoreTypeData {

	// keine weiteren Attribute

}