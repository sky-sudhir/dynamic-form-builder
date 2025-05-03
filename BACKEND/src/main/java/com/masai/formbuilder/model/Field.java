package com.masai.formbuilder.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.masai.formbuilder.enums.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String fieldId;

    private String label;
    
    private String placeHolder;

    @Enumerated(EnumType.STRING)
    private FieldType type;
    
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FieldOption> options = new ArrayList<>();

    private boolean required;

    private String conditionalFieldId;
    private String conditionalFieldValue;
    
    private Integer step;

    @ManyToOne
    private Form form;
}