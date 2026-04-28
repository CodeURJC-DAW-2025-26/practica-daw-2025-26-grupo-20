#!/bin/bash

# Set error handling for Bash
set -e

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Assuming the script is in 'docker/' directory, project root is one level up
PROJECT_ROOT="$( dirname "$SCRIPT_DIR" )"

# Go to project root
cd "$PROJECT_ROOT"

# Check if ImageName argument is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <image_name>"
  echo "Example: $0 mokaf"
  exit 1
fi

ImageName="$1"
Tag="latest"

echo "========================================="
echo "Construyendo la imagen: ${ImageName}:${Tag}"
echo "========================================="

# Execute the docker build command (relative to PROJECT_ROOT)
docker build -f docker/Dockerfile -t "${ImageName}:${Tag}" .

echo "Imagen construida correctamente: ${ImageName}:${Tag}"
