#!/bin/bash

# Este script construye la imagen Docker y la sube al Docker Hub de cada miembro del grupo.
# Para utilizarlo de forma automática, debes sustituir "usuario1", "usuario2", etc.
# por los verdaderos nombres de usuario de Docker Hub de tu grupo.

# Intentar cargar variables de entorno desde un archivo .env si existe
if [ -f .env ]; then
  echo "Cargando variables desde el archivo .env..."
  export $(grep -v '^#' .env | xargs)
fi

# Leer los nombres de usuario de la variable DOCKER_MEMBERS (separados por espacios o comas)
# Ejemplo: export DOCKER_MEMBERS="prz27,usuario2"
MEMBERS_LIST=$(echo "${DOCKER_MEMBERS:-}" | tr ',' ' ')

if [ -z "$MEMBERS_LIST" ]; then
  echo "❌ Error: La variable de entorno DOCKER_MEMBERS no está definida o está vacía."
  echo "Uso: DOCKER_MEMBERS='prz27 usuario2' ./docker-publish.sh"
  exit 1
fi

IMAGE_NAME="mokaf"
TAG="latest"

echo "========================================="
echo "🚀 Iniciando publicación de la imagen: $IMAGE_NAME:$TAG"
echo "Asegúrate de haber ejecutado './docker-build.sh' previamente."
echo "========================================="

# Iteramos sobre cada miembro del equipo
for MEMBER in $MEMBERS_LIST
do
  echo "========================================="
  echo "☁️ Preparando para subir a la cuenta de Docker Hub: $MEMBER"
  echo "========================================="
  
  # Etiquetar la imagen local con el nombre del registro del usuario
  docker tag $IMAGE_NAME:$TAG $MEMBER/$IMAGE_NAME:$TAG
  
  # Construir el nombre de la variable que debe contener la contraseña
  # Reemplazamos los caracteres no alfanuméricos por "_" para variables válidas
  SAFE_MEMBER_NAME=${MEMBER//[^a-zA-Z0-9]/_}
  VAR_NAME="DOCKER_PASS_${SAFE_MEMBER_NAME}"
  
  # Extraer la contraseña usando referencia indirecta en Bash
  MEMBER_PASS="${!VAR_NAME}"
  
  if [ -z "$MEMBER_PASS" ]; then
    echo "⚠️ Advertencia: No se ha encontrado la variable de entorno $VAR_NAME para el usuario $MEMBER."
    echo "Saltando la subida para este usuario..."
    echo ""
    continue
  fi
  
  echo "Realizando login automático con la contraseña obtenida del entorno ($VAR_NAME)..."
  echo "$MEMBER_PASS" | docker login -u "$MEMBER" --password-stdin
  
  if [ $? -eq 0 ]; then
    echo "Subiendo imagen: $MEMBER/$IMAGE_NAME:$TAG"
    docker push $MEMBER/$IMAGE_NAME:$TAG
    
    if [ $? -eq 0 ]; then
      echo "✅ Imagen subida a $MEMBER/$IMAGE_NAME:$TAG exitosamente."
    else
      echo "❌ Error al subir la imagen para $MEMBER."
    fi
  else
    echo "❌ Fallo en el inicio de sesión para el usuario $MEMBER. Se omite la subida."
  fi
  
  # Cerrar la sesión del usuario actual para no interferir con el siguiente
  docker logout
  echo ""
done

echo "🎉 Proceso de subida finalizado para todos los miembros definidos en el entorno."
