package com.springbatch.simplepartitionerlocal.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
public class SimplePartitionerJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job simpleFilesPartitionerJob(@Qualifier("migrarPessoaManager") Step migrarPessoaStep,
                                         @Qualifier("migrarDadosBancariosManager") Step migrarDadosBancariosStep) {
        return jobBuilderFactory.get("simpleFilesPartitionerJob")
                .start(dividirArquivosFlow(null, null))
                .next(migrarPessoaStep)
                .next(migrarDadosBancariosStep)
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Flow dividirArquivosFlow(@Qualifier("dividirArquivoPessoaStep") Step dividirArquivoPessoaStep,
                                    @Qualifier("dividirArquivoDadosBancariosStep") Step dividirArquivoDadosBancariosStep) {

        return new FlowBuilder<Flow>("dividirArquivosFlow")
                .start(dividirArquivoPessoaStep)
                .next(dividirArquivoDadosBancariosStep)
                .build();
    }
}
