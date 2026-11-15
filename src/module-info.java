module Qwirkle {
	requires java.desktop;
    requires java.sql;
    requires static org.junit.jupiter.api;

    opens quirkle.game.gameplay.board to org.junit.platform.commons;
}
