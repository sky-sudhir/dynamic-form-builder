package com.masai.formbuilder.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.data.annotation.CreatedDate;

import com.masai.formbuilder.enums.FormStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String formUid = UUID.randomUUID().toString(); // ✅ Unique token to access the form

    private String title;

    @Enumerated(EnumType.STRING)
    private FormStatus status;

    private LocalDateTime scheduledTime;
    
    private LocalDateTime createdAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();;

    @ManyToOne
    private User creator;

    private String password;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
    private List<Field> fields = new ArrayList<>();
    
    private String webhookUrl;
}
