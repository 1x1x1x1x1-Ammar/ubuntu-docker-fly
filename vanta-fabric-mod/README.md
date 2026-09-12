# VANTA Client Mod

A feature-rich Minecraft Fabric client mod with HUD customization, QoL modules, cosmetics, and performance optimizations.

## Features

### 🎨 HUD System
- **Fully customizable HUD elements** - Drag, drop, resize with snap-to-grid
- **30+ HUD elements** including FPS, coordinates, keystrokes, CPS, armor status, potion effects, and more
- **HUD Editor** - Press H to toggle full layout editor
- **Export/Import layouts** as `.vanta-hud` files
- **Preset layouts**: FPS Mode, Streamer, Survival, Minimal, Full Info

### ⚡ QoL Modules
- **Zoom** - Smooth cinematic zoom (hold C)
- **Fullbright** - Instant gamma adjustment without night vision
- **Auto Reconnect** - Automatic server reconnection with countdown
- **Waypoints** - Per-dimension waypoints with nether portal conversion
- **Inventory Sorter** - Sort any container with middle-click
- **Screenshot Enhancer** - Auto-screenshot on achievements
- **Chat Tweaks** - Timestamps, highlights, copy messages

### 🌟 Cosmetics (Client-side)
- **Capes** - 4 built-in capes + custom upload support
- **Wings** - Animated decorative wings
- **Trail Effects** - 5 particle trail types
- **RGB HUD** - Rainbow cycling colors with multiple modes

### 🚀 Performance
- Bundled with Sodium, Lithium, FerriteCore, Entity Culling
- FPS limiter for background
- Particle reduction slider
- Smart chunk loading
- Memory cleaner with auto-GC

## Installation

### Prerequisites
- Java 17 or higher
- Minecraft 1.20.4
- Fabric Loader 0.15.0+
- Fabric API

### Build from Source

```bash
# Clone the repository
git clone https://github.com/vanta-client/mod.git
cd vanta-fabric-mod

# Build with Gradle
./gradlew build
```

The built JAR will be in `build/libs/`

### Manual Installation
1. Download the latest release JAR
2. Place it in your `.minecraft/mods/` folder
3. Ensure you have Fabric Loader and Fabric API installed
4. Launch Minecraft

## Configuration

### In-Game
- Press **Right Shift** to open the Click GUI
- Press **H** to toggle HUD Editor
- All settings save automatically

### Config File
Location: `.minecraft/config/vanta.json`

```json
{
  "hudEnabled": true,
  "clickGuiKey": 54,
  "zoomKey": 29,
  "maxZoom": 10.0,
  "cosmeticsEnabled": true,
  "equippedCape": "vanta_default"
}
```

## Keybindings

| Action | Default Key |
|--------|-------------|
| Open Click GUI | Right Shift |
| Toggle HUD Editor | H |
| Zoom | C (hold) |
| Fullbright | L |
| Add Waypoint | Insert |
| Sort Inventory | Middle Mouse |

## API Services (Launcher)

The VANTA Launcher includes:
- **Modrinth API** - Browse and download mods
- **CurseForge API** - Alternative mod source
- **Download Manager** - Parallel downloads with queue system
- **Config Manager** - Profile-based settings

## Project Structure

```
vanta-fabric-mod/
├── src/main/java/gg/vanta/client/mod/
│   ├── VantaClientMod.java      # Main initializer
│   ├── config/
│   │   └── VantaConfig.java     # Config system
│   ├── gui/
│   │   ├── ClickGuiScreen.java  # Click GUI
│   │   └── modules/
│   │       ├── Module.java      # Module base class
│   │       └── ModuleManager.java
│   ├── hud/
│   │   ├── HudRenderer.java     # HUD rendering
│   │   └── elements/            # HUD element implementations
│   ├── qol/
│   │   ├── ZoomModule.java
│   │   ├── FullbrightModule.java
│   │   └── AutoReconnectModule.java
│   ├── cosmetics/
│   │   ├── CapeRenderer.java
│   │   ├── WingsRenderer.java
│   │   ├── TrailRenderer.java
│   │   └── RGBEngine.java
│   └── waypoints/
│       └── WaypointManager.java
├── fabric.mod.json
├── build.gradle
└── README.md
```

## Getting CurseForge API Key

1. Go to https://console.curseforge.com/
2. Sign in with your account
3. Create a new API key
4. Copy the key and add it to launcher settings

## Development

### Requirements
- JDK 17
- Gradle 8+
- IDE (IntelliJ recommended)

### Setup in IntelliJ
1. Open project and select `build.gradle`
2. Run `./gradlew genSources` to decompile Minecraft
3. Run `./gradlew runClient` to test

## License

MIT License - See LICENSE file for details

## Support

- Discord: https://discord.gg/vanta
- GitHub Issues: https://github.com/vanta-client/mod/issues
- Website: https://vanta.gg

---

**VANTA Client** - The ultimate Minecraft client experience 🚀
