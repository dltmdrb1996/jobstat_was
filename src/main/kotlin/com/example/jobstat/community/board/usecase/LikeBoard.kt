package com.example.jobstat.community.board.usecase

import com.example.jobstat.community.board.service.BoardService
import com.example.jobstat.core.usecase.impl.ValidUseCase
import jakarta.transaction.Transactional
import jakarta.validation.Validator
import jakarta.validation.constraints.Positive
import org.springframework.stereotype.Service

@Service
internal class LikeBoard(
    private val boardService: BoardService,
    validator: Validator,
) : ValidUseCase<LikeBoard.Request, LikeBoard.Response>(validator) {
    @Transactional
    override fun execute(request: Request): Response {
        val board = boardService.incrementLikeCount(request.boardId)
        return Response(likeCount = board.likeCount)
    }

    data class Request(
        @field:Positive val boardId: Long,
    )

    data class Response(
        val likeCount: Int,
    )
}
