NOTA: Os manisfestos estão armazenados no diretório "manifestos" na raiz
--------------------------------------------------------------------------------------

1 - Primeiro criar o cluster brasil-paralelo:
Obs: Estou usando o kind
criei o manifesto yaml com o nome kind-bp-config.yaml com o conteúdo abaixo:

kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
name: brasil-paralelo
nodes:
- role: control-plane
- role: worker

cd manifestos
kind create cluster --config cluster/kind-bp-config.yaml

Conferir com o kind:
kind get clusters

Conferir com o kubectl:
kubectl cluster-info --context kind-brasil-paralelo
Verificar os nodes do cluster
kubectl get nodes
--------------------------------------------------------------------------------------
2 - Dockerização do serviço de autenticação:

Criação do Dockerfile:

#####################################################################
# Dockerfile para o serviço de autenticação
FROM python:3.7-slim

WORKDIR /auth

# Instalar dependências de compilação por conta do pacote vibora
RUN apt-get update && apt-get install -y \
    gcc \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

COPY Pipfile Pipfile
RUN pip install pipenv
RUN pipenv install

COPY . .

EXPOSE 8037

CMD ["pipenv", "run", "python", "src/server.py"]
#####################################################################

Criada a imagem, testar com:
docker run -p 8037:8037 auth-service:latest