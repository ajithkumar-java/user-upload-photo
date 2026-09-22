package com.example.photovault.controller;

import com.example.photovault.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vault")
@CrossOrigin(origins = "*")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        boolean isAllowed = photoService.loginORCreateUser(username, password);
        if (isAllowed) {
            return "OK";
        } else {
            return "FAIL";
        }
    }

    @PostMapping("/upload")
    public String upload(@RequestParam String username, @RequestParam MultipartFile file) {
        try {
            return photoService.saveUserPhoto(username, file);
        } catch (IOException e) {
            return "Upload Error";
        }
    }

    @GetMapping("/view")
    public List<String> view(@RequestParam String username) {
        return photoService.getUserPhotosList(username);
    }

    @PostMapping("/delete") 
    public String delete(@RequestParam String username, @RequestParam String filepath) {
        return photoService.deleteUserPhoto(username, filepath);
    }
}
