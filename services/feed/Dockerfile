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