package org.ideacollaborate;

import jakarta.persistence.EntityNotFoundException;
import org.ideacollaborate.entity.Idea;
import org.ideacollaborate.entity.IdeaDTO;
import org.ideacollaborate.entity.User;
import org.ideacollaborate.entity.UserDTO;
import org.ideacollaborate.repository.IdeaRepository;
import org.ideacollaborate.repository.UserRepository;
import org.ideacollaborate.service.IdeaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdeaServiceTest {

    @Mock
    private IdeaRepository ideaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IdeaService ideaService;

    private User testUserEntity;
    private UserDTO testUserDTO;
    private Idea testIdeaEntity;
    private IdeaDTO testIdeaDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // Create test user entity
        testUserEntity = User.builder()
                .id(1L)
                .userName("testUser")
                .password("password")
                .build();

        // Create corresponding DTO
        testUserDTO = UserDTO.fromUser(testUserEntity);

        // Create test idea entity
        testIdeaEntity = new Idea();
        testIdeaEntity.setId(1L);
        testIdeaEntity.setTitle("title1");
        testIdeaEntity.setDescription("description1");
        testIdeaEntity.setOwner(testUserEntity);
        testIdeaEntity.setUpvotes(0);
        testIdeaEntity.setDownvotes(0);
        testIdeaEntity.setInterestedCollaborators(new ArrayList<>());

        // Create corresponding DTO
        testIdeaDTO = IdeaDTO.fromIdea(testIdeaEntity);
    }

    @Test
    void validateAddIdeaWithOwner() {
        // Prepare input DTO without owner
        IdeaDTO inputIdeaDTO = new IdeaDTO();
        inputIdeaDTO.setTitle("title1");
        inputIdeaDTO.setDescription("description1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserEntity));
        when(ideaRepository.save(any(Idea.class))).thenReturn(testIdeaEntity);

        IdeaDTO resultDTO = ideaService.addIdea(inputIdeaDTO, 1L);

        assertNotNull(resultDTO);
        assertEquals(testUserDTO.getId(), resultDTO.getOwner().getId());
        assertEquals(testUserDTO.getUserName(), resultDTO.getOwner().getUserName());
        verify(userRepository, times(1)).findById(1L);
        verify(ideaRepository, times(1)).save(any(Idea.class));
    }

    @Test
    void validateUpVote() {
        // Create test idea with votes
        Idea ideaWithVotes = new Idea();
        ideaWithVotes.setId(1L);
        ideaWithVotes.setTitle("title1");
        ideaWithVotes.setDescription("description1");
        ideaWithVotes.setOwner(testUserEntity);
        ideaWithVotes.setUpvotes(5);
        ideaWithVotes.setDownvotes(0);
        ideaWithVotes.setInterestedCollaborators(new ArrayList<>());

        // Create collaborator
        User collaborator = User.builder()
                .id(2L)
                .userName("collaborator")
                .password("pass")
                .build();

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(ideaWithVotes));
        when(userRepository.findById(2L)).thenReturn(Optional.of(collaborator));

        // After upvote
        Idea updatedIdea = new Idea();
        updatedIdea.setId(1L);
        updatedIdea.setTitle("title1");
        updatedIdea.setDescription("description1");
        updatedIdea.setOwner(testUserEntity);
        updatedIdea.setUpvotes(6);  // Increased by 1
        updatedIdea.setDownvotes(0);
        updatedIdea.setInterestedCollaborators(new ArrayList<>());

        when(ideaRepository.save(any(Idea.class))).thenReturn(updatedIdea);

        IdeaDTO result = IdeaDTO.fromIdea(ideaService.voteIdea(true, 1L, 2L));

        assertEquals(6, result.getUpvotes());
        assertEquals(0, result.getDownvotes());
    }

    @Test
    void validateDownVote() {
        // Create test idea with votes
        Idea ideaWithVotes = new Idea();
        ideaWithVotes.setId(1L);
        ideaWithVotes.setTitle("title1");
        ideaWithVotes.setDescription("description1");
        ideaWithVotes.setOwner(testUserEntity);
        ideaWithVotes.setUpvotes(5);
        ideaWithVotes.setDownvotes(2);
        ideaWithVotes.setInterestedCollaborators(new ArrayList<>());

        // Create collaborator
        User collaborator = User.builder()
                .id(2L)
                .userName("collaborator")
                .password("pass")
                .build();

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(ideaWithVotes));
        when(userRepository.findById(2L)).thenReturn(Optional.of(collaborator));

        // After downvote
        Idea updatedIdea = new Idea();
        updatedIdea.setId(1L);
        updatedIdea.setTitle("title1");
        updatedIdea.setDescription("description1");
        updatedIdea.setOwner(testUserEntity);
        updatedIdea.setUpvotes(5);
        updatedIdea.setDownvotes(1);  // Decreased by 1
        updatedIdea.setInterestedCollaborators(new ArrayList<>());

        when(ideaRepository.save(any(Idea.class))).thenReturn(updatedIdea);

        IdeaDTO result = IdeaDTO.fromIdea(ideaService.voteIdea(false, 1L, 2L));

        assertEquals(5, result.getUpvotes());
        assertEquals(1, result.getDownvotes());
    }

    @Test
    void validateOwnerVoting() {
        when(ideaRepository.findById(1L)).thenReturn(Optional.of(testIdeaEntity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserEntity));

        assertThrows(EntityNotFoundException.class, () -> {
            ideaService.voteIdea(true, 1L, 1L);
        });
    }

    @Test
    void validateExpressCollaboratorsInterest() {
        // Create collaborator
        User collaborator = User.builder()
                .id(2L)
                .userName("collaborator")
                .password("pass")
                .build();

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(testIdeaEntity));
        when(userRepository.findById(2L)).thenReturn(Optional.of(collaborator));

        // Updated idea with collaborator
        Idea updatedIdea = new Idea();
        updatedIdea.setId(1L);
        updatedIdea.setTitle("title1");
        updatedIdea.setDescription("description1");
        updatedIdea.setOwner(testUserEntity);
        updatedIdea.setUpvotes(0);
        updatedIdea.setDownvotes(0);
        List<User> collaborators = new ArrayList<>();
        collaborators.add(collaborator);
        updatedIdea.setInterestedCollaborators(collaborators);

        when(ideaRepository.save(any(Idea.class))).thenReturn(updatedIdea);

        ideaService.expressCollaborationInterest(1L, 2L);

        verify(ideaRepository, times(1)).save(any(Idea.class));
    }

    @Test
    void validateGetInterestedCollaborators() {
        User collaborator = User.builder()
                .id(2L)
                .userName("collaborator")
                .password("pass")
                .build();

        List<User> collaborators = new ArrayList<>();
        collaborators.add(collaborator);
        testIdeaEntity.setInterestedCollaborators(collaborators);

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(testIdeaEntity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserEntity));

        List<UserDTO> result = ideaService.getInterestedCollaborators(1L, 1L);

        assertEquals(1, result.size());
        assertEquals(collaborator.getId(), result.get(0).getId());
        assertEquals(collaborator.getUserName(), result.get(0).getUserName());

    }
}