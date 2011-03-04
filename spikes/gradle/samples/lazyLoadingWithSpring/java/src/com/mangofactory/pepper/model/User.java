package com.mangofactory.pepper.model;
// default package
// Generated 26-Jun-2010 11:51:56 by Hibernate Tools 3.2.4.GA

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * User generated by hbm2java
 */
@Entity
@Table(name = "users")
public class User extends  BaseEntity {

	@Column(length=65565)
	private String aboutMe;
	private Integer age;
	private Date creationDate;
	private String displayName;
	private int downVotes;
	private String emailHash;
	private Date lastAccessDate;
	private String location;
	private int reputation;
	private int upVotes;
	private int views;
	private String websiteUrl;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", targetEntity=Badge.class)
	private Set<Badge> badges = new HashSet<Badge>(0);
	@OneToMany(mappedBy="author")
	private Set<Post> posts = new HashSet<Post>(0);
	@OneToMany(mappedBy="user")
	private Set<Comment> comments = new HashSet<Comment>(0);
	@OneToMany(mappedBy="user")
	private Set<Vote> votes = new HashSet<Vote>(0);

	public User() {
	}

	
	public String getAboutMe() {
		return this.aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public Integer getAge() {
		return this.age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getDownVotes() {
		return this.downVotes;
	}

	public void setDownVotes(int downVotes) {
		this.downVotes = downVotes;
	}

	public String getEmailHash() {
		return this.emailHash;
	}

	public void setEmailHash(String emailHash) {
		this.emailHash = emailHash;
	}

	public Date getLastAccessDate() {
		return this.lastAccessDate;
	}

	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getReputation() {
		return this.reputation;
	}

	public void setReputation(int reputation) {
		this.reputation = reputation;
	}

	public int getUpVotes() {
		return this.upVotes;
	}

	public void setUpVotes(int upVotes) {
		this.upVotes = upVotes;
	}

	public int getViews() {
		return this.views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public String getWebsiteUrl() {
		return this.websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public Set<Badge> getBadges() {
		return this.badges;
	}

	public void setBadges(Set<Badge> badges) {
		this.badges = badges;
	}

	public Set<Post> getPosts() {
		return this.posts;
	}

	public void setPosts(Set<Post> posts) {
		this.posts = posts;
	}

	public Set<Comment> getComments() {
		return this.comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public Set<Vote> getVotes() {
		return this.votes;
	}

	public void setVotes(Set<Vote> votes) {
		this.votes = votes;
	}
}
