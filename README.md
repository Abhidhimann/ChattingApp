# Chatting App by Abhishek

This Android application allows users to discover and connect with others, view profiles, and engage in real-time one-on-one messaging.

## Features

- **Sign in/up page**: Log in/register using email and password or ****Google Single Sign-On (SSO)****.
- **Profile Page**: View and update your profile, including uploading a profile picture.
- **Discover Users Page**: Explore and find other users within the app and send connection requests.
- **Requests Page**: Manage incoming connection requests by accepting or rejecting them.
- **Chat list page**: See previous/new chats.
- **Chat Page**: Engage in real-time, one-on-one messaging with other users.
- **Notification**: Receive new messages alert through push notifications via ****Firebase Cloud Messaging (FCM)****.

## Tech Stacks

Common tech stacks used are:

- **Architecture** - MVI Architecture.
- **Retrofit + OkHttp** - RESTful API and networking client.
- **Hilt** - For dependency injection.
- **Coil** - Image loading library.
- **Coroutines** - Concurrency design pattern for asynchronous programming.
- **Zelory Image Compressor** - For compressing image before uploading to firebase storage.
- **Jetpack Compose** - Declarative and simplified way for UI development.
- **Material 3**: Apply Material 3 design principles and components to ensure app has a modern and cohesive look.
- **Firebase Libraries** - Firebase Messaging, Firebase Storage, Firebase Auth & Firestore.

## DEMO

1. **Apk file**: [Chatting App](https://github.com/Abhidhimann/ChattingApp/blob/chattingAppFirebase/app/release/app-release.apk.zip)

## Prerequisites

To build the project, ensure you do the following:

1. **Add Firebase to App**:
    - Create a project in the Firebase Console and add your Android app to the project, use [this link](https://firebase.google.com/docs/android/setup)

1. **Add google-services.json to your Firebase Project**:
    - Download the `google-services.json` file provided by firebase and place it in your app's `/app` directory.

2. **Add `WEB_CLIENT_ID` to `project.properties`**:
    - Open (or create) the `project.properties` file in the root directory of your project.
    - Add your Web Client ID this way:
      ```properties
      WEB_CLIENT_ID=your-web-client-id
      ```

For further assistance, refer to the official documentation or open an issue.
