resource "helm_release" "external_secrets" {
  name             = "external-secrets"
  repository       = "https://charts.external-secrets.io"
  chart            = "external-secrets"
  namespace        = "external-secrets-system"
  create_namespace = true

  depends_on = [aws_eks_node_group.main]
}

resource "kubernetes_namespace" "ecommerce" {
  metadata {
    name = "e-commerce"
  }

  depends_on = [aws_eks_node_group.main]
}

resource "kubernetes_service_account" "external_secrets_sa" {
  metadata {
    name      = "external-secrets-sa"
    namespace = kubernetes_namespace.ecommerce.metadata[0].name
    annotations = {
      "eks.amazonaws.com/role-arn" = aws_iam_role.external_secrets.arn
    }
  }

  depends_on = [helm_release.external_secrets]
}

resource "kubectl_manifest" "secret_store" {
  yaml_body = yamlencode({
    apiVersion = "external-secrets.io/v1"
    kind       = "SecretStore"
    metadata = {
      name      = "aws-ssm"
      namespace = kubernetes_namespace.ecommerce.metadata[0].name
    }
    spec = {
      provider = {
        aws = {
          service = "ParameterStore"
          region  = "ap-northeast-2"
          auth = {
            jwt = {
              serviceAccountRef = {
                name = kubernetes_service_account.external_secrets_sa.metadata[0].name
              }
            }
          }
        }
      }
    }
  })

  depends_on = [kubernetes_service_account.external_secrets_sa, helm_release.external_secrets]
}

resource "kubectl_manifest" "external_secret" {
  yaml_body = yamlencode({
    apiVersion = "external-secrets.io/v1"
    kind       = "ExternalSecret"
    metadata = {
      name      = "db-credentials"
      namespace = kubernetes_namespace.ecommerce.metadata[0].name
    }
    spec = {
      refreshInterval = "1h"
      secretStoreRef = {
        name = "aws-ssm"
        kind = "SecretStore"
      }
      target = {
        name = "db-credentials"
      }
      dataFrom = [
        {
          find = {
            path = "/e-commerce/prod/"
            name = { regexp = ".*" }
          }
          rewrite = [
            {
              regexp = {
                source = "/e-commerce/prod/(.*)"
                target = "$1"
              }
            }
          ]
        }
      ]
    }
  })

  depends_on = [kubectl_manifest.secret_store]
}