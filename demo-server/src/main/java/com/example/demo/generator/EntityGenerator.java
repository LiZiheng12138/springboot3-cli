package com.example.demo.generator;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntityGenerator {

    private static final String URL = "jdbc:mysql://localhost:3306/yuding-test?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Aa123456!";

    private static final String PACKAGE_ENTITY = "com.example.demo.entity";
    private static final String PACKAGE_ENTITY_DEF = "com.example.demo.table";

    private static final String OUTPUT_PATH = "demo-server/src/main/java/";

    public static void main(String[] args) throws Exception {
        generateEntity("app_user"); // 表名
    }

    public static void generateEntity(String tableName) throws Exception {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        DatabaseMetaData metaData = conn.getMetaData();

        ResultSet rs = metaData.getColumns(null, null, tableName, null);

        List<ColumnInfo> columns = new ArrayList<>();
        while (rs.next()) {
            String colName = rs.getString("COLUMN_NAME");
            String type = rs.getString("TYPE_NAME");
            columns.add(new ColumnInfo(colName, sqlType2JavaType(type)));
        }
        rs.close();
        conn.close();

        // 生成 Entity
        String entityName = toCamelCase(tableName, true) + "Entity";
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(PACKAGE_ENTITY).append(";\n\n");
        sb.append("import com.baomidou.mybatisplus.annotation.*;\n");
        sb.append("import lombok.Data;\n");
        sb.append("import com.mybatisflex.annotation.Table;\n");
        sb.append("import com.mybatisflex.annotation.Id;\n\n");
        sb.append("@Data\n");
        sb.append("@Table(\"").append(tableName).append("\")\n");
        sb.append("public class ").append(entityName).append(" {\n\n");

        for (ColumnInfo col : columns) {
            if ("id".equalsIgnoreCase(col.name)) {
                sb.append("    @Id\n");
            }
            sb.append("    private ").append(col.type).append(" ").append(toCamelCase(col.name, false)).append(";\n");
        }
        sb.append("}\n");

        writeToFile(OUTPUT_PATH + PACKAGE_ENTITY.replace(".", "/"), entityName + ".java", sb.toString());

        // 生成 EntityDef
        String defName = toCamelCase(tableName, true) + "TableDef";
        sb = new StringBuilder();
        sb.append("package ").append(PACKAGE_ENTITY_DEF).append(";\n\n");
        sb.append("import com.mybatisflex.core.TableDef;\n");
        sb.append("import com.mybatisflex.core.query.QueryColumn;\n\n");
        sb.append("public class ").append(defName).append(" extends TableDef {\n");
        sb.append("    public static final ").append(defName).append(" ").append(tableName.toUpperCase()).append(" = new ").append(defName).append("();\n\n");

        for (ColumnInfo col : columns) {
            sb.append("    public final QueryColumn ").append(toCamelCase(col.name, true))
                    .append(" = new QueryColumn(this, \"").append(col.name).append("\");\n");
        }

        sb.append("\n    private ").append(defName).append("() {\n");
        sb.append("        super(\"").append(tableName).append(", ").append("null").append("\");\n");
        sb.append("    }\n");
        sb.append("}\n");

        writeToFile(OUTPUT_PATH + PACKAGE_ENTITY_DEF.replace(".", "/"), defName + ".java", sb.toString());

        System.out.println("生成完成: " + entityName + " & " + defName);
    }

    private static void writeToFile(String path, String fileName, String content) throws Exception {
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
        FileWriter fw = new FileWriter(new File(dir, fileName));
        fw.write(content);
        fw.close();
    }

    private static String sqlType2JavaType(String sqlType) {
        switch (sqlType.toUpperCase()) {
            case "BIGINT": return "Long";
            case "INT":
            case "INTEGER": return "Integer";
            case "VARCHAR":
            case "CHAR":
            case "TEXT": return "String";
            case "DATETIME":
            case "TIMESTAMP": return "java.time.LocalDateTime";
            case "DATE": return "java.time.LocalDate";
            case "DECIMAL": return "java.math.BigDecimal";
            case "BIT":
            case "TINYINT": return "Boolean";
            default: return "String";
        }
    }

    private static String toCamelCase(String s, boolean firstUpper) {
        StringBuilder sb = new StringBuilder();
        boolean upper = firstUpper;
        for (char c : s.toCharArray()) {
            if (c == '_' || c == '-') {
                upper = true;
            } else {
                sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                upper = false;
            }
        }
        return sb.toString();
    }

    private static class ColumnInfo {
        String name;
        String type;
        public ColumnInfo(String name, String type) { this.name = name; this.type = type; }
    }
}