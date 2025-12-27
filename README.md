# 🏠 WarpHome - Minecraft Plugin

## ✨ Features

- **Warp Points erstellen**: Erstelle Warp Points über die GUI
- **Fancy Teleportation**: Teleportiere dich mit einem 5-Sekunden-Timer und coolen Effekten
- **Blauer Partikel-Kreis**: Ein rotierender blauer Kreis umgibt dich während der Teleportation
- **Spiraleffekte**: Aufsteigende Partikel-Spirale für extra Fancy-Faktor
- **Sounds**: Verschiedene Sounds für jede Aktion
- **ActionBar Countdown**: Live-Countdown in der Action Bar
- **Bewegungserkennung**: Teleportation wird abgebrochen, wenn du dich bewegst
- **GUI Management**: Vollständige Verwaltung über ein schönes GUI
- **Icon Customization**: Wähle Icons und Varianten für deine Warp Points
- **Bed Color Selection**: Wähle die Farbe für deine Warp Points

## 📋 Befehle

| Befehl | Beschreibung | Permission |
|--------|--------------|------------|
| `/home` | Öffnet das Warp Collection GUI | `warphome.home` |

## 🔐 Permissions

Alle Permissions sind standardmäßig für alle Spieler aktiviert:

- `warphome.home` - Erlaubt die Nutzung des Warp Collection GUIs
- `warphome.*` - Gibt alle Permissions

## ⚙️ Konfiguration

Die `config.yml` erlaubt folgende Einstellungen:

```yaml
# Teleportations-Verzögerung in Sekunden
teleport-delay: 5

# Maximale Anzahl an Warp Points pro Spieler (0 = unbegrenzt)
max-homes: 0

# Soll die Teleportation abgebrochen werden, wenn sich der Spieler bewegt?
cancel-on-move: true
```

Alle Nachrichten können ebenfalls in der `config.yml` angepasst werden!

## 🚀 Installation

1. Lade die `WarpHome.jar` herunter
2. Platziere sie in den `plugins` Ordner deines Servers
3. Starte den Server neu
4. Fertig! 🎉

## 📦 Build

Um das Plugin selbst zu bauen:

```bash
cd HomePlugin
./build.sh
```

Die fertige JAR-Datei findest du in: `target/WarpHome-1.0.0.jar`

Um das Plugin zu installieren:
```bash
cp target/WarpHome-1.0.0.jar ../WarpHome.jar
```

Dann starte den Server neu oder nutze: `/reload`

## 📁 Projektstruktur

```
HomePlugin/
├── src/
│   └── main/
│       ├── java/
│       │   └── de/
│       │       └── minecraft/
│       │           └── warp/
│       │               ├── WarpHome.java           # Haupt-Plugin-Klasse
│       │               ├── commands/
│       │               │   └── HomeCommand.java     # /home Befehl
│       │               ├── gui/
│       │               │   ├── MainGUI.java       # Haupt-GUI
│       │               │   ├── ColorSelector.java # Betten-Farben Auswahl
│       │               │   ├── IconSelector.java  # Icon Auswahl
│       │               │   ├── VariantSelector.java # Varianten Auswahl
│       │               │   └── DeleteConfirmation.java # Lösch-Bestätigung
│       │               ├── manager/
│       │               │   ├── HomeManager.java    # Home-Verwaltung
│       │               │   ├── TeleportManager.java # Teleportations-Logik
│       │               │   └── SettingsManager.java # Settings-Verwaltung
│       │               └── model/
│       │                   ├── Home.java           # Home-Modell
│       │                   └── PlayerSettings.java # Spieler-Settings
│       └── resources/
│           ├── plugin.yml                          # Plugin-Metadaten
│           └── config.yml                          # Konfiguration
├── pom.xml                                         # Maven-Konfiguration
├── build.sh                                        # Build-Script
└── README.md                                       # Diese Datei
```

## 🎨 GUI Features

- **Warp Collection**: Übersicht aller Warp Points
- **Icon Selection**: Wähle aus verschiedenen Icons (Anvil, Totem, Sword, Bell, Sapling, etc.)
- **Variant Selection**: Wähle Varianten für Icons (z.B. verschiedene Steine, Schwerter, etc.)
- **Bed Color Selection**: Wähle die Farbe für deine Warp Points
- **Delete Confirmation**: Sicherheitsabfrage beim Löschen

## 📝 Changelog

### Version 1.0.0
- Initial Release
- GUI-basierte Warp Point Verwaltung
- Teleportation mit visuellen Effekten
- Icon und Varianten-Auswahl
- Bed Color Selection

## 👨‍💻 Entwicklung

Das Plugin wurde mit Java 17 und der Spigot/Paper API entwickelt.

## 📄 Lizenz

Dieses Plugin ist für den privaten und kommerziellen Gebrauch frei verfügbar.

## 🙏 Credits

Entwickelt für Minecraft Server mit Spigot/Paper API.
(ja ich war zu faul und habe den readme per ChatGPT erstellt)
