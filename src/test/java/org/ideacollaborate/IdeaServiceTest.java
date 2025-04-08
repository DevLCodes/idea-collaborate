package org.ideacollaborate;

import jakarta.persistence.EntityNotFoundException;
import org.ideacollaborate.entity.Idea;
import org.ideacollaborate.entity.Employee;
import org.ideacollaborate.repository.EmployeeRepository;
import org.ideacollaborate.repository.IdeaRepository;
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
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private IdeaService ideaService;

    final Employee testEmployee = new Employee(1L,"employee1");
    final Idea testIdea = new Idea();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        testIdea.setId(1L);
        testIdea.setTitle("title1");
        testIdea.setDescription("description1");
        testIdea.setOwner(testEmployee);
        testIdea.setUpvotes(0);
        testIdea.setDownvotes(0);
        testIdea.setInterestedCollaborators(new ArrayList<>());

    }

    @Test
    void validateAddIdeaWithOwner() {

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(ideaRepository.save(any(Idea.class))).thenReturn(testIdea);

        Idea result = ideaService.addIdea(testIdea, 1L);

        assertNotNull(result);
        assertEquals(testEmployee, result.getOwner());
        verify(employeeRepository, times(1)).findById(1L);
        verify(ideaRepository, times(1)).save(testIdea);
    }


    @Test
    void validateUpVote() {
        Idea ideaWithVotes = testIdea;
        ideaWithVotes.setUpvotes(5);

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(ideaWithVotes));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(new Employee(2L, "voter")));
        when(ideaRepository.save(any(Idea.class))).thenReturn(ideaWithVotes);

        Idea result = ideaService.voteIdea(true, 1L, 2L);

        assertEquals(6, result.getUpvotes());
        assertEquals(0, result.getDownvotes());
    }


    @Test
    void validateOwnerVoting() {

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(testIdea));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));


        assertThrows(EntityNotFoundException.class, () -> {
            ideaService.voteIdea(true, 1L, 1L);
        });
    }


    @Test
    void validateExpressCollaboratorsInterest() {

        Employee newCollaborator = new Employee(2L, "collaborator");
        Idea ideaWithCollaborators = testIdea;

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(ideaWithCollaborators));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(newCollaborator));
        when(ideaRepository.save(any(Idea.class))).thenReturn(ideaWithCollaborators);

        ideaService.expressCollaborationInterest(1L, 2L);

        assertTrue(ideaWithCollaborators.getInterestedCollaborators().contains(newCollaborator));
        verify(ideaRepository, times(1)).save(ideaWithCollaborators);
    }

    @Test
    void validateGetInterestedCollaborators() {

        Employee collaborator = new Employee(2L, "collaborator");
        Idea ideaWithCollaborators = testIdea;
        ideaWithCollaborators.getInterestedCollaborators().add(collaborator);

        when(ideaRepository.findById(1L)).thenReturn(Optional.of(ideaWithCollaborators));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

        List<Employee> result = ideaService.getInterestedCollaborators(1L, 1L);

        assertEquals(1, result.size());
        assertEquals(collaborator, result.get(0));
    }

}
