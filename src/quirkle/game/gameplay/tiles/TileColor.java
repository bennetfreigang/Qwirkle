package quirkle.game.gameplay.tiles;

// OCP
public enum TileColor implements TileAttribute {
    RED('R'),
    YELLOW('Y'),
    GREEN('G'),
    BLUE('B'),
    PURPLE('P'),
    ORANGE('O');

    private final char shortCode;

    TileColor(char shortCode) {
        this.shortCode = shortCode;
    }

    @Override
    public char shortCode() {
        return shortCode;
    }
}
