# HaoHanDisplayUI - API Documentation  

---

## Table of Contents

- [1. Core & UI Lifecycle (`vn.haohan.displayui.api`)](#1-core--ui-lifecycle-vnhaohandisplayuiapi)
  - [DisplayUiService](#displayuiservice)
  - [UiHandle](#uihandle)
  - [UiDocument](#uidocument)
  - [UiOptions](#uioptions)
  - [UiHit](#uihit)
  - [UiPager](#uipager)
- [2. High-Level Layer & Component Architecture (`vn.haohan.displayui.api.layer`, `container`, `component`, `bridge`)](#2-high-level-layer--component-architecture)
  - [Layer & LayerManager](#layer--layermanager)
  - [Container System](#container-system)
  - [DropdownContainer & DropdownAnimationType](#dropdowncontainer--dropdownanimationtype)
  - [Component Hierarchy & Events](#component-hierarchy--events)
  - [UiDocumentBridge](#uidocumentbridge)
- [3. Typography & Text Formatting (`vn.haohan.displayui.api.text`)](#3-typography--text-formatting-vnhaohandisplayuiapitext)
  - [UiText](#uitext)
  - [UiTextAlignment & UiVerticalAlignment](#uitextalignment--uiverticalalignment)
  - [UiTextOpticalPreset](#uitextopticalpreset)
- [4. Layout & Viewports (`vn.haohan.displayui.api.layout` & `api.view`)](#4-layout--viewports-vnhaohandisplayuiapilayout--apiview)
  - [UiRect](#uirect)
  - [UiAnchorPoint & UiAnchor](#uianchorpoint--uianchor)
  - [UiCameraTransform](#uicameratransform)
  - [UiAudience](#uiaudience)
  - [UiFollowMode & UiFollowOptions](#uifollowmode--uifollowoptions)
- [5. Low-Level Controls & Interaction (`vn.haohan.displayui.api.interaction`)](#5-low-level-controls--interaction-vnhaohandisplayuiapiinteraction)
  - [UiControl & UiButton](#uicontrol--uibutton)
  - [UiCheckbox & UiSlider](#uicheckbox--uislider)
  - [UiScrollList, UiScrollAnimation & UiScrollAnimations](#uiscrolllist-uiscrollanimation--uiscrollanimations)
  - [UiClick, UiClickHandler & UiControlChangeHandler](#uiclick-uiclickhandler--uicontrolchangehandler)
  - [Bukkit Events: UiButtonClickEvent & UiControlChangeEvent](#bukkit-events-uibuttonclickevent--uicontrolchangeevent)
- [6. Node Scene Graph (`vn.haohan.displayui.api.node`)](#6-node-scene-graph-vnhaohandisplayuiapinode)
  - [UiNode](#uinode)
  - [TextNode & AlignedTextNode](#textnode--alignedtextnode)
  - [ItemNode & BlockNode](#itemnode--blocknode)
  - [EntityModelNode & UiModelRotation](#entitymodelnode--uimodelrotation)
  - [MobEntityNode](#mobentitynode)
  - [LineNode & PolylineNode](#linenode--polylinenode)
  - [TriangleNode & ParallelogramNode](#trianglenode--parallelogramnode)
  - [UiBackgroundNode & UiGradientBackgroundNode](#uibackgroundnode--uigradientbackgroundnode)
  - [UiIconNode & UiShapeNode](#uiiconnode--uishapenode)
- [7. Animation, Gradients & Geometry (`animation`, `gradient`, `shape`, `icon`)](#7-animation-gradients--geometry)
  - [Easings, UiAnimation & UiEffects](#easings-uianimation--uieffects)
  - [UiGradient, UiGradientEndpoint & UiGradientPosition](#uigradient-uigradientendpoint--uigradientposition)
  - [DisplayShapeMath & TRSResult](#displayshapemath--trsresult)
  - [UiIconRegistry](#uiiconregistry)
- [8. Document Parsing & Loading (`vn.haohan.displayui.api.loader`)](#8-document-parsing--loading-vnhaohandisplayuiapiloader)
  - [UiDocumentLoader](#uidocumentloader)
  - [UiDocumentParseException](#uidocumentparseexception)
- [9. In-Game Debugging System (`vn.haohan.displayui.api.debug` & `/uidebug`)](#9-in-game-debugging-system)
  - [UiDebugManager & Debug Architecture](#uidebugmanager--debug-architecture)
  - [The `/uidebug` Command Suite](#the-uidebug-command-suite)
- [10. Practical Examples](#10-practical-examples)
  - [Example 1: Building a Modern Panel with LayerManager and Components](#example-1-building-a-modern-panel-with-layermanager-and-components)
  - [Example 2: Collapsible Settings Menu with DropdownContainer](#example-2-collapsible-settings-menu-with-dropdowncontainer)
  - [Example 3: Seamless Multi-Page Navigation with UiHandle.replace(...)](#example-3-seamless-multi-page-navigation-with-uihandlereplace)
  - [Example 4: 3D Mob Exhibition with Cursor Tracking](#example-4-3d-mob-exhibition-with-cursor-tracking)
  - [Example 5: High-Performance Mesh Gradient Canvas](#example-5-high-performance-mesh-gradient-canvas)

---

## 1. Core & UI Lifecycle (`vn.haohan.displayui.api`)

### DisplayUiService

The central service interface for managing UI creation, lookup, debugging, and lifecycles. Obtain the singleton instance via the Bukkit Services Manager:

```java
DisplayUiService service = Bukkit.getServicesManager().load(DisplayUiService.class);
```

| Method | Description & Purpose | Parameters & Return |
|---|---|---|
| `icons()` | Retrieves the central custom icon registry `UiIconRegistry`. | `@return`: `UiIconRegistry` |
| `debug()` | Retrieves the live debug manager `UiDebugManager` for sessions and inspector HUDs. | `@return`: `UiDebugManager` |
| `create(ownerKey, origin, document)` | Creates and spawns a UI using low-level `UiDocument` at world location with default options. | `@param ownerKey`: Module/Plugin ID<br>`@param origin`: Anchor location<br>`@param document`: Layout document<br>`@return`: `UiHandle` |
| `create(ownerKey, origin, document, options, audience)` | Creates a UI using `UiDocument` with full options and audience filtering. | `@return`: `UiHandle` |
| `create(ownerKey, origin, layerManager)` | Creates and spawns a UI using high-level `LayerManager` (compiled via `UiDocumentBridge`). | `@param layerManager`: Layer hierarchy<br>`@return`: `UiHandle` |
| `create(ownerKey, origin, layerManager, options, audience)` | Creates a layer-based UI with full options and audience filtering. | `@return`: `UiHandle` |
| `create(ownerKey, origin, container)` | Creates and spawns a single root `Container` wrapped automatically in a base layer. | `@param container`: Root UI container<br>`@return`: `UiHandle` |
| `find(UUID id)` | Resolves an active UI handle by its session UUID. | `@param id`: Unique UI UUID<br>`@return`: `Optional<UiHandle>` |
| `active()` | Returns an unmodifiable collection of all active UI sessions on the server. | `@return`: `Collection<UiHandle>` |
| `removeOwnedBy(String ownerKey)` | Bulk-removes and cleans up all UI instances belonging to a specific owner key. | `@return`: Number of instances removed |

---

### UiHandle

Represents an active, live UI session instance in the Minecraft world. Provides real-time scene updates, atomic page transitions, animations, and safe entity cleanup.

| Method | Description & Purpose | Parameters & Return |
|---|---|---|
| `id()` | Returns the unique session identifier (UUID). | `@return`: `UUID` |
| `document()` | Returns the current canvas layout document. | `@return`: `UiDocument` |
| `options()` | Returns the current display and view options. | `@return`: `UiOptions` |
| `origin()` / `location()` | Returns the current center root world location of the UI canvas. | `@return`: `Location` |
| `isAlive()` | Checks if the UI canvas is still active and rendered in the world. | `@return`: `boolean` |
| `update(UiDocument)` | Incrementally updates the UI document (diffs node trees for in-place live state changes). | `@param newDocument`: Updated layout document |
| `update(UiOptions)` | Updates display options (distance, billboard, camera lock, follow parameters). | `@param newOptions`: Updated view options |
| `update(UiDocument, UiOptions)`| Atomically updates both the layout document and view options. | `@param newDocument`, `@param newOptions` |
| `replace(UiDocument)` | **Atomic Page Replacement**: Completely replaces the scene with a new document, avoiding mismatched entity recycling across structurally distinct pages. | `@param newDocument`: New page document |
| `replace(UiDocument, UiOptions)` | Replaces the document and display options atomically without entity morphing glitches. | `@param newDocument`, `@param newOptions` |
| `replace(LayerManager)` | Compiles and atomically replaces the current UI with a new `LayerManager` hierarchy. | `@param layerManager`: New layer stack |
| `replace(Container)` | Compiles and atomically replaces the current UI with a new root `Container`. | `@param container`: New root container |
| `move(Location origin)` | Relocates the UI canvas anchor in the world. | `@param origin`: Target location |
| `animate(UiAnimation)` | Plays an entrance/transition animation across the UI scene. | `@param animation`: Animation specification |
| `animateNodes(List<UiAnimation>)`| Animates each document node independently by index. | `@param animations`: Per-node animations |
| `stopAnimation()` | Stops the current animation and restores the scene's final resting state. | - |
| `isAnimating()` | Checks if the UI is currently executing an active animation. | `@return`: `boolean` |
| `control(String id)` | Finds an interactive control (button, slider, checkbox, scroll list) by its ID. | `@return`: `Optional<UiControl>` |
| `onClick(UiClickHandler)` | Registers a listener callback for player click interactions. | `@param handler`: Click callback |
| `onControlChange(UiControlChangeHandler)` | Registers a listener callback for control value changes (Checkbox, Slider). | `@param handler`: Change callback |
| `clearClickHandlers()`, `clearControlChangeHandlers()` | Clears all registered interaction listeners. | - |
| `show(Player)` / `hide(Player)` | Dynamically reveals or hides the UI instance for a specific player. | `@param player`: Target player |
| `remove()` | Despawns and permanently destroys this UI instance, cleaning up all Display entities. | - |
| `follow(Player)` | Starts player-following mode with default follow parameters. | `@param player`: The target player |
| `follow(Player, UiFollowOptions)` | Starts or updates player follow with custom damping, distance, and gaze deadzones. | `@param player`, `@param options` |
| `follow(Player, distance, damping, ticks)` | Convenience overload for configuring follow mode. | - |
| `stopFollow()` | Stops following and locks the UI at its current world location. | - |
| `followOptions()` | Returns active follow parameters (or default if follow is disabled). | `@return`: `UiFollowOptions` |
| `followMode()` | Returns active follow behavior mode (`DISABLED`, `POSITION`, `LOOK_AT`, `FULL`). | `@return`: `UiFollowMode` |
| `followTarget()` | Returns target player being followed, or `null`. | `@return`: `Player` |
| `isFollowCatchUp()` | Checks if the follow controller is currently actively gliding toward ideal alignment. | `@return`: `boolean` |
| `stopFollowCatchUp()` | Halts ongoing glide and locks the UI at its current position immediately. | - |
| `doubleSided(boolean)` | Enables or disables two-sided rendering for all nodes. | `@param enabled`: `true` for two-sided |
| `mirrorSide(boolean)` | Sets whether the back side uses horizontally mirrored coordinates. | `@param enabled`: `true` for mirrored |
| `sides(doubleSided, mirrorSide)` | Convenience method configuring two-sided and mirror options together. | - |

---

### UiDocument

The immutable blueprint defining the low-level UI canvas hierarchy, including dimensions, visual nodes, and interactive controls.

```java
UiDocument doc = UiDocument.builder(200, 150)
    .canvasScale(0.005f)
    .background(Color.fromRGB(20, 20, 25))
    .node(new TextNode(UiText.of("Welcome!"), 100, 20, 1.2f, Color.WHITE))
    .build();
```

| Method / Builder | Description & Purpose |
|---|---|
| `UiDocument.builder(width, height)` | Starts building a document with logical canvas pixel dimensions. |
| `canvasScale(float)` | Scale factor converting canvas pixels to world blocks (default: `0.005f`, where 200px = 1 block). |
| `background(Color)` | Sets a solid background color across the canvas. |
| `node(UiNode)` | Appends a render node to the document tree. |
| `control(UiControl)` | Appends an interactive control (Button, Checkbox, Slider, ScrollList). |
| `button(UiButton)` | Convenience method to append a button. |
| `button(id, bounds, action)` | Creates and adds a button with ID, bounds `UiRect`, and `UiButtonAction`. |
| `checkbox(id, bounds, checked)` | Creates and adds a toggleable Checkbox control. |
| `slider(id, bounds, progress)` | Creates and adds a draggable Slider control. |
| `findControl(id)` | Finds a registered interactive control by its identifier string. |
| `build()` | Completes construction and returns the immutable `UiDocument`. |

---

### UiOptions

Configures rendering, positioning, and viewer audience parameters for a UI display.

- `distance`: Distance in blocks from viewer's eyes to canvas (default: `1.8f`).
- `billboard`: Display billboard rotation mode (`CENTER`, `FIXED`, `HORIZONTAL`, `VERTICAL`).
- `camera`: Camera orientation specification via `UiCameraTransform`.
- `audience`: Audience visibility rules via `UiAudience` (public or restricted).
- `spectator`: Whether other players can view this individual player's UI.
- `followMode`: Follow mode behavior (`UiFollowMode.FOLLOW` or `NONE`).
- `followOptions`: Smoothing damping, interpolation, and gaze deadzones (`UiFollowOptions`).

---

### UiHit & UiPager

- **`UiHit(UiControl control, Vector3f hitPoint, float distance)`**: Record storing raycast intersection data, including the hit control, exact 3D intersection point, and distance from the viewer's eyes.
- **`UiPager`**: Utility helper for safe list pagination:
  - `pageCount(int totalItems, int pageSize)`: Calculates total number of pages (minimum 1).
  - `pageItems(List<T> items, int pageIndex, int pageSize)`: Safely extracts elements for a page without out-of-bounds errors.

---

## 2. High-Level Layer & Component Architecture

HaoHanDisplayUI features a modern, hierarchical Component and Container system built on top of an isolated Z-ordered Layer Stack.

```
LayerManager
 └── Layer (Z: 0 - Background)
      └── Container (Root Panel)
           ├── TextComponent (Title)
           ├── SliderComponent (Volume)
           └── DropdownContainer (Collapsible Options)
                ├── ButtonComponent (Option A)
                └── ButtonComponent (Option B)
```

### Layer & LayerManager

`Layer` objects represent isolated visual planes in 3D world space. Each layer is assigned a distinct base world-depth slot, completely eliminating Z-fighting and visual flickering.

- **`Layer.LAYER_DEPTH_STEP`**: Default depth separation between consecutive layers (`0.020f` blocks).
- **`Layer.ELEMENT_DEPTH_STEP`**: Sub-depth separation between elements inside the same layer (`0.0005f` blocks).

```java
LayerManager manager = new LayerManager();

// Create background layer (Z = 0)
Layer bgLayer = manager.createLayer("bg_layer", 0)
    .bounds(-100, -70, 200, 140)
    .backgroundColor(Color.fromRGB(24, 28, 36))
    .borderRound(6.0f);

// Create foreground interactive layer (Z = 1)
Layer uiLayer = manager.createLayer("ui_layer", 1);
```

| `Layer` Method | Description |
|---|---|
| `id()`, `zIndex()`, `baseDepth()` | Identifiers and computed world depth for the layer. |
| `bounds(x, y, w, h)` | Sets explicit boundary dimensions for the layer. |
| `backgroundColor(Color)` | Sets solid background color. |
| `gradient(UiGradient)` | Sets continuous mesh gradient background. |
| `borderRound(float radius)` | Sets corner curvature radius. |
| `visible(boolean)` | Toggles visibility of the entire layer and its children. |
| `addContainer(Container)` | Adds a root container to this layer. |
| `removeContainer(String id)` | Removes a container from this layer. |
| `animate(UiAnimation)` | Triggers an animation applied exclusively to this layer. |

---

### Container System

`Container` is a recursive UI element managing child components and nested sub-containers with 9-way anchor docking, proportional scaling, and scoped animation cascades.

```java
Container panel = Container.builder("main_panel")
    .anchor(UiAnchorPoint.CENTER)
    .origin(UiAnchorPoint.CENTER)
    .size(180, 120)
    .backgroundColor(Color.fromARGB(220, 30, 36, 48))
    .borderRound(4.0f)
    .build();

panel.addComponent(new TextComponent("lbl_title", UiText.of("§e§lSYSTEM SETTINGS"), 0, 12));
```

| `Container` Feature | Description |
|---|---|
| **9-Way Anchor & Origin** | Positioned via `anchor(UiAnchorPoint)` and `origin(UiAnchorPoint)` relative to parent container or layer. |
| **Recursive Hierarchy** | Containers can be nested arbitrarily deep using `addContainer(Container)`. |
| **Proportional Scale Cascade** | Scaling a parent container scales all nested containers and components proportionally (`parentScale * childScale`). |
| **Scoped Animation Cascade** | `panel.animate(UiAnimation)` animates the container and its subtree while isolating siblings and parent layers. |
| **Padding & Background** | Built-in background colors, mesh gradients, and rounded corner styling. |

---

### DropdownContainer & DropdownAnimationType

`DropdownContainer` extends `Container` to provide an animated collapsible dropdown menu with an interactive header bar, title, toggle indicators, and auto-arranged content.

```java
DropdownContainer dropdown = DropdownContainer.builder("dropdown_graphics")
    .headerTitle(Component.text("Render Quality", NamedTextColor.YELLOW))
    .headerHeight(16.0f)
    .animationType(DropdownAnimationType.SLIDE_AND_FADE)
    .animationDuration(6)
    .itemSpacing(4.0f)
    .build();

dropdown.addDropdownItem(new ButtonComponent("btn_low", UiText.of("Low Quality"), 100, 14));
dropdown.addDropdownItem(new ButtonComponent("btn_high", UiText.of("High Quality"), 100, 14));

dropdown.onToggle(expanded -> {
    player.sendMessage(expanded ? "§aDropdown opened!" : "§cDropdown closed!");
});
```

#### `DropdownAnimationType` Presets:
- `NONE`: Instantaneous toggle without animation.
- `FADE`: Smooth opacity fade in/out.
- `SLIDE_AND_FADE`: Smooth vertical slide combined with opacity fade.
- `SCALE_Y`: Vertical expansion and collapse from header baseline.
- `ACCORDION`: Smooth accordion style height stretch.

---

### Component Hierarchy & Events

All interactive and visual elements in the component architecture implement `Component` (via `AbstractComponent`):

```
Component (Interface)
 └── AbstractComponent
      ├── ButtonComponent
      ├── CheckboxComponent
      ├── SliderComponent
      ├── TextComponent
      ├── IconComponent
      ├── ShapeComponent
      └── CustomNodeComponent (Wraps raw UiNode)
```

#### Key Component Types:

1. **`ButtonComponent`**:
   - Customizable background color, hover background, border radius, icon, and text.
   - Click listener: `button.onClick(event -> ...)` consuming `ComponentClickEvent` (`player()`, `component()`, `clickX()`, `clickY()`, `clickType()`).
2. **`CheckboxComponent`**:
   - Toggle switch with label, box color, checkmark color, and initial checked state.
   - Change listener: `checkbox.onChange(event -> ...)` consuming `ComponentChangeEvent` (`asChecked()`).
3. **`SliderComponent`**:
   - Smooth or stepped value slider with `min`, `max`, `step`, and draggable thumb.
   - Change listener: `slider.onChange(event -> ...)` consuming `ComponentChangeEvent` (`asProgress()`, `asFloat()`, `asInt()`).
4. **`TextComponent`**:
   - Adventure Component / MiniMessage text, horizontal & vertical alignment, optical baseline correction.
5. **`IconComponent`**:
   - ItemStack or Material rendering with customizable UV source mapping.
6. **`ShapeComponent`**:
   - Vector geometry (rect, circle, triangle, polygon, star) with borders and rotations.
7. **`CustomNodeComponent`**:
   - Bridge wrapper allowing any low-level `UiNode` (e.g. `MobEntityNode`, `EntityModelNode`) to be placed inside a container.

---

### UiDocumentBridge

`UiDocumentBridge` translates high-level `LayerManager` and `Container` hierarchies into compiled low-level `UiDocument` scene graphs and automatically binds interactive click/change event routing to the resulting `UiHandle`.

```java
// Option A: Let service handle compilation and binding automatically:
UiHandle handle = service.create("my_plugin", origin, layerManager);

// Option B: Compile manually:
UiDocument compiledDoc = UiDocumentBridge.compile(layerManager);
UiHandle handle = service.create("my_plugin", origin, compiledDoc);
UiDocumentBridge.bindInteractions(handle, layerManager);
```

---

## 3. Typography & Text Formatting (`vn.haohan.displayui.api.text`)

### UiText

Rich text abstraction supporting Kyori Adventure `Component`, MiniMessage formatting, hex codes (`#RRGGBB`), legacy codes (`&` / `§`), and character-level gradients.

```java
// Gold-to-red bold gradient text
UiText text = UiText.builder("Auction House")
    .gradient("#FFAA00", "#FF5555")
    .bold(true)
    .build();
```

| Method / Builder | Description & Purpose |
|---|---|
| `UiText.of(String)` | Creates a `UiText` parsing MiniMessage and legacy formatting codes. |
| `UiText.of(Component)` | Creates a `UiText` directly wrapping a Kyori Adventure `Component`. |
| `UiText.builder(String)` | Opens a builder for advanced text formatting and styling. |
| `color(TextColor)` | Sets primary text color. |
| `hex(String)` | Sets hex color code (`#RRGGBB` or `RRGGBB`). |
| `gradient(fromHex, toHex)` | Applies character-level linear gradient from start to end characters. |
| `bold(boolean)`, `italic(boolean)` | Toggles bold and italic decorations. |
| `shadow(boolean)` | Enables or disables default Minecraft font drop shadow. |
| `estimateWidth()` | Estimates physical rendered width in Minecraft font pixels. |

### UiTextAlignment & UiVerticalAlignment

- **`UiTextAlignment`**: Horizontal alignment (`LEFT`, `CENTER`, `RIGHT`).
- **`UiVerticalAlignment`**: Vertical alignment (`TOP`, `CENTER`, `BOTTOM`).

### UiTextOpticalPreset

Predefined optical vertical offset presets correcting baseline alignment between Minecraft pixel font glyphs and background quads:
- `DEFAULT`: Offset 0.0px.
- `BUTTON`: Offset -0.5px.
- `TITLE`: Offset -1.0px.
- `BADGE`: Offset -0.25px.

---

## 4. Layout & Viewports (`vn.haohan.displayui.api.layout` & `api.view`)

### UiRect

A 2D bounding rectangle in logical canvas pixel coordinates used for positioning, layout calculations, and raycast hit detection.

```java
UiRect rect = new UiRect(10, 20, 100, 30); // x=10, y=20, w=100, h=30
```

| Method | Description & Purpose |
|---|---|
| `centerX()`, `centerY()` | Returns horizontal and vertical center coordinates. |
| `right()`, `bottom()` | Returns right edge (`x + width`) and bottom edge (`y + height`). |
| `contains(float px, float py)` | Tests if a 2D canvas point `(px, py)` falls inside this rectangle. |
| `intersects(UiRect other)` | Tests for intersection with another rectangle. |
| `centered(cx, cy, w, h)` | Factory creating a rectangle from a center position `(cx, cy)` and dimensions. |
| `insets(dx, dy)` | Expands or contracts bounding box by `(dx, dy)` margins. |
| `place(w, h, UiAnchorPoint)` | Calculates aligned child bounds of size `(w, h)` positioned according to an anchor point. |

### UiAnchorPoint & UiAnchor

- **`UiAnchorPoint`**: Unified 9-point anchor orientation enum:
  - `TOP_LEFT`, `TOP_CENTER`, `TOP_RIGHT`, `CENTER_LEFT`, `CENTER`, `CENTER_RIGHT`, `BOTTOM_LEFT`, `BOTTOM_CENTER`, `BOTTOM_RIGHT`.
  - `alignX()`, `alignY()`: Normalized factors (`0.0f`, `0.5f`, `1.0f`).
  - `computeOffset(w, h)`: Computes coordinate offset for specified dimensions.
- **`UiAnchor`**: Legacy anchor enum with `toPoint()` converter for backward compatibility.

### UiCameraTransform

Configures camera alignment and transform modes relative to the viewer:
- `fixed()`: Stationary orientation in world coordinates.
- `cameraFacing()`: Automatically rotates to face the player's eye direction.
- `withPitchLock(boolean)`, `withYawLock(boolean)`: Constrains individual rotational axes.
- `withAngles(float yaw, float pitch, roll)`: Sets explicit Euler rotation angles.

### UiAudience, UiFollowMode & UiFollowOptions

- **`UiAudience`**: Controls visibility permissions:
  - `UiAudience.all()`: Visible to every player in the world.
  - `UiAudience.only(Player...)`: Restricted strictly to specified players.
- **`UiFollowMode`**: Canvas following behavior (`NONE`, `POSITION`, `LOOK_AT`, `FULL`).
- **`UiFollowOptions`**: Configures follow damping, interpolation, target distance, and gaze deadzone tolerances (`gazeDeadzoneYaw`, `gazeDeadzonePitch`, `maxCatchUpSpeed`).

---

## 5. Low-Level Controls & Interaction (`vn.haohan.displayui.api.interaction`)

### UiControl & UiButton

- **`UiControl`**: Sealed base interface for all interactive canvas controls (Button, Checkbox, Slider, ScrollList).
- **`UiButton`**: Interactive button control bound to `UiButtonAction` (OPEN_URL, PLAYER_COMMAND, CONSOLE_COMMAND, SUGGEST_COMMAND, NONE).

### UiCheckbox & UiSlider

- **`UiCheckbox`**: Two-state toggle switch (checked/unchecked) with `checked()` and `withChecked(boolean)`.
- **`UiSlider`**: Continuous or stepped value slider across range [0.0f - 1.0f] with `progress()`, `step()`, and `withProgress(float)`.

### UiScrollList, UiScrollAnimation & UiScrollAnimations

- **`UiScrollList`**: Vertical content scroll container responding to mouse wheel / hotbar scroll inputs.
- **`UiScrollAnimations`**: Presets (`none()`, `slide(durationTicks, easing)`).

### Bukkit Events
- **`UiButtonClickEvent`**: Dispatched whenever a player clicks a `UiButton`.
- **`UiControlChangeEvent`**: Dispatched whenever a player modifies a `UiCheckbox` or `UiSlider`.

---

## 6. Node Scene Graph (`vn.haohan.displayui.api.node`)

The visual hierarchy is structured as an immutable Scene Graph using the sealed interface `UiNode`. All nodes support:
- `x()`, `y()`: Logical pixel position on the canvas.
- `depth()`: Z-depth offset used for visual layering and z-fighting prevention.
- `doubleSided()`: Flag enabling back-face rendering (visible from both front and rear).

```
UiNode (Sealed Interface)
 ├── TextNode / AlignedTextNode
 ├── ItemNode
 ├── BlockNode
 ├── EntityModelNode
 ├── MobEntityNode
 ├── LineNode
 ├── PolylineNode
 ├── TriangleNode
 ├── ParallelogramNode
 ├── UiBackgroundNode
 ├── UiGradientBackgroundNode
 ├── UiIconNode
 └── UiShapeNode
```

### Key Render Nodes:
- **`EntityModelNode` & `UiModelRotation`**: Renders custom 3D item models with `cursorTrack()`, `autoSpin()`, `hoverSpin()`, and rotational angle clamping.
- **`MobEntityNode`**: Renders live vanilla `LivingEntity` instances with armor, glowing items, and `cursorTrack()`.
- **`TriangleNode` & `ParallelogramNode`**: Analytical 2D shapes with skewing and shear math.
- **`UiGradientBackgroundNode`**: Multi-slice mesh gradient panel with configurable subdivision slices (`withSlices(16)`).
- **`UiShapeNode`**: Versatile 2D vector shape node supporting 15+ geometric presets and outlines.

---

## 7. Animation, Gradients & Geometry

### Easings, UiAnimation & UiEffects

- **`Easings`**: Over 20 mathematical easing curves: `Linear`, `InQuad`, `OutQuad`, `InOutQuad`, `InCubic`, `OutCubic`, `InOutCubic`, `InExpo`, `OutExpo`, `BackOut`, `ElasticOut`, `BounceOut`.
- **`UiAnimation`**: Transformation keyframe record specifying duration, delay, easing, opacity transitions, scale transitions, and translation offsets.
- **`UiEffects` Presets**:
  - `slidePresent(direction, distance, duration, easing)`: Smooth presentation entry slide.
  - `popPresent(...)`, `fadeScalePresent(...)`.
  - `fadeIn()`, `slideInFromLeft()`, `slideInFromRight()`, `slideInFromTop()`, `slideInFromBottom()`, `popIn()`, `scaleIn()`, `scaleOut()`, `bounceIn()`, `dropIn()`, `softRise()`.

### UiGradient & Geometry Math

- **`UiGradient`**: Analytical two-stop linear gradient model with 2D interpolation (`horizontal()`, `vertical()`, `diagonal()`).
- **`DisplayShapeMath` & `TRSResult`**: Analytical TRS matrix decomposition engine calculating exact Translation, LeftRotation, Scale, and RightRotation.
- **`UiIconRegistry`**: Central registry for custom named `ItemStack` icons.

---

## 8. Document Parsing & Loading (`vn.haohan.displayui.api.loader`)

### UiDocumentLoader

Loads and compiles UI documents from YAML/JSON files or raw strings.

```java
UiDocumentLoader loader = service.documentLoader();
UiDocument doc = loader.load(new File(getDataFolder(), "menus/main_menu.yml"));
```

---

## 9. In-Game Debugging System

HaoHanDisplayUI features a built-in real-time debugging engine for inspection and diagnostics directly in-game.

### UiDebugManager & Debug Architecture

Access the debug engine via `service.debug()`:
- **`UiDebugSession`**: Tracks player-specific debugging states, active inspect mode, and target highlight overlays.
- **`UiDebugInspector`**: Computes real-time crosshair raycasting against active layers, containers, and components.
- **BossBar Targeting HUD**: Displays the currently focused Component, Container ID, Layer Z-index, Bounds `(x, y, w, h)`, and World Distance on the player's screen in real time.

### The `/uidebug` Command Suite

Administrators can use `/uidebug` (or `/hhdui debug`) with rich diagnostic subcommands:

| Subcommand | Description |
|---|---|
| `/uidebug inspect [on\|off]` | Toggles real-time crosshair inspection and BossBar diagnostic HUD. |
| `/uidebug clickinspect [on\|off]` | Toggles click-to-inspect mode: clicking a component prints its full properties to chat without firing actions. |
| `/uidebug tree [handle-id]` | Dumps the hierarchical Layer -> Container -> Component tree to chat or console. |
| `/uidebug layer <layerId> toggle` | Dynamically toggles visibility of a specific layer in real time. |
| `/uidebug session` | Displays active debug session stats for the current player. |
| `/uidebug clear` | Cleans up all active debug sessions and HUD overlays. |

---

## 10. Practical Examples

### Example 1: Building a Modern Panel with LayerManager and Components

```java
DisplayUiService service = Bukkit.getServicesManager().load(DisplayUiService.class);

LayerManager manager = new LayerManager();

// 1. Background Layer (Z = 0)
manager.createLayer("background", 0)
    .bounds(-100, -60, 200, 120)
    .backgroundColor(Color.fromRGB(24, 28, 36))
    .borderRound(6.0f);

// 2. Foreground Interactive Layer (Z = 1)
Layer uiLayer = manager.createLayer("ui", 1);

Container rootPanel = Container.builder("root_panel")
    .anchor(UiAnchorPoint.CENTER)
    .origin(UiAnchorPoint.CENTER)
    .size(180, 100)
    .build();

// Title
rootPanel.addComponent(new TextComponent("title", UiText.of("§6§lDUNGEON TELEPORTER"), 0, 15));

// Action Button
ButtonComponent warpBtn = new ButtonComponent("btn_warp", UiText.of("§a§lENTER DUNGEON"), 120, 24);
warpBtn.backgroundColor(Color.fromRGB(35, 120, 50));
warpBtn.hoverBackgroundColor(Color.fromRGB(45, 160, 65));
warpBtn.onClick(event -> {
    event.player().sendMessage("§aTeleporting to the dungeon...");
    event.player().teleport(dungeonLocation);
});

rootPanel.addComponent(warpBtn);
uiLayer.addContainer(rootPanel);

// Spawn UI in front of player
UiHandle handle = service.create("my_plugin", player.getLocation().add(0, 1.5, 0), manager);
handle.animate(UiEffects.slidePresent("UP", 15.0f, 10, Easings.OutCubic));
```

---

### Example 2: Collapsible Settings Menu with DropdownContainer

```java
LayerManager manager = new LayerManager();
Layer uiLayer = manager.createLayer("settings_ui", 0);

Container mainContainer = Container.builder("settings_panel")
    .size(190, 140)
    .backgroundColor(Color.fromARGB(230, 20, 24, 32))
    .borderRound(5.0f)
    .build();

// Dropdown for Audio Options
DropdownContainer audioDropdown = DropdownContainer.builder("dropdown_audio")
    .headerTitle(Component.text("Audio Preferences", NamedTextColor.AQUA))
    .headerHeight(18.0f)
    .animationType(DropdownAnimationType.SLIDE_AND_FADE)
    .build();

CheckboxComponent musicToggle = new CheckboxComponent("chk_music", "Background Music", true);
musicToggle.onChange(event -> {
    player.sendMessage("§7Music toggled: " + (event.asChecked() ? "§aON" : "§cOFF"));
});

SliderComponent volumeSlider = new SliderComponent("sld_vol", 0.0f, 100.0f, 75.0f);
volumeSlider.onChange(event -> {
    player.sendMessage("§7Volume set to: §e" + event.asInt() + "%");
});

audioDropdown.addDropdownItem(musicToggle);
audioDropdown.addDropdownItem(volumeSlider);

mainContainer.addContainer(audioDropdown);
uiLayer.addContainer(mainContainer);

service.create("my_plugin", player.getLocation().add(0, 1.5, 0), manager);
```

---

### Example 3: Seamless Multi-Page Navigation with UiHandle.replace(...)

```java
// Page 1: Main Menu
UiDocument mainMenuDoc = buildMainMenu();
UiHandle handle = service.create("my_plugin", origin, mainMenuDoc);

handle.onClick(click -> {
    if ("btn_open_shop".equals(click.control().id())) {
        // Switch to Shop Page atomically without entity morphing glitches
        UiDocument shopDoc = buildShopMenu();
        handle.replace(shopDoc);
        handle.animate(UiEffects.fadeIn(8));
    }
});
```

---

### Example 4: 3D Mob Exhibition with Cursor Tracking

```java
// Display a live Warden mob tilted and tracking the player's crosshair cursor
MobEntityNode wardenNode = new MobEntityNode(EntityType.WARDEN, 100, 70, 1.0f)
    .cursorTrack()
    .withCustomizer(living -> {
        living.setCustomName("§4§lAncient Guardian");
        living.setCustomNameVisible(true);
    });

UiDocument mobDoc = UiDocument.builder(200, 150)
    .background(Color.fromRGB(15, 15, 20))
    .node(wardenNode)
    .build();

UiHandle handle = service.create("my_plugin", player.getEyeLocation().add(player.getLocation().getDirection().multiply(2)), mobDoc);
```

---

### Example 5: High-Performance Mesh Gradient Canvas

```java
UiGradient gradient = UiGradient.diagonal(
    Color.fromRGB(0, 150, 255),
    Color.fromRGB(180, 0, 255)
);

UiGradientBackgroundNode bgNode = new UiGradientBackgroundNode(
    new UiRect(0, 0, 220, 120),
    0.001f,
    gradient
).withSlices(16);

UiDocument gradientDoc = UiDocument.builder(220, 120)
    .node(bgNode)
    .node(new TextNode(UiText.of("§f§lPREMIUM DISPLAY UI"), 110, 60, 1.3f, Color.WHITE))
    .build();

UiHandle handle = service.create("my_plugin", origin, gradientDoc);
handle.animate(UiEffects.fadeIn(20));
```
