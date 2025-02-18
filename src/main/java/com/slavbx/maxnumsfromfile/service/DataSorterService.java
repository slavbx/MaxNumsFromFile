package com.slavbx.maxnumsfromfile.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Сервис для получения данных из файла и их сортировки.
 * Предоставляет функционал загрузки данных,
 * их сортировки и возвращения n-ого максимального значения
 */
@Service
public class DataSorterService {

    public ResponseEntity<String> findMaxNumFromFile(int n, String filename) {
        ResponseEntity<String> response;
        try {
            int[] data = loadData(filename);
            sort(0, data.length - 1, data);
            if (n > 0 && n <= data.length) {
                response = ResponseEntity.ok().body("Максимальное n-ое число: " + data[data.length - n]);
            } else {
                response =  ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("Ошибка: " + "необходимо передать число от 1 до " + (data.length));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка: " + e.getMessage());
        }
        return response;
    }

    /**
     * Метод загрузки массива данных из xlsx файла. Ожидается, что данные
     * представлены целыми числами и расположены в первом столбце
     */
    private int[] loadData(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); //Первый лист
            int maxRow = sheet.getLastRowNum(); //По последнюю строку
            int[] arr = new int[maxRow + 1]; //Массив для входных данных
            for (int i = 0; i <= maxRow; i++) {
                Cell cell = sheet.getRow(i).getCell(0);
                double cellValue = cell.getNumericCellValue();
                arr[i] = (int) cellValue;
            }
            return arr;
        }
    }

    /**
     * Метод, обеспечивающий сортировку массива слиянием с использованием рекурсии
     */
    private void sort(int first, int last, int... arr) {
        int half;
        if (first < last) {
            half = (first + last) / 2; // Делим массив пополам, сортируем каждую половину
            sort(first, half, arr);
            sort(half + 1, last, arr);
            merge(first, half, last, arr); // Делаем слияние отсортированных половин
        }
    }

    /**
     * Метод, обеспечивающий сортировку массива (вспомогательный)
     */
    private void merge(int first, int half, int last, int... input) {
        int[] merged = new int[last - first + 1]; // Массив слияния
        int i = first;
        int j = half + 1;
        boolean iPassed = false;
        boolean jPassed = false;

        for (int taken = 0; taken < merged.length; taken++) { // Число шагов == размер массива слияния
            // При финише j курсора, массив слияния дополняем остатками 1го массива
            if (jPassed) {
                merged[taken] = input[i];
                if (i < half) i++;
            }
            // При финише i курсора, массив слияния дополняем остатками 2го массива
            if (iPassed) {
                merged[taken] = input[j];
                if (j < last) j++;
            }
            // Идём курсорами i и j по каждому массиву, берём наименьшие значения на каждом шаге
            if (input[i] < input[j] && !iPassed) {
                merged[taken] = input[i];
                if (i < half) {
                    i++;
                } else {
                    iPassed = true;
                }
            } else if (input[i] >= input[j] && !jPassed) {
                merged[taken] = input[j];
                if (j < last) {
                    j++;
                } else {
                    jPassed = true;
                }
            }
        }
        // Помещаем массив слияния в исходный массив
        for (int k = 0; k < merged.length; k++) {
            input[first + k] = merged[k];
        }
    }
}
