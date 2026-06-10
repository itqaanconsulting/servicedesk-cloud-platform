param(
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot

Push-Location $projectRoot
try {
    if (-not $SkipBuild) {
        & mvn.cmd clean package
        if ($LASTEXITCODE -ne 0) {
            throw "Maven build failed with exit code $LASTEXITCODE."
        }

        & docker compose build ticket-service technician-service notification-service
        if ($LASTEXITCODE -ne 0) {
            throw "Docker image build failed with exit code $LASTEXITCODE."
        }
    }

    & kubectl apply -k k8s/base
    if ($LASTEXITCODE -ne 0) {
        throw "Kubernetes deployment failed with exit code $LASTEXITCODE."
    }

    & kubectl rollout status statefulset/ticket-database -n servicedesk --timeout=180s
    & kubectl rollout status statefulset/technician-database -n servicedesk --timeout=180s
    & kubectl rollout status deployment/technician-service -n servicedesk --timeout=180s
    & kubectl rollout status deployment/ticket-service -n servicedesk --timeout=180s
    & kubectl rollout status deployment/notification-service -n servicedesk --timeout=180s

    Write-Host "ServiceDesk is running in namespace 'servicedesk'."
    Write-Host "Run: kubectl port-forward service/ticket-service 8281:8081 -n servicedesk"
}
finally {
    Pop-Location
}
