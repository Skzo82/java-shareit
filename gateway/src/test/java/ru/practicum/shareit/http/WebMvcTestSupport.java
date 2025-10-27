package ru.practicum.shareit.http;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.error.ApiErrorHandler;
import ru.practicum.shareit.error.GlobalExceptionHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 🇷🇺 Общая аннотация для тестов контроллеров: отключает фильтры безопасности и подключает обработчики ошибок.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureMockMvc(addFilters = false)
@Import({ApiErrorHandler.class, GlobalExceptionHandler.class})
public @interface WebMvcTestSupport {
}
