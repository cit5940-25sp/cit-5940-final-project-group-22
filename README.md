# 🎮 Othello Game with AI

<div align="center">
  
  [![Java](https://img.shields.io/badge/Java-11%2B-007396?logo=java&logoColor=white)](https://www.java.com)
  [![JavaFX](https://img.shields.io/badge/JavaFX-11%2B-2ea44f)](https://openjfx.io/)
  [![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A36?logo=apache-maven)](https://maven.apache.org/)
  
  **A modern implementation of the classic Othello board game featuring advanced AI strategies**
  
  [🎯 Features](#-features) • 
  [🎮 Play](#-play) • 
  [🤖 AI Strategies](#-ai-strategies) • 
  [📁 Structure](#-structure) • 
  [🧪 Testing](#-testing)

</div>

---

## 🎯 Features

</div>

### ✨ Highlights
- 🖼️ **JavaFX GUI** - Modern, intuitive interface
- 💾 **Save/Load** - Preserve your games
- 🎯 **Smart AI** - Four different difficulty levels
- 🧪 **Well-Tested** - 80%+ code coverage
- 📊 **Real-time Visualization** - See available moves instantly

### 🎮 Play
```bash
# Human vs AI
mvn javafx:run -Djavafx.args="human minimax"

# AI Battle!
mvn javafx:run -Djavafx.args="mcts custom"
```

## 🤖 AI Strategies

<table>
<tr>
<td align="center">
  
### 🧠 Minimax
**Classic game theory**
- Depth: 2 levels
- Assumes perfect play
- Position-based heuristics

</td>
<td align="center">

### 🎲 Expectimax
**Probabilistic approach**
- Models human mistakes
- Chance nodes
- Risk-taking behavior

</td>
</tr>
<tr>
<td align="center">

### 🌲 MCTS
**Advanced exploration**
- 100 iterations/move
- UCT selection
- Random playouts

</td>
<td align="center">

### ⭐ Custom
**Hybrid strategy**
- Corner priority
- Monte Carlo rollouts
- 50 simulations/move

</td>
</tr>
</table>

## 📁 Structure

```mermaid
graph LR
    A[🎮 Othello] --> B[📦 gamelogic]
    A --> C[🖼️ gui]
    A --> D[🔧 util]
    B --> E[🤖 strategy]
    B --> F[💾 state]
    E --> G[AI Algorithms]
    F --> H[Save/Load]
```

<details>
<summary>📂 Detailed Structure</summary>

```
othello/
├── 🎯 App.java              # Entry point
├── 📊 Constants.java        # Game constants
├── gamelogic/
│   ├── 🎮 OthelloGame.java  # Core logic
│   ├── 👤 Player.java       # Player types
│   └── strategy/           # AI implementations
├── gui/
│   └── 🖥️ GameController.java
└── util/
    └── 💾 SaveLoadUtil.java
```

</details>

## 🧪 Testing

<div align="center">

| Metric | Status |
|:------:|:------:|
| Coverage | ![Coverage](https://img.shields.io/badge/coverage-92%25-brightgreen) |
| Tests | ![Tests](https://img.shields.io/badge/tests-passing-success) |
| Quality | ![Quality](https://img.shields.io/badge/code%20quality-A-brightgreen) |


## 🛠️ Design Patterns

### Strategy Pattern
```java
Strategy → MinimaxStrategy
        → ExpectimaxStrategy
        → MCTSStrategy
        → CustomStrategy
```

### Memento Pattern
```java
OthelloGame → GameMemento → GameHistory
```

## 🎨 Screenshots

<div align="center">

| Game Start | Mid Game | Save/Load |
|:----------:|:--------:|:---------:|
| 🏁 Initial board | 🎯 Available moves | 💾 State management |

</div>

---

## 🧑‍💻 Collaborators

- [@Mingtian Chen](https://github.com/mingtc0702)  
- [@Muqiao Lei](https://github.com/rmurdock41)  
- [@Lingchong Hu](https://github.com/LingchongHu-123)

