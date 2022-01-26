# api-heroi

This project was generated with [Spring Boot](https://spring.io/projects/spring-boot) version 2.2.7.RELEASE.

## API Endpoint

https://heroi-api.herokuapp.com

Método | URL | Ações | 
--- | --- | --- | 
POST | /api/herois | Adiciona um novo herói | 
GET | /api/herois | Lista todos os heróis (com paginação) | 
GET | /api/herois?page=0&size=3 | Lista os heróis (página específica) | 
GET | /api/herois/all | Lista todos os heróis | 
GET | /api/herois/${id} | Detalha o herói por id | 
PUT | /api/herois/${id} | Atualiza o herói por id | 
DELETE | /api/herois/${id} | Excluí o herói por id | 
DELETE | /api/herois/delete/all | Excluí todos os heróis |
