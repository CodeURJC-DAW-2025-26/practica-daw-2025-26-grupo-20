#!/bin/bash

set -e
export PATH="$HOME/.local/bin:$PATH"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

ENV_FILE="$SCRIPT_DIR/.env"
if [ ! -f "$ENV_FILE" ] && [ -f "$ROOT_DIR/.env" ]; then
    ENV_FILE="$ROOT_DIR/.env"
fi

COMPOSE_FILE="docker-compose.yml"
ARTIFACT_NAME="mokaf-compose"
TAG="latest"
ARTIFACT_TYPE="application/vnd.docker.compose.project.v1+yaml"
REGISTRY="docker.io"

if ! command -v oras >/dev/null 2>&1; then
    echo "Error: oras no está instalado."
    echo "Instálalo desde: https://oras.land/docs/installation"
    exit 1
fi

if [ ! -f "$SCRIPT_DIR/$COMPOSE_FILE" ]; then
    echo "No se encuentra el fichero $COMPOSE_FILE"
    exit 1
fi

cd "$SCRIPT_DIR"

if [ -f "$ENV_FILE" ]; then
    echo "Cargando variables desde $ENV_FILE..."
    while IFS= read -r line || [[ -n "$line" ]]; do
        line=$(echo "$line" | xargs)
        if [[ -z "$line" || "$line" =~ ^# ]]; then
            continue
        fi
        if [[ "$line" =~ ^(.+)=(.+)$ ]]; then
            var_name="${BASH_REMATCH[1]}"
            var_value="${BASH_REMATCH[2]}"
            var_name=$(echo "$var_name" | xargs)
            var_value=$(echo "$var_value" | xargs)
            export "$var_name"="$var_value"
        fi
    done < "$ENV_FILE"
fi

if [ -z "$DOCKER_MEMBERS" ]; then
    echo "DOCKER_MEMBERS no definido."
    exit 1
fi

IFS=',' read -r -a membersList <<< "$DOCKER_MEMBERS"
membersList=(${membersList[*]})

for member in "${membersList[@]}"; do
    member=$(echo "$member" | xargs)

    if [ -z "$member" ]; then
        continue
    fi

    safeMemberName=$(echo "$member" | sed 's/[^a-zA-Z0-9]/_/g')
    varName="DOCKER_PASS_${safeMemberName}"
    memberPass=$(eval echo "\$$varName")

    if [ -z "$memberPass" ]; then
        echo "No hay contraseña para $member en la variable $varName"
        continue
    fi

    targetRef="${REGISTRY}/${member}/${ARTIFACT_NAME}:${TAG}"

    echo "-----------------------------------------"
    echo "Procesando usuario: $member"
    echo "Artifact destino: $targetRef"

    echo "$memberPass" | oras login "$REGISTRY" -u "$member" --password-stdin

    oras push "$targetRef" \
      --artifact-type "$ARTIFACT_TYPE" \
      "$COMPOSE_FILE:application/yaml"

    echo "Subido correctamente: $targetRef"

    oras logout "$REGISTRY" >/dev/null 2>&1 || true
done

echo "-----------------------------------------"
echo "Publicación del OCI artifact completada"