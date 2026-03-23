param()

$ErrorActionPreference = "Stop"

$envFile = ".\docker\.env"

if (Test-Path $envFile) {
    Write-Host "Cargando variables desde .env..."

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

$BaseImageName = "mokaf"
$Tag = "latest"
$BaseImage = "${BaseImageName}:${Tag}"

$baseImageExists = docker images -q $BaseImage 2>$null

if (-not $baseImageExists) {
    Write-Host "La imagen base $BaseImage no existe."
    Write-Host "Ejecuta primero: .\docker\create_image.ps1 mokaf"
    exit 1
}

foreach ($member in $membersList) {
    $TargetImage = "${member}/${BaseImageName}:${Tag}"

    Write-Host "-----------------------------------------"
    Write-Host "Procesando usuario: $member"
    Write-Host "Imagen destino: $TargetImage"

    docker tag $BaseImage $TargetImage
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error al etiquetar $TargetImage"
        continue
    }

    $safeMemberName = $member -replace '[^a-zA-Z0-9]', '_'
    $varName = "DOCKER_PASS_$safeMemberName"
    $memberPass = [System.Environment]::GetEnvironmentVariable($varName, "Process")

    if (-not $memberPass) {
        Write-Host "No hay contraseña para $member en la variable $varName"
        continue
    }

    $memberPass | docker login -u $member --password-stdin
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error login $member"
        continue
    }

    docker push $TargetImage
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Subido correctamente: $TargetImage"
    }
    else {
        Write-Host "Error al subir $TargetImage"
    }

    docker logout | Out-Null
}

Write-Host "-----------------------------------------"
Write-Host "Publicación completada"