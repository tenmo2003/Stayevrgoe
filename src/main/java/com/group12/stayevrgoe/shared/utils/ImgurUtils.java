package com.group12.stayevrgoe.shared.utils;

import com.group12.stayevrgoe.shared.constants.Constants;
import com.group12.stayevrgoe.shared.exceptions.BusinessException;
import com.group12.stayevrgoe.shared.network.ImgurResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author anhvn
 */
@UtilityClass
@Slf4j
public class ImgurUtils {
    private static String clientId;

    static {
        String imgurKeyFilePath =
                FileUtils.getProjectRootDirectory()
                        + File.separator
                        + "certs"
                        + File.separator
                        + "imgur.txt";
        try {
            byte[] bytes = FileUtils.getFileBytesFromPath(imgurKeyFilePath);
            clientId = new String(bytes);
            log.info("Imgur client id: {}", clientId);
        } catch (Exception e) {
            log.error("Error getting imgur client id: {}", e.getMessage());
        }
    }

    public static String uploadSingleImage(MultipartFile image) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", clientId);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", image.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ImgurResponse response = restTemplate.postForObject(Constants.IMGUR_UPLOAD_IMAGE_URL, requestEntity, ImgurResponse.class);
        if (response == null) {
            log.error("Failed to upload image");
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image");
        }
        return response.getData().getLink();
    }

    public static List<String> uploadMultipleImages(List<MultipartFile> images) {
        List<String> links = new ArrayList<>();
        List<Callable<String>> tasks = new ArrayList<>();
        for (MultipartFile image : images) {
            tasks.add(() -> {
                String link = uploadSingleImage(image);
                links.add(link);
                return link;
            });
        }

        try {
            ThreadPoolUtils.executeAndWaitForTasks(tasks);
        } catch (InterruptedException e) {
            log.error("Error uploading multiple images: {}", e.getMessage());
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Error uploading multiple images");
        }

        return links;
    }
}
