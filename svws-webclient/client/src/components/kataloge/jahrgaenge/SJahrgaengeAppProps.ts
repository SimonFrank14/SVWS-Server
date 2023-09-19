import type { JahrgangsListeEintrag } from "@core";
import type { AuswahlChildData } from "~/components/AuswahlChildData";

export interface JahrgaengeAppProps {
	auswahl: JahrgangsListeEintrag | undefined;
	setTab: (value: AuswahlChildData) => Promise<void>;
	tab: AuswahlChildData;
	tabs: AuswahlChildData[];
	tabsHidden: boolean[];
}