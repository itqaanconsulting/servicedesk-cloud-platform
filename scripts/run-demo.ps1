param(
    [string]$TicketServiceUrl = "http://localhost:8181",
    [string]$TechnicianServiceUrl = "http://localhost:8082",
    [string]$NotificationServiceUrl = "http://localhost:8083"
)

$ErrorActionPreference = "Stop"

function Wait-ForService {
    param(
        [string]$Name,
        [string]$HealthUrl
    )

    for ($attempt = 1; $attempt -le 30; $attempt++) {
        try {
            $health = Invoke-RestMethod -Uri $HealthUrl -TimeoutSec 2
            if ($health.status -eq "UP") {
                Write-Host "$Name is ready."
                return
            }
        }
        catch {
        }

        Start-Sleep -Seconds 2
    }

    throw "$Name did not become ready at $HealthUrl."
}

Wait-ForService -Name "Ticket Service" -HealthUrl "$TicketServiceUrl/actuator/health"
Wait-ForService -Name "Technician Service" -HealthUrl "$TechnicianServiceUrl/actuator/health"
Wait-ForService -Name "Notification Service" -HealthUrl "$NotificationServiceUrl/actuator/health"

$demoId = Get-Date -Format "yyyyMMddHHmmss"

$technicianBody = @{
    name   = "Samira de Vries"
    email  = "samira.$demoId@example.com"
    skills = @("NETWORKING", "JAVA", "KUBERNETES")
} | ConvertTo-Json

$technician = Invoke-RestMethod `
    -Method Post `
    -Uri "$TechnicianServiceUrl/api/technicians" `
    -ContentType "application/json" `
    -Body $technicianBody

$ticketBody = @{
    title          = "VPN access unavailable"
    description    = "Remote employee cannot connect to the corporate VPN."
    requesterEmail = "alex.$demoId@example.com"
    priority       = "HIGH"
    requiredSkill  = "NETWORKING"
} | ConvertTo-Json

$ticket = Invoke-RestMethod `
    -Method Post `
    -Uri "$TicketServiceUrl/api/tickets" `
    -ContentType "application/json" `
    -Body $ticketBody

$assignedTicket = Invoke-RestMethod `
    -Method Post `
    -Uri "$TicketServiceUrl/api/tickets/$($ticket.id)/assignment"

$reservedTechnician = Invoke-RestMethod `
    -Uri "$TechnicianServiceUrl/api/technicians/$($technician.id)"

$notification = Invoke-RestMethod -Uri "$NotificationServiceUrl/api/notifications" |
    Where-Object { $_.ticketId -eq $assignedTicket.id } |
    Select-Object -First 1

Write-Host ""
Write-Host "Demo completed."
Write-Host ""

[pscustomobject]@{
    TicketId               = $assignedTicket.id
    TicketStatus           = $assignedTicket.status
    AssignmentStatus       = $assignedTicket.assignmentStatus
    RequiredSkill          = $assignedTicket.requiredSkill
    AssignedTechnician     = $assignedTicket.assignedTechnicianName
    TechnicianAvailability = $reservedTechnician.availability
    NotificationStatus     = $notification.status
    NotificationRecipient  = $notification.recipient
} | Format-List

Write-Host "Grafana dashboard: http://localhost:3000/d/servicedesk-overview"
Write-Host "Grafana login: admin / admin"
