import type { KlassenListeEintrag, Schuljahresabschnitt, LehrerListeEintrag } from "@core";

export interface KlassenAuswahlProps {
		auswahl: KlassenListeEintrag | undefined,
		mapKatalogeintraege: Map<number, KlassenListeEintrag>;
		mapLehrer: Map<number, LehrerListeEintrag>,
		gotoEintrag: (eintrag: KlassenListeEintrag) => Promise<void>;
		klassenFilter: {search: string, sichtbar: boolean};
		setKlassenFilter: (filter: {search: string, sichtbar: boolean}) => void;
		abschnitte: Map<number, Schuljahresabschnitt>;
		aktAbschnitt: Schuljahresabschnitt;
		aktSchulabschnitt: number;
		setAbschnitt: (abschnitt: Schuljahresabschnitt) => void;
	}