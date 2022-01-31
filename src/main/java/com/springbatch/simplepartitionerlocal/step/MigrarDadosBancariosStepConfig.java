package com.springbatch.simplepartitionerlocal.step;

import com.springbatch.simplepartitionerlocal.dominio.DadosBancarios;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MigrarDadosBancariosStepConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("transactionManagerApp")
    private PlatformTransactionManager transactionManagerApp;

    @Value("${migracaoDados.totalRegistros}")
    private Integer totalRegistros;

    @Value("${migracaoDados.gridSize}")
    private Integer gridSize;

    @Bean
    public Step migrarDadosBancariosManager(
            @Qualifier("dadosBancariosPartitioner") Partitioner dadosBancariosPartitioner,
            @Qualifier("arquivoDadosBancariosPartitionReader") ItemStreamReader<DadosBancarios> arquivoDadosBancariosReader,
            JdbcBatchItemWriter<DadosBancarios> bancoDadosBancariosWriter,
            TaskExecutor taskExecutor) {

        return stepBuilderFactory
                .get("migrarDadosBancariosStep.manager")
                .partitioner("migrarDadosBancariosStep", dadosBancariosPartitioner)
                .step(migrarDadosBancariosStep(arquivoDadosBancariosReader, bancoDadosBancariosWriter))
                .gridSize(gridSize)
                .taskExecutor(taskExecutor)
                .build();
    }


    private Step migrarDadosBancariosStep(ItemReader<DadosBancarios> arquivoDadosBancariosReader,
                                         JdbcBatchItemWriter<DadosBancarios> bancoDadosBancariosWriter) {
        return stepBuilderFactory
                .get("migrarDadosBancariosStep")
                .<DadosBancarios, DadosBancarios>chunk(totalRegistros / gridSize) // cada partição processará 2000 registros de uma vez
                .reader(arquivoDadosBancariosReader)
                .writer(bancoDadosBancariosWriter)
                .transactionManager(transactionManagerApp)
                .build();
    }
}
