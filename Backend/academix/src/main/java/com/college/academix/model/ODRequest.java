package com.college.academix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "od_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ODRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String rollNumber;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private String reason; // Event Name + Venue

    @Column(nullable = false)
    private LocalDate fromDate;

    @Column(nullable = false)
    private LocalDate toDate;

    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] eventDocument;

    private String eventDocumentName;

    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] paymentProof; // Optional

    private String paymentProofName;

    @Builder.Default
    private boolean mentorApproved = false;

    @Builder.Default
    private boolean hodApproved = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ODStatus status = ODStatus.PENDING;

    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
}