<div align="center">

# 🏋️ Lify

**A modern, offline-first gym tracker for Android.**
Log your workouts, track your progress, and stay consistent.

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Status](https://img.shields.io/badge/status-in%20development-orange)

</div>

---

## 📖 Overview

Lify is a native Android application for tracking gym workouts. It is built
with a modern Android stack (Kotlin, Jetpack Compose and Room) and stores
all data locally on the device, so it works without an internet connection.

> ⚠️ **Project status:** Lify is under active development. Features and UI
> may change.

## 📸 Screenshots

<div align="center">
  <img src="docs/screenshots/screenshot_1.jpg" width="22%" alt="Screenshot 1" />
  <img src="docs/screenshots/screenshot_2.jpg" width="22%" alt="Screenshot 2" />
  <img src="docs/screenshots/screenshot_3.jpg" width="22%" alt="Screenshot 3" />
  <img src="docs/screenshots/screenshot_4.jpg" width="22%" alt="Screenshot 4" />
</div>

## ✨ Features

- [ ] [Create and manage workout routines]
- [ ] [Log exercises, sets, reps and weight]
- [ ] [Workout history]
- [ ] [Progress tracking and statistics]
- [x] Local persistence with Room (no account required)
- [x] Fully declarative UI with Jetpack Compose

## 🛠️ Tech Stack

| Layer          | Technology                          |
| -------------- | ----------------------------------- |
| Language       | Kotlin                              |
| UI             | Jetpack Compose                     |
| Persistence    | Room                                |
| Build system   | Gradle (Kotlin DSL)                 |
| IDE            | Android Studio                      |

## 🏗️ Architecture

[Describe your architecture here, e.g. MVVM with a ViewModel per screen and a
repository layer on top of Room.]

```
app/
└── src/main/
    ├── java/.../        # Kotlin sources (UI, data, domain)
    └── res/             # Resources
```

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable)
- JDK 17 or higher
- Android SDK with a device or emulator running Android [X.X]+

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/Karm0na/Lify.git
cd Lify

# 2. Open the project in Android Studio and let Gradle sync

# 3. Run on an emulator or physical device
./gradlew installDebug
```

### Build

```bash
./gradlew assembleDebug     # Debug APK
./gradlew test              # Unit tests
```

## 🗺️ Roadmap

- [ ] [Planned feature 1]
- [ ] [Planned feature 2]
- [ ] Data export / backup
- [ ] Dark / light theme polish
- [ ] Google Play release

## 🤝 Contributing

Contributions, issues and feature requests are welcome. Feel free to open an
[issue](https://github.com/Karm0na/Lify/issues) or submit a pull request.

## 📄 License

[Choose a license, e.g. MIT.] See `LICENSE` for details.

## 👤 Author

**Karm0na** · [GitHub](https://github.com/Karm0na)
