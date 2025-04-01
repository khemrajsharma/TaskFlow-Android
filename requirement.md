# ModularTaskManager  

A modern Android task management app showcasing **multi-module architecture**, **Jetpack Compose**, and **MVVM** with Clean Architecture principles. Built to demonstrate scalable Android development practices.

## Tech Stack 🛠️
- **100% Kotlin** 
- **Multi-module Architecture** (Features, Core, Data, Domain)
- **Jetpack Compose** - Modern declarative UI
- **Hilt** - Dependency injection
- **Room** - Local persistence
- **WorkManager** - Background task scheduling
- **Kotlin Coroutines/Flow** - Asynchronous operations
- **Material 3** - Theming components

## Architecture ⚙️
Clean Architecture with MVVM pattern:
```
:app (entry point)
|
├── :core (common components)
│   ├── ui (theme/components)
│   ├── utils (extensions/helpers)
│   └── navigation (compose navigation)
|
├── :data (data layer)
│   ├── local (Room database/DAOs)
│   └── repository (data repository impl)
|
├── :domain (business logic)
│   ├── model (entities)
│   ├── repository (interfaces)
│   └── usecases (business rules)
|
└── :features (UI features)
    ├── tasks (task list/details)
    └── reminders (reminder management)
```

## Key Features ✨
- **Modular Design** - Independent feature modules for scalability
- **Offline First** - Room database with automatic WorkManager sync
- **Modern UI** - Jetpack Compose with dark/light themes
- **Dependency Injection** - Hilt for cross-module dependencies
- **Testing** - 70%+ test coverage (Unit tests + Compose UI tests)

## Testing 🧪
- **Unit Tests**: ViewModels, Use Cases, Repositories
- **UI Tests**: Compose component testing
- **Integration Tests**: Navigation flows