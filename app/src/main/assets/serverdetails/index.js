const express = require("express");
const admin = require("firebase-admin");
const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT_KEY);
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();
const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

app.post("/sendNotification", async (req, res) => {
  const { chatRoomId, chatRoomTitle, messageId, senderId, textContent } =
    req.body;

  try {
    const chatRef = db.collection("singleChat").doc(chatRoomId);
    const chatDoc = await chatRef.get();

    if (!chatDoc.exists) {
      return res.status(404).send("Chat room not found");
    }

    const chatData = chatDoc.data();
    const participantIds = chatData.participantIds;

    const notifications = await Promise.all(
      participantIds.map(async (participantId) => {
        if (participantId === senderId) {
          // Skip sending notification to the sender
          return null;
        }

        const userRef = db.collection("users_details").doc(participantId);
        const userDoc = await userRef.get();

        if (!userDoc.exists) {
          console.log(`User not found: ${participantId}`);
          return null;
        }

        const userData = userDoc.data();
        const token = userData.token;

        if (userData.current_chat_room === chatRoomId) {
          // Skip if user is inside the chat room
          return null;
        }

        if (!token) {
          console.log(`User does not have a valid FCM token: ${participantId}`);
          return null;
        }

        const message = {
          data: {
            pnsType: "message",
            body: JSON.stringify({
              chatRoomId: chatRoomId,
              chatRoomTitle: chatRoomTitle,
              messageId: messageId,
              senderId: senderId,
              textContent: textContent,
            }),
          },
          token: token,
        };

        try {
          const response = await admin.messaging().send(message);
          console.log(
            `Notification sent successfully to ${participantId}: ${response}`,
          );
          return response;
        } catch (error) {
          console.error(
            `Error sending notification to ${participantId}:`,
            error,
          );
          return null;
        }
      }),
    );

    res
      .status(200)
      .send(
        `Notifications sent: ${notifications.filter((result) => result !== null).length}`,
      );
  } catch (error) {
    console.error("Error sending notifications:", error);
    res.status(500).send("Error sending notifications");
  }
});

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
