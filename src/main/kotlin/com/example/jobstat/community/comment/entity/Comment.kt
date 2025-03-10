package com.example.jobstat.community.comment.entity

import com.example.jobstat.community.board.entity.Board
import com.example.jobstat.community.board.entity.ReadBoard
import com.example.jobstat.community.comment.CommentConstants
import com.example.jobstat.core.base.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

interface ReadComment {
    val id: Long
    val content: String
    val author: String
    val password: String?
    val userId: Long?
    val board: ReadBoard
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
}

@Entity
@Table(name = "comments")
internal class Comment protected constructor(
    content: String,
    author: String,
    password: String?,
    board: Board,
    userId: Long?,
) : BaseEntity(),
    ReadComment {
    @Column(nullable = false, length = CommentConstants.MAX_CONTENT_LENGTH)
    override var content: String = content
        protected set

    @Column(nullable = false, length = CommentConstants.MAX_AUTHOR_LENGTH)
    override var author: String = author
        protected set

    @Column(nullable = true)
    override var userId: Long? = userId
        protected set

    @Column(length = CommentConstants.ENCODED_PASSWORD_LENGTH)
    override var password: String? = password
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    override var board: Board = board
        protected set

    fun updateContent(newContent: String) {
        require(newContent.isNotBlank() && newContent.length <= CommentConstants.MAX_CONTENT_LENGTH) {
            CommentConstants.ErrorMessages.INVALID_CONTENT
        }
        this.content = newContent
    }

    companion object {
        fun create(
            content: String,
            author: String,
            password: String?,
            board: Board,
            userId: Long?,
        ): Comment {
            require(content.isNotBlank()) { CommentConstants.ErrorMessages.CONTENT_REQUIRED }
            require(content.length <= CommentConstants.MAX_CONTENT_LENGTH) {
                CommentConstants.ErrorMessages.INVALID_CONTENT
            }
            require(author.isNotBlank()) { CommentConstants.ErrorMessages.AUTHOR_REQUIRED }
            return Comment(content, author, password, board, userId)
        }
    }
}
