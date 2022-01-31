package com.springbatch.simplepartitionerlocal.reader;

import com.springbatch.simplepartitionerlocal.dominio.DadosBancarios;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class ArquivoDadosBancariosPartitionReaderConfig {

    @StepScope
    @Bean
    public FlatFileItemReader<DadosBancarios> arquivoDadosBancariosPartitionReader(
            @Value("#{stepExecutionContext['file']}") Resource resource) {

        return new FlatFileItemReaderBuilder<DadosBancarios>()
                .name("arquivoDadosBancariosPartitionReader")
                .resource(resource)
                .delimited()
                .names("pessoaId", "agencia", "conta", "banco", "id")
                .addComment("--")
                .targetType(DadosBancarios.class)
                .build();
    }
}
