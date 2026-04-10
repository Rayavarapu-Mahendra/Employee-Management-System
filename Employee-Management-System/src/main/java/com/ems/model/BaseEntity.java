package com.ems.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@Column(updatable = false)
	private String createdBy;
	
	private LocalDateTime updatedAt;
	
	private String updatedBy;
	
	@PrePersist
	protected void onCreate() {
		this.createdAt=LocalDateTime.now();
		this.updatedAt=LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt=LocalDateTime.now();
	}
}
