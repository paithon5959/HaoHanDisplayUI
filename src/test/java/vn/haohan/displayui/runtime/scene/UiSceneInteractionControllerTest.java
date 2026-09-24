/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 */
package vn.haohan.displayui.runtime.scene;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.haohan.displayui.api.UiDocument;
import vn.haohan.displayui.api.UiHit;
import vn.haohan.displayui.api.bridge.UiDocumentBridge;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.debug.UiDebugState;
import vn.haohan.displayui.api.layer.Layer;
import vn.haohan.displayui.api.layer.LayerManager;
import vn.haohan.displayui.api.layout.UiAnchorPoint;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UiSceneInteractionControllerTest {

    private UiScene scene;
    private UiSceneInteractionController controller;
    private Player player;

    @BeforeEach
    void setUp() {
        scene = mock(UiScene.class);
        controller = new UiSceneInteractionController(scene);
        player = mock(Player.class);
    }

    @Test
    @DisplayName("Verify button takes precedence over background container when click-inspect is enabled")
    void testButtonPriorityOverBackgroundContainerInClickInspect() {
        LayerManager lm = new LayerManager();

        // Layer 0: Background Canvas (covers -96 to 96, -64 to 64)
        Layer bgLayer = lm.createLayer("background_layer", 0);
        Container bgContainer = Container.builder("bg_canvas")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(192.0f, 128.0f)
                .build();
        bgLayer.addContainer(bgContainer);

        // Layer 1: Dialog Layer with sub-container and button
        Layer dialogLayer = lm.createLayer("dialog_layer", 1);
        Container dialog = Container.builder("demo_dialog")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(192.0f, 128.0f)
                .build();

        Container footer = Container.builder("footer_sub")
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -6.0f)
                .size(174.0f, 16.0f)
                .build();

        ButtonComponent targetButton = ButtonComponent.builder("my_target_button")
                .anchor(UiAnchorPoint.CENTER_RIGHT)
                .origin(UiAnchorPoint.CENTER_RIGHT)
                .offset(0.0f, 0.0f)
                .size(42.0f, 14.0f)
                .label("CLICK ME")
                .build();
        footer.addComponent(targetButton);
        dialog.addContainer(footer);
        dialogLayer.addContainer(dialog);

        // Apply click-inspect
        UiDebugState debugState = new UiDebugState();
        debugState.setClickInspectEnabled(true);
        debugState.apply(lm);

        UiDocument doc = UiDocumentBridge.compile(lm);

        // Verify that doc contains __inspect_cont_bg_canvas at the beginning
        assertEquals("__inspect_cont_bg_canvas", doc.buttons().get(0).id());

        // Target button center is approximately at x = 66, y = 50.
        UiHit hit = controller.findHit(doc, Collections.emptyList(), 66.0f, 50.0f, player, 2.0);

        assertNotNull(hit, "Should have found a hit");
        assertNotNull(hit.button(), "Hit target should be a button");
        assertEquals("my_target_button", hit.button().id(),
                "Hit should be the target button, NOT __inspect_cont_bg_canvas!");

        // Test hit on empty space inside demo_dialog where no buttons exist
        UiHit bgHit = controller.findHit(doc, Collections.emptyList(), -90.0f, -60.0f, player, 2.0);
        assertNotNull(bgHit);
        assertNotNull(bgHit.button());
        assertEquals("__inspect_cont_demo_dialog", bgHit.button().id(),
                "Empty space inside demo_dialog should inspect demo_dialog before bg_canvas");
    }

    @Test
    @DisplayName("Verify component inspect hitbox takes precedence over container hitbox")
    void testComponentInspectHitboxOverContainer() {
        LayerManager lm = new LayerManager();
        Layer layer = lm.createLayer("main_layer", 0);
        Container container = Container.builder("main_box")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(100.0f, 100.0f)
                .build();

        // TextComponent (non-button) in the center
        TextComponent text = TextComponent.builder("title_label")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(60.0f, 20.0f)
                .build();
        container.addComponent(text);
        layer.addContainer(container);

        UiDebugState debugState = new UiDebugState();
        debugState.setClickInspectEnabled(true);
        debugState.apply(lm);

        UiDocument doc = UiDocumentBridge.compile(lm);

        // Center is (0, 0), which is inside title_label
        UiHit hit = controller.findHit(doc, Collections.emptyList(), 0.0f, 0.0f, player, 1.0);
        assertNotNull(hit);
        assertEquals("__inspect_comp_title_label", hit.button().id(),
                "Hit on text component should inspect __inspect_comp_title_label, NOT container!");

        // Empty space in container at (40.0f, 40.0f)
        UiHit contHit = controller.findHit(doc, Collections.emptyList(), 40.0f, 40.0f, player, 1.0);
        assertNotNull(contHit);
        assertEquals("__inspect_cont_main_box", contHit.button().id(),
                "Hit on empty space in container should inspect container!");
    }
}
