locals {
  ticket_database_url     = "jdbc:postgresql://${azurerm_postgresql_flexible_server.platform.fqdn}:5432/${azurerm_postgresql_flexible_server_database.tickets.name}?sslmode=require"
  technician_database_url = "jdbc:postgresql://${azurerm_postgresql_flexible_server.platform.fqdn}:5432/${azurerm_postgresql_flexible_server_database.technicians.name}?sslmode=require"
}

resource "azurerm_container_app" "technician" {
  count = var.deploy_services ? 1 : 0

  name                         = "ca-${local.resource_prefix}-technician"
  container_app_environment_id = azurerm_container_app_environment.platform.id
  resource_group_name          = azurerm_resource_group.platform.name
  revision_mode                = "Single"
  tags                         = local.common_tags

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.container_apps.id]
  }

  registry {
    server   = azurerm_container_registry.platform.login_server
    identity = azurerm_user_assigned_identity.container_apps.id
  }

  secret {
    name  = "database-password"
    value = random_password.database.result
  }

  ingress {
    external_enabled = false
    target_port      = 8082
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 2

    http_scale_rule {
      name                = "http-concurrency"
      concurrent_requests = 50
    }

    container {
      name   = "technician-service"
      image  = "${azurerm_container_registry.platform.login_server}/technician-service:${var.image_tag}"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SERVER_PORT"
        value = "8082"
      }
      env {
        name  = "DB_URL"
        value = local.technician_database_url
      }
      env {
        name  = "DB_USERNAME"
        value = azurerm_postgresql_flexible_server.platform.administrator_login
      }
      env {
        name        = "DB_PASSWORD"
        secret_name = "database-password"
      }
      env {
        name  = "MANAGEMENT_TRACING_ENABLED"
        value = "false"
      }

      startup_probe {
        transport               = "HTTP"
        port                    = 8082
        path                    = "/actuator/health/liveness"
        interval_seconds        = 5
        failure_count_threshold = 30
      }

      readiness_probe {
        transport        = "HTTP"
        port             = 8082
        path             = "/actuator/health/readiness"
        interval_seconds = 10
      }

      liveness_probe {
        transport        = "HTTP"
        port             = 8082
        path             = "/actuator/health/liveness"
        interval_seconds = 15
      }
    }
  }

  depends_on = [azurerm_role_assignment.acr_pull]
}

resource "azurerm_container_app" "notification" {
  count = var.deploy_services ? 1 : 0

  name                         = "ca-${local.resource_prefix}-notification"
  container_app_environment_id = azurerm_container_app_environment.platform.id
  resource_group_name          = azurerm_resource_group.platform.name
  revision_mode                = "Single"
  tags                         = local.common_tags

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.container_apps.id]
  }

  registry {
    server   = azurerm_container_registry.platform.login_server
    identity = azurerm_user_assigned_identity.container_apps.id
  }

  ingress {
    external_enabled = false
    target_port      = 8083
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 2

    http_scale_rule {
      name                = "http-concurrency"
      concurrent_requests = 50
    }

    container {
      name   = "notification-service"
      image  = "${azurerm_container_registry.platform.login_server}/notification-service:${var.image_tag}"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SERVER_PORT"
        value = "8083"
      }
      env {
        name  = "MANAGEMENT_TRACING_ENABLED"
        value = "false"
      }

      startup_probe {
        transport               = "HTTP"
        port                    = 8083
        path                    = "/actuator/health/liveness"
        interval_seconds        = 5
        failure_count_threshold = 20
      }

      readiness_probe {
        transport        = "HTTP"
        port             = 8083
        path             = "/actuator/health/readiness"
        interval_seconds = 10
      }

      liveness_probe {
        transport        = "HTTP"
        port             = 8083
        path             = "/actuator/health/liveness"
        interval_seconds = 15
      }
    }
  }

  depends_on = [azurerm_role_assignment.acr_pull]
}

resource "azurerm_container_app" "ticket" {
  count = var.deploy_services ? 1 : 0

  name                         = "ca-${local.resource_prefix}-ticket"
  container_app_environment_id = azurerm_container_app_environment.platform.id
  resource_group_name          = azurerm_resource_group.platform.name
  revision_mode                = "Single"
  tags                         = local.common_tags

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.container_apps.id]
  }

  registry {
    server   = azurerm_container_registry.platform.login_server
    identity = azurerm_user_assigned_identity.container_apps.id
  }

  secret {
    name  = "database-password"
    value = random_password.database.result
  }

  ingress {
    external_enabled = true
    target_port      = 8081
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 3

    http_scale_rule {
      name                = "http-concurrency"
      concurrent_requests = 50
    }

    container {
      name   = "ticket-service"
      image  = "${azurerm_container_registry.platform.login_server}/ticket-service:${var.image_tag}"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SERVER_PORT"
        value = "8081"
      }
      env {
        name  = "DB_URL"
        value = local.ticket_database_url
      }
      env {
        name  = "DB_USERNAME"
        value = azurerm_postgresql_flexible_server.platform.administrator_login
      }
      env {
        name        = "DB_PASSWORD"
        secret_name = "database-password"
      }
      env {
        name  = "TECHNICIAN_SERVICE_URL"
        value = "https://${azurerm_container_app.technician[0].latest_revision_fqdn}"
      }
      env {
        name  = "MANAGEMENT_TRACING_ENABLED"
        value = "false"
      }

      startup_probe {
        transport               = "HTTP"
        port                    = 8081
        path                    = "/actuator/health/liveness"
        interval_seconds        = 5
        failure_count_threshold = 30
      }

      readiness_probe {
        transport        = "HTTP"
        port             = 8081
        path             = "/actuator/health/readiness"
        interval_seconds = 10
      }

      liveness_probe {
        transport        = "HTTP"
        port             = 8081
        path             = "/actuator/health/liveness"
        interval_seconds = 15
      }
    }
  }

  depends_on = [azurerm_role_assignment.acr_pull]
}
