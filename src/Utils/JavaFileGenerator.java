package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JavaFileGenerator {
    public static void createClass(String tableName) {

        String folderPath = "src/Models";
        Connection connection = DDBBConnection.Conectar();

        try {
            // Obtener metadatos de la tabla
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

            // Generar código Java
            StringBuilder codeBuilder = new StringBuilder();
            codeBuilder.append("package Models;").append("\n");
            codeBuilder.append("import com.fasterxml.jackson.annotation.JsonProperty;").append("\n");
            codeBuilder.append("import java.sql.Timestamp;").append("\n");
            codeBuilder.append("import java.sql.Date;").append("\n");
            codeBuilder.append("public class ").append(toCamelCase(tableName)).append(" {\n").append("\t\n");
            codeBuilder.append("\tprivate final static String TABLENAME = ").append('"' + tableName + '"').append(";\n").append("\t\n");
            codeBuilder.append("\tpublic static String getTABLENAME() {return TABLENAME;}\n").append("\t\n");

            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String columnType = atributeType.getType(resultSet.getString("TYPE_NAME"));
                codeBuilder.append("\t@JsonProperty(").append('"' + columnName + '"').append(")\n");
                codeBuilder.append("\tprivate ").append(columnType).append(" ").append(columnName).append(";\n");
                // Generar el getter
                codeBuilder.append("\tpublic ").append(columnType).append(" get").append(toCamelCase(columnName))
                        .append("() {\n")
                        .append("\t\treturn ").append(columnName).append(";\n")
                        .append("\t}\n").append("\t\n");
                // Generar el setter
                codeBuilder.append("\tpublic void set").append(toCamelCase(columnName)).append("(").append(columnType)
                        .append(" ").append(columnName).append(") {\n")
                        .append("\t\tthis.").append(columnName).append(" = ").append(columnName).append(";\n")
                        .append("\t}\n").append("\t\n");
            }

            codeBuilder.append("}");

            // Crear el archivo en la carpeta especificada
            String filename = toCamelCase(tableName) + ".java";
            File file = new File(folderPath, filename);

            // Verificar si la carpeta existe
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Escribir el código en el archivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(codeBuilder.toString());
            }

            System.out.println("Archivo generado exitosamente.");

            // Cerrar la conexión a la base de datos
            DDBBConnection.closeResources(connection, null, resultSet);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String toCamelCase(String text) {
        String[] parts = text.split("_");
        StringBuilder camelCase = new StringBuilder();
        for (String part : parts) {
            camelCase.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return camelCase.toString();
    }
}

interface atributeType {
    static String getType(String a) {
        String retType = "";
        switch (a) {
            case "INT":
                retType = "int";
                break;
            case "BIGINT":
                retType = "long";
                break;
            case "FLOAT":
                retType = "float";
                break;
            case "DOUBLE":
                retType = "double";
                break;
            case "DECIMAL":
                retType = "BigDecimal";
                break;
            case "CHAR":
                retType = "char";
                break;
            case "VARCHAR":
                retType = "String";
                break;
            case "TEXT":
                retType = "String";
                break;
            case "DATE":
                retType = "String";
                break;
            case "TIME":
                retType = "String";
                break;
            case "DATETIME":
                retType = "String";
                break;
            case "BOOLEAN":
                retType = "boolean";
                break;
            case "BLOB":
                retType = "Blob";
                break;
            case "SMALLINT":
                retType = "short";
                break;
            default:
                retType = a;
                break;
        }
        return retType;
    }
}

