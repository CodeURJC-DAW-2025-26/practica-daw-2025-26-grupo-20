#!/bin/bash

# Este script construye la imagen Docker oficial de la aplicacion

IMAGE_NAME="mokaf"
TAG="latest"

echo "========================================="
echo "☕ Construyendo la imagen oficial: $IMAGE_NAME"
echo "========================================="
docker build -t $IMAGE_NAME:$TAG .

if [ $? -ne 0 ]; then
  echo "❌ Error al construir la imagen de Docker. Abortando."
  exit 1
fi

echo ""
echo "✅ Imagen construida localmente con éxito."
echo ""
