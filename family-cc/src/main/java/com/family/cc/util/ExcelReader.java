package com.family.cc.util;
import com.family.cc.domain.dto.ExcelEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelReader {

    public List<ExcelEntity> readExcel(File file) {
        List<ExcelEntity> entities = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // 跳过表头
                }
                ExcelEntity entity = new ExcelEntity();
                entity.setChapterId((long) row.getCell(0).getNumericCellValue());
                entity.setCharacter(row.getCell(1).getStringCellValue());
                entity.setCharacterType(row.getCell(2).getStringCellValue());
                entity.setPinyin(row.getCell(3).getStringCellValue());
                entity.setTranslation(row.getCell(4).getStringCellValue());
                List<String> compounds = new ArrayList<>(8);
                List<String> synonyms = new ArrayList<>(3);
                List<String> antonyms = new ArrayList<>(3);
                compounds.add(row.getCell(5).getStringCellValue());
                compounds.add(row.getCell(6).getStringCellValue());
                compounds.add(row.getCell(7).getStringCellValue());
                compounds.add(row.getCell(8).getStringCellValue());
                compounds.add(row.getCell(9).getStringCellValue());
                compounds.add(row.getCell(10).getStringCellValue());
                compounds.add(row.getCell(11).getStringCellValue());
                compounds.add(row.getCell(12).getStringCellValue());
                synonyms.add(row.getCell(13).getStringCellValue());
                synonyms.add(row.getCell(14).getStringCellValue());
                synonyms.add(row.getCell(15).getStringCellValue());
                antonyms.add(row.getCell(16).getStringCellValue());
                antonyms.add(row.getCell(17).getStringCellValue());
                antonyms.add(row.getCell(18).getStringCellValue());
                entity.setCompounds(compounds);
                entity.setSynonyms(synonyms);
                entity.setAntonyms(antonyms);
                entity.setFrequency((long) row.getCell(19).getNumericCellValue());

                entities.add(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entities;
    }
}
