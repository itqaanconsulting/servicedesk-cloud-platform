output "resource_group_name" {
  description = "Name of the Azure resource group."
  value       = azurerm_resource_group.platform.name
}

output "container_registry_name" {
  description = "Name of the Azure Container Registry."
  value       = azurerm_container_registry.platform.name
}

output "container_registry_login_server" {
  description = "Login server used for image tags."
  value       = azurerm_container_registry.platform.login_server
}

output "ticket_service_url" {
  description = "Public Ticket Service URL when service deployment is enabled."
  value       = var.deploy_services ? "https://${azurerm_container_app.ticket[0].latest_revision_fqdn}" : null
}

output "database_server_fqdn" {
  description = "PostgreSQL server hostname."
  value       = azurerm_postgresql_flexible_server.platform.fqdn
}
