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
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, tilesOf());

        assertEquals(PlacementResult.LEGAL, actual, "Ohne neue Steine bleibt das Brett gültig");
    }

    @Test
    @DisplayName("Der erste Zug darf aus einem einzelnen Stein bestehen")
    void testFirstMoveWithSingleTile() {
        Map<Position, Tile> pending = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(tilesOf(), pending);

        assertEquals(PlacementResult.LEGAL, actual, "Ein einzelner Stein ist als erster Zug gültig");
    }

    @Test
    @DisplayName("Eine bereits belegte Position wird abgelehnt")
    void testPositionAlreadyOccupied() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(at(0, 0, TileColor.RED, TileSymbol.SQUARE));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.POSITION_OCCUPIED, actual, "Ein vorhandener Stein darf nicht überschrieben werden");
    }

    @Test
    @DisplayName("Neue Steine müssen in einer gemeinsamen Zeile oder Spalte liegen")
    void testPendingTilesNotAligned() {
        Map<Position, Tile> placed = tilesOf();
        Map<Position, Tile> pending = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 1, TileColor.RED, TileSymbol.SQUARE));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.NOT_IN_ONE_LINE, actual, "Diagonal liegende Steine ohne gemeinsame Zeile/Spalte sind ungültig");
    }

    @Test
    @DisplayName("Eine diagonale Berührung verbindet den Zug nicht mit dem Brett")
    void testDiagonalContactDoesNotConnectToBoard() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 1, TileColor.RED, TileSymbol.SQUARE),
                at(2, 1, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.NOT_CONNECTED_TO_BOARD, actual, "Neue Steine, die keinen vorhandenen Stein berühren, sind ungültig");
    }

    @Test
    @DisplayName("Bei gleichem Symbol darf keine Farbe doppelt vorkommen")
    void testRowWithDuplicateColor() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.BLUE, TileSymbol.CIRCLE),
                at(2, 0, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, actual, "Eine doppelt vorkommende Farbe in der Reihe ist ungültig");
    }

    @Test
    @DisplayName("Paarweise passende Nachbarn reichen für eine gültige Reihe nicht aus")
    void testRowMixingColorAndSymbolInconsistently() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.BLUE, TileSymbol.SQUARE));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, actual, "Eine Reihe muss durchgehend die gleiche Farbe ODER das gleiche Symbol haben");
    }

    @Test
    @DisplayName("Eine Reihe aus sechs verschiedenen Symbolen ist gültig")
    void testRowOfExactlySixTilesIsValid() {
        Map<Position, Tile> placed = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.RED, TileSymbol.DIAMOND),
                at(3, 0, TileColor.RED, TileSymbol.HEXAGON),
                at(4, 0, TileColor.RED, TileSymbol.STAR));
        Map<Position, Tile> pending = tilesOf(at(5, 0, TileColor.RED, TileSymbol.CROSS));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LEGAL, actual, "Eine vollständige Reihe aus genau sechs Steinen ist gültig");
    }

    @Test
    @DisplayName("Ein siebter Stein mit wiederholtem Symbol wird abgelehnt")
    void testRowOfSevenTilesIsInvalid() {
        Map<Position, Tile> placed = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.RED, TileSymbol.DIAMOND),
                at(3, 0, TileColor.RED, TileSymbol.HEXAGON),
                at(4, 0, TileColor.RED, TileSymbol.STAR));
        Map<Position, Tile> pending = tilesOf(
                at(5, 0, TileColor.RED, TileSymbol.CROSS),
                at(6, 0, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, actual, "Eine Reihe mit mehr als sechs Steinen ist ungültig");
    }

    @Test
    @DisplayName("Eine Lücke zwischen neuen Steinen in einer Zeile wird abgelehnt")
    void testPendingTilesWithGapInRow() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(3, 0, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LINE_HAS_GAP, actual, "Neue Steine mit einer Lücke dazwischen sind ungültig");
    }

    @Test
    @DisplayName("Eine Lücke zwischen neuen Steinen in einer Spalte wird abgelehnt")
    void testPendingTilesWithGapInColumn() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(0, 1, TileColor.RED, TileSymbol.SQUARE),
                at(0, 3, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LINE_HAS_GAP, actual, "Neue Steine mit einer Lücke in der Spalte sind ungültig");
    }

    @Test
    @DisplayName("Ein vorhandener Stein füllt eine Lücke zwischen neuen Steinen nicht")
    void testPendingTilesAroundAnExistingTile() {
        Map<Position, Tile> placed = tilesOf(at(1, 0, TileColor.RED, TileSymbol.SQUARE));
        Map<Position, Tile> pending = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(2, 0, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LINE_HAS_GAP, actual, "Ein bereits liegender Stein trennt die beiden Steine");
    }

    @Test
    @DisplayName("Erster Zug: gleiche Symbole mit verschiedenen Farben, auch über die Nullkoordinate")
    void testFirstMoveInRowWithDistinctColors() {
        Map<Position, Tile> placed = tilesOf();
        Map<Position, Tile> pending = tilesOf(
                at(-1, -3, TileColor.GREEN, TileSymbol.CIRCLE),
                at(0, -3, TileColor.BLUE, TileSymbol.CIRCLE),
                at(1, -3, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LEGAL, actual, "Ein gültiger erster Zug braucht keinen Anschluss an vorhandene Steine");
    }

    @Test
    @DisplayName("Spalte erweitern: gleiche Farbe mit verschiedenen Symbolen, auch über die Nullkoordinate")
    void testColumnWithDistinctSymbols() {
        Map<Position, Tile> placed = tilesOf(at(-3, -2, TileColor.GREEN, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(-3, -1, TileColor.GREEN, TileSymbol.SQUARE),
                at(-3, 0, TileColor.GREEN, TileSymbol.DIAMOND),
                at(-3, 1, TileColor.GREEN, TileSymbol.CROSS));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LEGAL, actual, "Eine Spalte darf die Nullkoordinate überschreiten");
    }

    @Test
    @DisplayName("Ein Stein darf gleichzeitig eine gültige Zeile und Spalte bilden")
    void testCrossingWithTwoValidLines() {
        Map<Position, Tile> placed = tilesOf(
                at(-1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(-1, -1, TileColor.BLUE, TileSymbol.SQUARE),
                at(0, -1, TileColor.BLUE, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LEGAL, actual, "Am Schnittpunkt müssen beide Reihen gültig sein");
    }

    @Test
    @DisplayName("Eine ungültige Spalte wird trotz gültiger Zeile abgelehnt")
    void testCrossingWithInvalidColumn() {
        Map<Position, Tile> placed = tilesOf(
                at(-1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(-1, -1, TileColor.BLUE, TileSymbol.SQUARE),
                at(0, -1, TileColor.BLUE, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(at(0, 0, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, actual, "Eine gültige Zeile gleicht eine ungültige Spalte nicht aus");
    }

    @Test
    @DisplayName("Eine ungültige Zeile wird trotz gültiger Spalte abgelehnt")
    void testCrossingWithInvalidRow() {
        Map<Position, Tile> placed = tilesOf(
                at(-1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(-1, -1, TileColor.BLUE, TileSymbol.SQUARE),
                at(0, -1, TileColor.BLUE, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(at(0, 0, TileColor.GREEN, TileSymbol.CIRCLE));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, actual, "Eine gültige Spalte gleicht eine ungültige Zeile nicht aus");
    }

    @Test
    @DisplayName("Auch die Querreihe eines weiteren neuen Steins muss gültig sein")
    void testMultiplePendingTilesWithInvalidSecondaryLine() {
        Map<Position, Tile> placed = tilesOf(
                at(0, -1, TileColor.RED, TileSymbol.CIRCLE),
                at(1, -1, TileColor.RED, TileSymbol.SQUARE));
        Map<Position, Tile> pending = tilesOf(
                at(0, 0, TileColor.BLUE, TileSymbol.CIRCLE),
                at(1, 0, TileColor.BLUE, TileSymbol.DIAMOND));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.INVALID_LINE, actual, "Auch die Querreihen weiterer neuer Steine werden geprüft");
    }

    @Test
    @DisplayName("Die Reihensuche endet am ersten freien Feld")
    void testGapInBoardSeparatesLines() {
        Map<Position, Tile> placed = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(0, -1, TileColor.GREEN, TileSymbol.CIRCLE),
                at(1, -1, TileColor.GREEN, TileSymbol.HEXAGON),
                at(2, -1, TileColor.GREEN, TileSymbol.SQUARE),
                at(2, 0, TileColor.BLUE, TileSymbol.SQUARE));
        Map<Position, Tile> pending = tilesOf(at(-1, 0, TileColor.RED, TileSymbol.DIAMOND));

        PlacementResult actual = PlacementValidator.checkPendingTilePlacement(placed, pending);

        assertEquals(PlacementResult.LEGAL, actual, "Der blaue Stein hinter der Lücke gehört nicht zur roten Reihe");
    }

    private record Placement(Position position, Tile tile) {}

    private static Placement at(int x, int y, TileColor color, TileSymbol symbol) {
        return new Placement(new Position(x, y), new Tile(color, symbol));
    }

    private static Map<Position, Tile> tilesOf(Placement... placements) {
        Map<Position, Tile> tiles = new LinkedHashMap<>();
        for (Placement placement : placements) {
            tiles.put(placement.position(), placement.tile());
        }
        return tiles;
    }
}
