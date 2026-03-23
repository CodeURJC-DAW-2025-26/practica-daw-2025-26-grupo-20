param()

$ErrorActionPreference = "Stop"

$envFile = ".\docker\.env"
if (-not (Test-Path $envFile) -and (Test-Path ".\.env")) {
    $envFile = ".\.env"
}

$composeFile = "docker/docker-compose.yml"
$artifactName = "mokaf-compose"
$tag = "latest"
$artifactType = "application/vnd.docker.compose.project.v1+yaml"
$registry = "docker.io"

if (-not (Get-Command oras -ErrorAction SilentlyContinue)) {
    Write-Host "Error: oras no está instalado."
    Write-Host "Instálalo desde: https://oras.land/docs/installation"
    exit 1
}

if (-not (Test-Path $composeFile)) {
    Write-Host "No se encuentra el fichero $composeFile"
    exit 1
}

if (Test-Path $envFile) {
    Write-Host "Cargando variables desde $envFile..."

    Get-Content $envFile | ForEach-Object {
        $line = $_.Trim()

        if (-not $line) { return }
        if ($line.StartsWith("#")) { return }

        $parts = $line -split "=", 2
        if ($parts.Count -eq 2) {
            $name = $parts[0].Trim()
            $value = $parts[1].Trim()
            [System.Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
}

$membersRaw = $env:DOCKER_MEMBERS
if (-not $membersRaw) {
    Write-Host "DOCKER_MEMBERS no definido."
    exit 1
}

$membersList = $membersRaw -split "," | ForEach-Object { $_.Trim() } | Where-Object { $_ }

foreach ($member in $membersList) {
    $safeMemberName = $member -replace '[^a-zA-Z0-9]', '_'
    $varName = "DOCKER_PASS_$safeMemberName"
    $memberPass = [System.Environment]::GetEnvironmentVariable($varName, "Process")

    if (-not $memberPass) {
        Write-Host "No hay contraseña para $member en la variable $varName"
        continue
    }

    $targetRef = "${registry}/${member}/${artifactName}:${tag}"

    Write-Host "-----------------------------------------"
    Write-Host "Procesando usuario: $member"
    Write-Host "Artifact destino: $targetRef"

    $memberPass | oras login $registry -u $member --password-stdin
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error de login para $member"
        continue
    }

    oras push $targetRef --artifact-type $artifactType "${composeFile}:application/yaml"
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Subido correctamente: $targetRef"
    }
    else {
        Write-Host "Error al subir $targetRef"
    }

    oras logout $registry | Out-Null
}

Write-Host "-----------------------------------------"
Write-Host "Publicación del OCI artifact completada"