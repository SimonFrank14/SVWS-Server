package de.svws_nrw.db.dto.migration.schild.benutzer;

import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.converter.migration.MigrationBoolean01Converter;


import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.svws_nrw.csv.converter.migration.MigrationBoolean01ConverterSerializer;
import de.svws_nrw.csv.converter.migration.MigrationBoolean01ConverterDeserializer;

/**
 * Diese Klasse dient als DTO für die Datenbanktabelle Benutzer.
 * Sie wurde automatisch per Skript generiert und sollte nicht verändert werden,
 * da sie aufgrund von Änderungen am DB-Schema ggf. neu generiert und überschrieben wird.
 */
@Entity
@Cacheable(DBEntityManager.use_db_caching)
@Table(name = "Benutzer")
@JsonPropertyOrder({"ID", "Typ", "Allgemein_ID", "Lehrer_ID", "Schueler_ID", "Erzieher_ID", "IstAdmin"})
public final class MigrationDTOBenutzer {

	/** Die Datenbankabfrage für alle DTOs */
	public static final String QUERY_ALL = "SELECT e FROM MigrationDTOBenutzer e";

	/** Die Datenbankabfrage für DTOs anhand der Primärschlüsselattribute */
	public static final String QUERY_PK = "SELECT e FROM MigrationDTOBenutzer e WHERE e.ID = ?1";

	/** Die Datenbankabfrage für DTOs anhand einer Liste von Primärschlüsselattributwerten */
	public static final String QUERY_LIST_PK = "SELECT e FROM MigrationDTOBenutzer e WHERE e.ID IN ?1";

	/** Die Datenbankabfrage für alle DTOs im Rahmen der Migration, wobei die Einträge entfernt werden, die nicht der Primärschlüssel-Constraint entsprechen */
	public static final String QUERY_MIGRATION_ALL = "SELECT e FROM MigrationDTOBenutzer e WHERE e.ID IS NOT NULL";

	/** Die Datenbankabfrage für DTOs anhand des Attributes ID */
	public static final String QUERY_BY_ID = "SELECT e FROM MigrationDTOBenutzer e WHERE e.ID = ?1";

	/** Die Datenbankabfrage für DTOs anhand einer Liste von Werten des Attributes ID */
	public static final String QUERY_LIST_BY_ID = "SELECT e FROM MigrationDTOBenutzer e WHERE e.ID IN ?1";

	/** Die Datenbankabfrage für DTOs anhand des Attributes Typ */
	public static final String QUERY_BY_TYP = "SELECT e FROM MigrationDTOBenutzer e WHERE e.Typ = ?1";

	/** Die Datenbankabfrage für DTOs anhand einer Liste von Werten des Attributes Typ */
	public static final String QUERY_LIST_BY_TYP = "SELECT e FROM MigrationDTOBenutzer e WHERE e.Typ IN ?1";

	/** Die Datenbankabfrage für DTOs anhand des Attributes Allgemein_ID */
	public static final String QUERY_BY_ALLGEMEIN_ID = "SELECT e FROM MigrationDTOBenutzer e WHERE e.Allgemein_ID = ?1";

	/** Die Datenbankabfrage für DTOs anhand einer Liste von Werten des Attributes Allgemein_ID */
	public static final String QUERY_LIST_BY_ALLGEMEIN_ID = "SELECT e FROM MigrationDTOBenutzer e WHERE e.Allgemein_ID IN ?1";

	/** Die Datenbankabfrage für DTOs anhand des Attributes Lehrer_ID */
	public static final String QUERY_BY_LEHRER_ID = "SELECT e FROM MigrationDTOBenutzer e WHERE e.Lehrer_ID = ?1";

	/** Die Datenbankabfrage für DTOs anhand einer Liste von Werten des Attributes Lehrer_ID */
	public static final String QUERY_LIST_BY_LEHRER_ID = "SELECT e FROM MigrationDTOBenutzer e WHERE e.Lehrer_ID IN ?1";

	/** Die Datenbankabfrage für DTOs anhand des Attributes Schueler_ID */
	public static final String QUERY_BY_SCHUELER_ID = "SELECT e FROM MigrationDTOBenutzer e WHERE e.Schueler_ID = ?1";

	/** Die Datenbankabfrage für DTOs anhand einer Liste von Werten des Attributes Schueler_ID */
	public static final String QUERY_LIST_BY_SCHUELER_ID = "SELECT e FROM MigrationDTOBenutzer e WHERE e.Schueler_ID IN ?1";

	/** Die Datenbankabfrage für DTOs anhand des Attributes Erzieher_ID */
	public static final String QUERY_BY_ERZIEHER_ID = "SELECT e FROM MigrationDTOBenutzer e WHERE e.Erzieher_ID = ?1";

	/** Die Datenbankabfrage für DTOs anhand einer Liste von Werten des Attributes Erzieher_ID */
	public static final String QUERY_LIST_BY_ERZIEHER_ID = "SELECT e FROM MigrationDTOBenutzer e WHERE e.Erzieher_ID IN ?1";

	/** Die Datenbankabfrage für DTOs anhand des Attributes IstAdmin */
	public static final String QUERY_BY_ISTADMIN = "SELECT e FROM MigrationDTOBenutzer e WHERE e.IstAdmin = ?1";

	/** Die Datenbankabfrage für DTOs anhand einer Liste von Werten des Attributes IstAdmin */
	public static final String QUERY_LIST_BY_ISTADMIN = "SELECT e FROM MigrationDTOBenutzer e WHERE e.IstAdmin IN ?1";

	/** Die ID des Benutzers */
	@Id
	@Column(name = "ID")
	@JsonProperty
	public Long ID;

	/** Der Typ des Benutzers (0 = Allgemeiner Benutzer, 1 = Lehrer bzw. Personal aus K_Lehrer, 2 = Schueler, 3 = Erzieher) */
	@Column(name = "Typ")
	@JsonProperty
	public Integer Typ;

	/** Die ID des allgemeinen Benutzers, falls der Benutzer es sich um einen allgemeinen Benutzer handelt */
	@Column(name = "Allgemein_ID")
	@JsonProperty
	public Long Allgemein_ID;

	/** Die Lehrer-ID des Benutzers, falls der Benutzer es sich um einen Lehrer handelt */
	@Column(name = "Lehrer_ID")
	@JsonProperty
	public Long Lehrer_ID;

	/** Die Schüler-ID des Benutzers, falls der Benutzer es sich um einen Schüler handelt */
	@Column(name = "Schueler_ID")
	@JsonProperty
	public Long Schueler_ID;

	/** Die erzieher-ID des Benutzers, falls der Benutzer es sich um einen Erzieher handelt */
	@Column(name = "Erzieher_ID")
	@JsonProperty
	public Long Erzieher_ID;

	/** Gibt an, ob der Benutzer Administrator-Rechte hat (1) oder nicht (0) */
	@Column(name = "IstAdmin")
	@JsonProperty
	@Convert(converter = MigrationBoolean01Converter.class)
	@JsonSerialize(using = MigrationBoolean01ConverterSerializer.class)
	@JsonDeserialize(using = MigrationBoolean01ConverterDeserializer.class)
	public Boolean IstAdmin;

	/**
	 * Erstellt ein neues Objekt der Klasse MigrationDTOBenutzer ohne eine Initialisierung der Attribute.
	 */
	@SuppressWarnings("unused")
	private MigrationDTOBenutzer() {
	}

	/**
	 * Erstellt ein neues Objekt der Klasse MigrationDTOBenutzer ohne eine Initialisierung der Attribute.
	 * @param ID   der Wert für das Attribut ID
	 * @param Typ   der Wert für das Attribut Typ
	 * @param IstAdmin   der Wert für das Attribut IstAdmin
	 */
	public MigrationDTOBenutzer(final Long ID, final Integer Typ, final Boolean IstAdmin) {
		if (ID == null) {
			throw new NullPointerException("ID must not be null");
		}
		this.ID = ID;
		if (Typ == null) {
			throw new NullPointerException("Typ must not be null");
		}
		this.Typ = Typ;
		if (IstAdmin == null) {
			throw new NullPointerException("IstAdmin must not be null");
		}
		this.IstAdmin = IstAdmin;
	}


	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MigrationDTOBenutzer other = (MigrationDTOBenutzer) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		return result;
	}


	/**
	 * Konvertiert das Objekt in einen String. Dieser kann z.B. für Debug-Ausgaben genutzt werden.
	 *
	 * @return die String-Repräsentation des Objektes
	 */
	@Override
	public String toString() {
		return "MigrationDTOBenutzer(ID=" + this.ID + ", Typ=" + this.Typ + ", Allgemein_ID=" + this.Allgemein_ID + ", Lehrer_ID=" + this.Lehrer_ID + ", Schueler_ID=" + this.Schueler_ID + ", Erzieher_ID=" + this.Erzieher_ID + ", IstAdmin=" + this.IstAdmin + ")";
	}

}
