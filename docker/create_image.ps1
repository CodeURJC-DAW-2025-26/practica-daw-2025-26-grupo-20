param(
    [string]$ImageName
)

$ErrorActionPreference = "Stop"

if (-not $ImageName) {
    Write-Host "Uso: .\docker\create_image.ps1 nombre_imagen"
    exit 1
}

$Tag = "latest"

Write-Host "========================================="
Write-Host "Construyendo la imagen: ${ImageName}:${Tag}"
Write-Host "========================================="

docker build -f docker/Dockerfile -t "${ImageName}:${Tag}" .

Write-Host "Imagen construida correctamente: ${ImageName}:${Tag}"