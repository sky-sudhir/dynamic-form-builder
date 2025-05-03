package com.masai.formbuilder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masai.formbuilder.model.Field;

public interface FieldRepository extends JpaRepository<Field, Long> {
	Field findByFieldId(String fieldId);
}
