package vn.haohan.displayui.runtime.scene;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.haohan.displayui.api.UiDocument;
import vn.haohan.displayui.api.node.AlignedTextNode;
import vn.haohan.displayui.api.node.UiBackgroundNode;
import vn.haohan.displayui.api.text.UiTextAlignment;
import org.bukkit.Color;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiSceneIncrementalUpdateTest {

    @Test
    @DisplayName("Documents on the same page with matching node positions are considered structurally similar")
    void testSamePageStructureMatches() {
        UiDocument docA = UiDocument.builder()
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("A"), 10.0f, 10.0f, 100.0f, 20.0f, UiTextAlignment.LEFT))
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("B"), 10.0f, 40.0f, 100.0f, 20.0f, UiTextAlignment.LEFT))
                .build();

        UiDocument docB = UiDocument.builder()
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("A Updated"), 10.0f, 10.0f, 100.0f, 20.0f, UiTextAlignment.LEFT))
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("B Updated"), 10.0f, 40.0f, 100.0f, 20.0f, UiTextAlignment.LEFT))
                .build();

        assertTrue(UiScene.isStructurallySimilar(docA, docB));
    }

    @Test
    @DisplayName("Documents from different pages with mismatched node positions and sizes are rejected")
    void testDifferentPageStructureRejects() {
        // Page 1: 2 small text nodes and footer button at y=110
        UiDocument page1 = UiDocument.builder()
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("Title"), 10.0f, 10.0f, 100.0f, 20.0f, UiTextAlignment.LEFT))
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("Next >"), 62.0f, 110.0f, 18.0f, 14.0f, UiTextAlignment.CENTER))
                .build();

        // Page 2: completely different layout where index 1 is now a large row at y=38
        UiDocument page2 = UiDocument.builder()
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("Title"), 10.0f, 10.0f, 100.0f, 20.0f, UiTextAlignment.LEFT))
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("Gold Coins Card"), 0.0f, 38.0f, 174.0f, 16.0f, UiTextAlignment.LEFT))
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("Next >"), 62.0f, 110.0f, 18.0f, 14.0f, UiTextAlignment.CENTER))
                .build();

        assertFalse(UiScene.isStructurallySimilar(page1, page2));
    }

    @Test
    @DisplayName("Null or empty documents edge cases")
    void testNullAndEmptyDocuments() {
        UiDocument empty1 = UiDocument.builder().build();
        UiDocument empty2 = UiDocument.builder().build();
        UiDocument nonEmpty = UiDocument.builder()
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("X"), 0.0f, 0.0f, 10.0f, 10.0f, UiTextAlignment.LEFT))
                .build();

        assertFalse(UiScene.isStructurallySimilar(null, empty1));
        assertFalse(UiScene.isStructurallySimilar(empty1, null));
        assertTrue(UiScene.isStructurallySimilar(empty1, empty2));
        assertFalse(UiScene.isStructurallySimilar(empty1, nonEmpty));
        assertFalse(UiScene.isStructurallySimilar(nonEmpty, empty1));
    }

    @Test
    @DisplayName("Documents with mismatched node types are not matched")
    void testMismatchedNodeTypes() {
        UiDocument docText = UiDocument.builder()
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("Text"), 10.0f, 10.0f, 100.0f, 20.0f, UiTextAlignment.LEFT))
                .build();
        UiDocument docBg = UiDocument.builder()
                .add(new UiBackgroundNode(10.0f, 10.0f, 0.0f, 100.0f, 20.0f, Color.WHITE))
                .build();

        assertFalse(UiScene.isStructurallySimilar(docText, docBg));
    }

    @Test
    @DisplayName("Position drift greater than 15px is rejected when ratio drops below 50%")
    void testPositionDriftThreshold() {
        UiDocument doc1 = UiDocument.builder()
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("1"), 10.0f, 10.0f, 100.0f, 20.0f, UiTextAlignment.LEFT))
                .build();
        UiDocument doc2 = UiDocument.builder()
                .add(new AlignedTextNode(net.kyori.adventure.text.Component.text("1"), 30.0f, 10.0f, 100.0f, 20.0f, UiTextAlignment.LEFT))
                .build();

        assertFalse(UiScene.isStructurallySimilar(doc1, doc2));
    }
}
