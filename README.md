# SAE 2.03 - IUT - docker-sae203 - MateZone

## Site de la présentation
- [Site de Présentation](https://Dono0530.github.io/S2.03-MateZone/)

## Instructions pour lancer le serveur MateZone

- Vérifiez si docker est installé :
```shell
docker --version
```

- Cloner le dépot :
 ```shell
git clone git@github.com:Dono0530/S2.03-MateZone
```

- Accéder au dépot :
```shell
cd S2.03-MateZone
```

- Construisez l'image de Dockerfile avec docker build : 
```shell
docker build -t MateZoneServer .
```

- Pour lancer le serveur web :
```shell
docker run -d -p 16326:80 MateZoneServer
```


## Instruction pour vérifier l´existence du conteneur


- Pour vérifier que le conteneur a été créé et est en cours de fonctionnement :
```shell
docker ps
```

## Instruction pour supprimer le conteneur ou l´image
# Pour supprimer une image, il faut supprimer tous les conteneurs qui la contiennent

- Il faut d’abord marquer dans le terminal, pour arrêter le conteneur  :
```shell
docker stop <nom-du-conteneur-choisie>
```
- Et ensuite, pour supprimer le conteneur existant :
```
docker rm <nom-du-conteneur-choisie>
```

- Ensuite pour supprimer l´image, il faut faire cette commande :
```shell
docker image rm <nom-de-l´image>
```

## Instructions pour lancer le client MateZone

- Cloner le dépot :
 ```shell
git clone git@github.com:Dono0530/S2.03-MateZone
```

- Accéder au dépot :
```shell
cd S2.03-MateZone
```

- Lancer le client :
 ```shell
 ./run.sh
```

## 👨‍💻 Crédits – Développeurs :

Donovan P.

Joshua H.

Lucas L.

Hugo V.
