package de.hsw;

import java.io.*;
import java.util.Base64;

public class RpcReader extends BufferedReader {

    public RpcReader(Reader in) {
        super(in);
    }

    public String readString() throws IOException {
        return returnValue(super.readLine());
    }

    public char readChar() throws IOException {
        String line = super.readLine();
        if (line == null || line.length() != 1) {
            throw new IOException("[RPC - READER]: Failed to read a character.");
        }
        return returnValue(line.charAt(0));
    }

    public int readInt() throws IOException {
        String line = super.readLine();
        if (line == null) {
            throw new EOFException("[RPC - READER]: End of stream reached.");
        }
        try {
            return returnValue(Integer.parseInt(line));
        } catch (NumberFormatException e) {
            throw new IOException("[RPC - READER]: Failed to read an integer: " + e.getMessage());
        }
    }

    public boolean readBoolean() throws IOException {
        String line = super.readLine();
        if (line == null) {
            throw new EOFException("[RPC - READER]: End of stream reached.");
        }
        return returnValue(Boolean.parseBoolean(line));
    }

    public String[] readStringArray() throws IOException {
        int numRows = readInt();
        String[] stringArray = new String[numRows];
        for (int i = 0; i < numRows; i++) {
            String line = super.readLine();
            if (line == null) {
                throw new EOFException("[RPC - READER]: End of stream reached.");
            }
            stringArray[i] = line;
        }
        return returnValue(stringArray);
    }

    public char[][] readCharArray() throws IOException {
        int numRows = readInt();
        char[][] charArray = new char[numRows][];
        for (int i = 0; i < numRows; i++) {
            String line = super.readLine();
            if (line == null) {
                throw new EOFException("[RPC - READER]: End of stream reached.");
            }
            charArray[i] = line.toCharArray();
        }
        return returnValue(charArray);
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        String line = super.readLine();
        if (line == null) {
            throw new EOFException("[RPC - READER]: End of stream reached.");
        }
        byte[] objectData = Base64.getDecoder().decode(line);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(objectData))) {
            return returnValue(ois.readObject());
        }
    }

    public <T> T returnValue(T value) {
        System.out.printf("[%d][SERVER - READING]: %s\n", Thread.currentThread().getId(), value);

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

        return value;
    }
}
