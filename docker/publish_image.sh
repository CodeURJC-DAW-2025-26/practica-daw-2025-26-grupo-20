#!/bin/bash

# Ensure the script exits immediately if a command exits with a non-zero status.
set -e

ENV_FILE=".env"

# --- Load environment variables from .env file ---
if [ -f "$ENV_FILE" ]; then
    echo "Cargando variables desde .env..."
    # Read file line by line and export variables
    while IFS= read -r line || [[ -n "$line" ]]; do
        # Trim whitespace
        line=$(echo "$line" | xargs)
        # Skip empty lines and comments
        if [[ -z "$line" || "$line" =~ ^# ]]; then
            continue
        fi
        # Split by '=' and export
        if [[ "$line" =~ ^(.+)=(.+)$ ]]; then
            var_name="${BASH_REMATCH[1]}"
            var_value="${BASH_REMATCH[2]}"
            # Trim whitespace from name and value
            var_name=$(echo "$var_name" | xargs)
            var_value=$(echo "$var_value" | xargs)
            export "$var_name"="$var_value"
        fi
    done < "$ENV_FILE"
fi

# --- Check DOCKER_MEMBERS environment variable ---
if [ -z "$DOCKER_MEMBERS" ]; then
    echo "DOCKER_MEMBERS no definido."
    exit 1
fi

# Split DOCKER_MEMBERS into an array and filter out empty entries
IFS=',' read -r -a membersList <<< "$DOCKER_MEMBERS"
# Filter out any empty strings that might result from leading/trailing commas or double commas
membersList=(${membersList[*]})

BaseImageName="mokaf"
Tag="latest"
BaseImage="${BaseImageName}:${Tag}"

# --- Check if base image exists locally ---
# Use docker images -q to get only the ID, redirect stderr to null
baseImageExists=$(docker images -q "$BaseImage" 2>/dev/null)

if [ -z "$baseImageExists" ]; then
    echo "La imagen base $BaseImage no existe."
    echo "Ejecuta primero: ./docker/create_image.sh mokaf"
    exit 1
fi

# --- Process each member ---
for member in "${membersList[@]}"; do
    # Trim whitespace from member name (in case it was like " member," or ",member ")
    member=$(echo "$member" | xargs)

    TargetImage="${member}/${BaseImageName}:${Tag}"

    echo "-----------------------------------------"
    echo "Procesando usuario: $member"
    echo "Imagen destino: $TargetImage"

    # Tag the image
    if ! docker tag "$BaseImage" "$TargetImage"; then
        echo "Error al etiquetar $TargetImage"
        continue # Move to the next member
    fi

    # Construct dynamic variable name for password
    # Replace non-alphanumeric characters with underscore for variable name safety
    safeMemberName=$(echo "$member" | sed 's/[^a-zA-Z0-9]/_/g')
    varName="DOCKER_PASS_${safeMemberName}"

    # Retrieve the password from environment variables
    memberPass=$(eval echo "\$$varName") # Use eval to resolve the dynamic variable name

    if [ -z "$memberPass" ]; then
        echo "No hay contraseña para $member en la variable $varName"
        continue # Move to the next member
    fi

    # Login to Docker registry
    # Using echo and piping is a common way to pass passwords to docker login --password-stdin
    echo "$memberPass" | docker login -u "$member" --password-stdin
    if [ $? -ne 0 ]; then
        echo "Error login $member"
        continue # Move to the next member
    fi

    # Push the image
    if docker push "$TargetImage"; then
        echo "Subido correctamente: $TargetImage"
    else
        echo "Error al subir $TargetImage"
        # We can choose to continue after push failure if needed, or stop if it's critical
        # For now, it continues to the next member. If you want to stop, remove 'continue' or use 'exit 1'
    fi

    # Logout from Docker registry
    docker logout > /dev/null 2>&1
done

echo "-----------------------------------------"
echo "Publicación completada"
