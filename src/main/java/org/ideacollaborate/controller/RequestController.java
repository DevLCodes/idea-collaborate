package org.ideacollaborate.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.ideacollaborate.entity.Employee;
import org.ideacollaborate.entity.Idea;
import org.ideacollaborate.service.IdeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/idea")
public class RequestController {

    @Autowired
    private IdeaService ideaService;

    @GetMapping("/list")
    public ResponseEntity<List<Idea>> getIdeaList(@RequestParam @Nullable String sortBy,
                                                  @RequestParam @Nullable String direction) {
        return ResponseEntity.ok(ideaService.getIdeaList(sortBy, direction));
    }

    @PostMapping("/add")
    public ResponseEntity<Idea> addIdea(@Valid @RequestBody Idea idea,
                                        @RequestParam Long userId) {
        return ResponseEntity.ok(ideaService.addIdea(idea, userId));
    }

    @PostMapping("/{ideaId}/vote")
    public ResponseEntity<String> voteIdea(@RequestParam boolean upvote,
                                           @PathVariable Long ideaId,
                                           @RequestParam Long voterId) {
        ideaService.voteIdea(upvote, ideaId, voterId);
        return ResponseEntity.ok("Vote has been cast on the idea");

    }
    @PostMapping("/{ideaId}/expressInterest")
    public ResponseEntity<String> expressInterest(@PathVariable Long ideaId,
                                                  @RequestParam Long userId) {
        ideaService.expressCollaborationInterest(ideaId, userId);
        return ResponseEntity.ok("Express interest has been cast on the idea");
    }

    @GetMapping("/{ideaId}/interestCollaborators")
    public ResponseEntity<List<Employee>> getInterestCollaborators(@PathVariable Long ideaId,
                                                                   @RequestParam Long userId) {
        return ResponseEntity.ok(ideaService.getInterestedCollaborators(ideaId, userId));
    }
}
