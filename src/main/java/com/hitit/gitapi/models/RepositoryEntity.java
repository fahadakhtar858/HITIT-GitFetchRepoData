package com.hitit.gitapi.models;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="repositories")
public class RepositoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContributorEntity> contributors;


    public RepositoryEntity(String name) {
        this.name = name;

    }

    public RepositoryEntity() {
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public List<ContributorEntity> getContributors() {
        return contributors;
    }

    public void setContributors(List<ContributorEntity> contributors) {
        this.contributors = contributors;
    }

}
