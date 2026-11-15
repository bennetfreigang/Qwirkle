package quirkle.game.gameplay.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileColor;
import quirkle.game.gameplay.tiles.TileSymbol;

class PlacementValidator {
    /**
     * Checks {@code pendingTiles} against every placement rule
     * @return {@link PlacementResult#LEGAL}, or the first rule that rejects them
     */
    static PlacementResult checkPendingTilePlacement(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        if (pendingTiles.isEmpty()) return PlacementResult.LEGAL;

        for (Position position : pendingTiles.keySet()) {
            if (placedTiles.containsKey(position)) return PlacementResult.POSITION_OCCUPIED;
        }

        PlacementResult geometryResult = checkGeometry(pendingTiles.keySet());
        if (!geometryResult.isLegal()) return geometryResult;
        if (!isConnectedToBoard(placedTiles, pendingTiles)) return PlacementResult.NOT_CONNECTED_TO_BOARD;

        Map<Position, Tile> allTiles = new HashMap<>(placedTiles);
        allTiles.putAll(pendingTiles);

        return formsValidLines(allTiles, pendingTiles.keySet())
                ? PlacementResult.LEGAL : PlacementResult.INVALID_LINE;
    }

    /**
     * Checks that the nonempty set of pending positions forms one unbroken row or column.
     * Existing tiles do not fill gaps between pending positions.
     */
    private static PlacementResult checkGeometry(Set<Position> positions) {
        // init min / max for x and y
        Position first = positions.iterator().next();
        int minX = first.x();
        int maxX = first.x();
        int minY = first.y();
        int maxY = first.y();

        // find min / max for x and y
        for (Position position : positions) {
            minX = Math.min(minX, position.x());
            maxX = Math.max(maxX, position.x());
            minY = Math.min(minY, position.y());
            maxY = Math.max(maxY, position.y());
        }

        // all positions in line?
        boolean sameRow = minY == maxY;
        boolean sameColumn = minX == maxX;
        if (!sameRow && !sameColumn) return PlacementResult.NOT_IN_ONE_LINE;

        // has line gap?
        long length = sameRow ? (long) maxX - minX + 1 : (long) maxY - minY + 1;
        return length == positions.size() ? PlacementResult.LEGAL : PlacementResult.LINE_HAS_GAP;
    }

    /** @return if a pending tile touches the board, or the board is still empty (first move). */
    private static boolean isConnectedToBoard(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        if (placedTiles.isEmpty()) {
            return true;
        }
        for (Position position : pendingTiles.keySet()) {
            for (Direction direction : Direction.values()) {
                if (placedTiles.containsKey(position.neighbor(direction))) {
                    return true;
                }
            }
        }
        return false;
    }

    /** @return if both lines through every pending position are valid Qwirkle lines */
    private static boolean formsValidLines(Map<Position, Tile> tiles, Set<Position> pendingPositions) {
        for (Position position : pendingPositions) {
            for (LineOrientation orientation : LineOrientation.values()) {
                if (!isValidLine(collectLine(tiles, position, orientation))) return false;
            }
        }
        return true;
    }

    /**
     * @return if {@code line} holds at most one tile, or shares one color with distinct symbols,
     *         or one symbol with distinct colors
     */
    private static boolean isValidLine(List<Tile> line) {
        if (line.size() <= 1) return true;

        Set<TileColor> colors = new HashSet<>();
        Set<TileSymbol> symbols = new HashSet<>();
        for (Tile tile : line) {
            colors.add(tile.getColor());
            symbols.add(tile.getSymbol());
        }

        boolean allSameColor = colors.size() == 1;
        boolean allSameSymbol = symbols.size() == 1;
        boolean allColorsDifferent = colors.size() == line.size();
        boolean allSymbolsDifferent = symbols.size() == line.size();

        return (allSameColor && allSymbolsDifferent) || (allSameSymbol && allColorsDifferent);
    }

    /** @return every tile contiguously connected to {@code position} along {@code orientation} */
    private static List<Tile> collectLine(Map<Position, Tile> tiles, Position position, LineOrientation orientation) {
        Position current = StreakWalker.walkToStreakEnd(tiles, position, orientation.backwardDirection);
        List<Tile> line = new ArrayList<>();

        while (tiles.containsKey(current)) {
            line.add(tiles.get(current));
            current = current.neighbor(orientation.forwardDirection);
        }

        return line;
    }
}
