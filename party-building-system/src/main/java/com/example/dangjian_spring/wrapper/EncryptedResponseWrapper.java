package com.example.dangjian_spring.wrapper;

import  jakarta.servlet.ServletOutputStream;
import  jakarta.servlet.WriteListener;
import  jakarta.servlet.http.HttpServletResponse;
import  jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptedResponseWrapper extends HttpServletResponseWrapper {

    private static final Logger log = LoggerFactory.getLogger(EncryptedResponseWrapper.class);

    private final ByteArrayOutputStream outputStream;
    private ServletOutputStream servletOutputStream;
    private PrintWriter writer;

    public EncryptedResponseWrapper(HttpServletResponse response) {
        super(response);
        outputStream = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (servletOutputStream == null) {
            servletOutputStream = new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                    // 实现 WriteListener 逻辑
                }

                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                }
            };
        }

        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (servletOutputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(outputStream, getCharacterEncoding()));
        }

        return writer;
    }

    public byte[] getContentAsByteArray() {
        if (writer != null) {
            writer.flush();
        } else if (servletOutputStream != null) {
            try {
                servletOutputStream.flush();
            } catch (IOException e) {
                log.debug("flush output stream", e);
            }
        }
        return outputStream.toByteArray();
    }
}