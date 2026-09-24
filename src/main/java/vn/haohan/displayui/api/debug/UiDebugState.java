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

import org.bukkit.Color;
import vn.haohan.displayui.api.animation.UiAnimation;
import vn.haohan.displayui.api.component.ButtonComponent;
import vn.haohan.displayui.api.component.Component;
import vn.haohan.displayui.api.container.Container;
import vn.haohan.displayui.api.interaction.UiButtonAction;
import vn.haohan.displayui.api.layer.Layer;
import vn.haohan.displayui.api.layer.LayerManager;
import vn.haohan.displayui.api.layout.UiAnchorPoint;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages runtime visual debugging overrides for any Display UI hierarchy.
 * Allows independent toggling, hiding, showing, and solo isolation of each
 * Layer, Container, and Component, as well as Click-to-Inspect raycast hitbox injection.
 */
public class UiDebugState {

    public enum SoloType {
        NONE,
        LAYER,
        CONTAINER,
        COMPONENT
    }

    public record UiTreeNode(String type, String id, boolean visible, List<UiTreeNode> children) {
    }

    private final Map<String, Boolean> layerOverrides = new ConcurrentHashMap<>();
    private final Map<String, Boolean> containerOverrides = new ConcurrentHashMap<>();
    private final Map<String, Boolean> componentOverrides = new ConcurrentHashMap<>();

    private final Map<String, UiAnimation> layerAnimationOverrides = new ConcurrentHashMap<>();
    private final Map<String, UiAnimation> containerAnimationOverrides = new ConcurrentHashMap<>();
    private final Map<String, UiAnimation> componentAnimationOverrides = new ConcurrentHashMap<>();

    private volatile SoloType soloType = SoloType.NONE;
    private volatile String soloId = null;
    private volatile boolean clickInspectEnabled = false;

    public UiDebugState() {
    }

    // --- Click Inspect Mode ---

    public boolean isClickInspectEnabled() {
        return clickInspectEnabled;
    }

    public void setClickInspectEnabled(boolean clickInspectEnabled) {
        this.clickInspectEnabled = clickInspectEnabled;
    }

    public boolean toggleClickInspect() {
        this.clickInspectEnabled = !this.clickInspectEnabled;
        return this.clickInspectEnabled;
    }

    // --- Visibility Setters ---

    public void setLayerVisibility(String id, boolean visible) {
        if (id != null) {
            layerOverrides.put(id.toLowerCase(Locale.ROOT), visible);
        }
    }

    public void setContainerVisibility(String id, boolean visible) {
        if (id != null) {
            containerOverrides.put(id.toLowerCase(Locale.ROOT), visible);
        }
    }

    public void setComponentVisibility(String id, boolean visible) {
        if (id != null) {
            componentOverrides.put(id.toLowerCase(Locale.ROOT), visible);
        }
    }

    private static final UiAnimation STOP_MARKER = UiAnimation.builder().durationTicks(1).build();

    // --- Animation Setters & Overrides ---

    public void setLayerAnimation(String id, UiAnimation animation) {
        if (id != null) {
            String key = id.toLowerCase(Locale.ROOT);
            layerAnimationOverrides.put(key, animation != null ? animation : STOP_MARKER);
        }
    }

    public void setContainerAnimation(String id, UiAnimation animation) {
        if (id != null) {
            String key = id.toLowerCase(Locale.ROOT);
            containerAnimationOverrides.put(key, animation != null ? animation : STOP_MARKER);
        }
    }

    public void setComponentAnimation(String id, UiAnimation animation) {
        if (id != null) {
            String key = id.toLowerCase(Locale.ROOT);
            componentAnimationOverrides.put(key, animation != null ? animation : STOP_MARKER);
        }
    }

    public void clearAnimation(String id) {
        if (id != null) {
            String key = id.toLowerCase(Locale.ROOT);
            layerAnimationOverrides.remove(key);
            containerAnimationOverrides.remove(key);
            componentAnimationOverrides.remove(key);
        }
    }

    public void clearAllAnimations() {
        layerAnimationOverrides.clear();
        containerAnimationOverrides.clear();
        componentAnimationOverrides.clear();
    }

    // --- Toggle Methods ---

    public boolean toggleLayer(String id, LayerManager manager) {
        String key = id.toLowerCase(Locale.ROOT);
        boolean current = getEffectiveLayerVisibility(key, manager);
        boolean target = !current;
        layerOverrides.put(key, target);
        return target;
    }

    public boolean toggleContainer(String id, LayerManager manager) {
        String key = id.toLowerCase(Locale.ROOT);
        boolean current = getEffectiveContainerVisibility(key, manager);
        boolean target = !current;
        containerOverrides.put(key, target);
        return target;
    }

    public boolean toggleComponent(String id, LayerManager manager) {
        String key = id.toLowerCase(Locale.ROOT);
        boolean current = getEffectiveComponentVisibility(key, manager);
        boolean target = !current;
        componentOverrides.put(key, target);
        return target;
    }

    // --- Current Effective Visibility Helpers ---

    public boolean getEffectiveLayerVisibility(String id, LayerManager manager) {
        String key = id.toLowerCase(Locale.ROOT);
        if (soloType == SoloType.LAYER && soloId != null) {
            return key.equalsIgnoreCase(soloId);
        }
        if (layerOverrides.containsKey(key)) {
            return layerOverrides.get(key);
        }
        if (manager != null) {
            for (Layer layer : manager.layers()) {
                if (layer.id().equalsIgnoreCase(key)) {
                    return layer.isVisible();
                }
            }
        }
        return true;
    }

    public boolean getEffectiveContainerVisibility(String id, LayerManager manager) {
        String key = id.toLowerCase(Locale.ROOT);
        if (soloType == SoloType.CONTAINER && soloId != null) {
            return key.equalsIgnoreCase(soloId);
        }
        if (containerOverrides.containsKey(key)) {
            return containerOverrides.get(key);
        }
        if (manager != null) {
            for (Layer layer : manager.layers()) {
                for (Container root : layer.containers()) {
                    Optional<Container> found = findContainerIgnoreCase(root, key);
                    if (found.isPresent()) {
                        return found.get().isVisible();
                    }
                }
            }
        }
        return true;
    }

    public boolean getEffectiveComponentVisibility(String id, LayerManager manager) {
        String key = id.toLowerCase(Locale.ROOT);
        if (soloType == SoloType.COMPONENT && soloId != null) {
            return key.equalsIgnoreCase(soloId);
        }
        if (componentOverrides.containsKey(key)) {
            return componentOverrides.get(key);
        }
        if (manager != null) {
            for (Layer layer : manager.layers()) {
                for (Container root : layer.containers()) {
                    Optional<Component> found = findComponentIgnoreCase(root, key);
                    if (found.isPresent()) {
                        return found.get().isVisible();
                    }
                }
            }
        }
        return true;
    }

    // --- Solo Mode ---

    public void setSolo(SoloType type, String id) {
        this.soloType = Objects.requireNonNull(type);
        this.soloId = (id != null) ? id.toLowerCase(Locale.ROOT) : null;
    }

    public void clearSolo() {
        this.soloType = SoloType.NONE;
        this.soloId = null;
    }

    public boolean isSoloActive() {
        return soloType != SoloType.NONE && soloId != null;
    }

    public SoloType getSoloType() {
        return soloType;
    }

    public String getSoloId() {
        return soloId;
    }

    // --- Bulk Operations ---

    public void reset() {
        layerOverrides.clear();
        containerOverrides.clear();
        componentOverrides.clear();
        layerAnimationOverrides.clear();
        containerAnimationOverrides.clear();
        componentAnimationOverrides.clear();
        clickInspectEnabled = false;
        clearSolo();
    }

    public void showAll(LayerManager manager) {
        clearSolo();
        if (manager == null) {
            layerOverrides.clear();
            containerOverrides.clear();
            componentOverrides.clear();
            return;
        }
        for (Layer layer : manager.layers()) {
            layerOverrides.put(layer.id().toLowerCase(Locale.ROOT), true);
            for (Container root : layer.containers()) {
                setContainerAndChildrenVisibility(root, true);
            }
        }
    }

    public void hideAll(LayerManager manager) {
        clearSolo();
        if (manager == null) {
            return;
        }
        for (Layer layer : manager.layers()) {
            layerOverrides.put(layer.id().toLowerCase(Locale.ROOT), false);
            for (Container root : layer.containers()) {
                setContainerAndChildrenVisibility(root, false);
            }
        }
    }

    private void setContainerAndChildrenVisibility(Container container, boolean visible) {
        containerOverrides.put(container.id().toLowerCase(Locale.ROOT), visible);
        for (Component comp : container.components()) {
            componentOverrides.put(comp.id().toLowerCase(Locale.ROOT), visible);
        }
        for (Container child : container.childContainers()) {
            setContainerAndChildrenVisibility(child, visible);
        }
    }

    // --- Apply To LayerManager ---

    /**
     * Applies all active visibility overrides, solo state, and animation overrides directly to the given LayerManager.
     * If Click-to-Inspect is enabled, also injects interactive hitboxes for all visible elements.
     * This must be called prior to compiling the LayerManager into a UiDocument.
     */
    public void apply(LayerManager manager) {
        if (manager == null) return;

        boolean isSolo = isSoloActive();

        for (Layer layer : manager.layers()) {
            String layerKey = layer.id().toLowerCase(Locale.ROOT);

            if (isSolo && soloType == SoloType.LAYER) {
                layer.visible(layerKey.equalsIgnoreCase(soloId));
            } else if (layerOverrides.containsKey(layerKey)) {
                layer.visible(layerOverrides.get(layerKey));
            }

            if (layerAnimationOverrides.containsKey(layerKey)) {
                UiAnimation anim = layerAnimationOverrides.get(layerKey);
                if (anim != null && anim != STOP_MARKER) {
                    layer.animate(anim);
                } else {
                    layer.stopAnimation();
                }
            }

            for (Container root : layer.containers()) {
                applyContainer(root, isSolo);
            }
        }

        if (clickInspectEnabled) {
            injectClickInspectHitboxes(manager);
        }
    }

    private void applyContainer(Container container, boolean isSolo) {
        String contKey = container.id().toLowerCase(Locale.ROOT);

        if (isSolo && soloType == SoloType.CONTAINER) {
            container.visible(contKey.equalsIgnoreCase(soloId));
        } else if (containerOverrides.containsKey(contKey)) {
            container.visible(containerOverrides.get(contKey));
        }

        if (containerAnimationOverrides.containsKey(contKey)) {
            UiAnimation anim = containerAnimationOverrides.get(contKey);
            if (anim != null && anim != STOP_MARKER) {
                container.animate(anim);
            } else {
                container.stopAnimation();
            }
        }

        // Apply to components inside this container
        for (Component comp : container.components()) {
            String compKey = comp.id().toLowerCase(Locale.ROOT);
            if (isSolo && soloType == SoloType.COMPONENT) {
                comp.visible(compKey.equalsIgnoreCase(soloId));
            } else if (componentOverrides.containsKey(compKey)) {
                comp.visible(componentOverrides.get(compKey));
            }

            if (componentAnimationOverrides.containsKey(compKey)) {
                UiAnimation anim = componentAnimationOverrides.get(compKey);
                if (anim != null && anim != STOP_MARKER) {
                    comp.animate(anim);
                } else {
                    comp.stopAnimation();
                }
            }
        }

        // Recursively apply to child containers
        for (Container child : container.childContainers()) {
            applyContainer(child, isSolo);
        }
    }

    /**
     * Injects transparent ButtonComponents onto every visible Component and Container
     * so that mouse clicks on the hologram can raycast and inspect them immediately.
     */
    public void injectClickInspectHitboxes(LayerManager manager) {
        if (manager == null) return;

        for (Layer layer : manager.layers()) {
            if (!layer.isVisible()) continue;
            for (Container root : layer.containers()) {
                injectContainerHitboxes(root);
            }
        }
    }

    private void injectContainerHitboxes(Container container) {
        if (!container.isVisible()) return;

        List<Component> hitboxes = new ArrayList<>();

        // 1. Hitbox for each non-button component inside container
        for (Component comp : container.components()) {
            if (comp.isVisible() && !(comp instanceof ButtonComponent) && !comp.id().startsWith("__inspect_")) {
                ButtonComponent inspectBtn = ButtonComponent.builder("__inspect_comp_" + comp.id())
                        .anchor(comp.anchor())
                        .origin(comp.origin())
                        .offset(comp.x(), comp.y())
                        .size(Math.max(comp.width(), 4.0f), Math.max(comp.height(), 4.0f))
                        .backgroundColor(Color.fromARGB(0, 0, 0, 0))
                        .action(UiButtonAction.none())
                        .build();
                hitboxes.add(inspectBtn);
            }
        }

        // 2. Hitbox for container itself (covering empty areas)
        ButtonComponent contBtn = ButtonComponent.builder("__inspect_cont_" + container.id())
                .anchor(UiAnchorPoint.TOP_LEFT)
                .origin(UiAnchorPoint.TOP_LEFT)
                .offset(0, 0)
                .size(container.width(), container.height())
                .backgroundColor(Color.fromARGB(0, 0, 0, 0))
                .action(UiButtonAction.none())
                .build();
        hitboxes.add(contBtn);

        for (Component btn : hitboxes) {
            container.addComponent(btn);
        }

        // Recursively apply to child containers
        for (Container child : container.childContainers()) {
            injectContainerHitboxes(child);
        }
    }

    // --- Hierarchy Tree Builder ---

    public List<UiTreeNode> buildTree(LayerManager manager) {
        if (manager == null) return Collections.emptyList();
        List<UiTreeNode> roots = new ArrayList<>();
        for (Layer layer : manager.layers()) {
            List<UiTreeNode> layerChildren = new ArrayList<>();
            for (Container rootCont : layer.containers()) {
                layerChildren.add(buildContainerNode(rootCont));
            }
            roots.add(new UiTreeNode("LAYER", layer.id(), layer.isVisible(), layerChildren));
        }
        return roots;
    }

    private UiTreeNode buildContainerNode(Container container) {
        List<UiTreeNode> children = new ArrayList<>();
        for (Component comp : container.components()) {
            if (comp.id().startsWith("__inspect_")) continue;
            children.add(new UiTreeNode("COMPONENT", comp.id(), comp.isVisible(), Collections.emptyList()));
        }
        for (Container child : container.childContainers()) {
            children.add(buildContainerNode(child));
        }
        return new UiTreeNode("CONTAINER", container.id(), container.isVisible(), children);
    }

    // --- Internal Search Helpers ---

    private Optional<Container> findContainerIgnoreCase(Container parent, String targetId) {
        if (parent.id().equalsIgnoreCase(targetId)) return Optional.of(parent);
        for (Container child : parent.childContainers()) {
            Optional<Container> found = findContainerIgnoreCase(child, targetId);
            if (found.isPresent()) return found;
        }
        return Optional.empty();
    }

    private Optional<Component> findComponentIgnoreCase(Container parent, String targetId) {
        for (Component comp : parent.components()) {
            if (comp.id().equalsIgnoreCase(targetId)) return Optional.of(comp);
        }
        for (Container child : parent.childContainers()) {
            Optional<Component> found = findComponentIgnoreCase(child, targetId);
            if (found.isPresent()) return found;
        }
        return Optional.empty();
    }
}
