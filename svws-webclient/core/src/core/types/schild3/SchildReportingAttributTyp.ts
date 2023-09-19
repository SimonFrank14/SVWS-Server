import type { JavaEnum } from '../../../java/lang/JavaEnum';
import { JavaObject } from '../../../java/lang/JavaObject';

export class SchildReportingAttributTyp extends JavaObject implements JavaEnum<SchildReportingAttributTyp> {

	/** the name of the enumeration value */
	readonly __name : string;

	/** the ordinal value for the enumeration value */
	readonly __ordinal : number;

	/** an array containing all values of this enumeration */
	static readonly all_values_by_ordinal : Array<SchildReportingAttributTyp> = [];

	/** an array containing all values of this enumeration indexed by their name*/
	static readonly all_values_by_name : Map<string, SchildReportingAttributTyp> = new Map<string, SchildReportingAttributTyp>();

	/**
	 * Boolean
	 */
	public static readonly BOOLEAN : SchildReportingAttributTyp = new SchildReportingAttributTyp("BOOLEAN", 0, "boolean");

	/**
	 * Ganzzahl
	 */
	public static readonly INT : SchildReportingAttributTyp = new SchildReportingAttributTyp("INT", 1, "integer");

	/**
	 * Zahl, auch Kommazahlen
	 */
	public static readonly NUMBER : SchildReportingAttributTyp = new SchildReportingAttributTyp("NUMBER", 2, "number");

	/**
	 * Zeichenkette
	 */
	public static readonly STRING : SchildReportingAttributTyp = new SchildReportingAttributTyp("STRING", 3, "string");

	/**
	 * Mehrzeilige Zeichenkette
	 */
	public static readonly MEMO : SchildReportingAttributTyp = new SchildReportingAttributTyp("MEMO", 4, "memo");

	/**
	 * Datumsangabe
	 */
	public static readonly DATE : SchildReportingAttributTyp = new SchildReportingAttributTyp("DATE", 5, "date");

	/**
	 * Der JSON-Typ als String
	 */
	private readonly type : string;

	/**
	 * Initialisiert den Datentyp für die Aufzählung
	 *
	 * @param type   der JSON-Datentyp
	 */
	private constructor(name : string, ordinal : number, type : string) {
		super();
		this.__name = name;
		this.__ordinal = ordinal;
		SchildReportingAttributTyp.all_values_by_ordinal.push(this);
		SchildReportingAttributTyp.all_values_by_name.set(name, this);
		this.type = type;
	}

	public toString() : string {
		return this.type;
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
	 * Returns true if this and the other enumeration values are equal.
	 *
	 * @param other   the other enumeration value
	 *
	 * @returns true if they are equal and false otherwise
	 */
	public equals(other : JavaObject) : boolean {
		if (!(other instanceof SchildReportingAttributTyp))
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
	public compareTo(other : SchildReportingAttributTyp) : number {
		return this.__ordinal - other.__ordinal;
	}

	/**
	 * Returns an array with enumeration values.
	 *
	 * @returns the array with enumeration values
	 */
	public static values() : Array<SchildReportingAttributTyp> {
		return [...this.all_values_by_ordinal];
	}

	/**
	 * Returns the enumeration value with the specified name.
	 *
	 * @param name   the name of the enumeration value
	 *
	 * @returns the enumeration values or null
	 */
	public static valueOf(name : string) : SchildReportingAttributTyp | null {
		const tmp : SchildReportingAttributTyp | undefined = this.all_values_by_name.get(name);
		return (!tmp) ? null : tmp;
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.types.schild3.SchildReportingAttributTyp', 'java.lang.Enum'].includes(name);
	}

}

export function cast_de_svws_nrw_core_types_schild3_SchildReportingAttributTyp(obj : unknown) : SchildReportingAttributTyp {
	return obj as SchildReportingAttributTyp;
}