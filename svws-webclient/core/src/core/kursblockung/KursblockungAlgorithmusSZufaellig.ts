import { KursblockungAlgorithmusS, cast_de_svws_nrw_core_kursblockung_KursblockungAlgorithmusS } from '../../core/kursblockung/KursblockungAlgorithmusS';
import { KursblockungStatic } from '../../core/kursblockung/KursblockungStatic';
import { Random } from '../../java/util/Random';
import { KursblockungDynDaten } from '../../core/kursblockung/KursblockungDynDaten';
import { KursblockungDynSchueler } from '../../core/kursblockung/KursblockungDynSchueler';
import { Class } from '../../java/lang/Class';
import { Logger } from '../../core/logger/Logger';

export class KursblockungAlgorithmusSZufaellig extends KursblockungAlgorithmusS {

	/**
	 *  Die Anzahl an Runden ohne Verbesserung, bevor es zum Abbruch kommt.
	 */
	private static readonly MAX_RUNDEN_IN_FOLGE_OHNE_VERBESSERUNG : number = 30;

	/**
	 *  Array der SuS, deren Kurse verteilt werden sollen.
	 */
	private readonly _schuelerArr : Array<KursblockungDynSchueler>;

	/**
	 *  Zur Speicherung einer zufälligen Permutation der Indizes der Schüler.
	 */
	private readonly _perm : Array<number>;


	/**
	 * Im Konstruktor kann die Klasse die jeweiligen Datenstrukturen aufbauen. Kurse dürfen in dieser Methode noch nicht
	 * auf Schienen verteilt werden.
	 *
	 * @param random Ein {@link Random}-Objekt zur Steuerung des Zufalls über einen Anfangs-Seed.
	 * @param logger Logger zum Protokollieren von Warnungen und Fehlern.
	 * @param dynDat Die dynamischen Blockungsdaten.
	 */
	public constructor(random : Random, logger : Logger, dynDat : KursblockungDynDaten) {
		super(random, logger, dynDat);
		this._schuelerArr = dynDat.gibSchuelerArrayAlle();
		this._perm = KursblockungStatic.gibPermutation(this._random, this._schuelerArr.length);
	}

	/**
	 * Der Algorithmus verteilt die SuS auf ihre Kurse zufällig. Kommt es während des Verteilens zur Kollision, so wird
	 * der Kurs nicht gewählt.
	 */
	public berechne() : void {
		this.dynDaten.aktionSchuelerAusAllenKursenEntfernen();
		this.dynDaten.gibStatistik().aktionBewertungSpeichernS();
		let countKeineVerbesserung : number = 0;
		do {
			countKeineVerbesserung = this.verteileSchuelerAlle() ? 0 : (countKeineVerbesserung + 1);
		} while (countKeineVerbesserung < KursblockungAlgorithmusSZufaellig.MAX_RUNDEN_IN_FOLGE_OHNE_VERBESSERUNG);
	}

	/**
	 * Der Algorithmus verteilt die SuS in zufälliger Reihenfolge ein weiteres Mal zufällig. Falls die Verteilung
	 * schlechter ist, wird der vorherige Zustand wiederhergestellt.
	 *
	 * @return TRUE, falls der Zustand sich verbessert hat.
	 */
	private verteileSchuelerAlle() : boolean {
		let verbesserung : boolean = false;
		KursblockungStatic.aktionPermutiere(this._random, this._perm);
		for (let p : number = 0; p < this._schuelerArr.length; p++) {
			const i : number = this._perm[p];
			verbesserung = verbesserung || this.verteileSchuelerEiner(this._schuelerArr[i]);
		}
		return verbesserung;
	}

	private verteileSchuelerEiner(schueler : KursblockungDynSchueler) : boolean {
		this.dynDaten.gibStatistik().aktionBewertungSpeichernS();
		schueler.aktionZustandSpeichernS();
		schueler.aktionKurseAlleEntfernen();
		schueler.aktionKurseVerteilenNurFachartenMitEinemErlaubtenKurs();
		schueler.aktionKurseVerteilenZufaellig();
		const cmp : number = this.dynDaten.gibStatistik().gibBewertungZustandS_NW_KD();
		if (cmp < 0)
			schueler.aktionZustandLadenS();
		return cmp > 0;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.kursblockung.KursblockungAlgorithmusSZufaellig';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.kursblockung.KursblockungAlgorithmusS', 'de.svws_nrw.core.kursblockung.KursblockungAlgorithmusSZufaellig'].includes(name);
	}

	public static class = new Class<KursblockungAlgorithmusSZufaellig>('de.svws_nrw.core.kursblockung.KursblockungAlgorithmusSZufaellig');

}

export function cast_de_svws_nrw_core_kursblockung_KursblockungAlgorithmusSZufaellig(obj : unknown) : KursblockungAlgorithmusSZufaellig {
	return obj as KursblockungAlgorithmusSZufaellig;
}
