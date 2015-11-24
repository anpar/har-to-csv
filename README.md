Outil pour convertir les fichiers .har fournit par Mozilla Firefox en .csv.

Les commandes suivantes sont réalisables une fois positionné dans le répertoire src. Le fichier .har de source doit s'appeler "in.har". Les fichiers .csv résultants se trouve dans le répertoire "/out".

Pour compiler :
`make compile`

Pour convertir le fichier in.har en un fichier csv complet (cookies non compris dans le fichier):
`make all`

Pour obtenir uniquement la liste des domaines :
`make compile`

Pour obtenir uniquement la liste des cookies :
`make cookies`

# Crédit
Inspiré de https://github.com/spcgh0st/HarTools.

Antoine Paris et Momin Charles
