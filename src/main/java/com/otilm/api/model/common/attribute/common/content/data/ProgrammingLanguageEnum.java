package com.otilm.api.model.common.attribute.common.content.data;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum ProgrammingLanguageEnum implements IPlatformEnum {

    APACHE_CONFIGURATION ("apacheconf", "ApacheConf","language-apacheconf"),
    BASH ("bash", "Bash", "language-bash"),
    BASIC ("basic", "Basic", "language-basic"),
    C ("c", "C", "language-c"),
    C_SHARP ("csharp", "C#","language-csharp"),
    CPP ("cpp", "C++","language-cpp"),
    CSS ("css", "CSS","language-css"),
    DOCKER ("docker", "Dockerfile","language-docker"),
    F_SHARP ("fsharp", "F#","language-fsharp"),
    GHERKIN ("gherkin", "Gherkin", "language-gherkin"),
    GIT ("git", "Git","language-git"),
    GO ("go", "Go","language-go"),
    GRAPHQL ("graphql", "GraphQL","language-graphql"),
    HTML ("html", "HTML","language-html"),
    HTTP ("http", "HTTP","language-http"),
    INI ("ini", "Ini","language-ini"),
    JAVA ("java", "Java","language-java"),
    JAVASCRIPT ("javascript", "JavaScript","language-javascript"),
    JSON ("json", "JSON","language-json"),
    KOTLIN ("kotlin", "Kotlin","language-kotlin"),
    LATEX ("latex", "LaTeX","language-latex"),
    LISP ("lisp", "Lisp","language-lisp"),
    MAKEFILE ("makefile", "Makefile","language-makefile"),
    MARKDOWN ("markdown", "Markdown","language-markdown"),
    MATLAB ("matlab", "Matlab","language-matlab"),
    NGINX ("nginx", "Nginx conf","language-nginx"),
    OBJECTIVE_C ("objectivec", "Objective C","language-objectivec"),
    PERL ("perl", "Perl","language-perl"),
    PHP ("php", "PHP","language-php"),
    POWERSHELL ("powershell", "PowerShell","language-powershell"),
    PROPERTIES ("properties", "Properties","language-properties"),
    PYTHON ("python", "Python","language-python"),
    RUBY ("ruby", "Ruby","language-ruby"),
    RUST ("rust", "Rust","language-rust"),
    SMALLTALK ("smalltalk", "Smalltalk","language-smalltalk"),
    SQL ("sql", "SQL","language-sql"),
    TYPESCRIPT ("typescript", "TypeScript","language-typescript"),
    VB_NET ("vbnet", "VB.NET","language-vbnet"),
    XQUERY ("xquery", "XQuery","language-xquery"),
    XML ("xml", "XML","language-xml"),
    YAML ("yaml", "YAML","language-yaml");

    private static final ProgrammingLanguageEnum[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;
    private String markupClass;

    ProgrammingLanguageEnum(final String code, final String label, final String markupClass) {
        this(code, label,null, markupClass);
    }

    ProgrammingLanguageEnum(final String code, final String label, final String description, final String markupClass) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.markupClass = markupClass;
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() { return this.description; }

    public String getMarkupClass() { return markupClass; }

    @JsonCreator
    public static ProgrammingLanguageEnum findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown programming language {}", code)));
    }
}
