# Java Chess Project — Development Guidelines

## Table of Contents
- [1. Object-Oriented Design (OOP) Structure](#1-object-oriented-design-oop-structure)
- [2. Core Java Features & APIs](#2-core-java-features--apis)
- [3. Implementation & Team Standards](#3-implementation--team-standards)
- [4. Grading & Final Submission](#4-grading--final-submission)

---

## 1. Object-Oriented Design (OOP) Structure

### Inheritance
Use a superclass (e.g., `Piece`) and subclasses (e.g., `Pawn`, `Knight`, `PowerUp`) with the `extends` keyword to manage hierarchical data.

```java
public class Pawn extends Piece {
    // Pawn-specific logic
}
```

### Abstraction
Define common piece behaviors as `abstract` methods in an abstract class to ensure all pieces implement required logic.

```java
public abstract class Piece {
    public abstract List<int[]> calculateMoves();
}
```

### Interfaces
Use interfaces to define optional behaviors and ensure behavioral consistency.

```java
public interface TimedEffect {
    void applyEffect(int durationSeconds);
    void removeEffect();
}
```

### Encapsulation
Declare fields as `private` and expose them through public getters/setters.

```java
public class Piece {
    private String color;
    private int[] position;

    public String getColor() { return color; }
    public void setPosition(int[] position) { this.position = position; }
}
```

### Polymorphism
Use **method overriding** for piece-specific move logic and **method overloading** for varied constructors.

```java
@Override
public List<int[]> calculateMoves() {
    // Knight-specific L-shaped movement
}
```

---

## 2. Core Java Features & APIs

### Java Collections
Use built-in collections for game data management.

| Collection | Use Case |
|---|---|
| `ArrayList<Move>` | Move history |
| `HashMap<String, Piece>` | Board coordinate → piece mapping |

```java
HashMap<String, Piece> board = new HashMap<>();
board.put("e2", new Pawn("white"));
```

### Exception Handling
Wrap risky operations in `try-catch-finally` blocks.

```java
try {
    board.movePiece(from, to);
} catch (InvalidMoveException e) {
    System.out.println("Illegal move: " + e.getMessage());
} finally {
    updateUI();
}
```

### User-Defined Exceptions
Create custom exception classes by extending `Exception`.

```java
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}
```

### File I/O
Save and load game states using `BufferedReader` / `BufferedWriter`.

```java
// Save game
BufferedWriter writer = new BufferedWriter(new FileWriter("save.txt"));
writer.write(gameState.serialize());
writer.close();

// Load game
BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
String line;
while ((line = reader.readLine()) != null) {
    gameState.deserialize(line);
}
reader.close();
```

---

## 3. Implementation & Team Standards

### Naming Conventions
Follow **CamelCase** for all identifiers:

| Type | Convention | Example |
|---|---|---|
| Classes | `UpperCamelCase` | `ChessBoard`, `InvalidMoveException` |
| Methods | `lowerCamelCase` | `calculateMoves()`, `getCurrentTurn()` |
| Variables | `lowerCamelCase` | `currentTurn`, `moveHistory` |
| Constants | `UPPER_SNAKE_CASE` | `MAX_BOARD_SIZE` |

### Documentation (Javadoc)
Add `/** ... */` comments above every class and method.

```java
/**
 * Represents a chess piece on the board.
 *
 * @param color the color of the piece ("white" or "black")
 * @param position the starting position as [row, col]
 */
public Piece(String color, int[] position) { ... }

/**
 * Calculates all valid moves for this piece.
 *
 * @return a list of valid target positions as [row, col] arrays
 */
public abstract List<int[]> calculateMoves();
```

### Version Control (Git & GitHub)
Follow these practices for collaboration:

```bash
# Commit changes with clear messages
git add .
git commit -m "feat: add castling logic to King class"
git push origin main

# Work in branches to avoid conflicts
git checkout -b feature/save-load-game
```

> **Tip:** Commit often. Each commit should represent one logical change.

### Advanced Features (Planned)
- **GUI** — Swing or JavaFX for the board interface
- **Multithreading** — `Timer` or `Thread` for move clocks
- **Sockets** — `ServerSocket` / `Socket` for multiplayer networking

---

## 4. Grading & Final Submission

### Feature Coverage Checklist

- [ ] Inheritance & Abstraction implemented
- [ ] At least one Interface used
- [ ] Encapsulation applied to all fields
- [ ] Polymorphism demonstrated (override + overload)
- [ ] Collections used (`ArrayList`, `HashMap`)
- [ ] Custom exception class created
- [ ] `try-catch-finally` blocks in place
- [ ] File I/O (save/load) working
- [ ] Javadoc comments on all classes & methods
- [ ] Git history shows regular commits

> **Important:** Ensure the implementation covers **all** features learned in class — not just the hardest ones. Graders check for breadth.

### Submission Requirements

| Item | Details |
|---|---|
| **Format** | `.zip` exported from Eclipse |
| **Report** | ~5 pages (design decisions, class diagram, feature summary) |
| **Demo** | Recorded video walkthrough of the running program |

---

*Last updated: May 7th 2026*