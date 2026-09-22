package com.example.photovault.service;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Value("${file.upload-dir}")
	private String rootuploadDir;
	
    // 1. login  & auto-register logic

	public boolean loginORCreateUser(String username, String password) {
        String countSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        int userCount = jdbcTemplate.queryForObject(countSql, Integer.class, username);
        
        if(userCount >0) {
        	String passwordSql = "SELECT password FROM users WHERE username = ?";
        	String storedPassword = jdbcTemplate.queryForObject(passwordSql, String.class, username);
        	
        	if (storedPassword.equals(password)) {
        		return true;
        	} else {
        		return false;
        	}
        	} else {
        	String	insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
        	
			jdbcTemplate.update(insertSql, username, password);
			return true;
        	}
        
        }
	
	 // 2. upload photo file & database logic
	
	public String saveUserPhoto(String username, MultipartFile file) throws IOException {

	    String idSql = "SELECT id FROM users WHERE username = ?";
	    int userId = jdbcTemplate.queryForObject(idSql, Integer.class, username);

	    File folder = new File(rootuploadDir + File.separator + userId);

	    if (!folder.exists()) {
	        folder.mkdirs();
	    }

	    File savedFile = new File(folder, file.getOriginalFilename());
	    file.transferTo(savedFile);

	    String fullPath = savedFile.getAbsolutePath();

	    
	    String photoUrl = "/uploads/" + userId + "/" + file.getOriginalFilename();

	    String insertPhotoSql =
	            "INSERT INTO photo (user_id, file_path, photo_url) VALUES (?, ?, ?)";

	    jdbcTemplate.update(
	            insertPhotoSql,
	            userId,
	            fullPath,
	            photoUrl
	    );

	    return "file uploaded successfully";
	}
		// 3. get user photos logic
		
	    		
	    public List<String> getUserPhotosList(String username) {
	        String sql = "SELECT p.photo_url FROM photo p " +
	                     "JOIN users u ON p.user_id = u.id " +
	                     "WHERE u.username = ?";
	        
	        List<String> allPaths = jdbcTemplate.queryForList(sql, String.class, new Object[]{ username });
	        return allPaths;
	    }
	    
		// delete logic 
		
		public String deleteUserPhoto(String username, String fullPath) {
			String idSql = "select id from users where username = ?";
			int userId = jdbcTemplate.queryForObject(idSql, Integer.class, username);

			File fileToDelete = new File(fullPath);
			if (fileToDelete.exists()) {
				fileToDelete.delete();

				String deleteSql = "delete from photo where user_id = ? and file_path = ?";
				jdbcTemplate.update(deleteSql, userId, fullPath);

				return "file deleted successfully";
			} else {
				return "file not found";
			}
		}
					
}
