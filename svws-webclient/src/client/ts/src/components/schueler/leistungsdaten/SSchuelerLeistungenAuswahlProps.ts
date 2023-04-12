import type { SchuelerLernabschnittListeEintrag, List } from "@svws-nrw/svws-core";

export interface SchuelerLeistungenAuswahlProps {
	lernabschnitt: SchuelerLernabschnittListeEintrag | undefined;
	lernabschnitte: List<SchuelerLernabschnittListeEintrag>;
	gotoLernabschnitt: (value: SchuelerLernabschnittListeEintrag | undefined) => Promise<void>;
}