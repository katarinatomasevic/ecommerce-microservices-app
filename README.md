# E-Commerce Microservices System

## 1. Uvod i Pregled Arhitekture

Ovaj projekat implementira jednostavan **distribuirani e-commerce sistem** zasnovan na **Spring Boot Microservices** arhitekturi.  
Cilj sistema je da demonstrira osnovne principe **mikroservisne komunikacije**, **service discovery** mehanizma, **API Gateway rutiranja** i **otpornosti na greške** pomoću Feign klijenata i Resilience4j biblioteke.

### Tema
**E-Commerce System** – upravljanje korisnicima i njihovim porudžbinama.

### Implementirane komponente
| Servis | Uloga | Port |
|--------|--------|------|
| **discovery-service** | Service Discovery (Eureka Server) | `8761` |
| **gateway** | Centralna ulazna tačka sistema (Spring Cloud Gateway) | `8085` |
| **users-service** | CRUD operacije nad korisnicima (User entitet) | `8081` |
| **orders-service** | CRUD operacije nad porudžbinama (Order entitet) + komunikacija sa Users servisom | `8082` |

---

## 2. Tehnički Zahtevi i Implementacija

| Zahtev | Implementacija | Detalji |
|--------|----------------|---------|
| **Service Discovery** | Eureka Server | `discovery-service` registruje sve aktivne mikroservise. |
| **API Gateway** | Spring Cloud Gateway | Sve rute prolaze kroz gateway (`localhost:8085`). |
| **Komunikacija među servisima** | OpenFeign | `orders-service` koristi `UserClient` za pozivanje `users-service` (npr. dohvatanje korisnika po ID-u). |
| **Otpornost sistema** | Resilience4j | Circuit Breaker + Retry primenjeni na Feign pozivima u `orders-service`. |
| **Persistencija** | H2 baza podataka | Svaki servis koristi lokalnu in-memory bazu (`jdbc:h2:mem:`). |
| **Dokumentacija API-ja** | Swagger UI | Automatski generisani endpointi za testiranje. |

---

## 3. Pokretanje Aplikacije

### Preporučeni redosled pokretanja (lokalno)

1. **discovery-service** → [http://localhost:8761](http://localhost:8761)  
   > Pokreće Eureka server i omogućava registraciju ostalih servisa.

2. **users-service** → [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)  
   > CRUD operacije nad korisnicima (H2 baza: `usersdb`).

3. **orders-service** → [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)  
   > CRUD operacije nad porudžbinama + agregacioni endpoint sa korisničkim podacima.

4. **gateway** → [http://localhost:8085](http://localhost:8085)  
   > Centralna ulazna tačka – svi eksterni pozivi idu preko gateway-a.  
   Primer: `GET http://localhost:8085/api/users` ili `GET http://localhost:8085/api/orders`

---

## 4. API Endpoints

### Users Service (`8081` → `/api/users`)

| Metoda | Putanja | Opis |
|--------|----------|------|
| `POST` | `/api/users` | Kreiranje korisnika |
| `GET` | `/api/users` | Dohvatanje svih korisnika |
| `GET` | `/api/users/{id}` | Dohvatanje korisnika po ID-u |
| `PUT` | `/api/users/{id}` | Ažuriranje korisnika |
| `DELETE` | `/api/users/{id}` | Brisanje korisnika |

---

### Orders Service (`8082` → `/api/orders`)

| Metoda | Putanja | Opis |
|--------|----------|------|
| `POST` | `/api/orders` | Kreiranje porudžbine |
| `GET` | `/api/orders` | Dohvatanje svih porudžbina |
| `GET` | `/api/orders/{id}` | Dohvatanje porudžbine po ID-u |
| `PUT` | `/api/orders/{id}` | Ažuriranje porudžbine |
| `DELETE` | `/api/orders/{id}` | Brisanje porudžbine |
| `GET` | `/api/orders/{id}/with-user` | **Agregacioni endpoint** — dohvata porudžbinu zajedno sa detaljima korisnika (Feign poziv + Circuit Breaker + Retry) |

---

## 5. Otpornost Sistema (Resilience4j)

- **Circuit Breaker** štiti sistem od preopterećenja kada je `users-service` nedostupan.  
  Kada dođe do greške, `orders-service` vraća fallback odgovor sa “UNKNOWN” korisnikom.

- **Retry** mehanizam automatski ponavlja poziv prema `users-service` pre nego što proglasi neuspeh.

---

## 6. Swagger Dokumentacija

| Servis | Swagger URL |
|--------|--------------|
| **Users Service** | [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html) |
| **Orders Service** | [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html) |

> Napomena: prilikom testiranja komunikacije između servisa, preporučuje se korišćenje **Gateway-a (8085)** umesto direktnog pristupa servisima.

---

## 7. Napomene i Preporuke

- Svi servisi se automatski registruju u **Eureka serveru** (`http://localhost:8761`).
- Baze se resetuju pri svakom pokretanju jer su **in-memory** (H2).
- **Feign + Resilience4j** implementacija omogućava jednostavno testiranje otpornosti (gašenjem `users-service` i pozivom `/api/orders/{id}/user` se može videti fallback).

---
