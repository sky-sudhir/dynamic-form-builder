package com.masai.formbuilder.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.masai.formbuilder.model.Form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDto {
    private Long id;
    private String uid;
    private String title;
    private List<FieldDto> fields;
    private String status;
    private LocalDateTime scheduledTime;
    private String password;
    private String webhookUrl;
    private String createdDate;
    private long responseCount;

    public FormDto(Form form) {
        this.id = form.getId();
        this.uid = form.getFormUid();
        this.title = form.getTitle();
        this.fields = form.getFields().stream().map(FieldDto::new).toList();
        this.status = form.getStatus().toString();
        this.scheduledTime = form.getScheduledTime();
        this.password = form.getPassword();
        this.webhookUrl = form.getWebhookUrl();
        this.createdDate = form.getCreatedAt().toString();
    }
}
