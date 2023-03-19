package de.nrw.schule.svws.core.utils.klausurplan;

import java.util.Random;

import jakarta.validation.constraints.NotNull;

/** 
 * Dieser Algorithmus hat folgende Strategie - Pseudocode:
 * <pre>
 * Solange nicht alle Klausuren verteilt sind
 *     Erzeuge einen neuen Termin 
 *     Gehe die Klausurgruppen nach ihrem Knotengrad durch (hoch zu niedrig).
 *         Versuche die Klausurgruppe hinzuzufügen.
 * </pre>
 * 
 * @author Benjamin A. Bartsch 
 */
public class KlausurterminblockungAlgorithmusGreedy2b extends KlausurterminblockungAlgorithmusAbstract {

	/** 
	 * Konstruktor.
	 * 
	 * @param pRandom   Ein {@link Random}-Objekt zur Steuerung des Zufalls über einen Anfangs-Seed.
	 * @param pDynDaten Die aktuellen Blockungsdaten. 
	 */
	public KlausurterminblockungAlgorithmusGreedy2b(@NotNull Random pRandom, @NotNull KlausurterminblockungDynDaten pDynDaten) {
		super(pRandom, pDynDaten);
	}

	@Override
	public @NotNull String toString() {
		return "Schienen nacheinander & Klausuren nach Knotengrad";
	}

	@Override
	public void berechne(long pZeitEnde) {
		_dynDaten.aktion_Clear_TermineNacheinander_GruppeNachGrad();
		_dynDaten.aktionZustand1Speichern();

		while (System.currentTimeMillis() < pZeitEnde) {
			_dynDaten.aktion_Clear_TermineNacheinander_GruppeNachGrad();

			if (_dynDaten.gibIstBesserAlsZustand1())
				_dynDaten.aktionZustand1Speichern();
			else
				_dynDaten.aktionZustand1Laden();
		}

		if (_dynDaten.gibIstBesserAlsZustand2())
			_dynDaten.aktionZustand2Speichern();
	}

}
