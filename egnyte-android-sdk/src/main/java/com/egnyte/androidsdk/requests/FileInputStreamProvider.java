package com.egnyte.androidsdk.requests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link InputStreamProvider} that provides {@link InputStream} from a {@link File}
 */
public class FileInputStreamProvider implements InputStreamProvider {

    private final File sourceFile;

    public FileInputStreamProvider(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    public InputStream provideInputStream() throws IOException {
        return new FileInputStream(sourceFile);
    }
}
