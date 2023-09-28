package de.hsw;

import java.io.*;
import java.util.Base64;

public class RpcWriter extends BufferedWriter {

    public RpcWriter(Writer out) {
        super(out);
    }

    public void writeString(String value) throws IOException {
        super.write(value);
        super.newLine();
        flushStream(value);
    }

    public void writeChar(char value) throws IOException {
        super.write(value + "\n");
        flushStream(value);
    }

    public void writeInt(int value) throws IOException {
        super.write(Integer.toString(value));
        super.newLine();
        flushStream(value);
    }

    public void writeBoolean(boolean value) throws IOException {
        super.write(Boolean.toString(value));
        super.newLine();
        flushStream(value);
    }

    public void writeStringArray(String[] stringArray) throws IOException {
        int numRows = stringArray.length;
        writeInt(numRows);
        for (String string : stringArray) {
            super.write(string);
            super.newLine();
        }
        flushStream(stringArray);
    }

    public void writeCharArray(char[][] charArray) throws IOException {
        int numRows = charArray.length;
        writeInt(numRows);
        for (char[] row : charArray) {
            super.write(row);
            super.newLine();
        }
        flushStream(charArray);
    }

    public void writeObject(Object obj) throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(byteStream)) {
            oos.writeObject(obj);
            oos.flush();
            byte[] objectData = byteStream.toByteArray();
            String encodedObject = Base64.getEncoder().encodeToString(objectData);
            super.write(encodedObject);
            super.newLine();
            flushStream(obj);
        }
    }

    private <T> void flushStream(T value) throws IOException {
        System.out.println("[CLIENT - WRITING]: " + value);

        if (value instanceof char[][] charArray) {
            StringBuilder boardString = new StringBuilder();

            boardString.append("\n");
            for (int i = 1; i <= charArray[0].length; i++) {
                boardString.append("   ").append(i);
            }
            boardString.append("\n");

            for (char[] chars : charArray) {
                for (char aChar : chars) {
                    boardString.append(" | ").append(aChar);
                }
                boardString.append(" |\n");
            }

            System.err.println(boardString);
        }

        super.flush();
    }
}
