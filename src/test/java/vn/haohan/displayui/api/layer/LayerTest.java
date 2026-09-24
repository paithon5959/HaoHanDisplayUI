/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 */
package vn.haohan.displayui.api.layer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.haohan.displayui.api.container.Container;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LayerTest {

    @Test
    @DisplayName("Layer depth allocation guarantees zero Z-fighting")
    void testLayerDepthAllocation() {
        Layer bgLayer = new Layer("background", 0);
        Layer contentLayer = new Layer("content", 1);
        Layer modalLayer = new Layer("modal", 2);

        assertEquals(0.000f, bgLayer.baseDepth(), 1e-6f);
        assertEquals(0.020f, contentLayer.baseDepth(), 1e-6f);
        assertEquals(0.040f, modalLayer.baseDepth(), 1e-6f);

        // Check sub-depth within layer
        float d0 = contentLayer.allocateDepth(0);
        float d1 = contentLayer.allocateDepth(1);
        float d2 = contentLayer.allocateDepth(2);

        assertTrue(d1 > d0, "Depth must strictly increase with index to prevent Z-fighting");
        assertTrue(d2 > d1, "Depth must strictly increase with index to prevent Z-fighting");

        // Ensure elements of contentLayer NEVER collide with modalLayer
        float lastContentDepth = contentLayer.allocateDepth(30);
        assertTrue(lastContentDepth < modalLayer.baseDepth(),
                "Layer sub-depths must not leak into next layer's base depth!");
    }

    @Test
    @DisplayName("LayerManager sorting by Z-index")
    void testLayerManagerOrder() {
        LayerManager manager = new LayerManager();
        manager.createLayer("modal", 2);
        manager.createLayer("background", 0);
        manager.createLayer("content", 1);

        assertEquals(3, manager.layerCount());

        List<Layer> sorted = manager.layers();
        assertEquals("background", sorted.get(0).id());
        assertEquals(0, sorted.get(0).zIndex());

        assertEquals("content", sorted.get(1).id());
        assertEquals(1, sorted.get(1).zIndex());

        assertEquals("modal", sorted.get(2).id());
        assertEquals(2, sorted.get(2).zIndex());
    }

    @Test
    @DisplayName("Container registration on Layer")
    void testContainerOnLayer() {
        Layer contentLayer = new Layer("content", 1);
        Container card = Container.builder("card_1").build();

        contentLayer.addContainer(card);
        assertEquals(1, contentLayer.containers().size());
        assertTrue(card.layer().isPresent());
        assertEquals(contentLayer, card.layer().get());

        contentLayer.removeContainer("card_1");
        assertEquals(0, contentLayer.containers().size());
        assertTrue(card.layer().isEmpty());
    }
}
