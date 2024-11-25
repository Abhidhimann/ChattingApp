# Chatting App by Abhishek

This Android application allows users to discover and connect with others, view profiles, and engage in real-time one-on-one messaging.

## Features

- **Sign in/up page**: Log in/register using email and password or ****Google Single Sign-On (SSO)****.
- **AI Chatbot**: Access a built-in AI-powered chatbot for general queries or assistance while chatting.
- **Conversation Summarization**: Generate concise summaries of past chats to quickly review key points.
- **Profile Page**: View and update your profile, including uploading a profile picture.
- **Discover Users Page**: Explore and find other users within the app and send connection requests.
- **Requests Page**: Manage incoming connection requests by accepting or rejecting them.
- **Chat list page**: See previous/new chats.
- **Chat Page**: Engage in real-time, one-on-one messaging with other users.
- **Notification**: Receive new messages alert through push notifications via ****Firebase Cloud Messaging (FCM)****.

## Tech Stacks

Common tech stacks used are:

- **Architecture** - MVI + Clean Architecture.
- **Retrofit + OkHttp** - RESTful API and networking client.
- **Hilt** - For dependency injection.
- **Coil** - Image loading library.
- **Coroutines** - Concurrency design pattern for asynchronous programming.
- **Zelory Image Compressor** - For compressing image before uploading to firebase storage.
- **Jetpack Compose** - Declarative and simplified way for UI development.
- **Material 3**: Apply Material 3 design principles and components to ensure app has a modern and cohesive look.
- **Firebase Libraries** - Firebase Messaging, Firebase Storage, Remote Config, Firebase Auth & Firestore.

## DEMO

https://github.com/user-attachments/assets/e878306e-249b-4362-86dc-1bd59e71c8da

https://github.com/user-attachments/assets/7492e308-982e-4703-b5df-ef0320c1f519

https://github.com/user-attachments/assets/b8e83d7d-c93a-4ca5-8728-f63962e80be1

https://github.com/user-attachments/assets/5177304c-5052-4aff-895e-19bf5dc79738

https://github.com/user-attachments/assets/463ea2ed-6b2d-4b15-adb0-b4f3454a06bb

https://github.com/user-attachments/assets/49df4135-9692-44b4-8fe8-6f0345fe55df

https://github.com/user-attachments/assets/bfc32dde-2156-43c4-b9f9-09e0aa12cd20


**Apk file**: [Chatting App](https://github.com/Abhidhimann/ChattingApp/blob/chattingAppFirebase/app/release/app-release.apk.zip)

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
**Note**: For FCM, instead of using Cloud Functions, this application utilizes a temporary Node.js server. Please refer to the [server details](https://github.com/Abhidhimann/ChattingApp/blob/chattingAppFirebase/app/src/main/assets/serverdetails). Note that notifications will only function while this server is running. We will explore alternative solutions in the future.

For further assistance, refer to the official documentation or open an issue.

## Suggestions and Feedback

If you have any suggestions or feedback regarding the project, please feel free to reach out to me:

- **Email**: [abhishekdhimaniitg@gmail.com](mailto:abhishekdhimaniitg@gmail.com)

Your input is greatly appreciated and helps improve the project!
