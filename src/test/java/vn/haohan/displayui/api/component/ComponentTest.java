/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 */
package vn.haohan.displayui.api.component;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.haohan.displayui.api.animation.UiAnimation;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.gradient.UiGradient;
import vn.haohan.displayui.api.interaction.event.UiButtonClickEvent;
import vn.haohan.displayui.api.interaction.event.UiControlChangeEvent;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.layout.UiRect;
import vn.haohan.displayui.api.text.UiTextAlignment;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ComponentTest {

    @Test
    @DisplayName("TextComponent creation, properties, and alignment")
    void testTextComponent() {
        TextComponent text = TextComponent.builder("title_text")
                .text("Welcome to Server")
                .anchor(UiAnchorPoint.CENTER_TOP)
                .origin(UiAnchorPoint.CENTER_TOP)
                .offset(0.0f, 10.0f)
                .size(150.0f, 20.0f)
                .alignment(UiTextAlignment.CENTER)
                .fontSize(9.0f)
                .shadow(true)
                .borderRound(4.0f)
                .color(Color.YELLOW)
                .build();

        assertEquals("title_text", text.id());
        assertEquals(UiAnchorPoint.CENTER_TOP, text.anchor());
        assertEquals(UiAnchorPoint.CENTER_TOP, text.origin());
        assertEquals(0.0f, text.x());
        assertEquals(10.0f, text.y());
        assertEquals(150.0f, text.width());
        assertEquals(20.0f, text.height());
        assertEquals(UiTextAlignment.CENTER, text.alignment());
        assertEquals(9.0f, text.fontSize());
        assertTrue(text.shadow());
        assertEquals(Color.YELLOW, text.color());
        assertEquals(4.0f, text.borderRound());
    }

    @Test
    @DisplayName("ButtonComponent click dispatching and hitSlop")
    void testButtonComponent() {
        AtomicBoolean clicked = new AtomicBoolean(false);

        ButtonComponent btn = ButtonComponent.builder("submit_btn")
                .label("Click Me")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(80.0f, 25.0f)
                .borderRound(8.0f)
                .hitSlop(5.0f)
                .onClick(event -> clicked.set(true))
                .build();

        assertEquals("submit_btn", btn.id());
        assertEquals(5.0f, btn.hitSlop());
        assertEquals(8.0f, btn.borderRound());

        btn.triggerClick(vn.haohan.displayui.api.component.event.ComponentClickEvent.of(btn, null));
        assertTrue(clicked.get(), "Button click listener should have been executed");
    }

    @Test
    @DisplayName("SliderComponent stepped value snapping and event triggering")
    void testSliderComponent() {
        AtomicReference<Double> changedVal = new AtomicReference<>(0.0);

        SliderComponent slider = SliderComponent.builder("music_slider")
                .range(0.0, 100.0, 50.0)
                .step(10.0)
                .onValueChange(event -> changedVal.set(event.newValue()))
                .build();

        assertEquals(50.0, slider.value());
        assertEquals(0.5, slider.normalizedProgress(), 1e-6);

        // Snap test
        slider.value(73.0);
        assertEquals(70.0, slider.value(), "Value should snap to nearest step (70.0)");

        slider.triggerChange(vn.haohan.displayui.api.component.event.ComponentChangeEvent.of(slider, null, 50.0, 80.0));
        assertEquals(80.0, changedVal.get());
    }

    @Test
    @DisplayName("CheckboxComponent toggle event and styling")
    void testCheckboxComponent() {
        AtomicBoolean toggled = new AtomicBoolean(false);

        CheckboxComponent checkbox = CheckboxComponent.builder("pvp_toggle")
                .checked(false)
                .activeBoxColor(Color.GREEN)
                .inactiveBoxColor(Color.GRAY)
                .onToggle(event -> toggled.set(true))
                .build();

        assertFalse(checkbox.checked());
        assertEquals(Color.GRAY, checkbox.currentBoxColor());

        checkbox.triggerToggle(vn.haohan.displayui.api.component.event.ComponentChangeEvent.of(checkbox, null, 0.0, 1.0));
        assertTrue(checkbox.checked());
        assertTrue(toggled.get());
        assertEquals(Color.GREEN, checkbox.currentBoxColor());
    }

    @Test
    @DisplayName("ShapeComponent geometry and rotation properties")
    void testShapeComponent() {
        ShapeComponent shape = ShapeComponent.builder("badge_shape")
                .shapeType("rounded_rect")
                .size(40.0f, 40.0f)
                .borderRound(12.0f)
                .rotation(45.0f)
                .outline(true)
                .outlineThickness(2.5f)
                .build();

        assertEquals("rounded_rect", shape.shapeType());
        assertEquals(45.0f, shape.rotation());
        assertTrue(shape.outline());
        assertEquals(2.5f, shape.outlineThickness());
        assertEquals(12.0f, shape.borderRound());
    }

    @Test
    @DisplayName("Independent Component Animation")
    void testComponentIndependentAnimation() {
        TextComponent text = new TextComponent("anim_text");
        assertFalse(text.isAnimating());

        UiAnimation anim = UiAnimation.fadeIn(10);
        text.animate(anim);

        assertTrue(text.isAnimating());
        assertTrue(text.activeAnimation().isPresent());
        assertEquals(anim, text.activeAnimation().get());

        text.stopAnimation();
        assertFalse(text.isAnimating());
        assertTrue(text.activeAnimation().isEmpty());
    }

    @Test
    @DisplayName("Component position and bounding box computation")
    void testComputeBounds() {
        TextComponent text = TextComponent.builder("pos_test")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(100.0f, 40.0f)
                .offset(10.0f, -5.0f)
                .build();

        // Parent container is 300x200
        // Anchor CENTER = (150, 100)
        // Offset = (10, -5) -> (160, 95)
        // Origin CENTER = (50, 20)
        // Left = 160 - 50 = 110, Top = 95 - 20 = 75
        UiRect bounds = text.computeBounds(300.0f, 200.0f);
        assertEquals(110.0f, bounds.x(), 1e-5f);
        assertEquals(75.0f, bounds.y(), 1e-5f);
        assertEquals(100.0f, bounds.width(), 1e-5f);
        assertEquals(40.0f, bounds.height(), 1e-5f);
    }

    @Test
    @DisplayName("IconComponent properties and item initialization")
    void testIconComponent() {
        IconComponent icon = IconComponent.builder("icon_emerald")
                .material(org.bukkit.Material.EMERALD)
                .size(24.0f, 24.0f)
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(12.0f, 15.0f)
                .doubleSided(true)
                .build();

        assertEquals("icon_emerald", icon.id());
        assertEquals(org.bukkit.Material.EMERALD, icon.material());
        assertEquals(24.0f, icon.width());
        assertEquals(24.0f, icon.height());
        assertTrue(icon.doubleSided());
    }

    @Test
    @DisplayName("CustomNodeComponent factory instantiation")
    void testCustomNodeComponent() {
        CustomNodeComponent custom = CustomNodeComponent.builder("custom_shape")
                .size(50.0f, 50.0f)
                .factory((x, y, w, h, d, s) -> new vn.haohan.displayui.api.node.UiBackgroundNode(
                        x, y, d, w, h, org.bukkit.Color.RED))
                .build();

        assertEquals("custom_shape", custom.id());
        assertNotNull(custom.factory());
        vn.haohan.displayui.api.node.UiNode node = custom.instantiate(10, 20, 50, 50, 0.005f, 1.0f);
        assertNotNull(node);
        assertTrue(node instanceof vn.haohan.displayui.api.node.UiBackgroundNode);
    }
}
