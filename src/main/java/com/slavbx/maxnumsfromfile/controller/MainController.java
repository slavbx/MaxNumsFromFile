package com.slavbx.maxnumsfromfile.controller;

import com.slavbx.maxnumsfromfile.service.DataSorterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class MainController {
    private final DataSorterService dataSorterService;

    public MainController(DataSorterService dataSorterService) {
        this.dataSorterService = dataSorterService;
    }

    @Operation(summary = "Запрос максимального n-ого")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ",
                    content = @Content(schema = @Schema(example = "Максимальное n-ое число: 5"))),
            @ApiResponse(responseCode = "422", description = "Параметры запроса некорректны",
                    content = @Content(schema = @Schema(example = "Ошибка: необходимо передать число от 1 до 10"))),
            @ApiResponse(responseCode = "404", description = "Файл не найден",
                    content = @Content(schema = @Schema(example = "Ошибка: Unnamed.xlsx (Не удается найти указанный файл)"))),
    })
    @GetMapping("/getmax")
    public ResponseEntity<String> getMaxNumFromFile(@Schema(description = "Название файла", example = "Data.xlsx")
                                                        @RequestParam String filename,
                                                    @Schema(description = "Номер максимального", example = "1")
                                                        @RequestParam int n) {
        return dataSorterService.findMaxNumFromFile(n, filename);
    }
}
