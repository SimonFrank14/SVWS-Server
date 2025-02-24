import type { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";
import type { GostKursplanungAuswahlProps } from "~/components/gost/kursplanung/SGostKursplanungAuswahlProps";
import type { GostKursplanungProps } from "~/components/gost/kursplanung/SGostKursplanungProps";

import type { GostBlockungListeneintrag, GostBlockungsergebnis} from "@core";
import { BenutzerKompetenz, DeveloperNotificationException, GostHalbjahr, ServerMode } from "@core";

import { api } from "~/router/Api";
import { RouteNode } from "~/router/RouteNode";

import { routeApp } from "~/router/apps/RouteApp";
import { routeGost, type RouteGost } from "~/router/apps/gost/RouteGost";
import { routeGostKursplanungSchueler } from "~/router/apps/gost/kursplanung/RouteGostKursplanungSchueler";

import { RouteDataGostKursplanung } from "~/router/apps/gost/kursplanung/RouteDataGostKursplanung";

import { ConfigElement } from "~/components/Config";
import { schulformenGymOb } from "~/router/RouteHelper";
import { routeError } from "~/router/error/RouteError";

const SGostKursplanung = () => import("~/components/gost/kursplanung/SGostKursplanung.vue");
const SGostKursplanungAuswahl = () => import("~/components/gost/kursplanung/SGostKursplanungAuswahl.vue");

export class RouteGostKursplanung extends RouteNode<RouteDataGostKursplanung, RouteGost> {

	public constructor() {
		super(schulformenGymOb, [
			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
		], "gost.kursplanung", "kursplanung/:halbjahr([0-5])?/:idblockung(\\d+)?/:idergebnis(\\d+)?", SGostKursplanung, new RouteDataGostKursplanung());
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.setView("gost_child_auswahl", SGostKursplanungAuswahl, (route) => this.getAuswahlProps(route));
		super.text = "Kursplanung";
		super.children = [
			routeGostKursplanungSchueler
		];
		super.defaultChild = routeGostKursplanungSchueler;
		this.isHidden = (params?: RouteParams) => {
			return this.checkHidden(params);
		}
		api.config.addElements([
			new ConfigElement("gost.kursplanung.kursansicht.ausgeblendet", "user", "false"),
			new ConfigElement("gost.kursplanung.kursansicht.sortierung", "user", "kursart"),
			new ConfigElement("gost.kursplanung.kursansicht.zeigeSchienenbezeichnung", "user", "false"),
			new ConfigElement("gost.kursplanung.umkursen.fixierteVerschieben", "user", "false"),
			new ConfigElement("gost.kursplanung.umkursen.inZielkursFixieren", "user", "false"),
			new ConfigElement("gost.kursplanung.berechnung.ausfuehrlicheDarstellungKursdifferenz", "user", "true"),
		]);
	}

	public checkHidden(params?: RouteParams) {
		try {
			const { abiturjahr } = params ? RouteNode.getIntParams(params, ["abiturjahr"]) : { abiturjahr: null };
			if ((abiturjahr === null) || (abiturjahr === -1))
				return { name: routeGost.defaultChild!.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, abiturjahr }};
			return false;
		} catch (e) {
			return routeError.getRoute(e as DeveloperNotificationException);
		}
	}

	public async beforeEach(to: RouteNode<any, any>, to_params: RouteParams, from: RouteNode<any, any> | undefined, from_params: RouteParams) : Promise<boolean | void | Error | RouteLocationRaw> {
		try {
			const { abiturjahr, halbjahr: halbjahrId, idblockung: idBlockung, idergebnis: idErgebnis } = RouteNode.getIntParams(to_params, ["abiturjahr", "halbjahr", "idblockung", "idergebnis"]);
			const halbjahr = GostHalbjahr.fromID(halbjahrId ?? null);
			if (abiturjahr === undefined || abiturjahr === -1)
				return this.getRoute();
			if (halbjahr === null) {
				let hj = GostHalbjahr.getPlanungshalbjahrFromAbiturjahrSchuljahrUndHalbjahr(abiturjahr, routeApp.data.aktAbschnitt.value.schuljahr, routeApp.data.aktAbschnitt.value.abschnitt);
				if (hj === null) // In zwei Fällen existiert kein Planungshalbjahr, z.B. weil der Abiturjahrgang (fast) abgeschlossen ist oder noch in der Sek I ist.
					hj = (abiturjahr < routeApp.data.aktAbschnitt.value.schuljahr + routeApp.data.aktAbschnitt.value.abschnitt) ? GostHalbjahr.Q22 : GostHalbjahr.EF1;
				return this.getRouteHalbjahr(abiturjahr, hj.id);
			}
			if ((idBlockung === undefined) && (idErgebnis !== undefined))
				return this.getRouteHalbjahr(abiturjahr, halbjahr.id);
			return true;
		} catch(e) {
			return routeError.getRoute(e instanceof Error ? e : new DeveloperNotificationException("Unbekannter Fehler beim Laden der Klausurplanungsdaten."));
		}
	}

	protected async update(to: RouteNode<any, any>, to_params: RouteParams, from: RouteNode<any, any> | undefined, from_params: RouteParams, isEntering: boolean) : Promise<void | Error | RouteLocationRaw> {
		try {
			const { abiturjahr, halbjahr: halbjahrId, idblockung: idBlockung, idergebnis: idErgebnis } = RouteNode.getIntParams(to_params, ["abiturjahr", "halbjahr", "idblockung", "idergebnis"]);
			const halbjahr = GostHalbjahr.fromID(halbjahrId ?? null);
			// Prüfe den Abiturjahrgang und setze diesen ggf.
			if (abiturjahr === undefined)
				throw new DeveloperNotificationException("Fehler: Der Abiturjahrgang darf an dieser Stelle nicht undefined sein.");
			await this.data.setAbiturjahr(abiturjahr, isEntering);
			// Prüfe das Halbjahr und setzte dieses ggf.
			if (halbjahr === null) {
				let hj = GostHalbjahr.getPlanungshalbjahrFromAbiturjahrSchuljahrUndHalbjahr(abiturjahr, routeApp.data.aktAbschnitt.value.schuljahr, routeApp.data.aktAbschnitt.value.abschnitt);
				if (hj === null) // In zwei Fällen existiert kein Planungshalbjahr, z.B. weil der Abiturjahrgang (fast) abgeschlossen ist oder noch in der Sek I ist.
					hj = (abiturjahr < routeApp.data.aktAbschnitt.value.schuljahr + routeApp.data.aktAbschnitt.value.abschnitt) ? GostHalbjahr.Q22 : GostHalbjahr.EF1;
				return this.getRouteHalbjahr(abiturjahr, hj.id);
			}
			const changedHalbjahr: boolean = await this.data.setHalbjahr(halbjahr);
			if (changedHalbjahr && (halbjahr.id !== this.data.halbjahr.id))
				return this.getRouteHalbjahr(abiturjahr, halbjahr.id);
			// Prüfe die Blockung und setzte diese ggf.
			if (idBlockung === undefined) {
			// ... wurde die ID der Blockung auf undefined gesetzt, so prüfe, ob die Blockungsliste leer ist und wähle ggf. die aktive Blockung oder das erste Element aus
				if (this.data.mapBlockungen.size > 0) {
					let blockungsEintrag : GostBlockungListeneintrag | undefined = undefined;
					for (const e of this.data.mapBlockungen.values()) {
						if (e.istAktiv === true) {
							blockungsEintrag = e;
							break;
						}
					}
					if (blockungsEintrag === undefined)
						[blockungsEintrag] = this.data.mapBlockungen.values();
					return this.getRouteBlockung(abiturjahr, halbjahr.id, blockungsEintrag.id);
				}
				if (this.data.hatBlockung)
					await this.data.setAuswahlBlockung(undefined);
				return; // akzeptiere die Route, da keine Blockung für den Abiturjahrgang und das Halbjahr vorhanden ist.
			}
			const blockungsEintrag = this.data.mapBlockungen.get(idBlockung);
			if (blockungsEintrag === undefined) {
			// ... eine Blockung mit der ID ist nicht vorhanden. Die Route wird abgelehnt und es findet eine Umleitung statt
			// TODO sollte z.B. nach Anlegen einer Ableitung die neue ID trotzdem erreichen können
				return this.getRouteHalbjahr(abiturjahr, halbjahr.id);
			}
			if (!this.data.hatBlockung || (this.data.auswahlBlockung.id !== blockungsEintrag.id)) {
				await this.data.setAuswahlBlockung(blockungsEintrag);
				// ... wurde die ID der Blockung verändert, so setze den neu ausgewählten Blockungs-Eintrag und aktualisiere ggf. die Route
				if (idErgebnis === undefined) {
					if (this.data.ergebnisse.size() <= 0)
						throw new DeveloperNotificationException("Fehler bei der Blockung. Es muss bei einer Blockung immer mindestens das Vorlagen-Ergebnis vorhanden sein.");
					// ...wenn kein Ergebnis in der Route gesetzt wurde, aber ein Ergebnis existiert, dann setze die Route neu auf das Vorlagen-Ergebnis und ggf. auf den aktuellen Schüler
					if (this.data.hatSchueler)
						return this.getRouteSchueler(abiturjahr, halbjahr.id, blockungsEintrag.id, this.data.auswahlErgebnis.id, this.data.auswahlSchueler.id);
					return this.getRouteErgebnis(abiturjahr, halbjahr.id, blockungsEintrag.id, this.data.auswahlErgebnis.id);
				}
			}
			// Prüfe das Blockungsergebnis und setzte dieses ggf.
			if (idErgebnis === undefined) {
			// ... wurde die ID des Ergebnisses auf undefined setzt, so prüfe, ob die Ergebnisliste leer ist und wähle ggf. das aktiver oder das erste Element aus
				if ((this.data.hatBlockung) && (this.data.ergebnisse.size() > 0)) {
					let ergebnis : GostBlockungsergebnis | undefined = undefined;
					for (const e of this.data.datenmanager.ergebnisGetListeSortiertNachBewertung()) {
						if (e.istAktiv === true) {
							ergebnis = e;
							break;
						}
					}
					if (ergebnis === undefined)
						ergebnis = this.data.datenmanager.ergebnisGetListeSortiertNachBewertung().get(0);
					return this.getRouteErgebnis(abiturjahr, halbjahr.id, idBlockung, ergebnis.id);
				}
				if ((this.data.hatBlockung) && (this.data.ergebnisse.size() <= 0))
					return; // akzeptiere die Route, da kein Ergebnis vorhanden ist - sollt eigentlich nicht vorkommen, da ein Vorlagenergebnis notwendig ist
				return this.getRouteHalbjahr(abiturjahr, halbjahr.id); // Es existiert keine Blockung, also route zu der Halbjahresauswahl
			}
			let ergebnis;
			try {
				ergebnis = routeGostKursplanung.data.datenmanager.ergebnisGet(idErgebnis);
			} catch (e) {
			// ...wenn die Ergebnis-ID ungültig ist, dann setze ggf. das erste Ergebnis und route dahin
				if (this.data.ergebnisse.size() <= 0)
					throw new DeveloperNotificationException("Fehler bei der Blockung. Es muss bei einer Blockung immer mindestens das Vorlagen-Ergebnis vorhanden sein.");
				const ergebnis = this.data.datenmanager.ergebnisGetListeSortiertNachBewertung().get(0);
				return this.getRouteErgebnis(abiturjahr, halbjahr.id, idBlockung, ergebnis.id);
			}
			if (routeGostKursplanung.data.auswahlErgebnis.id !== ergebnis.id) {
			// ... wurde die ID des Ergebnisses verändert, so setze den neu ausgewählten Ergebnis-Eintrag
				await routeGostKursplanung.data.setAuswahlErgebnis(ergebnis);
				if (this.data.hatSchueler)
					return this.getRouteSchueler(abiturjahr, halbjahr.id, blockungsEintrag.id, this.data.auswahlErgebnis.id, this.data.auswahlSchueler.id);
				return this.getRouteErgebnis(abiturjahr, halbjahr.id, blockungsEintrag.id, this.data.auswahlErgebnis.id);
			}
			// Setze die aktuelle Route auf die Schüler-Route, so dass die Auswahl geladen wird.
			if (this.name === to.name)
				return routeGostKursplanungSchueler.getRoute(abiturjahr, halbjahr.id, ergebnis.blockungID, ergebnis.id, undefined);
		} catch(e) {
			return routeError.getRoute(e instanceof Error ? e : new DeveloperNotificationException("Unbekannter Fehler beim Laden der Klausurplanungsdaten."));
		}
	}

	public async leave(from: RouteNode<any, any>, from_params: RouteParams): Promise<void> {
		await this.data.setAuswahlBlockung(undefined, true);
		this.data.unsetHalbjahr();
	}

	public getRoute() : RouteLocationRaw {
		return { name: routeGost.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, abiturjahr: -1 }};
	}

	public getRouteHalbjahr(abiturjahr: number, halbjahr: number) : RouteLocationRaw {
		return { name: this.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, abiturjahr, halbjahr }};
	}

	public getRouteBlockung(abiturjahr: number, halbjahr: number, idblockung: number) : RouteLocationRaw {
		return { name: this.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, abiturjahr, halbjahr, idblockung }};
	}

	public getRouteErgebnis(abiturjahr: number, halbjahr: number, idblockung: number, idergebnis: number) : RouteLocationRaw {
		return { name: this.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, abiturjahr, halbjahr, idblockung, idergebnis }};
	}

	public getRouteSchueler(abiturjahr: number, halbjahr: number, idblockung: number, idergebnis: number, idschueler: number) : RouteLocationRaw {
		return { name: routeGostKursplanungSchueler.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, abiturjahr, halbjahr, idblockung, idergebnis, idschueler }};
	}


	public getAuswahlProps(to: RouteLocationNormalized): GostKursplanungAuswahlProps {
		return {
			schulform: api.schulform,
			serverMode: api.mode,
			benutzerKompetenzen: api.benutzerKompetenzen,
			benutzerKompetenzenAbiturjahrgaenge: api.benutzerKompetenzenAbiturjahrgaenge,
			// Für die Halbjahresauswahl
			setHalbjahr: this.data.gotoHalbjahr,
			jahrgangsdaten: () => this.data.jahrgangsdaten,
			halbjahr: this.data.halbjahr,
			// ... und zusätzlich für die Blockungsauswahl
			addBlockung: this.data.addBlockung,
			removeBlockung: this.data.removeBlockung,
			patchBlockung: this.data.patchBlockung,
			gotoBlockung: this.data.gotoBlockung,
			auswahlBlockung: this.data.hatBlockung ? this.data.auswahlBlockung : undefined,
			mapBlockungen: () => this.data.mapBlockungen,
			addErgebnisse: this.data.addErgebnisse,
			apiStatus: api.status,
			// ... und zusätzlich für die Ergebnisauswahl
			getDatenmanager: () => this.data.datenmanager,
			getErgebnismanager: () => this.data.ergebnismanager,
			patchErgebnis: this.data.patchErgebnis,
			rechneGostBlockung: this.data.rechneGostBlockung,
			removeErgebnisse: this.data.removeErgebnisse,
			gotoErgebnis: this.data.gotoErgebnis,
			hatBlockung: this.data.hatBlockung && this.data.hatErgebnis,
			auswahlErgebnis: this.data.hatErgebnis ? this.data.auswahlErgebnis : undefined,
			restoreBlockung: this.data.restoreBlockung,
			revertBlockung: this.data.revertBlockung,
			aktAbschnitt: api.abschnitt,
			mode: api.mode,
			ausfuehrlicheDarstellungKursdifferenz: () => this.data.ausfuehrlicheDarstellungKursdifferenz,
			setAusfuehrlicheDarstellungKursdifferenz: this.data.setAusfuehrlicheDarstellungKursdifferenz,
			mapCoreTypeNameJsonData: () => api.mapCoreTypeNameJsonData,
		}
	}

	public getProps(to: RouteLocationNormalized): GostKursplanungProps {
		return {
			schulform: api.schulform,
			serverMode: api.mode,
			benutzerKompetenzen: api.benutzerKompetenzen,
			benutzerKompetenzenAbiturjahrgaenge: api.benutzerKompetenzenAbiturjahrgaenge,
			jahrgangsdaten: () => this.data.jahrgangsdaten,
			hatBlockung: this.data.hatBlockung && this.data.hatErgebnis,
			addBlockung: this.data.addBlockung,
			restoreBlockung: this.data.restoreBlockung,
			getDatenmanager: () => this.data.datenmanager,
			getKursauswahl: () => this.data.kursAuswahl,
			setKursauswahl: this.data.setKursAuswahl,
			hatErgebnis: this.data.hatErgebnis,
			getErgebnismanager: () => this.data.ergebnismanager,
			regelnUpdate: this.data.regelnUpdate,
			updateKursSchienenZuordnung: this.data.updateKursSchienenZuordnung,
			patchSchiene: this.data.patchSchiene,
			addSchiene: this.data.addSchiene,
			removeSchiene: this.data.removeSchiene,
			patchKurs: this.data.patchKurs,
			addKurs: this.data.addKurs,
			removeKurse: this.data.removeKurse,
			combineKurs: this.data.combineKurs,
			splitKurs: this.data.splitKurs,
			addKursLehrer: this.data.addKursLehrer,
			removeKursLehrer: this.data.removeKursLehrer,
			addSchieneKurs: this.data.addSchieneKurs,
			removeSchieneKurs: this.data.removeSchieneKurs,
			ergebnisAbleiten: this.data.ergebnisAbleiten,
			ergebnisHochschreiben: this.data.ergebnisHochschreiben,
			ergebnisAktivieren: this.data.ergebnisAktivieren,
			ergebnisSynchronisieren: this.data.ergebnisSynchronisieren,
			kurssortierung: this.data.kurssortierung,
			getPDF: this.data.getPDF,
			existiertSchuljahresabschnitt: this.data.existiertSchuljahresabschnitt,
			schuelerFilter: () => this.data.schuelerFilter,
			faecherManager: routeGost.data.faecherManager,
			halbjahr: this.data.halbjahr,
			mapLehrer: this.data.mapLehrer,
			mapFachwahlStatistik: () => this.data.mapFachwahlStatistik,
			updateKursSchuelerZuordnungen: this.data.updateKursSchuelerZuordnungen,
			apiStatus: api.status,
			//Config
			blockungstabelleHidden: () => this.data.blockungstabelleHidden,
			setBlockungstabelleHidden: this.data.setBlockungstabelleHidden,
			zeigeSchienenbezeichnungen: () => this.data.zeigeSchienenbezeichnungen,
			setZeigeSchienenbezeichnungen: this.data.setZeigeSchienenbezeichnungen,
			fixierteVerschieben: () => this.data.fixierteVerschieben,
			setFixierteVerschieben: this.data.setFixierteVerschieben,
			inZielkursFixieren: () => this.data.inZielkursFixieren,
			setInZielkursFixieren: this.data.setInZielkursFixieren,
		}
	}

}

export const routeGostKursplanung = new RouteGostKursplanung();
