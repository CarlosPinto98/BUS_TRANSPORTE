package com.unimag.DTO;

import com.unimag.entities.Enums.StatusParcel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParcelDTO {

    public record parcelCreateRequest(
            @NotBlank String senderName,
            @NotBlank
            @Pattern(regexp = "\\d{10}", message = "phone must be exactly 10 digits")
            String senderPhone,
            @NotBlank String receiverName,
            @NotBlank
            @Pattern(regexp = "\\d{10}", message = "phone must be exactly 10 digits")
            String receiverPhone,
            @NotNull @DecimalMin("0.0")
            BigDecimal price,
            @NotNull Long fromStopId,
            @NotNull Long toStopId,
            Long tripId) implements Serializable {}

    public record parcelUpdateRequest(
            @NotNull(message = "status is required")
            StatusParcel statusParcel,
            String proofPhotoUrl,
            String deliveryOtp) implements Serializable {}

    public record parcelResponse(
            Long id,
            String code,
            String senderName,
            String senderPhone,
            String receiverName,
            String receiverPhone,
            BigDecimal price,
            String statusParcel,
            String proofPhotoUrl,
            String deliveryOtp,
            LocalDateTime createdAt,
            LocalDateTime deliveredAt,
            Long fromStopId,
            Long toStopId,
            Long tripId) implements Serializable {}
}
