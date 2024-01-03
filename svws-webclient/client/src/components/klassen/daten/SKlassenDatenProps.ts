import type { Schulform, KlassenDaten, Schueler, KlassenListeManager, List, Schulgliederung } from "@core";

export interface KlassenDatenProps {
	schulform: Schulform;
	schulgliederungen: List<Schulgliederung>;
	patch: (data : Partial<KlassenDaten>) => Promise<void>;
	klassenListeManager: () => KlassenListeManager;
	gotoSchueler: (eintrag: Schueler) => Promise<void>,
}