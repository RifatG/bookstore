package com.example.my_book_shop_app.services;

import com.example.my_book_shop_app.repositories.BookFileRepository;
import com.example.my_book_shop_app.struct.book.file.BookFile;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Service
public class ResourceStorage {

    @Value("${upload.path.book-covers}")
    String bookImageUploadPath;

    @Value("${upload.path.author-covers}")
    String authorImageUploadPath;

    @Value("${server.root.path}")
    String rootPath;

    @Value("${download.path.book-files}")
    String downloadPath;

    private final BookFileRepository bookFileRepository;

    @Autowired
    public ResourceStorage(BookFileRepository bookFileRepository) {
        this.bookFileRepository = bookFileRepository;
    }

    public String saveNewBookImage(MultipartFile file, String slug) throws IOException {
        return saveImage(file, slug, bookImageUploadPath);
    }

    public String saveNewAuthorImage(MultipartFile file, String slug) throws IOException {
        return saveImage(file, slug, authorImageUploadPath);
    }

    private String saveImage(MultipartFile file, String slug, String uploadPath) throws IOException {
        String fullUploadPath = rootPath + uploadPath;
        String resourceURI = null;

        if(!file.isEmpty()) {
            if(!new File(fullUploadPath).exists()) {
                Files.createDirectories(Paths.get(fullUploadPath));
                Logger.getLogger(this.getClass().getSimpleName()).info("Created image folder in " + fullUploadPath);
            }

            String fileName = slug + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            Path path = Paths.get(fullUploadPath , fileName);
            resourceURI = Paths.get(uploadPath, fileName).toString();
            file.transferTo(path);
            Logger.getLogger(this.getClass().getSimpleName()).info(fileName + " uploaded OK!");
        }

        return resourceURI;
    }

    public Path getBookFilePath(String hash) {
        return Paths.get(this.bookFileRepository.findBookFileByHash(hash).getPath());
    }

    public MediaType getBookFileMime(String hash) {
        BookFile bookFile = this.bookFileRepository.findBookFileByHash(hash);
        String mimeType =
                URLConnection.guessContentTypeFromName(Paths.get(bookFile.getPath()).getFileName().toString());
        if(mimeType != null) {
            return MediaType.parseMediaType(mimeType);
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public byte[] getBookFileByteArray(String hash) throws IOException {
        BookFile bookFile = this.bookFileRepository.findBookFileByHash(hash);
        Path path = Paths.get(downloadPath, bookFile.getPath());
        return Files.readAllBytes(path);
    }
}
