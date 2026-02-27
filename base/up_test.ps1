# Script PowerShell multiplataforma para ejecutar tests con Docker Compose
# Funciona en: Windows, macOS, Linux
# Requisito: PowerShell 7+ (pwsh)

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "  Ejecutando Tests con Docker Compose" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Variables
$COMPOSE_FILE = "docker-compose.test.yml"
$PROJECT_NAME = "up_test"
$CONTAINER_NAME = "api"
$LOCAL_REPORT_DIR = "build\reports\tests\test"

# Detectar el SO
$IsWindows = $PSVersionTable.Platform -eq "Win32NT" -or $PSVersionTable.PSVersion.Major -lt 6

# Paso 1: Ejecutar docker-compose
Write-Host "[1] Iniciando tests..." -ForegroundColor Green
docker-compose -p $PROJECT_NAME -f $COMPOSE_FILE up --build --abort-on-container-exit

Write-Host ""
Write-Host "[2] Obteniendo ID del contenedor..." -ForegroundColor Cyan

# Paso 2: Obtener ID del contenedor (el contenedor debería seguir existiendo aunque esté parado)
$container_id = docker-compose -p $PROJECT_NAME -f $COMPOSE_FILE ps -a -q $CONTAINER_NAME 2>$null

if ($container_id) {
    Write-Host "OK Contenedor encontrado: $container_id" -ForegroundColor Green
    Write-Host "[3] Preparando carpeta local..." -ForegroundColor Cyan

    # Crear directorio local si no existe
    if (Test-Path $LOCAL_REPORT_DIR) {
        Remove-Item -Path $LOCAL_REPORT_DIR -Recurse -Force -ErrorAction SilentlyContinue
    }
    New-Item -ItemType Directory -Force -Path $LOCAL_REPORT_DIR | Out-Null
    Write-Host "OK Carpeta local preparada: $LOCAL_REPORT_DIR" -ForegroundColor Green

    Write-Host "[4] Copiando reportes desde el contenedor..." -ForegroundColor Cyan

    # Paso 3: Copiar reportes del contenedor
    # Primero verificar qué hay en el contenedor
    Write-Host "   Verificando contenido del contenedor..." -ForegroundColor DarkGray
    $docker_result = & docker exec $container_id find /app/build/reports/tests -type f -name "*.html" 2>$null

    if ($docker_result) {
        Write-Host "   Archivos encontrados en contenedor:" -ForegroundColor DarkGray
        Write-Host $docker_result -ForegroundColor DarkGray
    }

    # Intentar copiar los reportes completos
    try {
        Write-Host "   Ejecutando: docker cp $container_id`:/app/build/reports/tests\. $(Get-Location)\$LOCAL_REPORT_DIR" -ForegroundColor DarkGray

        & docker cp "$container_id`:/app/build/reports/tests/." "$LOCAL_REPORT_DIR"

        Write-Host "OK Reportes copiados exitosamente" -ForegroundColor Green

        # Verificar que existen los archivos
        $files = Get-ChildItem -Path $LOCAL_REPORT_DIR -Recurse -ErrorAction SilentlyContinue
        if ($files) {
            Write-Host "OK Se copiaron $($files.Count) archivos" -ForegroundColor Green
            Write-Host "   Contenido:" -ForegroundColor DarkGray
            $files | ForEach-Object { Write-Host "   - $($_.FullName.Replace((Get-Location).Path, ''))" -ForegroundColor DarkGray }
        }
        else {
            Write-Host "WARNING No se encontraron archivos copiados" -ForegroundColor Yellow
        }
    }
    catch {
        Write-Host "ERROR Excepción al copiar reportes: $_" -ForegroundColor Red
    }
}
else {
    Write-Host "WARNING No se encontro el contenedor" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "[5] Deteniendo contenedores..." -ForegroundColor Cyan

# Paso 4: Bajar los contenedores
docker-compose -p $PROJECT_NAME -f $COMPOSE_FILE down

# Esperar un momento
Start-Sleep -Seconds 2

Write-Host ""
Write-Host "[6] Verificando reporte en la máquina local..." -ForegroundColor Cyan

# Paso 5: Abrir el reporte
$REPORT_PATH = "build\reports\tests\test\index.html"
$REPORT_DIR = "build\reports\tests\test"

if (Test-Path $REPORT_PATH) {
    Write-Host "OK Reporte encontrado: $REPORT_PATH" -ForegroundColor Green
    Write-Host ""
    Write-Host "====================================" -ForegroundColor Green
    Write-Host "     Tests ejecutados exitosamente" -ForegroundColor Green
    Write-Host "  Abriendo reporte en el navegador..." -ForegroundColor Green
    Write-Host "====================================" -ForegroundColor Green
    Write-Host ""

    if ($IsWindows) {
        Write-Host "Abriendo: $((Get-Item $REPORT_PATH).FullName)" -ForegroundColor Cyan
        Start-Process $REPORT_PATH
    }
    else {
        Write-Host "Abriendo: $REPORT_PATH" -ForegroundColor Cyan
    }
}
elseif (Test-Path $REPORT_DIR) {
    Write-Host "OK Carpeta de reportes encontrada" -ForegroundColor Green
    Write-Host ""
    Write-Host "====================================" -ForegroundColor Cyan
    Write-Host "   Contenido de la carpeta:" -ForegroundColor Cyan
    Write-Host "====================================" -ForegroundColor Cyan
    Write-Host ""

    Get-ChildItem -Path $REPORT_DIR -Recurse | ForEach-Object {
        Write-Host "   - $($_.FullName.Replace((Get-Location).Path, ''))" -ForegroundColor DarkGray
    }

    Write-Host ""
    Write-Host "   Abriendo carpeta..." -ForegroundColor Cyan

    if ($IsWindows) {
        Invoke-Item $REPORT_DIR
    }
}
else {
    Write-Host "WARNING Reporte no encontrado" -ForegroundColor Yellow
    Write-Host "   Ruta esperada: $(Get-Location)\$REPORT_PATH" -ForegroundColor Yellow

    if (Test-Path "build\reports") {
        Write-Host "   Contenido de build\reports:" -ForegroundColor Yellow
        Get-ChildItem -Path "build\reports" -Recurse -ErrorAction SilentlyContinue | ForEach-Object {
            Write-Host "   - $($_.FullName.Replace((Get-Location).Path, ''))" -ForegroundColor DarkGray
        }
    }
    else {
        Write-Host "   ERROR: La carpeta build\reports no existe" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "OK Proceso completado" -ForegroundColor Green



