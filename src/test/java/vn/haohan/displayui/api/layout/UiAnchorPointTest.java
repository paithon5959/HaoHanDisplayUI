/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 */
package vn.haohan.displayui.api.layout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class UiAnchorPointTest {

    @Test
    @DisplayName("Verify normalized coordinates for all 9 directions")
    void testFactors() {
        assertFactors(UiAnchorPoint.TOP_LEFT, 0.0f, 0.0f);
        assertFactors(UiAnchorPoint.CENTER_TOP, 0.5f, 0.0f);
        assertFactors(UiAnchorPoint.TOP_RIGHT, 1.0f, 0.0f);
        assertFactors(UiAnchorPoint.CENTER_LEFT, 0.0f, 0.5f);
        assertFactors(UiAnchorPoint.CENTER, 0.5f, 0.5f);
        assertFactors(UiAnchorPoint.CENTER_RIGHT, 1.0f, 0.5f);
        assertFactors(UiAnchorPoint.BOTTOM_LEFT, 0.0f, 1.0f);
        assertFactors(UiAnchorPoint.CENTER_BOTTOM, 0.5f, 1.0f);
        assertFactors(UiAnchorPoint.BOTTOM_RIGHT, 1.0f, 1.0f);
    }

    private void assertFactors(UiAnchorPoint point, float expectedX, float expectedY) {
        assertEquals(expectedX, point.xFactor(), 1e-6f, "X factor mismatch for " + point);
        assertEquals(expectedY, point.yFactor(), 1e-6f, "Y factor mismatch for " + point);
    }

    @Test
    @DisplayName("Anchor TOP_LEFT and Origin TOP_LEFT with offset")
    void testTopLeftToTopLeft() {
        float[] pos = UiAnchorPoint.TOP_LEFT.resolve(400, 300, 20, 30, 100, 50, UiAnchorPoint.TOP_LEFT);
        assertEquals(20.0f, pos[0], 1e-6f);
        assertEquals(30.0f, pos[1], 1e-6f);
    }

    @Test
    @DisplayName("Anchor CENTER and Origin CENTER aligns perfectly in middle")
    void testCenterToCenter() {
        // Parent 400x300, Child 100x50
        // Anchor at (200, 150)
        // Origin offset is (50, 25)
        // Expected left = 200 - 50 = 150, top = 150 - 25 = 125
        float[] pos = UiAnchorPoint.CENTER.resolve(400, 300, 0, 0, 100, 50, UiAnchorPoint.CENTER);
        assertEquals(150.0f, pos[0], 1e-6f);
        assertEquals(125.0f, pos[1], 1e-6f);
    }

    @Test
    @DisplayName("Anchor BOTTOM_RIGHT and Origin BOTTOM_RIGHT pins to bottom right corner")
    void testBottomRightToBottomRight() {
        // Parent 400x300, Child 100x50, Offset (-10, -10)
        // Anchor at (400, 300)
        // Pos = (390, 290)
        // Origin offset = (100, 50)
        // Left = 390 - 100 = 290, Top = 290 - 50 = 240
        float[] pos = UiAnchorPoint.BOTTOM_RIGHT.resolve(400, 300, -10, -10, 100, 50, UiAnchorPoint.BOTTOM_RIGHT);
        assertEquals(290.0f, pos[0], 1e-6f);
        assertEquals(240.0f, pos[1], 1e-6f);
    }

    @ParameterizedTest
    @EnumSource(UiAnchorPoint.class)
    @DisplayName("Full 81-way combination resolution check")
    void testAllCombinations(UiAnchorPoint anchor) {
        float parentW = 500.0f;
        float parentH = 400.0f;
        float childW = 80.0f;
        float childH = 60.0f;
        float offX = 15.0f;
        float offY = -10.0f;

        for (UiAnchorPoint origin : UiAnchorPoint.values()) {
            float expectedX = (parentW * anchor.xFactor()) + offX - (childW * origin.xFactor());
            float expectedY = (parentH * anchor.yFactor()) + offY - (childH * origin.yFactor());

            float[] resolved = anchor.resolve(parentW, parentH, offX, offY, childW, childH, origin);
            assertEquals(expectedX, resolved[0], 1e-5f);
            assertEquals(expectedY, resolved[1], 1e-5f);
            assertEquals(expectedX, anchor.resolveX(parentW, offX, childW, origin), 1e-5f);
            assertEquals(expectedY, anchor.resolveY(parentH, offY, childH, origin), 1e-5f);
        }
    }
}
