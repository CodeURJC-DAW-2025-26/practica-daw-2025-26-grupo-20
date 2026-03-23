#!/bin/bash

# Set error handling for Bash
set -e

# Check if ImageName argument is provided
if [ -z "$1" ]; then
  echo "Usage: ./create_image.sh <image_name>"
  exit 1
fi

ImageName="$1"
Tag="latest"

echo "========================================="
echo "Construyendo la imagen: ${ImageName}:${Tag}"
echo "========================================="

# Execute the docker build command
docker build -f docker/Dockerfile -t "${ImageName}:${Tag}" .

echo "Imagen construida correctamente: ${ImageName}:${Tag}"
