package hu.blackbelt.core.persistence.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLTemplates;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QuerydslDatabaseTemplate {

    public static final ImmutableMap<String, SQLTemplates> TEMPLATES =
            new ImmutableMap.Builder<String, SQLTemplates>()
                    .put("oracle", new OracleTemplates().builder()
                            .printSchema()
                            .quote()
                            .newLineToSingleSpace()
                            .build())
                    .put("hsqldb", new HSQLDBTemplates().builder()
                            .printSchema()
                            .quote()
                            .newLineToSingleSpace()
                            .build())
                    .build();
}
