import React from "react";
import { Input } from "../../../../ui/input";
import { Checkbox } from "../../../../ui/checkbox";
import { Label } from "../../../../ui/label";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../../../../ui/select";

export function FieldRenderer({ field, disabled, onChange, register, setValue, preview, value, required }) {
  const renderInput = () => {
    const commonProps = {
      id: `field-${field.id}`,
      disabled,
      required: required || field.required || false,
      "aria-label": field.label || field.placeholder || `${field.type} field`,
      ...(register ? register(field.id, { 
        required: field.required,
       
      }) : {}),
      ...(onChange ? { onChange: (e) => onChange(e.target.value) } : {}),
      ...(onChange ? { onValueChange: (value) => onChange(value) } : {}),
      ...(onChange ? { onCheckedChange : (e) => onChange(e) } : {}),
      value: value || '',
      checked: value || false,
    };

    switch (field.type) {
      case "text":
      case "email":
      case "number":
        return (
          <Input
            {...commonProps}
            type={field.type}
            placeholder={field.placeholder || `Enter ${field.type}`}
            name={field.id}
          />
        );

      case "dropdown":
        // Check if we have valid options
        const hasValidOptions = field.options?.some((opt) => {
          const value = opt?.label?.trim() && opt?.value?.trim();
          return value;
        });

        return (
          <div className="space-y-2">
            {/* {field.label && <Label>{field.label}</Label>} */}
            {!hasValidOptions && (
              <div className="text-sm text-red-500 mb-2">
                This dropdown has no valid options. Please add options with
                non-empty label and values.
              </div>
            )}
            <Select
              {...commonProps}
              placeholder={field.placeholder || "Select an option"}
              name={field.id}
              disabled={disabled}
            >
              <SelectTrigger className="w-full">
                <SelectValue
                  placeholder={field.placeholder || "Select an option"}
                />
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  {field.options?.length > 0 ? (
                    field.options.map((option, index) => {
                      const value =
                        typeof option === "string" ? option : option.value;
                      const label =
                        typeof option === "string"
                          ? option
                          : option.label || option.value;

                      // Skip options with empty values
                      if (!value) return null;

                      return (
                        <SelectItem key={index} value={value}>
                          {label || value}
                        </SelectItem>
                      );
                    })
                  ) : (
                    <SelectItem value="no-options" disabled>
                      No options available
                    </SelectItem>
                  )}
                </SelectGroup>
              </SelectContent>
            </Select>
          </div>
        );

      case "checkbox":
        return (
          <div className="flex items-center space-x-2">
            <Checkbox {...commonProps} name={field.id}  />
            {field.label && (
              <Label htmlFor={`field-${field.id}`} className="cursor-pointer">
                {field.label}
              </Label>
            )}
          </div>
        );

      case "rating":
        return (
          <div
            className="flex items-center gap-1"
            role="radiogroup"
            aria-label="Rating"
          >
            {[1, 2, 3, 4, 5].map((ratingValue) => (
              <button
                key={ratingValue}
                onClick={() => {
                  onChange(ratingValue);
                  if (setValue) {
                    setValue(field.id, ratingValue);
                  }
                }}
                type="button"
                aria-label={`Rate ${ratingValue} stars`}
                className={`p-1 rounded hover:bg-gray-100 ${
                  value >= ratingValue ? "text-yellow-400" : "text-gray-300"
                }`}
              >
                ★
              </button>
            ))}
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <div className="space-y-2">
      {field.type !== "checkbox" && field.label && (
        <Label htmlFor={`field-${field.id}`} className="font-medium">
          {field.label}
          {field.required && <span className="text-red-500 ml-1">*</span>}
        </Label>
      )}
      {renderInput()}
    </div>
  );
}
