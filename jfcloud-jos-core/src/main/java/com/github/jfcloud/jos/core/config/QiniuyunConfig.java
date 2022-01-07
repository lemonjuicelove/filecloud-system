package com.github.jfcloud.jos.core.config;

import com.github.jfcloud.jos.core.domain.QiniuyunKodo;
import lombok.Data;

@Data
public class QiniuyunConfig {
    private QiniuyunKodo kodo = new QiniuyunKodo();
}
