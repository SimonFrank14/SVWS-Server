import { JavaObject } from '../../../java/lang/JavaObject';
import { ConcurrentModificationException } from '../../../java/util/ConcurrentModificationException';
import { MinHeap, cast_de_svws_nrw_core_adt_tree_MinHeap } from '../../../core/adt/tree/MinHeap';
import type { JavaIterator } from '../../../java/util/JavaIterator';
import { Class } from '../../../java/lang/Class';
import { NoSuchElementException } from '../../../java/util/NoSuchElementException';
import { UnsupportedOperationException } from '../../../java/lang/UnsupportedOperationException';

export class MinHeapIterator<T> extends JavaObject implements JavaIterator<T> {

	/**
	 * Referenz auf das Array der Elemente im Minimum Heap.
	 */
	private readonly _elements : Array<T | null>;

	/**
	 * Die aktuelle Position beim Durchlaufen des Arrays der den Minimum Heap enthält.
	 */
	private _current : number = 0;

	/**
	 * Die aktuelle Größe des Minimum Heaps, also die Anzahl der enthaltenen Elemente.
	 */
	private readonly _heap : MinHeap<T>;

	/**
	 * Die Anzahl der Modifikationen, die bei dem {@link MinHeap} zur Zeit des Erzeugen des
	 *  Iterators gemacht wurden. Dieser Wert muss mit dem bei der {@link MinHeap} übereinstimmen.
	 *  Ansonsten wird eine {@link ConcurrentModificationException} generiert.
	 */
	private readonly _expModCount : number;


	/**
	 * Erstellt einen neuen Iterator für die Klasse MinHeap
	 *
	 * @param elem   die Elemente des Minimum Heaps
	 * @param heap   eine Referenz zum Minimum Heap, um auf parallel erfolgende modifizierende Zugriffe reagieren zu können.
	 */
	constructor(elem : Array<T | null>, heap : MinHeap<T>) {
		super();
		this._current = -1;
		this._elements = elem;
		this._heap = heap;
		this._expModCount = heap.getModCount();
	}

	public hasNext() : boolean {
		if (this._heap.getModCount() !== this._expModCount)
			throw new ConcurrentModificationException()
		return ((this._current + 1) < this._heap.size());
	}

	public next() : T {
		if (!this.hasNext())
			throw new NoSuchElementException("Keine weiteren Elemente vorhanden. Eine Prüfung mit hasNext() vorab ist empfehlenswert.")
		const elem : T | null = this._elements[++this._current];
		if (elem === null)
			throw new NoSuchElementException("Interner Fehler in der Datenstruktur.")
		return elem;
	}

	public remove() : void {
		throw new UnsupportedOperationException("remove")
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.adt.tree.MinHeapIterator';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['java.util.Iterator', 'de.svws_nrw.core.adt.tree.MinHeapIterator'].includes(name);
	}

	public static class = new Class<MinHeapIterator<any>>('de.svws_nrw.core.adt.tree.MinHeapIterator');

}

export function cast_de_svws_nrw_core_adt_tree_MinHeapIterator<T>(obj : unknown) : MinHeapIterator<T> {
	return obj as MinHeapIterator<T>;
}
