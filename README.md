# FarmYukti - Frontend

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android)
![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue?style=flat)
![UI Toolkit](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat)

FarmYukti is an advanced agricultural marketplace application designed to bridge the gap between farmers and buyers. This repository houses the frontend mobile client, engineered specifically for the Android ecosystem. It provides a highly responsive, location-aware interface for users to list, discover, and transact agricultural goods efficiently.

## 📖 Table of Contents
- [Project Overview](#project-overview)
- [System Architecture](#system-architecture)
- [Core Technologies](#core-technologies)
- [Key Features](#key-features)
- [Dataflow Architecture](#dataflow-architecture)
- [Getting Started](#getting-started)
- [Repository Structure](#repository-structure)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Project Overview

The primary objective of the FarmYukti frontend is to deliver a seamless user experience while handling complex spatial data and dynamic market inventories. By leveraging a purely declarative UI paradigm and robust state management, the application ensures that the presentation layer remains decoupled from business logic and data retrieval processes.

## 🏗 System Architecture

This project strictly adheres to the **Model-View-ViewModel (MVVM)** architectural pattern, ensuring a clear separation of concerns, improved testability, and resilient state management during lifecycle events.

*   **View Layer:** Built entirely with Jetpack Compose. Functions act purely as observers of the ViewModel state, triggering UI events based on user interactions.
*   **ViewModel Layer:** Manages UI state using `StateFlow` and handles asynchronous data operations via Kotlin Coroutines. It acts as the intermediary, formatting domain data for presentation.
*   **Data/Repository Layer:** Interfaces with the FarmYukti Spring Boot backend. It handles network requests (via Retrofit/Ktor), data serialization, and manages spatial schemas required for location-based features.

## 🛠 Core Technologies

*   **Language:** Kotlin (leveraging Coroutines & Flow for asynchronous programming)
*   **UI Toolkit:** Jetpack Compose (Declarative UI framework)
*   **Architecture Components:** ViewModels, Lifecycle-aware components
*   **Dependency Injection:** Hilt / Dagger (if implemented, adjust accordingly)
*   **Networking:** Retrofit / OkHttp (for REST API consumption)

## ✨ Key Features

*   **Declarative User Interface:** A fully modernized UI layer built without XML, enabling rapid iteration and fluid, state-driven animations.
*   **Spatial Data Integration:** Consumes complex location data schemas from the backend to enable proximity-based search and local marketplace discovery.
*   **Lifecycle-Aware State:** Robust handling of configuration changes (e.g., screen rotations) ensuring zero data loss and uninterrupted user workflows.
*   **Dynamic Dataflow:** Efficient consumption of REST APIs to reflect real-time agricultural inventory and pricing changes.

## 🔄 Dataflow Architecture

The application's dataflow is designed for predictability and performance:
1.  **User Action:** The user interacts with a Compose UI element (e.g., searching for local produce).
2.  **Intent:** The UI dispatches an event/intent to the ViewModel.
3.  **Processing:** The ViewModel launches a Coroutine to fetch data via the Repository.
4.  **Backend Communication:** The Repository executes a REST API call to the Spring Boot backend.
5.  **State Update:** The response is parsed, and the ViewModel updates the immutable `StateFlow`.
6.  **Recomposition:** The Compose UI observes the state change and recomposes only the affected UI nodes.

## 🚀 Getting Started

### Prerequisites
*   [Android Studio](https://developer.android.com/studio) (Ladybug or newer recommended)
*   JDK 17 or higher
*   Android SDK API Level 24+

### Installation & Setup

1. **Clone the repository:**
    
        git clone https://github.com/harsh-dy0dx/FarmYukti---Frontend.git

2. **Open the project:**
    Launch Android Studio and select `File > Open`, then navigate to the cloned directory.

3. **Configure the Environment:**
    Ensure your `local.properties` or environment variables contain the necessary base URLs or API keys required to connect to the FarmYukti backend.

4. **Build and Run:**
    Sync the Gradle project and hit `Run` to deploy the app to an emulator or physical device.

## 📁 Repository Structure

    FarmYukti---Frontend/
    ├── app/
    │   ├── src/
    │   │   ├── main/
    │   │   │   ├── java/com/farmyukti/         # Application source code
    │   │   │   │   ├── ui/                     # Jetpack Compose screens and components
    │   │   │   │   ├── viewmodel/              # State management and UI logic
    │   │   │   │   ├── model/                  # Data classes and DTOs
    │   │   │   │   └── repository/             # Network and data access logic
    │   │   │   ├── res/                        # Resources (Drawables, Values)
    │   │   │   └── AndroidManifest.xml         # App configuration and permissions
    │   └── build.gradle.kts                    # Module-level build configurations
    ├── build.gradle.kts                        # Project-level build configurations
    └── README.md                               # Project documentation

## 🤝 Contributing

We welcome contributions! Please follow these steps to contribute:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeature`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Open a Pull Request for review.

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.



<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-43-14-33_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/5644dcc2-160e-4ef7-af5c-85480777885f" />
<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-43-48-58_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/3ddde54d-2769-415a-bb95-db0c73ac67f0" />
<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-43-35-19_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/c00b14af-4207-4556-bc0c-488dd6a1b49e" />
<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-43-58-85_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/b1f866bf-e023-4ed2-92e5-8352dffe5835" />
<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-43-23-82_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/5d8f0c0e-1187-4d84-a43c-7056d280533a" />
<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-43-27-97_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/60dead02-6ab7-446f-b9d8-a600b3391df7" />
<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-43-10-96_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/fdba1675-8830-408b-9fb3-d914d4bb3cda" />
<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-43-19-83_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/c3016242-a6ab-4b62-81e7-27d8a724ffe4" />
<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-44-16-99_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/06ad79d9-a5da-4ae1-8dc3-80e5aaff3f59" />
<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-44-04-34_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/1d4164c2-6e74-4c6c-aaae-65929d27e968" />
<img width="1080" height="2400" alt="Screenshot_2026-08-29-23-43-43-36_9810b80aa2c9e3d8e20ae9e42b22a420 jpg" src="https://github.com/user-attachments/assets/d52390b2-88ed-4be2-a795-f6cc5e277650" />




