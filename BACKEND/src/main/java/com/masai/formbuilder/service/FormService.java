package com.masai.formbuilder.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.masai.formbuilder.dto.FieldDto;
import com.masai.formbuilder.dto.FormDto;
import com.masai.formbuilder.enums.FieldType;
import com.masai.formbuilder.enums.FormStatus;
import com.masai.formbuilder.exception.BadRequestException;
import com.masai.formbuilder.model.Field;
import com.masai.formbuilder.model.FieldOption;
import com.masai.formbuilder.model.Form;
import com.masai.formbuilder.model.Response;
import com.masai.formbuilder.model.User;
import com.masai.formbuilder.repository.FormRepository;
import com.masai.formbuilder.repository.ResponseRepository;

@Service
public class FormService {
    @Autowired
    private FormRepository formRepository;
    @Autowired
    private contextService contextService;
    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private DashboardService dashboardService;
    
    public FormDto createForm(FormDto dto) throws BadRequestException {
        User creator = contextService.getCurrentUser();
        if(creator == null) throw new BadRequestException("No Creator Found");
        
        // ✅ Uniqueness check
        if (formRepository.existsByTitleIgnoreCaseAndCreator(dto.getTitle(), creator)) {
            throw new BadRequestException("You already have a form with the title " + dto.getTitle());
        }
        
        Form form = new Form();
        form.setTitle(dto.getTitle());
        if(dto.getStatus() == null) form.setStatus(FormStatus.OPEN);
        else form.setStatus(FormStatus.valueOf(dto.getStatus()));
        form.setCreator(creator);
        form.setScheduledTime(dto.getScheduledTime());
        form.setPassword(dto.getPassword());
        form.setWebhookUrl(dto.getWebhookUrl());

        Form frm = form;
        
        List<Field> fields = dto.getFields().stream().map(f -> {
            Field field = new Field();
            field.setLabel(f.getLabel());
            field.setFieldId(f.getId());
            field.setPlaceHolder(f.getPlaceHolder());
            field.setType(f.getType());
            // ✅ Handle DROPDOWN options
            if (f.getType() == FieldType.DROPDOWN && f.getOptions() != null) {
                List<FieldOption> options = f.getOptions().stream().map(fo -> FieldOption.mapToResponse(fo, field)).toList();
                field.setOptions(options);
            }
            field.setRequired(f.isRequired());
            field.setConditionalFieldId(f.getConditionalFieldId());
            field.setConditionalFieldValue(f.getConditionalFieldValue());
            field.setStep(f.getStep());
            field.setForm(frm);
            return field;
        }).toList();

        form.setFields(fields);
        form = formRepository.save(form);
        return new FormDto(form);
    }

    public FormDto getPublishedForm(Long formId) {
        Form form = formRepository.findByIdAndStatus(formId, FormStatus.OPEN).orElseThrow();
        return new FormDto(form);
    }
    
    public String submitForm(Long formId, Map<String, Object> answers, String ip, String password, String email) throws JsonProcessingException, BadRequestException {
        Form form = formRepository.findById(formId).orElseThrow();

        if ("CLOSED".equals(form.getStatus())) {
            throw new BadRequestException("Form is closed");
        }

        if (form.getPassword() != null && !form.getPassword().equals(password)) {
            throw new BadRequestException("Incorrect password");
        }

        String ipAddr = (ip != null && !ip.isEmpty()) ? ip.split(",")[0].trim() : "UNKNOWN";

        boolean isDuplicate = responseRepository.existsByFormIdAndIpAddress(formId, ipAddr);
        if (isDuplicate) {
            throw new BadRequestException("Duplicate submission detected");
        }

        Response response = new Response();
        response.setForm(form);
        response.setEmail(email);
        response.setIpAddress(ipAddr);
        response.setAnswersJson(new ObjectMapper().writeValueAsString(answers));
        response.setSubmittedAt(LocalDateTime.now());

        responseRepository.save(response);
        dashboardService.notifyWebhook(response, form.getWebhookUrl());

        return "Submitted successfully";
    }

	public List<FormDto> getAllForms() {
		User creator = contextService.getCurrentUser();
	    if (creator == null) throw new BadRequestException("No Creator Found");

	    List<Form> forms = formRepository.findAllByCreator(creator);
	    return forms.stream()
	        .map(form -> {
	            long responseCount = responseRepository.countByForm(form);
	            FormDto dto = new FormDto(form);
	            dto.setResponseCount(responseCount);
	            return dto;
	        })
	        .toList();
	}
}