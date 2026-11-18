package quirkle.game.gameplay.board;

import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileColor;
import quirkle.game.gameplay.tiles.TileSymbol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlacementValidatorTest {

    @Test
    @DisplayName("Ohne neue Steine ist die Platzierung gültig")
    void testEmptyPlacementOnExistingBoard() {
        Map<Position, Tile> placed = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, tilesByPosition());

        assertEquals(PlacementResult.LEGAL, result, "Ohne neue Steine bleibt das Brett gültig");
    }

    @Test
    @DisplayName("Der erste Zug darf aus einem einzelnen Stein bestehen")
    void testFirstMoveWithSingleTile() {
        Map<Position, Tile> pending = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(tilesByPosition(), pending);

        assertEquals(PlacementResult.LEGAL, result, "Ein einzelner Stein ist als erster Zug gültig");
    }

    @Test
    @DisplayName("Eine bereits belegte Position wird abgelehnt")
    void testPositionAlreadyOccupied() {
        Map<Position, Tile> placed = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.SQUARE));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.POSITION_OCCUPIED, result, "Ein vorhandener Stein darf nicht überschrieben werden");
    }

    @Test
    @DisplayName("Neue Steine müssen in einer gemeinsamen Zeile oder Spalte liegen")
    void testPendingTilesNotAligned() {
        Map<Position, Tile> placed = tilesByPosition();
        Map<Position, Tile> pending = tilesByPosition(
                tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                tileAt(1, 1, TileColor.RED, TileSymbol.SQUARE));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.NOT_IN_ONE_LINE, result, "Diagonal liegende Steine ohne gemeinsame Zeile/Spalte sind ungültig");
    }

    @Test
    @DisplayName("Eine diagonale Berührung verbindet den Zug nicht mit dem Brett")
    void testDiagonalContactDoesNotConnectToBoard() {
        Map<Position, Tile> placed = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesByPosition(
                tileAt(1, 1, TileColor.RED, TileSymbol.SQUARE),
                tileAt(2, 1, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.NOT_CONNECTED_TO_BOARD, result, "Neue Steine, die keinen vorhandenen Stein berühren, sind ungültig");
    }

    @Test
    @DisplayName("Bei gleichem Symbol darf keine Farbe doppelt vorkommen")
    void testRowWithDuplicateColor() {
        Map<Position, Tile> placed = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesByPosition(
                tileAt(1, 0, TileColor.BLUE, TileSymbol.CIRCLE),
                tileAt(2, 0, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, result, "Eine doppelt vorkommende Farbe in der Reihe ist ungültig");
    }

    @Test
    @DisplayName("Paarweise passende Nachbarn reichen für eine gültige Reihe nicht aus")
    void testRowMixingColorAndSymbolInconsistently() {
        Map<Position, Tile> placed = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesByPosition(
                tileAt(1, 0, TileColor.RED, TileSymbol.SQUARE),
                tileAt(2, 0, TileColor.BLUE, TileSymbol.SQUARE));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, result, "Eine Reihe muss durchgehend die gleiche Farbe ODER das gleiche Symbol haben");
    }

    @Test
    @DisplayName("Eine Reihe aus sechs verschiedenen Symbolen ist gültig")
    void testRowOfExactlySixTilesIsValid() {
        Map<Position, Tile> placed = tilesByPosition(
                tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                tileAt(1, 0, TileColor.RED, TileSymbol.SQUARE),
                tileAt(2, 0, TileColor.RED, TileSymbol.DIAMOND),
                tileAt(3, 0, TileColor.RED, TileSymbol.HEXAGON),
                tileAt(4, 0, TileColor.RED, TileSymbol.STAR));
        Map<Position, Tile> pending = tilesByPosition(tileAt(5, 0, TileColor.RED, TileSymbol.CROSS));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LEGAL, result, "Eine vollständige Reihe aus genau sechs Steinen ist gültig");
    }

    @Test
    @DisplayName("Ein siebter Stein mit wiederholtem Symbol wird abgelehnt")
    void testRowOfSevenTilesIsInvalid() {
        Map<Position, Tile> placed = tilesByPosition(
                tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                tileAt(1, 0, TileColor.RED, TileSymbol.SQUARE),
                tileAt(2, 0, TileColor.RED, TileSymbol.DIAMOND),
                tileAt(3, 0, TileColor.RED, TileSymbol.HEXAGON),
                tileAt(4, 0, TileColor.RED, TileSymbol.STAR));
        Map<Position, Tile> pending = tilesByPosition(
                tileAt(5, 0, TileColor.RED, TileSymbol.CROSS),
                tileAt(6, 0, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, result, "Eine Reihe mit mehr als sechs Steinen ist ungültig");
    }

    @Test
    @DisplayName("Eine Lücke zwischen neuen Steinen in einer Zeile wird abgelehnt")
    void testPendingTilesWithGapInRow() {
        Map<Position, Tile> placed = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesByPosition(
                tileAt(1, 0, TileColor.RED, TileSymbol.SQUARE),
                tileAt(3, 0, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LINE_HAS_GAP, result, "Neue Steine mit einer Lücke dazwischen sind ungültig");
    }

    @Test
    @DisplayName("Eine Lücke zwischen neuen Steinen in einer Spalte wird abgelehnt")
    void testPendingTilesWithGapInColumn() {
        Map<Position, Tile> placed = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesByPosition(
                tileAt(0, 1, TileColor.RED, TileSymbol.SQUARE),
                tileAt(0, 3, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LINE_HAS_GAP, result, "Neue Steine mit einer Lücke in der Spalte sind ungültig");
    }

    @Test
    @DisplayName("Ein vorhandener Stein füllt eine Lücke zwischen neuen Steinen nicht")
    void testPendingTilesAroundAnExistingTile() {
        Map<Position, Tile> placed = tilesByPosition(tileAt(1, 0, TileColor.RED, TileSymbol.SQUARE));
        Map<Position, Tile> pending = tilesByPosition(
                tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                tileAt(2, 0, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LINE_HAS_GAP, result, "Ein bereits liegender Stein trennt die beiden Steine");
    }

    @Test
    @DisplayName("Erster Zug: gleiche Symbole mit verschiedenen Farben, auch über die Nullkoordinate")
    void testFirstMoveInRowWithDistinctColors() {
        Map<Position, Tile> placed = tilesByPosition();
        Map<Position, Tile> pending = tilesByPosition(
                tileAt(-1, -3, TileColor.GREEN, TileSymbol.CIRCLE),
                tileAt(0, -3, TileColor.BLUE, TileSymbol.CIRCLE),
                tileAt(1, -3, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LEGAL, result, "Ein gültiger erster Zug braucht keinen Anschluss an vorhandene Steine");
    }

    @Test
    @DisplayName("Ein Stein darf gleichzeitig eine gültige Zeile und Spalte bilden")
    void testCrossingWithTwoValidLines() {
        Map<Position, Tile> placed = tilesByPosition(
                tileAt(-1, 0, TileColor.RED, TileSymbol.SQUARE),
                tileAt(-1, -1, TileColor.BLUE, TileSymbol.SQUARE),
                tileAt(0, -1, TileColor.BLUE, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LEGAL, result, "Am Schnittpunkt müssen beide Reihen gültig sein");
    }

    @Test
    @DisplayName("Eine ungültige Spalte wird trotz gültiger Zeile abgelehnt")
    void testCrossingWithInvalidColumn() {
        Map<Position, Tile> placed = tilesByPosition(
                tileAt(-1, 0, TileColor.RED, TileSymbol.SQUARE),
                tileAt(-1, -1, TileColor.BLUE, TileSymbol.SQUARE),
                tileAt(0, -1, TileColor.BLUE, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesByPosition(tileAt(0, 0, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, result, "Eine gültige Zeile gleicht eine ungültige Spalte nicht aus");
    }

    @Test
    @DisplayName("Eine ungültige Zeile wird trotz gültiger Spalte abgelehnt")
    void testCrossingWithInvalidRow() {
        Map<Position, Tile> placed = tilesByPosition(
                tileAt(-1, 0, TileColor.RED, TileSymbol.SQUARE),
                tileAt(-1, -1, TileColor.BLUE, TileSymbol.SQUARE),
                tileAt(0, -1, TileColor.BLUE, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesByPosition(tileAt(0, 0, TileColor.GREEN, TileSymbol.CIRCLE));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, result, "Eine gültige Spalte gleicht eine ungültige Zeile nicht aus");
    }

    @Test
    @DisplayName("Auch die Querreihe eines weiteren neuen Steins muss gültig sein")
    void testMultiplePendingTilesWithInvalidSecondaryLine() {
        Map<Position, Tile> placed = tilesByPosition(
                tileAt(0, -1, TileColor.RED, TileSymbol.CIRCLE),
                tileAt(1, -1, TileColor.RED, TileSymbol.SQUARE));
        Map<Position, Tile> pending = tilesByPosition(
                tileAt(0, 0, TileColor.BLUE, TileSymbol.CIRCLE),
                tileAt(1, 0, TileColor.BLUE, TileSymbol.DIAMOND));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, result, "Auch die Querreihen weiterer neuer Steine werden geprüft");
    }

    @Test
    @DisplayName("Die Reihensuche endet am ersten freien Feld")
    void testGapInBoardSeparatesLines() {
        Map<Position, Tile> placed = tilesByPosition(
                tileAt(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                tileAt(0, -1, TileColor.GREEN, TileSymbol.CIRCLE),
                tileAt(1, -1, TileColor.GREEN, TileSymbol.HEXAGON),
                tileAt(2, -1, TileColor.GREEN, TileSymbol.SQUARE),
                tileAt(2, 0, TileColor.BLUE, TileSymbol.SQUARE));
        Map<Position, Tile> pending = tilesByPosition(tileAt(-1, 0, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LEGAL, result, "Der blaue Stein hinter der Lücke gehört nicht zur roten Reihe");
    }

    private record Placement(Position position, Tile tile) {}

    private static Placement tileAt(int x, int y, TileColor color, TileSymbol symbol) {
        return new Placement(new Position(x, y), new Tile(color, symbol));
    }

    private static Map<Position, Tile> tilesByPosition(Placement... placements) {
        Map<Position, Tile> tiles = new LinkedHashMap<>();
        for (Placement placement : placements) {
            tiles.put(placement.position(), placement.tile());
        }
        return tiles;
    }
}
