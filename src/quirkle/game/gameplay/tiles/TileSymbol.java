package quirkle.game.gameplay.tiles;

// OCP
public enum TileSymbol implements TileAttribute {
    CIRCLE('C'),
    SQUARE('Q'),
    DIAMOND('D'),
    HEXAGON('H'),
    STAR('S'),
    CROSS('X');

    private final char shortCode;

    TileSymbol(char shortCode) {
        this.shortCode = shortCode;
    }

    @Override
    public char shortCode() {
        return shortCode;
    }
}
