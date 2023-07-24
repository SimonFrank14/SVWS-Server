import { JavaObject } from '../../java/lang/JavaObject';
import { StringBuilder } from '../../java/lang/StringBuilder';
import type { Collection } from '../../java/util/Collection';

export class StringUtils extends JavaObject {

	private static readonly buchstaben : Array<string> = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"];


	private constructor() {
		super();
	}

	/**
	 * Liefert einen durch Komma getrennten String aller Inhalte der übergebenen {@link Collection}.
	 *
	 * @param collection  Die zu verbindenden Inhalte.
	 *
	 * @return einen durch Komma getrennten String aller Inhalte der übergebenen {@link Collection}.
	 */
	public static collectionToCommaSeparatedString(collection : Collection<string>) : string {
		const sb : StringBuilder = new StringBuilder();
		for (const s of collection)
			if (sb.isEmpty())
				sb.append(s);
			else
				sb.append(", " + s!);
		return sb.toString();
	}

	/**
	 * Liefert die umgewandelte Zahl aus dem Bereich 0=A bis 25=Z in einen Buchstaben um.
	 *
	 * @param number  Die umzuwandelnde Zahl.
	 *
	 * @return die umgewandelte Zahl aus dem Bereich 0=A bis 25=Z in einen Buchstaben um.
	 */
	public static numberToLetterIndex0(number : number) : string {
		return (number < 0) || (number > 25) ? "" : StringUtils.buchstaben[number];
	}

	/**
	 * Liefert die umgewandelte Zahl aus dem Bereich 1=A bis 26=Z in einen Buchstaben um.
	 *
	 * @param number  Die umzuwandelnde Zahl.
	 *
	 * @return die umgewandelte Zahl aus dem Bereich 1=A bis 26=Z in einen Buchstaben um.
	 */
	public static numberToLetterIndex1(number : number) : string {
		return (number < 1) || (number > 26) ? "" : StringUtils.buchstaben[number - 1];
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.utils.StringUtils'].includes(name);
	}

}

export function cast_de_svws_nrw_core_utils_StringUtils(obj : unknown) : StringUtils {
	return obj as StringUtils;
}
