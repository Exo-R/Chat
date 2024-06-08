package ru.focus;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MessageSocket implements Closeable{
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final ObjectMapper objectMapper;


    public MessageSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.objectMapper = new ObjectMapper();
    }

    public void sendMessage(Message message) throws IOException {
        String msg = objectMapper.writeValueAsString(message);
        synchronized (writer) {
            writer.write(msg);
            writer.newLine();
            writer.flush();
        }
    }

    public Message receiveMessage() throws IOException {
        String msg;
        synchronized (reader) {
            msg = reader.readLine();
        }
        return objectMapper.readValue(msg, Message.class);
    }

    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }

}


