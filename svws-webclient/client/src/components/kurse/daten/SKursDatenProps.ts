import type { KursDaten, JahrgangsListeEintrag, LehrerListeEintrag, Schueler } from "@core";

export interface KursDatenProps {
		patch: (data : Partial<KursDaten>) => Promise<void>;
		data: KursDaten;
		mapJahrgaenge: Map<number, JahrgangsListeEintrag>;
		mapLehrer: Map<number, LehrerListeEintrag>;
		gotoSchueler: (eintrag: Schueler) => Promise<void>,
	}