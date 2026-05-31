# Yoga Studio

## Description

Le projet est composé de :
- un **frontend Angular**
- un **backend Java (Spring Boot)**
- une suite complète de **tests automatisés**

---

##  Technologies utilisées pour les tests

### Frontend
- Jest

### Backend
- JUnit / Mockito / Spring Boot Test

### E2E
- Cypress

---

## Lancer l'application

### Back
```bash
mvn spring-boot:run
```

### Front
```bash
npm install
npm run start
```
---

## Lancer les tests

### Tests Backend (Java)

Lancer les tests :
```bash
mvn test
```

Rapport disponible dans :

- target/site/jacoco/index.html

---

### Tests Frontend (Angular - Jest)

Lancer les tests avec couverture :
```bash
npm run test
```

Rapport disponible dans :

- coverage/jest/index.html

---

### 🌐 Tests End-to-End (Cypress)

Lancer les tests E2E avec couverture :
```bash
npm run e2e:ci
```

Puis :
```bash
npm run e2e:coverage
```

Rapport disponible dans :

- coverage/lcov-report/index.html

---