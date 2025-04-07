package org.ideacollaborate.service;

import jakarta.persistence.EntityNotFoundException;
import org.ideacollaborate.entity.Employee;
import org.ideacollaborate.entity.Idea;
import org.ideacollaborate.repository.IdeaRepository;
import org.ideacollaborate.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    public Idea addIdea(final Idea idea,
                        final Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + employeeId));
        idea.setOwner(employee);
        return ideaRepository.save(idea);
    }

    public Idea voteIdea(final boolean upvote, final Long ideaId, final Long voterId) {

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found with id: " + ideaId));

        Employee employee = employeeRepository.findById(voterId)
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

    public void expressCollaborationInterest(Long ideaId, Long employeeId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        List<Employee> interestedCollaborators = idea.getInterestedCollaborators();
        interestedCollaborators.add(employee);
        idea.setInterestedCollaborators(interestedCollaborators);

        ideaRepository.save(idea);
    }

    public List<Employee> getInterestedCollaborators(Long ideaId, Long employeeId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        return idea.getInterestedCollaborators();


    }
}
