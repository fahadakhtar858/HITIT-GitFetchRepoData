package com.hitit.gitapi.models;

import jakarta.persistence.*;

@Entity
@Table(name = "contributors")

public class ContributorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="username")
    private String username;
    @Column(name="location")
    private String location;
    @Column(name="company")
    private String company;

    @Column(name = "contributions")
    private int contributions;
    //private String repoName;

    public ContributorEntity() {

    }
    @ManyToOne
    @JoinColumn(name = "repository_id")
    private RepositoryEntity repository;
    public ContributorEntity(String username, String location, String company, int contributions, RepositoryEntity repo) {
        this.username = username;
        this.location = location;
        this.company = company;
        this.contributions = contributions;
        this.repository = repo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public RepositoryEntity getRepository() {
        return repository;
    }

    public void setRepository(RepositoryEntity repository) {
        this.repository = repository;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getContributions() {
        return contributions;
    }

    public void setContributions(int contributions) {
        this.contributions = contributions;
    }
}
