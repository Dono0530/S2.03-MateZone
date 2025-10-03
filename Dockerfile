# Utiliser l'image debian officielle comme image parent
FROM debian:latest

# Installer les services nécessaires (Apache, Java et PHP)
RUN apt-get update && \
    apt-get install -y apache2 php libapache2-mod-php openjdk-17-jdk && \
    apt-get clean

# Définir les variables d'environnement pour Java
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH
ENV DISPLAY=host.docker.internal:0.0

# Copier les fichiers de l'hôte vers l'image
COPY ./app/MetaZone_code/data /var/www/html/data
COPY ./app/MetaZone_code/compile.list /app/MetaZone_code/
COPY ./app/MetaZone_code/ /app/MetaZone_code/

RUN chown -R www-data:www-data /var/www/html/data
RUN chmod -R 777 /var/www/html/data

# Configurer Apache pour écouter sur les ports 80 et 8080
RUN grep -qxF 'Listen 80' /etc/apache2/ports.conf || echo 'Listen 80' >> /etc/apache2/ports.conf && \
    grep -qxF 'Listen 8080' /etc/apache2/ports.conf || echo 'Listen 8080' >> /etc/apache2/ports.conf

# Exposer les ports nécessaires
EXPOSE 80 8080

# Définir le répertoire de travail
WORKDIR /app/MetaZone_code

# test tmp
RUN ls -lR /app/MetaZone_code

# Compiler les fichiers Java
RUN javac -encoding UTF-8 -d ./classes @compile.list

# Démarrer Apache en mode foreground pour que le conteneur reste actif
CMD ["apachectl", "-D", "FOREGROUND"]
