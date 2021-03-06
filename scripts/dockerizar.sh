#!/bin/bash
# ------------------------------------------------------------------
# [jseluy]  SCRIPT PARA DOCKER
#           Creación de la imagen con nombre y versión automáticos.
#           Subida de la imagen al Registry de Carsa.
# ------------------------------------------------------------------

VERSION=3.0.0
USAGE="Uso:\tUbicarse en la carpeta scripts (cd scripts/)\n\n\t./dockerizar-ms.sh"

# ------------------------------------------------------------------

regla='^[a-z0-9_-]{5,}:v[0-9]+\.[0-9]+\.?[0-9]*$'
registry=10.4.101.105:5000
rutaDockerfile=../target

# ------------------------------------------------------------------

# Busco el war del proyecto
nombre="$(find $rutaDockerfile -name '*.war')"

# Aíslo el nombre+versión #
# Elimino la parte inicial (ruta)
nombre="${nombre##*/}"
# Elimino la parte final (extensión)
nombre="${nombre%.war}"
# Elimino la parte final (SNAPSHOT)
nombre="${nombre%-SNAPSHOT}"

# Separo la versión
version="${nombre##*-}"

#Separo el nombre
nombre="${nombre%-*}"

# Armo el nombre de la imagen

imagen=$nombre:v$version


if [ ! -f $rutaDockerfile/Dockerfile ]; then
	printf "\n>>>\tNo se encuentra el archivo « Dockerfile ».\n\tEjecute mvn clean package y vuelva a correr el script.\n"
	printf "\nEl proceso fue abortado.\n\n"
	exit 1

fi

# Solicito al usuario que valide el nombre del proyecto
echo    # move to a new line
read -p "Indique si es correcto ( $imagen ) [s/n]: " -n 1 respuesta
echo    # move to a new line

if [[ ! $respuesta =~ ^[SsYy]$ ]]
then

    read -p "Ingrese el nombre para la nueva imagen: " imagen

    # Verifico que el nombre tenga una estructura determinada
    while [[ ! $imagen =~ $regla ]]; do
        printf "\n>>>\tDebe especificar un nombre, con el siguiente formato:\n\n"
        printf "\tNOMBRE_REPO:vMAYOR.MINOR[.REV]\n\n"
        read -p "Ingrese el nombre para la nueva imagen: " imagen
    done

fi

echo    # move to a new line
docker build -t $imagen $rutaDockerfile

# Compruebo si se ejecutó docker build SIN ERRORES
return_val=$?

if [ ! "$return_val" -eq 0 ]; then
    printf "\ndocker build\tERROR\n\n"
	exit 1
else
    printf "\ndocker build\tOK\n\n"
fi


# Se ingresa al regisrty de Carsa
docker login -u dockeruser -p dockerpass $registry

# Se procede con la creación del TAG
docker tag $imagen $registry/$imagen
printf "\nTAG creado en\t$registry\n\n"

# Se manda el TAG al registry de Carsa
docker push $registry/$imagen

echo    # move to a new line

docker logout $registry

# FIN