import type {
	GostFaecherManager,
	GostHalbjahr,
	GostKlausurenCollectionSkrsKrs,
	GostKlausurtermin,
	GostKlausurterminblockungDaten,
	GostKursklausur,
	GostKursklausurManager,
	KursManager,
	LehrerListeEintrag,
	List,
	SchuelerListeEintrag,
} from "@core";
import type { WritableComputedRef } from "vue";

export interface GostKlausurplanungSchienenProps {
	halbjahr: GostHalbjahr;
	kMan: () => GostKursklausurManager;
	mapSchueler: Map<number, SchuelerListeEintrag>;
	patchKlausur: (klausur: GostKursklausur, patch: Partial<GostKursklausur>) => Promise<GostKlausurenCollectionSkrsKrs>;
	erzeugeKlausurtermin: (quartal: number, istHaupttermin: boolean) => Promise<GostKlausurtermin>;
	loescheKlausurtermine: (termine: List<GostKlausurtermin>) => Promise<void>;
	erzeugeKursklausurenAusVorgaben: (quartal: number) => Promise<void>;
	patchKlausurtermin: (id: number, termin: Partial<GostKlausurtermin>) => Promise<void>;
	blockenKursklausuren: (blockungDaten: GostKlausurterminblockungDaten) => Promise<boolean>;
	quartalsauswahl: WritableComputedRef<0 | 1 | 2>;
	gotoVorgaben: () => Promise<void>;
}
