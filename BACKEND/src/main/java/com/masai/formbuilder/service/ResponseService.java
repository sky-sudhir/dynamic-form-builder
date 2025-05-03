package com.masai.formbuilder.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masai.formbuilder.dto.ResponseDto;
import com.masai.formbuilder.exception.BadRequestException;
import com.masai.formbuilder.model.Form;
import com.masai.formbuilder.model.Response;
import com.masai.formbuilder.repository.FormRepository;
import com.masai.formbuilder.repository.ResponseRepository;

@Service
public class ResponseService {
    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private FormRepository formRepository;
    @Autowired
    private DashboardService dashboardService;

    public String submitForm(Long formId, ResponseDto dto, String ip) throws JsonProcessingException {
        Form form = formRepository.findById(formId).orElseThrow();

        if ("CLOSED".equals(form.getStatus())) {
            throw new BadRequestException("Form is closed");
        }
       boolean isDuplicate = responseRepository.existsByFormIdAndIpAddress(formId, ip);
       if (isDuplicate) {
           throw new BadRequestException("Duplicate submission detected");
       }

        Response response = new Response();
        response.setForm(form);
        response.setEmail(dto.getEmail());
        response.setIpAddress(ip);
        response.setAnswersJson(new ObjectMapper().writeValueAsString(dto.getAnswersJson()));
        response.setSubmittedAt(LocalDateTime.now());

        responseRepository.save(response);
        dashboardService.notifyWebhook(response, form.getWebhookUrl());

        return "Submitted successfully";
    }
}
