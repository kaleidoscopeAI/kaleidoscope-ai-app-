# Kaleidoscope AI

A modular, production-ready, mathematically rigorous AGI framework for Android, featuring:

- **Physics-based Cube Engine**: 3D lattice of nodes and bonds with probabilistic tension, stress, and emergent behavior.
- **LLM Integration**: Real-time feedback and reflection via Ollama or other LLMs.
- **Web Crawler**: Feeds external data into the cube for adaptive intelligence.
- **Persistent Memory**: Room database for node/bond state, reflections, and history.
- **3D Visualization**: Jetpack Compose and OpenGL renderer for real-time cube state.
- **MVVM + Hilt**: Clean architecture, DI, and lifecycle-aware orchestration.
- **Export/Import**: CSV export of all state, session import for portability.
- **Testing & CI**: Unit tests and GitHub Actions for robust quality.

## Project Structure

```
KaleidoscopeAI/
├── core/                # Core logic: Vec3, Node, Bond, Membrane, Engine, etc.
├── data/                # Room entities, DAOs, repository
├── di/                  # Hilt DI modules
├── llm/                 # LLM/Ollama client and listener
├── renderer/            # OpenGL renderer
├── ui/screens/          # Jetpack Compose screens
├── ui/theme/            # Compose theme
├── ui/viewmodel/        # ViewModel(s)
├── util/                # Export/import utilities
├── app/                 # Android app module
├── MainActivity.kt      # App entry point
├── KaleidoscopeAIApp.kt # Hilt application class
├── build.gradle.kts     # Gradle build files
└── ...
```

## Quick Start

1. **Clone and open in Android Studio**
2. **Build and run** (requires Android SDK 34+)
3. **Start Ollama server** (or configure LLM endpoint in settings)
4. **Explore the UI**: Cube, Chat, Memory, Settings
5. **Export/Import**: Use utilities to save/load state

## Key Modules

- `core/`: Physics, topology, and intelligence logic
- `data/`: Persistent memory (Room)
- `llm/`: LLM integration (Ollama, streaming)
- `renderer/`: 3D OpenGL visualization
- `ui/`: Compose UI, navigation, ViewModel
- `util/`: Export/import tools

## Testing & CI
- Run `./gradlew test` for unit tests
- GitHub Actions workflow for CI

## Extending
- Add new intelligence modules, analytics, or domain-specific logic in `core/`
- Integrate new LLMs or data sources in `llm/` and `web/`
- Expand UI/UX in `ui/screens/`

## License
MIT
