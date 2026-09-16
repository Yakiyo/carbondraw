# ♠ Carbon Draw ♥

A roguelike deckbuilder card game inspired by Balatro, built with JavaFX.

Play poker hands to beat target scores across 10 antes, collect Jokers and Tarots from the shop to boost your scoring power, and unlock permanent Voucher upgrades to dominate your run.

---

## Requirements

| Requirement | Version |
|---|---|
| **Java (JDK)** | 21 or higher |
| **Apache Maven** | 3.6+ |

> All other dependencies (JavaFX, Gson, MySQL Connector) are managed automatically by Maven.

---

## How to Run / Compile

1. **Clone the repository**
   ```bash
   git clone https://github.com/Yakiyo/carbondraw.git
   cd carbondraw
   ```

2. **Compile the project**
   ```bash
   mvn clean compile
   ```

3. **Run the game**
   ```bash
   mvn javafx:run
   ```

4. **(Optional) Run tests**
   ```bash
   mvn test
   ```
