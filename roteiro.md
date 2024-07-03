NOTA: Os manisfestos estão armazenados no diretório "manifestos" na raiz
--------------------------------------------------------------------------------------

1 - Usando o minikube
Iniciar o Minikube utilizando o driver Docker:
minikube start --driver=docker

cd manifestos

Conferir com o kubectl:
kubectl cluster-info
Verificar os nodes do cluster
kubectl get nodes
--------------------------------------------------------------------------------------
2 - Dockerização do serviço de autenticação:

2.1 - Criação do Dockerfile:

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

2.2 - Construir a imagem Docker:
docker build -t auth-service:latest .

2.3 Criada a imagem, testar com:
docker run -p 8037:8037 auth-service:latest
NOTA: carregar a imagem no cluster kind
kind load docker-image auth-service:latest --name brasil-paralelo
--------------------------------------------------------------------------------------
3 - Dockerização do serviço de feed em javascrip usando o nodejs
3.1 - Criação do Dockerfile:

#####################################################################
# Dockerfile para o serviço de feed (conteúdo)
# Dockerfile para o serviço de conteúdo
FROM node:14

WORKDIR /feed

COPY package*.json ./
RUN npm install
RUN npm run release

COPY . .

EXPOSE 3000

# Definir a variável de ambiente e o comando de execução
ENV USERINFO_URL=http://localhost:8037/api/userinfo

CMD ["npm", "run", "start"]
#####################################################################

3.2 - Construir a imagem docker
docker build -t feed-service:latest .

3.3 Criada a imagem, testar com:
docker run -p 3000:3000 feed-service:latest
NOTA: carregar a imagem no cluster kind
kind load docker-image feed-service:latest --name brasil-paralelo
-------------------------------------------------------------------------
4 - criar a rede e docker-compose para os serviços se enxergarem entre si
4.1 - criar a rede BP:
docker network create rede_bp

4.2 - criação do docker-compose.yml:
Tive que criar para testar a dockerização das duas aplicações e verificar se elas respondem. O uso do docker-compose facilita a subida dos serviços para eles se enxergarem.
4.2.1 - criação do Dockerfile.auth e Dockerfile.feed. São praticamente iguais aos originais exceto o apontamento para os diretórios auth e feed respectivamente já que estão juntos no mesmo diretório do docker-compose.yml
-------------------------------------------------------------------------

5 - Criação dos manifestos kubernetes, dos deployments, services e ingress
auth-deployment.yaml
auth-service.yaml
feed-deployment.yaml
feed-service.yaml
ingress.yml

6 - Aplicar os manifestos no cluster brasil-paralelo
kubectl apply -f manifestos/auth-deployment.yaml
kubectl apply -f manifestos/auth-service.yaml
kubectl apply -f manifestos/feed-deployment.yaml
kubectl apply -f manifestos/feed-service.yaml

7 - Aplicar o ingress
kubectl apply -f manifestos/ingress.yaml

8 - Testar os serviços
curl -H "Authorization: Bearer 66ec51ac-72ea-479d-8b5f-d99eede929f0" -v http://feed-bp-host/feed/patriota
curl -H "Authorization: Bearer 66ec51ac-72ea-479d-8b5f-d99eede929f0" -v http://feed-bp-host/feed/premium
OBS: Para facilitar, criei as entradas no hosts:
echo "$(minikube ip) auth-bp-host" | sudo tee -a /etc/hosts
echo "$(minikube ip) feed-bp-host" | sudo tee -a /etc/hosts
