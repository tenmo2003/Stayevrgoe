package com.group12.stayevrgoe.shared.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JwtConstants {
    public static final long JWT_TOKEN_VALIDITY = (long) 1000 * 60 * 60 * 24;

    public static final String SECRET_KEY = "MIIBITANBgkqhkiG9w0BAQEFAAOCAQ4AMIIBCQKCAQBzLTXlzr8BOIscEktgJkGt" +
            "1Kqpfaau/j1/2BLDixkKwbii3qisogNJJ/MlQXmR6oWS4OkyU9u5UmKedlbtw0Mn" +
            "qJ8vKQaiDWR112k/45NIDlVEN7yfhoYDkCexUk/7RClzGmqU8gBznm08Ky2TB5xa" +
            "lv0kou8jZWpE343pQBqf7LjI+r8LxbaRgzIfnXgCHgod5i3dVjOvHnJxlW/kNENR" +
            "hlzaVTDjE3XySumvvd18DVzZQ7TV6fD3wJhqw/mzJZ7MVbct/0TyXTGzO5j4++YQ" +
            "9kxW97DU8/OblAqVu1mXdgF/+p4n21bKY8t31UeiP8h8b7V5mklzcljqDWdYN9I3" +
            "AgMBAAE=";
}
