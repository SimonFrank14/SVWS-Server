import type { KlassenListeManager, ServerMode } from "@core";
import type { AbschnittAuswahlDaten } from "@comp";
import type { ViewType } from "@ui";

export interface KlassenAuswahlProps {
	serverMode: ServerMode;
	klassenListeManager: () => KlassenListeManager;
	schuljahresabschnittsauswahl: () => AbschnittAuswahlDaten;
	gotoEintrag: (eintragId?: number | null) => Promise<void>;
	gotoGruppenprozess: (navigate: boolean) => Promise<void>;
	gotoCreationMode: (navigate: boolean) => Promise<void>;
	setFilter: () => Promise<void>;
	setzeDefaultSortierung: () => Promise<void>;
	activeRouteType: ViewType;
}
