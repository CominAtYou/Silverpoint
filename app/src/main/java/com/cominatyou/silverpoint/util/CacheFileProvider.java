package com.cominatyou.silverpoint.util;

import com.cominatyou.silverpoint.R;

import androidx.core.content.FileProvider;

public class CacheFileProvider extends FileProvider {
    public CacheFileProvider() {
        super(R.xml.file_paths);
    }
}
