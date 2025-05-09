package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.model.dto.FandomDTO;
import com.tgb.gsvnbackend.service.FandomService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fandoms")
public class FandomController {

    private final FandomService fandomService;

    @Autowired
    public FandomController(FandomService fandomService) {
        this.fandomService = fandomService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FandomDTO> getFandomById(@PathVariable int id) {
        FandomDTO fandomDTO = fandomService.read(id);
        return ResponseEntity.ok(fandomDTO);
    }

    @GetMapping
    public ResponseEntity<List<FandomDTO>> getAllFandoms() {
        List<FandomDTO> fandomDTOs = fandomService.readAll();
        return ResponseEntity.ok(fandomDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FandomDTO>> searchFandomsByTitle(@RequestParam String title) {
        List<FandomDTO> fandomDTOs = fandomService.searchByTitle(title);
        return ResponseEntity.ok(fandomDTOs);
    }

    @PostMapping
    public ResponseEntity<FandomDTO> createFandom(@Valid @RequestBody FandomDTO fandomDTO) {
        FandomDTO createdFandom = fandomService.create(fandomDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFandom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FandomDTO> updateFandom(@PathVariable int id, @Valid @RequestBody FandomDTO fandomDTO) {
        FandomDTO updatedFandom = fandomService.update(id, fandomDTO);
        return ResponseEntity.ok(updatedFandom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFandom(@PathVariable int id) {
        fandomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/page")
    public ResponseEntity<Page<FandomDTO>> getFandomsByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fandom_id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        Page<FandomDTO> fandomDTOPage = fandomService.readByPage(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(fandomDTOPage);
    }
}

