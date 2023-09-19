import type { JavaEnum } from '../../../java/lang/JavaEnum';
import { JavaObject } from '../../../java/lang/JavaObject';
import { HashMap } from '../../../java/util/HashMap';
import { UebergangsempfehlungKatalogEintrag } from '../../../core/data/schueler/UebergangsempfehlungKatalogEintrag';
import { Schulform } from '../../../core/types/schule/Schulform';

export class Uebergangsempfehlung extends JavaObject implements JavaEnum<Uebergangsempfehlung> {

	/** the name of the enumeration value */
	readonly __name : string;

	/** the ordinal value for the enumeration value */
	readonly __ordinal : number;

	/** an array containing all values of this enumeration */
	static readonly all_values_by_ordinal : Array<Uebergangsempfehlung> = [];

	/** an array containing all values of this enumeration indexed by their name*/
	static readonly all_values_by_name : Map<string, Uebergangsempfehlung> = new Map<string, Uebergangsempfehlung>();

	/**
	 * Übergangsempfehlung Hauptschule
	 */
	public static readonly HAUPTSCHULE : Uebergangsempfehlung = new Uebergangsempfehlung("HAUPTSCHULE", 0, [new UebergangsempfehlungKatalogEintrag(1, "H", "Hauptschule", Schulform.H, null, null, null)]);

	/**
	 * Übergangsempfehlung Hauptschule / Realschule (eingeschränkt)
	 */
	public static readonly HAUPTSCHULE_REALSCHULE : Uebergangsempfehlung = new Uebergangsempfehlung("HAUPTSCHULE_REALSCHULE", 1, [new UebergangsempfehlungKatalogEintrag(5, "H/R", "Hauptschule / Realschule (eingeschränkt)", Schulform.H, Schulform.R, null, null)]);

	/**
	 * Übergangsempfehlung Realschule
	 */
	public static readonly REALSCHULE : Uebergangsempfehlung = new Uebergangsempfehlung("REALSCHULE", 2, [new UebergangsempfehlungKatalogEintrag(2, "R", "Realschule", Schulform.R, null, null, null)]);

	/**
	 * Übergangsempfehlung Realschule / Gymnasium (eingeschränkt)
	 */
	public static readonly REALSCHULE_GYMNASIUM : Uebergangsempfehlung = new Uebergangsempfehlung("REALSCHULE_GYMNASIUM", 3, [new UebergangsempfehlungKatalogEintrag(6, "R/GY", "Realschule / Gymnasium (eingeschränkt)", Schulform.R, Schulform.GY, null, null)]);

	/**
	 * Übergangsempfehlung Gymnasium
	 */
	public static readonly GYMNASIUM : Uebergangsempfehlung = new Uebergangsempfehlung("GYMNASIUM", 4, [new UebergangsempfehlungKatalogEintrag(3, "GY", "Gymnasium", Schulform.GY, null, null, null)]);

	/**
	 * Keine Übergangsempfehlung
	 */
	public static readonly KEINE : Uebergangsempfehlung = new Uebergangsempfehlung("KEINE", 5, [new UebergangsempfehlungKatalogEintrag(4, "OHNE", "Keine Empfehlung", null, null, null, null)]);

	/**
	 * Die Version dieses Core-Types, um beim Datenbank Update-Process die Version des Core-Types feststellen zu können.
	 */
	public static readonly VERSION : number = 1;

	/**
	 * Der aktuellen Daten der Übergangsempfehlung
	 */
	public readonly daten : UebergangsempfehlungKatalogEintrag;

	/**
	 * Die Historie mit den Einträgen der Übergangsempfehlung
	 */
	public readonly historie : Array<UebergangsempfehlungKatalogEintrag>;

	/**
	 * Eine Hashmap mit allen definierten Übergangsempfehlungen, zugeordnet zu ihren Kürzeln
	 */
	private static readonly _mapKuerzel : HashMap<string, Uebergangsempfehlung | null> = new HashMap();

	/**
	 * Erzeugt einen neuen Eintrag in der Aufzählung.
	 *
	 * @param historie   die Historie der Eintrags, welche ein Array von
	 *                   {@link UebergangsempfehlungKatalogEintrag} ist
	 */
	private constructor(name : string, ordinal : number, historie : Array<UebergangsempfehlungKatalogEintrag>) {
		super();
		this.__name = name;
		this.__ordinal = ordinal;
		Uebergangsempfehlung.all_values_by_ordinal.push(this);
		Uebergangsempfehlung.all_values_by_name.set(name, this);
		this.historie = historie;
		this.daten = historie[historie.length - 1];
	}

	/**
	 * Gibt eine Map von den Kürzeln auf den zugehörigen Core-Type-Wert.
	 * Sollte diese noch nicht initialisiert sein, so wird sie initialisiert.
	 *
	 * @return die Map von den Kürzeln auf den zugehörigen Core-Type-Wert
	 */
	private static getMapByKuerzel() : HashMap<string, Uebergangsempfehlung | null> {
		if (Uebergangsempfehlung._mapKuerzel.size() === 0) {
			for (const s of Uebergangsempfehlung.values()) {
				if (s.daten !== null)
					Uebergangsempfehlung._mapKuerzel.put(s.daten.kuerzel, s);
			}
		}
		return Uebergangsempfehlung._mapKuerzel;
	}

	/**
	 * Gibt den Core-Type-Wert für das angegebene Kürzel der Übergangsempfehlung zurück.
	 *
	 * @param kuerzel   das Kürzel
	 *
	 * @return die Übergangsempfehlung oder null, falls das Kürzel ungültig ist
	 */
	public static getByKuerzel(kuerzel : string | null) : Uebergangsempfehlung | null {
		return Uebergangsempfehlung.getMapByKuerzel().get(kuerzel);
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
		if (!(other instanceof Uebergangsempfehlung))
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
	public compareTo(other : Uebergangsempfehlung) : number {
		return this.__ordinal - other.__ordinal;
	}

	/**
	 * Returns an array with enumeration values.
	 *
	 * @returns the array with enumeration values
	 */
	public static values() : Array<Uebergangsempfehlung> {
		return [...this.all_values_by_ordinal];
	}

	/**
	 * Returns the enumeration value with the specified name.
	 *
	 * @param name   the name of the enumeration value
	 *
	 * @returns the enumeration values or null
	 */
	public static valueOf(name : string) : Uebergangsempfehlung | null {
		const tmp : Uebergangsempfehlung | undefined = this.all_values_by_name.get(name);
		return (!tmp) ? null : tmp;
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.types.schueler.Uebergangsempfehlung', 'java.lang.Enum'].includes(name);
	}

}

export function cast_de_svws_nrw_core_types_schueler_Uebergangsempfehlung(obj : unknown) : Uebergangsempfehlung {
	return obj as Uebergangsempfehlung;
}