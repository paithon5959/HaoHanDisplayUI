/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 */
package vn.haohan.displayui.api.bridge;

import org.bukkit.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.haohan.displayui.api.UiDocument;
import vn.haohan.displayui.api.component.*;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.gradient.UiGradient;
import vn.haohan.displayui.api.layer.Layer;
import vn.haohan.displayui.api.layer.LayerManager;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.node.UiNode;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UiDocumentBridgeTest {

    @Test
    @DisplayName("Compile LayerManager into UiDocument with unique non-colliding depths")
    void testCompileLayerManager() {
        LayerManager manager = new LayerManager();
        Layer bgLayer = manager.createLayer("bg", 0)
                .gradient(UiGradient.horizontal(Color.BLACK, Color.BLUE));
        Layer mainLayer = manager.createLayer("main", 1);

        Container card = Container.builder("card")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(200, 150)
                .backgroundColor(Color.fromRGB(30, 30, 40))
                .borderRound(8.0f)
                .build();

        TextComponent title = TextComponent.builder("title")
                .text("Title")
                .anchor(UiAnchorPoint.CENTER_TOP)
                .origin(UiAnchorPoint.CENTER_TOP)
                .size(100, 20)
                .build();

        ButtonComponent btn = ButtonComponent.builder("btn_1")
                .label("OK")
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .size(60, 24)
                .build();

        card.addComponent(title);
        card.addComponent(btn);
        mainLayer.addContainer(card);

        UiDocument doc = UiDocumentBridge.compile(manager);
        assertNotNull(doc);
        assertFalse(doc.nodes().isEmpty());
        assertEquals(1, doc.buttons().size());

        // Verify every generated node has a strictly unique depth to avoid Z-fighting!
        Set<Float> depths = new HashSet<>();
        for (UiNode node : doc.nodes()) {
            assertFalse(depths.contains(node.depth()),
                    "Found duplicate depth " + node.depth() + ", Z-fighting would occur!");
            depths.add(node.depth());
        }
    }

    @Test
    @DisplayName("Compile nested containers and verify coordinate inheritance")
    void testCompileNestedContainers() {
        Container root = Container.builder("root")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .size(300, 300)
                .build();

        Container sub = Container.builder("sub")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(100, 100)
                .build();

        ButtonComponent btn = ButtonComponent.builder("nested_btn")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(40, 20)
                .build();

        sub.addComponent(btn);
        root.addContainer(sub);

        UiDocument doc = UiDocumentBridge.compile(root);
        assertEquals(1, doc.buttons().size());
        assertEquals("nested_btn", doc.buttons().get(0).id());

        // sub is centered in root (300x300): top-left at (100, 100)
        // btn (40x20) is centered in sub (100x100): top-left in sub at (30, 40)
        // Global button position: (100 + 30 = 130, 100 + 40 = 140)
        assertEquals(130.0f, doc.buttons().get(0).x(), 1e-4f);
        assertEquals(140.0f, doc.buttons().get(0).y(), 1e-4f);
    }

    @Test
    @DisplayName("Compile centered root container with nested header/footer hierarchy")
    void testCompileCenteredRootContainerAndHierarchy() {
        LayerManager manager = new LayerManager();
        Layer bgLayer = manager.createLayer("bg", 0)
                .bounds(-96, -64, 192, 128)
                .gradient(UiGradient.horizontal(Color.RED, Color.BLUE))
                .doubleSided(true);

        Layer mainLayer = manager.createLayer("main", 1);
        Container mainDialog = Container.builder("dialog")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(192.0f, 128.0f)
                .build();

        Container header = Container.builder("header")
                .anchor(UiAnchorPoint.CENTER_TOP)
                .origin(UiAnchorPoint.CENTER_TOP)
                .offset(0.0f, 6.0f)
                .size(174.0f, 18.0f)
                .build();

        Container footer = Container.builder("footer")
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -6.0f)
                .size(174.0f, 16.0f)
                .build();

        ButtonComponent prevBtn = ButtonComponent.builder("prev_btn")
                .anchor(UiAnchorPoint.CENTER_LEFT)
                .origin(UiAnchorPoint.CENTER_LEFT)
                .offset(0.0f, 0.0f)
                .size(18.0f, 14.0f)
                .build();
        footer.addComponent(prevBtn);

        mainDialog.addContainer(header);
        mainDialog.addContainer(footer);
        mainLayer.addContainer(mainDialog);

        UiDocument doc = UiDocumentBridge.compile(manager);
        assertNotNull(doc);
        assertEquals(1, doc.buttons().size());

        // prevBtn is in footer:
        // mainDialog global: (-96, -64)
        // footer local inside mainDialog: localX = 9.0, localY = 128 - 6 - 16 = 106.0
        // footer global: (-96 + 9 = -87, -64 + 106 = +42)
        // prevBtn local inside footer: localX = 0, localY = 16*0.5 - 14*0.5 = 1.0
        // prevBtn global: (-87 + 0 = -87.0f, +42 + 1 = +43.0f)
        assertEquals(-87.0f, doc.buttons().get(0).x(), 1e-4f);
        assertEquals(43.0f, doc.buttons().get(0).y(), 1e-4f);
    }

    @Test
    @DisplayName("Compile controls (Slider and Checkbox) into UiDocument")
    void testCompileControls() {
        Container container = Container.builder("controls_box").size(200, 200).build();
        SliderComponent slider = SliderComponent.builder("my_slider").range(0, 10, 5).build();
        CheckboxComponent checkbox = CheckboxComponent.builder("my_checkbox").checked(true).build();

        container.addComponent(slider);
        container.addComponent(checkbox);

        UiDocument doc = UiDocumentBridge.compile(container);
        assertEquals(2, doc.controls().size());
        assertTrue(doc.controls().stream().anyMatch(c -> c.id().equals("my_slider")));
        assertTrue(doc.controls().stream().anyMatch(c -> c.id().equals("my_checkbox")));
    }

    @Test
    @DisplayName("Verify CompiledUi can be generated cleanly for replace calls")
    void testCompileForReplacement() {
        LayerManager lm = new LayerManager();
        lm.createLayer("test", 0);
        UiDocumentBridge.CompiledUi compiled = UiDocumentBridge.compileWithAnimations(lm);
        assertNotNull(compiled.document());
    }
}
