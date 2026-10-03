# NOVA DARTS — native Android game

Ein eigenständiges Dart-Handyspiel für Android: **Kotlin + Jetpack Compose**, ohne WebView, Flutter, React Native, Backend oder Netzwerkfunktionen. Der Spielname, die Benutzeroberfläche, die Dartboard-Grafik, das Scoring und die Ausrüstung sind für dieses Projekt neu umgesetzt.

## Spielumfang

- **501 Duell:** Double-out-Regeln, drei Darts pro Aufnahme, Bust-Reset und ein lokaler NOVA-CPU-Gegner.
- **Around the Clock:** Segmente 1 bis 20 der Reihe nach treffen, danach das Bull.
- **Bull Rush:** 30 Darts, freie Zielwahl und ein lokal gespeicherter Highscore.
- **Board zum Anvisieren:** Board antippen, Trefferfeld und Punkte ansehen, dann den Dart bewusst werfen.
- **Armory mit 20 Teilen:** Körper/Barrel, Schaft, Flight, Spitze und Zubehör — darunter Tungsten-Barrels, Carbon-Schäfte, Holo-Flights, Titan-Spitzen, Oche-Matte, Board-Lichtring und Grip-Wrap.
- **Offline-Progression:** Münzen werden nur durch abgeschlossene Partien verdient; Teile werden lokal freigeschaltet und ausgerüstet. Kein Echtgeld-Shop.
- **Lokale Statistik:** Level, Siege, 3-Dart-Average, Bullseyes, Routen und persönliche Rekorde.

Die Set-Werte sind kosmetische Loadout-Informationen. Die Trefferwertung ist skillbasiert und bleibt unabhängig von freigeschalteten Teilen.

## Offline- und Datenschutz-Design

Die App fordert **keine `INTERNET`-Berechtigung** an. Matchlogik, CPU, Fortschritt und Speicherung laufen vollständig auf dem Gerät. Es gibt keine Anmeldung, Werbung, Analytics, Telemetrie, Cloud-Synchronisierung, Bluetooth- oder Kamera-Integration. Der lokale Spielstand liegt in Android `SharedPreferences`.

## Bauen und starten

Voraussetzungen: Android Studio mit Android SDK 35 und JDK 17; bei manuellem Build Gradle 8.9.

1. In Android Studio **diesen Ordner** `examples/nova-darts` als Projekt öffnen.
2. Gradle synchronisieren lassen und ein Android-Gerät oder einen Emulator mit Android 7.0 (API 24) oder neuer auswählen.
3. `app` starten, oder im Ordner `examples/nova-darts` mit einer installierten Gradle-Version ausführen:

   ```bash
   gradle :app:assembleDebug
   gradle :app:testDebugUnitTest
   ```

Das Debug-APK liegt nach erfolgreichem Build unter `app/build/outputs/apk/debug/app-debug.apk`. Gradle muss beim **Build** die Android-/Compose-Abhängigkeiten auflösen; die installierte App selbst benötigt keine Netzwerkverbindung.

## Open-Source-Recherche und Auswahl

Bei der Recherche auf GitHub wurden diese Projekte geprüft:

- [`vgupta98/compose-game`](https://github.com/vgupta98/compose-game) ist ein kleines Jetpack-Compose-2D-Game-Engine-Projekt unter Apache-2.0. Für ein präzises Dartboard werden seine Physik- und Objektmodelle nicht benötigt; zusätzlich würde eine JitPack-Abhängigkeit die Build-Kette erweitern. Es wurde daher **kein fremder Engine-Code übernommen**.
- [`BySplashGm/OpenDart`](https://github.com/BySplashGm/OpenDart) ist ein MIT-lizenziertes Offline-Dart-Scoring-Projekt, verwendet aber Flutter und erfüllt damit nicht die Vorgabe „rein natives Android“.
- [`OpenDartboard/OpenDartboard`](https://github.com/OpenDartboard/OpenDartboard) ist ein GPL-3.0-FOSS-Werkzeug rund um automatische Stahlspitzen-Boards und Hardware-Scoring. Das ist eine interessante Hardware-Referenz, aber keine benötigte Laufzeitintegration für ein reines Touch-Spiel.
- Die AndroidX-/Jetpack-Compose-Basis ist Apache-2.0-lizenziert. Die UI nutzt Compose Canvas und Standard-Android-Speicherung; es werden keine Drittanbieter-Spielassets eingebunden.

Die Repository-Lizenz im übergeordneten Projekt ist MIT. Vor einer Veröffentlichung im Play Store sollten zusätzlich Name/Marke, App-Icon, Datenschutzangaben und die dann aktuellen Google-Play-Anforderungen geprüft werden.
