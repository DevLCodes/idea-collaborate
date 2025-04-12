package org.ideacollaborate.service;

import jakarta.persistence.EntityNotFoundException;
import org.ideacollaborate.entity.Idea;
import org.ideacollaborate.entity.IdeaDTO;
import org.ideacollaborate.entity.User;
import org.ideacollaborate.entity.UserDTO;
import org.ideacollaborate.repository.IdeaRepository;
import org.ideacollaborate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;


    public IdeaDTO addIdea(final IdeaDTO ideaDTO,
                        final Long userID) {

        User user = userRepository.findById(userID)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userID));
        Idea idea = ideaDTO.toIdea();
        idea.setOwner(user);
        Idea savedIdea = ideaRepository.save(idea);

        return IdeaDTO.fromIdea(savedIdea);
    }

    public Idea voteIdea(final boolean upvote, final Long ideaId, final Long voterId) {

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found with id: " + ideaId));

        User user = userRepository.findById(voterId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if(idea.getOwner().getId().equals(voterId)) {
            throw  new EntityNotFoundException("Owner of the idea cannot vote");
        }
        if(upvote) {
            idea.setUpvotes(idea.getUpvotes() + 1);
        }else {
            idea.setDownvotes(idea.getDownvotes() - 1);
        }
        return ideaRepository.save(idea);
    }

    public List<Idea> getIdeaList(final String sortBy, final String direction) {

        if(sortBy !=null && direction !=null){
            Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
          return ideaRepository.findAll(sort).stream().toList();
        }
        return ideaRepository.findAll();
    }

    public void expressCollaborationInterest(Long ideaId, Long userId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<User> interestedCollaborators = idea.getInterestedCollaborators();
        interestedCollaborators.add(user);
        idea.setInterestedCollaborators(interestedCollaborators);

        ideaRepository.save(idea);
    }

    public List<UserDTO> getInterestedCollaborators(Long ideaId, Long userId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return idea.getInterestedCollaborators().stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());


    }
}
