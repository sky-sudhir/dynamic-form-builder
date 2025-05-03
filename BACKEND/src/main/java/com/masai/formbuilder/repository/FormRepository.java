package com.masai.formbuilder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masai.formbuilder.enums.FormStatus;
import com.masai.formbuilder.model.Form;
import com.masai.formbuilder.model.User;

public interface FormRepository extends JpaRepository<Form, Long> {
    Optional<Form> findByIdAndStatus(Long id, FormStatus status);
    boolean existsByTitleIgnoreCaseAndCreator(String title, User creator);
    
	Optional<Form> findByFormUid(String formUid);
	
	List<Form> findAllByCreator(User user);
}

