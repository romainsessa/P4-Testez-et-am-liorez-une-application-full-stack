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

## Lancer les tests

### Tests Backend (Java)

Lancer les tests :

mvn test

Générer le rapport de couverture :

mvn testCoverage

Rapport disponible dans :

target/site/jacoco/index.html

---

### Tests Frontend (Angular - Jest)

Lancer les tests avec couverture :

npm run test

Rapport disponible dans :

coverage/jest/index.html

---

### 🌐 Tests End-to-End (Cypress)

Lancer les tests E2E avec couverture :

npm run e2e:ci

Puis :

npm run e2e:coverage

Rapport disponible dans :

coverage/lcov-report/index.html

---