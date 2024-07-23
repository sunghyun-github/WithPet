package com.animal.mypet.comment;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.animal.mypet.board.Board;
import com.animal.mypet.board.BoardService;
import com.animal.mypet.user.User;
import com.animal.mypet.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/comment")
@Controller
public class CommentController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final UserService userService;

  
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create/{idx}")
    public String createComment(CommentForm commentForm) {
        return "comment/form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{idx}")
    public String createComment(Model model, @PathVariable("idx") Integer idx, @Valid CommentForm commentForm,
            BindingResult bindingResult, Principal principal) {
        Board board = this.boardService.getBoard(idx);
        User user = this.userService.getUser(principal.getName());
        
        if (bindingResult.hasErrors()) {
            return "comment/form";
        }
        if (commentForm.getContent().trim().isEmpty()) {
            model.addAttribute("commentError", "내용을 입력하세요.");
            return "comment/form";
        }
        Comment comment = this.commentService.create(board, commentForm.getContent(), user);
        return String.format("redirect:/board/detail/%s#comment_%s", 
        		comment.getBoard().getIdx(), comment.getIdx());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{idx}")
    public ResponseEntity<Comment> modifyCommentForm(@PathVariable("idx") Integer idx, Principal principal) {
        Comment comment = commentService.getComment(idx);
        if (!comment.getAuthor().getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        return ResponseEntity.ok(comment);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{idx}")
    public ResponseEntity<?> modifyComment(@Valid @RequestBody CommentForm commentForm, Principal principal,
                                           @PathVariable("idx") Integer idx) {
        Comment comment = commentService.getComment(idx);
        if (!comment.getAuthor().getUserId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정권한이 없습니다.");
        }
        commentService.modify(comment, commentForm.getContent());
        return ResponseEntity.ok().body("댓글이 수정되었습니다.");
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{idx}")
    @ResponseBody
    public String deleteComment(@PathVariable("idx") Integer idx, Principal principal) {
        Comment comment = this.commentService.getComment(idx);
        if (!comment.getAuthor().getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return "success";
    }

    
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote/{idx}")
    public ResponseEntity<Integer> voteComment(@PathVariable("idx") Integer idx, Principal principal) {
        Comment comment = commentService.getComment(idx);
        User user = userService.getUser(principal.getName());

        commentService.vote(comment, user);

        int voteCount = comment.getVoter().size();
        return ResponseEntity.ok().body(voteCount);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/vote/{idx}")
    public ResponseEntity<Integer> cancelVoteComment(@PathVariable("idx") Integer idx, Principal principal) {
        Comment comment = commentService.getComment(idx);
        User user = userService.getUser(principal.getName());

        commentService.cancelVote(comment, user);

        int voteCount = comment.getVoter().size();
        return ResponseEntity.ok().body(voteCount);
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/status/{idx}")
    public ResponseEntity<Map<String, Object>> getVoteStatus(@PathVariable("idx") Integer idx, Principal principal) {
        Comment comment = commentService.getComment(idx);
        User user = userService.getUser(principal.getName());

        boolean isActive = comment.getVoter().contains(user);
        int voteCount = comment.getVoter().size();

        Map<String, Object> response = new HashMap<>();
        response.put("isActive", isActive);
        response.put("count", voteCount);

        return ResponseEntity.ok(response);
    }
    
}