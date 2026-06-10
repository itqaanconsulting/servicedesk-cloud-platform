variable "project_name" {
  description = "Short name used as a prefix for Azure resources."
  type        = string
  default     = "servicedesk"

  validation {
    condition     = can(regex("^[a-z0-9-]{3,18}$", var.project_name))
    error_message = "project_name must contain 3-18 lowercase letters, numbers or hyphens."
  }
}

variable "environment" {
  description = "Deployment environment name."
  type        = string
  default     = "demo"

  validation {
    condition     = can(regex("^[a-z0-9]{2,8}$", var.environment))
    error_message = "environment must contain 2-8 lowercase letters or numbers."
  }
}

variable "location" {
  description = "Azure region for all resources."
  type        = string
  default     = "westeurope"
}

variable "deploy_services" {
  description = "Deploy the Container Apps after their images are available in ACR."
  type        = bool
  default     = false
}

variable "image_tag" {
  description = "Container image tag deployed to all services."
  type        = string
  default     = "latest"
}

variable "tags" {
  description = "Additional tags applied to Azure resources."
  type        = map(string)
  default     = {}
}
