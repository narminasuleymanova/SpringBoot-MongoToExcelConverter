package com.example.demo.controllers;

import com.example.demo.models.Users;
import com.example.demo.services.UsersService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class UsersController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/users")
    public String showUsers(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "name") String sortBy,
                            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        if (search != null && !search.isEmpty()) {
            List<Users> users = usersService.searchUsers(search);
            model.addAttribute("users", users);
            model.addAttribute("search", search);
        } else {
            Page<Users> usersPage = usersService.getAllUsers(pageable);
            model.addAttribute("users", usersPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", usersPage.getTotalPages());
            model.addAttribute("sortBy", sortBy);
        }

        return "users"; // Thymeleaf template name
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() throws IOException {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<Users> usersPage = usersService.getAllUsers(pageable);
        List<Users> users = usersPage.getContent();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        Row headerRow = sheet.createRow(0);
        String[] columnHeaders = {"ID", "Name", "Surname", "Age"};
        for (int i = 0; i < columnHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeaders[i]);
        }

        int rowNum = 1;
        for (Users user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getName() != null ? user.getName() : "");
            row.createCell(2).setCellValue(user.getSurname() != null ? user.getSurname() : "");
            row.createCell(3).setCellValue(user.getAge());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] bytes = outputStream.toByteArray();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }
}
