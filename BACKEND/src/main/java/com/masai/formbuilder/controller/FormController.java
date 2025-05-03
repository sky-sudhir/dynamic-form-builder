package com.masai.formbuilder.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.masai.formbuilder.dto.FormDto;
import com.masai.formbuilder.dto.MessageResponse;
import com.masai.formbuilder.enums.FormStatus;
import com.masai.formbuilder.exception.BadRequestException;
import com.masai.formbuilder.exception.InvalidCredentialsException;
import com.masai.formbuilder.exception.UnProcessableException;
import com.masai.formbuilder.model.Form;
import com.masai.formbuilder.repository.FormRepository;
import com.masai.formbuilder.repository.ResponseRepository;
import com.masai.formbuilder.service.FormService;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    @Autowired
    private FormService formService;
    
    @Autowired
    private FormRepository formRepository;
    
    @Autowired
    private ResponseRepository responseRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createForm(@RequestBody FormDto dto) throws BadRequestException {
        return ResponseEntity.ok(new MessageResponse("Form created successfully.", formService.createForm(dto)));
    }

    @GetMapping("/public/{formId}")
    public ResponseEntity<FormDto> getPublicForm(@PathVariable(required = false) Long formId) {
        return ResponseEntity.ok(formService.getPublishedForm(formId));
    }
    
    @GetMapping()
    public ResponseEntity<List<FormDto>> getAllForms() {
        return ResponseEntity.ok(formService.getAllForms());
    } 
    
    @GetMapping("/{formUid}")
    public ResponseEntity<FormDto> getFormByPublicUid(@PathVariable String formUid) {
    	Form form = formRepository.findByFormUid(formUid)
    	        .orElseThrow(() -> new BadRequestException("No Form Found!"));

    	if(form.getStatus() == FormStatus.CLOSED) {
    		throw new InvalidCredentialsException("This form is closed and no longer accepting responses");
    	}else if(form.getStatus() == FormStatus.SCHEDULED) {
    		if (LocalDateTime.now().isAfter(form.getScheduledTime())) {
    			return ResponseEntity.ok(new FormDto(form));
    		}
    		throw new UnProcessableException(form.getScheduledTime().toString());
    	}
    	return ResponseEntity.ok(new FormDto(form));
    }
    
    @GetMapping("/access/{formUid}")
    public ResponseEntity<FormDto> getFormBehaviour(@PathVariable String formUid) {
    	Form form = formRepository.findByFormUid(formUid)
    	        .orElseThrow(() -> new BadRequestException("No Form Found!"));
    	
    	FormDto formDto = new FormDto();

    	if(form.getStatus() == FormStatus.CLOSED) {
    		throw new InvalidCredentialsException("This form is closed and no longer accepting responses");
    	}else if(form.getStatus() == FormStatus.SCHEDULED) {
    		if (LocalDateTime.now().isAfter(form.getScheduledTime())) {
    			formDto.setPassword((form.getPassword() != null && !form.getPassword().isEmpty()) ? "true" : "false");
    			return ResponseEntity.ok(formDto);
    		}
    		throw new UnProcessableException(form.getScheduledTime().toString());
    	}
    	formDto.setPassword((form.getPassword() != null && !form.getPassword().isEmpty()) ? "true" : "false");
    	return ResponseEntity.ok(formDto);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateForm(@RequestBody FormDto dto){
    	Form form = formRepository.findByFormUid(dto.getUid())
    	        .orElseThrow(() -> new BadRequestException("No Form Found!"));
    	
    	if(form.getPassword().equals(dto.getPassword())) {
    		return ResponseEntity.ok(new MessageResponse("Password validated successfully. You may now access the form."));
    	}else {
    		throw new BadRequestException("Invalid password. Please try again.");
    	}
    }
    
    @DeleteMapping("/{formUid}")
    public ResponseEntity<?> deleteForm(@PathVariable String formUid){
    	Form form = formRepository.findByFormUid(formUid)
    	        .orElseThrow(() -> new BadRequestException("No Form Found!"));
    	
        if (responseRepository.existsByForm(form)) {
            throw new BadRequestException("Cannot delete: This form has already received responses.");
        }
    	
    	formRepository.delete(form);
        return ResponseEntity.ok(new MessageResponse("Form deleted successfully"));
    }
}

