#!/bin/bash

echo "Building WarpHome Plugin..."

# Prüfen ob Maven installiert ist
if ! command -v mvn &> /dev/null
then
    echo "Maven ist nicht installiert. Bitte installiere Maven zuerst."
    exit 1
fi

# JAVA_HOME setzen (für JDK 17)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Prüfen ob JAVA_HOME existiert
if [ ! -d "$JAVA_HOME" ]; then
    echo "JDK nicht gefunden unter $JAVA_HOME"
    echo "Bitte installiere das JDK: apt install -y default-jdk"
    exit 1
fi

echo "Using JAVA_HOME: $JAVA_HOME"

# Plugin bauen
mvn clean package

# Prüfen ob Build erfolgreich war
if [ -f "target/WarpHome-1.0.0.jar" ]; then
    echo ""
    echo "✓ Build erfolgreich!"
    echo ""
    echo "Plugin wurde erstellt: target/WarpHome-1.0.0.jar"
    echo ""
    echo "Um das Plugin zu installieren, kopiere die JAR-Datei in den plugins-Ordner:"
    echo "cp target/WarpHome-1.0.0.jar ../WarpHome.jar"
    echo ""
    echo "Dann starte den Server neu oder nutze: /reload"
else
    echo "✗ Build fehlgeschlagen!"
    exit 1
fi

