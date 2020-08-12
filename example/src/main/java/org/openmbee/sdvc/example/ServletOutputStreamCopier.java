package org.openmbee.sdvc.example;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServletOutputStreamCopier extends ServletOutputStream {

    private ByteArrayOutputStream copy;
    private final ServletOutputStream os;

    public ServletOutputStreamCopier(ServletOutputStream os) {
        this.copy = new ByteArrayOutputStream(1024);
        this.os = os;
    }

    @Override
    public void write(int b) throws IOException {
        os.write(b);
        copy.write(b);
    }

    public byte[] getCopy() {
        return copy.toByteArray();
    }

    @Override
    public boolean isReady() {
        return this.os.isReady();
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        this.os.setWriteListener(listener);
    }
}
