package othello.gamelogic.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Caretaker for storing and retrieving game mementos.
 */
public class GameHistory {
    private final List<GameMemento> history = new ArrayList<>();

    /** Save a new snapshot */
    public void save(GameMemento m) {
        history.add(m);
    }

    /** Retrieve and remove the last snapshot */
    public GameMemento undo() {
        if (history.isEmpty()) return null;
        return history.remove(history.size() - 1);
    }

    /** Clear all saved snapshots */
    public void clear() {
        history.clear();
    }
}
