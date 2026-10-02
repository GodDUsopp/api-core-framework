package com.enterprise.api.logging;

import org.slf4j.Logger;
import java.io.OutputStream;
import java.io.PrintStream;

public class Slf4jPrintStream extends PrintStream {
    private final Logger logger;

    public Slf4jPrintStream(Logger logger) {
        super(new OutputStream() {
            private StringBuilder buffer = new StringBuilder();
            @Override
            public void write(int b) {
                if (b == '\n') {
                    logger.info(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append((char) b);
                }
            }
        });
        this.logger = logger;
    }
}