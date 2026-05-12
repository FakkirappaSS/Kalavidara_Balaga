# Kalavidara-Balaga: Folk Artist Talent Hub

"Transforming Karnataka's traditional troupes into sustainable, bookable professionals."

## Overview
Kalavidara-Balaga (meaning 'Artists' Collective' in Kannada) is a GenAI-powered Android marketplace designed to connect Karnataka's traditional folk performance troupes (Dollu Kunitha, Yakshagana, Pooja Kunitha, etc.) with urban event managers and cultural organizations. It transitions folk artists from relying on a razor-thin seasonal calendar to having a year-round, financially viable, professional digital presence.

## Features
* **Role-Based Authentication**: Secure login for Artists (Troupe Admin), Clients (Event Planners), and App Admins via Firebase.
* **GenAI-Powered Biographies**: A 1-click feature leveraging OpenRouter (Mistral/Gemini) to automatically draft highly professional, culturally rich troupe biographies based on simple inputs (Art Form, Experience, Member Count).
* **AI Portfolio Image Captioning**: Vision AI automatically analyzes uploaded performance photos and generates vivid, contextual captions.
* **Advanced Search & Discovery**: An in-memory, zero-latency search engine allowing clients to discover troupes by District and Art Type simultaneously.
* **Direct Booking Intents**: Clients can tap to instantly launch the Android Phone Dialer or WhatsApp with the troupe's contact details pre-filled.
* **Staggered Gallery & Video Links**: A beautiful `StaggeredGridLayout` portfolio with pinch-to-zoom capabilities, alongside simulated YouTube/Google Drive performance links.
* **Zero-Cost Storage Architecture**: A custom image processing algorithm that heavily compresses and Base64-encodes images directly into Firestore, bypassing expensive Firebase Storage limits.
* **Admin Moderation**: A secure Admin dashboard to review and approve/reject new troupe registrations before they go public.
* **Vibrant Traditional UI**: Custom typography featuring **Noto Sans Kannada** and a warm color palette (Maroon, Gold, Saffron) reflecting Karnataka's heritage.
* **Invisible Analytics**: Tracks Profile Views and Booking Inquiries for each troupe.

## Tech Stack
* **Platform**: Android (Kotlin / Jetpack Compose)
* **Architecture**: MVVM + Repository Pattern + Clean Architecture
* **Dependency Injection**: Dagger Hilt
* **Database & Auth**: Firebase Firestore & Firebase Authentication
* **AI Layer**: OpenRouter (Mistral-7B / Gemini 2.0 Pro) & Google AI
* **Image Loading & Processing**: Coil (Compose) + Custom Base64 Encoding
* **Navigation**: Jetpack Compose Navigation

## Setup Instructions
1. Clone the repository.
2. Ensure you have a valid `google-services.json` file in the `app/` directory connected to your Firebase Project.
3. Obtain an OpenRouter API Key and configure it in `Constants.kt`.
4. Build and Run the app on an emulator or physical device running Android 7.0 (API 24) or higher.

## Impact
By providing a searchable digital identity, Kalavidara-Balaga directly fights cultural erosion. It enables younger generations of folk artists to remain in their traditional arts by making it a financially sustainable profession, preventing migration to unrelated daily-wage labor.

---
*Document prepared for the Android App Development using GenAI program.*
