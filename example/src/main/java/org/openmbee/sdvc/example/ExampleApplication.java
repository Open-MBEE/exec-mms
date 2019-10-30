package org.openmbee.sdvc.example;

import java.util.Arrays;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.ParsedUpdate;
import org.eclipse.rdf4j.query.parser.QueryParser;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParserFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "org.openmbee")
@EnableSwagger2
public class ExampleApplication {

    public static void main(String[] args) {
        //SpringApplication.run(ExampleApplication.class, args);
        String query = "PREFIX  data:  <http://example.org/foaf/>\n" //null dataset
            + "PREFIX  foaf:  <http://xmlns.com/foaf/0.1/>\n"
            + "PREFIX  rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "\n"
            + "SELECT ?mbox ?nick ?ppd\n"
            + "WHERE\n"
            + "{\n"
            + "  GRAPH data:aliceFoaf\n"
            + "  {\n"
            + "    ?alice foaf:mbox <mailto:alice@work.example> ;\n"
            + "           foaf:knows ?whom .\n"
            + "    ?whom  foaf:mbox ?mbox ;\n"
            + "           rdfs:seeAlso ?ppd .\n"
            + "    ?ppd  a foaf:PersonalProfileDocument .\n"
            + "  } .\n"
            + "  GRAPH ?ppd\n"
            + "  {\n"
            + "      ?w foaf:mbox ?mbox ;\n"
            + "         foaf:nick ?nick\n"
            + "  }\n"
            + "}";
        String query2 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" //default and named dataset iri populated
            + "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
            + "\n"
            + "SELECT ?who ?g ?mbox\n"
            + "FROM dc:help\n"
            + "FROM NAMED <http://example.org/alice>\n"
            + "FROM NAMED <http://example.org/bob>\n"
            + "WHERE\n"
            + "{\n"
            + "   ?g dc:publisher ?who .\n"
            + "   GRAPH ?g { ?x foaf:mbox ?mbox }\n"
            + "}";
        String updateNoGraph = "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
            + "INSERT DATA\n"
            + "{ \n"
            + "  <http://example/book1> dc:title \"A new book\" ;\n"
            + "                         dc:creator \"A.N.Other\" .\n"
            + "}";
        String updateInsert = "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" //doesn't have dataset
            + "PREFIX ns: <http://example.org/ns#>\n"
            + "INSERT DATA\n"
            + "{ GRAPH <http://example/bookStore> { <http://example/book1>  ns:price  42 } }";
        String updateDelete = "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"//nope
            + "DELETE DATA\n"
            + "{ GRAPH <http://example/bookStore> { <http://example/book1>  dc:title  \"Fundamentals of Compiler Desing\" } } ;\n"
            + "\n"
            + "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
            + "INSERT DATA\n"
            + "{ GRAPH <http://example/bookStore> { <http://example/book1>  dc:title  \"Fundamentals of Compiler Design\" } }";
        String updateWith = "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n" //MODIFY EXPR, has DATASET
            + "\n"
            + "WITH <http://example/addresses>\n"
            + "DELETE { ?person foaf:givenName 'Bill' }\n"
            + "INSERT { ?person foaf:givenName 'William' }\n"
            + "WHERE\n"
            + "  { ?person foaf:givenName 'Bill'\n"
            + "  } ";
        String updateWithData = "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n" //MODIFY EXPR, works, has dataset default, insert, remove iri
            + "\n"
            + "WITH <http://example/addresses>\n"
            + "DELETE  { foaf:blah2 foaf:givenName 'Bill' }\n"
            + "INSERT  { foaf:blah foaf:givenName 'William' } WHERE {}\n";
        String updateWithUsing = "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n" //MODIFY EXPR, has DATASET defaultRemove and Insert are with, defaultGraph is using, namedGraphs is using named
            + "\n"
            + "WITH <http://example/addresses>\n"
            + "DELETE { ?person foaf:givenName 'Bill' }\n"
            + "INSERT { ?person foaf:givenName 'William' }\n"
            + "USING <http://asdf>\n"
            + "USING NAMED <http://sdfsd>\n"
            + "WHERE\n"
            + "  { ?person foaf:givenName 'Bill'\n"
            + "  } ";
        String updateWhere = "PREFIX dc:  <http://purl.org/dc/elements/1.1/>\n" //MODIFY EXPR no dataset
            + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
            + "\n"
            + "INSERT \n"
            + "  { GRAPH <http://example/bookStore2> { ?book ?p ?v } }\n"
            + "WHERE\n"
            + "  { GRAPH  <http://example/bookStore>\n"
            + "       { ?book dc:date ?date .\n"
            + "         FILTER ( ?date > \"1970-01-01T00:00:00-02:00\"^^xsd:dateTime )\n"
            + "         ?book ?p ?v\n"
            + "  } }\t";
        String updateClearAll = "CLEAR GRAPH <http://example.org/named>"; //failed to parse for ALL or DEFAULT or NAMED, CLEAR Expr has graph iri
        String updateCopy = "COPY DEFAULT TO <http://example.org/named>"; //copy expr, has destination iri, null source graph
        String updateAdd = "ADD <http://blah> TO <http://example.org/named>"; //add expr, has destination iri, source iri
        QueryParser parser = (new SPARQLParserFactory()).getParser();
        ParsedQuery parsed = parser.parseQuery(query, "http://mms.openmbee.org");
        ParsedUpdate parsedU = parser.parseUpdate(updateWithUsing, "http://mms.openmbee.org");

        String a = "a";
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .ignoredParameterTypes(Authentication.class)
            .apiInfo(apiInfo()).select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any()).build()
            .securitySchemes(Arrays.asList(
                new BasicAuth("basicAuth"),
                new ApiKey("bearerToken", "Authorization", "header")))
            .securityContexts(Arrays.asList(
                SecurityContext.builder().securityReferences(Arrays.asList(
                    new SecurityReference("basicAuth", new AuthorizationScope[0]),
                    new SecurityReference("bearerToken", new AuthorizationScope[0])
                ))
                .forPaths(PathSelectors.any()).build()
            ));
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("MMS Example API")
            .description("Documentation for MMS Example API").termsOfServiceUrl("")
            .contact(new Contact("OpenMBEE", "http://www.openmbee.org",
                ""))
            .license("Apache License Version 2.0").licenseUrl("").version("2.0").build();
    }
}
