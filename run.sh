#!/bin/bash

#Pos bon rep
cd app/MetaZone_code || exit 1

# Créer un dossier /classes pour .class (pas crééer si déjà (-p))
mkdir -p .classes

#Compile et envoie dans ./classes
javac -d .classes @compile.list

#Place dans classes puis compile le ctrl.
java -cp .classes MateZone.Controleur
