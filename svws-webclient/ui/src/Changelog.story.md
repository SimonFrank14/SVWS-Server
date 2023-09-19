---
group: 'design-system'
title: 'Changelog'
iconColor: '#ccc'
id: 'changelog'
icon: 'carbon:bookmark'
---

# Changelog

Chronologische Auflistung von Änderungen an den Komponenten Fix/Feature/Breaking Change:

## ab 0.7
* DataTable ist nun auf generics umgestellt, die Items bestimmen den Typ.
* Multiselect, TextInput und TextArea geben nun auch den Wert bei @blur/@change raus. Darüber hinaus verwalten sie nun den internen State.

## ab 0.6.18: 

* TextInput: Fix, wenn `type="number"` ist, dann wird beim Klicken der rauf/runter-Pfeile kein `emit` ausgelöst, weil `document.ativeElement` nicht das Input-Feld ist. Es muss erst durch ein `focus()` gesetzt werden.
* Multiselect kann bei autocomplete nun auch per Tab das erste Element auswählen.
* emits werden bei TextInput und TextArea nur noch bei `blur`-Events ausgegeben.
* Drag und Drop wird entfernt