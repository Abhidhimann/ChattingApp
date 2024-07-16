package com.example.chattingApp.data.remote.dto

import com.example.chattingApp.domain.model.Conversation
import com.example.chattingApp.domain.model.UserSummary
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date


class SingleChatDto(
    var chatId: String = "",
    @ServerTimestamp
    var updatedAt: Date? = null,
    var originator: UserSummaryDto? = null,
    var recipient: UserSummaryDto? = null,
    var participantIds: List<String> = emptyList(),
    var unreadMessageCount: Int = 0
    // todo for now not added lastMessage on chat, will change on db side, when db is clear that
// won't work ( but ok for now)
) {
    fun toConversation(selfUser: UserSummary): Conversation? {
        if (originator == null || recipient == null) {
            return null
        }
        val lastMessage = if (selfUser.userId == originator!!.userId) {
            "${recipient!!.name} accepted your request"
        } else {
            "You accepted ${originator!!.name} request"
        }
        return Conversation(
            conversationId = chatId,
            title = if (selfUser.userId == originator!!.userId) recipient!!.name else originator!!.name,
            iconUri = if (selfUser.userId == originator!!.userId) recipient!!.profileImageUrl else originator!!.profileImageUrl,
            unReadMessageCount = unreadMessageCount,
            lastMessage = lastMessage,
            updateAt = updatedAt?.toInstant(),
            participantsIds = participantIds,
            currentUserId = selfUser.userId
        )
    }
}

