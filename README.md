[![CircleCI](https://circleci.com/gh/dmarjanovic94/kbs-shopifine/tree/dev.svg?style=svg&circle-token=19db9cb126a9397de48fa0cfe86311a01163f89d)](https://circleci.com/gh/dmarjanovic94/kbs-shopifine/tree/dev)

# Shopifine 2.0.0
Project for course "Sistemi bazirani na znanju"

### Core Service
- Written in `Play Framework 2.5` using `Scala Programming Language`. 
- Used `SBT` for **external** dependencies.
- Used **JSON API** for _public API_. 
- Used _Docker Compose_ for `MySQL` database.
- Provided **API documentation** via `Swagger UI`. 
- Supported **Continuous Integration** via `Circle`. 

### Drools Service
- Written in `AkkaHttp` using `Scala Programming Language`. 
- Used `SBT` for **external** dependencies.
- Used `Drools` for **rules based engine**.
- Provided **tests** for **rules** using `ScalaTest`. 

### Web Application
- Written in `HTML` and `AngularJS`.
- Used `BigBag` _css_ and _.js_ theme.
- Used `Bower` for external dependencies.
