package com.animal.mypet.board;

import java.io.File;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.animal.mypet.DataNotFoundException;
import com.animal.mypet.comment.Comment;
import com.animal.mypet.comment.CommentForm;
import com.animal.mypet.comment.CommentService;
import com.animal.mypet.user.User;
import com.animal.mypet.user.UserService;
import com.nimbusds.jose.shaded.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RequestMapping("/board")
@Controller
public class BoardController {
	
	private final BoardService boardService;
	private final UserService userService;
	private final CommentService commentService;
	
	@GetMapping("/list")
	public String list(Model model, @RequestParam(value="page", defaultValue="0") int page, 
			@RequestParam(value = "kw", defaultValue = "") String kw) {
		try {
            Page<Board> paging = this.boardService.getList(page, kw);
            List<Board> topBoards = this.boardService.getTopBoards();
            model.addAttribute("paging", paging);
            model.addAttribute("topBoards", topBoards);
            model.addAttribute("kw", kw);
        } catch (DataNotFoundException e) {
            model.addAttribute("message", "게시글이 없습니다.");
        }
        return "board/list";
    }
	
	@GetMapping(value = "/detail/{idx}")
    public String detail(Model model, @PathVariable("idx") Integer idx, Principal principal, CommentForm commentForm, @RequestParam(value="page", defaultValue = "0") int page) {
		Board board = this.boardService.getBoard(idx);
		Page<Comment> paging = this.commentService.getList(board ,page);
        model.addAttribute("paging", paging);
        model.addAttribute("board", board);
        if(principal != null) {
        	model.addAttribute("isAuthenticated", true);
        	model.addAttribute("username", principal.getName());
        } else {
        	model.addAttribute("isAuthenticated", false);
        }
        return "board/detail";
    }
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/write")
    public String boardWrite(BoardForm boardForm) {
        return "board/form";
    }
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/write")
    public String boardWrite(@Valid BoardForm boardForm, BindingResult bindingResult, Principal principal) {
		
		if (bindingResult.hasErrors()) {
            return "board/form";
        }
		User user = this.userService.getUser(principal.getName());
        this.boardService.write(boardForm.getTitle(), boardForm.getContent(), user);

        return "redirect:/board/list";
    }
	
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{idx}")
    public String boardModify(BoardForm boardForm, @PathVariable("idx") Integer idx, Principal principal) {
		Board board = this.boardService.getBoard(idx);
        if(!board.getAuthor().getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        boardForm.setTitle(board.getTitle());
        boardForm.setContent(board.getContent());
        return "board/form";
        
    }
	
	@PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{idx}")
    public String boardModify(@Valid BoardForm boardForm, BindingResult bindingResult, Principal principal, @PathVariable("idx") Integer idx) {
        if (bindingResult.hasErrors()) {
            return "board/form";
        }
        Board board = this.boardService.getBoard(idx);
        if (!board.getAuthor().getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.boardService.modify(board, boardForm.getTitle(), boardForm.getContent());
        return String.format("redirect:/board/detail/%s", idx);
    }
	
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{idx}")
    public String boardDelete(Principal principal, @PathVariable("idx") Integer idx) {
		Board board = this.boardService.getBoard(idx);
        if (!board.getAuthor().getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.boardService.delete(board);
        return "redirect:/board/list";
    }
	
	@PreAuthorize("isAuthenticated()")
    @PostMapping("/vote/{idx}")
    public ResponseEntity<Integer> voteBoard(@PathVariable("idx") Integer idx, Principal principal) {
		Board board = boardService.getBoard(idx);
        User user = userService.getUser(principal.getName());

        boardService.vote(board, user);

        int voteCount = board.getVoter().size();
        return ResponseEntity.ok().body(voteCount);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/vote/{idx}")
    public ResponseEntity<Integer> cancelVoteBoard(@PathVariable("idx") Integer idx, Principal principal) {
        Board board = boardService.getBoard(idx);
        User user = userService.getUser(principal.getName());

        boardService.cancelVote(board, user);

        int voteCount = board.getVoter().size();
        return ResponseEntity.ok().body(voteCount);
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/status/{idx}")
    public ResponseEntity<Map<String, Object>> getVoteStatus(@PathVariable("idx") Integer idx, Principal principal) {
    	 Board board = boardService.getBoard(idx);
         User user = userService.getUser(principal.getName());

        boolean isActive = board.getVoter().contains(user);
        int voteCount = board.getVoter().size();

        Map<String, Object> response = new HashMap<>();
        response.put("isActive", isActive);
        response.put("count", voteCount);

        return ResponseEntity.ok(response);
    }
	

	
}