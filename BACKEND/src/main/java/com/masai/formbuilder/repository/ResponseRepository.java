package com.masai.formbuilder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masai.formbuilder.model.Form;
import com.masai.formbuilder.model.Response;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByFormId(Long formId);
    
    boolean existsByFormIdAndIpAddress(Long formId, String ipAddress);
    
    boolean existsByForm(Form form);
    
    long countByForm(Form form);
}