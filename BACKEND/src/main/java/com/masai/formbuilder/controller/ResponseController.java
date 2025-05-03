package com.masai.formbuilder.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.masai.formbuilder.dto.MessageResponse;
import com.masai.formbuilder.dto.ResponseDto;
import com.masai.formbuilder.service.ResponseService;

@RestController
@RequestMapping("/api/responses")
public class ResponseController {
    @Autowired
    private ResponseService responseService;

    @PostMapping("/submit/{formId}")
    public ResponseEntity<?> submitResponse(@PathVariable Long formId, @RequestBody ResponseDto dto, HttpServletRequest request) throws JsonProcessingException {
        return ResponseEntity.ok(new MessageResponse(responseService.submitForm(formId, dto, request.getRemoteAddr())));
    }
}
