/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 *
 * HaoHanDisplayUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package vn.haohan.displayui.demo.pages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.TextComponent;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.gradient.UiGradient;
import vn.haohan.displayui.api.gradient.UiGradientPosition;
import vn.haohan.displayui.api.layout.UiAnchorPoint;
import vn.haohan.displayui.api.text.UiTextAlignment;
import vn.haohan.displayui.demo.BaseDemoPage;
import vn.haohan.displayui.demo.DemoContext;

import java.util.List;

/**
 * Visual demo gallery for gradient backgrounds using modern Container and Component architecture.
 */
public final class GradientBackgroundDemoPage extends BaseDemoPage {

    private record CardInfo(String title, String fromLabel, String toLabel,
                            UiGradientPosition startPos, Color startColor,
                            UiGradientPosition endPos, Color endColor) {
        public String fullDirection() {
            return fromLabel + " → " + toLabel;
        }
    }

    private static final List<CardInfo> CARDS = List.of(
            new CardInfo("Horizontal", "Left", "Right",
                    UiGradientPosition.CENTER_LEFT, Color.fromRGB(220, 20, 60),
                    UiGradientPosition.CENTER_RIGHT, Color.fromRGB(25, 25, 112)),
            new CardInfo("Vertical", "Top", "Bottom",
                    UiGradientPosition.CENTER_TOP, Color.fromRGB(46, 204, 113),
                    UiGradientPosition.CENTER_BOTTOM, Color.fromRGB(22, 160, 133)),
            new CardInfo("Diagonal Down", "Top-Left", "Bottom-Right",
                    UiGradientPosition.TOP_LEFT, Color.fromRGB(155, 89, 182),
                    UiGradientPosition.BOTTOM_RIGHT, Color.fromRGB(241, 196, 15)),
            new CardInfo("Diagonal Up", "Bottom-Left", "Top-Right",
                    UiGradientPosition.BOTTOM_LEFT, Color.fromRGB(255, 105, 180),
                    UiGradientPosition.TOP_RIGHT, Color.fromRGB(0, 206, 209)),
            new CardInfo("Center Slant", "Center-Left", "Top-Right",
                    UiGradientPosition.CENTER_LEFT, Color.fromRGB(255, 215, 0),
                    UiGradientPosition.TOP_RIGHT, Color.fromRGB(30, 144, 255)),
            new CardInfo("Radial Corner", "Center", "Bottom-Right",
                    UiGradientPosition.CENTER, Color.fromRGB(255, 255, 255),
                    UiGradientPosition.BOTTOM_RIGHT, Color.fromRGB(44, 62, 80))
    );

    @Override
    public String title() {
        return "GRADIENT BACKGROUNDS";
    }

    @Override
    public void build(Container container, DemoContext context) {
        container.addComponent(TextComponent.builder("grad_subtitle")
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 0.0f)
                .size(174.0f, 6.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(Component.text("Click any gradient card below to apply it to the root background", NamedTextColor.GRAY))
                .fontSize(3.8f)
                .build());

        float panelW = 54.0f;
        float panelH = 32.0f;
        float[] colsX = {0.0f, 60.0f, 120.0f};
        float[] rowsY = {9.0f, 44.0f};

        for (int i = 0; i < CARDS.size(); i++) {
            CardInfo card = CARDS.get(i);
            float x = colsX[i % 3];
            float y = rowsY[i / 3];

            addGradientCard(container, i, x, y, panelW, panelH, card);
        }
    }

    private void addGradientCard(Container container, int index, float x, float y,
                                 float w, float h, CardInfo card) {
        Container cardContainer = Container.builder("grad_card_" + index)
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(x, y)
                .size(w, h)
                .backgroundColor(Color.fromRGB(24, 28, 38))
                .borderRound(4.0f)
                .build();

        // Title
        cardContainer.addComponent(TextComponent.builder("title_" + index)
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 2.0f)
                .size(w, 5.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(Component.text(card.title(), NamedTextColor.GOLD, TextDecoration.BOLD))
                .fontSize(3.8f)
                .shadow(true)
                .build());

        // Direction text
        Component directionComponent = Component.text()
                .append(Component.text(card.fromLabel(), NamedTextColor.GRAY))
                .append(Component.text(" → ", NamedTextColor.AQUA))
                .append(Component.text(card.toLabel(), NamedTextColor.GRAY))
                .build();

        cardContainer.addComponent(TextComponent.builder("dir_" + index)
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0.0f, 7.5f)
                .size(w, 4.0f)
                .alignment(UiTextAlignment.CENTER)
                .text(directionComponent)
                .fontSize(2.8f)
                .build());

        // Preview Swatch Container
        UiGradient swatchGrad = UiGradient.of(card.startPos(), card.startColor(), card.endPos(), card.endColor());
        Container swatch = Container.builder("swatch_" + index)
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(4.0f, 13.0f)
                .size(w - 8.0f, 9.0f)
                .gradient(swatchGrad)
                .borderRound(2.0f)
                .build();
        cardContainer.addContainer(swatch);

        // Apply Button
        cardContainer.addComponent(ButtonComponent.builder("apply_root_" + index)
                .anchor(UiAnchorPoint.CENTER_BOTTOM)
                .origin(UiAnchorPoint.CENTER_BOTTOM)
                .offset(0.0f, -2.0f)
                .size(w - 8.0f, 7.0f)
                .label("APPLY")
                .borderRound(2.0f)
                .build());

        container.addContainer(cardContainer);
    }

    @Override
    public boolean onClick(DemoContext context, String buttonId, Player player) {
        if (buttonId.startsWith("apply_root_")) {
            int cardIndex = Integer.parseInt(buttonId.substring("apply_root_".length()));
            if (cardIndex >= 0 && cardIndex < CARDS.size()) {
                CardInfo card = CARDS.get(cardIndex);
                UiGradient rootGrad = UiGradient.of(
                        card.startPos(),
                        Color.fromARGB(215,
                                Math.min(255, (int)(card.startColor().getRed() * 0.45f + 15)),
                                Math.min(255, (int)(card.startColor().getGreen() * 0.45f + 15)),
                                Math.min(255, (int)(card.startColor().getBlue() * 0.45f + 25))),
                        card.endPos(),
                        Color.fromARGB(235,
                                Math.min(255, (int)(card.endColor().getRed() * 0.40f + 10)),
                                Math.min(255, (int)(card.endColor().getGreen() * 0.40f + 10)),
                                Math.min(255, (int)(card.endColor().getBlue() * 0.40f + 20)))
                );
                context.rootGradient(rootGrad);
                context.updateView();
                player.sendMessage("§a✦ Root background gradient updated to: §e" + card.title() + " §7(" + card.fullDirection() + ")");
                return true;
            }
        }
        return false;
    }
}
