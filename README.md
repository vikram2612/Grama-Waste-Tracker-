# 🌿 Grama-Waste Tracker

**Clean Village, Green Village** | ಸ್ವಚ್ಛ ಹಳ್ಳಿ, ಹಸಿರು ಹಳ್ಳಿ

A modern Android application for rural waste collection management, helping villagers track the live location of the waste collection tractor ("Kachara Gaadi"), receive arrival alerts, and report illegal garbage dumping locations ("Blackspots").

---

## 📱 Features

### 👤 Citizen Features
| Feature | Description |
|---------|-------------|
| **Login/Register** | Email-based authentication via Firebase Auth |
| **Live Tractor Tracking** | Real-time tractor location on Google Maps with ETA |
| **Tractor Status** | Active/Offline status with driver info and route name |
| **Blackspot Reporting** | Report illegal dumps with photo + auto GPS + description |
| **My Reports** | View submitted reports and their resolution status |
| **Waste Segregation Guide** | Bilingual guide for dry, wet, and hazardous waste |
| **AI Waste Classifier** | Classify waste items into categories with confidence score |
| **AI Chatbot** | Interactive waste disposal guidance chatbot |
| **Push Notifications** | Alerts when the tractor is nearby (FCM) |
| **Bilingual UI** | Full Kannada (ಕನ್ನಡ) + English support |

### 🛡️ Admin/Panchayat Features
| Feature | Description |
|---------|-------------|
| **Admin Dashboard** | Overview with stats: total, pending, resolved reports |
| **Manage Tractor** | Update tractor location, toggle active/offline status |
| **View All Reports** | See all blackspot reports with filter (All/Pending/Resolved) |
| **Mark Resolved** | Resolve blackspot reports with confirmation |
| **View Report Images** | Full image display for reported blackspots |

### 🤖 GenAI Features
| Feature | Description |
|---------|-------------|
| **AI Waste Classifier** | Keyword-based classification into dry/wet/hazardous |
| **Confidence Scoring** | Shows classification confidence percentage |
| **Disposal Advice** | Category-specific disposal recommendations |
| **Chatbot Assistant** | Interactive Q&A for waste management guidance |

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **DI** | Hilt (Dagger) |
| **Authentication** | Firebase Auth |
| **Database** | Firebase Realtime Database |
| **Storage** | Firebase Storage |
| **Maps** | Google Maps SDK + Maps Compose |
| **Notifications** | Firebase Cloud Messaging (FCM) |
| **Image Loading** | Coil |
| **Permissions** | Accompanist Permissions |
| **Navigation** | Jetpack Navigation Compose |

---

## 📁 Project Structure

```
app/src/main/java/com/grama/wastetracker/
├── GramaWasteApp.kt                    # Application class (Hilt + Notification Channels)
├── data/
│   ├── model/
│   │   └── Models.kt                   # User, TractorLocation, BlackspotReport, etc.
│   └── repository/
│       ├── AuthRepository.kt           # Firebase Auth operations
│       ├── TractorRepository.kt        # Real-time tractor location
│       ├── BlackspotRepository.kt      # Blackspot CRUD + image upload
│       ├── NotificationRepository.kt   # Push notification management
│       └── WasteGuideRepository.kt     # Waste guide data
├── di/
│   ├── FirebaseModule.kt              # Firebase instance providers
│   └── RepositoryModule.kt            # Repository bindings
├── service/
│   └── GramaFCMService.kt            # FCM push notification handler
└── ui/
    ├── MainActivity.kt                 # Entry point with role-based routing
    ├── navigation/
    │   ├── Screen.kt                   # Route definitions
    │   └── NavGraph.kt                 # Navigation graph
    ├── theme/
    │   ├── Color.kt                    # Green/Blue village color palette
    │   ├── Theme.kt                    # Material 3 theme (light + dark)
    │   ├── Type.kt                     # Typography system
    │   └── Shape.kt                    # Shape definitions
    ├── viewmodel/
    │   ├── AuthViewModel.kt            # Login, register, session
    │   ├── HomeViewModel.kt            # Tractor tracking + ETA
    │   ├── BlackspotViewModel.kt       # Report submission + listing
    │   ├── AdminViewModel.kt           # Dashboard + tractor management
    │   ├── WasteGuideViewModel.kt      # Waste guide data
    │   └── AiAssistantViewModel.kt     # AI classifier + chatbot
    └── screens/
        ├── LoginScreen.kt              # Login with gradient UI
        ├── RegisterScreen.kt           # Registration form
        ├── HomeScreen.kt               # Map + status + quick actions
        ├── ReportBlackspotScreen.kt    # Photo + GPS + description form
        ├── BlackspotListScreen.kt      # User's reports list
        ├── WasteGuideScreen.kt         # Expandable bilingual guide cards
        ├── AiAssistantScreen.kt        # Chatbot + classifier dual-mode
        ├── NotificationsScreen.kt      # Notification center
        ├── ProfileScreen.kt            # User profile + settings
        ├── AdminDashboardScreen.kt     # Admin stats + management
        ├── AdminReportsScreen.kt       # All reports with filter/resolve
        └── AdminTractorScreen.kt       # Tractor location update form
```

---

## 🗄️ Firebase Database Structure

```json
{
  "users": {
    "<uid>": {
      "uid": "string",
      "fullName": "string",
      "email": "string",
      "phone": "string",
      "villageName": "string",
      "role": "citizen | admin",
      "fcmToken": "string",
      "createdAt": "timestamp"
    }
  },
  "tractor_location": {
    "latitude": "double",
    "longitude": "double",
    "isActive": "boolean",
    "speed": "double",
    "heading": "float",
    "driverName": "string",
    "lastUpdated": "timestamp",
    "routeName": "string"
  },
  "blackspot_reports": {
    "<reportId>": {
      "id": "string",
      "userId": "string",
      "userName": "string",
      "description": "string",
      "imageUrl": "string",
      "latitude": "double",
      "longitude": "double",
      "status": "pending | resolved",
      "createdAt": "timestamp",
      "resolvedAt": "timestamp",
      "resolvedBy": "string",
      "adminNotes": "string"
    }
  },
  "notifications": {
    "<uid>": {
      "<notifId>": {
        "title": "string",
        "message": "string",
        "type": "tractor_alert | report_update | general",
        "isRead": "boolean",
        "createdAt": "timestamp"
      }
    }
  },
  "waste_guides": {
    "<guideId>": {
      "category": "dry | wet | hazardous",
      "title": "string",
      "titleKn": "string (Kannada)",
      "description": "string",
      "descriptionKn": "string",
      "items": ["string"],
      "itemsKn": ["string"],
      "disposalTip": "string",
      "disposalTipKn": "string"
    }
  }
}
```

---

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Google account for Firebase & Maps

### Step 1: Clone the Project
```bash
git clone <repository-url>
cd "Grama Waste tracker"
```

### Step 2: Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (e.g., "GramaWasteTracker")
3. Add an Android app with package name: `com.grama.wastetracker`
4. Download `google-services.json` and place it in `app/` directory
5. Enable these Firebase services:
   - **Authentication** → Enable Email/Password sign-in
   - **Realtime Database** → Create database, paste rules from `firebase-database-rules.json`
   - **Storage** → Enable, paste rules from `firebase-storage-rules.txt`
   - **Cloud Messaging** → Enable for push notifications

### Step 3: Google Maps API Key
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Enable "Maps SDK for Android"
3. Create an API key (restrict to Android apps with your package + SHA-1)
4. Add to `local.properties`:
   ```properties
   MAPS_API_KEY=AIzaSy_YOUR_API_KEY_HERE
   ```

### Step 4: Build & Run
```bash
# Open in Android Studio and sync Gradle
# OR build from terminal:
./gradlew assembleDebug
```

### Step 5: Create Admin User
1. Register a normal user through the app
2. Go to Firebase Console → Realtime Database
3. Find the user under `users/<uid>`
4. Change `"role"` from `"citizen"` to `"admin"`
5. Re-login — the app will redirect to the Admin Dashboard

---

## 🎨 Design System

### Color Palette
| Color | Hex | Usage |
|-------|-----|-------|
| Green Primary | `#2E7D32` | Primary actions, headers |
| Green Dark | `#1B5E20` | Status bar, admin header |
| Green Light | `#4CAF50` | Active status, success |
| Blue Primary | `#0288D1` | Secondary actions, AI features |
| Orange Accent | `#FF9800` | Warnings, pending status |
| Red Error | `#D32F2F` | Errors, location pins |

### Theme
- Material 3 with custom color scheme
- Light & Dark mode support
- Rounded cards (16–24dp corners)
- Gradient headers on auth screens
- Status-colored badges for reports

---

## 📋 Key Implementation Details

### Real-time Tractor Tracking
- Uses Firebase Realtime Database listeners (`ValueEventListener`)
- Location updates flow via Kotlin `callbackFlow`
- ETA calculated using **Haversine formula** (20 km/h average tractor speed)

### Image Uploads
- Images picked via `ActivityResultContracts.GetContent()`
- Uploaded to Firebase Storage under `blackspot_images/`
- Download URL stored in the report's `imageUrl` field

### AI Classification
- **Keyword-based classifier** using a curated vocabulary for each waste category
- Confidence scoring based on keyword match count
- Returns category, confidence %, explanation, and disposal advice

### Navigation
- Role-based start destination (Citizen → Home, Admin → Dashboard)
- Sealed class routes for type-safe navigation
- Proper back stack management with `popUpTo`

### Security
- Firebase Database rules enforce role-based access
- Admin-only write access for tractor location
- Users can only read their own notifications
- Storage rules limit uploads to 10MB images

---

## 📦 Dependencies Summary

| Library | Version | Purpose |
|---------|---------|---------|
| Compose BOM | 2024.01.00 | UI toolkit |
| Material 3 | Latest via BOM | Design system |
| Firebase BOM | 32.7.1 | Firebase services |
| Hilt | 2.50 | Dependency injection |
| Maps Compose | 4.3.0 | Google Maps in Compose |
| Coil | 2.5.0 | Image loading |
| Navigation Compose | 2.7.6 | Screen navigation |
| Accompanist | 0.34.0 | Permissions |
| SplashScreen | 1.0.1 | Android 12+ splash |

---

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

---

## 📄 License

This project is developed for rural waste management purposes.
Free to use for Gram Panchayat and village-level deployment.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

**Built with ❤️ for Clean Villages**
