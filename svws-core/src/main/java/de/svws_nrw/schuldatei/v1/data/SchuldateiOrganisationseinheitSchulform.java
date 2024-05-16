package de.svws_nrw.schuldatei.v1.data;

import de.svws_nrw.core.transpiler.TranspilerDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Die Klasse beschreibt die Informationen zu einer Schulform, welche einer
 * Organisationseinheit im Rahmen der Schuldatei zugeordnet sein kann.
 */
@XmlRootElement
@Schema(description = "die Information zu einer Schulform einer Organisationseinheit.")
@TranspilerDTO
public class SchuldateiOrganisationseinheitSchulform {

	/** Die ID des Schulform-Eintrages. */
	@Schema(description = "die ID des Schulform-Eintrages", example = "4711")
	public Integer id;

	/** Die Schulnummer. */
	@Schema(description = "die Schulnummer", example = "100001")
	public @NotNull Integer schulnummer = 0;

	/** Schulformcode */
	@Schema(description = "Schulformcode", example = "Schulform")
	public @NotNull String schulformcode = "";

	/** Schulformwert */
	@Schema(description = "Schulformwert", example = "08")
	public @NotNull String schulformwert = "";

	/** Gibt die Gültigkeit ab welchem Schuljahr an */
    @Schema(description = "Gibt die Gültigkeit ab welchem Schuljahr an")
    public String gueltigab;

    /** Gibt die Gültigkeit bis zu welchem Schuljahr an */
    @Schema(description = "Gibt die Gültigkeit bis zu welchem Schuljahr an")
    public String gueltigbis;

    /** Gibt das Änderungsdatum des Eintrags an*/
    @Schema(description = "Gibt das Änderungsdatum des Eintrags an")
    public String geaendertam;

    /**
     * Erstellt eine neue Schulform für eine Organiationseinheit der Schuldatei
     */
    public SchuldateiOrganisationseinheitSchulform() {
        // Die Initialisierung mit Defaults erfolgt direkt über die Attribute
    }

}