import type { List, GostStatistikFachwahl, GostHalbjahr, GostFaecherManager } from "@core";

export interface GostFachwahlenProps {
	fachwahlstatistik: List<GostStatistikFachwahl>;
	faecherManager: GostFaecherManager;
	doSelect: (idFach: number | undefined, bereich: string | undefined, halbjahr?: GostHalbjahr) => Promise<void>;
	selected: () => { idFach?: number, bereich: string };
}