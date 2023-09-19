import type { JavaEnum } from '../../../java/lang/JavaEnum';
import { JavaObject } from '../../../java/lang/JavaObject';
import { HashMap } from '../../../java/util/HashMap';
import { Schulform } from '../../../core/types/schule/Schulform';
import { OrganisationsformKatalogEintrag } from '../../../core/data/schule/OrganisationsformKatalogEintrag';
import { Arrays } from '../../../java/util/Arrays';

export class AllgemeinbildendOrganisationsformen extends JavaObject implements JavaEnum<AllgemeinbildendOrganisationsformen> {

	/** the name of the enumeration value */
	readonly __name : string;

	/** the ordinal value for the enumeration value */
	readonly __ordinal : number;

	/** an array containing all values of this enumeration */
	static readonly all_values_by_ordinal : Array<AllgemeinbildendOrganisationsformen> = [];

	/** an array containing all values of this enumeration indexed by their name*/
	static readonly all_values_by_name : Map<string, AllgemeinbildendOrganisationsformen> = new Map<string, AllgemeinbildendOrganisationsformen>();

	/**
	 * Organisationsform: Nicht zuordenbar (Früherziehung für Hör- und Sehgeschädigte, Ambulante Maßnahmen)
	 */
	public static readonly NICHT_ZUGEORDNET : AllgemeinbildendOrganisationsformen = new AllgemeinbildendOrganisationsformen("NICHT_ZUGEORDNET", 0, [new OrganisationsformKatalogEintrag(3000000, "*", "Nicht zuordbar (Früherziehung für Hör- und Sehgeschädigte, Ambulante Maßnahmen)", Arrays.asList(Schulform.S, Schulform.KS), null, null)]);

	/**
	 * Organisationsform: Halbtagsunterricht
	 */
	public static readonly HALBTAG : AllgemeinbildendOrganisationsformen = new AllgemeinbildendOrganisationsformen("HALBTAG", 1, [new OrganisationsformKatalogEintrag(3001000, "1", "Halbtagsunterricht", Arrays.asList(Schulform.FW, Schulform.HI, Schulform.WF, Schulform.G, Schulform.GE, Schulform.GM, Schulform.GY, Schulform.H, Schulform.PS, Schulform.R, Schulform.S, Schulform.KS, Schulform.SG, Schulform.SK, Schulform.SR, Schulform.V), null, null)]);

	/**
	 * Organisationsform: Teilnahme am gebundenen Ganztag
	 */
	public static readonly GANZTAG : AllgemeinbildendOrganisationsformen = new AllgemeinbildendOrganisationsformen("GANZTAG", 2, [new OrganisationsformKatalogEintrag(3002000, "2", "Teilnahme am gebundenen Ganztag", Arrays.asList(Schulform.FW, Schulform.HI, Schulform.WF, Schulform.G, Schulform.GE, Schulform.GM, Schulform.GY, Schulform.H, Schulform.PS, Schulform.R, Schulform.S, Schulform.KS, Schulform.SG, Schulform.SK, Schulform.SR, Schulform.V), null, null)]);

	/**
	 * Organisationsform: Teilnahme am erweiterten Ganztag
	 */
	public static readonly GANZTAG_ERWEITERT : AllgemeinbildendOrganisationsformen = new AllgemeinbildendOrganisationsformen("GANZTAG_ERWEITERT", 3, [new OrganisationsformKatalogEintrag(3003000, "3", "Teilnahme am erweiterten Ganztag", Arrays.asList(Schulform.FW, Schulform.HI, Schulform.WF, Schulform.H, Schulform.R, Schulform.S, Schulform.KS, Schulform.SK), null, null)]);

	/**
	 * Organisationsform: Teilnahme am offenen Ganztag
	 */
	public static readonly GANZTAG_OFFEN : AllgemeinbildendOrganisationsformen = new AllgemeinbildendOrganisationsformen("GANZTAG_OFFEN", 4, [new OrganisationsformKatalogEintrag(3004000, "4", "Teilnahme am offenen Ganztag", Arrays.asList(Schulform.FW, Schulform.HI, Schulform.WF, Schulform.G, Schulform.PS, Schulform.S, Schulform.KS), null, null)]);

	/**
	 * Die Version dieses Core-Types, um beim Datenbank Update-Process die Version des Core-Types feststellen zu können.
	 */
	public static readonly VERSION : number = 1;

	/**
	 * Der aktuellen Daten der Organisationsform
	 */
	public readonly daten : OrganisationsformKatalogEintrag;

	/**
	 * Die Historie mit den Einträgen der Organisationsform
	 */
	public readonly historie : Array<OrganisationsformKatalogEintrag>;

	/**
	 * Eine Hashmap mit allen definierten Organisationsformen, zugeordnet zu ihren Kürzeln
	 */
	private static readonly _mapKuerzel : HashMap<string, AllgemeinbildendOrganisationsformen> = new HashMap();

	/**
	 * Erzeugt eine neue Organisationsform in der Aufzählung.
	 *
	 * @param historie   die Historie der Organisationsform, welche ein Array von
	 *                   {@link OrganisationsformKatalogEintrag} ist
	 */
	private constructor(name : string, ordinal : number, historie : Array<OrganisationsformKatalogEintrag>) {
		super();
		this.__name = name;
		this.__ordinal = ordinal;
		AllgemeinbildendOrganisationsformen.all_values_by_ordinal.push(this);
		AllgemeinbildendOrganisationsformen.all_values_by_name.set(name, this);
		this.historie = historie;
		this.daten = historie[historie.length - 1];
	}

	/**
	 * Gibt eine Map von den Kürzeln der Organisationsformen auf die
	 * zugehörigen Organisationsformen zurück.
	 * Sollte diese noch nicht initialisiert sein, so wird sie initialisiert.
	 *
	 * @return die Map von den Kürzeln auf die zugehörigen Organisationsformen
	 */
	private static getMapByKuerzel() : HashMap<string, AllgemeinbildendOrganisationsformen> {
		if (AllgemeinbildendOrganisationsformen._mapKuerzel.size() === 0) {
			for (const s of AllgemeinbildendOrganisationsformen.values()) {
				if (s.daten !== null)
					AllgemeinbildendOrganisationsformen._mapKuerzel.put(s.daten.kuerzel, s);
			}
		}
		return AllgemeinbildendOrganisationsformen._mapKuerzel;
	}

	/**
	 * Gibt die Organisationsform für das angegebene Kürzel zurück.
	 *
	 * @param kuerzel   das Kürzel der Organisationsform
	 *
	 * @return die Organisationsform oder null, falls das Kürzel ungültig ist
	 */
	public static getByKuerzel(kuerzel : string | null) : AllgemeinbildendOrganisationsformen | null {
		return AllgemeinbildendOrganisationsformen.getMapByKuerzel().get(kuerzel);
	}

	/**
	 * Returns the name of this enumeration value.
	 *
	 * @returns the name
	 */
	public name() : string {
		return this.__name;
	}

	/**
	 * Returns the ordinal value of this enumeration value.
	 *
	 * @returns the ordinal value
	 */
	public ordinal() : number {
		return this.__ordinal;
	}

	/**
	 * Returns the name of this enumeration value.
	 *
	 * @returns the name
	 */
	public toString() : string {
		return this.__name;
	}

	/**
	 * Returns true if this and the other enumeration values are equal.
	 *
	 * @param other   the other enumeration value
	 *
	 * @returns true if they are equal and false otherwise
	 */
	public equals(other : JavaObject) : boolean {
		if (!(other instanceof AllgemeinbildendOrganisationsformen))
			return false;
		return this === other;
	}

	/**
	 * Returns the ordinal value as hashcode, since the ordinal value is unique.
	 *
	 * @returns the ordinal value as hashcode
	 */
	public hashCode() : number {
		return this.__ordinal;
	}

	/**
	 * Compares this enumeration value with the other enumeration value by their ordinal value.
	 *
	 * @param other   the other enumeration value
	 *
	 * @returns a negative, zero or postive value as this enumeration value is less than, equal to
	 *          or greater than the other enumeration value
	 */
	public compareTo(other : AllgemeinbildendOrganisationsformen) : number {
		return this.__ordinal - other.__ordinal;
	}

	/**
	 * Returns an array with enumeration values.
	 *
	 * @returns the array with enumeration values
	 */
	public static values() : Array<AllgemeinbildendOrganisationsformen> {
		return [...this.all_values_by_ordinal];
	}

	/**
	 * Returns the enumeration value with the specified name.
	 *
	 * @param name   the name of the enumeration value
	 *
	 * @returns the enumeration values or null
	 */
	public static valueOf(name : string) : AllgemeinbildendOrganisationsformen | null {
		const tmp : AllgemeinbildendOrganisationsformen | undefined = this.all_values_by_name.get(name);
		return (!tmp) ? null : tmp;
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.types.schule.AllgemeinbildendOrganisationsformen', 'java.lang.Enum'].includes(name);
	}

}

export function cast_de_svws_nrw_core_types_schule_AllgemeinbildendOrganisationsformen(obj : unknown) : AllgemeinbildendOrganisationsformen {
	return obj as AllgemeinbildendOrganisationsformen;
}