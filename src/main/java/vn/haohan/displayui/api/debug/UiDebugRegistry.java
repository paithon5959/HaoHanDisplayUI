/*
 * Copyright (C) 2026 HaoHanSMP
 *
 * This file is part of HaoHanDisplayUI.
 *
 * HaoHanDisplayUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HaoHanDisplayUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HaoHanDisplayUI. If not, see <https://www.gnu.org/licenses/>.
 */
package vn.haohan.displayui.api.debug;

import vn.haohan.displayui.api.animation.Easings;
import vn.haohan.displayui.api.animation.UiAnimation;
import vn.haohan.displayui.api.animation.UiEffects;
import vn.haohan.displayui.api.component.Component;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.layer.Layer;
import vn.haohan.displayui.api.layer.LayerManager;

import java.util.*;

/**
 * Registry containing known identifiers of layers, containers, and components,
 * facilitating autocompletion and element discovery during runtime debugging.
 */
public class UiDebugRegistry {

    public static final List<String> KNOWN_ACTIONS = List.of(
            "show", "hide", "toggle", "status", "anim"
    );

    public static final List<String> KNOWN_SOLO_TYPES = List.of(
            "layer", "container", "component"
    );

    public static final List<String> KNOWN_ANIMATION_PRESETS = List.of(
            "fadein", "fadeout", "popin", "bouncein", "dropin", "softrise",
            "slideleft", "slideright", "slidetop", "slidebottom",
            "scalein", "scaleout", "stop", "reset"
    );

    public static final List<String> COMMON_DURATIONS = List.of(
            "6", "10", "14", "18", "24", "30"
    );

    protected final Set<String> registeredLayers = new LinkedHashSet<>();
    protected final Set<String> registeredContainers = new LinkedHashSet<>();
    protected final Set<String> registeredComponents = new LinkedHashSet<>();

    public UiDebugRegistry() {
    }

    public void registerLayers(Collection<String> ids) {
        if (ids != null) registeredLayers.addAll(ids);
    }

    public void registerContainers(Collection<String> ids) {
        if (ids != null) registeredContainers.addAll(ids);
    }

    public void registerComponents(Collection<String> ids) {
        if (ids != null) registeredComponents.addAll(ids);
    }

    /**
     * Extracts all layer IDs currently present in the active LayerManager,
     * merging with statically registered layer IDs.
     */
    public Set<String> getLayerIds(LayerManager manager) {
        Set<String> set = new LinkedHashSet<>(registeredLayers);
        if (manager != null) {
            for (Layer layer : manager.layers()) {
                set.add(layer.id());
            }
        }
        return set;
    }

    /**
     * Extracts all container IDs currently present in the active LayerManager,
     * merging with statically registered container IDs.
     */
    public Set<String> getContainerIds(LayerManager manager) {
        Set<String> set = new LinkedHashSet<>(registeredContainers);
        if (manager != null) {
            for (Layer layer : manager.layers()) {
                for (Container root : layer.containers()) {
                    collectContainerIds(root, set);
                }
            }
        }
        return set;
    }

    /**
     * Extracts all component IDs currently present in the active LayerManager,
     * merging with statically registered component IDs.
     */
    public Set<String> getComponentIds(LayerManager manager) {
        Set<String> set = new LinkedHashSet<>(registeredComponents);
        if (manager != null) {
            for (Layer layer : manager.layers()) {
                for (Container root : layer.containers()) {
                    collectComponentIds(root, set);
                }
            }
        }
        return set;
    }

    // --- Static Utility Methods ---

    public static Set<String> getAvailableLayerIds(LayerManager manager) {
        Set<String> set = new LinkedHashSet<>();
        if (manager != null) {
            for (Layer layer : manager.layers()) {
                set.add(layer.id());
            }
        }
        return set;
    }

    public static Set<String> getAvailableContainerIds(LayerManager manager) {
        Set<String> set = new LinkedHashSet<>();
        if (manager != null) {
            for (Layer layer : manager.layers()) {
                for (Container root : layer.containers()) {
                    collectContainerIds(root, set);
                }
            }
        }
        return set;
    }

    public static Set<String> getAvailableComponentIds(LayerManager manager) {
        Set<String> set = new LinkedHashSet<>();
        if (manager != null) {
            for (Layer layer : manager.layers()) {
                for (Container root : layer.containers()) {
                    collectComponentIds(root, set);
                }
            }
        }
        return set;
    }

    private static void collectContainerIds(Container container, Set<String> set) {
        set.add(container.id());
        for (Container child : container.childContainers()) {
            collectContainerIds(child, set);
        }
    }

    private static void collectComponentIds(Container container, Set<String> set) {
        for (Component comp : container.components()) {
            if (!comp.id().startsWith("__inspect_")) {
                set.add(comp.id());
            }
        }
        for (Container child : container.childContainers()) {
            collectComponentIds(child, set);
        }
    }

    /**
     * Instantiates a pre-configured {@link UiAnimation} based on preset name and optional duration ticks.
     * Returns null if preset is "stop" or "reset" or unknown.
     */
    public static UiAnimation createAnimation(String presetName, Integer durationTicks) {
        if (presetName == null) return null;
        String p = presetName.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
        int d = (durationTicks != null && durationTicks > 0) ? durationTicks : 16;

        return switch (p) {
            case "fadein", "fade" -> UiAnimation.fadeIn(durationTicks != null ? durationTicks : 12, Easings.OutCubic);
            case "fadeout" -> UiAnimation.fadeOut(durationTicks != null ? durationTicks : 12, Easings.InCubic);
            case "popin", "pop" -> UiAnimation.builder()
                    .durationTicks(durationTicks != null ? durationTicks : 18)
                    .easing(Easings.BackOut)
                    .opacity(0.0f, 1.0f)
                    .scale(0.72f, 1.0f)
                    .build();
            case "bouncein", "bounce" -> UiAnimation.builder()
                    .durationTicks(durationTicks != null ? durationTicks : 22)
                    .easing(Easings.BounceOut)
                    .opacity(0.0f, 1.0f)
                    .scale(0.8f, 1.0f)
                    .offset(UiAnimation.Direction.TOP, 24)
                    .build();
            case "dropin", "drop" -> UiAnimation.builder()
                    .durationTicks(durationTicks != null ? durationTicks : 20)
                    .easing(Easings.BounceOut)
                    .opacity(0.0f, 1.0f)
                    .offset(UiAnimation.Direction.TOP, 30)
                    .build();
            case "softrise", "rise" -> UiAnimation.builder()
                    .durationTicks(durationTicks != null ? durationTicks : 18)
                    .easing(Easings.OutQuad)
                    .opacity(0.0f, 1.0f)
                    .offset(UiAnimation.Direction.BOTTOM, 14)
                    .build();
            case "slideleft" -> UiAnimation.slideIn(d, UiAnimation.Direction.LEFT, 24, Easings.OutCubic);
            case "slideright" -> UiAnimation.slideIn(d, UiAnimation.Direction.RIGHT, 24, Easings.OutCubic);
            case "slidetop", "slideup" -> UiAnimation.slideIn(d, UiAnimation.Direction.TOP, 20, Easings.OutCubic);
            case "slidebottom", "slidedown" -> UiAnimation.slideIn(d, UiAnimation.Direction.BOTTOM, 20, Easings.OutCubic);
            case "scalein", "scale" -> UiAnimation.builder()
                    .durationTicks(d)
                    .easing(Easings.OutCubic)
                    .opacity(0.0f, 1.0f)
                    .scale(0.5f, 1.0f)
                    .build();
            case "scaleout" -> UiAnimation.builder()
                    .durationTicks(durationTicks != null ? durationTicks : 14)
                    .easing(Easings.InCubic)
                    .opacity(1.0f, 0.0f)
                    .scale(1.0f, 0.75f)
                    .build();
            default -> null;
        };
    }
}
