package com.springbatch.simplepartitionerlocal.step;

import com.springbatch.simplepartitionerlocal.dominio.Pessoa;
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
public class MigrarPessoaStepConfig {
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
    public Step migrarPessoaManager(
            @Qualifier("pessoaPartitioner") Partitioner pessoaPartitioner,
            @Qualifier("arquivoPessoaPartitionReader") ItemStreamReader<Pessoa> arquivoPessoaReader,
            JdbcBatchItemWriter<Pessoa> bancoPessoaWriter,
            TaskExecutor taskExecutor) {

        return stepBuilderFactory
                .get("migrarPessoaStep.manager")
                .partitioner("migrarPessoaStep", pessoaPartitioner)
                .step(migrarPessoaStep(arquivoPessoaReader, bancoPessoaWriter))
                .gridSize(gridSize)
                .taskExecutor(taskExecutor)
                .build();
    }


    private Step migrarPessoaStep(ItemReader<Pessoa> arquivoPessoaReader,
                                 JdbcBatchItemWriter<Pessoa> bancoPessoaWriter) {
        return stepBuilderFactory
                .get("migrarPessoaStep")
                .<Pessoa, Pessoa>chunk(totalRegistros / gridSize) // cada partição processará 2000 registros de uma vez
                .reader(arquivoPessoaReader)
                .writer(bancoPessoaWriter)
                .transactionManager(transactionManagerApp)
                .build();
    }

}
