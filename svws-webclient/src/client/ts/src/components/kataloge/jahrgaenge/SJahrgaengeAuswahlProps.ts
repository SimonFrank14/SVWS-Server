import { JahrgangsListeEintrag, Schuljahresabschnitt } from "@svws-nrw/svws-core";

export interface JahrgaengeAuswahlProps {
	auswahl: JahrgangsListeEintrag | undefined;
	mapKatalogeintraege: Map<number, JahrgangsListeEintrag>;
	gotoEintrag: (religion: JahrgangsListeEintrag) => Promise<void>;
	abschnitte: Map<number, Schuljahresabschnitt>;
	aktAbschnitt: Schuljahresabschnitt;
	aktSchulabschnitt: number;
	setAbschnitt: (abschnitt: Schuljahresabschnitt) => void;
	returnToKataloge: () => Promise<void>;
}