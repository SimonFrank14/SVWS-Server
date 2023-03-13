package de.nrw.schule.svws.core.utils.klausurplan;

import java.util.List;
import java.util.Random;

import de.nrw.schule.svws.core.data.gost.klausuren.GostKursklausur;
import jakarta.validation.constraints.NotNull;

/**
 * Algorithmus zur Blockung von Klausuren auf eine minimale Anzahl von Schienen
 * (ergo Klausurtage).
 * 
 * @author Benjamin A. Bartsch
 */
public class KlausurblockungSchienenAlgorithmus {

	/**
	 * Der Konstruktor ist leer und erstellt auch keine Datenstrukturen.
	 */
	public KlausurblockungSchienenAlgorithmus() {
	}

	/**
	 * @param pInput          Die Eingabe beinhaltet alle Klausuren, welche die SuS beinhalten.
	 * @param pMaxTimeMillis  Logger für Benutzerhinweise, Warnungen und Fehler.
	 * @return Eine Liste von Listen: 1. Ebene = Schienen, 2. Ebene = KlausurIDs
	 */
	public @NotNull List<@NotNull List<@NotNull Long>> berechne(@NotNull List<@NotNull GostKursklausur> pInput, long pMaxTimeMillis) {
		// End-Zeitpunkt berechnet.
		long zeitEndeGesamt = System.currentTimeMillis() + pMaxTimeMillis;

		// Random-Objekt erzeugen
		long seed = new Random().nextLong(); // Trick, um den Seed zu kennen.
		@NotNull Random random = new Random(seed);

		// Konvertierung: KlausurblockungSchienenInput --> KlausurblockungSchienenDynDaten
		KlausurblockungSchienenDynDaten dynDaten = new KlausurblockungSchienenDynDaten(random, pInput);

		// Algorithmen erzeugen
		@NotNull KlausurblockungSchienenAlgorithmusAbstract @NotNull [] algorithmen = new KlausurblockungSchienenAlgorithmusAbstract @NotNull [] {
				// Alle Algorithmen zur Verteilung von Klausuren auf ihre Schienen ...
				new KlausurblockungSchienenAlgorithmusGreedy3(random, dynDaten), // Backtracking
				new KlausurblockungSchienenAlgorithmusGreedy4(random, dynDaten), // DSatur
				new KlausurblockungSchienenAlgorithmusGreedy1(random, dynDaten), // Klausuren zufällig & Schienen zufällig
				new KlausurblockungSchienenAlgorithmusGreedy1b(random, dynDaten), // Klausuren zufällig & Schienen nacheinander
				new KlausurblockungSchienenAlgorithmusGreedy2(random, dynDaten), // Klausuren nach Knotengrad & Schienen zufällig
				new KlausurblockungSchienenAlgorithmusGreedy2b(random, dynDaten), // Klausuren nach Knotengrad & Schienen nacheinander
				new KlausurblockungSchienenAlgorithmusGreedy5(random, dynDaten), // Simulated Annealing
				new KlausurblockungSchienenAlgorithmusGreedy6(random, dynDaten), // Recursive Largest First (RLF)
				// ... Ende der Algorithmen.
		};

		// Sammeln der Ausgaben
		
		//@NotNull LinkedCollection<@NotNull List<@NotNull List<@NotNull Long>>> outputs = new LinkedCollection<>();
		
		// Blockungsschleife
		// System.out.println("----------------------------------------------------");
		dynDaten.aktion_EntferneAlles_SchienenNacheinander_KlausurenZufaellig();
		dynDaten.aktionZustand2Speichern();
		
		long zeitProAlgorithmus = 10L; // Weniger ist nicht gut.
		do {
			// System.out.println("zeitProAlgorithmus --> " + zeitProAlgorithmus);

			// Alle Algorithmus starten (pro Algorithmus ggf. auch mehrfach).
			for (int iAlgo = 0; iAlgo < algorithmen.length; iAlgo++) {
				long zeitEndeRunde = System.currentTimeMillis() + zeitProAlgorithmus;
				algorithmen[iAlgo].berechne(zeitEndeRunde);
				//outputs.addLast(dynDaten.gibErzeugeOutput());
			}

			zeitProAlgorithmus *= 2; // Nächste Runde hat mehr Zeit.
		} while (System.currentTimeMillis() + algorithmen.length * zeitProAlgorithmus <= zeitEndeGesamt); // noch Zeit?
		
		// Lade besten globalen Zustand
		dynDaten.aktionZustand2Laden();

		/*
		while (outputs.size() >= 2) {
			List<@NotNull List<@NotNull Long>> e1 = outputs.pollFirst();
			if (e1 == null) throw new DeveloperNotificationException("Should not happen: e1 ist NULL!");
			List<@NotNull List<@NotNull Long>> e2 = outputs.pollFirst();
			if (e2 == null) throw new DeveloperNotificationException("Should not happen: e2 ist NULL!");
			if (e1.size() < e2.size()) {
				outputs.addLast(e1);
				continue;
			}
			if (e1.size() > e2.size()) {
				outputs.addLast(e2);
				continue;
			}
			if (random.nextBoolean()) {
				outputs.addLast(e1);
			} else {
				outputs.addLast(e2);
			}
		}

		List<@NotNull List<@NotNull Long>> out = outputs.pollFirst();
		if (out == null) throw new DeveloperNotificationException("Should not happen: out ist NULL!");
		return out;
		
		*/
		
		return dynDaten.gibErzeugeOutput();
	}

}
