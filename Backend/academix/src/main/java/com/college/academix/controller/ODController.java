package com.college.academix.controller;

import com.college.academix.dto.ApiResponse;
import com.college.academix.model.ODRequest;
import com.college.academix.repository.UserRepository;
import com.college.academix.service.ODService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/od")
@Tag(name = "OD Requests", description = "OD Request management and workflow")
public class ODController {

    @Autowired
    private ODService odService;

    @Operation(summary = "Get All OD Requests", description = "View all OD requests (for admins or authorized users)")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ODRequest>>> getAllRequests() {
        return ResponseEntity.ok(ApiResponse.success("All OD requests retrieved", odService.getAllODRequests()));
    }
}