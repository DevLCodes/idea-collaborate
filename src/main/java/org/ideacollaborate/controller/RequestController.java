package org.ideacollaborate.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.ideacollaborate.entity.IdeaDTO;
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
    public ResponseEntity<?> getIdeaList(@RequestParam @Nullable String sortBy,
                                                  @RequestParam @Nullable String direction) {
        try{
            return ResponseEntity.ok(ideaService.getIdeaList(sortBy, direction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Request Failed : "+ e.getMessage());
        }

    }

    @PostMapping("/add")
    public ResponseEntity<?> addIdea(@Valid @RequestBody IdeaDTO ideaDTO,
                                            @RequestParam Long userId) {
        try{
            return ResponseEntity.ok(ideaService.addIdea(ideaDTO, userId));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Request Failed : "+ e.getMessage());
        }
    }

    @PostMapping("/{ideaId}/vote")
    public ResponseEntity<String> voteIdea(@RequestParam boolean upvote,
                                           @PathVariable Long ideaId,
                                           @RequestParam Long voterId) {
        try{
            ideaService.voteIdea(upvote, ideaId, voterId);
            return ResponseEntity.ok("Vote successful");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Vote failed : " + e.getMessage());
        }

    }
    @PostMapping("/{ideaId}/expressInterest")
    public ResponseEntity<String> expressInterest(@PathVariable Long ideaId,
                                                  @RequestParam Long userId) {
        try{
            ideaService.expressCollaborationInterest(ideaId, userId);
            return ResponseEntity.ok("Express interest has been cast on the idea");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Express interest failed : " + e.getMessage());
        }


    }

    @GetMapping("/{ideaId}/interestCollaborators")
    public ResponseEntity<?> getInterestCollaborators(@PathVariable Long ideaId,
                                                                  @RequestParam Long userId) {
        try{
            return ResponseEntity.ok(ideaService.getInterestedCollaborators(ideaId, userId));
        }catch(Exception e) {
            return ResponseEntity.badRequest().body("Request Failed : "+ e.getMessage());
        }

    }
}
