package org.ideacollaborate.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class IdeaDTO {
    private Long id;
    private String title;
    private String description;
    private List<String> tags;
    private UserDTO owner;
    private Date createdDate;
    private int upvotes;
    private int downvotes;
    private List<UserDTO> interestedCollaborators;

    public static IdeaDTO fromIdea(Idea idea) {
        IdeaDTO dto = new IdeaDTO();
        dto.setId(idea.getId());
        dto.setTitle(idea.getTitle());
        dto.setDescription(idea.getDescription());
        if(idea.getTags() != null) {
            dto.setTags(idea.getTags());
        }
        dto.setOwner(UserDTO.fromUser(idea.getOwner()));
        dto.setCreatedDate(idea.getCreatedDate());
        dto.setUpvotes(idea.getUpvotes());
        dto.setDownvotes(idea.getDownvotes());

        if (idea.getOwner() != null) {
            dto.setOwner(UserDTO.fromUser(idea.getOwner()));
        }

        if (idea.getInterestedCollaborators() != null) {
            dto.setInterestedCollaborators(
                    idea.getInterestedCollaborators().stream()
                            .map(UserDTO::fromUser)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public Idea toIdea() {
        Idea idea = new Idea();
        idea.setId(this.id);
        idea.setTitle(this.title);
        idea.setDescription(this.description);
        idea.setTags(this.tags);
        idea.setCreatedDate(this.createdDate);
        idea.setUpvotes(this.upvotes);
        idea.setDownvotes(this.downvotes);
        return idea;
    }
}