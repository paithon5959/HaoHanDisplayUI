/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 */
package vn.haohan.displayui.api.container;

import org.bukkit.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.haohan.displayui.api.animation.UiAnimation;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.Component;
import vn.haohan.displayui.api.component.SliderComponent;
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.layout.UiAnchorPoint;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ContainerTest {

    @Test
    @DisplayName("Container creation, size, styling, and basic component addition")
    void testBasicContainer() {
        Container container = Container.builder("card_main")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(200.0f, 150.0f)
                .backgroundColor(Color.fromRGB(20, 25, 35))
                .borderRound(10.0f)
                .build();

        assertEquals("card_main", container.id());
        assertEquals(200.0f, container.width());
        assertEquals(150.0f, container.height());
        assertEquals(10.0f, container.borderRound());
        assertEquals(Color.fromRGB(20, 25, 35), container.backgroundColor());

        TextComponent title = TextComponent.builder("title").text("Main Card").build();
        container.addComponent(title);

        assertEquals(1, container.components().size());
        assertTrue(container.getComponent("title").isPresent());
        assertTrue(title.parent().isPresent());
        assertEquals(container, title.parent().get());

        container.removeComponent("title");
        assertEquals(0, container.components().size());
        assertTrue(title.parent().isEmpty());
    }

    @Test
    @DisplayName("Nested child containers and recursive search")
    void testNestedContainers() {
        Container parentCard = Container.builder("parent_card").size(300, 200).build();
        Container headerSub = Container.builder("header_sub").size(280, 40).build();
        Container controlsSub = Container.builder("controls_sub").size(280, 100).build();

        TextComponent headerText = TextComponent.builder("header_label").text("Header").build();
        headerSub.addComponent(headerText);

        SliderComponent volSlider = SliderComponent.builder("volume_slider").build();
        controlsSub.addComponent(volSlider);

        parentCard.addContainer(headerSub);
        parentCard.addContainer(controlsSub);

        assertEquals(2, parentCard.childContainers().size());
        assertTrue(headerSub.parentContainer().isPresent());
        assertEquals(parentCard, headerSub.parentContainer().get());

        // Recursive search for components deep inside subcontainers
        Optional<Component> foundHeader = parentCard.findComponent("header_label");
        assertTrue(foundHeader.isPresent());
        assertEquals(headerText, foundHeader.get());

        Optional<Component> foundSlider = parentCard.findComponent("volume_slider");
        assertTrue(foundSlider.isPresent());
        assertEquals(volSlider, foundSlider.get());

        // Recursive search for container
        assertTrue(parentCard.findContainer("controls_sub").isPresent());
    }

    @Test
    @DisplayName("Scale cascade propagation: Parent -> Child Container -> Component")
    void testScaleCascade() {
        Container parent = Container.builder("root_card").scale(1.5f).build();
        Container child = Container.builder("child_box").scale(2.0f).build();
        ButtonComponent button = ButtonComponent.builder("btn").scale(0.5f).build();

        parent.addContainer(child);
        child.addComponent(button);

        // Parent scale = 1.5
        assertEquals(1.5f, parent.effectiveScale(), 1e-5f);

        // Child effective scale = parent (1.5) * child (2.0) = 3.0
        assertEquals(3.0f, child.effectiveScale(), 1e-5f);

        // Button effective scale = child effective (3.0) * button local (0.5) = 1.5
        assertEquals(1.5f, button.effectiveScale(), 1e-5f);
    }

    @Test
    @DisplayName("Opacity cascade propagation down container tree")
    void testOpacityCascade() {
        Container parent = Container.builder("root").opacity(0.8f).build();
        Container child = Container.builder("sub").opacity(0.5f).build();
        TextComponent text = TextComponent.builder("txt").opacity(0.5f).build();

        parent.addContainer(child);
        child.addComponent(text);

        assertEquals(0.8f, parent.effectiveOpacity(), 1e-5f);
        assertEquals(0.4f, child.effectiveOpacity(), 1e-5f);
        assertEquals(0.2f, text.effectiveOpacity(), 1e-5f);
    }

    @Test
    @DisplayName("Scoped animation cascade and sibling isolation")
    void testScopedAnimationCascade() {
        Container parent = Container.builder("parent").build();
        Container targetSub = Container.builder("target_sub").build();
        Container siblingSub = Container.builder("sibling_sub").build();

        TextComponent targetText = TextComponent.builder("txt_target").build();
        TextComponent siblingText = TextComponent.builder("txt_sibling").build();

        parent.addContainer(targetSub);
        parent.addContainer(siblingSub);
        targetSub.addComponent(targetText);
        siblingSub.addComponent(siblingText);

        // Before animation
        assertFalse(targetSub.isAnimating());
        assertFalse(targetSub.isCascadeAnimating());
        assertFalse(siblingSub.isAnimating());
        assertFalse(siblingSub.isCascadeAnimating());

        // Trigger animation ONLY on targetSub
        UiAnimation anim = UiAnimation.fadeIn(12);
        targetSub.animate(anim);

        assertTrue(targetSub.isAnimating());
        assertTrue(targetSub.isCascadeAnimating());

        // Crucial test: siblingSub MUST remain completely static!
        assertFalse(siblingSub.isAnimating(), "Sibling container must not animate!");
        assertFalse(siblingSub.isCascadeAnimating(), "Sibling container must not be in cascade animation!");
        assertFalse(parent.isAnimating(), "Parent container must not animate!");
    }

    @Test
    @DisplayName("Real-time update listeners on Container")
    void testRealTimeUpdate() {
        Container container = Container.builder("live_container").build();
        AtomicBoolean updated = new AtomicBoolean(false);

        container.onUpdate(c -> updated.set(true));
        container.update();

        assertTrue(updated.get(), "Container update callback must be invoked");
    }

    @Test
    @DisplayName("Compute global position and bounds for root and nested containers")
    void testGlobalPositionAndBounds() {
        Container root = Container.builder("root_dialog")
                .anchor(UiAnchorPoint.CENTER)
                .origin(UiAnchorPoint.CENTER)
                .size(192.0f, 128.0f)
                .build();

        float[] rootPos = root.computeGlobalPosition();
        assertEquals(-96.0f, rootPos[0], 1e-4f);
        assertEquals(-64.0f, rootPos[1], 1e-4f);
        assertEquals(-96.0f, root.computeGlobalBounds().x(), 1e-4f);
        assertEquals(-64.0f, root.computeGlobalBounds().y(), 1e-4f);

        Container child = Container.builder("child")
                .anchor(UiAnchorPoint.CENTER_TOP)
                .origin(UiAnchorPoint.CENTER_TOP)
                .offset(0.0f, 6.0f)
                .size(174.0f, 18.0f)
                .build();
        root.addContainer(child);

        float[] childPos = child.computeGlobalPosition();
        // localX: 192*0.5 - 174*0.5 = 9.0; globalX: -96 + 9 = -87.0
        // localY: 0 + 6 = 6.0; globalY: -64 + 6 = -58.0
        assertEquals(-87.0f, childPos[0], 1e-4f);
        assertEquals(-58.0f, childPos[1], 1e-4f);
    }
}
