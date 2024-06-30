package de.svws_nrw.core.utils.gost.klausurplanung;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.svws_nrw.core.adt.PairNN;
import de.svws_nrw.core.adt.map.HashMap2D;
import de.svws_nrw.core.adt.map.HashMap3D;
import de.svws_nrw.core.adt.map.HashMap4D;
import de.svws_nrw.core.data.gost.GostFach;
import de.svws_nrw.core.data.gost.klausurplanung.GostKlausurenUpdate;
import de.svws_nrw.core.data.gost.klausurplanung.GostKlausurtermin;
import de.svws_nrw.core.data.gost.klausurplanung.GostKlausurvorgabe;
import de.svws_nrw.core.data.gost.klausurplanung.GostKursklausur;
import de.svws_nrw.core.data.gost.klausurplanung.GostSchuelerklausur;
import de.svws_nrw.core.data.gost.klausurplanung.GostSchuelerklausurTermin;
import de.svws_nrw.core.data.kurse.KursDaten;
import de.svws_nrw.core.data.lehrer.LehrerListeEintrag;
import de.svws_nrw.core.data.schueler.SchuelerListeEintrag;
import de.svws_nrw.core.data.stundenplan.StundenplanZeitraster;
import de.svws_nrw.core.exceptions.DeveloperNotificationException;
import de.svws_nrw.core.types.Wochentag;
import de.svws_nrw.core.types.fach.ZulaessigesFach;
import de.svws_nrw.core.types.gost.GostHalbjahr;
import de.svws_nrw.core.utils.DateUtils;
import de.svws_nrw.core.utils.KursManager;
import de.svws_nrw.core.utils.ListUtils;
import de.svws_nrw.core.utils.Map2DUtils;
import de.svws_nrw.core.utils.Map3DUtils;
import de.svws_nrw.core.utils.Map4DUtils;
import de.svws_nrw.core.utils.MapUtils;
import de.svws_nrw.core.utils.gost.GostFaecherManager;
import de.svws_nrw.core.utils.stundenplan.StundenplanManager;
import jakarta.validation.constraints.NotNull;

/**
 * Ein Manager zur Handhabung von Daten des Typs {@link GostKursklausur}.
 * Hierbei werden auch Hilfsmethoden zur Interpretation der Daten erzeugt. Es
 * werden Kursklausuren eines Gost-Halbjahres eines Abiturjahrgangs verwaltet.
 */
public class GostKursklausurManager {

	private @NotNull GostKlausurvorgabenManager _vorgabenManager;
	private KursManager _kursManager = null;
	private Map<Long, LehrerListeEintrag> _lehrerMap = null;
	private Map<Long, SchuelerListeEintrag> _schuelerMap = null;

	private static final @NotNull Comparator<GostKlausurtermin> _compTermin =
			(final @NotNull GostKlausurtermin a, final @NotNull GostKlausurtermin b) -> {
				if ((a.datum != null) && (b.datum != null))
					return a.datum.compareTo(b.datum);
				if (b.datum != null)
					return +1;
				if (a.datum != null)
					return -1;
//				if (a.quartal != b.quartal)
//					return a.quartal - b.quartal;
				return Long.compare(a.id, b.id);
			};

	private final @NotNull Comparator<GostKursklausur> _compKursklausur =
			(final @NotNull GostKursklausur a, final @NotNull GostKursklausur b) -> {
				final GostFaecherManager faecherManager = _vorgabenManager.getFaecherManagerOrNull();
				final @NotNull GostKlausurvorgabe va = vorgabeByKursklausur(a);
				final @NotNull GostKlausurvorgabe vb = vorgabeByKursklausur(b);
				if (va.kursart.compareTo(vb.kursart) < 0)
					return +1;
				if (va.kursart.compareTo(vb.kursart) > 0)
					return -1;
				if (faecherManager != null) {
					final GostFach aFach = faecherManager.get(va.idFach);
					final GostFach bFach = faecherManager.get(vb.idFach);
					if ((aFach != null) && (bFach != null)) {
						if (aFach.sortierung > bFach.sortierung)
							return +1;
						if (aFach.sortierung < bFach.sortierung)
							return -1;
					}
				}
				if (va.halbjahr != vb.halbjahr)
					return va.halbjahr - vb.halbjahr;
				if (va.quartal != vb.quartal)
					return va.quartal - vb.quartal;
				return Long.compare(a.id, b.id);
			};

	private final @NotNull Comparator<GostSchuelerklausur> _compSchuelerklausur =
			(final @NotNull GostSchuelerklausur a, final @NotNull GostSchuelerklausur b) -> {
				final GostFaecherManager faecherManager = _vorgabenManager.getFaecherManagerOrNull();
				final GostKlausurvorgabe aV = vorgabeBySchuelerklausur(a);
				final GostKlausurvorgabe bV = vorgabeBySchuelerklausur(b);
				if (aV.quartal != bV.quartal)
					return aV.quartal - bV.quartal;
				if (aV.kursart.compareTo(bV.kursart) < 0)
					return +1;
				if (aV.kursart.compareTo(bV.kursart) > 0)
					return -1;
				if (faecherManager != null) {
					final GostFach aFach = faecherManager.get(aV.idFach);
					final GostFach bFach = faecherManager.get(bV.idFach);
					if ((aFach != null) && (bFach != null)) {
						if (aFach.sortierung > bFach.sortierung)
							return +1;
						if (aFach.sortierung < bFach.sortierung)
							return -1;
					}
				}
				return Long.compare(a.id, b.id);
			};

	private final @NotNull Comparator<GostSchuelerklausurTermin> _compSchuelerklausurTermin =
			(final @NotNull GostSchuelerklausurTermin a, final @NotNull GostSchuelerklausurTermin b) -> {
				if (a.idSchuelerklausur == b.idSchuelerklausur) {
					return Integer.compare(a.folgeNr, b.folgeNr);
				}
				final @NotNull GostSchuelerklausur kA = schuelerklausurBySchuelerklausurtermin(a);
				final @NotNull GostSchuelerklausur kB = schuelerklausurBySchuelerklausurtermin(b);
				if (_schuelerMap != null && kA.idSchueler != kB.idSchueler) {
					final SchuelerListeEintrag sA = getSchuelerMap().get(kA.idSchueler);
					final SchuelerListeEintrag sB = getSchuelerMap().get(kB.idSchueler);
					if (sA != null && sB != null)
						return (sA.nachname + sA.vorname).compareTo(sB.nachname + sB.vorname);
				}
				return Long.compare(a.id, b.id);
			};

	// GostKursklausur
	private final @NotNull Map<Long, GostKursklausur> _kursklausur_by_id = new HashMap<>();
	private final @NotNull List<GostKursklausur> _kursklausurmenge = new ArrayList<>();
	private final @NotNull HashMap3D<Integer, Integer, Integer, List<GostKursklausur>> _kursklausurmenge_by_halbjahr_and_quartal = new HashMap3D<>();
	private final @NotNull Map<Long, List<GostKursklausur>> _kursklausurmenge_by_idTermin = new HashMap<>();
	private final @NotNull Map<Long, List<GostKursklausur>> _kursklausurmenge_by_idVorgabe = new HashMap<>();
	private final @NotNull HashMap4D<Integer, Integer, Long, Integer, List<GostKursklausur>> _kursklausurmenge_by_abijahr_and_halbjahr_and_idTermin_and_quartal =
			new HashMap4D<>();
	private final @NotNull HashMap4D<Long, Integer, Integer, Integer, GostKursklausur> _kursklausur_by_idKurs_and_abijahr_and_halbjahr_and_quartal =
			new HashMap4D<>();
	private final @NotNull HashMap2D<Integer, Long, List<GostKursklausur>> _kursklausurmenge_by_kw_and_schuelerId = new HashMap2D<>();
	private final @NotNull HashMap2D<Long, Long, List<GostKursklausur>> _kursklausurmenge_by_terminId_and_schuelerId = new HashMap2D<>();

	// GostKlausurtermin
	private final @NotNull Map<Long, GostKlausurtermin> _termin_by_id = new HashMap<>();
	private final @NotNull List<GostKlausurtermin> _terminmenge = new ArrayList<>();
	private final @NotNull HashMap3D<Integer, Integer, Integer, List<GostKlausurtermin>> _terminmenge_by_abijahr_and_halbjahr_and_quartal = new HashMap3D<>();
	private final @NotNull HashMap2D<String, Integer, List<GostKlausurtermin>> _terminmenge_by_datum_and_abijahr = new HashMap2D<>();

	// GostSchuelerklausur
	private final @NotNull Map<Long, GostSchuelerklausur> _schuelerklausur_by_id = new HashMap<>();
	private final @NotNull List<GostSchuelerklausur> _schuelerklausurmenge = new ArrayList<>();
	private final @NotNull Map<Long, List<GostSchuelerklausur>> _schuelerklausurmenge_by_idKursklausur = new HashMap<>();

	// GostSchuelerklausurTermin
	private final @NotNull Map<Long, GostSchuelerklausurTermin> _schuelerklausurtermin_by_id = new HashMap<>();
	private final @NotNull List<GostSchuelerklausurTermin> _schuelerklausurterminmenge = new ArrayList<>();
	private final @NotNull Map<Long, List<GostSchuelerklausurTermin>> _schuelerklausurterminmenge_by_idSchuelerklausur = new HashMap<>();
	private final @NotNull Map<Long, List<GostSchuelerklausurTermin>> _schuelerklausurterminmenge_by_idTermin = new HashMap<>();
	private final @NotNull HashMap2D<Long, Long, List<GostSchuelerklausurTermin>> _schuelerklausurterminaktuellmenge_by_idTermin_and_idKursklausur =
			new HashMap2D<>();
	private final @NotNull HashMap4D<Integer, Integer, Integer, Long, List<GostSchuelerklausurTermin>> _schuelerklausurterminntaktuellmenge_by_abijahr_and_halbjahr_and_quartal_and_idTermin =
			new HashMap4D<>(); // ggf.
	// neuer
	// Manager

	/**
	 * Erstellt einen leeren Manager.
	 */
	public GostKursklausurManager() {
		_vorgabenManager = new GostKlausurvorgabenManager();
	}

	/**
	 * Erstellt einen neuen Manager mit den als Liste angegebenen GostKursklausuren
	 * und Klausurterminen und erzeugt die privaten Attribute.
	 *
	 * @param vorgabenManager            der Klausurvorgaben-Manager
	 * @param listKlausuren              die Liste der GostKursklausuren eines
	 *                                   Abiturjahrgangs
	 * @param listTermine                die Liste der GostKlausurtermine eines
	 *                                   Abiturjahrgangs
	 * @param listSchuelerklausuren      die Liste der GostSchuelerklausuren eines
	 *                                   Abiturjahrgangs
	 * @param listSchuelerklausurtermine die Liste der GostSchuelerklausurTermine
	 *                                   eines Abiturjahrgangs
	 */
	public GostKursklausurManager(final @NotNull GostKlausurvorgabenManager vorgabenManager,
			final @NotNull List<GostKursklausur> listKlausuren,
			final List<GostKlausurtermin> listTermine,
			final List<GostSchuelerklausur> listSchuelerklausuren,
			final List<GostSchuelerklausurTermin> listSchuelerklausurtermine) {
		_vorgabenManager = vorgabenManager;
		initAll(listKlausuren, listTermine, listSchuelerklausuren, listSchuelerklausurtermine);
	}

	private void initAll(final @NotNull List<GostKursklausur> listKlausuren,
			final List<GostKlausurtermin> listTermine,
			final List<GostSchuelerklausur> listSchuelerklausuren,
			final List<GostSchuelerklausurTermin> listSchuelerklausurtermine) {
		kursklausurAddAllOhneUpdate(listKlausuren);
		if (listTermine != null)
			terminAddAllOhneUpdate(listTermine);
		if (listSchuelerklausuren != null)
			schuelerklausurAddAllOhneUpdate(listSchuelerklausuren);
		if (listSchuelerklausurtermine != null)
			schuelerklausurterminAddAllOhneUpdate(listSchuelerklausurtermine);

		update_all();

	}

	/**
	 * Setzt den KursManager
	 *
	 * @param kursManager der KursManager
	 */
	public void setKursManager(final @NotNull KursManager kursManager) {
		_kursManager = kursManager;
	}

	/**
	 * Liefert den KursManager, falls dieser gesetzt ist, sonst wird eine
	 * DeveloperNotificationException geworfen.
	 *
	 * @return den KursManager
	 */
	public @NotNull KursManager getKursManager() {
		if (_kursManager == null)
			throw new DeveloperNotificationException("KursManager not set.");
		return _kursManager;
	}

	/**
	 * Liefert den GostFaecherManager, falls dieser gesetzt ist, sonst wird eine
	 * DeveloperNotificationException geworfen.
	 *
	 * @return den GostFaecherManager
	 */
	public @NotNull GostFaecherManager getFaecherManager() {
		return _vorgabenManager.getFaecherManager();
	}

	/**
	 * Setzt die LehrerMap
	 *
	 * @param lehrerMap die LehrerMap
	 */
	public void setLehrerMap(final @NotNull Map<Long, LehrerListeEintrag> lehrerMap) {
		_lehrerMap = lehrerMap;
	}

	/**
	 * Liefert die LehrerMap, falls diese gesetzt ist, sonst wird eine
	 * DeveloperNotificationException geworfen.
	 *
	 * @return die LehrerMap
	 */
	public @NotNull Map<Long, LehrerListeEintrag> getLehrerMap() {
		if (_lehrerMap == null)
			throw new DeveloperNotificationException("LehrerMap not set.");
		return _lehrerMap;
	}

	/**
	 * Liefert die SchuelerMap, falls diese gesetzt ist, sonst wird eine
	 * DeveloperNotificationException geworfen.
	 *
	 * @return die SchuelerMap
	 */
	public @NotNull Map<Long, SchuelerListeEintrag> getSchuelerMap() {
		if (_schuelerMap == null)
			throw new DeveloperNotificationException("SchuelerMap not set.");
		return _schuelerMap;
	}

	/**
	 * Setzt die SchuelerMap
	 *
	 * @param schuelerMap die SchuelerMap
	 */
	public void setSchuelerMap(final @NotNull Map<Long, SchuelerListeEintrag> schuelerMap) {
		_schuelerMap = schuelerMap;
	}

	private void update_all() {

		update_kursklausurmenge();
		update_terminmenge();
		update_schuelerklausurmenge();
		update_schuelerklausurterminmenge();

		update_kursklausurmenge_by_halbjahr_and_quartal();
		update_kursklausurmenge_by_idTermin();
		update_kursklausurmenge_by_idVorgabe();

		update_schuelerklausurmenge_by_idKursklausur();
		update_schuelerklausurterminmenge_by_idSchuelerklausur();
		update_schuelerklausurterminmenge_by_idTermin();
		update_schuelerklausurterminaktuellmenge_by_idTermin_and_idKursklausur();

		update_kursklausurmenge_by_halbjahr_and_quartal_and_idTermin();
		update_kursklausur_by_idKurs_and_halbjahr_and_quartal();
		update_terminmenge_by_halbjahr_and_quartal();
		update_terminmenge_by_datum();
		update_kursklausurmenge_by_terminId_and_schuelerId();

//		update_schuelerIds_by_idTermin(); // benötigt _kursklausurmenge_by_idTermin
		update_kursklausurmenge_by_kw_and_schuelerId(); // benötigt _kursklausurmenge_by_idTermin

		update_schuelerklausurterminntaktuellmenge_by_halbjahr_and_idTermin_and_quartal(); // benötigt
		// _schuelerklausurterminmenge_by_idSchuelerklausur
	}

	private void update_kursklausurmenge_by_halbjahr_and_quartal() {
		_kursklausurmenge_by_halbjahr_and_quartal.clear();
		for (final @NotNull GostKursklausur kk : _kursklausurmenge) {
			final @NotNull GostKlausurvorgabe v = vorgabeByKursklausur(kk);
			Map3DUtils.getOrCreateArrayList(_kursklausurmenge_by_halbjahr_and_quartal, v.abiJahrgang, v.halbjahr,
					v.quartal).add(kk);
		}
	}

	private void update_kursklausurmenge_by_idTermin() {
		_kursklausurmenge_by_idTermin.clear();
		for (final @NotNull GostKursklausur kk : _kursklausurmenge)
			MapUtils.getOrCreateArrayList(_kursklausurmenge_by_idTermin, (kk.idTermin != null) ? kk.idTermin : -1)
					.add(kk);
	}

	private void update_kursklausurmenge_by_idVorgabe() {
		_kursklausurmenge_by_idVorgabe.clear();
		for (final @NotNull GostKursklausur kk : _kursklausurmenge)
			MapUtils.getOrCreateArrayList(_kursklausurmenge_by_idVorgabe, kk.idVorgabe).add(kk);
	}

	private void update_kursklausurmenge_by_halbjahr_and_quartal_and_idTermin() {
		_kursklausurmenge_by_abijahr_and_halbjahr_and_idTermin_and_quartal.clear();
		for (final @NotNull GostKursklausur kk : _kursklausurmenge) {
			final @NotNull GostKlausurvorgabe v = vorgabeByKursklausur(kk);
			Map4DUtils.getOrCreateArrayList(_kursklausurmenge_by_abijahr_and_halbjahr_and_idTermin_and_quartal,
					v.abiJahrgang, v.halbjahr, (kk.idTermin != null) ? kk.idTermin : -1, v.quartal).add(kk);
		}
	}

	private void update_kursklausur_by_idKurs_and_halbjahr_and_quartal() {
		_kursklausur_by_idKurs_and_abijahr_and_halbjahr_and_quartal.clear();
		for (final @NotNull GostKursklausur kk : _kursklausurmenge) {
			final @NotNull GostKlausurvorgabe v = vorgabeByKursklausur(kk);
			_kursklausur_by_idKurs_and_abijahr_and_halbjahr_and_quartal.put(kk.idKurs, v.abiJahrgang, v.halbjahr,
					v.quartal, kk);
		}
	}

	private void update_terminmenge_by_halbjahr_and_quartal() {
		_terminmenge_by_abijahr_and_halbjahr_and_quartal.clear();
		for (final @NotNull GostKlausurtermin t : _terminmenge)
			Map3DUtils.getOrCreateArrayList(_terminmenge_by_abijahr_and_halbjahr_and_quartal, t.abijahr, t.halbjahr,
					t.quartal).add(t);
	}

	private void update_terminmenge_by_datum() {
		_terminmenge_by_datum_and_abijahr.clear();
		for (final @NotNull GostKlausurtermin t : _terminmenge)
			Map2DUtils.getOrCreateArrayList(_terminmenge_by_datum_and_abijahr, t.datum, t.abijahr).add(t);
	}

	private void update_schuelerklausurmenge_by_idKursklausur() {
		_schuelerklausurmenge_by_idKursklausur.clear();
		for (final @NotNull GostSchuelerklausur sk : _schuelerklausurmenge) {
			MapUtils.getOrCreateArrayList(_schuelerklausurmenge_by_idKursklausur, sk.idKursklausur).add(sk);
		}
	}

	private void update_kursklausurmenge_by_kw_and_schuelerId() {
		_kursklausurmenge_by_kw_and_schuelerId.clear();
		for (final @NotNull GostKlausurtermin t : _terminmenge) {
			if (t.datum == null)
				continue;
			final int kw = DateUtils.gibKwDesDatumsISO8601(t.datum);
			final List<GostKursklausur> klausuren = _kursklausurmenge_by_idTermin.get(t.id);
			if (klausuren != null)
				for (final @NotNull GostKursklausur kk : klausuren) {
					for (final @NotNull GostSchuelerklausur sk : schuelerklausurGetMengeByKursklausurid(kk.id))
						Map2DUtils.getOrCreateArrayList(_kursklausurmenge_by_kw_and_schuelerId, kw, sk.idSchueler)
								.add(kk);
				}
		}
	}

	private void update_kursklausurmenge_by_terminId_and_schuelerId() {
		_kursklausurmenge_by_terminId_and_schuelerId.clear();
		for (final @NotNull GostKursklausur kk : _kursklausurmenge) {
			for (final @NotNull GostSchuelerklausur sk : schuelerklausurGetMengeByKursklausurid(kk.id))
				Map2DUtils.getOrCreateArrayList(_kursklausurmenge_by_terminId_and_schuelerId, kk.idTermin, sk.idSchueler).add(kk);
		}
	}

	private void update_schuelerklausurterminmenge_by_idSchuelerklausur() {
		_schuelerklausurterminmenge_by_idSchuelerklausur.clear();
		for (final @NotNull GostSchuelerklausurTermin skt : _schuelerklausurterminmenge)
			MapUtils.getOrCreateArrayList(_schuelerklausurterminmenge_by_idSchuelerklausur, skt.idSchuelerklausur).add(skt);
		for (final @NotNull List<GostSchuelerklausurTermin> sktList : _schuelerklausurterminmenge_by_idSchuelerklausur.values())
			sktList.sort(_compSchuelerklausurTermin);
	}

	private void update_schuelerklausurterminmenge_by_idTermin() {
		_schuelerklausurterminmenge_by_idTermin.clear();
		for (final @NotNull GostSchuelerklausurTermin skt : _schuelerklausurterminmenge) {
			if (skt.folgeNr == 0)
				MapUtils.getOrCreateArrayList(_schuelerklausurterminmenge_by_idTermin, kursklausurBySchuelerklausurTermin(skt).idTermin).add(skt);
			else
				MapUtils.getOrCreateArrayList(_schuelerklausurterminmenge_by_idTermin, skt.idTermin).add(skt);
		}
	}

	private void update_schuelerklausurterminaktuellmenge_by_idTermin_and_idKursklausur() {
		_schuelerklausurterminaktuellmenge_by_idTermin_and_idKursklausur.clear();
		for (final @NotNull Entry<Long, List<GostSchuelerklausurTermin>> e : _schuelerklausurterminmenge_by_idTermin.entrySet())
			for (final @NotNull GostSchuelerklausurTermin skt : e.getValue())
				if (istAktuellerSchuelerklausurtermin(skt))
					Map2DUtils.getOrCreateArrayList(_schuelerklausurterminaktuellmenge_by_idTermin_and_idKursklausur, e.getKey(),
							schuelerklausurBySchuelerklausurtermin(skt).idKursklausur).add(skt);
	}

	private void update_schuelerklausurterminntaktuellmenge_by_halbjahr_and_idTermin_and_quartal() { // ggf. neuer
		// Manager
		_schuelerklausurterminntaktuellmenge_by_abijahr_and_halbjahr_and_quartal_and_idTermin.clear();
		for (final @NotNull GostSchuelerklausur sk : _schuelerklausurmenge) {
			if (_schuelerklausurterminmenge_by_idSchuelerklausur.containsKey(sk.id)) {
				final GostSchuelerklausurTermin sktLast = schuelerklausurterminaktuellBySchuelerklausur(sk.id);
				if (sktLast.folgeNr > 0) {
					final GostKlausurvorgabe v = vorgabeBySchuelerklausurTermin(sktLast);
					Map4DUtils.getOrCreateArrayList(
							_schuelerklausurterminntaktuellmenge_by_abijahr_and_halbjahr_and_quartal_and_idTermin,
							v.abiJahrgang, v.halbjahr, v.quartal, (sktLast.idTermin != null) ? sktLast.idTermin : -1)
							.add(sktLast);
				}
			}
		}
	}

	// #####################################################################
	// #################### GostKursklausur ################################
	// #####################################################################

	private void update_kursklausurmenge() {
		_kursklausurmenge.clear();
		_kursklausurmenge.addAll(_kursklausur_by_id.values());
		_kursklausurmenge.sort(_compKursklausur);
	}

	/**
	 * Fügt ein {@link GostKursklausur}-Objekt hinzu.
	 *
	 * @param kursklausur Das {@link GostKursklausur}-Objekt, welches hinzugefügt
	 *                    werden soll.
	 */
	public void kursklausurAdd(final @NotNull GostKursklausur kursklausur) {
		kursklausurAddAll(ListUtils.create1(kursklausur));
		update_all();
	}

	private void kursklausurAddAllOhneUpdate(final @NotNull List<GostKursklausur> list) {
		// check all
		final @NotNull HashSet<Long> setOfIDs = new HashSet<>();
		for (final @NotNull GostKursklausur klausur : list) {
			kursklausurCheck(klausur);
			DeveloperNotificationException.ifTrue(
					"kursklausurAddAllOhneUpdate: ID=" + klausur.id + " existiert bereits!",
					_kursklausur_by_id.containsKey(klausur.id));
			DeveloperNotificationException.ifTrue(
					"kursklausurAddAllOhneUpdate: ID=" + klausur.id + " doppelt in der Liste!",
					!setOfIDs.add(klausur.id));
		}

		// add all
		for (final @NotNull GostKursklausur klausur : list)
			DeveloperNotificationException.ifMapPutOverwrites(_kursklausur_by_id, klausur.id, klausur);
	}

	/**
	 * Fügt alle {@link GostKursklausur}-Objekte hinzu.
	 *
	 * @param listKursklausuren Die Menge der {@link GostKursklausur}-Objekte,
	 *                          welche hinzugefügt werden soll.
	 */
	public void kursklausurAddAll(final @NotNull List<GostKursklausur> listKursklausuren) {
		kursklausurAddAllOhneUpdate(listKursklausuren);
		update_all();
	}

	private static void kursklausurCheck(final @NotNull GostKursklausur kursklausur) {
		DeveloperNotificationException.ifInvalidID("kursklausur.id", kursklausur.id);
	}

	/**
	 * Liefert das zur ID zugehörige {@link GostKursklausur}-Objekt. <br>
	 * Laufzeit: O(1)
	 *
	 * @param idKursklausur Die ID des angefragten-Objektes.
	 *
	 * @return das zur ID zugehörige {@link GostKursklausur}-Objekt.
	 */
	public @NotNull GostKursklausur kursklausurGetByIdOrException(final long idKursklausur) {
		return DeveloperNotificationException.ifMapGetIsNull(_kursklausur_by_id, idKursklausur);
	}

	/**
	 * Liefert eine Liste aller {@link GostKursklausur}-Objekte. <br>
	 * Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link GostKursklausur}-Objekte.
	 */
	public @NotNull List<GostKursklausur> kursklausurGetMengeAsList() {
		return _kursklausurmenge;
	}

	/**
	 * Aktualisiert das vorhandene {@link GostKursklausur}-Objekt durch das neue
	 * Objekt.
	 *
	 * @param kursklausur Das neue {@link GostKursklausur}-Objekt.
	 */
	public void kursklausurPatchAttributes(final @NotNull GostKursklausur kursklausur) {
		kursklausurPatchAttributesOhneUpdate(kursklausur);
		update_all();
	}

	/**
	 * Aktualisiert das vorhandene {@link GostKursklausur}-Objekt durch das neue
	 * Objekt.
	 *
	 * @param kursklausur Das neue {@link GostKursklausur}-Objekt.
	 */
	public void kursklausurPatchAttributesOhneUpdate(final @NotNull GostKursklausur kursklausur) {
		kursklausurCheck(kursklausur);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_kursklausur_by_id, kursklausur.id);
		DeveloperNotificationException.ifMapPutOverwrites(_kursklausur_by_id, kursklausur.id, kursklausur);
	}

	/**
	 * Aktualisiert das vorhandene {@link GostKursklausur}-Objekt durch das neue
	 * Objekt.
	 *
	 * @param kursklausurMenge Das neue {@link GostKursklausur}-Objekt.
	 */
	public void kursklausurMengePatchAttributes(final @NotNull List<GostKursklausur> kursklausurMenge) {

		for (@NotNull final GostKursklausur kursklausur : kursklausurMenge)
			kursklausurPatchAttributesOhneUpdate(kursklausur);

		update_all();
	}

	private void kursklausurRemoveOhneUpdateById(final long idKursklausur) {
		DeveloperNotificationException.ifMapRemoveFailes(_kursklausur_by_id, idKursklausur);
	}

	/**
	 * Entfernt ein existierendes {@link GostKursklausur}-Objekt.
	 *
	 * @param idKursklausur Die ID des {@link GostKursklausur}-Objekts.
	 */
	public void kursklausurRemoveById(final long idKursklausur) {
		kursklausurRemoveOhneUpdateById(idKursklausur);

		update_all();
	}

	/**
	 * Entfernt alle {@link GostKursklausur}-Objekte.
	 *
	 * @param listKursklausuren Die Liste der zu entfernenden
	 *                          {@link GostKursklausur}-Objekte.
	 */
	public void kursklausurRemoveAll(final @NotNull List<GostKursklausur> listKursklausuren) {
		for (final @NotNull GostKursklausur kursklausur : listKursklausuren)
			kursklausurRemoveOhneUpdateById(kursklausur.id);

		update_all();
	}

	// #####################################################################
	// #################### GostKlausurtermin ################################
	// #####################################################################

	private void update_terminmenge() {
		_terminmenge.clear();
		_terminmenge.addAll(_termin_by_id.values());
		_terminmenge.sort(_compTermin);
	}

	/**
	 * Fügt ein {@link GostKlausurtermin}-Objekt hinzu.
	 *
	 * @param termin Das {@link GostKlausurtermin}-Objekt, welches hinzugefügt
	 *               werden soll.
	 */
	public void terminAdd(final @NotNull GostKlausurtermin termin) {
		terminAddAll(ListUtils.create1(termin));
	}

	private void terminAddAllOhneUpdate(final @NotNull List<GostKlausurtermin> list) {
		// check all
		final @NotNull HashSet<Long> setOfIDs = new HashSet<>();
		for (final @NotNull GostKlausurtermin termin : list) {
			terminCheck(termin);
			DeveloperNotificationException.ifTrue("terminAddAllOhneUpdate: ID=" + termin.id + " existiert bereits!",
					_termin_by_id.containsKey(termin.id));
			DeveloperNotificationException.ifTrue("terminAddAllOhneUpdate: ID=" + termin.id + " doppelt in der Liste!",
					!setOfIDs.add(termin.id));
		}

		// add all
		for (final @NotNull GostKlausurtermin termin : list)
			DeveloperNotificationException.ifMapPutOverwrites(_termin_by_id, termin.id, termin);
	}

	/**
	 * Fügt alle {@link GostKlausurtermin}-Objekte hinzu.
	 *
	 * @param listTermine Die Menge der {@link GostKlausurtermin}-Objekte, welche
	 *                    hinzugefügt werden soll.
	 */
	public void terminAddAll(final @NotNull List<GostKlausurtermin> listTermine) {
		terminAddAllOhneUpdate(listTermine);
		update_all();
	}

	private static void terminCheck(final @NotNull GostKlausurtermin termin) {
		DeveloperNotificationException.ifInvalidID("termin.id", termin.id);
	}

	/**
	 * Liefert das zur ID zugehörige {@link GostKlausurtermin}-Objekt. <br>
	 * Laufzeit: O(1)
	 *
	 * @param idTermin Die ID des angefragten-Objektes.
	 *
	 * @return das zur ID zugehörige {@link GostKlausurtermin}-Objekt.
	 */
	public @NotNull GostKlausurtermin terminGetByIdOrException(final long idTermin) {
		return DeveloperNotificationException.ifMapGetIsNull(_termin_by_id, idTermin);
	}

	/**
	 * Liefert das zur ID zugehörige {@link GostKlausurtermin}-Objekt oder null.
	 * <br>
	 * Laufzeit: O(1)
	 *
	 * @param idTermin Die ID des angefragten-Objektes.
	 *
	 * @return das zur ID zugehörige {@link GostKlausurtermin}-Objekt oder null.
	 */
	public GostKlausurtermin terminGetByIdOrNull(final long idTermin) {
		return _termin_by_id.get(idTermin);
	}

	/**
	 * Liefert eine Liste aller {@link GostKlausurtermin}-Objekte. <br>
	 * Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link GostKlausurtermin}-Objekte.
	 */
	public @NotNull List<GostKlausurtermin> terminGetMengeAsList() {
		return _terminmenge;
	}

	/**
	 * Aktualisiert das vorhandene {@link GostKlausurtermin}-Objekt durch das neue
	 * Objekt.
	 *
	 * @param termin Das neue {@link GostKlausurtermin}-Objekt.
	 */
	public void terminPatchAttributes(final @NotNull GostKlausurtermin termin) {
		terminCheck(termin);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_termin_by_id, termin.id);
		DeveloperNotificationException.ifMapPutOverwrites(_termin_by_id, termin.id, termin);

		update_all();
	}

	private void terminRemoveOhneUpdateById(final long idTermin) {
		DeveloperNotificationException.ifMapRemoveFailes(_termin_by_id, idTermin);
		final List<GostKursklausur> kursklausurenZuTermin = _kursklausurmenge_by_idTermin.get(idTermin);
		if (kursklausurenZuTermin != null)
			for (final @NotNull GostKursklausur k : kursklausurenZuTermin)
				k.idTermin = null;
		final List<GostSchuelerklausurTermin> schuelerklausurtermineZuTermin = _schuelerklausurterminmenge_by_idTermin.get(idTermin);
		if (schuelerklausurtermineZuTermin != null)
			for (final @NotNull GostSchuelerklausurTermin skt : schuelerklausurtermineZuTermin)
				skt.idTermin = null;
	}

	/**
	 * Entfernt ein existierendes {@link GostKlausurtermin}-Objekt.
	 *
	 * @param idTermin Die ID des {@link GostKlausurtermin}-Objekts.
	 */
	public void terminRemoveById(final long idTermin) {
		terminRemoveOhneUpdateById(idTermin);

		update_all();
	}

	/**
	 * Entfernt alle {@link GostKlausurtermin}-Objekte.
	 *
	 * @param listTermine Die Liste der zu entfernenden
	 *                    {@link GostKlausurtermin}-Objekte.
	 */
	public void terminRemoveAll(final @NotNull List<GostKlausurtermin> listTermine) {
		for (final @NotNull GostKlausurtermin termin : listTermine)
			terminRemoveOhneUpdateById(termin.id);

		update_all();
	}

	// #####################################################################
	// #################### GostSchuelerklausur ################################
	// #####################################################################

	private void update_schuelerklausurmenge() {
		_schuelerklausurmenge.clear();
		_schuelerklausurmenge.addAll(_schuelerklausur_by_id.values());
		_schuelerklausurmenge.sort(_compSchuelerklausur);
	}

	/**
	 * Fügt ein {@link GostSchuelerklausur}-Objekt hinzu.
	 *
	 * @param kursklausur Das {@link GostSchuelerklausur}-Objekt, welches
	 *                    hinzugefügt werden soll.
	 */
	public void schuelerklausurAdd(final @NotNull GostSchuelerklausur kursklausur) {
		schuelerklausurAddAll(ListUtils.create1(kursklausur));
		update_all();
	}

	private void schuelerklausurAddAllOhneUpdate(final @NotNull List<GostSchuelerklausur> list) {
		// check all
		final @NotNull HashSet<Long> setOfIDs = new HashSet<>();
		for (final @NotNull GostSchuelerklausur klausur : list) {
			schuelerklausurCheck(klausur);
			DeveloperNotificationException.ifTrue("schuelerklausurAddAllOhneUpdate: ID=" + klausur.id + " existiert bereits!",
					_schuelerklausur_by_id.containsKey(klausur.id));
			DeveloperNotificationException.ifTrue("schuelerklausurAddAllOhneUpdate: ID=" + klausur.id + " doppelt in der Liste!",
					!setOfIDs.add(klausur.id));
		}

		// add all
		for (final @NotNull GostSchuelerklausur klausur : list)
			DeveloperNotificationException.ifMapPutOverwrites(_schuelerklausur_by_id, klausur.id, klausur);
	}

	/**
	 * Fügt alle {@link GostKursklausur}-Objekte hinzu.
	 *
	 * @param listKursklausuren Die Menge der {@link GostKursklausur}-Objekte,
	 *                          welche hinzugefügt werden soll.
	 */
	public void schuelerklausurAddAll(final @NotNull List<GostSchuelerklausur> listKursklausuren) {
		schuelerklausurAddAllOhneUpdate(listKursklausuren);
		update_all();
	}

	private static void schuelerklausurCheck(final @NotNull GostSchuelerklausur kursklausur) {
		DeveloperNotificationException.ifInvalidID("schuelerklausur.idSchuelerklausur", kursklausur.id);
	}

	/**
	 * Liefert das zur ID zugehörige {@link GostKursklausur}-Objekt. <br>
	 * Laufzeit: O(1)
	 *
	 * @param idKursklausur Die ID des angefragten-Objektes.
	 *
	 * @return das zur ID zugehörige {@link GostKursklausur}-Objekt.
	 */
	public @NotNull GostSchuelerklausur schuelerklausurGetByIdOrException(final long idKursklausur) {
		return DeveloperNotificationException.ifMapGetIsNull(_schuelerklausur_by_id, idKursklausur);
	}

	/**
	 * Liefert eine Liste aller {@link GostKursklausur}-Objekte. <br>
	 * Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link GostKursklausur}-Objekte.
	 */
	public @NotNull List<GostSchuelerklausur> schuelerklausurGetMengeAsList() {
		return new ArrayList<>(_schuelerklausurmenge);
	}

	/**
	 * Aktualisiert das vorhandene {@link GostKursklausur}-Objekt durch das neue
	 * Objekt.
	 *
	 * @param kursklausur Das neue {@link GostKursklausur}-Objekt.
	 */
	public void schuelerklausurPatchAttributes(final @NotNull GostSchuelerklausur kursklausur) {
		schuelerklausurCheck(kursklausur);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_schuelerklausur_by_id, kursklausur.id);
		DeveloperNotificationException.ifMapPutOverwrites(_schuelerklausur_by_id, kursklausur.id, kursklausur);

		update_all();
	}

	private void schuelerklausurRemoveOhneUpdateById(final long idKursklausur) {
		DeveloperNotificationException.ifMapRemoveFailes(_schuelerklausur_by_id, idKursklausur);
	}

	/**
	 * Entfernt ein existierendes {@link GostKursklausur}-Objekt.
	 *
	 * @param idKursklausur Die ID des {@link GostKursklausur}-Objekts.
	 */
	public void schuelerklausurRemoveById(final long idKursklausur) {
		schuelerklausurRemoveOhneUpdateById(idKursklausur);

		update_all();
	}

	/**
	 * Entfernt alle {@link GostKursklausur}-Objekte.
	 *
	 * @param listKursklausuren Die Liste der zu entfernenden
	 *                          {@link GostKursklausur}-Objekte.
	 */
	public void schuelerklausurRemoveAll(final @NotNull List<GostSchuelerklausur> listKursklausuren) {
		for (final @NotNull GostSchuelerklausur kursklausur : listKursklausuren)
			schuelerklausurRemoveOhneUpdateById(kursklausur.id);

		update_all();
	}

	// #####################################################################
	// #################### GostSchuelerklausurTermin
	// ################################
	// #####################################################################

	private void update_schuelerklausurterminmenge() {
		_schuelerklausurterminmenge.clear();
		_schuelerklausurterminmenge.addAll(_schuelerklausurtermin_by_id.values());
		_schuelerklausurterminmenge.sort(_compSchuelerklausurTermin);
	}

	/**
	 * Fügt ein {@link GostSchuelerklausurTermin}-Objekt hinzu.
	 *
	 * @param schuelerklausurtermin Das {@link GostSchuelerklausurTermin}-Objekt,
	 *                              welches hinzugefügt werden soll.
	 */
	public void schuelerklausurterminAdd(final @NotNull GostSchuelerklausurTermin schuelerklausurtermin) {
		schuelerklausurterminAddAll(ListUtils.create1(schuelerklausurtermin));
		update_all();
	}

	private void schuelerklausurterminAddAllOhneUpdate(final @NotNull List<GostSchuelerklausurTermin> list) {
		// check all
		final @NotNull HashSet<Long> setOfIDs = new HashSet<>();
		for (final @NotNull GostSchuelerklausurTermin schuelerklausurtermin : list) {
			schuelerklausurterminCheck(schuelerklausurtermin);
			DeveloperNotificationException.ifTrue(
					"schuelerklausurterminAddAllOhneUpdate: ID=" + schuelerklausurtermin.id + " existiert bereits!",
					_schuelerklausurtermin_by_id.containsKey(schuelerklausurtermin.id));
			DeveloperNotificationException.ifTrue(
					"schuelerklausurterminAddAllOhneUpdate: ID=" + schuelerklausurtermin.id + " doppelt in der Liste!",
					!setOfIDs.add(schuelerklausurtermin.id));
		}

		// add all
		for (final @NotNull GostSchuelerklausurTermin schuelerklausurtermin : list)
			DeveloperNotificationException.ifMapPutOverwrites(_schuelerklausurtermin_by_id, schuelerklausurtermin.id,
					schuelerklausurtermin);
	}

	/**
	 * Fügt alle {@link GostSchuelerklausurTermin}-Objekte hinzu.
	 *
	 * @param listSchuelerklausurtermine   die Menge der {@link GostSchuelerklausurTermin}-Objekte, welche hinzugefügt werden sollen.
	 */
	public void schuelerklausurterminAddAll(final @NotNull List<GostSchuelerklausurTermin> listSchuelerklausurtermine) {
		schuelerklausurterminAddAllOhneUpdate(listSchuelerklausurtermine);
		update_all();
	}

	private static void schuelerklausurterminCheck(final @NotNull GostSchuelerklausurTermin schuelerklausurtermin) {
		DeveloperNotificationException.ifInvalidID("schuelerschuelerklausurtermin.idSchuelerschuelerklausurtermin", schuelerklausurtermin.id);
	}

	/**
	 * Liefert das zur ID zugehörige {@link GostSchuelerklausurTermin}-Objekt. <br>
	 * Laufzeit: O(1)
	 *
	 * @param idSchuelerklausurtermin Die ID des angefragten-Objektes.
	 *
	 * @return das zur ID zugehörige {@link GostSchuelerklausurTermin}-Objekt.
	 */
	public @NotNull GostSchuelerklausurTermin schuelerklausurterminGetByIdOrException(final long idSchuelerklausurtermin) {
		return DeveloperNotificationException.ifMapGetIsNull(_schuelerklausurtermin_by_id, idSchuelerklausurtermin);
	}

	/**
	 * Liefert eine Liste aller {@link GostSchuelerklausurTermin}-Objekte. <br>
	 * Laufzeit: O(1)
	 *
	 * @return eine Liste aller {@link GostSchuelerklausurTermin}-Objekte.
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminGetMengeAsList() {
		return new ArrayList<>(_schuelerklausurterminmenge);
	}

	/**
	 * Aktualisiert das vorhandene {@link GostSchuelerklausurTermin}-Objekt durch
	 * das neue Objekt.
	 *
	 * @param schuelerklausurtermin Das neue
	 *                              {@link GostSchuelerklausurTermin}-Objekt.
	 */
	public void schuelerklausurterminPatchAttributes(final @NotNull GostSchuelerklausurTermin schuelerklausurtermin) {
		schuelerklausurterminCheck(schuelerklausurtermin);

		// Altes Objekt durch neues Objekt ersetzen
		DeveloperNotificationException.ifMapRemoveFailes(_schuelerklausurtermin_by_id, schuelerklausurtermin.id);
		DeveloperNotificationException.ifMapPutOverwrites(_schuelerklausurtermin_by_id, schuelerklausurtermin.id,
				schuelerklausurtermin);

		update_all();
	}

	private void schuelerklausurterminRemoveOhneUpdateById(final long idSchuelerklausurtermin) {
		DeveloperNotificationException.ifMapRemoveFailes(_schuelerklausurtermin_by_id, idSchuelerklausurtermin);
	}

	/**
	 * Entfernt ein existierendes {@link GostSchuelerklausurTermin}-Objekt.
	 *
	 * @param idSchuelerklausurtermin   die ID des {@link GostSchuelerklausurTermin}-Objekts.
	 */
	public void schuelerklausurterminRemoveById(final long idSchuelerklausurtermin) {
		schuelerklausurterminRemoveOhneUpdateById(idSchuelerklausurtermin);

		update_all();
	}

	/**
	 * Entfernt alle {@link GostSchuelerklausurTermin}-Objekte.
	 *
	 * @param listSchuelerklausurtermine   die Liste der zu entfernenden {@link GostSchuelerklausurTermin}-Objekte.
	 */
	public void schuelerklausurterminRemoveAll(final @NotNull List<GostSchuelerklausurTermin> listSchuelerklausurtermine) {
		for (final @NotNull GostSchuelerklausurTermin schuelerklausurtermin : listSchuelerklausurtermine)
			schuelerklausurterminRemoveOhneUpdateById(schuelerklausurtermin.id);

		update_all();
	}

	// ################################################################################

	/**
	 * Liefert das GostKursklausur-Objekt zum übergebenen Termin und Kurs
	 *
	 * @param idTermin die ID des Klausurtermins
	 * @param idKurs   die ID des Kurses
	 *
	 * @return das GostKursklausur-Objekt
	 */
	public GostKursklausur kursklausurGetByTerminidAndKursid(final long idTermin, final long idKurs) {
		final List<GostKursklausur> klausuren = kursklausurGetMengeByTerminid(idTermin);
		for (final @NotNull GostKursklausur klaus : klausuren) {
			if (klaus.idKurs == idKurs)
				return klaus;
		}
		return null;
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten zum übergebenen Datum
	 *
	 * @param datum das Datum der Klausurtermine im Format YYYY-MM-DD
	 *
	 * @return die Liste von GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminGetMengeByDatum(final @NotNull String datum) {
		final @NotNull List<GostKlausurtermin> ergebnis = new ArrayList<>();
		if (!_terminmenge_by_datum_and_abijahr.containsKey1(datum))
			return ergebnis;
		for (@NotNull final List<GostKlausurtermin> termine : _terminmenge_by_datum_and_abijahr
				.getNonNullValuesOfKey1AsList(datum))
			ergebnis.addAll(termine);
		return ergebnis;
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten zum übergebenen Datum
	 *
	 * @param datum das Datum der Klausurtermine im Format YYYY-MM-DD
	 *
	 * @return die Liste von GostKlausurtermin-Objekten
	 */
	public @NotNull List<List<GostKlausurtermin>> terminGruppierteUeberschneidungenGetMengeByDatum(final @NotNull String datum) {
		return gruppiereUeberschneidungen(terminGetMengeByDatum(datum));
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten zum übergebenen Datum
	 *
	 * @param datum       das Datum der Klausurtermine im Format YYYY-MM-DD
	 * @param abiJahrgang der Abiturjahrgang
	 *
	 * @return die Liste von GostKlausurtermin-Objekten
	 */
	public @NotNull List<List<GostKlausurtermin>> terminGruppierteUeberschneidungenGetMengeByDatumAndAbijahr(
			final @NotNull String datum, final Integer abiJahrgang) {
		if (abiJahrgang == null)
			return terminGruppierteUeberschneidungenGetMengeByDatum(datum);
		final List<GostKlausurtermin> termine = _terminmenge_by_datum_and_abijahr.getOrNull(datum,
				abiJahrgang);
		return (termine != null) ? gruppiereUeberschneidungen(termine) : new ArrayList<>();
	}

	/*
	 * Q&D: Muss noch hinsichtlich Laufzeit optimiert werden
	 */
	private @NotNull List<List<GostKlausurtermin>> gruppiereUeberschneidungen(final @NotNull List<GostKlausurtermin> termine) {
		final @NotNull List<List<GostKlausurtermin>> ergebnis = new ArrayList<>();
		boolean added = false;
		for (final @NotNull GostKlausurtermin terminToAdd : termine) {
			// Not supported by transpiler outerloop:
			for (final @NotNull List<GostKlausurtermin> listToCheck : ergebnis) {
				for (final @NotNull GostKlausurtermin terminInListe : termine) {
					if (checkTerminUeberschneidung(terminInListe, terminToAdd)) {
						listToCheck.add(terminToAdd);
						// Not supported by transpiler break outerloop;
						added = true;
					}
					if (added)
						break;
				}
				if (added)
					break;
			}
			if (!added)
				ergebnis.add(ListUtils.create1(terminToAdd));
		}
		return ergebnis;
	}


	private boolean checkTerminUeberschneidung(final @NotNull GostKlausurtermin t1, final @NotNull GostKlausurtermin t2) {
		final int s1 = minKursklausurstartzeitByTerminid(t1.id);
		final int s2 = minKursklausurstartzeitByTerminid(t2.id);
		final int e1 = maxKursklausurendzeitByTerminid(t1.id);
		final int e2 = maxKursklausurendzeitByTerminid(t2.id);
		return (e1 >= s2) && (e2 >= s1);
	}


	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten zum übergebenen Datum
	 *
	 * @param datum       das Datum der Klausurtermine im Format YYYY-MM-DD
	 * @param abiJahrgang der Abiturjahrgang
	 *
	 * @return die Liste von GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminGetMengeByDatumAndAbijahr(final @NotNull String datum, final int abiJahrgang) {
		final List<GostKlausurtermin> termine = _terminmenge_by_datum_and_abijahr.getOrNull(datum, abiJahrgang);
		return (termine != null) ? termine : new ArrayList<>();
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten zum übergebenen Datum
	 *
	 * @param datum       das Datum der Klausurtermine im Format YYYY-MM-DD
	 * @param abiJahrgang der Abiturjahrgang
	 *
	 * @return die Liste von GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminGetFremdmengeByDatumAndAbijahr(final @NotNull String datum, final int abiJahrgang) {
		final @NotNull List<GostKlausurtermin> termine = new ArrayList<>();
		final Map<Integer, List<GostKlausurtermin>> jgDatumMap = _terminmenge_by_datum_and_abijahr.getSubMapOrNull(datum);
		if (jgDatumMap != null)
			for (final @NotNull Entry<Integer, List<GostKlausurtermin>> entry : jgDatumMap.entrySet())
				if (entry.getKey() != abiJahrgang)
					if (entry.getValue() != null)
						termine.addAll(entry.getValue());
		return termine;
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten zum übergebenen Datum
	 *
	 * @param datum       das Datum der Klausurtermine
	 * @param abiJahrgang der Abiturjahrgang
	 * @param zr          Zeitraster
	 * @param manager     der StundenplanManager
	 *
	 * @return die Liste von GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminGetMengeByDatumAndAbijahrAndZeitraster(
			final @NotNull String datum, final int abiJahrgang, final @NotNull StundenplanZeitraster zr,
			final @NotNull StundenplanManager manager) {
		final List<GostKlausurtermin> termine = terminGetMengeByDatumAndAbijahr(datum, abiJahrgang);
		final List<GostKlausurtermin> retList = new ArrayList<>();
		for (final @NotNull GostKlausurtermin termin : termine) {
			final int maxKlausurDauer = maxKlausurdauerGetByTerminid(termin.id);
			final @NotNull List<StundenplanZeitraster> zrsTermin = manager
					.getZeitrasterByWochentagStartVerstrichen(Wochentag.fromIDorException(zr.wochentag),
							DeveloperNotificationException.ifNull("Startzeit des Klausurtermins", termin.startzeit),
							maxKlausurDauer);
			for (final @NotNull StundenplanZeitraster zrTermin : zrsTermin)
				if (zrTermin.id == zr.id)
					retList.add(termin);
		}
		return retList;
	}

	/**
	 * Liefert eine Liste von GostKursklausur-Objekten zum übergebenen Termin
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return die Liste von GostKursklausur-Objekten
	 */
	public @NotNull List<GostKursklausur> kursklausurGetMengeByTerminid(final Long idTermin) {
		final List<GostKursklausur> klausuren = _kursklausurmenge_by_idTermin
				.get((idTermin != null) ? idTermin : -1);
		return (klausuren != null) ? klausuren : new ArrayList<>();
	}

	/**
	 * Liefert eine Liste von GostKursklausur-Objekten zum übergebenen Termin auf
	 * Wunsch mit Kursklausuren der Nachschreiber
	 *
	 * @param idTermin          die ID des Klausurtermins
	 * @param mitNachschreibern wenn true werden die Kursklausuren einzelner
	 *                          Nachschreiber mit inkludiert
	 *
	 * @return die Liste von GostKursklausur-Objekten
	 */
	public @NotNull Set<GostKursklausur> kursklausurMitNachschreibernGetMengeByTerminid(final Long idTermin,
			final boolean mitNachschreibern) {
		final Set<GostKursklausur> klausuren = new HashSet<>(kursklausurGetMengeByTerminid(idTermin));
		if (mitNachschreibern)
			for (final GostSchuelerklausurTermin skt : schuelerklausurterminGetMengeByTerminid(idTermin)) {
				klausuren.add(kursklausurBySchuelerklausurTermin(skt));
			}
		return klausuren;
	}

	/**
	 * Liefert eine Liste von GostKursklausur-Objekten zum übergebenen Quartal
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gosthalbjahr
	 * @param quartal     die Nummer des Quartals
	 *
	 * @return die Liste von GostKursklausur-Objekten
	 */
	public List<GostKursklausur> kursklausurGetMengeByHalbjahrAndQuartal(final int abiJahrgang, final @NotNull GostHalbjahr halbjahr, final int quartal) {
		return _kursklausurmenge_by_halbjahr_and_quartal.getOrNull(abiJahrgang, halbjahr.id, quartal);
	}

	/**
	 * Liefert eine Liste von GostKursklausur-Objekten für die noch kein Termin /
	 * Schiene gesetzt wurde
	 *
	 * @return die Liste von GostKursklausur-Objekten
	 */
	public @NotNull List<GostKursklausur> kursklausurOhneTerminGetMenge() {
		return kursklausurGetMengeByTerminid(-1L);
	}

	/**
	 * Liefert eine Liste von GostKursklausur-Objekten zum übergebenen Quartal für
	 * die noch kein Termin / Schiene gesetzt wurde
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gosthalbjahr
	 * @param quartal     die Nummer des Quartals, 0 für alle Quartale
	 *
	 * @return die Liste von GostKursklausur-Objekten
	 */
	public @NotNull List<GostKursklausur> kursklausurOhneTerminGetMengeByHalbjahrAndQuartal(
			final int abiJahrgang, final @NotNull GostHalbjahr halbjahr, final int quartal) {
		if (quartal > 0) {
			final List<GostKursklausur> klausuren = _kursklausurmenge_by_abijahr_and_halbjahr_and_idTermin_and_quartal
					.getOrNull(abiJahrgang, halbjahr.id, -1L, quartal);
			return (klausuren != null) ? klausuren : new ArrayList<>();
		}
		final List<GostKursklausur> klausuren = new ArrayList<>();
		for (final @NotNull List<GostKursklausur> kl : _kursklausurmenge_by_abijahr_and_halbjahr_and_idTermin_and_quartal
				.getNonNullValuesOfMap4AsList(abiJahrgang, halbjahr.id, -1L)) {
			klausuren.addAll(kl);
		}
		return klausuren;
	}

//	/**
//	 * Gibt eine Liste von Schüler-IDs zurück, die vom übergebenen Termin betroffen
//	 * sind.
//	 *
//	 * @param idTermin die ID des Klausurtermins
//	 *
//	 * @return die Liste der betroffenen Schüler-IDs
//	 */
//	public @NotNull List<Long> schueleridsGetMengeByTerminid(final long idTermin) {
//		final List<Long> schuelerIds = _schuelerIds_by_idTermin.get(idTermin);
//		return schuelerIds != null ? schuelerIds : new ArrayList<>();
//	}

	/**
	 * Liefert eine Liste von allen Paralleljahrgängen in der Oberstufe.
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    die ID des Gost-Halbjahres
	 * @param includeSelf falls true, wir das übergebene Halbjahr in die Rückgabe
	 *                    inkludiert
	 *
	 * @return die Liste von Paaren, die das Abiturjahr und GostHalbjahr beinhalten.
	 */
	public static @NotNull List<PairNN<Integer, GostHalbjahr>> getParallelHalbjahre(
			final int abiJahrgang, final int halbjahr, final boolean includeSelf) {
		final GostHalbjahr hjAktuell = GostHalbjahr.fromIDorException(halbjahr);
		if (hjAktuell == null)
			throw new DeveloperNotificationException("Kein gültiges Gost-Halbjahr");
		final @NotNull List<PairNN<Integer, GostHalbjahr>> ergebnis = new ArrayList<>();
		if (includeSelf)
			ergebnis.add(new PairNN<>(abiJahrgang, hjAktuell));
		if (halbjahr >= 2)
			ergebnis.add(new PairNN<>(abiJahrgang + 1, GostHalbjahr.fromIDorException(halbjahr - 2)));
		if (halbjahr >= 4)
			ergebnis.add(new PairNN<>(abiJahrgang + 2, GostHalbjahr.fromIDorException(halbjahr - 4)));
		if (halbjahr <= 3)
			ergebnis.add(new PairNN<>(abiJahrgang - 1, GostHalbjahr.fromIDorException(halbjahr + 2)));
		if (halbjahr <= 1)
			ergebnis.add(new PairNN<>(abiJahrgang - 2, GostHalbjahr.fromIDorException(halbjahr + 4)));
		return ergebnis;
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten zum übergebenen Quartal
	 *
	 * @param abiJahrgang   der Abitur-Jahrgang
	 * @param halbjahr      das Gost-Halbjahr
	 * @param quartal       die Nummer des Quartals, 0 für alle Quartale
	 * @param multijahrgang wenn true, werden die Klausurtermin-Objekte der
	 *                      Parallel-Jahrgänge eingeschlossen
	 *
	 * @return die Liste von GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminGetMengeByHalbjahrAndQuartalMultijahrgang(
			final int abiJahrgang, final @NotNull GostHalbjahr halbjahr, final int quartal,
			final boolean multijahrgang) {
		if (!multijahrgang)
			return terminGetMengeByHalbjahrAndQuartal(abiJahrgang, halbjahr, quartal);
		final List<GostKlausurtermin> termine = new ArrayList<>();

		for (final @NotNull PairNN<Integer, GostHalbjahr> jgHj : getParallelHalbjahre(abiJahrgang, halbjahr.id, true))
			termine.addAll(terminGetMengeByHalbjahrAndQuartal(jgHj.a, jgHj.b, quartal));

		return termine;
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten zum übergebenen Quartal
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gost-Halbjahr
	 * @param quartal     die Nummer des Quartals, 0 für alle Quartale
	 *
	 * @return die Liste von GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminGetMengeByHalbjahrAndQuartal(final int abiJahrgang,
			final @NotNull GostHalbjahr halbjahr, final int quartal) {
		final List<GostKlausurtermin> termine = new ArrayList<>();
		if (quartal > 0) {
			if (_terminmenge_by_abijahr_and_halbjahr_and_quartal.getOrNull(abiJahrgang, halbjahr.id, quartal) != null)
				termine.addAll(
						_terminmenge_by_abijahr_and_halbjahr_and_quartal.getOrNull(abiJahrgang, halbjahr.id, quartal));
			if (_terminmenge_by_abijahr_and_halbjahr_and_quartal.getOrNull(abiJahrgang, halbjahr.id, 0) != null)
				termine.addAll(_terminmenge_by_abijahr_and_halbjahr_and_quartal.getOrNull(abiJahrgang, halbjahr.id, 0));
			return termine;
		}
		if (_terminmenge_by_abijahr_and_halbjahr_and_quartal.containsKey1AndKey2(abiJahrgang, halbjahr.id))
			for (final @NotNull List<GostKlausurtermin> qTermine : _terminmenge_by_abijahr_and_halbjahr_and_quartal
					.getNonNullValuesOfMap3AsList(abiJahrgang, halbjahr.id)) {
				termine.addAll(qTermine);
			}
		return termine;
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten, die für Nachschreiber
	 * zugelassen sind zum übergebenen Quartal
	 *
	 * @param abiJahrgang   der Abitur-Jahrgang
	 * @param halbjahr      das Gost-Halbjahr
	 * @param quartal       die Nummer des Quartals, 0 für alle Quartale
	 * @param multijahrgang wenn true, werden die Klausurtermin-Objekte der
	 *                      Parallel-Jahrgänge eingeschlossen
	 *
	 * @return die Liste von NT-GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminGetNTMengeByHalbjahrAndQuartalMultijahrgang(
			final int abiJahrgang, final @NotNull GostHalbjahr halbjahr, final int quartal,
			final boolean multijahrgang) {
		final List<GostKlausurtermin> termine = new ArrayList<>();
		for (final @NotNull GostKlausurtermin t : terminGetMengeByHalbjahrAndQuartalMultijahrgang(abiJahrgang, halbjahr,
				quartal, multijahrgang))
			if (!t.istHaupttermin || t.nachschreiberZugelassen)
				termine.add(t);
		termine.sort(_compTermin);
		return termine;
	}

	/**
	 * Prüft, ob in einem Nachschreibtermin Schülerklausurtermine anderer
	 * Jahrgangsstufen enthalten sind
	 *
	 * @param abiJahrgang   der Abitur-Jahrgang
	 * @param halbjahr      das Gost-Halbjahr
	 * @param quartal       die Nummer des Quartals, 0 für alle Quartale
	 * @param multijahrgang wenn true, werden die Klausurtermin-Objekte der
	 *                      Parallel-Jahrgänge eingeschlossen
	 *
	 * @return true, falls andere Jgst. enthalten sind, sonst false
	 */
	public boolean terminGetNTMengeEnthaeltFremdeJgstByHalbjahrAndQuartalMultijahrgang(final int abiJahrgang,
			final @NotNull GostHalbjahr halbjahr, final int quartal, final boolean multijahrgang) {
		final List<GostKlausurtermin> termine = terminGetNTMengeByHalbjahrAndQuartalMultijahrgang(abiJahrgang, halbjahr, quartal, multijahrgang);
		for (final @NotNull GostKlausurtermin t : terminGetMengeByHalbjahrAndQuartalMultijahrgang(abiJahrgang, halbjahr, quartal, multijahrgang))
			if (terminMitAnderenJgst(t))
				return true;
		return false;
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten, die als Haupttermin
	 * angelegt wurden zum übergebenen Quartal
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gost-Halbjahr
	 * @param quartal     die Nummer des Quartals, 0 für alle Quartale
	 *
	 * @return die Liste von HT-GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminGetHTMengeByHalbjahrAndQuartal(final int abiJahrgang, final @NotNull GostHalbjahr halbjahr,
			final int quartal) {
		final List<GostKlausurtermin> termine = new ArrayList<>();
		for (final @NotNull GostKlausurtermin t : terminGetMengeByHalbjahrAndQuartal(abiJahrgang, halbjahr, quartal))
			if (t.istHaupttermin)
				termine.add(t);
		termine.sort(_compTermin);
		return termine;
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten des Halbjahres, bei denen
	 * ein Datum gesetzt ist
	 *
	 * @return die Liste von GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminMitDatumGetMenge() {
		final List<GostKlausurtermin> termineMitDatum = new ArrayList<>();
		for (final @NotNull GostKlausurtermin termin : _terminmenge)
			if (termin.datum != null)
				termineMitDatum.add(termin);
		termineMitDatum.sort(_compTermin);
		return termineMitDatum;
	}

	/**
	 * Liefert eine Liste von GostKlausurtermin-Objekten des Quartals, bei denen ein
	 * Datum gesetzt ist
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gost-Halbjahr
	 * @param quartal     die Nummer des Quartals
	 *
	 * @return die Liste von GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminMitDatumGetMengeByHalbjahrAndQuartal(final int abiJahrgang,
			final @NotNull GostHalbjahr halbjahr, final int quartal) {
		final List<GostKlausurtermin> termineMitDatum = new ArrayList<>();
		for (final @NotNull GostKlausurtermin termin : terminGetMengeByHalbjahrAndQuartal(abiJahrgang, halbjahr,
				quartal))
			if (termin.datum != null)
				termineMitDatum.add(termin);
		termineMitDatum.sort(_compTermin);
		return termineMitDatum;
	}

	/**
	 * Liefert eine Liste von HT-GostKlausurtermin-Objekten des Quartals, bei denen
	 * ein Datum gesetzt ist
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gost-Halbjahr
	 * @param quartal     die Nummer des Quartals
	 *
	 * @return die Liste von HT-GostKlausurtermin-Objekten
	 */
	public @NotNull List<GostKlausurtermin> terminMitDatumGetHTMengeByHalbjahrAndQuartal(final int abiJahrgang,
			final @NotNull GostHalbjahr halbjahr, final int quartal) {
		final List<GostKlausurtermin> termineMitDatum = new ArrayList<>();
		for (final @NotNull GostKlausurtermin termin : terminGetHTMengeByHalbjahrAndQuartal(abiJahrgang, halbjahr,
				quartal))
			if (termin.datum != null)
				termineMitDatum.add(termin);
		termineMitDatum.sort(_compTermin);
		return termineMitDatum;
	}

	/**
	 * Liefert das Quartal der Kursklausuren innerhalb des Klausurtermins, sofern
	 * alle identisch sind, sonst -1.
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return das Quartal aller Klausuren, sofern identisch, sonst -1.
	 */
	public int quartalGetByTerminid(final long idTermin) {
		final @NotNull List<GostKursklausur> klausuren = kursklausurGetMengeByTerminid(idTermin);
		final @NotNull List<GostSchuelerklausurTermin> schuelertermine = schuelerklausurterminNtByTerminid(
				idTermin);
		if (klausuren.isEmpty() && schuelertermine.isEmpty())
			return DeveloperNotificationException.ifMapGetIsNull(_termin_by_id, idTermin).quartal;
		final @NotNull List<GostKlausurvorgabe> vorgaben = new ArrayList<>();
		for (final @NotNull GostKursklausur k : klausuren)
			vorgaben.add(vorgabeByKursklausur(k));
		for (final @NotNull GostSchuelerklausurTermin k : schuelertermine)
			vorgaben.add(vorgabeBySchuelerklausurTermin(k));
		int quartal = -1;
		for (final @NotNull GostKlausurvorgabe v : vorgaben) {
			if (quartal == -1)
				quartal = v.quartal;
			if (quartal != v.quartal)
				return -1;
		}
		return quartal;
	}

	/**
	 * Liefert die Anzahl der Schülerklausuren zu einem bestimmten Klausurtermin.
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return die Anzahl der Schülerklausuren des Termins.
	 */
	public int schuelerklausurAnzahlGetByTerminid(final long idTermin) {
		final List<GostSchuelerklausurTermin> skts = _schuelerklausurterminmenge_by_idTermin.get(idTermin);
		return (skts == null) ? 0 : skts.size();
	}

	/**
	 * Liefert die minimale Startzeit des Klausurtermins in Minuten
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return die minimale Startzeit
	 */
	public int minKursklausurstartzeitByTerminid(final long idTermin) {
		int minStart = 1440;
		final @NotNull GostKlausurtermin termin = terminGetByIdOrException(idTermin);
		final List<GostKursklausur> kks = _kursklausurmenge_by_idTermin.get(idTermin);
		final List<GostSchuelerklausurTermin> skts = schuelerklausurterminFolgeterminGetMengeByTerminid(
				idTermin);
		if (((kks == null) || kks.isEmpty()) && ((skts == null) || skts.isEmpty()))
			return termin.startzeit;
		if (kks != null)
			for (final GostKursklausur kk : kks) {
				int skStartzeit = -1;
				if (kk.startzeit != null)
					skStartzeit = kk.startzeit;
				else if (termin.startzeit != null)
					skStartzeit = termin.startzeit;
				else
					throw new DeveloperNotificationException(
							"Startzeit des Termins nicht definiert, Termin-ID: " + idTermin);
				if (skStartzeit < minStart)
					minStart = skStartzeit;
			}
		if (skts != null)
			for (final GostSchuelerklausurTermin skt : skts) {
				int skStartzeit = -1;
				if (skt.startzeit != null)
					skStartzeit = skt.startzeit;
				else if (termin.startzeit != null)
					skStartzeit = termin.startzeit;
				else
					throw new DeveloperNotificationException(
							"Startzeit des Termins nicht definiert, Termin-ID: " + idTermin);
				if (skStartzeit < minStart)
					minStart = skStartzeit;
			}
		return minStart;
	}

	/**
	 * Liefert die maximale Endzeit des Klausurtermins in Minuten
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return die maximale Endzeit
	 */
	public int maxKursklausurendzeitByTerminid(final long idTermin) {
		int maxEnd = minKursklausurstartzeitByTerminid(idTermin) + 1;
		final @NotNull GostKlausurtermin termin = terminGetByIdOrException(idTermin);
		final List<GostKursklausur> kks = _kursklausurmenge_by_idTermin.get(idTermin);
		final List<GostSchuelerklausurTermin> skts = schuelerklausurterminFolgeterminGetMengeByTerminid(
				idTermin);
		if (((kks == null) || kks.isEmpty()) && ((skts == null) || skts.isEmpty()))
			return maxEnd;
		if (kks != null)
			for (final GostKursklausur kk : kks) {
				final @NotNull GostKlausurvorgabe vorgabe = _vorgabenManager.vorgabeGetByIdOrException(kk.idVorgabe);
				int skStartzeit = -1;
				if (kk.startzeit != null)
					skStartzeit = kk.startzeit;
				else if (termin.startzeit != null)
					skStartzeit = termin.startzeit;
				else
					throw new DeveloperNotificationException(
							"Startzeit des Termins nicht definiert, Termin-ID: " + idTermin);
				final int endzeit = skStartzeit + vorgabe.dauer + vorgabe.auswahlzeit;
				if (endzeit > maxEnd)
					maxEnd = endzeit;
			}
		if (skts != null)
			for (final GostSchuelerklausurTermin skt : skts) {
				final @NotNull GostKlausurvorgabe vorgabe = vorgabeBySchuelerklausurTermin(skt);
				int skStartzeit = -1;
				if (skt.startzeit != null)
					skStartzeit = skt.startzeit;
				else if (termin.startzeit != null)
					skStartzeit = termin.startzeit;
				else
					throw new DeveloperNotificationException(
							"Startzeit des Termins nicht definiert, Termin-ID: " + idTermin);
				final int endzeit = skStartzeit + vorgabe.dauer + vorgabe.auswahlzeit;
				if (endzeit > maxEnd)
					maxEnd = endzeit;
			}
		return maxEnd;
	}

	/**
	 * Liefert die maximale Klausurdauer innerhalb eines Klausurtermins
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return die maximale Klausurdauer innerhalb des Termins
	 */
	public int maxKlausurdauerGetByTerminid(final long idTermin) {
		int maxDauer = 0;
		final List<GostKursklausur> klausuren = _kursklausurmenge_by_idTermin.get(idTermin);
		if (klausuren != null)
			for (final @NotNull GostKursklausur klausur : klausuren) {
				final @NotNull GostKlausurvorgabe vorgabe = vorgabeByKursklausur(klausur);
				maxDauer = (vorgabe.dauer > maxDauer) ? vorgabe.dauer : maxDauer;
			}
		final List<GostSchuelerklausurTermin> skts = schuelerklausurterminFolgeterminGetMengeByTerminid(
				idTermin);
		if (skts != null)
			for (final @NotNull GostSchuelerklausurTermin skt : skts) {
				final @NotNull GostKlausurvorgabe vorgabe = vorgabeBySchuelerklausurTermin(skt);
				maxDauer = (vorgabe.dauer > maxDauer) ? vorgabe.dauer : maxDauer;
			}
		return maxDauer;
	}

	// #####################################################################
	// #################### Konfliktberechnung Start
	// ################################
	// #####################################################################

	/**
	 * Prüft, ob Schülerklausurtermine aus der Menge menge2 konfliktfrei in die
	 * Menge menge1 hinzugefügt werden können. Falls ein Schülerklausurtermin aus
	 * menge1 bereits in menge2 enthalten ist, wird dies nicht als Konflikt
	 * bewertet.
	 *
	 * @param menge1 f
	 * @param menge2 f
	 *
	 * @return d
	 */
	private @NotNull Map<GostSchuelerklausurTermin, GostSchuelerklausurTermin> berechneKonflikteSchuelerklausurtermine(
			final List<GostSchuelerklausurTermin> menge1,
			final List<GostSchuelerklausurTermin> menge2) {
		final @NotNull Map<Long, GostSchuelerklausurTermin> map1 = new HashMap<>();
		if (menge1 != null)
			for (@NotNull final GostSchuelerklausurTermin termin1 : menge1)
//		DeveloperNotificationException.ifMapPutOverwrites(map1,
//			schuelerklausurGetByIdOrException(termin1.idSchuelerklausur).idSchueler, termin1);
				map1.put(schuelerklausurGetByIdOrException(termin1.idSchuelerklausur).idSchueler, termin1);
		return berechneKonflikteMapschuelerklausurterminToListSchuelerklausurtermin(map1, menge2);
	}

	/**
	 * Prüft, ob Schülerklausurtermine aus der Menge menge2 konfliktfrei in die
	 * Menge menge1 hinzugefügt werden können. Falls ein Schülerklausurtermin aus
	 * menge1 bereits in menge2 enthalten ist, wird dies nicht als Konflikt
	 * bewertet.
	 *
	 * @param menge1 f
	 * @param menge2 f
	 *
	 * @return d
	 */
	private @NotNull Map<GostSchuelerklausurTermin, GostSchuelerklausurTermin> berechneKonflikteMapschuelerklausurterminToListSchuelerklausurtermin(
			final Map<Long, GostSchuelerklausurTermin> menge1, final List<GostSchuelerklausurTermin> menge2) {
		final @NotNull Map<GostSchuelerklausurTermin, GostSchuelerklausurTermin> ergebnis = new HashMap<>();
		if ((menge1 == null) || (menge2 == null))
			return ergebnis;
		for (final @NotNull GostSchuelerklausurTermin skt2 : menge2) {
			final @NotNull GostSchuelerklausur sk = schuelerklausurBySchuelerklausurtermin(skt2);
			final GostSchuelerklausurTermin skt1 = menge1.get(sk.idSchueler);
			if ((skt1 != null) && (skt1.id != skt2.id))
				ergebnis.put(skt1, skt2);
		}
		return ergebnis;
	}

	/**
	 * Prüft, ob ein Schülerklausurtermin konfliktfrei zu einem bestehenden
	 * Klausurtermin hinzugefügt werden kann. Falls der Schülerklausurtermin bereits
	 * dem Termin zugewiesen war, wird dies nicht als Konflikt bewertet.
	 *
	 * @param termin der zu prüfende Klausurtermin
	 * @param skt    der zu prüfende Schülerklausurtermin
	 *
	 * @return die Anzahl der Konflikte
	 */
	public int konflikteAnzahlZuTerminGetByTerminAndSchuelerklausurtermin(final @NotNull GostKlausurtermin termin,
			final @NotNull GostSchuelerklausurTermin skt) {
		return berechneKonflikteSchuelerklausurtermine(_schuelerklausurterminmenge_by_idTermin.get(termin.id),
				ListUtils.create1(skt)).size();
	}

	/**
	 * Prüft, ob der zu einer Schülerklausur gehörige Schüler in einer Kursklausur
	 * enthalten ist.
	 *
	 * @param schuelerklausur die zu prüfende Schülerklausur
	 * @param kursklausur     die zu prüfende Kursklausur
	 *
	 * @return die Anzahl der Konflikte
	 */
	public boolean konfliktZuKursklausurBySchuelerklausur(final @NotNull GostSchuelerklausur schuelerklausur, final @NotNull GostKursklausur kursklausur) {
		final @NotNull List<Long> schuelerids = new ArrayList<>();
		for (final @NotNull GostSchuelerklausur sk : schuelerklausurGetMengeByKursklausurid(kursklausur.id))
			schuelerids.add(sk.idSchueler);
		return schuelerids.contains(schuelerklausur.idSchueler);
	}

	/**
	 * Liefert eine Map Kursklausur -> Schülerids, die die Konflikte in jeder
	 * Klausur der übergebenen Termin-ID enthält
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return die Map Kursklausur -> Schülerids
	 */
	public @NotNull Map<GostKursklausur, Set<Long>> konflikteMapKursklausurSchueleridsByTerminid(final long idTermin) {
		final List<GostKursklausur> klausuren = _kursklausurmenge_by_idTermin.get(idTermin);
		if (klausuren == null)
			return new HashMap<>();
		return berechneKonflikte(klausuren, klausuren);
	}

	/**
	 * Liefert eine Map Kursklausur -> Schülerids, die nur die Konflikte liefert,
	 * die die übergeben Klausur im übergebenen Termin verursacht
	 *
	 * @param idTermin      die ID des Klausurtermins
	 * @param idKursklausur die ID der Kursklausur
	 *
	 * @return die Map Kursklausur -> Schülerids
	 */
	public @NotNull Map<GostKursklausur, Set<Long>> konflikteNeuMapKursklausurSchueleridsByTerminidAndKursklausurid(
			final long idTermin, final long idKursklausur) {
		final List<GostKursklausur> klausuren1 = _kursklausurmenge_by_idTermin.get(idTermin);
		final List<GostKursklausur> klausuren2 = new ArrayList<>();
		klausuren2.add(DeveloperNotificationException.ifMapGetIsNull(_kursklausur_by_id, idKursklausur));
		return berechneKonflikte((klausuren1 != null) ? klausuren1 : new ArrayList<>(), klausuren2);
	}

	/**
	 * Prüft, ob eine Kursklausur konfliktfrei zu einem bestehenden Klausurtermin
	 * hinzugefügt werden kann. Es werden die Schüler-IDs, die den Konflikt
	 * verursachen, als Liste zurückgegeben. Wenn die zurückgegebene Liste leer ist,
	 * gibt es keinen Konflikt.
	 *
	 * @param klausur die zu prüfende Kursklausur
	 *
	 * @return die Anzahl der Schüler-IDs, die einen Konflikt verursachen.
	 */
	public int konflikteAnzahlZuEigenemTerminGetByKursklausur(final @NotNull GostKursklausur klausur) {
		final @NotNull List<GostKursklausur> klausuren1 = new ArrayList<>(
				DeveloperNotificationException.ifMapGetIsNull(_kursklausurmenge_by_idTermin, klausur.idTermin));
		klausuren1.remove(klausur);
		final List<GostKursklausur> klausuren2 = new ArrayList<>();
		klausuren2.add(klausur);
		return countKonflikte(berechneKonflikte(klausuren1, klausuren2));
	}

	/**
	 * Prüft, ob eine Kursklausur konfliktfrei zu einem bestehenden Klausurtermin
	 * hinzugefügt werden kann. Es werden die Schüler-IDs, die den Konflikt
	 * verursachen, als Liste zurückgegeben. Wenn die zurückgegebene Liste leer ist,
	 * gibt es keinen Konflikt.
	 *
	 * @param termin  der zu prüfende Klausurtermin
	 * @param klausur die zu prüfende Kursklausur
	 *
	 * @return die Liste der Schüler-IDs, die einen Konflikt verursachen.
	 */
	public int konflikteAnzahlZuTerminGetByTerminAndKursklausur(final @NotNull GostKlausurtermin termin,
			final @NotNull GostKursklausur klausur) {
		return countKonflikte(konflikteNeuMapKursklausurSchueleridsByTerminidAndKursklausurid(termin.id, klausur.id));
	}

	/**
	 * Prüft, ob es innerhalb eines bestehenden Klausurtermins Konflikte gibt. Es
	 * wird die Anzahl der Konflikte zurückgegeben.
	 *
	 * @param idTermin die ID des zu prüfenden Klausurtermins
	 *
	 * @return die Anzahl der Konflikte innerhalb des Termins.
	 */
	public int konflikteAnzahlGetByTerminid(final long idTermin) {
		return countKonflikte(konflikteMapKursklausurSchueleridsByTerminid(idTermin));
	}

	private @NotNull Map<GostKursklausur, Set<Long>> berechneKonflikte(final @NotNull List<GostKursklausur> klausuren1,
			final @NotNull List<GostKursklausur> klausuren2) {
		final Map<GostKursklausur, Set<Long>> result = new HashMap<>();
		final List<GostKursklausur> kursklausuren2Copy = new ArrayList<>(klausuren2);
		for (final @NotNull GostKursklausur kk1 : klausuren1) {
			kursklausuren2Copy.remove(kk1);
			for (final @NotNull GostKursklausur kk2 : kursklausuren2Copy) {
				final Set<Long> konflikte = berechneKlausurKonflikte(kk1, kk2);
				if (!konflikte.isEmpty()) {
					MapUtils.getOrCreateHashSet(result, kk1).addAll(konflikte);
					MapUtils.getOrCreateHashSet(result, kk2).addAll(konflikte);
				}
			}
		}
		return result;
	}

	private @NotNull Set<Long> berechneKlausurKonflikte(final @NotNull GostKursklausur kk1, final @NotNull GostKursklausur kk2) {
		final @NotNull HashSet<Long> konflikte = new HashSet<>(getSchuelerIDsFromKursklausur(kk1));
		konflikte.retainAll(getSchuelerIDsFromKursklausur(kk2));
		return konflikte;
	}

	private static int countKonflikte(final @NotNull Map<GostKursklausur, Set<Long>> konflikte) {
		final @NotNull HashSet<Long> susIds = new HashSet<>();
		for (final @NotNull Set<Long> klausurSids : konflikte.values())
			susIds.addAll(klausurSids);
		return susIds.size();
	}

	// #####################################################################
	// #################### Konfliktberechnung Ende ################################
	// #####################################################################

	/**
	 * Liefert für einen Schwellwert und einen Klausurtermin eine Map, die alle
	 * Schülerids mit einer Kursklausur-Liste enthält, die in der den Termin
	 * enthaltenen Kalenderwoche mehr (>=) Klausuren schreibt, als der Schwellwert
	 * definiert
	 *
	 * @param termin    der Klausurtermin, dessen Kalenderwoche geprüft wird
	 * @param threshold der Schwellwert (z.B. 3), der erreicht sein muss, damit die
	 *                  Klausuren in die Map aufgenommen werden
	 *
	 * @return die Map (Schülerid -> GostKursklausur)
	 */
	public @NotNull Map<Long, List<GostKursklausur>> klausurenProSchueleridExceedingKWThresholdByTerminAndThreshold(
			final @NotNull GostKlausurtermin termin, final int threshold) {
		return klausurenProSchueleridExceedingKWThresholdByTerminAndKursklausurAndThreshold(termin, null, threshold);
	}

	/**
	 * Liefert für einen Schwellwert, einen Klausurtermin und eine Kursklausur eine
	 * Map, die alle Schülerids mit einer Kursklausur-Liste enthält, die in der den
	 * Termin enthaltenen Kalenderwoche mehr (>=) Klausuren schreibt, als der
	 * Schwellwert definiert, wenn die übergebene Kursklausur in den Termin
	 * integriert würde
	 *
	 * @param termin    der Klausurtermin, dessen Kalenderwoche geprüft wird
	 * @param klausur   die Klausur, deren Integration in den Termin angenommen wird
	 * @param threshold der Schwellwert (z.B. 3), der erreicht sein muss, damit die
	 *                  Klausuren berücksichtigt werden
	 *
	 * @return die Map (Schülerid -> GostKursklausur)
	 */
	public @NotNull Map<Long, List<GostKursklausur>> klausurenProSchueleridExceedingKWThresholdByTerminAndKursklausurAndThreshold(
			final @NotNull GostKlausurtermin termin, final GostKursklausur klausur, final int threshold) {
		final Map<Long, List<GostKursklausur>> ergebnis = new HashMap<>();
		if (termin.datum == null)
			return ergebnis;
		final int kw = DateUtils.gibKwDesDatumsISO8601(termin.datum);
		final Map<Long, List<GostKursklausur>> kursklausurmenge_by_schuelerId = _kursklausurmenge_by_kw_and_schuelerId.getSubMapOrNull(kw);
		if (kursklausurmenge_by_schuelerId == null)
			return ergebnis;
		for (final @NotNull Entry<Long, List<GostKursklausur>> entry : kursklausurmenge_by_schuelerId
				.entrySet()) {
			final List<GostKursklausur> temp = entry.getValue();
			final @NotNull List<GostKursklausur> klausuren = (temp != null) ? new ArrayList<>(temp)
					: new ArrayList<>();
			if ((klausur != null) && (klausur.idTermin != termin.id)
					&& getSchuelerIDsFromKursklausur(klausur).contains(entry.getKey()))
				klausuren.add(klausur);
			if (klausuren.size() >= threshold)
				ergebnis.put(entry.getKey(), klausuren);
		}
		return ergebnis;
	}

	/**
	 * Liefert für eine Liste von GostSchuelerklausur-Objekten die zugehörigen
	 * Schüler-IDs als Liste.
	 *
	 * @param sks Die Liste von GostSchuelerklausur-Objekten
	 *
	 * @return die Liste der Schüler-IDs
	 */
	public @NotNull List<Long> getSchuelerIDsFromSchuelerklausuren(
			final @NotNull List<GostSchuelerklausur> sks) {
		final @NotNull List<Long> ids = new ArrayList<>();
		for (final @NotNull GostSchuelerklausur sk : sks) {
			ids.add(sk.idSchueler);
		}
		return ids;
	}

	/**
	 * Liefert für ein GostKursklausur-Objekt die zugehörigen Schüler-IDs als Liste.
	 *
	 * @param kk die Kursklausur, zu der die Schüler-IDs gesucht werden.
	 *
	 * @return die Liste der Schüler-IDs
	 */
	public @NotNull List<Long> getSchuelerIDsFromKursklausur(final @NotNull GostKursklausur kk) {
		return getSchuelerIDsFromSchuelerklausuren(schuelerklausurGetMengeByKursklausurid(kk.id));
	}

	/**
	 * Liefert für einen Schwellwert und einen Klausurtermin eine Map, die alle
	 * Schülerids mit einer Kursklausur-Liste enthält, die in der den Termin
	 * enthaltenen Kalenderwoche mehr (>=) Klausuren schreibt, als der Schwellwert
	 * definiert
	 *
	 * @param termin        der Klausurtermin, dessen Kalenderwoche geprüft wird
	 * @param datum         das Datum, auf
	 * @param threshold     der Schwellwert (z.B. 3), der erreicht sein muss, damit
	 *                      die Klausuren in die Map aufgenommen werden
	 * @param thresholdOnly nur die exakte Anzahl an Klausurkonflikten wird in die
	 *                      Ergebnismap übernommen
	 *
	 * @return die Map (Schülerid -> GostKursklausur)
	 */
	public @NotNull Map<Long, HashSet<GostKursklausur>> klausurenProSchueleridExceedingKWThresholdByTerminAndDatumAndThreshold(
			final @NotNull GostKlausurtermin termin, final @NotNull String datum, final int threshold, final boolean thresholdOnly) {
		final int kwDatum = DateUtils.gibKwDesDatumsISO8601(datum);
		return klausurenProSchueleridExceedingKWThresholdByKwAndTerminAndThreshold(kwDatum, termin, threshold, thresholdOnly);
	}


	private @NotNull Map<Long, HashSet<GostKursklausur>> klausurenProSchueleridExceedingKWThresholdByKwAndTerminAndThreshold(
			final int kw, final GostKlausurtermin termin, final int threshold, final boolean thresholdOnly) {
		final @NotNull Map<Long, HashSet<GostKursklausur>> ergebnis = new HashMap<>();

		final Map<Long, List<GostKursklausur>> kursklausurmenge_by_schuelerId = _kursklausurmenge_by_kw_and_schuelerId.getSubMapOrNull(kw);
		if (kursklausurmenge_by_schuelerId == null)
			return ergebnis;

		for (final @NotNull Entry<Long, List<GostKursklausur>> entry : kursklausurmenge_by_schuelerId.entrySet()) {
			final List<GostKursklausur> temp = entry.getValue();
			final @NotNull HashSet<GostKursklausur> klausuren = (temp != null) ? new HashSet<>(temp) : new HashSet<>();
			if (termin != null) {
				final List<GostKursklausur> klausurenInTermin = _kursklausurmenge_by_terminId_and_schuelerId
						.getOrNull(termin.id, entry.getKey());
				if (klausurenInTermin != null)
					klausuren.addAll(klausurenInTermin);
			}
			if ((klausuren.size() == threshold) || ((klausuren.size() > threshold) && !thresholdOnly))
				ergebnis.put(entry.getKey(), klausuren);
		}
		return ergebnis;
	}


	/**
	 * Liefert für einen Schwellwert und einen Klausurtermin eine Map, die alle
	 * Schülerids mit einer Kursklausur-Liste enthält, die in der den Termin
	 * enthaltenen Kalenderwoche mehr (>=) Klausuren schreibt, als der Schwellwert
	 * definiert
	 *
	 * @param kw            der Klausurtermin, dessen Kalenderwoche geprüft wird
	 * @param threshold     der Schwellwert (z.B. 3), der erreicht sein muss, damit
	 *                      die Klausuren in die Map aufgenommen werden
	 * @param thresholdOnly nur die exakte Anzahl an Klausurkonflikten wird in die
	 *                      Ergebnismap übernommen
	 *
	 * @return die Map (Schülerid -> GostKursklausur)
	 */
	public @NotNull Map<Long, HashSet<GostKursklausur>> klausurenProSchueleridExceedingKWThresholdByKwAndThreshold(
			final int kw, final int threshold, final boolean thresholdOnly) {
		return klausurenProSchueleridExceedingKWThresholdByKwAndTerminAndThreshold(kw, null, threshold, thresholdOnly);
	}

	/**
	 * Liefert den Klausurtermin zu einer Kursklausur, sonst NULL.
	 *
	 * @param klausur die Kursklausur, zu der der Termin gesucht wird.
	 *
	 * @return den Klausurtermin
	 */
	public GostKlausurtermin terminOrNullByKursklausur(final @NotNull GostKursklausur klausur) {
		return _termin_by_id.get(klausur.idTermin);
	}

	/**
	 * Liefert den Klausurtermin zu einer Kursklausur, sonst NULL.
	 *
	 * @param klausur die Kursklausur, zu der der Termin gesucht wird.
	 *
	 * @return den Klausurtermin
	 */
	public @NotNull GostKlausurtermin terminOrExceptionByKursklausur(final @NotNull GostKursklausur klausur) {
		return DeveloperNotificationException.ifMapGetIsNull(_termin_by_id, DeveloperNotificationException
				.ifNull(String.format("idTermin von Klausur %d darf nicht NULL sein", klausur.id), klausur.idTermin));
	}

	/**
	 * Liefert den Klausurtermin zu einem Schülerklausurtermin oder NULL.
	 *
	 * @param termin der Schülerklausurtermin, zu dem der Termin gesucht wird.
	 *
	 * @return den Klausurtermin
	 */
	public GostKlausurtermin terminOrNullBySchuelerklausurTermin(final @NotNull GostSchuelerklausurTermin termin) {
		if (termin.folgeNr > 0)
			return (termin.idTermin == null) ? null : terminGetByIdOrException(termin.idTermin);
		return terminOrNullByKursklausur(kursklausurBySchuelerklausurTermin(termin));
	}

	/**
	 * Liefert den Klausurtermin zu einem Schülerklausurtermin oder NULL.
	 *
	 * @param termin der Schülerklausurtermin, zu dem der Termin gesucht wird.
	 *
	 * @return den Klausurtermin
	 */
	public @NotNull GostKlausurtermin terminOrExceptionBySchuelerklausurTermin(
			final @NotNull GostSchuelerklausurTermin termin) {
		if (termin.folgeNr > 0) {
			return terminGetByIdOrException(DeveloperNotificationException
					.ifNull(String.format("idTermin von Termin %d darf nicht NULL sein", termin.id), termin.idTermin));
		}
		return terminOrExceptionByKursklausur(kursklausurBySchuelerklausurTermin(termin));
	}

	/**
	 * Liefert den Klausurtermin zu einer Schülerklausur oder NULL.
	 *
	 * @param sk die Schülerklausur
	 *
	 * @return den Klausurtermin
	 */
	public GostKlausurtermin terminKursklausurBySchuelerklausur(final @NotNull GostSchuelerklausur sk) {
		return terminOrNullByKursklausur(kursklausurBySchuelerklausur(sk));
	}

	/**
	 * Liefert die Klausurvorgabe zu einer Kursklausur.
	 *
	 * @param klausur die Kursklausur, zu der die Vorgabe gesucht wird.
	 *
	 * @return die Klausurvorgabe
	 */
	public @NotNull GostKlausurvorgabe vorgabeByKursklausur(final @NotNull GostKursklausur klausur) {
		return _vorgabenManager.vorgabeGetByIdOrException(klausur.idVorgabe);
	}

	/**
	 * Liefert die Klausurvorgabe zu einer Schuelerklausur.
	 *
	 * @param klausur die Schuelerklausur, zu der die Vorgabe gesucht wird.
	 *
	 * @return die Klausurvorgabe
	 */
	public @NotNull GostKlausurvorgabe vorgabeBySchuelerklausur(final @NotNull GostSchuelerklausur klausur) {
		final @NotNull GostKursklausur kk = kursklausurGetByIdOrException(klausur.idKursklausur);
		return _vorgabenManager.vorgabeGetByIdOrException(kk.idVorgabe);
	}

	/**
	 * Liefert die Klausurvorgabe zu einem Schuelerklausurtermin.
	 *
	 * @param klausur der Schuelerklausurtermin, zu der die Vorgabe gesucht wird.
	 *
	 * @return die Klausurvorgabe
	 */
	public @NotNull GostKlausurvorgabe vorgabeBySchuelerklausurTermin(
			final @NotNull GostSchuelerklausurTermin klausur) {
		return vorgabeBySchuelerklausur(schuelerklausurGetByIdOrException(klausur.idSchuelerklausur));
	}

	/**
	 * Liefert die GostSchuelerklausur zu einem GostSchuelerklausurTermin.
	 *
	 * @param klausur die Schuelerklausur, zu der die GostKursklausur gesucht wird.
	 *
	 * @return die GostSchuelerklausur
	 */
	public @NotNull GostSchuelerklausur schuelerklausurBySchuelerklausurtermin(
			final @NotNull GostSchuelerklausurTermin klausur) {
		return schuelerklausurGetByIdOrException(klausur.idSchuelerklausur);
	}

	/**
	 * Liefert die GostKursklausur zu einer Schuelerklausur.
	 *
	 * @param klausur die Schuelerklausur, zu der die GostKursklausur gesucht wird.
	 *
	 * @return die GostKursklausur
	 */
	public @NotNull GostKursklausur kursklausurBySchuelerklausur(final @NotNull GostSchuelerklausur klausur) {
		return kursklausurGetByIdOrException(klausur.idKursklausur);
	}

	/**
	 * Liefert die GostKursklausur zu einem Schuelerklausurtermin.
	 *
	 * @param termin der Schuelerklausurtermin, zu der die GostKursklausur gesucht
	 *               wird.
	 *
	 * @return die GostKursklausur
	 */
	public @NotNull GostKursklausur kursklausurBySchuelerklausurTermin(final @NotNull GostSchuelerklausurTermin termin) {
		return kursklausurBySchuelerklausur(schuelerklausurGetByIdOrException(termin.idSchuelerklausur));
	}

	/**
	 * Liefert zurück, ob die übergebene Klausurvorgabe von einer Kursklausur
	 * verwendet wird.
	 *
	 * @param vorgabe die Klausurvorgabe, die auf Verwendung geprüft werden soll.
	 *
	 * @return true oder false
	 */
	public boolean istVorgabeVerwendetByKursklausur(final @NotNull GostKlausurvorgabe vorgabe) {
		final List<GostKursklausur> klausuren = _kursklausurmenge_by_idVorgabe.get(vorgabe.idVorgabe);
		return (klausuren != null) && !klausuren.isEmpty();
	}

	/**
	 * Liefert das GostKursklausur-Objekt zu den übergebenen Parametern.
	 *
	 * @param idKurs      die ID des Kurses
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gosthalbjahr
	 * @param quartal     das Quartal der Klausur
	 *
	 * @return die Kursklausur
	 */
	public GostKursklausur kursklausurByKursidAndHalbjahrAndQuartal(final long idKurs, final int abiJahrgang,
			final @NotNull GostHalbjahr halbjahr, final int quartal) {
		return _kursklausur_by_idKurs_and_abijahr_and_halbjahr_and_quartal.getOrNull(idKurs, abiJahrgang, halbjahr.id,
				quartal);
	}

	/**
	 * Liefert die Vorgänger-GostKursklausur aus dem letzten Quartal, soweit
	 * vorhanden.
	 *
	 * @param klausur die Kursklausur, deren Vorgänger gesucht wird
	 *
	 * @return die Kursklausur
	 */
	public GostKursklausur kursklausurVorterminByKursklausur(final @NotNull GostKursklausur klausur) {
		final GostKlausurvorgabe previousVorgabe = _vorgabenManager
				.getPrevious(_vorgabenManager.vorgabeGetByIdOrException(klausur.idVorgabe));
		if (previousVorgabe == null)
			return null;
		final List<GostKursklausur> klausuren = _kursklausurmenge_by_idVorgabe.get(previousVorgabe.idVorgabe);
		if (klausuren == null)
			return null;
		for (final @NotNull GostKursklausur k : klausuren) {
			final KursDaten kKurs = getKursManager().get(k.idKurs);
			final KursDaten klausurKurs = getKursManager().get(klausur.idKurs);
			if ((kKurs == null) || (klausurKurs == null))
				throw new DeveloperNotificationException("Keine Kurszuordnung im kursManager zu Kurs-ID");
			if (kKurs.kuerzel.equals(klausurKurs.kuerzel)) // TODO unsauber, aber KursId geht nicht, weil ggf. in
				// Schuljahresabschnitten unterschiedlich
				return k;
		}
		return null;
	}

	/**
	 * Gibt die Startzeit der übergebenen Klausur aus. Falls keine individuelle
	 * gesetzt ist, wird die des Termins zurückgegeben. Sollte kein Termin gesetzt
	 * sein oder der Termin keine Startzeit definiert haben, wird null
	 * zurückgegeben.
	 *
	 * @param klausur die Kursklausur, deren Startzeit gesucht wird.
	 *
	 * @return die Startzeit der Klausur
	 */
	public Integer startzeitByKursklausurOrNull(final @NotNull GostKursklausur klausur) {
		final GostKlausurtermin termin = terminOrNullByKursklausur(klausur);
		if (klausur.startzeit != null)
			return klausur.startzeit;
		return (termin == null) ? null : termin.startzeit;
	}

	/**
	 * Gibt die Startzeit der übergebenen Klausur aus. Falls keine individuelle
	 * gesetzt ist, wird die des Termins zurückgegeben. Sollte kein Termin gesetzt
	 * sein oder der Termin keine Startzeit definiert haben, wird eine Exception
	 * zurückgegeben.
	 *
	 * @param klausur die Kursklausur, deren Startzeit gesucht wird.
	 *
	 * @return die Startzeit der Klausur
	 */
	public int startzeitByKursklausurOrException(final @NotNull GostKursklausur klausur) {
		return (klausur.startzeit != null) ? klausur.startzeit
				: DeveloperNotificationException.ifNull("Startzeit darf nicht null sein.",
						terminOrExceptionByKursklausur(klausur).startzeit);
	}

	/**
	 * Gibt die Startzeit der übergebenen Klausur aus. Falls keine individuelle
	 * gesetzt ist, wird die des Termins zurückgegeben. Sollte kein Termin gesetzt
	 * sein oder der Termin keine Startzeit definiert haben, wird null
	 * zurückgegeben.
	 *
	 * @param klausur die Kursklausur, deren Startzeit gesucht wird.
	 *
	 * @return die Startzeit der Klausur
	 */
	public boolean hatAbweichendeStartzeitByKursklausur(final @NotNull GostKursklausur klausur) {
		final GostKlausurtermin termin = terminOrNullByKursklausur(klausur);
		return !((klausur.startzeit == null) || (termin == null) || (termin.startzeit == null)
				|| termin.startzeit.equals(klausur.startzeit));
	}

	/**
	 * Gibt die Liste von Schülerklausur-Terminen zu einer Schülerklausur zurück.
	 *
	 * @param sk die Schülerklausur, zu der die Termine gesucht werden.
	 *
	 * @return die Liste von Schülerklausur-Terminen
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminGetMengeBySchuelerklausur(
			final @NotNull GostSchuelerklausur sk) {
		return DeveloperNotificationException.ifMapGetIsNull(_schuelerklausurterminmenge_by_idSchuelerklausur, sk.id);
	}

	/**
	 * Gibt die Liste von Schülerklausur-Terminen zu einem Klausurtermin zurück.
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return die Liste von Schülerklausur-Terminen
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminGetMengeByTerminid(final long idTermin) {
		final List<GostSchuelerklausurTermin> list = _schuelerklausurterminmenge_by_idTermin.get(idTermin);
		return (list != null) ? list : new ArrayList<>();
	}

	/**
	 * Gibt die Liste von Schülerklausur-Terminen zu einem Klausurtermin und einer
	 * Kursklausur zurück.
	 *
	 * @param idTermin      die ID des Klausurtermins
	 * @param idKursklausur die ID der Kursklausur
	 *
	 * @return die Liste von Schülerklausur-Terminen
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminaktuellGetMengeByTerminidAndKursklausurid(final long idTermin,
			final long idKursklausur) {
		final List<GostSchuelerklausurTermin> list =
				_schuelerklausurterminaktuellmenge_by_idTermin_and_idKursklausur.getOrNull(idTermin, idKursklausur);
		return (list != null) ? list : new ArrayList<>();
	}

	/**
	 * Gibt die Liste von Schülerklausur-Terminen zu einem Klausurtermin zurück.
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return die Liste von Schülerklausur-Terminen
	 */
	public @NotNull List<GostSchuelerklausur> schuelerklausurGetMengeByTerminid(final long idTermin) {
		final List<GostSchuelerklausur> ergebnis = new ArrayList<>();
		final List<GostSchuelerklausurTermin> list = _schuelerklausurterminmenge_by_idTermin.get(idTermin);
		if (list == null)
			return ergebnis;
		for (final @NotNull GostSchuelerklausurTermin termin : list)
			ergebnis.add(schuelerklausurBySchuelerklausurtermin(termin));
		return ergebnis;
	}

	/**
	 * Gibt die Liste von Folge-Schülerklausur-Terminen (Nachschreibtermine) zu
	 * einem Klausurtermin zurück.
	 *
	 * @param idTermin die ID des Klausurtermins
	 *
	 * @return die Liste von Folge-Schülerklausur-Terminen
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminFolgeterminGetMengeByTerminid(final long idTermin) {
		final @NotNull List<GostSchuelerklausurTermin> ergebnis = new ArrayList<>();
		for (final @NotNull GostSchuelerklausurTermin skt : schuelerklausurterminGetMengeByTerminid(idTermin))
			if (skt.folgeNr > 0)
				ergebnis.add(skt);
		return ergebnis;
	}

	/**
	 * Prüft, ob der übergebene Schülerklausurtermin der aktuellste Termin der
	 * Schülerklausur ist.
	 *
	 * @param skt der Schülerklausurtermin, der geprüft werden soll
	 *
	 * @return true, wenn es sich um den aktuellen Termin handelt, sonst false
	 */
	public boolean istAktuellerSchuelerklausurtermin(final @NotNull GostSchuelerklausurTermin skt) {
		return schuelerklausurterminaktuellBySchuelerklausur(skt.idSchuelerklausur) == skt;
	}

	/**
	 * Liefert den aktuellen Schülerklausurtermin zu einer übergebenen
	 * Schülerklausur
	 *
	 * @param idSchuelerklausur die ID der Schülerklausur, deren aktueller
	 *                          Schülerklausurtermin gesucht wird
	 *
	 * @return den aktuellen Schülerklausurtermin
	 */
	public @NotNull GostSchuelerklausurTermin schuelerklausurterminaktuellBySchuelerklausur(final long idSchuelerklausur) {
		final @NotNull List<GostSchuelerklausurTermin> skts =
				DeveloperNotificationException.ifMapGetIsNull(_schuelerklausurterminmenge_by_idSchuelerklausur, idSchuelerklausur);
		return DeveloperNotificationException.ifNull("Schülerklausur " + idSchuelerklausur + " enthält keine Schülerklausurtermine.", skts.getLast());
	}

	/**
	 * Liefert eine Liste von aktuellen
	 * Nachschreib-GostSchuelerklausurTermin-Objekten zum übergebenen Quartal für
	 * die ein Termin gesetzt wurde
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gosthalbjahr
	 * @param quartal     die Nummer des Quartals, 0 für alle Quartale
	 *
	 * @return die Liste von GostSchuelerklausurTermin-Objekten
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminNtAktuellMitTerminGetMengeByHalbjahrAndQuartal(
			final int abiJahrgang, final @NotNull GostHalbjahr halbjahr, final int quartal) {
		final @NotNull List<GostSchuelerklausurTermin> ergebnis = new ArrayList<>();
		if (quartal > 0) {
			final Map<Long, List<GostSchuelerklausurTermin>> skts =
					_schuelerklausurterminntaktuellmenge_by_abijahr_and_halbjahr_and_quartal_and_idTermin.getMap4OrNull(abiJahrgang, halbjahr.id, quartal);
			if (skts != null)
				for (final @NotNull Entry<Long, List<GostSchuelerklausurTermin>> entry : skts.entrySet())
					if (entry.getKey() != -1)
						ergebnis.addAll(entry.getValue());
		} else {
			final Map<Integer, Map<Long, List<GostSchuelerklausurTermin>>> skts =
					_schuelerklausurterminntaktuellmenge_by_abijahr_and_halbjahr_and_quartal_and_idTermin.getMap3OrNull(abiJahrgang, halbjahr.id);
			if (skts != null)
				for (@NotNull final Entry<Integer, Map<Long, List<GostSchuelerklausurTermin>>> entry : skts.entrySet())
					for (@NotNull final Entry<Long, List<GostSchuelerklausurTermin>> entry2 : entry.getValue().entrySet())
						if (entry2.getKey() != -1)
							ergebnis.addAll(entry2.getValue());
		}
		return ergebnis;
	}

	/**
	 * Liefert eine Liste von allen aktuellen
	 * Nachschreib-GostSchuelerklausurTermin-Objekten zum übergebenen Quartal
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gosthalbjahr
	 * @param quartal     die Nummer des Quartals, 0 für alle Quartale
	 *
	 * @return die Liste von GostSchuelerklausurTermin-Objekten
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminNtAktuellGetMengeByHalbjahrAndQuartal(
			final int abiJahrgang, final @NotNull GostHalbjahr halbjahr, final int quartal) {
		final @NotNull List<GostSchuelerklausurTermin> ergebnis = new ArrayList<>();
		if (quartal > 0) {
			final Map<Long, List<GostSchuelerklausurTermin>> map4 =
					_schuelerklausurterminntaktuellmenge_by_abijahr_and_halbjahr_and_quartal_and_idTermin.getMap4OrNull(abiJahrgang, halbjahr.id, quartal);
			if (map4 == null)
				return ergebnis;
			for (final @NotNull List<GostSchuelerklausurTermin> terminList : map4.values())
				ergebnis.addAll(terminList);
		} else {
			final Map<Integer, Map<Long, List<GostSchuelerklausurTermin>>> map3 =
					_schuelerklausurterminntaktuellmenge_by_abijahr_and_halbjahr_and_quartal_and_idTermin.getMap3OrNull(abiJahrgang, halbjahr.id);
			if (map3 == null)
				return ergebnis;
			for (final @NotNull Map<Long, List<GostSchuelerklausurTermin>> map4 : map3.values())
				for (final @NotNull List<GostSchuelerklausurTermin> terminList : map4.values())
					ergebnis.addAll(terminList);
		}
		return ergebnis;
	}

	/**
	 * Liefert eine Liste von aktuellen
	 * Nachschreib-GostSchuelerklausurTermin-Objekten zum übergebenen Quartal für
	 * die ein Termin gesetzt wurde
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gosthalbjahr
	 * @param quartal     die Nummer des Quartals, 0 für alle Quartale
	 *
	 * @return die Liste von GostSchuelerklausurTermin-Objekten
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminNtAktuellMitTerminUndDatumGetMengeByHalbjahrAndQuartal(
			final int abiJahrgang, final @NotNull GostHalbjahr halbjahr, final int quartal) {
		final @NotNull List<GostSchuelerklausurTermin> ergebnis = new ArrayList<>();
		for (final @NotNull GostSchuelerklausurTermin termin : schuelerklausurterminNtAktuellMitTerminGetMengeByHalbjahrAndQuartal(
				abiJahrgang, halbjahr, quartal)) {
			final GostKlausurtermin t = terminOrNullBySchuelerklausurTermin(termin);
			if ((t != null) && (t.datum != null))
				ergebnis.add(termin);
		}
		return ergebnis;
	}

	/**
	 * Liefert eine Liste von aktuellen
	 * Nachschreib-GostSchuelerklausurTermin-Objekten zum übergebenen Quartal für
	 * die noch kein Termin gesetzt wurde
	 *
	 * @param abiJahrgang der Abitur-Jahrgang
	 * @param halbjahr    das Gosthalbjahr
	 * @param quartal     die Nummer des Quartals, 0 für alle Quartale
	 *
	 * @return die Liste von GostSchuelerklausurTermin-Objekten
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminNtAktuellOhneTerminGetMengeByHalbjahrAndQuartal(
			final int abiJahrgang, final @NotNull GostHalbjahr halbjahr, final int quartal) {
		if (quartal > 0) {
			final List<GostSchuelerklausurTermin> skts =
					_schuelerklausurterminntaktuellmenge_by_abijahr_and_halbjahr_and_quartal_and_idTermin.getOrNull(abiJahrgang, halbjahr.id, quartal, -1L);
			if (skts != null)
				skts.sort(_compSchuelerklausurTermin);
			return (skts != null) ? skts : new ArrayList<>();
		}
		final @NotNull List<GostSchuelerklausurTermin> skts = new ArrayList<>();
		final Map<Integer, Map<Long, List<GostSchuelerklausurTermin>>> mapHalbjahr =
				_schuelerklausurterminntaktuellmenge_by_abijahr_and_halbjahr_and_quartal_and_idTermin.getMap3OrNull(abiJahrgang, halbjahr.id);
		if (mapHalbjahr != null)
			for (final @NotNull Map<Long, List<GostSchuelerklausurTermin>> sktList : mapHalbjahr.values()) {
				final List<GostSchuelerklausurTermin> listTermine = sktList.get(-1L);
				if (listTermine != null)
					skts.addAll(listTermine);
			}
		skts.sort(_compSchuelerklausurTermin);
		return skts;
	}

	/**
	 * Liefert eine Liste von Nachschreib-GostSchuelerklausurTermin-Objekten zum
	 * übergebenen Klausurtermin
	 *
	 * @param termin der Gost-Klausurtermin
	 *
	 * @return die Liste von GostSchuelerklausurTermin-Objekten
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminNtByTermin(final @NotNull GostKlausurtermin termin) {
		return schuelerklausurterminNtByTerminid(termin.id);
	}

	/**
	 * Liefert eine Liste von Nachschreib-GostSchuelerklausurTermin-Objekten zur
	 * übergebenen Klausurtermin-ID
	 *
	 * @param idTermin die ID des Gost-Klausurtermins
	 *
	 * @return die Liste von GostSchuelerklausurTermin-Objekten
	 */
	public @NotNull List<GostSchuelerklausurTermin> schuelerklausurterminNtByTerminid(final long idTermin) {
		final @NotNull List<GostSchuelerklausurTermin> ergebnis = new ArrayList<>();
		final @NotNull List<GostSchuelerklausurTermin> listSkts = schuelerklausurterminGetMengeByTerminid(
				idTermin);
		if (listSkts != null)
			for (final @NotNull GostSchuelerklausurTermin skt : listSkts)
				if (skt.folgeNr > 0)
					ergebnis.add(skt);
		return ergebnis;
	}

	/**
	 * Liefert den GostSchuelerklausurTermin, sofern vorhanden, zu einer
	 * Klausurtermin-ID und einer Schüler-ID
	 *
	 * @param idTermin   die ID des Klausurtermins
	 * @param idSchueler die ID des Schülers
	 *
	 * @return das GostSchuelerklausurTermin-Objekt, sofern vorhanden
	 */
	public GostSchuelerklausurTermin schuelerklausurterminByTerminidAndSchuelerid(final long idTermin, final long idSchueler) {
		final List<GostSchuelerklausurTermin> skts = _schuelerklausurterminmenge_by_idTermin.get(idTermin);
		if (skts != null)
			for (final @NotNull GostSchuelerklausurTermin skt : skts)
				if (schuelerklausurGetByIdOrException(skt.idSchuelerklausur).idSchueler == idSchueler)
					return skt;
		return null;
	}

	/**
	 * Liefert die GostSchuelerklausur-Objekte zur übergebenen Kursklausur-ID
	 *
	 * @param idKursklausur die ID der Kursklausur
	 *
	 * @return die GostSchuelerklausur-Objekte
	 */
	public @NotNull List<GostSchuelerklausur> schuelerklausurGetMengeByKursklausurid(final long idKursklausur) {
		final List<GostSchuelerklausur> listSks = _schuelerklausurmenge_by_idKursklausur.get(idKursklausur);
		return (listSks == null) ? new ArrayList<>() : listSks;
	}

	/**
	 * Liefert das Lehrerkürzel zu einer übergebenen Kursklausur.
	 *
	 * @param k die Kursklausur
	 *
	 * @return das Lehrerkürzel
	 */
	public @NotNull String kursLehrerKuerzelByKursklausur(final @NotNull GostKursklausur k) {
		final @NotNull KursDaten kurs = getKursByKursklausur(k);
		final LehrerListeEintrag lehrer = getLehrerMap().get(kurs.lehrer);
		if (lehrer == null)
			throw new DeveloperNotificationException("Lehrer mit ID " + kurs.lehrer + " nicht in LehrerMap vorhanden.");
		return lehrer.kuerzel;
	}

	/**
	 * Liefert den KursDaten aus dem KursManager zu einer übergebenen Kursklausur.
	 *
	 * @param k die Kursklausur
	 *
	 * @return den KursDaten
	 */
	public @NotNull KursDaten getKursByKursklausur(final @NotNull GostKursklausur k) {
		final KursDaten kurs = getKursManager().get(k.idKurs);
		if (kurs == null)
			throw new DeveloperNotificationException("Kurs mit ID " + k.idKurs + " nicht in KursManager vorhanden.");
		return kurs;
	}

	/**
	 * Liefert das GostFach aus dem GostFaecherManager zu einer übergebenen
	 * Kursklausur.
	 *
	 * @param k die Kursklausur
	 *
	 * @return das GostFach
	 */
	public @NotNull GostFach getGostFachByKursklausur(final @NotNull GostKursklausur k) {
		final GostFach fach = getFaecherManager().get(vorgabeByKursklausur(k).idFach);
		if (fach == null)
			throw new DeveloperNotificationException(
					"Fach mit ID " + vorgabeByKursklausur(k).idFach + " nicht in GostFaecherManager vorhanden.");
		return fach;
	}

	/**
	 * Liefert das Lehrerkürzel zu einer übergebenen Kursklausur.
	 *
	 * @param k die Kursklausur
	 *
	 * @return das Lehrerkürzel
	 */
	public @NotNull List<Integer> kursSchieneByKursklausur(final @NotNull GostKursklausur k) {
		return getKursByKursklausur(k).schienen;
	}

	/**
	 * Liefert die Kurzbezeichnung des Kurses zu einer übergebenen Kursklausur.
	 *
	 * @param k die Kursklausur
	 *
	 * @return die Kurzbezeichnung
	 */
	public @NotNull String kursKurzbezeichnungByKursklausur(final @NotNull GostKursklausur k) {
		return getKursByKursklausur(k).kuerzel;
	}

	/**
	 * Liefert die Kurzbezeichnung des Kurses zu einer übergebenen Kursklausur.
	 *
	 * @param k die Kursklausur
	 *
	 * @return die Kurzbezeichnung
	 */
	public @NotNull String kursKurzbezeichnungBySchuelerklausur(final @NotNull GostSchuelerklausur k) {
		return getKursByKursklausur(kursklausurBySchuelerklausur(k)).kuerzel;
	}

	/**
	 * Liefert die Anzahl aller Schüler im Kurs zu einer übergebenen Kursklausur.
	 *
	 * @param k die Kursklausur
	 *
	 * @return die Schüleranzahl
	 */
	public int kursAnzahlSchuelerGesamtByKursklausur(final @NotNull GostKursklausur k) {
		return getKursByKursklausur(k).schueler.size();
	}

	/**
	 * Liefert die Anzahl der Klausurscheiber im Kurs zu einer übergebenen
	 * Kursklausur.
	 *
	 * @param k die Kursklausur
	 *
	 * @return die Schüleranzahl
	 */
	public int kursAnzahlKlausurschreiberByKursklausur(final @NotNull GostKursklausur k) {
		final List<GostSchuelerklausur> liste = _schuelerklausurmenge_by_idKursklausur.get(k.id);
		return (liste == null) ? 0 : liste.size();
	}

	/**
	 * Liefert das Kürzel zur Anzeige des Faches zu einer übergebenen Kursklausur.
	 * Falls kein Anzeigekürzel gesetzt ist, wird das interne Kürzel zurückgegeben.
	 *
	 * @param k die Kursklausur
	 *
	 * @return die Kurzbezeichnung
	 */
	public @NotNull String fachKuerzelAnzeigeByKursklausur(final @NotNull GostKursklausur k) {
		final GostFach fach = getGostFachByKursklausur(k);
		return (fach.kuerzelAnzeige != null) ? fach.kuerzelAnzeige : fach.kuerzel;
	}

	/**
	 * Liefert das Kürzel zur Anzeige des Faches zu einer übergebenen Kursklausur.
	 * Falls kein Anzeigekürzel gesetzt ist, wird das interne Kürzel zurückgegeben.
	 *
	 * @param k die Kursklausur
	 *
	 * @return die Kurzbezeichnung
	 */
	public @NotNull String fachKuerzelByKursklausur(final @NotNull GostKursklausur k) {
		return getGostFachByKursklausur(k).kuerzel;
	}

	/**
	 * Liefert die Hintergrundfarbe zur übergebenen Kursklausur.
	 *
	 * @param k die Kursklausur
	 *
	 * @return die Hintergrundfarbe
	 */
	public @NotNull String fachBgColorByKursklausur(final @NotNull GostKursklausur k) {
		return ZulaessigesFach.getByKuerzelASD(fachKuerzelByKursklausur(k)).getHMTLFarbeRGBA(1.0);
	}

	/**
	 * Liefert den Vorgänger-Schülerklausurtermin zu einer Schülerklausur, also den
	 * versäumte Schülerklausurtermin
	 *
	 * @param skt der Schülerklausurtermin, dessen Vorgänger gesucht wird
	 *
	 * @return den Vorgänger-Schülerklausurtermin
	 */
	public GostSchuelerklausurTermin schuelerklausurterminVorgaengerBySchuelerklausurtermin(final @NotNull GostSchuelerklausurTermin skt) {
		final @NotNull List<GostSchuelerklausurTermin> alleTermine =
				DeveloperNotificationException.ifMapGetIsNull(_schuelerklausurterminmenge_by_idSchuelerklausur, skt.idSchuelerklausur);
		for (final @NotNull GostSchuelerklausurTermin skAktuell : alleTermine)
			if (skAktuell.folgeNr == (skt.folgeNr - 1))
				return skAktuell;
		return null;
	}

	/**
	 * Prüft, ob eine Kursklausur externe Schüler enthält
	 *
	 * @param k die zu prüfende Kursklausur
	 *
	 * @return true, falls externe Schüler enthalten sind, sonst false
	 */
	public boolean kursklausurMitExternenS(final @NotNull GostKursklausur k) {
		final List<GostSchuelerklausur> listSks = _schuelerklausurmenge_by_idKursklausur.get(k.id);
		if (listSks != null)
			for (final @NotNull GostSchuelerklausur sk : listSks)
				if (DeveloperNotificationException.ifMapGetIsNull(getSchuelerMap(), sk.idSchueler).externeSchulNr != null)
					return true;
		return false;
	}

	/**
	 * Prüft, ob ein Klausurtermin Schülerklausurtermine anderer Jahrgangsstufen
	 * enthält
	 *
	 * @param t der zu prüfende Klausurtermin
	 *
	 * @return true, falls Schülerklausurtermine anderer Jgst. enthalten sind
	 */
	public boolean terminMitAnderenJgst(final @NotNull GostKlausurtermin t) {
		final List<GostSchuelerklausurTermin> listSkts = _schuelerklausurterminmenge_by_idTermin.get(t.id);
		if (listSkts != null)
			for (final @NotNull GostSchuelerklausurTermin skt : listSkts)
				if (vorgabeBySchuelerklausurTermin(skt).abiJahrgang != t.abijahr)
					return true;
		return false;
	}

	/**
	 * Gibt das Datum des Vorgängertermins zum Übergebenen Schülerklausurtermin
	 * zurück.
	 *
	 * @param skt der Schülerklausurtermin
	 *
	 * @return das Datum als String oder null
	 */
	public String datumSchuelerklausurVorgaenger(final @NotNull GostSchuelerklausurTermin skt) {
		final GostSchuelerklausurTermin vorgaengerSkt = schuelerklausurterminVorgaengerBySchuelerklausurtermin(skt);
		if (vorgaengerSkt == null)
			throw new DeveloperNotificationException("Kein Vorgängertermin zu Schülerklausurtermin gefunden.");
		final GostKlausurtermin termin = terminOrNullBySchuelerklausurTermin(vorgaengerSkt);
		return (termin == null) ? null : termin.datum;
	}

	/**
	 * Erstellt ein GostKlausurenUpdate-Objekt für den API-Call, das beim
	 * übergebenen Gost-Klausurtermin die Nachschreiberzulassung entfernt und ggf.
	 * schon zugewiesene Schülerklausurtermine aus diesem entfernt.
	 *
	 * @param termin der Schülerklausurtermin
	 *
	 * @return das GostKlausurenUpdate-Objekt mit den zu patchenden
	 *         GostSchuelerklausurTermin-Objekten und dem Gost-Klausurtermin
	 */
	public @NotNull GostKlausurenUpdate patchKlausurterminNachschreiberZuglassenFalse(final @NotNull GostKlausurtermin termin) {
		final GostKlausurenUpdate update = new GostKlausurenUpdate();
		update.listKlausurtermineNachschreiberZugelassenFalse.add(termin.id);
		for (final @NotNull GostSchuelerklausurTermin skt : schuelerklausurterminNtByTermin(termin))
			update.listSchuelerklausurTermineRemoveIdTermin.add(skt.id);
		return update;
	}

	/**
	 * Führt alle Attribut-Patches aller Objekte im übergeben Update-Objekt im
	 * Manager durch.
	 *
	 * @param update das GostKlausurenUpdate-Objekt mit den zu patchenden
	 *
	 */
	public void updateExecute(final @NotNull GostKlausurenUpdate update) {
		for (final long sktId : update.listSchuelerklausurTermineRemoveIdTermin) {
			final @NotNull GostSchuelerklausurTermin skt = schuelerklausurterminGetByIdOrException(sktId);
			skt.idTermin = null;
			schuelerklausurterminPatchAttributes(skt);
		}
		for (final long ktId : update.listKlausurtermineNachschreiberZugelassenFalse) {
			final @NotNull GostKlausurtermin kt = terminGetByIdOrException(ktId);
			kt.nachschreiberZugelassen = false;
			terminPatchAttributes(kt);
		}
	}

}
