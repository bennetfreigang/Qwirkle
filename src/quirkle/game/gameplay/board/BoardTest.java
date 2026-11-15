package quirkle.game.gameplay.board;

import java.util.Map;

import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileColor;
import quirkle.game.gameplay.tiles.TileSymbol;
import quirkle.testing.Test;

import static quirkle.testing.Assertions.assertEquals;

public class BoardTest {

    @Test
    void testCheckPlacementRejectsCommittedPosition() {
        Position position = new Position(0, 0);
        Tile original = new Tile(TileColor.RED, TileSymbol.CIRCLE);
        Board board = new Board(Map.of(position, original), Map.of());

        PlacementResult result = board.checkPlacement(position, new Tile(TileColor.RED, TileSymbol.SQUARE));

        assertEquals(PlacementResult.POSITION_OCCUPIED, result, "Ein vorhandener Stein belegt seine Position");
        assertEquals(Map.of(position, original), board.getPlacedTiles(), "Der vorhandene Stein bleibt erhalten");
        assertEquals(Map.of(), board.getPendingTiles(), "Die Prüfung legt keinen Stein ab");
    }

    @Test
    void testCheckPlacementRejectsPendingPosition() {
        Position position = new Position(0, 0);
        Tile original = new Tile(TileColor.RED, TileSymbol.CIRCLE);
        Board board = new Board(Map.of(), Map.of(position, original));

        PlacementResult result = board.checkPlacement(position, new Tile(TileColor.RED, TileSymbol.SQUARE));

        assertEquals(PlacementResult.POSITION_OCCUPIED, result, "Ein vorgemerkter Stein belegt seine Position");
        assertEquals(Map.of(position, original), board.getPendingTiles(), "Der vorgemerkte Stein bleibt erhalten");
    }

    @Test
    void testCheckPlacementIncludesPendingTilesWithoutStagingCandidate() {
        Tile original = new Tile(TileColor.RED, TileSymbol.CIRCLE);
        Map<Position, Tile> pending = Map.of(new Position(0, 0), original);
        Board board = new Board(Map.of(), pending);

        PlacementResult result = board.checkPlacement(new Position(1, 0), new Tile(TileColor.RED, TileSymbol.SQUARE));

        assertEquals(PlacementResult.LEGAL, result, "Der neue Stein ergänzt den vorgemerkten Stein");
        assertEquals(pending, board.getPendingTiles(), "Die Prüfung legt den Kandidaten nicht ab");
        assertEquals(Map.of(), board.getPlacedTiles(), "Die Prüfung verändert das Brett nicht");
    }

    @Test
    void testCheckPlacementRejectsDuplicateOfPendingTile() {
        Map<Position, Tile> pending = Map.of(new Position(0, 0), new Tile(TileColor.RED, TileSymbol.CIRCLE));
        Board board = new Board(Map.of(), pending);

        PlacementResult result = board.checkPlacement(new Position(1, 0), new Tile(TileColor.RED, TileSymbol.CIRCLE));

        assertEquals(PlacementResult.INVALID_LINE, result, "Bereits vorgemerkte Steine werden mitgeprüft");
        assertEquals(pending, board.getPendingTiles(), "Ein ungültiger Kandidat wird nicht abgelegt");
    }

    @Test
    void testEmptyPlacementCanBeCheckedAndCommitted() {
        Map<Position, Tile> placed = Map.of(new Position(0, 0), new Tile(TileColor.RED, TileSymbol.CIRCLE));
        Board board = new Board(placed, Map.of());

        assertEquals(PlacementResult.LEGAL, board.checkPendingPlacement(), "Eine leere Platzierung ist gültig");
        assertEquals(0, board.commitPendingTiles(), "Ohne neue Steine gibt es keine Punkte");
        assertEquals(placed, board.getPlacedTiles(), "Das Brett bleibt erhalten");
        assertEquals(Map.of(), board.getPendingTiles(), "Es bleiben keine vorgemerkten Steine zurück");
    }
}
