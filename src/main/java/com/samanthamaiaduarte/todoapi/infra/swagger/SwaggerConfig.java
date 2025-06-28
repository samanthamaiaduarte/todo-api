package com.samanthamaiaduarte.todoapi.infra.swagger;

import com.samanthamaiaduarte.todoapi.domain.task.TaskResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.LoginResponseDTO;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final LocalDateTime dateTime = LocalDateTime.of(2025, 6, 28, 12, 34, 56);
        final LocalDate date = LocalDate.of(2025, 6, 28);

        TaskResponseDTO taskResponse = new TaskResponseDTO(UUID.randomUUID(),"Task title", "Task description", date, false);
        List<TaskResponseDTO> tasksUncompleted = List.of(
                new TaskResponseDTO(UUID.randomUUID(),"Task title 1", "Task description 1", date.minusDays(1), false),
                new TaskResponseDTO(UUID.randomUUID(),"Task title 2", "Task description 2", date, false),
                new TaskResponseDTO(UUID.randomUUID(),"Task title 3", "Task description 3", date.plusDays(1), false)
        );
        List<TaskResponseDTO> tasksCompleted = List.of(
                new TaskResponseDTO(UUID.randomUUID(),"Task title 1", "Task description 1", date.minusDays(1), true),
                new TaskResponseDTO(UUID.randomUUID(),"Task title 2", "Task description 2", date, true),
                new TaskResponseDTO(UUID.randomUUID(),"Task title 3", "Task description 3", date.plusDays(1), true)
        );

        LoginResponseDTO token = new LoginResponseDTO(dateTime, "bearer", "valid_token", 7200);

        ExceptionHandlerSchema badRequest = new ExceptionHandlerSchema(400, "BAD_REQUEST", dateTime, "Invalid data.");
        ExceptionHandlerSchema unauthorized = new ExceptionHandlerSchema(401, "UNAUTHORIZED", dateTime, "Invalid / Expired token.");
        ExceptionHandlerSchema unauthorizedLogin = new ExceptionHandlerSchema(401, "UNAUTHORIZED", dateTime, "Invalid credentials.");
        ExceptionHandlerSchema forbidden = new ExceptionHandlerSchema(403, "FORBIDDEN", dateTime, "No permission / Token is required.");
        ExceptionHandlerSchema notFound = new ExceptionHandlerSchema(404, "NOT_FOUND", dateTime, "Record not found.");
        ExceptionHandlerSchema conflict = new ExceptionHandlerSchema(409, "CONFLICT", dateTime, "Record already exists.");
        ExceptionHandlerSchema unsupported = new ExceptionHandlerSchema(415, "UNSUPPORTED_MEDIA_TYPE", dateTime, "Wrong data type content for payload.");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                        .addSchemas("Token", new Schema<LoginResponseDTO>()
                                .description("Payload of a generated token")
                                .type("object")
                                .addProperty("refresh", new DateTimeSchema())
                                .addProperty("token_type", new StringSchema())
                                .addProperty("access_token", new StringSchema())
                                .addProperty("expires_in", new IntegerSchema())
                        )
                        .addSchemas("Error", new Schema<ExceptionHandlerSchema>()
                                .description("Payload for a error message.")
                                .type("object")
                                .addProperty("statusCode", new IntegerSchema())
                                .addProperty("status", new StringSchema())
                                .addProperty("timestamp", new DateTimeSchema())
                                .addProperty("errorMessage", new StringSchema())
                        )
                        .addSchemas("Task", new Schema<TaskResponseDTO>()
                                .description("Payload for created or updated a task")
                                .type("object")
                                .addProperty("id", new UUIDSchema())
                                .addProperty("title", new StringSchema())
                                .addProperty("description", new StringSchema())
                                .addProperty("dueDate", new DateSchema())
                                .addProperty("completed", new BooleanSchema())
                        )
                        .addResponses("200", new ApiResponse()
                                .description("Ok")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Task")).example(taskResponse))
                                )
                        )
                        .addResponses("200uncompletedListTask", new ApiResponse()
                                .description("Ok")
                                .content(new Content().addMediaType("application/json",
                                         new MediaType().schema(new Schema<>().$ref("#/components/schemas/Task")).example(tasksUncompleted))
                                )
                        )
                        .addResponses("200completedListTask", new ApiResponse()
                                .description("Ok")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Task")).example(tasksCompleted))
                                )
                        )
                        .addResponses("200updateTask", new ApiResponse()
                                .description("Update successfully")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Task")).example(taskResponse))
                                )
                        )
                        .addResponses("200login", new ApiResponse()
                                .description("Ok")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Token")).example(token))
                                )
                        )
                        .addResponses("201", new ApiResponse()
                                .description("Created successfully")
                        )
                        .addResponses("201task", new ApiResponse()
                                .description("Created successfully")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Task")).example(taskResponse))
                                )
                        )
                        .addResponses("204deleteTask", new ApiResponse()
                                .description("Record deleted")
                        )
                        .addResponses("204completeTask", new ApiResponse()
                                .description("Updated successfully")
                        )
                        .addResponses("400", new ApiResponse()
                                .description("Bad request")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Error")).example(badRequest))
                                )
                        )
                        .addResponses("401", new ApiResponse()
                                .description("Unauthorized")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Error")).example(unauthorized))
                                )
                        )
                        .addResponses("401login", new ApiResponse()
                                .description("Unauthorized")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Error")).example(unauthorizedLogin))
                                )
                        )
                        .addResponses("403", new ApiResponse()
                                .description("Forbidden")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Error")).example(forbidden))
                                )
                        )
                        .addResponses("404", new ApiResponse()
                                .description("Not found")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Error")).example(notFound))
                                )
                        )
                        .addResponses("409", new ApiResponse()
                                .description("Conflict")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Error")).example(conflict))
                                )
                        )
                        .addResponses("415", new ApiResponse()
                                .description("Unsupported content")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Error")).example(unsupported))
                                )
                        )
                )
                .info(new Info()
                        .title("TO-DO API")
                        .version("1.0")
                        .description("Personal task management API")
                        .contact(new Contact()
                                .name("Samantha Maia Duarte")
                                .email("sam@samanthamaiaduarte.com")));
    }
}
