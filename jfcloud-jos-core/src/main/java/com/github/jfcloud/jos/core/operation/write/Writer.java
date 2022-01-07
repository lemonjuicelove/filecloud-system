package com.github.jfcloud.jos.core.operation.write;

import com.github.jfcloud.jos.core.operation.write.domain.WriteFile;

import java.io.InputStream;

public abstract class Writer {
    public abstract void write(InputStream inputStream, WriteFile writeFile);
}
