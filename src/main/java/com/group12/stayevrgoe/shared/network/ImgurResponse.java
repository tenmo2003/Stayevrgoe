package com.group12.stayevrgoe.shared.network;

import lombok.Data;
import lombok.ToString;

/**
 * @author anhvn
 */
@Data
@ToString
public class ImgurResponse {
    private ImgurDTO data;
    private boolean success;
    private int status;
}