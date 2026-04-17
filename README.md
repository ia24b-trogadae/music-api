# music-api
Spring Boot API for albums and songs with JWT authentication

## ERD

![ERD](docs/erd.png)

## Berechtigungsmatrix

Die API verwendet drei Zugriffsstufen:

- **PermitAll**: Kein Login erforderlich
- **ADMIN**: Voller Zugriff auf alle Funktionen
- **EDITOR**: Zugriff auf Lesen und Schreiben, aber keine Löschrechte

### Tabelle

| Endpoint | Methode | PermitAll | ADMIN | EDITOR |
|----------|--------|----------|-------|--------|
| /auth/login | POST | ✔ | ✔ | ✔ |
| /albums/ping | GET | ✔ | ✔ | ✔ |
| /albums | GET | ✘ | ✔ | ✔ |
| /albums/{id} | GET | ✘ | ✔ | ✔ |
| /albums/count | GET | ✘ | ✔ | ✔ |
| /albums/by-date | GET | ✘ | ✔ | ✔ |
| /albums | POST | ✘ | ✔ | ✔ |
| /albums/{id} | PUT | ✘ | ✔ | ✔ |
| /albums/{id} | DELETE | ✘ | ✔ | ✘ |
| /albums | DELETE | ✘ | ✔ | ✘ |

### Erklärung

- Benutzer müssen sich über `/auth/login` anmelden und erhalten ein JWT.
- Geschützte Endpunkte können nur mit gültigem Token aufgerufen werden.
- Die Zugriffsrechte werden über die Rolle (`ADMIN`, `EDITOR`) gesteuert.