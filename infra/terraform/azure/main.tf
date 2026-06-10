locals {
  resource_prefix = "${var.project_name}-${var.environment}"
  common_tags = merge(
    {
      application = var.project_name
      environment = var.environment
      managed-by  = "terraform"
    },
    var.tags
  )
}

resource "random_string" "resource_suffix" {
  length  = 6
  special = false
  upper   = false
}

resource "random_password" "database" {
  length           = 24
  special          = true
  override_special = "!#%*-_=+"
}

resource "azurerm_resource_group" "platform" {
  name     = "rg-${local.resource_prefix}"
  location = var.location
  tags     = local.common_tags
}

resource "azurerm_log_analytics_workspace" "platform" {
  name                = "log-${local.resource_prefix}"
  location            = azurerm_resource_group.platform.location
  resource_group_name = azurerm_resource_group.platform.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
  tags                = local.common_tags
}

resource "azurerm_container_registry" "platform" {
  name                = replace("acr${var.project_name}${var.environment}${random_string.resource_suffix.result}", "-", "")
  resource_group_name = azurerm_resource_group.platform.name
  location            = azurerm_resource_group.platform.location
  sku                 = "Basic"
  admin_enabled       = false
  tags                = local.common_tags
}

resource "azurerm_user_assigned_identity" "container_apps" {
  name                = "id-${local.resource_prefix}-apps"
  resource_group_name = azurerm_resource_group.platform.name
  location            = azurerm_resource_group.platform.location
  tags                = local.common_tags
}

resource "azurerm_role_assignment" "acr_pull" {
  scope                = azurerm_container_registry.platform.id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_user_assigned_identity.container_apps.principal_id
}

resource "azurerm_container_app_environment" "platform" {
  name                       = "cae-${local.resource_prefix}"
  resource_group_name        = azurerm_resource_group.platform.name
  location                   = azurerm_resource_group.platform.location
  log_analytics_workspace_id = azurerm_log_analytics_workspace.platform.id
  tags                       = local.common_tags
}

resource "azurerm_postgresql_flexible_server" "platform" {
  name                          = "psql-${local.resource_prefix}-${random_string.resource_suffix.result}"
  resource_group_name           = azurerm_resource_group.platform.name
  location                      = azurerm_resource_group.platform.location
  version                       = "16"
  administrator_login           = "servicedeskadmin"
  administrator_password        = random_password.database.result
  public_network_access_enabled = true
  sku_name                      = "B_Standard_B1ms"
  storage_mb                    = 32768
  backup_retention_days         = 7
  auto_grow_enabled             = true
  tags                          = local.common_tags

  authentication {
    active_directory_auth_enabled = false
    password_auth_enabled         = true
  }
}

# Azure's 0.0.0.0 rule allows connections from Azure services, including Container Apps.
resource "azurerm_postgresql_flexible_server_firewall_rule" "azure_services" {
  name             = "AllowAzureServices"
  server_id        = azurerm_postgresql_flexible_server.platform.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

resource "azurerm_postgresql_flexible_server_database" "tickets" {
  name      = "tickets"
  server_id = azurerm_postgresql_flexible_server.platform.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

resource "azurerm_postgresql_flexible_server_database" "technicians" {
  name      = "technicians"
  server_id = azurerm_postgresql_flexible_server.platform.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}
