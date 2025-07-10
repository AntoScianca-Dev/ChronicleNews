# 📰 Progetto finale specializzazione Java Sciancalepore Antonia
_Progetto Java Spring Boot per la gestione di articoli in una piattaforma collaborativa, con autenticazione, autorizzazione e sistema di revisione._

---

## 📌 Funzionalità principali

### ✅ Autenticazione e registrazione

- Registrazione e login utenti con Spring Security
- Ruoli supportati: `USER`, `WRITER`, `REVISOR`, `ADMIN`
- Accesso differenziato alle funzionalità in base al ruolo tramite dashboard dedicate
- Redirect automatico alla homepage dopo login/register

### 📝 Gestione articoli

- Creazione articoli con:
  - Titolo
  - Sottotitolo
  - Corpo del testo
  - Categoria selezionabile
  - Immagini multiple di cui verrà visualizzata una piccola anteprima
  - Salvataggio immagini su **Supabase** (upload asincrono)

- Visualizzazione articoli:
  - Pagina **index** (tutti gli articoli)
  - Pagina **home** (ultimi 3 articoli)
  - Pagina **dettaglio** per ogni articolo

### 🔍 Ricerca avanzata

- Ricerca full-text per:
  - Titolo
  - Sottotitolo
  - Corpo del testo
  - Categoria
- Ricerca per click dalla pagina di dettaglio dell'articolo:
  - Categoria (cliccando sulla categoria)
  - Autore (cliccando sul nome utente)

### 📂 Requisiti & gestione

- Tutti gli articoli hanno uno stato di approvazione:
  - `null` = in revisione
  - `true` = accettato
  - `false` = rifiutato
- Visualizzazione pubblica solo per articoli accettati
- Revisione degli articoli da parte dei revisori tramite interfaccia dedicata
  - dashboard revisore:
    - articoli in revisione da accettare o rifiutare
    - riepilogo articoli accettati
    - riepilogo articoli rifiutati
    - lo stato di articolo accettato a rifiutato può essere sempre modificato dal revisore

---

## 💡 Tecnologie utilizzate

| Componente       | Descrizione                                |
|------------------|--------------------------------------------|
| Java             | Versione consigliata: Java 21              |
| Spring Boot      | Backend, sicurezza, gestione dati          |
| Spring Security  | Autenticazione/Autorizzazione              |
| Thymeleaf        | Template engine lato frontend              |
| MySQL            | Database relazionale                       |
| Supabase         | Cloud storage per immagini                 |
| Lombok           | Generazione automatica di boilerplate code |
| ModelMapper      | Mappatura entità ↔ DTO                     |
| Maven            | Gestione dipendenze                        |

---

## 📁 Struttura del progetto

```

sql/
├── create.sql
├── drop.sql
└── insert.sql
src/
├── main/
│   ├── java/it/aulab/progetto/
│   │   ├── config/
│   │   ├── controllers/
│   │   ├── dtos/
│   │   ├── models/
│   │   ├── repositories/
│   │   ├── services/
│   │   └── utils/
│   ├── resources/
│   │   ├── static/
│   │   ├── templates/
│   │   └── application.properties
```

---

## 🛠️ Avvio del progetto

1. **Clona il repository**
2. **Configura il DB MySQL**  
   Esegui gli script in `sql/` per creare le tabelle (`create.sql`, `insert.sql`)
3. **Configura Supabase**
   Inserisci le chiavi nel file `application.properties`:
   ```properties
   supabase.url=
   supabase.key=
   supabase.bucket=
   supabase.image=
   ```
4. **Avvia l'app**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

Accedi su [http://localhost:8080](http://localhost:8080)

---

## 👤 Ruoli utente

| Ruolo       | Permessi principali                                         |
|-------------|-------------------------------------------------------------|
| USER        | Visualizza articoli accettati                               |
| WRITER      | Crea articoli, li invia in revisione                        |
| REVISOR     | Accetta/rifiuta articoli                                    |
| ADMIN       | Gestione ruoli utenti e gestione categorie                  |

---

## ✨ Extra

- Navbar dinamica in base all'autenticazione e ruolo
- Animazioni e anteprima immagine lato client
- Supporto tema chiaro/scuro (toggle via JavaScript)
- Validazioni con Bean Validation
- Caricamento immagini multiplo con preview sia in fase di creazione dell'articolo che in fase di modifica dello stesso
- Ricerca disponibile anche da utenti non loggati

---

## 🧪 Funzionalità testabili

- ✅ Registrazione nuovo utente
- ✅ Creazione articoli con o senza immagini
- ✅ Visualizzazione articoli per categoria/autore
- ✅ Ricerca avanzata in navbar
- ✅ Revisione e approvazione articoli
- ✅ Salvataggio e recupero immagini da Supabase

---

## 📄 Licenza

Questo progetto è a scopo didattico ed è stato realizzato per il percorso formativo **Aulab - Hackademy - Specializzazione Java**.
