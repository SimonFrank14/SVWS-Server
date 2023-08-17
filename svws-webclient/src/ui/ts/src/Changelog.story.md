---
group: 'design-system'
title: 'Changelog'
iconColor: '#ccc'
id: 'changelog'
icon: 'carbon:bookmark'
---

# Changelog

Chronologische Auflistung von Änderungen an den Komponenten Fix/Feature/Breaking Change:

## ab 0.6.18: 

* TextInput: Fix, wenn `type="number"` ist, dann wird beim Klicken der rauf/runter-Pfeile kein `emit` ausgelöst, weil `document.ativeElement` nicht das Input-Feld ist. Es muss erst durch ein `focus()` gesetzt werden.