package com.college.academix.service;

import com.college.academix.exception.BadRequestException;
import com.college.academix.exception.ResourceNotFoundException;
import com.college.academix.model.*;
import com.college.academix.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ODService {

    @Autowired
    private ODRepository odRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    // ===================== STUDENT: Create OD Request =====================

    public ODRequest createODRequest(String reason, String fromDate, String toDate,
            User currentUser, MultipartFile eventDocument,
            MultipartFile paymentProof) {
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        ODRequest odRequest = ODRequest.builder()
                .studentName(currentUser.getName())
                .rollNumber(student.getRollNumber())
                .section(student.getSection().getName())
                .reason(reason)
                .fromDate(java.time.LocalDate.parse(fromDate))
                .toDate(java.time.LocalDate.parse(toDate))
                .status(ODStatus.PENDING)
                .mentorApproved(false)
                .hodApproved(false)
                .student(student)
                .build();

        // Handle event document upload
        if (eventDocument != null && !eventDocument.isEmpty()) {
            try {
                odRequest.setEventDocument(eventDocument.getBytes());
                odRequest.setEventDocumentName(eventDocument.getOriginalFilename());
            } catch (IOException e) {
                throw new BadRequestException("Failed to process event document");
            }
        }

        // Handle payment proof upload (optional)
        if (paymentProof != null && !paymentProof.isEmpty()) {
            try {
                odRequest.setPaymentProof(paymentProof.getBytes());
                odRequest.setPaymentProofName(paymentProof.getOriginalFilename());
            } catch (IOException e) {
                throw new BadRequestException("Failed to process payment proof");
            }
        }

        return odRepository.save(odRequest);
    }

    // ===================== STUDENT: View Own OD Requests =====================

    public List<ODRequest> getStudentODRequests(User currentUser) {
        Student student = studentRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return odRepository.findByStudent(student);
    }

    // ===================== MENTOR: Get Pending Requests for Section
    // =====================

    public List<ODRequest> getPendingRequestsForMentor(User currentUser) {
        Faculty faculty = facultyRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));

        if (!faculty.isClassAdvisor() || faculty.getAdvisorSection() == null) {
            throw new BadRequestException("You are not a class advisor for any section");
        }

        String sectionName = faculty.getAdvisorSection().getName();
        return odRepository.findBySectionAndStatus(sectionName, ODStatus.PENDING);
    }

    // ===================== MENTOR: Approve OD =====================

    public ODRequest mentorApprove(Long odId) {
        ODRequest odRequest = odRepository.findById(odId)
                .orElseThrow(() -> new ResourceNotFoundException("OD Request not found"));

        if (odRequest.getStatus() != ODStatus.PENDING) {
            throw new BadRequestException("OD Request is not in PENDING status");
        }

        odRequest.setMentorApproved(true);
        odRequest.setStatus(ODStatus.MENTOR_APPROVED);
        return odRepository.save(odRequest);
    }

    // ===================== MENTOR/HOD: Reject OD =====================

    public ODRequest rejectOD(Long odId, String rejectionReason) {
        ODRequest odRequest = odRepository.findById(odId)
                .orElseThrow(() -> new ResourceNotFoundException("OD Request not found"));

        if (odRequest.getStatus() == ODStatus.APPROVED || odRequest.getStatus() == ODStatus.REJECTED) {
            throw new BadRequestException("OD Request has already been finalized");
        }

        odRequest.setStatus(ODStatus.REJECTED);
        odRequest.setRejectionReason(rejectionReason);
        return odRepository.save(odRequest);
    }

    // ===================== HOD: Get Mentor-Approved Requests =====================

    public List<ODRequest> getMentorApprovedRequests() {
        return odRepository.findByStatus(ODStatus.MENTOR_APPROVED);
    }

    // ===================== HOD: Final Approve OD =====================

    public ODRequest hodApprove(Long odId) {
        ODRequest odRequest = odRepository.findById(odId)
                .orElseThrow(() -> new ResourceNotFoundException("OD Request not found"));

        if (odRequest.getStatus() != ODStatus.MENTOR_APPROVED) {
            throw new BadRequestException("OD Request must be approved by mentor first");
        }

        odRequest.setHodApproved(true);
        odRequest.setStatus(ODStatus.APPROVED);
        return odRepository.save(odRequest);
    }

    // ===================== All OD Requests =====================

    public List<ODRequest> getAllODRequests() {
        return odRepository.findAll();
    }
}