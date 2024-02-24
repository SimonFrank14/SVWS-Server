import type { GostBlockungRegelUpdate, GostBlockungsdatenManager, GostBlockungsergebnisKursSchuelerZuordnung, GostBlockungsergebnisManager, SchuelerListeEintrag } from "@core";
import type { ApiStatus } from "~/components/ApiStatus";

export interface GostUmwahlansichtProps {
	hatBlockung: boolean,
	hatErgebnis: boolean,
	regelnUpdate: (update: GostBlockungRegelUpdate) => Promise<void>;
	updateKursSchuelerZuordnung: (idSchueler: number, idKursNeu: number, idKursAlt: number | undefined) => Promise<boolean>;
	removeKursSchuelerZuordnung: (zuordnungen: Iterable<GostBlockungsergebnisKursSchuelerZuordnung>) => Promise<boolean>;
	autoKursSchuelerZuordnung: (idSchueler : number) => Promise<void>;
	gotoSchueler: (idSchueler: number) => Promise<void>;
	gotoLaufbahnplanung: (idSchueler: number) => Promise<void>;
	getDatenmanager: () => GostBlockungsdatenManager;
	getErgebnismanager: () => GostBlockungsergebnisManager;
	schueler: SchuelerListeEintrag | undefined;
	apiStatus: ApiStatus;
}