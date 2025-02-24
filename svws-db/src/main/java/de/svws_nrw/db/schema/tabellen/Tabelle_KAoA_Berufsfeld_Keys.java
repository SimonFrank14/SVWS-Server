package de.svws_nrw.db.schema.tabellen;

import java.util.Arrays;
import java.util.Collection;

import de.svws_nrw.asd.types.kaoa.KAOABerufsfeld;
import de.svws_nrw.db.schema.SchemaDatentypen;
import de.svws_nrw.db.schema.SchemaRevisionen;
import de.svws_nrw.db.schema.SchemaTabelle;
import de.svws_nrw.db.schema.SchemaTabelleCoreType;
import de.svws_nrw.db.schema.SchemaTabelleSpalte;

/**
 * Diese Klasse beinhaltet die Schema-Definition für die Tabelle KAoA_Berufsfeld_Keys.
 */
public class Tabelle_KAoA_Berufsfeld_Keys extends SchemaTabelle {

	/** Die Definition der Tabellenspalte ID */
	public SchemaTabelleSpalte col_ID = add("ID", SchemaDatentypen.BIGINT, true)
			.setNotNull()
			.setJavaComment("Die eindeutige ID für das Berufsfeld");

	/**
	 * Erstellt die Schema-Defintion für die Tabelle KAoA_Berufsfeld_Keys.
	 */
	public Tabelle_KAoA_Berufsfeld_Keys() {
		super("KAoA_Berufsfeld_Keys", SchemaRevisionen.REV_0);
		setMigrate(false);
		setImportExport(false);
		setJavaSubPackage("schule");
		setJavaClassName("DTOKAoABerufsfeldKeys");
		setJavaComment("Gültige Schlüsselwerte für Fremdschlüssel zu den KAOA-Berufsfeldern");
		setCoreType(new SchemaTabelleCoreType(this, KAOABerufsfeld.class, KAOABerufsfeld.data().getVersion(),
				rev -> Arrays.stream(KAOABerufsfeld.values())
						.map(a -> a.historie().stream().map(h -> "" + h.id).toList()).flatMap(Collection::stream).toList()));
	}

}
